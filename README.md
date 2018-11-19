Spark
------------------------------------

## Quick Start Spark

- Create spark-env.sh

`cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh` 
- To Enable Spark Shuttle Service which is required for _Spark Dynamic Allocation_.
Spark Shuffle Service is started by the Worker on port *7337* by default

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
### How do I redirect Spark Launcher Logging ?
 *Not to be confused with the embedded driver logs in client mode* 
 
 These are the sparklaunch logs.
 
 Example:
 
 ```
 Sep 17, 2018 8:12:44 PM org.apache.spark.launcher.OutputRedirector redirect
 INFO: 18/09/17 20:12:44 WARN Utils: Your hostname, pop-os resolves to a loopback address: 127.0.1.1; using 192.168.86.49 instead (on interface wlp59s0)
 Sep 17, 2018 8:12:44 PM org.apache.spark.launcher.OutputRedirector redirect
 INFO: 18/09/17 20:12:44 WARN Utils: Set SPARK_LOCAL_IP if you need to bind to another address
 Sep 17, 2018 8:12:44 PM org.apache.spark.launcher.OutputRedirector redirect
 INFO: 18/09/17 20:12:44 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
 Sep 17, 2018 8:12:44 PM org.apache.spark.launcher.OutputRedirector redirect
 INFO: 18/09/17 20:12:44 INFO SparkContext: Running Spark version 2.3.1
 Sep 17, 2018 8:12:44 PM org.apache.spark.launcher.OutputRedirector redirect
 INFO: 18/09/17 20:12:44 INFO SparkContext: Submitted application: Spark Pi
```

There are a few ways:
- redirect to a file - no log file size management
```
sparkLauncher.redirectError(new File("/dev/null"));
```
For some reason all the spark logging is tied to stderr.
stdout displays the output which in this case is 

```
Pi is roughly 3.1415003141500315
```

- Inherit the logging to stdout and stderr of the this application
```
sparkLauncher.redirectError(ProcessBuilder.Redirect.INHERIT);
sparkLauncher.redirectOutput(ProcessBuilder.Redirect.INHERIT);
```
In this case the output does not have the annoying header that bloats the logs
```
Sep 16, 2018 8:55:55 PM org.apache.spark.launcher.OutputRedirector redirect                   

```
However, in this case its not possible to intercept and mute the **SparkLauncher** logs.
Not to be confused with the embedded driver logs. If say the embedded driver uses 
log4j instead of java.util.logging, its possible to redirect driver logs to a file. 

- Finally, its possible to configure java.util.logging to re-route the **SparkLauncher** logs
to a file. This log file handler can be configured to rotate when a _max_ size is reached
and to retain a certain number of _rotated_ logs.
Set the log format to get rid of the annyoing `Sep 16, 2018 8:55:55 PM org.apache.spark.launcher.OutputRedirector redirect`
```
        System.setProperty("java.util.logging.SimpleFormatter.format","%5$s%6$s%n");
```


## How do I specify a logging.properties to be used rather than the system one ?
 Use `-Djava.util.logging.config.file=src/main/resources/logging.properties`.
 If SparkMonitor is used to launch multiple apps via a configuration file, then 
 it might be better to generate the config as opposed to use a static logging.properties.

## How do I create a fat jar ?
`gradle shadowJar
`
## How do I run the app from the command line ?

`java -DHOST=$(hostname) -jar ./build/libs/spark-monitor-all.jar`