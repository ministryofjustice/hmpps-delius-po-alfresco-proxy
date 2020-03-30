default: build
.PHONY: build

build:
	docker run --rm -v $(PWD):/home/gradle/project -w /home/gradle/project gradle:6.3.0-jdk8 gradle clean test cucumber