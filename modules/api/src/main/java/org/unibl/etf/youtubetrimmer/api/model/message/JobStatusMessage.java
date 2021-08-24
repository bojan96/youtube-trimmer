package org.unibl.etf.youtubetrimmer.api.model.message;

import lombok.Data;

@Data
public class JobStatusMessage {
    private int id;
    private String newStatus;
}
