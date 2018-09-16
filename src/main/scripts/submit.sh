#!/bin/bash

spark-submit \
	--master spark://pop-os:7077 \
	--deploy-mode client \
	--conf spark.driver.cores=1 \
	--conf spark.driver.memory=512M \
	--conf spark.executor.cores=1 \
	--conf spark.executor.memory=1G \
	--conf spark.cores.max=2 \
       	--class org.apache.spark.examples.SparkPi \
	$HOME/.sdkman/candidates/spark/current/examples/jars/spark-examples_2.11-2.3.1.jar \
	10000

