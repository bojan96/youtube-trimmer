package org.unibl.etf.youtubetrimmer.api.model;


import lombok.Data;

@Data
public class JobDetails {
    private int id;
    private int trimFrom;
    private int trimTo;
    private String videoUrl;
    private String downloadUrl;
}
