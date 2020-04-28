#!/usr/bin/env bash
set -e

source ${HMPPS_BUILD_WORK_DIR}/terraform/env_configs/${environment_name}/${environment_name}.properties

env | sort

function apply_stack() {
    terragrunt apply ${environment_name}.plan
}

for d in ./*/ ; do
    (cd "$d" && apply_stack)
done