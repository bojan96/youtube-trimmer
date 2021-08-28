package org.unibl.etf.youtubetrimmer.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.repository.JobRepository;
import org.unibl.etf.youtubetrimmer.common.repository.UserRepository;
import org.unibl.etf.youtubetrimmer.common.repository.VideoRepository;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final VideoRepository videoRepo;

    @GetMapping
    public String test() {
        UserEntity e = new UserEntity();
        e.setUsername("username");
        e.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("password"));
        userRepo.save(e);
        return e.getUsername();
    }
}
