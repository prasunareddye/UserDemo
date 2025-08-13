package com.code.challenge.service;

import com.code.challenge.model.Profile;
import com.code.challenge.model.PublicProfile;
import com.code.challenge.model.User;
import com.code.challenge.model.UserAddress;
import com.code.challenge.repository.PublicProfileRepository;
import com.code.challenge.repository.UserAddressRepository;
import com.code.challenge.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceIface {

    private final UserRepo userRepo;

    private final UserAddressRepository userAddressRepository;

    private final PublicProfileRepository publicProfileRepository;

    @Override
    public Optional<User> getUser(final UUID userId) {
        return userRepo.findById(userId);
    }

    @Override
    public List<User> listUsers() {
        return userRepo.findAll();
    }

    @Override
    public User saveUser(final User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByUsername(final String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void deleteUser(final UUID userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public UserAddress saveAddress(UserAddress address, UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        UserAddress existingAddress = user.getAddress();

        if (existingAddress != null) {
            existingAddress.updateFrom(address);
            return userAddressRepository.save(existingAddress);
        } else {
            address.setUser(user);
            return userAddressRepository.save(address);
        }
    }

    @Override
    public Optional<UserAddress> getAddressForUser(UUID userId) {
        return userRepo.findById(userId)
            .map(User::getAddress);
    }

    @Override
    public Profile saveProfile(Profile profile, UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Profile existingProfile = user.getProfile();

        if (existingProfile != null) {
            existingProfile.updateFrom(profile);
            return publicProfileRepository.save(existingProfile);
        } else {
            profile.setUser(user);
            return publicProfileRepository.save(profile);
        }
    }

    @Override
    public Optional<Profile> getProfile(UUID userId) {
        return userRepo.findById(userId)
            .map(User::getProfile);
    }

    @Override
    public List<PublicProfile> getAllProfiles() {
        List<Profile> profiles = publicProfileRepository.findAll();
        return profiles.stream().map(PublicProfile::convert).toList();

    }

}
