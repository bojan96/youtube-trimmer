package org.unibl.etf.youtubetrimmer.trimmer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.JobEventMessage;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessageToJobEventsQueue(JobEventMessage jobEventMessage) {
        rabbitTemplate.convertAndSend(Queues.JOB_EVENT, jobEventMessage);
    }
}
