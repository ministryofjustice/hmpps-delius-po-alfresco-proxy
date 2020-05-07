#!/usr/bin/env bash

/ecs-deploy \
    --aws-access-key ${AWS_ACCESS_KEY_ID} \
    --aws-secret-key ${AWS_SECRET_ACCESS_KEY} \
    --region ${AWS_REGION} \
    --cluster ${cluster_arn} \
    --service-name ${service_name} \
    --image ${docker_image} \
    --desired-count ${desired_count} \
    --verbose