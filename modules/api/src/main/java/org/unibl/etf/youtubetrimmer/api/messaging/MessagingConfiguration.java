package org.unibl.etf.youtubetrimmer.api.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

    @Bean(Queues.DOWNLOAD)
    public Queue downloadQueue() {
        return new Queue(Queues.DOWNLOAD);
    }

    @Bean(Queues.TRIM)
    public Queue trimQueue() {
        return new Queue(Queues.TRIM);
    }
}
