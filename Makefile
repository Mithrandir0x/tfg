
cobertura:
	gradle -x :storm-hdfs:test cobertura

compile:
	gradle -x :storm-hdfs:test clean build fatJar

publish-topology: compile
	cp log-pipeline/build/libs/log-pipeline-1.0-SNAPSHOT.jar ../structor-mithrandir0x/

publish-loggateway: compile
	cp log-gateway/build/libs/log-gateway-1.0-SNAPSHOT.war ../structor-mithrandir0x/

publish: publish-topology publish-loggateway
