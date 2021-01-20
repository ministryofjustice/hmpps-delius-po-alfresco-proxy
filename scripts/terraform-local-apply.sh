#!/usr/bin/env bash

set -e

if [[ "$ci_components_flag" = true ]] ; then
    source ${HMPPS_BUILD_WORK_DIR}/ci_env_configs/dev.properties
    cd ${HMPPS_BUILD_WORK_DIR}/ci-components
else
      source ${HMPPS_BUILD_WORK_DIR}/env_configs/${environment_name}/${environment_name}.properties
      cd ${HMPPS_BUILD_WORK_DIR}/components
fi

/home/tools/data/scripts/2-stack-apply.sh