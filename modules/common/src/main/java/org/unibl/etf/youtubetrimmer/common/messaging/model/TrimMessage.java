package org.unibl.etf.youtubetrimmer.common.messaging.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TrimMessage implements Serializable {
    private static final long serialVersionUID = 1;
    private int jobId;
}
