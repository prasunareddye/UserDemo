package com.code.challenge.service;

import com.code.challenge.model.Profile;
import com.code.challenge.model.PublicProfile;
import com.code.challenge.model.User;
import com.code.challenge.model.UserAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceIface {
    Optional<User> getUser(UUID userId);

    List<User> listUsers();

    User saveUser(User user);

    Optional<User> getUserByUsername(String username);

    void deleteUser(UUID userId);

    UserAddress saveAddress(UserAddress address, UUID userId);

    Optional<UserAddress> getAddressForUser(UUID userId);

    Profile saveProfile(Profile profile, UUID userId);

    Optional<Profile> getProfile(UUID userId);

    List<PublicProfile> getAllProfiles();
}
