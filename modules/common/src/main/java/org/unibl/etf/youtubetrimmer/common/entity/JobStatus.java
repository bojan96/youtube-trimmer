package org.unibl.etf.youtubetrimmer.common.entity;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum JobStatus {
    WAITING_DOWNLOAD("waiting_download"),
    DOWNLOADING("downloading"),
    WAITING_TRIM("waiting_trim"),
    TRIMMING("trimming"),
    COMPLETE("complete"),
    CANCELED("canceled");

    private final String dbValue;

    JobStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDBValue() {
        return dbValue;
    }

    public static JobStatus getForDBValue(String dbValue) {
        for (JobStatus status : values()) {
            if (status.dbValue.equals(dbValue))
                return status;
        }

        throw new IllegalArgumentException("Invalid dbValue, valid values are " + validDbValues());
    }

    private static String validDbValues()
    {
        String values = Arrays.stream(values())
                .map(jobStatus -> "\"" + jobStatus.dbValue + "\"")
                .collect(Collectors.joining(","));

        return "{" + values + "}";
    }
}
