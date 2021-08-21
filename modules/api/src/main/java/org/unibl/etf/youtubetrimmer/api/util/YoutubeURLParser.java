package org.unibl.etf.youtubetrimmer.api.util;

import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

public class YoutubeURLParser {

    private static final String VIDEO_PARAM = "v";
    @Getter
    private final String videoId;


    public YoutubeURLParser(String youtubeUrl) {
        videoId = UriComponentsBuilder
                .fromHttpUrl(youtubeUrl).build()
                .getQueryParams()
                .getFirst(VIDEO_PARAM);
    }



}
