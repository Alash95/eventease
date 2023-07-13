package com.alash.eventease.repository;

import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.model.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(UserEntity existingUser);
}
