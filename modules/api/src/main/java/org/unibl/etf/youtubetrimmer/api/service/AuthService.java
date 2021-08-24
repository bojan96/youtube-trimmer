package org.unibl.etf.youtubetrimmer.api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.api.properties.AuthenticationsProperties;
import org.unibl.etf.youtubetrimmer.api.security.JwtAuthenticationToken;
import org.unibl.etf.youtubetrimmer.common.entity.UserEntity;
import org.unibl.etf.youtubetrimmer.common.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String UID_CLAIM = "uid";
    private final TimeService timeService;
    private final AuthenticationsProperties props;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    public Optional<String> login(String username, String password) {
        UserEntity example = new UserEntity();
        example.setUsername(username);
        return userRepo
                .findOne(Example.of(example))
                .map(u -> {
                    if (passwordEncoder.matches(password, u.getPassword()))
                        return generateTokenForUser(u.getId(), username);

                    return null;
                });
    }

    private String generateTokenForUser(int userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim(UID_CLAIM, userId)
                .setExpiration(getExpirationDate())
                .signWith(getSigningKey())
                .compact();
    }

    private Date getExpirationDate() {
        int expirationDuration = props.getToken().getExpirationDuration();
        Instant expiration = timeService
                .now()
                .plus(expirationDuration, ChronoUnit.DAYS);

        return Date.from(expiration);
    }

    public String issueToken(String oldToken) {
        Jws<Claims> claims = parseToken(oldToken);
        Instant expiration = claims.getBody().getExpiration().toInstant();
        int userId = getUserId(claims);
        String username = getUsername(claims);
        Duration duration = Duration.between(timeService.now(), expiration);
        return duration.toDays() < 3 ? generateTokenForUser(userId, username) : oldToken;
    }

    public boolean isTokenValid(String token, boolean tokenExpirationCheck) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {

            return !(ex instanceof UnsupportedJwtException
                    || ex instanceof MalformedJwtException
                    || ex instanceof SignatureException
                    || (tokenExpirationCheck ?
                    ex instanceof ExpiredJwtException : false));

        }
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | ExpiredJwtException ex) {
            return false;
        }
    }

    public JwtAuthenticationToken getPrincipalFromToken(String token) {
        Jws<Claims> claims = parseToken(token);
        return new JwtAuthenticationToken(getUserId(claims), token, getUsername(claims));
    }

    private int getUserId(Jws<Claims> claims) {
        return claims.getBody().get(UID_CLAIM, Integer.class);
    }

    private String getUsername(Jws<Claims> claims) {
        return claims.getBody().getSubject();
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
