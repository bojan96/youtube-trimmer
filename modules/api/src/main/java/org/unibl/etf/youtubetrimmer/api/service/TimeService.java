package org.unibl.etf.youtubetrimmer.api.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeService {
    public Instant now() {
        return Instant.now();
    }
}
