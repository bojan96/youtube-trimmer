package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.api.util.YoutubeURLParser;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.entity.VideoEntity;
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

        JobDetails details = mapper.map(savedEntity, JobDetails.class);

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
        List<JobEntity> jobs = jobRepo.findAll(userJobsExample(userId),
                Sort.by(Sort.Order.desc("date")));
        List<JobDetails> jobDetails = mapEntityToDetails(jobs);
        return jobDetails;
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
