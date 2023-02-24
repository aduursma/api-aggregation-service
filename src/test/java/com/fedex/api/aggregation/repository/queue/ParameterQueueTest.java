package com.fedex.api.aggregation.repository.queue;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
class ParameterQueueTest {

    private ParameterQueue parameterQueue;

    @BeforeEach
    public void setUp() {
        parameterQueue = new ParameterQueue(5, 5);
    }

    @Test
    void verifyEnqueuingCountriesParameter() {
        parameterQueue.enqueue("NL");
        parameterQueue.enqueue("CN");

        assertThat(parameterQueue.queue.stream().count()).isEqualTo(2);
    }

    @Test
    void verifyEnqueuingNullParameterIsNotEnqueued() {
        parameterQueue.enqueue(null);

        assertThat(parameterQueue.queue.stream().count()).isEqualTo(0);
    }

    @Test
    void verifyEnqueuingEmptyParameterIsNotEnqueued() {
        parameterQueue.enqueue("");

        assertThat(parameterQueue.queue.stream().count()).isEqualTo(0);
    }

    @Test
    void verifyNoParametersWhenGettingParametersWithoutEnqueuingFirst() {
        List<String> parameters = parameterQueue.getParameters();

        Assertions.assertThat(parameters)
            .isEmpty();
    }

    @Test
    void verifyParametersWhenGettingParametersAfterEnqueuing() {
        doVerifyParameters();
    }

    @Test
    void verifyParametersWhenGettingParametersAfterEnqueuingAndAfterUpdatingTimeoutInSeconds() {
        parameterQueue = new ParameterQueue(5, 1);
        doVerifyParameters();
    }

    void doVerifyParameters() {
        parameterQueue.enqueue("NL");
        parameterQueue.enqueue("CN");

        StopWatch watch = new StopWatch();
        watch.start();

        List<String> parameters = parameterQueue.getParameters();

        watch.stop();

        log.info("Elapsed time getting parameters in millis: {}", watch.getTime());

        assertThat(watch.getTime(SECONDS)).isGreaterThanOrEqualTo(parameterQueue.timeout);

        Assertions.assertThat(parameters)
            .isNotEmpty()
            .hasSize(2)
            .contains("NL", "CN");
    }

}
