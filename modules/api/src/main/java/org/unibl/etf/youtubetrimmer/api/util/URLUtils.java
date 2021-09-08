package org.unibl.etf.youtubetrimmer.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;

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

    public static boolean isValidYoutubeUrl(String url)
    {
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
        String host = uri.getHost();
        String path = uri.getPath();

        return host.matches("^(www.)?youtube\\.com$") && path.equals("/" + WATCH_PATH)
                && uri.getQueryParams().containsKey(VIDEO_PARAM);
    }
}
