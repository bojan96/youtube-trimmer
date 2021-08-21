package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.amqp.Queues;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final RabbitTemplate rabbitTemplate;

    public void sendJobToDownloadQueue(int jobId) {
        rabbitTemplate.convertAndSend(Queues.DOWNLOAD, jobId);
    }

    public void sendJobToTrimQueue(int jobId) {
        rabbitTemplate.convertAndSend(Queues.TRIM, jobId);
    }
}
