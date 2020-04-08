FROM openjdk:8-jdk-alpine

COPY build/libs/hmpps-delius-po-alfresco-proxy.jar /opt/app/hmpps-delius-po-alfresco-proxy.jar

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/app/hmpps-delius-po-alfresco-proxy.jar"]
EXPOSE 8080