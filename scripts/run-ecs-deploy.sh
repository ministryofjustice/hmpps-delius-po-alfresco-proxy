#!/usr/bin/env bash

set -ex

/ecs-deploy \
    --aws-assume-role ${terraform_role_arn} \
    --region ${AWS_REGION} \
    --cluster ${cluster_arn} \
    --service-name ${service_name} \
    --image ${docker_image} \
    --desired-count ${desired_count} \
    --timeout ${deployment_timeout} \
    --verbose