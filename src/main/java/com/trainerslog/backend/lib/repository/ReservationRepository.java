package com.trainerslog.backend.lib.repository;

import com.trainerslog.backend.lib.entity.Reservation;
import com.trainerslog.backend.lib.types.ReservationCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select r from Reservation r " +
            "join User u on r.client = u " +
            "where u.username = :username " +
            "and r.timeIntervalBegin = :reservationTime")
    List<Reservation> findReservationsForUserAtGivenMoment(String username, LocalDateTime reservationTime);

    @Query("select r from Reservation r " +
            "join User u on r.client = u " +
            "where r.client.username = :username and month(r.timeIntervalBegin) = :month and year(r.timeIntervalBegin) = :year")
    List<Reservation> findReservationsForUserByMonth(String username, int year, int month);

    @Query("select r from Reservation r " +
            "join User u on r.client = u " +
            "where r.client.username = :username and month(r.timeIntervalBegin) = :month and day(r.timeIntervalBegin) = :day and year(r.timeIntervalBegin) = :year")
    List<Reservation> findReservationsForUserByMonthAndDay(String username, int year, int month, int day);

    @Query("select r from Reservation r " +
            "join User u on r.client = u " +
            "where r.trainer.user.username = :username and month(r.timeIntervalBegin) = :month and year(r.timeIntervalBegin) = :year")
    List<Reservation> findReservationsForTrainerByMonth(String username, int year, int month);

    @Query("select r from Reservation r " +
            "join User u on r.client = u " +
            "where r.trainer.user.username = :username and month(r.timeIntervalBegin) = :month and day(r.timeIntervalBegin) = :day and year(r.timeIntervalBegin) = :year")
    List<Reservation> findReservationsForTrainerByMonthAndDay(String username, int year, int month, int day);

    @Query("SELECT NEW com.trainerslog.backend.lib.types.ReservationCount(DATE_TRUNC('day', r.timeIntervalBegin), COUNT(r)) " +
            "FROM Reservation r " +
            "WHERE r.timeIntervalBegin BETWEEN :startDate AND :endDate GROUP BY DATE_TRUNC('day', r.timeIntervalBegin)")
    List<ReservationCount> findReservationsByYearAndMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
