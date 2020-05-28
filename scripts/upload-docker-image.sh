#!/usr/bin/env bash

set -e

echo Pushing the Docker image...
docker push ${REPOSITORY_URI}:$TAG