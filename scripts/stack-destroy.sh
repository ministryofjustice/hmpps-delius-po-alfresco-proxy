#!/usr/bin/env bash
set -e

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
