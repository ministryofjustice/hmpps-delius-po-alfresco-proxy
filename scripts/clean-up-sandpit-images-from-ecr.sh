#!/usr/bin/env bash

export my_aws_env="sandpit"
export service_name="dlc-${my_aws_env}-spgw-alfproxy"
export cluster_arn="arn:aws:ecs:eu-west-2:723123699647:cluster/${service_name}"
export project_name="alfresco-proxy"
export AWS_REGION="eu-west-2"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"
export ENVIRONMENT_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::723123699647:role/terraform"
export ENGINEERING_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::895523100917:role/terraform"

$(pwd)/scripts/get-deployed-image.sh
export image_tag=`cat image.tag | tr -d '\n'`
rm -f image.tag
echo "image_tag = ${image_tag}"

source $(pwd)/scripts/unassume-role.sh

#--------------------------------------------------------------------------------

source $(pwd)/scripts/assume-role.sh ${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}

eval $(aws --region ${AWS_REGION} ecr get-login --no-include-email)
aws sts get-caller-identity

$(pwd)/scripts/delete-undeployed-images.sh
