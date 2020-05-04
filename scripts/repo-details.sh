#!/usr/bin/env bash

shortened_sha=${CODEBUILD_RESOLVED_SOURCE_VERSION:0:7}
echo "{}" |  jq --arg SHORTENED_SHA ${shortened_sha} '. | {shortenedHash: $SHORTENED_SHA}' > ${CODEBUILD_SRC_DIR}/repo-details.json