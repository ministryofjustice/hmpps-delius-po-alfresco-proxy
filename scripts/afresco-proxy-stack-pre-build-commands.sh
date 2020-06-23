#!/usr/bin/env bash

${CODEBUILD_SRC_DIR}/scripts/get-deployed-image.sh
#image_tag=`cat image.tag | head -n 1 | tr -d '\n'`
#echo "image_tag = ${image_tag}"
#export TF_VAR_image_version=${image_tag}