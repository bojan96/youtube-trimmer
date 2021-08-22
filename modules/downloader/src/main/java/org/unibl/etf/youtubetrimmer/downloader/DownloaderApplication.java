package org.unibl.etf.youtubetrimmer.downloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.unibl.etf.youtubetrimmer.common.repository")
@EntityScan("org.unibl.etf.youtubetrimmer.common.entity")
public class DownloaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloaderApplication.class, args);
    }

}
