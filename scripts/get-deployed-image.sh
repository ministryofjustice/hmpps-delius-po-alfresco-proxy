#!/usr/bin/env bash

set -e

dtag="latest"

source $(pwd)/scripts/assume-role.sh ${ENV_TERRAFORM_IAM_ROLE_ARN}
describe_service_results=`aws --output json ecs describe-services --services ${service_name} --cluster ${cluster_arn}`
task_def_arn=`echo ${describe_service_results} | jq --arg SERVICE_NAME "${service_name}" -r '.services[] | select(.serviceName==$SERVICE_NAME) | .deployments[] | select(.status=="PRIMARY") | .taskDefinition'`

echo "=================================================="
if [ ! -z "${task_def_arn}" ]; then
    task_def_result=`aws ecs describe-task-definition --task-definition "${task_def_arn}"`

    docker_image=`echo ${task_def_result} | jq --arg CONTAINER_NAME "${container_name}" -r '.taskDefinition | .containerDefinitions[] | select(.name==$CONTAINER_NAME) | .image'`
    if [ ! -z "${docker_image}" ] && [ ${docker_image} != null ]; then
        dtag=`echo "${docker_image}" | awk -F':' '{print $2}'`
    fi
else
    source $(pwd)/scripts/assume-role.sh ${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}
    docker_image=`aws ecr describe-images \
        --region ${AWS_REGION} \
        --repository-name ${IMAGE_NAME} \
        --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]'`
    if [ ! -z "${docker_image}" ] && [ ${docker_image} != null ]; then
        dtag=`echo ${docker_image}`
    fi
fi

echo ${dtag} > image.tag