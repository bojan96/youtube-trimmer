package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.exception.*;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.api.util.URLUtils;
import org.unibl.etf.youtubetrimmer.api.util.YoutubeURLParser;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.entity.VideoEntity;
import org.unibl.etf.youtubetrimmer.common.messaging.model.Command;
import org.unibl.etf.youtubetrimmer.common.messaging.model.CommandMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.DownloadMessage;
import org.unibl.etf.youtubetrimmer.common.messaging.model.TrimMessage;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.common.repository.UserRepository;
import org.unibl.etf.youtubetrimmer.common.repository.VideoRepository;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final VideoRepository videoRepo;
    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final MessagingService messagingService;
    private final TimeService timeService;
    private final ModelMapper mapper;
    private final VideoStorageService storageService;

    public JobDetails createJob(Job job) {

        if(!URLUtils.isValidYoutubeUrl(job.getVideoUrl()))
            throw new URLNotSupportedException(job.getVideoUrl());

        if(job.getTrimFrom() >= job.getTrimTo())
            throw new TrimRangeInvalidException();

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

        VideoEntity videoEntity = video.orElseGet(() -> VideoEntity.builder().videoUid(videoId).build());
        jobEntity.setVideo(videoEntity);
        boolean skipDownload = videoEntity.getVideoReference() != null;
        jobEntity.setStatus(skipDownload ? JobStatus.WAITING_TRIM : JobStatus.WAITING_DOWNLOAD);
        JobEntity savedEntity = jobRepo.save(jobEntity);
        JobDetails details = mapper.map(savedEntity, JobDetails.class);

        if (skipDownload) {
            messagingService.sendJobToTrimQueue(TrimMessage.builder()
                    .jobId(details.getId())
                    .build());
        } else {
            messagingService.sendJobToDownloadQueue(
                    DownloadMessage
                            .builder()
                            .jobId(details.getId())
                            .videoUrl(details.getVideoUrl())
                            .build());
        }

        return details;
    }

    public Optional<JobDetails> getJob(int jobId, int userId) {
        Optional<JobEntity> job = jobRepo.findById(jobId);
        if(job.isEmpty())
            return Optional.empty();

        JobEntity jobEntity = job.get();
        if(jobEntity.getUser().getId() != userId)
            throw new ForbiddenAccessException();
        JobDetails jobDetails = mapper.map(jobEntity, JobDetails.class);
        if(jobEntity.getStatus() == JobStatus.COMPLETE)
            jobDetails.setDownloadUrl(storageService.getVideoDownloadUrl(jobEntity.getTrimmedVideoReference()));

        return Optional.of(jobDetails);
    }

    public List<JobDetails> getJobs(int userId) {
        List<JobEntity> jobs = jobRepo.findByUserIdAndStatusNotOrderByDateDesc(userId, JobStatus.CANCELED);
        List<JobDetails> jobDetails = mapEntityToDetails(jobs);

        for(int i = 0; i < jobs.size(); ++i)
        {
            JobEntity jobEntity = jobs.get(i);

            if(jobEntity.getStatus() == JobStatus.COMPLETE)
            {
                jobDetails
                        .get(i)
                        .setDownloadUrl(storageService.getVideoDownloadUrl(jobEntity.getTrimmedVideoReference()));

            }
        }


        return jobDetails;
    }

    public void cancelJob(int jobId, int userId) {
        Optional<JobEntity> job = jobRepo.findById(jobId);
        JobEntity jobEntity = job.orElseThrow(NotFoundException::new);
        JobStatus jobStatus = jobEntity.getStatus();

        if (jobEntity.getUser().getId() != userId)
            throw new IllegalOperationException();
        if (jobStatus == JobStatus.COMPLETE)
            throw new IllegalOperationException();
        if (jobStatus == JobStatus.CANCELED)
            return;

        jobEntity.setStatus(JobStatus.CANCELED);
        jobRepo.save(jobEntity);

        CommandMessage cancellationMessage = CommandMessage
                .builder()
                .command(Command.CANCEL)
                .parameter(jobId)
                .build();

        if(jobStatus == JobStatus.WAITING_DOWNLOAD
                || jobStatus == JobStatus.DOWNLOADING)
            messagingService.sendCommandToDownloaders(cancellationMessage);
        else
            messagingService.sendCommandToTrimmers(cancellationMessage);
    }


    private List<JobDetails> mapEntityToDetails(List<JobEntity> jobs) {
        Type listType = new TypeToken<List<JobDetails>>() {
        }.getType();
        return mapper.map(jobs, listType);
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(timeService.now(), ZoneOffset.UTC);
    }

    private String getVideoId(String youtubeUrl) {
        return new YoutubeURLParser(youtubeUrl).getVideoId();
    }

}
