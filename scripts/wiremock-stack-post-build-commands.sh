#!/usr/bin/env bash

echo "Running Wiremock stack post build stage"
apk --no-cache add curl
# Wait for wiremock to deploy
date
echo "Waiting for wiremock to start up (12 minutes)"
sleep 720
date