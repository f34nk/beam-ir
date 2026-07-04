
all: format test

.PHONY: test
test: 
	mvn test

.PHONY: build
build:
	mvn install
	tree ~/.m2/repository/io/beam/beam-ir

.PHONY: release
release:
	mvn package       # build the JAR

.PHONY: format
format: format/java

.PHONY: format/java
format/java:
	mvn spotless:apply

.PHONY: format/erlang
format/erlang:
	cd src/test/resources/erlang && \
	erlfmt --write *.erl

.PHONY: clean
clean:
	rm -rf target/
