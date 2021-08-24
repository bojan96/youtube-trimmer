package org.unibl.etf.youtubetrimmer.common.messaging.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class CommandMessage implements Serializable {
    private static final long serialVersionUID = 1;
    private Command command;
    @Singular
    private List<Object> parameters;

}
