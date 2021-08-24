package org.unibl.etf.youtubetrimmer.api;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unibl.etf.youtubetrimmer.api.model.JobDetails;
import org.unibl.etf.youtubetrimmer.api.model.message.JobStatusMessage;
import org.unibl.etf.youtubetrimmer.api.util.LambdaConverter;
import org.unibl.etf.youtubetrimmer.api.util.URLUtils;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;
import org.unibl.etf.youtubetrimmer.common.entity.JobStatus;
import org.unibl.etf.youtubetrimmer.common.messaging.model.JobEventMessage;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(JobEntity.class, JobDetails.class)
                .addMappings(m -> m.using(LambdaConverter.of(JobStatus::getDBValue))
                        .map(JobEntity::getStatus, JobDetails::setStatus))
                .addMappings(m -> m.using(LambdaConverter.of(URLUtils::getYoutubeUrl))
                        .map(j -> j.getVideo().getVideoUid(), JobDetails::setVideoUrl));

        mapper.typeMap(JobEventMessage.class, JobStatusMessage.class)
                .addMappings(m ->
                        m.using(LambdaConverter.of(JobStatus::getDBValue))
                        .map(JobEventMessage::getNewStatus, JobStatusMessage::setNewStatus));
        return mapper;
    }
}
