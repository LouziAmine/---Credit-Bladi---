package com.credit.bladi.service.auth;

import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements ITokenService {

    private final long expirationMs;

    private final SecretKey signingKey;

    private final IRevokedTokenRepository revokedTokenRepository;

    private final Clock clock;

    @Autowired
    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration-ms}") long expirationMs,
        IRevokedTokenRepository revokedTokenRepository
    ) {
        this(secret, expirationMs, revokedTokenRepository, Clock.systemUTC());
    }

    JwtService(String secret, long expirationMs, IRevokedTokenRepository revokedTokenRepository, Clock clock) {
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
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(signingKey)
            .compact();
    }

    @Override
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        Claims claims = parseClaims(token);
        return !revokedTokenRepository.isRevoked(token)
            && claims.getSubject().equals(username)
            && !claims.getExpiration().before(Date.from(clock.instant()));
    }

    @Override
    public void blacklistToken(String token) {
        try {
            long expiration = parseClaims(token).getExpiration().getTime();
            revokedTokenRepository.revoke(token, expiration);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ignored) {
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}