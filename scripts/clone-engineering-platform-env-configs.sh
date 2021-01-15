#!/usr/bin/env bash

rm -rf terraform/ci_env_configs
git clone git@github.com:ministryofjustice/hmpps-engineering-platform-terraform.git
mv hmpps-engineering-platform-terraform/env_configs terraform/ci_env_configs
rm -rf hmpps-engineering-platform-terraform
