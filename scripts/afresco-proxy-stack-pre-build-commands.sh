#!/usr/bin/env bash

${CODEBUILD_SRC_DIR}/scripts/get-deployed-image.sh

source $(pwd)/scripts/export-alfresco-proxy-version.sh