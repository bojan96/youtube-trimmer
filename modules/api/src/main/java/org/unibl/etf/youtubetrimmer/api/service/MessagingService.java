package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.messaging.Exchanges;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.CommandMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.DownloadMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;

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

    public void sendCommandToDownloaders(CommandMessage commandMessage) {
        rabbitTemplate.convertAndSend(Exchanges.DOWNLOADER_COMMAND, "", commandMessage);
    }

    public void sendCommandToTrimmers(CommandMessage commandMessage) {
        rabbitTemplate.convertAndSend(Exchanges.TRIMMER_COMMAND, "", commandMessage);
    }
}
