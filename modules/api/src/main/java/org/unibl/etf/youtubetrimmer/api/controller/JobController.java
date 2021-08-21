package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.api.model.request.JobRequest;
import org.unibl.etf.youtubetrimmer.api.model.response.JobResponse;
import org.unibl.etf.youtubetrimmer.api.security.JwtAuthenticationToken;
import org.unibl.etf.youtubetrimmer.api.service.JobService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ModelMapper mapper;

    @PostMapping
    public JobResponse createJob(@Valid @RequestBody JobRequest jobRequest, JwtAuthenticationToken user) {
        Job job = mapper.map(jobRequest, Job.class);
        job.setUserId(user.getId());
        JobDetails jobDetails = jobService.createJob(job);
        return mapper.map(jobDetails, JobResponse.class);
    }
}
