package org.unibl.etf.youtubetrimmer.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties("storage")
@Component
@Validated
public class StorageProperties {
    private String videoContainerName;
    private String connectionStringPath;
    private int sasTokenExpiryDurationDays;
    private String downloadUrlBase;
}
