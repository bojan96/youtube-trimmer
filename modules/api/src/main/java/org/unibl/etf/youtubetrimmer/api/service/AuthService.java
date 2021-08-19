package org.unibl.etf.youtubetrimmer.api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.properties.AuthenticationsProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TimeService timeService;
    private final AuthenticationsProperties props;

    public String generateTokenForUser(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(Date.from(timeService.now().plus(props.getToken().getExpirationDuration(), ChronoUnit.DAYS)))
                .setSubject(userId)
                .signWith(getSigningKey())
                .compact();
    }

    public String issueToken(String oldToken) {
        Jws<Claims> claims = parseToken(oldToken);
        Instant expiration = claims.getBody().getExpiration().toInstant();

        Duration duration = Duration.between(timeService.now(), expiration);
        if (duration.toDays() < 3)
            return generateTokenForUser(claims.getBody().getSubject());

        return oldToken;
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            return false;
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
    }

    @SneakyThrows
    private Key getSigningKey() {
        String keyBase64 = Files.readString(Path.of(props.getToken().getSigningKeyPath()));
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyBase64));
    }
}
