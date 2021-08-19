package org.unibl.etf.youtubetrimmer.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public String createNewUser() {
        String uid = generateUid();
        UserEntity user = new UserEntity();
        user.setUid(uid);
        userRepo.save(user);
        return uid;
    }

    private String generateUid() {
        return UUID.randomUUID().toString();
    }
}
