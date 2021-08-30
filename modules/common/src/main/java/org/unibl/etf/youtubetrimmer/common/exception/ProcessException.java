package org.unibl.etf.youtubetrimmer.common.exception;

public class ProcessException extends RuntimeException {
    public ProcessException(int statusCode, String command)
    {
        super(String.format("Download command \"%s\" failed with status code: %d", command, statusCode));
    }
}
