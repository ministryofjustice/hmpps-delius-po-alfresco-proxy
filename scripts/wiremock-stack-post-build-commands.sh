#!/usr/bin/env bash

echo "Running Wiremock stack post build stage"
apk --no-cache add curl
# Wait for wiremock to deploy
echo "Waiting for wiremock to start up"
sleep 720
curl -sS http://wiremock-dlc-dev-spgw-alfproxy.sandpit.delius-core.probation.hmpps.dsd.io:8080/__admin/mappings