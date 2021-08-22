package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.amqp.Queues;
import org.unibl.etf.youtubetrimmer.common.message.DownloadMessage;
import org.unibl.etf.youtubetrimmer.common.message.TrimMessage;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final RabbitTemplate rabbitTemplate;

    public void sendJobToDownloadQueue(DownloadMessage downloadMessage) {
        rabbitTemplate.convertAndSend(Queues.DOWNLOAD, downloadMessage);
    }

    public void sendJobToTrimQueue(TrimMessage trimMessage) {
        rabbitTemplate.convertAndSend(Queues.TRIM, trimMessage);
    }
}
