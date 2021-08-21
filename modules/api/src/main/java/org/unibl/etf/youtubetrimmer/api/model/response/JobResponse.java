package org.unibl.etf.youtubetrimmer.api.model.response;

import lombok.Data;

@Data
public class JobResponse {
    private int id;
    private int trimFrom;
    private int trimTo;
    private String videoUrl;
    private String downloadUrl;
}
