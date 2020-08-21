#!/usr/bin/env bash

set -e

# ${1} - the script to run: plan or apply
# See Makefile for usage

export service_name="dlc-${my_aws_env}-spgw-alfproxy"
export cluster_arn="arn:aws:ecs:eu-west-2:723123699647:cluster/${service_name}"
export container_name="alfresco-proxy"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"
export AWS_REGION="eu-west-2"
export ENVIRONMENT_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::723123699647:role/terraform"
export ENGINEERING_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::895523100917:role/terraform"

$(pwd)/scripts/get-deployed-image.sh
image_tag=`cat image.tag | head -n 1 | tr -d '\n'`
rm -f image.tag
echo "image_tag = ${image_tag}"

docker run -it --rm \
    -v ${src_root_dir}:/home/tools/data \
    -v ~/.aws:/home/tools/.aws \
    -e AWS_PROFILE=hmpps-token \
    -e TF_LOG=INFO \
    -e HMPPS_BUILD_WORK_DIR=/home/tools/data/terraform \
    -e environment_name="delius-core-${my_aws_env}" \
    -e TF_VAR_image_version="${image_tag}" \
    -e LOCK_ID="${lockId}" \
    -e CUSTOM_COMMON_PROPERTIES_DIR=/home/tools/data/terraform/env_configs/common \
    -e "TERM=xterm-256color" \
    --entrypoint "scripts/${1}" \
    mojdigitalstudio/hmpps-terraform-builder-0-12:latest