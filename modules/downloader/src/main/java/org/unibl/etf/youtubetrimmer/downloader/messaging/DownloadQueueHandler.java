package org.unibl.etf.youtubetrimmer.downloader.messaging;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.messaging.Queues;
import org.unibl.etf.youtubetrimmer.common.messaging.model.DownloadMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.EventMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.downloader.properties.DownloaderProperties;
import org.unibl.etf.youtubetrimmer.downloader.service.DownloadService;
import org.unibl.etf.youtubetrimmer.downloader.service.MessagingService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = Queues.DOWNLOAD, containerFactory = "containerFactory")
@Log4j2
public class DownloadQueueHandler {

    private final JobRepository jobRepo;
    private final DownloadService downloadService;
    private final MessagingService messagingService;
    private final DownloaderProperties props;

    @RabbitHandler
    @SneakyThrows
    public void handleMessage(DownloadMessage message) {

        log.info("Processing job(id = {}, url = {})", message.getJobId(), message.getVideoUrl());
        Optional<JobEntity> job = jobRepo.findById(message.getJobId());
        if (job.isEmpty())
            return;

        JobEntity jobEntity = job.get();
        if (jobEntity.getStatus() == JobStatus.CANCELED) {
            log.info("Job(id = {}) is canceled", message.getJobId());
            return;
        }

        markJobAsDownloading(jobEntity);
        Optional<Path> video = downloadService.download(message.getVideoUrl());
        if (video.isEmpty()) {
            log.info("Job(id = {}) is canceled", message.getJobId());
            return;
        }

        Path videoPath = video.get();
        Path targetPath = Path.of(props.getVideoDirectory()).resolve(videoPath.getFileName());
        Files.copy(videoPath, targetPath);

        jobEntity.getVideo().setVideoReference(targetPath.toString());
        markJobAsWaitingTrim(jobEntity);
        messagingService.sendMessageToTrimQueue(TrimMessage.builder().jobId(message.getJobId()).build());
        log.info("Video for job(id = {}) downloaded successfully(path = {})",
                message.getJobId(), videoPath.toString());
    }

    private void markJobAsDownloading(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.DOWNLOADING);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToEventsQueue(EventMessage.builder().build());
    }

    private void markJobAsWaitingTrim(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.WAITING_TRIM);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToEventsQueue(EventMessage.builder().build());
    }


}
