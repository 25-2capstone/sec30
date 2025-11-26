package com.gmg.sec30.repository;

import com.gmg.sec30.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}

