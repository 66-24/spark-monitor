package org.apache.spark.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class SparkLauncherWrapper {

    private static final Logger log = LoggerFactory.getLogger(SparkLauncherWrapper.class);

    private static final CountDownLatch appExitLatch = new CountDownLatch(1);
    private File devNull = new File("/dev/null");

    public static void main(String[] args) throws Exception {
        //This gets rid of the annoying header for every line "Nov 20, 2018 11:21:42 AM org.apache.spark.launcher.OutputRedirector redirect"
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");

        SparkLauncherWrapper sparkLauncherWrapper = new SparkLauncherWrapper();
        sparkLauncherWrapper.launch();
        appExitLatch.await();

    }


    private java.util.logging.Logger createLogger(String appName) throws IOException {
        final java.util.logging.Logger logger = getRootLogger();
        final FileHandler handler = new FileHandler("./" + appName + "-%u-%g.log", 10_000_000, 5, true);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
        return logger;
    }

    private void launch() throws IOException {

        SparkSubmitBuilder builder = new SparkSubmitBuilder();

        SparkLauncher sparkLauncher = builder.buildLauncher();

        sparkLauncher.setVerbose(false);
        sparkLauncher.redirectToLog(createLogger(sparkLauncher.builder.appName).getName());

//        sparkLauncher.redirectError(devNull);

//        sparkLauncher.redirectError();

//        sparkLauncher.redirectError(ProcessBuilder.Redirect.INHERIT);
//        sparkLauncher.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        sparkLauncher.startApplication(new SparkAppListener(sparkLauncher.builder.appName));


    }

    private java.util.logging.Logger getRootLogger() {
        final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        Arrays.stream(logger.getHandlers()).forEach(logger::removeHandler);
        //Without this the logging will go to the Console and to a file.
        logger.setUseParentHandlers(false);
        return logger;
    }

    private class SparkAppListener implements SparkAppHandle.Listener {
        private final String appName;

        private SparkAppListener(String appName) {
            this.appName = appName;
        }

        @Override
        public void stateChanged(SparkAppHandle handle) {

            log.info("State changed: {}, {}, {}",
                    appName,
                    handle.getAppId(),
                    handle.getState());

            if (handle.getState().isFinal()) {
                appExitLatch.countDown();
            }
        }

        @Override
        public void infoChanged(SparkAppHandle handle) {
            log.info("Info changed: {}, {}, {}",
                    appName,
                    handle.getAppId(),
                    handle.getState());
        }
    }

}
