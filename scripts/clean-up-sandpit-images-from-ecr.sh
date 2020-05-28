#!/usr/bin/env bash

export cluster_arn="arn:aws:ecs:eu-west-2:723123699647:cluster/dlc-sandpit-spgw-ecs-cluster"
export service_name="dlc-sandpit-spgw-alfproxy"
export project_name="alfresco-proxy"
export AWS_REGION="eu-west-2"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"

terraform_role_arn="arn:aws:iam::723123699647:role/terraform"
temp_role=$(aws sts assume-role --role-arn ${terraform_role_arn} --role-session-name alfresco-proxy-temp-session --duration-seconds 900)

export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity

$(pwd)/scripts/get-deployed-image.sh
export image_tag=`cat image.tag | tr -d '\n'`
rm -f image.tag
echo "image_tag = ${image_tag}"

unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_SESSION_TOKEN

#--------------------------------------------------------------------------------
export my_aws_env="sandpit"

ENGINEERING_TERRAFORM_IAM_ROLE="arn:aws:iam::895523100917:role/terraform"
temp_role=$(aws sts assume-role --role-arn ${ENGINEERING_TERRAFORM_IAM_ROLE} --role-session-name alfresco-proxy-temp-session --duration-seconds 900)

export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity
eval $(aws --region ${AWS_REGION} ecr get-login --no-include-email)
aws sts get-caller-identity

$(pwd)/scripts/delete-undeployed-images.sh
