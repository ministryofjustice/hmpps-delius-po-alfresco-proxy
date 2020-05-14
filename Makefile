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
	scripts/terraform-local-builder.sh delius-core-dev terraform-local-plan.sh

sandpit-apply:
	scripts/terraform-local-builder.sh delius-core-sandpit terraform-local-apply.sh

terragrunt-unlock:
	scripts/terraform-local-builder.sh delius-core-dev terraform-local-unlock.sh

terragrunt-refresh:
	scripts/terraform-local-builder.sh delius-core-dev terraform-local-refresh.sh