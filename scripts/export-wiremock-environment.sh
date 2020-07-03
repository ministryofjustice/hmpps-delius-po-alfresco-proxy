#!/usr/bin/env bash

export TF_VAR_docker_image="rodolpheche/wiremock"
export TF_VAR_image_version="2.26.3-alpine"
export TF_VAR_is_wiremock="true"
#export TF_VAR_internal_health_command="http://localhost:8080/__admin/mappings"
export TF_VAR_internal_health_command="exit 0"