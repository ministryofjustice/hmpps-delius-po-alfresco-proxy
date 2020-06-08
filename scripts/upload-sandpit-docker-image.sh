#!/usr/bin/env bash

set -e

ENGINEERING_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::895523100917:role/terraform"
source $(pwd)/scripts/assume-role.sh ${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}

eval $(aws --region ${AWS_REGION} ecr get-login --no-include-email)
aws sts get-caller-identity

$(pwd)/scripts/upload-docker-image.sh