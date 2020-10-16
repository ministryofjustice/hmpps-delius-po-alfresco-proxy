FROM mojdigitalstudio/hmpps-base-java

ENV TINI_VERSION v0.18.0

USER root
RUN apk --no-cache add curl

COPY build/libs/hmpps-delius-po-alfresco-proxy.jar /opt/app/hmpps-delius-po-alfresco-proxy.jar
COPY docker-image-scripts /tmp/docker-image-scripts

# Add Tini to use as the entrypoint provisioner
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini-static /tini
RUN chmod +x /tini

EXPOSE 8080

ENTRYPOINT ["/tini", "-v", "--", "/tmp/docker-image-scripts/docker-entrypoint.sh"]
CMD ["alf-proxy"]