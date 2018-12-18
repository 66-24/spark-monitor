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


## Making sense of the format specifiers used in java.utill.logger
In `/usr/lib/jvm/java-8-oracle/src.zip!/java/util/logging/SimpleFormatter.java:161`, SimpleFormatter uses the following:
```
String.format(format, date, source, logger, level, message, thrown);
```
`%5$s` applies to the 5th arg which is level 
`%6$s` applies to the 6th arg which is message
`%n`   represents a newline

See [docs](https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html)

## What is the default format specifier used by java.util.logger.SimpleFormatter ?
```
%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n"
```

## How do I specify a logging.properties to be used rather than the system one ?
 Use `-Djava.util.logging.config.file=src/main/resources/logging.properties`.
 If SparkMonitor is used to launch multiple apps via a configuration file, then 
 it might be better to generate the config as opposed to use a static logging.properties.
 
## How do I control the logging pattern of the Driver and Executor ?
Add the following Spark configuration:
```
    conf.put("spark.driver.extraJavaOptions", "-Dlog4j.configuration=file:/path/to/my_log4j.properties");
    conf.put("spark.executor.extraJavaOptions", "-Dlog4j.configuration=file:/path/to/my_log4j.properties");
```
Now define the layout pattern you want in `my_log4j.properties`

```
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p [%t] %C{5}: %m%n
```

## How many logging systems are used by this application ?
 - The driver and executor both use slfj-api which is then bound to log4j in this application.
 -  org.apache.spark.launcher.SparkLauncher uses java.util.logging.Logger to redirectLogs produced by the `Driver`
and `Executor(s)` 

## How do I create a fat jar ?
`gradle shadowJar

## How do I see the list of runtime dependencies ?
```bash
gradle dependencies --configuration=runtime
```

## How do I run the app from the command line ?

`java -DHOST=$(hostname) -jar ./build/libs/spark-monitor-all.jar`

## Application fails to launch
Make sure the `spark_examples*.jar` is present. See `org.apache.spark.launcher.SparkSubmitBuilder.`