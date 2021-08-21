package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.request.JobRequest;
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
    public int createJob(@Valid @RequestBody JobRequest jobRequest, JwtAuthenticationToken user) {
        Job job = mapper.map(jobRequest, Job.class);
        job.setUserId(user.getId());
        return jobService.createJob(job);
    }
}
