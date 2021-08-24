package org.unibl.etf.youtubetrimmer.common.messaging.model;

import lombok.Builder;
import lombok.Getter;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;

import java.io.Serializable;

@Getter
@Builder
public class JobEventMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private int id;
    private String username;
    private JobStatus newStatus;

}
