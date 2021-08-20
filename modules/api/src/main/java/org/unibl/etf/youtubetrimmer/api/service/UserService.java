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

    public int createNewUser() {
        UserEntity user = new UserEntity();
        UserEntity savedEntity = userRepo.save(user);
        return savedEntity.getId();
    }
}
