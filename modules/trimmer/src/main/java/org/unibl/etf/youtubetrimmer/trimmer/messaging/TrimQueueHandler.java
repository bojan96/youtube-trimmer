package org.unibl.etf.youtubetrimmer.trimmer.messaging;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.EventMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.trimmer.properties.TrimmerProperties;
import org.unibl.etf.youtubetrimmer.trimmer.service.MessagingService;
import org.unibl.etf.youtubetrimmer.trimmer.service.TrimmingService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = Queues.TRIM, containerFactory = "containerFactory")
@Log4j2
public class TrimQueueHandler {

    private final JobRepository jobRepo;
    private final MessagingService messagingService;
    private final TrimmingService trimmingService;
    private final TrimmerProperties props;

    @RabbitHandler
    @SneakyThrows
    public void handleMessage(TrimMessage message) {

        log.info("Processing job(id = {})", message.getJobId());
        Optional<JobEntity> job = jobRepo.findById(message.getJobId());
        if (job.isEmpty())
            return;

        JobEntity jobEntity = job.get();
        if (jobEntity.getStatus() == JobStatus.CANCELED) {
            log.info("Job(id = {}) is canceled", message.getJobId());
            return;
        }

        markJobAsTrimming(jobEntity);
        Optional<Path> video = trimmingService.trim(jobEntity.getVideo().getVideoReference(),
                jobEntity.getTrimFrom(), jobEntity.getTrimTo());

        if (video.isEmpty()) {
            log.info("Job(id = {}) is canceled", message.getJobId());
            return;
        }

        Path videoPath = video.get();
        Path targetPath = Path.of(props.getOutputDirectory()).resolve(videoPath.getFileName());
        Files.copy(videoPath, targetPath);

        jobEntity.setTrimmedVideoReference(targetPath.toString());
        markJobAsCompleted(jobEntity);
        log.info("Job(id = {}) processed successfully", message.getJobId());

    }

    private void markJobAsTrimming(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.TRIMMING);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToEventsQueue(EventMessage.builder().build());
    }

    private void markJobAsCompleted(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.COMPLETE);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToEventsQueue(EventMessage.builder().build());
    }

}
