#!/usr/bin/env bash

set -ex

echo "{}" |  jq --arg REPOSITORY_URI ${REPOSITORY_URI} --arg TAG ${TAG} '. | {name: $REPOSITORY_URI, tag: $TAG}' > ${CODEBUILD_SRC_DIR}/image.json