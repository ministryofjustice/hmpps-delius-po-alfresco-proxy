#!/usr/bin/env bash

echo Cleaning up untagged images...
UNTAGGED_IMAGES=$(aws ecr list-images --region ${AWS_REGION} --repository-name ${IMAGE_NAME} --filter tagStatus=UNTAGGED --query 'imageIds[?type(imageTag)!=`string`].[imageDigest]' --output text)

for DIGEST in ${UNTAGGED_IMAGES[*]}; do
    aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${DIGEST}
done

echo "----------------------------------------------------------"
echo Cleaning up undeployed images...

aws ecr list-images --repository-name ${IMAGE_NAME} | \
jq --arg IMAGE_TAG ${image_tag}  --arg MY_AWS_ENV ${my_aws_env} -r '.imageIds[] | select(.imageTag | startswith($MY_AWS_ENV)) | select(.imageTag!=$IMAGE_TAG) | [.imageDigest] | @tsv' | \
while IFS=$'\t' read -r digest; do
    aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${digest}
done

echo "--------------------------- The following images were left intact ---------------------------"

aws ecr list-images --repository-name ${IMAGE_NAME}