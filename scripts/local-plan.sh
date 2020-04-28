#!/usr/bin/env bash

rm -rf terraform/env_configs
git clone https://github.com/ministryofjustice/hmpps-env-configs.git terraform/env_configs
cd ${HMPPS_BUILD_WORK_DIR}/terraform/components
${HMPPS_BUILD_WORK_DIR}/scripts/1-stack-plan.sh