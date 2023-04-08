package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.Trainer;
import com.trainerslog.backend.lib.types.TrainerFullNameAndUsername;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("select t from Trainer t join User u on t.user = u where u.username = :trainerUsername")
    Optional<Trainer> findByUsername(String trainerUsername);

    @Query("select new com.trainerslog.backend.lib.types.TrainerFullNameAndUsername(CONCAT(t.user.firstName, ' ', t.user.lastName), t.user.username) from Trainer t join User u on t.user = u")
    List<TrainerFullNameAndUsername> findAllUsernamesForTrainers();
}
