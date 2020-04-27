#!/usr/bin/env bash

taskArns=`aws ecs list-tasks --cluster $cluster_arn | jq '.taskArns'`
aws ecs describe-tasks --cluster $cluster_arn --tasks $taskArns