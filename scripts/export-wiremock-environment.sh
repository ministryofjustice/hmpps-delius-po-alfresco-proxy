#!/usr/bin/env bash

image_tag="2.26.3-alpine"
echo "image_tag = ${image_tag}"
export TF_VAR_image_version=${image_tag}