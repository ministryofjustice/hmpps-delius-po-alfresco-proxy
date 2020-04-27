#!/usr/bin/env bash

taskArns=`aws ecs list-tasks --cluster $cluster_name | jq '.taskArns'`
aws ecs describe-tasks --cluster $cluster_name --tasks $taskArns