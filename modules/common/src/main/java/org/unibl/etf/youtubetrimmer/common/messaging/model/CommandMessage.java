package org.unibl.etf.youtubetrimmer.common.messaging.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CommandMessage implements Serializable {
    private static final long serialVersionUID = 1;
    private Command command;
}
