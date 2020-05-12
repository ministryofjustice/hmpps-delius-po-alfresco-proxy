#!/usr/bin/env bash

dtag="latest"

temp_role=$(aws sts assume-role --role-arn ${terraform_role_arn} --role-session-name alfresco-proxy-temp-ci-session --duration-seconds 900)

export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity

set -e

describe_service_results=`aws --output json ecs describe-services --services ${service_name} --cluster ${cluster_arn}`
task_def_arn=`echo ${describe_service_results} | jq --arg SERVICE_NAME "${service_name}" -r '.services[] | select(.serviceName==$SERVICE_NAME) | .deployments[] | select(.status=="PRIMARY") | .taskDefinition'`

taskArns=`aws ecs list-tasks --cluster ${cluster_arn} | jq '.taskArns'`
if [ ! -z "${taskArns}" ] && [ "${taskArns}" != "[]" ]; then
    describe_tasks_result=`aws ecs describe-tasks --cluster ${cluster_arn} --tasks "${taskArns}"`
    echo "--------------------------------------------------"
    echo ${describe_tasks_result} | jq '.'
    echo "--------------------------------------------------"
    docker_image=`echo ${describe_tasks_result} | jq --arg TASK_DEF_ARN "${task_def_arn}" -r '.tasks[] | select(.taskDefinitionArn==$TASK_DEF_ARN) | .containers[] | select(.name=="alfresco-proxy") | .image'`
    echo "=================================================="
    if [ ! -z "${docker_image}" ] && [ ${docker_image} != null ]; then
        dtag=`echo "${docker_image}" | awk -F':' '{print $2}'`
    fi
fi

echo ${dtag}