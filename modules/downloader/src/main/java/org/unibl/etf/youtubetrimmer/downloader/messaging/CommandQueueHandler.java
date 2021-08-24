package org.unibl.etf.youtubetrimmer.downloader.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.common.messaging.model.Command;
import org.unibl.etf.youtubetrimmer.common.messaging.model.CommandMessage;
import org.unibl.etf.youtubetrimmer.downloader.service.DownloadService;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = "#{commandQueue.name}")
@Log4j2
public class CommandQueueHandler {

    private final DownloadService downloadService;
    @RabbitHandler
    public void handleMessage(CommandMessage commandMessage) {

        if(commandMessage.getCommand() == Command.CANCEL)
            downloadService.cancelDownload((Integer)commandMessage.getParameters().get(0));
    }
}
