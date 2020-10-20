FROM mojdigitalstudio/hmpps-base-java

USER root

COPY build/libs/hmpps-delius-po-alfresco-proxy.jar /opt/app/hmpps-delius-po-alfresco-proxy.jar
COPY docker-image-scripts /opt/docker-image-scripts


RUN apk update; \
    apk add python-dev py-setuptools; \
    pip install -U pip; \
    pip install awscli --upgrade; \
    apk --no-cache add curl; \
    rm -rf ~/.cache ~/.gems; \
    rm -rf /var/cache/apk/*; \
    apk update

EXPOSE 8080

ENTRYPOINT ["/opt/docker-image-scripts/docker-entrypoint.sh"]
