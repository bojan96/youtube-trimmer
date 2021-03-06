package org.unibl.etf.youtubetrimmer.downloader.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties("downloader")
@Validated
public class DownloaderProperties {
    private String workingDirectory;
    private String videoDirectory;
    private String processLogsDirectory;
}
