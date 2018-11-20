#!/bin/bash

if [[ -z "$PROJECT_HOME" ]]; then
    echo "Set shell property \"PROJECT_HOME\""
    exit 1
fi

LOG4J_CONFIG="-Dlog4j.configuration=file://${PROJECT_HOME}/src/main/resources/log4j.properties"

spark-submit \
	--master spark://pop-os:7077 \
	--deploy-mode client \
	--conf spark.driver.cores=1 \
	--conf spark.driver.memory=512M \
	--conf spark.executor.cores=1 \
	--conf spark.executor.memory=1G \
	--conf spark.cores.max=2 \
	--conf "spark.driver.extraJavaOptions=${LOG4J_CONFIG}" \
    --conf "spark.executor.extraJavaOptions=${LOG4J_CONFIG}" \
       	--class org.apache.spark.examples.SparkPi \
	$HOME/.sdkman/candidates/spark/current/examples/jars/spark-examples_2.11-2.3.1.jar \
	100

