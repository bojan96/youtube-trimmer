package org.unibl.etf.youtubetrimmer.downloader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.JobEventMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessageToTrimQueue(TrimMessage message) {
        rabbitTemplate.convertAndSend(Queues.TRIM, message);
    }

    public void sendMessageToJobEventsQueue(JobEventMessage message) {
        rabbitTemplate.convertAndSend(Queues.JOB_EVENT, message);
    }


}
