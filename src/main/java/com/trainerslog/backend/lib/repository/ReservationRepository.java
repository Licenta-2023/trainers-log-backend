package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r " +
            "join Trainer t on r.trainer = t " +
            "where t.user.username = :trainerUsername " +
            "and r.timeIntervalBegin = :reservationTime")
    List<Reservation> findReservationsForTrainerAtGivenMoment(String trainerUsername, LocalDateTime reservationTime);
}
