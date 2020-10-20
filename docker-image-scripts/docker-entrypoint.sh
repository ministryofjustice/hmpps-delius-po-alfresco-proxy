#!/bin/bash
set -e

main() {
  bootstrap_app
  start_alfproxy "$@"
}

bootstrap_app() {
  bash /opt/docker-image-scripts/import-truststore.sh
}

start_alfproxy() {
  usr/bin/java -jar /opt/app/hmpps-delius-po-alfresco-proxy.jar
}

main "$@"
