#!/usr/bin/env bash

export IMAGE_REGISTRY="895523100917.dkr.ecr.eu-west-2.amazonaws.com"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"
export TAG="sandpit"
export REPOSITORY_URI=${IMAGE_REGISTRY}/${IMAGE_NAME}

ENGINEERING_TERRAFORM_IAM_ROLE="arn:aws:iam::895523100917:role/terraform"
temp_role=$(aws sts assume-role --role-arn ${ENGINEERING_TERRAFORM_IAM_ROLE} --role-session-name alfresco-proxy-temp-session --duration-seconds 900)

export AWS_REGION="eu-west-2"
export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity
eval $(aws --region ${AWS_REGION} ecr get-login --no-include-email)
aws sts get-caller-identity

$(pwd)/scripts/upload-docker-image.sh