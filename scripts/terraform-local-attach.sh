#!/usr/bin/env bash

#launches a bash terminal with env configs set
#useful for things like removing state or other adhoc terraform commands

set -e
rm -rf terraform/env_configs
git clone https://github.com/ministryofjustice/hmpps-env-configs.git terraform/env_configs


source ${HMPPS_BUILD_WORK_DIR}/env_configs/${environment_name}/${environment_name}.properties
cd ${HMPPS_BUILD_WORK_DIR}/components


/bin/bash