package org.apache.spark.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SparkLauncherWrapper {
    private static final Logger log = LoggerFactory.getLogger(SparkLauncherWrapper.class);

    private static final CountDownLatch appExitLatch = new CountDownLatch(1);
    private File devNull = new File("/dev/null");

    public static void main(String[] args) throws Exception {
        SparkLauncherWrapper sparkLauncherWrapper = new SparkLauncherWrapper();
        sparkLauncherWrapper.launch();
        appExitLatch.await();

    }

    private void launch() throws IOException {
        SparkSubmitBuilder builder = new SparkSubmitBuilder();

        SparkLauncher sparkLauncher = builder.buildLauncher();

        sparkLauncher.setVerbose(true);
        sparkLauncher.redirectError(devNull);
        sparkLauncher.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        sparkLauncher.startApplication(new SparkAppListener(sparkLauncher.builder.appName));


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
