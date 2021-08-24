package org.unibl.etf.youtubetrimmer.api.messaging;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.api.model.stomp.JobEventStompMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.JobEventMessage;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = Queues.JOB_EVENT)
public class JobEventsQueueListener {

    private final SimpMessagingTemplate stompSender;
    private final ModelMapper mapper;
    private static final String JOB_STATUS_PATH = "/queue/job-events";

    @RabbitHandler
    public void handleMessage(JobEventMessage message) {
        JobEventStompMessage statusMessage = mapper.map(message, JobEventStompMessage.class);
        stompSender.convertAndSendToUser(message.getUsername(), JOB_STATUS_PATH, statusMessage);
    }
}
