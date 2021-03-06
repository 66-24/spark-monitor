package org.apache.spark.launcher;

import java.net.URISyntaxException;
import java.util.HashMap;

class SparkSubmitBuilder {
    private final static String pi = "\u03C0";
    private final static String HOST = System.getProperty("HOST");
    private final static String SPARK_HOME = System.getenv("SPARK_HOME");


    String getCmd() {
        SparkLauncher sparkLauncher = buildLauncher();

        return String.join(" ", sparkLauncher.builder.buildSparkSubmitArgs());
    }

    SparkLauncher buildLauncher() {
        final SparkLauncher sparkLauncher = new SparkLauncher();

        getEnv().forEach(sparkLauncher::setConf);

        sparkLauncher.setDeployMode("client");


        sparkLauncher.setAppName("Spark " + pi);
        sparkLauncher.setAppResource(SPARK_HOME + "/examples/jars/spark-examples_2.11-2.3.1.jar");
        sparkLauncher.setSparkHome(SPARK_HOME);
        sparkLauncher.setMainClass("org.apache.spark.examples.SparkPi");
        sparkLauncher.setMaster("spark://" + HOST + ":7077");
        sparkLauncher.addAppArgs("100");
        return sparkLauncher;
    }

    private HashMap<String, String> getEnv() {
        final String log4jConfig = getLog4jConfig();
        HashMap<String, String> conf = new HashMap<>();
        conf.put("spark.executor.cores", "1");
        conf.put("spark.executor.memory", "1G");
        conf.put("spark.driver.cores", "1");
        conf.put("spark.driver.memory", "512M");
        conf.put("spark.cores.max", "2");
        conf.put("spark.driver.extraJavaOptions", log4jConfig);
        conf.put("spark.executor.extraJavaOptions", log4jConfig);
        return conf;
    }

    private String getLog4jConfig() {
        try {
            return "-Dlog4j.configuration=" + ClassLoader.getSystemResource("log4j.properties").toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to find log4j.properties", e);
        }
    }
}
