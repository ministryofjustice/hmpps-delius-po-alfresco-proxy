default: build
.PHONY: build

build:
	docker run --rm \
		-e APPLICATION_NAME=hmpps-delius-po-alfresco-proxy \
		-e SPG_ALFRESCO_HEALTH_ENDPOINT=/alfresco/service/noms-spg/notificationStatus \
		-e SPG_ALFRESCO_BASE_URL= \
		-v $(PWD):/home/gradle/project \
		-w /home/gradle/project gradle:6.3.0-jdk8 gradle clean test cucumber

run:
	docker run --rm \
		-p 8080:8080 \
		-e APPLICATION_NAME=hmpps-delius-po-alfresco-proxy \
		-e SPG_ALFRESCO_HEALTH_ENDPOINT=/alfresco/service/noms-spg/notificationStatus \
		-e SPG_ALFRESCO_BASE_URL= \
		-v $(PWD):/home/gradle/project \
		-w /home/gradle/project \
		gradle:6.3.0-jdk8 gradle clean bootRun