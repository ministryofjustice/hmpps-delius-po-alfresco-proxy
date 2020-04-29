#!/usr/bin/env bash

export IMAGE_REGISTRY="895523100917.dkr.ecr.eu-west-2.amazonaws.com"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"
export TAG="sandpit"
export REPOSITORY_URI=${IMAGE_REGISTRY}/${IMAGE_NAME}

$(pwd)/scripts/build-docker-image.sh