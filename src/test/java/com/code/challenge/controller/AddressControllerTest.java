package com.code.challenge.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.code.challenge.model.UserAddress;
import com.code.challenge.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {


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
     public void testSaveAddressWithInvalidFields() throws Exception {

        UserAddress address = new UserAddress(); // initialize with required fields
        address.setStreetAddress("");
        address.setCity("");
        address.setPostalCode("");
        address.setState("");
        address.setApartmentNumber("");
        final var content = mapper.writeValueAsString(address);


        mockMvc.perform(post("/users/{userId}/saveaddress", ANNA_CONDA_STRING_ID).with(jwt()
                    .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                    .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID)))
                .contentType("application/json")
                .content(content))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.streetAddress").value("Street address is required."))
            .andExpect(jsonPath("$.city").value("City is required."))
            .andExpect(jsonPath("$.state").value("State is required."))
            .andExpect(jsonPath("$.postalCode").value("Postal code is required."))
            .andExpect(jsonPath("$.apartmentNumber").value("Apartment number cannot be left blank."));
    }


    @Test
    public void getAddressForUserTest() throws Exception {

        UserAddress address = new UserAddress(); // initialize with required fields
        address.setStreetAddress("123 Main St");
        address.setCity("Cityville");
        address.setPostalCode("80124");
        address.setState("CO");
        address.setApartmentNumber("A897");
        address.setId(Long.valueOf("1"));

       Mockito.when(userService.getAddressForUser(any(UUID.class))).thenReturn(Optional.of(address));
        mockMvc.perform(get("/users/{userId}/address", ANNA_CONDA_STRING_ID).with(jwt()
                .authorities(new SimpleGrantedAuthority("SCOPE_USER"))
                .jwt(claims -> claims.claim("userId", ANNA_CONDA_STRING_ID))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.streetAddress").value("123 Main St"))
            .andExpect(jsonPath("$.city").value("Cityville"))
            .andExpect(jsonPath("$.state").value("CO"))
            .andExpect(jsonPath("$.postalCode").value("80124"))
            .andExpect(jsonPath("$.apartmentNumber").value("A897"))
            .andExpect(status().isOk());
    }

}
