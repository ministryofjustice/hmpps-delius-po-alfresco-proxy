#!/usr/bin/env bash

set -e

# ${1} - the aws environment
# ${2} - the script to run: plan or apply
# See Makefile for usage

export terraform_role_arn="arn:aws:iam::723123699647:role/terraform"
export cluster_arn="arn:aws:ecs:eu-west-2:723123699647:cluster/dlc-sandpit-spgw-ecs-cluster"
export service_name="dlc-sandpit-spgw-alfproxy"

image_tag=$($(pwd)/scripts/get-deployed-image.sh) | tr -d '\n'
echo "image_tag = ${image_tag}"

docker run -it --rm \
    -v $(pwd):/home/tools/data \
    -v ~/.aws:/home/tools/.aws \
    -e AWS_PROFILE=hmpps-token \
    -e TF_LOG=INFO \
    -e HMPPS_BUILD_WORK_DIR=/home/tools/data/terraform \
    -e environment_name="${1}" \
    -e TF_VAR_image_version="${image_tag}" \
    -e CUSTOM_COMMON_PROPERTIES_DIR=/home/tools/data/terraform/env_configs/common \
    -e "TERM=xterm-256color" \
    --entrypoint "scripts/${2}" \
    mojdigitalstudio/hmpps-terraform-builder-lite:latest