#!/usr/bin/env bash

echo "Running Wiremock stack post build stage"
docker run --rm curlimages/curl http://alfresco.dev.delius-core.probation.hmpps.dsd.io:8080/__admin/mappings