package org.unibl.etf.youtubetrimmer.api;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(JobEntity.class, JobDetails.class).addMappings(m -> m.using(new AbstractConverter<JobStatus, String>() {
            @Override
            protected String convert(JobStatus jobStatus) {
                return jobStatus.getDBValue();
            }
        }).map(JobEntity::getStatus, JobDetails::setStatus));
        return mapper;
    }
}
