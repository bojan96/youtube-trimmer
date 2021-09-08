package org.unibl.etf.youtubetrimmer.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.unibl.etf.youtubetrimmer.api.exception.TrimRangeInvalidException;
import org.unibl.etf.youtubetrimmer.api.exception.URLNotSupportedException;

@RestControllerAdvice
public class ErrorHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public String invalidTrimRange(TrimRangeInvalidException ex)
    {
        return "Invalid trim range";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidJobUrl(URLNotSupportedException ex)
    {
        return String.format("%s is not valid url", ex.getUrl());
    }
}
