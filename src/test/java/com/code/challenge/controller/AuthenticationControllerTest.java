package com.code.challenge.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.code.challenge.model.AuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    private static final JsonMapper mapper = JsonMapper.builder().build();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authenticate() throws Exception {

        final var authRequest = new AuthenticationRequest("PhilIngwell", "password");

        mockMvc
            .perform(post("/authenticate")
                .content(mapper.writeValueAsString(authRequest))
                .contentType("application/json"))
            .andExpect(jsonPath("$.token").exists())
            .andExpect(status().isOk());
    }

    @Test
    public void authenticate_notFound_username() throws Exception {
        final var authRequest = new AuthenticationRequest("WillieMaykit", "password");

        mockMvc
            .perform(post("/authenticate")
                .content(mapper.writeValueAsString(authRequest))
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void authenticate_notFound_password() throws Exception {
        final var authRequest = new AuthenticationRequest("PhilIngwell", "wrongpassword");
        mockMvc
            .perform(post("/authenticate")
                .content(mapper.writeValueAsString(authRequest))
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }
}