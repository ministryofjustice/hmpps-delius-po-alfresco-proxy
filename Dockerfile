FROM mojdigitalstudio/hmpps-base-java

USER root
RUN apk --no-cache add curl

COPY build/libs/hmpps-delius-po-alfresco-proxy.jar /opt/app/hmpps-delius-po-alfresco-proxy.jar

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/app/hmpps-delius-po-alfresco-proxy.jar"]
EXPOSE 8080