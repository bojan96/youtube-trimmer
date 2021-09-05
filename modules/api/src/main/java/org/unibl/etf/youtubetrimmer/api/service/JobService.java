package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.exception.IllegalOperationException;
import org.unibl.etf.youtubetrimmer.api.exception.NotFoundException;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
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

    public JobDetails createJob(Job job) {

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

    public Optional<JobDetails> getJob(int jobId) {
        return jobRepo.findById(jobId)
                .map(j -> {
                    JobDetails jobDetails = mapper.map(j, JobDetails.class);
                    return jobDetails;
                });
    }

    public List<JobDetails> getJobs(int userId) {
        List<JobEntity> jobs = jobRepo.findByUserIdAndStatusNotOrderByDateDesc(userId, JobStatus.CANCELED);
        List<JobDetails> jobDetails = mapEntityToDetails(jobs);
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

    private Example<JobEntity> userJobsExample(int userId) {
        JobEntity job = new JobEntity();
        UserEntity u = new UserEntity();
        u.setId(userId);
        job.setUser(u);
        return Example.of(job);
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(timeService.now(), ZoneOffset.UTC);
    }

    private String getVideoId(String youtubeUrl) {
        return new YoutubeURLParser(youtubeUrl).getVideoId();
    }

}
