#!/usr/bin/env bash
set -e

export my_aws_env="${1}"
export src_root_dir=$(pwd)

$(pwd)/scripts/terraform-local-builder.sh terraform-local-${2}.sh
