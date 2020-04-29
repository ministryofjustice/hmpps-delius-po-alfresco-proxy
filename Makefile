default: build
.PHONY: build

build:
	docker run --rm \
    	--env-file=config.env \
		-v $(PWD):/home/gradle/project \
		-w /home/gradle/project \
		gradle:6.3.0-jdk8 gradle clean test cucumber

run:
	docker run --rm \
		-p 8080:8080 \
    	--env-file=config.env \
		-v $(PWD):/home/gradle/project \
		-w /home/gradle/project \
		gradle:6.3.0-jdk8 gradle clean bootRun

package:
	docker run --rm \
    	--env-file=config.env \
		-v $(PWD):/home/gradle/project \
		-w /home/gradle/project \
		gradle:6.3.0-jdk8 gradle clean test cucumber bootJar

build-image:
	scripts/build-sandpit-docker-image.sh

upload-image:
	scripts/upload-sandpit-docker-image.sh

sandpit-plan:
	scripts/local-terraform-builder.sh delius-core-sandpit terraform-sandpit-plan.sh

sandpit-apply:
	scripts/local-terraform-builder.sh delius-core-sandpit terraform-sandpit-apply.sh