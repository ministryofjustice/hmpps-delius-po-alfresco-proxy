#!/usr/bin/env bash

set -ex

test_results_bucket="tf-eu-west-2-hmpps-eng-dev-ci-test-results-s3bucket"
project_name="hmpps-po-alfresco-proxy"
now=$(date +"%m-%d-%Y")

aws s3 cp build/test-results s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-unit-test-results  --recursive
aws s3 cp build/reports/tests s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-unit-test-reports --recursive

aws s3 cp build/cucumber s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-integration-test-results --recursive
aws s3 cp build/cucumber-html-reports s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-integration-test-reports --recursive