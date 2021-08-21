package org.unibl.etf.youtubetrimmer.api.amqp;

public final class Queues {
    private Queues() {

    }

    public static final String DOWNLOAD = "download_queue";
    public static final String TRIM = "trim_queue";
}
