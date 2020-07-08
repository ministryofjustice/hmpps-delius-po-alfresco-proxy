#!/usr/bin/env bash
echo "Assuming Engineering Role"
ENGINEERING_TERRAFORM_IAM_ROLE_ARN="arn:aws:iam::895523100917:role/terraform"
source $(pwd)/scripts/assume-role.sh ${ENGINEERING_TERRAFORM_IAM_ROLE_ARN}


aws --version

echo "Getting ECR Login"
#echo "(aws ecr get-login-password --no-include-email --region ${AWS_REGION})"
#eval $(aws ecr get-login-password --no-include-email --region ${AWS_REGION})


aws ecr get-login-password \
    --region ${AWS_REGION} \
| docker login \
    --username AWS \
    --password-stdin 895523100917.dkr.ecr.${AWS_REGION}.amazonaws.com

echo "Login got"








$(pwd)/scripts/upload-docker-image.sh