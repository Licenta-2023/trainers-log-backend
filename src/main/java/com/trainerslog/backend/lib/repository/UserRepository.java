package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void removeByUsername(String username);
}
