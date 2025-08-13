package com.code.challenge.controller;

import com.code.challenge.model.AuthenticationRequest;
import com.code.challenge.model.Jwt;
import com.code.challenge.service.JwtService;
import com.code.challenge.service.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    @PostMapping("/authenticate")
    @PermitAll
    public Jwt authenticate(
        @RequestBody final AuthenticationRequest authenticationRequest
    ) {
        // fetch user
        final var user = userService.getUserByUsername(
            authenticationRequest.username()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!encoder.matches(authenticationRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // return jwt
        return new Jwt(jwtService.encode(user));

    }
}
