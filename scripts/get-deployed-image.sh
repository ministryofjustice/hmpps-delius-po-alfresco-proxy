#!/usr/bin/env bash

set -e

dtag="latest"

source $(pwd)/scripts/assume-role.sh ${ENVIRONMENT_TERRAFORM_IAM_ROLE_ARN}
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

    echo Cleaning up untagged images...
    eval $(aws --region ${AWS_REGION} ecr get-login --no-include-email)
    aws sts get-caller-identity
    UNTAGGED_IMAGES=$(aws ecr list-images --region ${AWS_REGION} --repository-name ${IMAGE_NAME} --filter tagStatus=UNTAGGED --query 'imageIds[?type(imageTag)!=`string`].[imageDigest]' --output text)
    for DIGEST in ${UNTAGGED_IMAGES[*]}; do
        aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${DIGEST}
    done

    if [ ${my_aws_env} == "sandpit" ]; then
        docker_image=$(aws ecr describe-images \
            --region ${AWS_REGION} \
            --repository-name ${IMAGE_NAME} \
            --query 'sort_by(imageDetails[?starts_with(imageTags[0], `sandpit`) == `true`],& imagePushedAt)[-1].imageTags[0]')
    elif [ ${my_aws_env} == "dev" ]; then
        docker_image=$(aws ecr describe-images \
            --region ${AWS_REGION} \
            --repository-name ${IMAGE_NAME} \
            --query 'sort_by(imageDetails[?starts_with(imageTags[0], `dev`) == `true`],& imagePushedAt)[-1].imageTags[0]')
    else
        echo "Unknown environment... exiting"
        exit 1
    fi

    if [ ! -z "${docker_image}" ] && [ ${docker_image} != null ]; then
        dtag=`echo ${docker_image}`
    fi
fi

echo ${dtag} > image.tag