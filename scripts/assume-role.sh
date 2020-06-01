#!/usr/bin/env bash

terraform_role_arn=${1}
temp_role=$(aws sts assume-role --role-arn ${terraform_role_arn} --role-session-name spgw-temp-session --duration-seconds 900)
if [ $? -ne 0 ]; then
  echo "Failed to assume role. Exiting."
  exit 1
fi
export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity