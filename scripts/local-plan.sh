#!/usr/bin/env bash

cd ${HMPPS_BUILD_WORK_DIR}
rm -rf env_configs
git clone https://github.com/ministryofjustice/hmpps-env-configs.git env_configs
cd ${HMPPS_BUILD_WORK_DIR}/terraform/components
${HMPPS_BUILD_WORK_DIR}/scripts/1-stack-plan.sh