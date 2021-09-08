package org.unibl.etf.youtubetrimmer.trimmer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("trimmer")
@Data
@Validated
public class TrimmerProperties {
    private String workingDirectory;
    private String outputDirectory;
    private String processLogsDirectory;
    private String videoContainerName;
}
