#!/usr/bin/env bash

set -e

export my_aws_env="${1}"
export src_root_dir=$(pwd)

function read_lock_id() {
    echo -n "Enter lock id: "
    read lock_id
}

while true; do
  read_lock_id

  if [[ -n "${lock_id}" ]]
  then
    printf "About to unlock terraform lock-id %s\n" "${lock_id}"
    break
  fi
done

echo "-----------------------------------------"
export  lockId=${lock_id}

$(pwd)/scripts/terraform-local-builder.sh terraform-local-unlock.sh