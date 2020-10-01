#!/usr/bin/env bash

set -e

echo Cleaning up untagged images...
UNTAGGED_IMAGES=$(aws ecr list-images --region ${AWS_REGION} --repository-name ${IMAGE_NAME} --filter tagStatus=UNTAGGED --query 'imageIds[?type(imageTag)!=`string`].[imageDigest]' --output text)

for DIGEST in ${UNTAGGED_IMAGES[*]}; do
    aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${DIGEST}
done

echo "----------------------------------------------------------"
echo Cleaning up undeployed images...


if ["${my_aws_env}" == "sandpit"]; then
    aws ecr list-images --repository-name ${IMAGE_NAME} | \
    jq --argjson MY_IMAGE_TAG ${image_tag} -r '.imageIds[] | select(.imageTag | startswith("sandpit")) | select(.imageTag != "sandpit-2-") | select(.imageTag != $MY_IMAGE_TAG) | [.imageDigest] | @tsv' | \
    while IFS=$'\t' read -r digest; do
        aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${digest}
    done
elif ["${my_aws_env}" == "sandpit-2"]; then
    aws ecr list-images --repository-name ${IMAGE_NAME} | \
    jq --argjson MY_IMAGE_TAG ${image_tag} -r '.imageIds[] | select(.imageTag | startswith("sandpit-2-")) | select(.imageTag!=$MY_IMAGE_TAG) | [.imageDigest] | @tsv' | \
    while IFS=$'\t' read -r digest; do
        aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${digest}
    done
else
    aws ecr list-images --repository-name ${IMAGE_NAME} | \
    jq --arg MY_AWS_ENV ${my_aws_env} --argjson MY_IMAGE_TAG ${image_tag} -r '.imageIds[] | select(.imageTag | startswith($MY_AWS_ENV)) | select(.imageTag!=$MY_IMAGE_TAG) | [.imageDigest] | @tsv' | \
    while IFS=$'\t' read -r digest; do
        aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${digest}
    done
fi

echo "--------------------------- The following images were left intact ---------------------------"

aws ecr list-images --repository-name ${IMAGE_NAME}