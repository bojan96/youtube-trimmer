package org.unibl.etf.youtubetrimmer.common.messaging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Exchanges {
    public static final String DOWNLOADER_COMMAND = "downloader_command_exchange";
    public static final String TRIMMER_COMMAND = "trimmer_command_exchange";
}
