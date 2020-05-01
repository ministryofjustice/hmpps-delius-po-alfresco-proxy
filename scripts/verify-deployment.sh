#!/usr/bin/env bash

task_group=${1}
pre_deployment_taskdef_file=${2}
post_deployment_taskdef_file=${3}

preDeploymentVersionStr=`cat ${HMPPS_BUILD_WORK_DIR}/${pre_deployment_taskdef_file} | jq '.taskDefVersion' | awk '{print substr($1,2,length($1)-2)}'`
preDeploymentVersion="$(($preDeploymentVersionStr + 0))"
currentVersion=${preDeploymentVersion}

next_wait_time=0
counter=0
lastStatus=""
desiredStatus=""
until ([ ${currentVersion} -gt ${preDeploymentVersion} ] && [ ${lastStatus} == "RUNNING" ] && [ ${desiredStatus} == "RUNNING" ]) || [ $next_wait_time -eq ${DEPLOY_WAIT_TIME_OUT} ]; do
    let counter=counter+1
    scripts/describe-tasks.sh ${task_group} ${post_deployment_taskdef_file}
    echo "--------------- counter=${counter} ---------------"
    cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file}
    echo "------------------------------------------------"
    tempVer=`cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file} | jq '.taskDefVersion' | awk '{print substr($1,2,length($1)-2)}'`
    let currentVersion=$(($tempVer + 0))
    let lastStatus=`cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file} | jq '.lastStatus'`
    let desiredStatus=`cat ${HMPPS_BUILD_WORK_DIR}/${post_deployment_taskdef_file} | jq '.desiredStatus'`
    sleep 30
    let next_wait_time=next_wait_time+5
done