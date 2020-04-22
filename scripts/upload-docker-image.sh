#!/usr/bin/env bash

IMAGE_TO_DELETE=$(aws --region ${AWS_REGION} ecr describe-images --repository-name ${IMAGE_NAME} --image-ids imageTag=latest | jq -r .imageDetails[0].imageDigest)

echo Pushing the Docker image...
docker push ${REPOSITORY_URI}:latest

if [[ -n "${IMAGE_TO_DELETE}" ]]; then
    aws --region ${AWS_REGION} ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${IMAGE_TO_DELETE}
fi
