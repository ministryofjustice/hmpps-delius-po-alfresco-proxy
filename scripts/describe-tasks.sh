#!/usr/bin/env bash

TASKGROUP=${1}
OUTPUTFILE=${2}

temp_role=$(aws sts assume-role --role-arn ${terraform_role_arn} --role-session-name alfresco-proxy-temp-ci-session --duration-seconds 900)

export AWS_ACCESS_KEY_ID=$(echo ${temp_role} | jq .Credentials.AccessKeyId | xargs)
export AWS_SECRET_ACCESS_KEY=$(echo ${temp_role} | jq .Credentials.SecretAccessKey | xargs)
export AWS_SESSION_TOKEN=$(echo ${temp_role} | jq .Credentials.SessionToken | xargs)

aws sts get-caller-identity

taskArns=`aws ecs list-tasks --cluster ${cluster_arn} | jq '.taskArns'`
if [ ! -z "${taskArns}" ] && [ "${taskArns}" != "[]" ]; then
    echo "--------------------------------------------------"
    describe_tasks_result=`aws ecs describe-tasks --cluster ${cluster_arn} --tasks "${taskArns}"`
    echo ${describe_tasks_result} | jq '.'
    echo "--------------------------------------------------"
    current_task=`echo ${describe_tasks_result} | jq --arg TASKGROUP "${TASKGROUP}" '.tasks[] | select(.group==$TASKGROUP) | {taskDefVersion: .taskDefinitionArn | split(":") | last, lastStatus: .lastStatus, desiredStatus: .desiredStatus, healthStatus: .healthStatus}'`
    echo ${current_task} | jq '.' > ${HMPPS_BUILD_WORK_DIR}/${OUTPUTFILE}
fi