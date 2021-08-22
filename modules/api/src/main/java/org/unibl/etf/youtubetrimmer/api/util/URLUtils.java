package org.unibl.etf.youtubetrimmer.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URLUtils {
    private static final String YOUTUBE_URL = "https://youtube.com";
    private static final String VIDEO_PARAM = "v";
    private static final String WATCH_PATH = "watch";

    public static String getYoutubeUrl(String videoId) {
        return UriComponentsBuilder.fromHttpUrl(YOUTUBE_URL)
                .path(WATCH_PATH)
                .queryParam(VIDEO_PARAM, videoId)
                .build()
                .toString();
    }
}
