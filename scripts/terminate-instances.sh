#!/usr/bin/env bash

set -e

source $(pwd)/scripts/assume-role.sh ${ENVIRONMENT_TERRAFORM_IAM_ROLE_ARN}
describe_service_results=`aws --output json ecs describe-services --services ${service_name} --cluster ${cluster_arn}`
task_def_arn=`echo ${describe_service_results} | jq --arg SERVICE_NAME "${service_name}" -r '.services[] | select(.serviceName==$SERVICE_NAME) | .deployments[] | select(.status=="PRIMARY") | .taskDefinition'`
echo "=================================================="
if [ -z "${task_def_arn}" ]; then
    # No alfresco-proxy task definition.
    exit 0
fi

tasks_results=`aws ecs list-tasks --cluster ${cluster_arn} | jq -r '.taskArns[]'`
if [ -z "${tasks_results}" ]; then
    # No tasks in this cluster
    exit 0
fi

# TODO: what happens when there are multiple instances?
container_instance_arn=`aws ecs describe-tasks \
    --cluster ${cluster_arn} \
    --tasks ${tasks_results} | jq --arg TASK_DEF_ARN "${task_def_arn}" -r '.tasks[] | select(.taskDefinitionArn==$TASK_DEF_ARN) | .containerInstanceArn'`
if [ -z "${container_instance_arn}" ]; then
    # No instances found
    exit 0
fi

ec2_instance_id=`aws ecs describe-container-instances \
    --cluster ${cluster_arn} \
    --container-instances ${container_instance_arn} | jq -r '.containerInstances[].ec2InstanceId'`

aws ec2 terminate-instances --instance-ids ${ec2_instance_id}
sleep 30