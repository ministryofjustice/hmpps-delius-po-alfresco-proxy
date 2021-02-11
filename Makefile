default: build
.PHONY: build

unit-test:
	./gradlew clean test

integration-test:
	./gradlew clean cucumber

build:
	./gradlew clean test cucumber

package:
	./gradlew clean test cucumber bootRepackage

##############
# env builds #
##############

sandpit-plan:
	scripts/local-stack-action.sh sandpit plan

sandpit-apply:
	scripts/local-stack-action.sh sandpit apply

sandpit-show:
	scripts/local-stack-action.sh sandpit show

sandpit-destroy:
	scripts/local-stack-action.sh sandpit destroy

dev-destroy:
	scripts/local-stack-action.sh dev destroy

# Builds the application, docker image, uploads image to ECR and deploys to ECS
sandpit-ecs-deploy:
	scripts/local-ecs-deployer.sh sandpit

sandpit-ecr-clean:
	scripts/clean-up-sandpit-images-from-ecr.sh sandpit

sandpit-unlock:
	scripts/local-unlock-stack.sh sandpit

########## sandpit 2 ######
sandpit-2-plan:
	scripts/local-stack-action.sh sandpit-2 plan

sandpit-2-apply:
	scripts/local-stack-action.sh sandpit-2 apply

sandpit-2-show:
	scripts/local-stack-action.sh sandpit-2 show

# Builds the application, docker image, uploads image to ECR and deploys to ECS
sandpit-2-ecs-deploy:
	scripts/local-ecs-deployer.sh sandpit-2

sandpit-2-ecr-clean:
	scripts/clean-up-sandpit-images-from-ecr.sh sandpit-2

sandpit-2-unlock:
	scripts/local-unlock-stack.sh sandpit-2

sandpit-2-destroy:
	scripts/local-stack-action.sh sandpit-2 destroy

# Alfresco proxy Pipeline
eng-ci-plan: ## Run eng-ci-plan with ci_components_flag=true
	scripts/local-stack-action.sh dev plan true

eng-ci-apply: ## Run eng-ci-apply with ci_components_flag=true
	scripts/local-stack-action.sh dev apply true

eng-ci-destroy: ## Run eng-ci-destroy with ci_components_flag=true
	scripts/local-stack-action.sh dev destroy true