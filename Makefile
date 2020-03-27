default: build
.PHONY: build

build:
	docker run --rm -v $(PWD):/home/gradle/project -w /home/gradle/project gradle gradle clean test cucumber