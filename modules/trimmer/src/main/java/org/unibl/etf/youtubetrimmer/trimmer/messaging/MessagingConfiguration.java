package org.unibl.etf.youtubetrimmer.trimmer.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unibl.etf.youtubetrimmer.common.messaging.Exchanges;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;

@Configuration
public class MessagingConfiguration {

    @Bean
    public Queue trimQueue() {
        return new Queue(Queues.TRIM);
    }

    @Bean
    public FanoutExchange commandExchange() {
        return new FanoutExchange(Exchanges.TRIMMER_COMMAND);
    }

    @Bean
    public Queue commandQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding(FanoutExchange commandExchange, Queue commandQueue) {
        return BindingBuilder.bind(commandQueue).to(commandExchange);
    }

    @Bean("containerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(ConnectionFactory connFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connFactory);
        factory.setPrefetchCount(1);
        return factory;
    }
}
