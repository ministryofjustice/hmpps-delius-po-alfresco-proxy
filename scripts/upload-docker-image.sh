#!/usr/bin/env bash

echo Pushing the Docker image...
docker push ${REPOSITORY_URI}:$TAG

echo Cleaning up untagged images...
UNTAGGED_IMAGES=$(aws ecr list-images --region ${AWS_REGION} --repository-name ${IMAGE_NAME} --filter tagStatus=UNTAGGED --query 'imageIds[?type(imageTag)!=`string`].[imageDigest]' --output text)

for DIGEST in ${UNTAGGED_IMAGES[*]}; do
    aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${DIGEST}
done