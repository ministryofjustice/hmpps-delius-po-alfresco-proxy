#!/usr/bin/env bash
set -e

source ${CODEBUILD_SRC_DIR}/env_configs/${environment_name}/${environment_name}.properties

env | sort

function plan_stack() {
    if [ -d .terraform ]; then
        rm -rf .terraform
    fi
    sleep 1
    terragrunt init
    set +e
    terragrunt plan -detailed-exitcode --out ${environment_name}.plan > tf.plan.out
    exitcode="$?"
    set -e
    if [ "$exitcode" == '1' ]; then
        exit 1
    fi

    cat tf.plan.out
#
#    parse-terraform-plan -i tf.plan.out | jq '.changedResources[] | (.action != "update") or (.changedAttributes | to_entries | map(.key != "tags.source-hash") | reduce .[] as $item (false; . or $item))' | jq -e -s 'reduce .[] as $item (false; . or $item) == false'
}

for d in ./*/ ; do
    (cd "$d" && plan_stack)
done

set -e