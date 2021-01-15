#!/usr/bin/env bash
set -e

env | sort

function plan_stack() {
    if [ -d .terraform ]; then
        rm -rf .terraform
    fi
    rm -f ${environment_name}.plan
    sleep 1
    terragrunt fmt
    terragrunt init
    terragrunt refresh
    terragrunt plan --out ${environment_name}.plan
}

for d in ./*/ ; do
    (cd "$d" && plan_stack)
done

set +e