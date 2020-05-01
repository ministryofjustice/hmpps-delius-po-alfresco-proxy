#!/usr/bin/env bash

task_group=${1}
pre_deployment_taskdef_file=${2}
post_deployment_taskdef_file=${3}

preDeploymentVersionStr=`cat ${HMPPS_BUILD_WORK_DIR}/${pre_deployment_taskdef_file} | jq '.taskDefVersion' | awk '{print substr($1,2,length($1)-2)}'`
preDeploymentVersion="$(($preDeploymentVersionStr + 0))"
currentVersion=${preDeploymentVersion}

NEXT_WAIT_TIME=0
TIME_OUT=20
counter=0
until [ ${currentVersion} -gt ${preDeploymentVersion} ] || [ $NEXT_WAIT_TIME -eq ${TIME_OUT} ]; do
    let counter=counter+1
    scripts/describe-tasks.sh ${task_group} ${post_deployment_taskdef_file}
    echo "--------------- counter=$counter ---------------"
    cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file}
    echo "------------------------------------------------"
    tempVer=`cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file} | jq '.taskDefVersion' | awk '{print substr($1,2,length($1)-2)}'`
    currentVersion=$(($tempVer + 0))
    sleep 5
    let NEXT_WAIT_TIME=NEXT_WAIT_TIME+5
done