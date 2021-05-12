CUR_BRANCH := $(shell git branch --show-current)

clean:
	mvn clean
	docker-compose -f quickstart.yml -f quickstart-postgresql.yml -f quickstart-metrics.yml down

fast-test: clean
	mvn -T 5 -DexcludedGroups="slow" clean test

slow-test: clean
	mvn -T 5 clean test

package: clean
	mvn package -DskipTests=true

quick-start: package
	docker-compose -f quickstart.yml -f quickstart-postgresql.yml -f quickstart-metrics.yml up

pr-check:
	git reset --hard
	git checkout main
	mvn -T 5 clean test
	sonar-scanner -Dsonar.projectBaseDir=. -Dsonar.host.url=http://sonar.onedegree.hk/ -Dsonar.projectName=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.projectKey=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.java.binaries="**/target" -Dsonar.exclusions=**/src/test/java/**/*.* -Dsonar.projectVersion=main
	git reset --hard
	git checkout $(CUR_BRANCH)
	mvn -T 5 clean test
	sonar-scanner -Dsonar.projectBaseDir=. -Dsonar.host.url=http://sonar.onedegree.hk/ -Dsonar.projectName=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.projectKey=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.java.binaries="**/target" -Dsonar.exclusions=**/src/test/java/**/*.* -Dsonar.projectVersion=$(CUR_BRANCH)
