package com.code.challenge.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.code.challenge.model.Profile;
import com.code.challenge.model.Role;
import com.code.challenge.model.User;
import com.code.challenge.model.UserAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PHIL_INGWELL_STRING_ID = "11111111-1111-1111-1111-111111111111";
    private static final UUID PHIL_INGWELL_UUID = UUID.fromString(PHIL_INGWELL_STRING_ID);

    private static final String ANNA_CONDA_STRING_ID = "22222222-2222-2222-2222-222222222222";
    private static final UUID ANNA_CONDA_UUID = UUID.fromString(ANNA_CONDA_STRING_ID);

    // need this to bypass password write only so password can be serialized for test
    private static final JsonMapper mapper = JsonMapper.builder()
        .configure(MapperFeature.USE_ANNOTATIONS, false).build();

    // GET /users/{userId}

    @Test
    public void getUser() throws Exception {

        mockMvc.perform(get("/users/{userId}", PHIL_INGWELL_STRING_ID).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))
                .jwt(claims -> claims.claim("userId", PHIL_INGWELL_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("PhilIngwell"))
            .andExpect(jsonPath("$.firstName").value("Phil"))
            .andExpect(jsonPath("$.lastName").value("Ingwell"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void getUser_asAdmin() throws Exception {

        mockMvc.perform(get("/users/{userId}", ANNA_CONDA_STRING_ID).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))
                .jwt(claims -> claims.claim("userId", PHIL_INGWELL_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("AnnaConda"))
            .andExpect(jsonPath("$.firstName").value("Anna"))
            .andExpect(jsonPath("$.lastName").value("Conda"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void getUser_unauthorized() throws Exception {

        mockMvc.perform(get("/users/{userId}", ANNA_CONDA_STRING_ID))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getInvalidUser_forbidden() throws Exception {

        mockMvc.perform(get("/users/{userId}", PHIL_INGWELL_STRING_ID).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID))))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getInvalidUser_notFound() throws Exception {

        final var id = UUID.randomUUID().toString();

        mockMvc.perform(get("/users/{userId}", id).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", id))))
            .andExpect(status().isNotFound());
    }

    // GET /users

    @Test
    public void listUsers() throws Exception {

        mockMvc.perform(get("/users").with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))
                .jwt(claims -> claims.claim("userId", PHIL_INGWELL_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void listUsers_forbidden_notAdmin() throws Exception {

        mockMvc.perform(get("/users").with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID))))
            .andExpect(status().isForbidden());
    }

    @Test
    public void listUsers_unauthorized() throws Exception {

        mockMvc.perform(get("/users"))
            .andExpect(status().isUnauthorized());
    }

    // POST /users

    @Test
    @Transactional
    public void createUser() throws Exception {

        final var user = new User()
            .setUsername("WillieMaykit")
            .setFirstName("Willie")
            .setLastName("Maykit")
            .setPassword("password")
            .setRoles(Set.of(new Role().setName("USER")));

        final var content = mapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("Willie"))
            .andExpect(jsonPath("$.lastName").value("Maykit"))
            .andExpect(jsonPath("$.username").value("WillieMaykit"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(status().isOk());
    }




    // PUT /users/{userId}

    @Test
    @Transactional
    public void updateUser() throws Exception {

        final var user = new User()
            .setId(ANNA_CONDA_UUID)
            .setUsername("AnnaCondax")
            .setPassword("passwordx")
            .setFirstName("Annax")
            .setLastName("Condax")
            .setRoles(Set.of(new Role().setName("USER")));

        final var content = mapper.writeValueAsString(user);

        mockMvc.perform(put("/users/{userId}", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.firstName").value("Annax"))
            .andExpect(jsonPath("$.lastName").value("Condax"))
            .andExpect(jsonPath("$.username").value("AnnaCondax"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(status().isOk());
    }

    @Test
    public void updateUser_forbidden_notOwner() throws Exception {

        final var user = new User()
            .setId(PHIL_INGWELL_UUID)
            .setUsername("PhilIngwell")
            .setPassword("password")
            .setFirstName("Phil")
            .setLastName("Ingwell")
            .setRoles(Set.of(new Role().setName("USER")));

        final var content = mapper.writeValueAsString(user);

        mockMvc.perform(put("/users/{userId}", PHIL_INGWELL_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content)
            )
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateUser_invalid() throws Exception {

        final var user = new User()
            .setId(PHIL_INGWELL_UUID);

        final var content = mapper.writeValueAsString(user);

        mockMvc.perform(put("/users/{userId}", PHIL_INGWELL_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))
                    .jwt(claims -> claims.claim("userId", PHIL_INGWELL_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.firstName").exists())
            .andExpect(jsonPath("$.lastName").exists())
            .andExpect(jsonPath("$.password").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_unauthorized() throws Exception {

        final var user = new User()
            .setId(PHIL_INGWELL_UUID)
            .setUsername("PhilIngwell")
            .setPassword("password")
            .setFirstName("Phil")
            .setLastName("Ingwell")
            .setRoles(Set.of(new Role().setName("USER")));

        final var content = mapper.writeValueAsString(user);

        mockMvc.perform(put("/users/{userId}", PHIL_INGWELL_STRING_ID)
                .contentType("application/json")
                .content(content)
            )
            .andExpect(status().isUnauthorized());
    }





    // DELETE /users/{userId}

    @Test
    @Transactional
    public void deleteUser() throws Exception {

        mockMvc.perform(
                delete("/users/{userId}", ANNA_CONDA_STRING_ID).with(jwt()
                        .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                        .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                    .contentType("application/json"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteUser_admin() throws Exception {

        mockMvc.perform(delete("/users/{userId}", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))
                    .jwt(claims -> claims.claim("userId", PHIL_INGWELL_STRING_ID)))
                .contentType("application/json"))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_forbidden() throws Exception {

        mockMvc.perform(delete("/users/{userId}", PHIL_INGWELL_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUser_unauthorized() throws Exception {

        mockMvc.perform(delete("/users/{userId}", PHIL_INGWELL_STRING_ID))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    public void testSaveAddress() throws Exception {

        UserAddress address = new UserAddress(); // initialize with required fields
        address.setStreetAddress("123 Main St");
        address.setCity("Cityville");
        address.setPostalCode("80124");
        address.setState("CO");
        address.setApartmentNumber("A897");
        final var content = mapper.writeValueAsString(address);


        mockMvc.perform(post("/users/{userId}/saveaddress", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.streetAddress").value("123 Main St"))
            .andExpect(jsonPath("$.city").value("Cityville"))
            .andExpect(jsonPath("$.state").value("CO"))
            .andExpect(jsonPath("$.postalCode").value("80124"))
            .andExpect(jsonPath("$.apartmentNumber").value("A897"))
            .andExpect(status().isOk());
    }



    @Test
    @Transactional
    public void saveProfileTest() throws Exception {

        Profile profile=new Profile();
        profile.setBio("This is an example of bio");
        profile.setNickname("nick name");
        final var content = mapper.writeValueAsString(profile);


        mockMvc.perform(post("/users/{userId}/saveprofile", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.bio").value("This is an example of bio"))
            .andExpect(jsonPath("$.nickname").value("nick name"))
            .andExpect(status().isOk());
    }
}
