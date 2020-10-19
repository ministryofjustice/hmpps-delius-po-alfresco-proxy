#!/bin/bash +x

set -e

# Create certs directory
mkdir -p /opt/app/truststore

# Copy all of the certs from s3 bucket for this environment
aws s3 cp s3://${SPG_CERTIFICATE_BUCKET}${SPG_CERTIFICATE_PATH}truststore_jks  /opt/app/truststore --recursive

echo "Truststore import completed successfully"