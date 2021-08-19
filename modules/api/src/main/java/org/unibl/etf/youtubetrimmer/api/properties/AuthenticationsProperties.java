package org.unibl.etf.youtubetrimmer.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties("authentication")
@Component
@Validated
public class AuthenticationsProperties {

    @Data
    public static class Token {
        private String signingKeyPath;
        private int expirationDuration;
    }

    private Token token;
}
