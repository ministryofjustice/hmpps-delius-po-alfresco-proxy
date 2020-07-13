#!/usr/bin/env bash

make package

timestamp() {
  date +"%Y%m%d%H%M%S"
}

mypatch=`git diff`

if [ -z "${mypatch}" ]; then
    postfix=`timestamp`
else
    md5=`echo -n ${mypatch} | md5sum`
    postfix=${md5:0:7}
fi

export my_aws_env=${1}
export AWS_REGION="eu-west-2"
export IMAGE_REGISTRY="895523100917.dkr.ecr.eu-west-2.amazonaws.com"
export IMAGE_NAME="hmpps/spgw-alfresco-proxy"
export TAG="${my_aws_env}-${postfix}"
export REPOSITORY_URI=${IMAGE_REGISTRY}/${IMAGE_NAME}

echo "image tag => ${TAG}"

$(pwd)/scripts/build-docker-image.sh
$(pwd)/scripts/upload-sandpit-docker-image.sh

terraform_role_arn="arn:aws:iam::723123699647:role/terraform"
service_name="dlc-${my_aws_env}-spgw-alfproxy"
cluster_arn="arn:aws:ecs:eu-west-2:723123699647:cluster/${service_name}"
docker_image=${REPOSITORY_URI}:${TAG}
desired_count=1
deployment_timeout=900

docker run -it --rm \
    -v ~/.aws:/aws \
    -e AWS_CONFIG_FILE=/aws/config \
    -e AWS_SHARED_CREDENTIALS_FILE=/aws/credentials \
    silintl/ecs-deploy:3.7.1 \
    --profile hmpps-dev-admin \
    --region ${AWS_REGION} \
    --cluster ${cluster_arn} \
    --service-name ${service_name} \
    --image ${docker_image} \
    --desired-count ${desired_count} \
    --timeout ${deployment_timeout} \
    --enable-rollback \
    --verbose