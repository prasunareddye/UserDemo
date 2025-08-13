package com.code.challenge.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.code.challenge.model.Profile;
import com.code.challenge.model.PublicProfile;
import com.code.challenge.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPublicProfileTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;


    private static final String PHIL_INGWELL_STRING_ID = "11111111-1111-1111-1111-111111111111";
    private static final UUID PHIL_INGWELL_UUID = UUID.fromString(PHIL_INGWELL_STRING_ID);

    private static final String ANNA_CONDA_STRING_ID = "22222222-2222-2222-2222-222222222222";
    private static final UUID ANNA_CONDA_UUID = UUID.fromString(ANNA_CONDA_STRING_ID);

    // need this to bypass password write only so password can be serialized for test
    private static final JsonMapper mapper = JsonMapper.builder()
        .configure(MapperFeature.USE_ANNOTATIONS, false).build();


       @Test
    public void testSaveProfileWithMissingData() throws Exception {
        Profile profile = new Profile();
        final var content = mapper.writeValueAsString(profile);
        mockMvc.perform(post("/users/{userId}/saveprofile", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(status().isBadRequest());
    }


    @Test
    public void getProfileForUserTest() throws Exception {
        Profile profile = new Profile();
        profile.setBio("This is an example of bio");
        profile.setNickname("nick name");
        profile.setId(Long.valueOf("1"));
        final var content = mapper.writeValueAsString(profile);
        Mockito.when(userService.getProfile(any(UUID.class))).thenReturn(Optional.of(profile));
        mockMvc.perform(get("/users/{userId}/profile", ANNA_CONDA_STRING_ID).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.bio").value("This is an example of bio"))
            .andExpect(jsonPath("$.nickname").value("nick name"))
            .andExpect(status().isOk());
    }



    @Test
    public void getAllPublicProfilesTest() throws Exception {

        PublicProfile profile = new PublicProfile();
        profile.setBio("This is an example of bio");
        profile.setNickname("nick name");
       // profile.setId(Long.valueOf("1"));
        List<PublicProfile> profiles=Arrays.asList(profile);
        Mockito.when(userService.getAllProfiles()).thenReturn(profiles);
        mockMvc.perform(get("/users/publicprofiles").with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").doesNotExist())
         .andExpect(jsonPath("$[0].bio").exists())
            .andExpect(jsonPath("$[0].nickname").exists());

        }

}
