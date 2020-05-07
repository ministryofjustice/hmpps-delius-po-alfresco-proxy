#!/usr/bin/env bash

set -ex

echo Building the Docker image...
docker build -t $REPOSITORY_URI:$TAG --rm=true .