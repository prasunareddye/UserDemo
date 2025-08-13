package com.code.challenge.controller;

import com.code.challenge.exception.EntityNotFoundException;
import com.code.challenge.exception.InvalidProfileDataException;
import com.code.challenge.model.Profile;
import com.code.challenge.model.PublicProfile;
import com.code.challenge.model.User;
import com.code.challenge.model.UserAddress;
import com.code.challenge.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public User getUser(
        @CurrentSecurityContext(expression = "authentication") final JwtAuthenticationToken authentication,
        @PathVariable final String userId
    ) {
        final var currentUserId = authentication.getTokenAttributes().get("userId");
        final var authorized = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("SCOPE_ADMIN"));
        if (!currentUserId.equals(userId) && !authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userService.getUser(UUID.fromString(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<User> listUsers() {
        return userService.listUsers();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public User updateUser(
        @CurrentSecurityContext(expression = "authentication") final JwtAuthenticationToken authentication,
        @PathVariable final String userId,
        @Valid @RequestBody final User user) {
        final var currentUserId = authentication.getTokenAttributes().get("userId");
        // make sure the current user is the same as the user being updated
        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        // set user id to current user to prevent allowing other users to be updated
        user.setId(UUID.fromString(userId));
        return userService.saveUser(user);
    }

    @PostMapping
    @PermitAll
    public User createUser(@Valid @RequestBody final User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public void deleteUser(
        @CurrentSecurityContext(expression = "authentication") final JwtAuthenticationToken authentication,
        @PathVariable final String userId
    ) {
        final var currentUserId = authentication.getTokenAttributes().get("userId");
        final var authorized = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("SCOPE_ADMIN"));
        if (currentUserId.equals(userId) || authorized) {
            userService.deleteUser(UUID.fromString(userId));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/{userId}/saveaddress")
    @PreAuthorize("isAuthenticated()")
    public UserAddress saveAddress(
        @CurrentSecurityContext(expression = "authentication") final JwtAuthenticationToken authentication, @PathVariable String userId, @Valid @RequestBody UserAddress address) {

        final var currentUserId = authentication.getTokenAttributes().get("userId");
        if (currentUserId.equals(userId)) {
            return userService.saveAddress(address, UUID.fromString(userId));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/{userId}/address")
    @PreAuthorize("isAuthenticated()")
    public Optional<UserAddress> getAddressForUser(@CurrentSecurityContext(expression = "authentication") final JwtAuthenticationToken authentication, @PathVariable String userId) throws EntityNotFoundException {

        Optional<UserAddress> address = userService.getAddressForUser(UUID.fromString(userId));
        if (address.isEmpty()) {
            throw new EntityNotFoundException("Entity does not exist");
        }
        return address;
    }


    @PostMapping("/{userId}/saveprofile")
    @PreAuthorize("isAuthenticated()")
    public Profile saveProfile( @CurrentSecurityContext(expression = "authentication") JwtAuthenticationToken authentication,
        @PathVariable UUID userId, @Valid @RequestBody Profile profile) throws InvalidProfileDataException
    {
        UUID currentUserId = UUID.fromString(authentication.getTokenAttributes().get("userId").toString());

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (isProfileDataInvalid(profile)) {
            throw new InvalidProfileDataException("Bio and nickname cannot be null.");
        }

        return userService.saveProfile(profile, userId);
    }

    private boolean isProfileDataInvalid(Profile profile) {
        return !StringUtils.hasLength(profile.getBio()) && !StringUtils.hasLength(profile.getNickname());
    }


    @GetMapping("/publicprofiles")
    @PreAuthorize("isAuthenticated()")
    public List<PublicProfile> listPublicProfiles() {
        return userService.getAllProfiles();
    }


    @GetMapping("/{userId}/profile")
    @PreAuthorize("isAuthenticated()")
    public Optional<Profile> getProfile(@PathVariable String userId) throws EntityNotFoundException {

        Optional<Profile> publicProfile = userService.getProfile(UUID.fromString(userId));
        if (publicProfile.isEmpty()) {
            throw new EntityNotFoundException("Entity does not exist");
        }
        return publicProfile;
    }
}
