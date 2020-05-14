#!/usr/bin/env bash

set -ex

echo Pushing the Docker image...
docker push ${REPOSITORY_URI}:$TAG