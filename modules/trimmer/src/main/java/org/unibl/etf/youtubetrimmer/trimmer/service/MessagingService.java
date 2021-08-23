package org.unibl.etf.youtubetrimmer.trimmer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.messaging.model.EventMessage;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessageToEventsQueue(EventMessage eventMessage) {
    }
}
