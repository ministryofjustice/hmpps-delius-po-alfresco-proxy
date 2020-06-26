#!/usr/bin/env bash

echo "Running Wiremock stack post build stage"
apk --no-cache add curl
sleep 20
curl http://alfresco.dev.delius-core.probation.hmpps.dsd.io:8080/__admin/mappings