#!/usr/bin/env bash
set -e

source ${HMPPS_BUILD_WORK_DIR}/env_configs/${environment_name}/${environment_name}.properties

env | sort

function plan_stack() {
    if [ -d .terraform ]; then
        rm -rf .terraform
    fi
    rm -f ${environment_name}.plan
    sleep 1
    terragrunt init
    terragrunt plan --out ${environment_name}.plan
}

for d in ./*/ ; do
    (cd "$d" && plan_stack)
done

set +e