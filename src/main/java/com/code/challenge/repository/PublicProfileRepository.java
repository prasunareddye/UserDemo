package com.code.challenge.repository;

import com.code.challenge.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicProfileRepository extends JpaRepository<Profile, Long> {
}
