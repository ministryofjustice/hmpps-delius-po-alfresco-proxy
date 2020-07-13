#!/usr/bin/env bash
set -e

source ${HMPPS_BUILD_WORK_DIR}/env_configs/${environment_name}/${environment_name}.properties

env | sort

function show_stack() {
    if [ -d .terraform ]; then
        rm -rf .terraform
    fi
    sleep 1
    terragrunt show
}

for d in ./*/ ; do
    (cd "$d" && show_stack)
done

set +e