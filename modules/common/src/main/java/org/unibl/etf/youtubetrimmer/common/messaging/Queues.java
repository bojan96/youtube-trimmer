package org.unibl.etf.youtubetrimmer.common.messaging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Queues {
    public static final String DOWNLOAD = "download_queue";
    public static final String TRIM = "trim_queue";
    public static final String JOB_EVENT = "job_event_queue";
}
