#!/usr/bin/env bash
set -e

env | sort

function apply_stack() {
    terragrunt apply ${environment_name}.plan
}

for d in ./*/ ; do
    (cd "$d" && apply_stack)
done