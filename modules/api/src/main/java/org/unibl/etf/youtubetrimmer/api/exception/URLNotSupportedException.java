package org.unibl.etf.youtubetrimmer.api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class URLNotSupportedException extends RuntimeException {
    private final String url;
}
