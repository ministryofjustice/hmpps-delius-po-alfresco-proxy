#!/usr/bin/env bash

# ${1} - the aws environment
# ${2} - the script to run: plan or apply
# See Makefile for usage

docker run -it --rm \
    -v $(pwd):/home/tools/data \
    -v ~/.aws:/home/tools/.aws \
    -e AWS_PROFILE=hmpps-token \
    -e TF_LOG=INFO \
    -e HMPPS_BUILD_WORK_DIR=/home/tools/data \
    -e HMPPS_BASE_DIR=/home/tools/data/terraform \
    -e environment_name="${1}" \
    -e CUSTOM_COMMON_PROPERTIES_DIR=/home/tools/data/terraform/env_configs/common \
    -e "TERM=xterm-256color" \
    --entrypoint "scripts/${2}" \
    mojdigitalstudio/hmpps-terraform-builder-lite:latest