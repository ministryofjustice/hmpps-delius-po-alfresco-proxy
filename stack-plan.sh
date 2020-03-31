#!/usr/bin/env bash
set +e

env | sort

rm -rf src/main/terraform/common_env_configs
git clone https://github.com/ministryofjustice/hmpps-env-configs.git src/main/terraform/common_env_configs

source src/main/terraform/common_env_configs/${environment_name}/${environment_name}.properties

for d in ./*/ ; do
    (cd "$d" && if [ -d .terraform ]; then rm -rf .terraform; fi; sleep 1; terragrunt init; terragrunt plan -detailed-exitcode --out ${environment_name}.plan > tf.plan.out; exitcode="\$?"; if [ "\$exitcode" -ne 0 ]; then exit "\$exitcode"; fi;)
done

cat tf.plan.out

if [ exitcode == '1' ]; then
    exit 1
fi

parse-terraform-plan -i tf.plan.out | jq '.changedResources[] | (.action != "update") or (.changedAttributes | to_entries | map(.key != "tags.source-hash") | reduce .[] as $item (false; . or $item))' | jq -e -s 'reduce .[] as $item (false; . or $item) == false'

set -e