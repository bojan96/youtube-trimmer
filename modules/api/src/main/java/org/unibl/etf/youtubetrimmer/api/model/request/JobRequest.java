package org.unibl.etf.youtubetrimmer.api.model.request;


import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class JobRequest {
    @Min(0)
    private int trimFrom;
    @Min(0)
    private int trimTo;
    @NotBlank
    @URL
    private String videoUrl;
}
