package org.unibl.etf.youtubetrimmer.trimmer.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.common.messaging.model.Command;
import org.unibl.etf.youtubetrimmer.common.messaging.model.CommandMessage;
import org.unibl.etf.youtubetrimmer.trimmer.service.TrimmingService;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = "#{commandQueue.name}")
public class CommandQueueListener {

    private final TrimmingService trimmingService;

    @RabbitHandler
    public void handleMessage(CommandMessage message) {
        if(message.getCommand() == Command.CANCEL)
            trimmingService.cancelJob((Integer)message.getParameters().get(0));
    }
}
