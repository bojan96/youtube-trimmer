package org.unibl.etf.youtubetrimmer.api.messaging;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unibl.etf.youtubetrimmer.common.messaging.Exchanges;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;

@Configuration
public class MessagingConfiguration {

    @Bean
    public Queue downloadQueue() {
        return new Queue(Queues.DOWNLOAD);
    }

    @Bean
    public Queue trimQueue() {
        return new Queue(Queues.TRIM);
    }

    @Bean
    public FanoutExchange commandExchange() {
        return new FanoutExchange(Exchanges.DOWNLOADER_COMMAND);
    }

    @Bean
    public FanoutExchange trimmerExchange() {
        return new FanoutExchange(Exchanges.TRIMMER_COMMAND);
    }

}
