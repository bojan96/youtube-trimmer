package org.unibl.etf.youtubetrimmer.api.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfiguration {

    @Bean(Queues.DOWNLOAD)
    public Queue downloadQueue() {
        return new Queue(Queues.DOWNLOAD);
    }

    @Bean(Queues.TRIM)
    public Queue trimQueue(){
        return new Queue(Queues.TRIM);
    }
}
