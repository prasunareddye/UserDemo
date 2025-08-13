package com.code.challenge.service;

import com.code.challenge.model.Role;
import com.code.challenge.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public String encode(final User user) {

        final var issued = Instant.now();
        final var expiration = issued.plus(Duration.ofHours(2));

        final var header = JwsHeader.with(MacAlgorithm.HS256).build();

        final var claims = JwtClaimsSet
            .builder()
            .issuer("challenge")
            .subject(user.getUsername())
            .claim("userId", user.getId())
            .claim("scp", user.getRoles().stream().map(Role::getName).toList())
            .issuedAt(issued)
            .expiresAt(expiration)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

    }
}
