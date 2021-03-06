#!/usr/bin/env bash

set -e

function getLatestImageTag() {
    source $(pwd)/scripts/unassume-role.sh

    if [ -n "${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}" ]; then
        source $(pwd)/scripts/assume-role.sh ${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}
    fi

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
    elif [ ${my_aws_env} == "sandpit-2" ]; then
        docker_image=$(aws ecr describe-images \
            --region ${AWS_REGION} \
            --repository-name ${IMAGE_NAME} \
            --query 'sort_by(imageDetails[?starts_with(imageTags[0], `sandpit-2`) == `true`],& imagePushedAt)[-1].imageTags[0]')
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
        current_tag=`echo ${docker_image}`
    fi
}

alfresco_proxy_image="895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/spgw-alfresco-proxy"
current_tag="latest"

source $(pwd)/scripts/assume-role.sh ${ENVIRONMENT_TERRAFORM_IAM_ROLE_ARN}

# Check if the cluster exists at all
cluster_results=`aws ecs describe-clusters --cluster "${cluster_arn}" | jq -r '.failures'`
cluster_results_errors=`echo "${cluster_results}" | jq '. | length'`
if [[ ${cluster_results_errors} > 0 ]]; then
    printf "============> ecs describe-clusters has errors:\n%s\n---------------------------\n" "${cluster_results}"
    echo ${current_tag} > image.tag
    exit 0
fi

# Cluster exists. Get the latest image and tag
describe_service_results=`aws --output json ecs describe-services --services ${service_name} --cluster ${cluster_arn}`
task_def_arn=`echo ${describe_service_results} | jq --arg SERVICE_NAME "${service_name}" -r '.services[] | select(.serviceName==$SERVICE_NAME) | .deployments[] | select(.status=="PRIMARY") | .taskDefinition'`

echo "=================================================="
if [ -n "${task_def_arn}" ]; then
    task_def_result=`aws ecs describe-task-definition --task-definition "${task_def_arn}"`

    docker_image=`echo ${task_def_result} | jq --arg CONTAINER_NAME "${container_name}" -r '.taskDefinition | .containerDefinitions[] | select(.name==$CONTAINER_NAME) | .image'`
    if [ -n "${docker_image}" ] && [ ${docker_image} != null ]; then
        current_image=`echo "${docker_image}" | awk -F':' '{print $1}'`
        echo "current_image =======> ${current_image}"
        if [ ${current_image} == ${alfresco_proxy_image} ]; then
            current_tag=`echo "${docker_image}" | awk -F':' '{print $2}'`
        else
            getLatestImageTag
        fi
    else
        getLatestImageTag
    fi
else
    getLatestImageTag
fi

echo ${current_tag} > image.tag