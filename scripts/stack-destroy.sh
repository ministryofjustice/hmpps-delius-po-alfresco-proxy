#!/usr/bin/env bash
set -e

source ${HMPPS_BUILD_WORK_DIR}/env_configs/${environment_name}/${environment_name}.properties

env | sort

function destroy_stack() {
  if [ -d .terraform ]; then
    rm -rf .terraform
  fi
  sleep 1
  terragrunt destroy
}

for d in ./*/; do
  (cd "$d" && destroy_stack)
done

set +e
