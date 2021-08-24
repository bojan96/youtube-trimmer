package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.youtubetrimmer.api.exception.IllegalOperationException;
import org.unibl.etf.youtubetrimmer.api.exception.NotFoundException;
import org.unibl.etf.youtubetrimmer.api.model.Job;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.api.model.request.JobRequest;
import org.unibl.etf.youtubetrimmer.api.model.response.JobResponse;
import org.unibl.etf.youtubetrimmer.api.security.JwtAuthenticationToken;
import org.unibl.etf.youtubetrimmer.api.service.JobService;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable int id) {
        return ResponseEntity.of(jobService
                .getJob(id)
                .map(j -> mapper.map(j, JobResponse.class)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity cancelJob(@PathVariable int id, JwtAuthenticationToken user) {
        try {
            jobService.cancelJob(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalOperationException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping
    public List<JobResponse> getJobs(JwtAuthenticationToken user) {
        return mapDetailsToResponse(jobService.getJobs(user.getId()));
    }

    private List<JobResponse> mapDetailsToResponse(List<JobDetails> jobDetails) {
        Type listType = new TypeToken<List<JobResponse>>() {
        }.getType();
        return mapper.map(jobDetails, listType);
    }

}
