#!/usr/bin/env bash

echo Building the Docker image...
docker build -t $REPOSITORY_URI:$TAG --rm=true .
