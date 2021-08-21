package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.util.YoutubeURLParser;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.entity.VideoEntity;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.common.repository.UserRepository;
import org.unibl.etf.youtubetrimmer.common.repository.VideoRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final VideoRepository videoRepo;
    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final MessagingService messagingService;
    private final TimeService timeService;

    public int createJob(Job job) {

        String videoId = getVideoId(job.getVideoUrl());
        Optional<VideoEntity> video = videoRepo.findOne(Example.of(VideoEntity.builder()
                .videoUid(videoId).build()));
        UserEntity userEntity = userRepo.findById(job.getUserId()).get();
        JobEntity jobEntity = JobEntity.builder()
                .trimFrom(job.getTrimFrom())
                .trimTo(job.getTrimTo())
                .user(userEntity)
                .date(now())
                .build();

        video.ifPresentOrElse(v -> {
            jobEntity.setStatus(JobStatus.WAITING_TRIM);
            jobEntity.setVideo(v);
        }, () -> {
            jobEntity.setStatus(JobStatus.WAITING_DOWNLOAD);
            VideoEntity videoEntity = VideoEntity.builder().videoUid(videoId).build();
            jobEntity.setVideo(videoEntity);
        });
        JobEntity savedEntity = jobRepo.save(jobEntity);
        video.ifPresentOrElse(v -> messagingService.sendJobToTrimQueue(savedEntity.getId()),
                () -> messagingService.sendJobToDownloadQueue(savedEntity.getId()));

        return savedEntity.getId();
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(timeService.now(), ZoneOffset.UTC);
    }

    private String getVideoId(String youtubeUrl) {
        return new YoutubeURLParser(youtubeUrl).getVideoId();
    }
}