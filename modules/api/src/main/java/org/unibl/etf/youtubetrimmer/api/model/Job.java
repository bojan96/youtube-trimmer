package org.unibl.etf.youtubetrimmer.api.model;

import lombok.Data;

@Data
public class Job {
    private int userId;
    private int trimFrom;
    private int trimTo;
    private String videoUrl;
}
