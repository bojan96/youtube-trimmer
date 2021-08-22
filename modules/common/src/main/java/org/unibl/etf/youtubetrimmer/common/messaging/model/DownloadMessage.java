package org.unibl.etf.youtubetrimmer.common.messaging.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadMessage {
    private int jobId;
    private String videoUrl;
}
