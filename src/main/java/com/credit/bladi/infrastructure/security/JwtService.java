package com.credit.bladi.infrastructure.security;

import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.service.auth.ITokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements ITokenService {
    private final long expirationMs;

    private final SecretKey signingKey;

    private final IRevokedTokenRepository revokedTokenRepository;

    private final Clock clock;

    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration-ms}") long expirationMs,
        IRevokedTokenRepository revokedTokenRepository,
        Clock clock
    ) {
        this.expirationMs = expirationMs;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.revokedTokenRepository = revokedTokenRepository;
        this.clock = clock;
    }

    @Override
    public String generateToken(String username, String role) {
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(username)
            .claim("role", role)
            .issuedAt(Date.from(clock.instant()))
            .expiration(Date.from(clock.instant().plusMillis(expirationMs)))
            .signWith(signingKey)
            .compact();
    }

    @Override
    public Optional<String> findUsername(String token) {
        return parseQuietly(token).map(Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        return parseQuietly(token)
            .filter(claims -> claims.getSubject().equals(username))
            .filter(claims -> !claims.getExpiration().before(Date.from(clock.instant())))
            .isPresent()
            && !revokedTokenRepository.isRevoked(token);
    }

    @Override
    public void blacklistToken(String token) {
        try {
            long expiration = parseClaims(token).getExpiration().getTime();
            revokedTokenRepository.revoke(token, expiration);
        } catch (JwtException | IllegalArgumentException ignored) {
        }
    }

    private Optional<Claims> parseQuietly(String token) {
        try {
            return Optional.of(parseClaims(token));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .clock(() -> Date.from(clock.instant()))
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
