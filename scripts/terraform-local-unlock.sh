#!/usr/bin/env bash

set -ex

rm -rf terraform/env_configs
git clone https://github.com/ministryofjustice/hmpps-env-configs.git terraform/env_configs
cd ${HMPPS_BUILD_WORK_DIR}/components
/home/tools/data/scripts/stack-unlock.sh