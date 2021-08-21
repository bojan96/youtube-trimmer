package org.unibl.etf.youtubetrimmer.api.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobDetails {
    private int id;
    private int trimFrom;
    private int trimTo;
    private String videoUrl;
    private String downloadUrl;
    private String status;
    private LocalDateTime date;
}
