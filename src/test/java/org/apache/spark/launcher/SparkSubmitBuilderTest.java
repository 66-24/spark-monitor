package org.apache.spark.launcher;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SparkSubmitBuilderTest {

    @Test
    void given_valid_config_should_return_valid_spark_submit_cmd() {
        SparkSubmitBuilder builder = new SparkSubmitBuilder();
        Assertions.assertThat(builder.getCmd()).isBlank();
    }
}