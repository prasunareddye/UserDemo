package com.code.challenge.converter;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderConverterTest {

    @Test
    public void encode_toDatabase_and_toEntity() {

        final var encoder = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();
        final var encryptor = new PasswordEncoderConverter(encoder);

        final var toDatabase = encryptor.convertToDatabaseColumn("password");
        assertTrue(encoder.matches("password", toDatabase));

        final var toEntity = encryptor.convertToEntityAttribute(toDatabase);
        assertEquals(toEntity, toDatabase);

    }
}