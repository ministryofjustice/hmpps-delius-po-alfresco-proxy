#!/usr/bin/env bash

aws ecr list-images --repository-name ${IMAGE_NAME} | \
jq --arg IMAGE_TAG ${image_tag} -r '.imageIds[] | select(.imageTag | startswith("dev")) | select(.imageTag!=$IMAGE_TAG) | [.imageDigest] | @tsv' | \
while IFS=$'\t' read -r digest; do
    aws ecr batch-delete-image --repository-name ${IMAGE_NAME} --image-ids imageDigest=${digest}
done

aws ecr list-images --repository-name ${IMAGE_NAME}