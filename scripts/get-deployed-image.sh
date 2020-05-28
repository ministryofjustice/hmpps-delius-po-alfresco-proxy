#!/usr/bin/env bash

set -e

dtag="latest"

describe_service_results=`aws --output json ecs describe-services --services ${service_name} --cluster ${cluster_arn}`
task_def_arn=`echo ${describe_service_results} | jq --arg SERVICE_NAME "${service_name}" -r '.services[] | select(.serviceName==$SERVICE_NAME) | .deployments[] | select(.status=="PRIMARY") | .taskDefinition'`

echo "=================================================="
if [ ! -z "${task_def_arn}" ]; then
    task_def_result=`aws ecs describe-task-definition --task-definition "${task_def_arn}"`

    docker_image=`echo ${task_def_result} | jq --arg PROJECT_NAME "${project_name}" -r '.taskDefinition | .containerDefinitions[] | select(.name==$PROJECT_NAME) | .image'`
    if [ ! -z "${docker_image}" ] && [ ${docker_image} != null ]; then
        dtag=`echo "${docker_image}" | awk -F':' '{print $2}'`
    fi
fi

echo ${dtag} > image.tag