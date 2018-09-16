Spark
------------------------------------

## Quick Start Spark

- Create spark-env.sh

`cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh` 
- To Enable Spark Shuttle Service which is required for _Spark Dynamic Allocation_

` echo "SPARK_WORKER_OPTS="-Dspark.shuffle.service.enabled=true" >> $SPARK_HOME/conf/spark-env.sh`
- Create log4j.properties for Spark 
```
cp $SPARK_HOME/conf/log4j.properties.template $SPARK_HOME/conf/log4j.properties
```

- Start spark master and slave 
```
$SPARK_HOME/sbin/start-master.sh
$SPARK_HOME/sbin/start-slave.sh
```

# FAQ
### How do I reduce the verbosity of the Spark Logging ?
