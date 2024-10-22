package com.milesight.iab.eventbus.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author leon
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "eventbus.disruptor")
public class DisruptorOptions {

    private boolean enabled = true;
    /**
     * The size of the ring buffer, must be power of 2.
     */
    private int ringBufferSize = 4096;

    private int maxPoolSize = Integer.MAX_VALUE;

    private String eventBusTaskExecutor = "applicationTaskExecutor";

    public static DisruptorOptions defaultOptions() {
        return new DisruptorOptions();
    }
}
