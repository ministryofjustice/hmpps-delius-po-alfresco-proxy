#!/bin/bash
set -e

# Add alf-proxy as command if needed
if [ "${1:0:1}" = '-' ]; then
    set -- alf-proxy "$@"
fi

# Drop root privileges if we are running alf-proxy
# allow the container to be started with `--user`
if [ "$1" = 'alf-proxy' -a "$(id -u)" = '0' ]; then
  /usr/bin/java -jar /opt/app/hmpps-delius-po-alfresco-proxy.jar
fi
