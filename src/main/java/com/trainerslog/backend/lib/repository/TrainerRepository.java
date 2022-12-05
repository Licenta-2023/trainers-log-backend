package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("select t from Trainer t join User u on t.user = u where u.username = :trainerUsername")
    Optional<Trainer> findByUsername(String trainerUsername);
}
