#!/usr/bin/env bash
set -e

export my_aws_env="${1}"
export src_root_dir=$(pwd)
export ci_components_flag=${3:-false}


if "$ci_components_flag"  ; then
  $(pwd)/scripts/clone-engineering-platform-env-configs.sh
fi

$(pwd)/scripts/terraform-local-builder.sh terraform-local-${2}.sh
