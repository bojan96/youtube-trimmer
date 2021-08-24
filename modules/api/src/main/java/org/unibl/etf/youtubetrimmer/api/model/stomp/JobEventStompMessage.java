package org.unibl.etf.youtubetrimmer.api.model.stomp;

import lombok.Data;

@Data
public class JobEventStompMessage {
    private int id;
    private String newStatus;
}
