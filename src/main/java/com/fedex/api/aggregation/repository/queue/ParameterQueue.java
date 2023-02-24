package com.fedex.api.aggregation.repository.queue;

import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Component
public class ParameterQueue {

    protected final int capacity;
    protected final int timeout;
    protected final BlockingQueue<String> queue;

    public ParameterQueue(@Value("${parameter.queue.capacity:5}") int capacity, @Value("${parameter.queue.timeout:5}") int timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
        queue = Queues.newLinkedBlockingDeque(capacity);
    }

    public void enqueue(String parameter) {
        if (StringUtils.isNotBlank(parameter)) {
            boolean isAdded = queue.offer(parameter);
            log.info("Adding parameter: {} ({})", parameter, isAdded);
        }
    }

    public List<String> getParameters() {
        List<String> parameters = new ArrayList<>();

        if (!queue.isEmpty()) {
            Queues.drainUninterruptibly(queue, parameters, capacity, timeout, SECONDS);
        }
        return parameters;
    }

}
