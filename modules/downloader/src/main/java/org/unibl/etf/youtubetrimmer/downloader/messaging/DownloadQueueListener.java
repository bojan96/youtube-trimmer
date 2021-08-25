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
import org.unibl.etf.youtubetrimmer.common.messaging.model.JobEventMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.common.util.FileUtils;
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
public class DownloadQueueListener {

    private final JobRepository jobRepo;
    private final DownloadService downloadService;
    private final MessagingService messagingService;
    private final DownloaderProperties props;

    @RabbitHandler
    @SneakyThrows
    public void handleMessage(DownloadMessage message) {

        Optional<JobEntity> job = jobRepo.findById(message.getJobId());
        if (job.isEmpty())
            return;
        log.info("Processing job(id = {}, url = {})", message.getJobId(), message.getVideoUrl());

        JobEntity jobEntity = job.get();
        if (jobEntity.getStatus() == JobStatus.CANCELED) {
            log.info("Job(id = {}) is canceled", message.getJobId());
            return;
        }

        if (jobEntity.getVideo().getVideoReference() == null) {

            markJobAsDownloading(jobEntity);
            Optional<Path> video = downloadService.download(message.getVideoUrl(), message.getJobId());
            if (video.isEmpty()) {
                log.info("Job(id = {}) is canceled", message.getJobId());
                return;
            }

            Path videoPath = video.get();
            String videoFilename = jobEntity.getVideo().getId() + "." + FileUtils.getExtension(videoPath.getFileName().toString());
            Path targetPath = Path.of(props.getVideoDirectory()).resolve(videoFilename);
            Files.copy(videoPath, targetPath);
            jobEntity.getVideo().setVideoReference(targetPath.toString());
        } else {
            log.info("Job(id = {}) - Video already downloaded, skipping download", message.getJobId());
        }
        markJobAsWaitingTrim(jobEntity);
        messagingService.sendMessageToTrimQueue(TrimMessage.builder().jobId(message.getJobId()).build());
        log.info("Video for job(id = {}) downloaded successfully",
                message.getJobId());
    }

    private void markJobAsDownloading(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.DOWNLOADING);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToJobEventsQueue(JobEventMessage
                .builder()
                .id(jobEntity.getId())
                .username(jobEntity.getUser().getUsername())
                .newStatus(JobStatus.DOWNLOADING)
                .build());
    }

    private void markJobAsWaitingTrim(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.WAITING_TRIM);
        jobRepo.save(jobEntity);
        messagingService.sendMessageToJobEventsQueue(JobEventMessage
                .builder()
                .id(jobEntity.getId())
                .username(jobEntity.getUser().getUsername())
                .newStatus(JobStatus.WAITING_TRIM)
                .build());
    }


}
