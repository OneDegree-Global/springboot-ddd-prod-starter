CUR_BRANCH := $(shell git branch --show-current)
JACOCO_RPORTS := "application/target/site/jacoco/jacoco.xml,domain/target/site/jacoco/jacoco.xml,external/target/site/jacoco/jacoco.xml,messaging/target/site/jacoco/jacoco.xml,persistence/target/site/jacoco/jacoco.xml,search/target/site/jacoco/jacoco.xml,web/target/site/jacoco/jacoco.xml"

clean:
	mvn clean
	docker-compose -f quickstart.yml -f quickstart-postgresql.yml -f quickstart-metrics.yml down

fast-test: clean
	mvn -T 5 -DexcludedGroups="slow" clean test
	mvn jacoco:report


slow-test: clean
	mvn -T 5 clean test
	mvn jacoco:report

package: clean
	mvn package -DskipTests=true

quick-start: package
	docker-compose -f quickstart.yml -f quickstart-postgresql.yml -f quickstart-metrics.yml up

pr-check:
	git reset --hard
	git checkout main
	mvn -T 5 clean test
	mvn jacoco:report
	sonar-scanner -Dsonar.projectBaseDir=. -Dsonar.host.url=http://sonar.onedegree.hk/ -Dsonar.projectName=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.projectKey=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.java.binaries="**/target" -Dsonar.exclusions=**/src/test/java/**/*.* -Dsonar.coverage.jacoco.xmlReportPaths=$(JACOCO_RPORTS) -Dsonar.projectVersion=main
	git reset --hard
	git checkout $(CUR_BRANCH)
	mvn -T 5 clean test
	mvn jacoco:report
	sonar-scanner -Dsonar.projectBaseDir=. -Dsonar.host.url=http://sonar.onedegree.hk/ -Dsonar.projectName=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.projectKey=springboot-ddd-prod-starter:$(CUR_BRANCH) -Dsonar.java.binaries="**/target" -Dsonar.exclusions=**/src/test/java/**/*.* -Dsonar.coverage.jacoco.xmlReportPaths=$(JACOCO_RPORTS) -Dsonar.projectVersion=$(CUR_BRANCH)

check-dependency:
	mvn org.owasp:dependency-check-maven:check
