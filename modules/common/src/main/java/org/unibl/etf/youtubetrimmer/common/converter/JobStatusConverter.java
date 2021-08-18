package org.unibl.etf.youtubetrimmer.common.converter;

import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JobStatusConverter implements AttributeConverter<JobStatus, String> {

    @Override
    public String convertToDatabaseColumn(JobStatus jobStatus) {
        return jobStatus.getDBValue();
    }

    @Override
    public JobStatus convertToEntityAttribute(String dbValue) {
        return JobStatus.getForDBValue(dbValue);
    }
}
