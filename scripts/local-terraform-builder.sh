#!/usr/bin/env bash

docker run -it --rm \
    -v $(pwd):/home/tools/data \
    -v ~/.aws:/home/tools/.aws \
    -e AWS_PROFILE=hmpps-token \
    -e TF_LOG=INFO \
    -e HMPPS_BUILD_WORK_DIR=/home/tools/data \
    -e environment_name="delius-core-sandpit" \
    -e CUSTOM_COMMON_PROPERTIES_DIR=/home/tools/data/env_configs/common \
    -e "TERM=xterm-256color" \
    --entrypoint "scripts/${1}" \
    mojdigitalstudio/hmpps-terraform-builder-lite:latest