#!/usr/bin/env bash

set -e

timestamp() {
  date +"%Y%m%d%H%M%S"
}

if [ -z "${CODEBUILD_RESOLVED_SOURCE_VERSION}" ]; then
    postfix=`timestamp`
else
    postfix=${CODEBUILD_RESOLVED_SOURCE_VERSION:0:7}
fi

echo "{}" |  jq --arg POSTFIX ${postfix} '. | {tagPostfix: $POSTFIX}' > ${CODEBUILD_SRC_DIR}/image-tag-postfix.json