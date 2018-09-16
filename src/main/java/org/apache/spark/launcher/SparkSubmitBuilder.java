package org.apache.spark.launcher;

import java.util.HashMap;

class SparkSubmitBuilder {
    private final static String pi = "\u03C0";

    String getCmd() {
        SparkLauncher sparkLauncher = buildLauncher();

        return String.join(" ", sparkLauncher.builder.buildSparkSubmitArgs());
    }

    SparkLauncher buildLauncher() {
        final SparkLauncher sparkLauncher = new SparkLauncher();

        getEnv().forEach(sparkLauncher::setConf);

        sparkLauncher.setDeployMode("client");
        final String home = "/home/srini";


        sparkLauncher.setAppName("Spark " + pi);
        sparkLauncher.setAppResource(home + "/.sdkman/candidates/spark/current/examples/jars/spark-examples_2.11-2.3.1.jar");
        sparkLauncher.setSparkHome(home + "/.sdkman/candidates/spark/current");
        sparkLauncher.setMainClass("org.apache.spark.examples.SparkPi");
        sparkLauncher.setMaster("spark://pop-os:7077");
        sparkLauncher.addAppArgs("100");
        return sparkLauncher;
    }

    private HashMap<String, String> getEnv() {
        HashMap<String, String> conf = new HashMap<>();
        conf.put("spark.executor.cores", "1");
        conf.put("spark.executor.memory", "1G");
        conf.put("spark.driver.cores", "1");
        conf.put("spark.driver.memory", "512M");
        conf.put("spark.cores.max", "2");
        return conf;
    }
}
