package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Reservation;
import com.trainerslog.backend.lib.entity.Trainer;
import com.trainerslog.backend.lib.entity.User;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.exception.NotFoundException;
import com.trainerslog.backend.lib.repository.ReservationRepository;
import com.trainerslog.backend.lib.repository.TrainerRepository;
import com.trainerslog.backend.lib.repository.UserRepository;
import com.trainerslog.backend.lib.types.*;
import com.trainerslog.backend.lib.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final TrainerRepository trainerRepository;

    private final UserRepository userRepository;

    public Reservation addReservationToTrainer(ReservationRequest reservationRequest) {
        LocalDateTime truncatedByHoursTimeIntervalBegin = reservationRequest.timeIntervalBegin().truncatedTo(ChronoUnit.HOURS);
        Trainer trainer = trainerRepository.findByUsername(reservationRequest.trainerUsername()).orElseThrow(() -> new NotFoundException(String.format("No trainer with username %s found.", reservationRequest.trainerUsername())));
        validateCreateReservation(reservationRequest, trainer);

        Reservation reservation = new Reservation();

        reservation.setTrainer(trainer);

        reservation.setClient(userRepository.findByUsername(
                reservationRequest.username()).orElseThrow(() -> new UsernameNotFoundException(String.format("No client found for username %s.", reservationRequest.username())))
        );

        reservation.setReservationType(reservationRequest.reservationType());

        reservation.setTimeIntervalBegin(truncatedByHoursTimeIntervalBegin);

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(ReservationRequest reservationRequest, String authorization) {
        String requestUsername = UserUtils.getUsernameFromBearerToken(authorization);
        validateRequestUserForDeleteReservation(reservationRequest, requestUsername);

        throwIfReservationIsInPast(reservationRequest.timeIntervalBegin());

        LocalDateTime truncatedByHoursTimeIntervalBegin = reservationRequest.timeIntervalBegin().truncatedTo(ChronoUnit.HOURS);
        List<Reservation> reservations = reservationRepository.findReservationsForTrainerAtGivenMoment(reservationRequest.trainerUsername(), truncatedByHoursTimeIntervalBegin);
        if (reservations.size() == 0) {
            throw new ClientException("Reservation does not exist");
        }
        Reservation reservationToDelete;
        if( reservationRequest.reservationType().equals(ReservationType.TRAINING)) {
            reservationToDelete = reservations.stream()
                    .filter(reservation -> reservation.getClient().getUsername().equals(reservationRequest.username()) && reservation.getReservationType().equals(reservationRequest.reservationType()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format("No client found for username %s and trainer %s.", reservationRequest.username(), reservationRequest.trainerUsername())));
        } else {
            reservationToDelete = reservations.get(0);
            if (!reservationToDelete.getReservationType().equals(reservationRequest.reservationType())) {
                throw new ClientException("Reservation does not exist");
            }
        }


        reservationRepository.delete(reservationToDelete);
    }

    private void validateRequestUserForDeleteReservation(ReservationRequest reservationRequest, String requestUsername) {

        User requestUser = userRepository.findByUsername(requestUsername).orElseThrow(() -> new NotFoundException(String.format("No user with username %s found.", requestUsername)));
        boolean userIsTrainerOrAdmin = requestUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(UserRoles.TRAINER) || role.getName().equals(UserRoles.ADMIN));
        if ( !userIsTrainerOrAdmin && !requestUser.getUsername().equals(reservationRequest.username())) {
            throw new ClientException(String.format("User %s cannot delete a reservation for an other user %s", requestUsername, reservationRequest.username()));
        }
    }

    private void validateCreateReservation(ReservationRequest reservationRequest, Trainer trainer) {
        LocalDateTime truncatedByHoursTimeIntervalBegin = reservationRequest.timeIntervalBegin().truncatedTo(ChronoUnit.HOURS);
        List<Reservation> trainerReservationsAtGivenMoment = reservationRepository.findReservationsForTrainerAtGivenMoment(reservationRequest.trainerUsername(), truncatedByHoursTimeIntervalBegin);

        throwIfReservationIsInPast(truncatedByHoursTimeIntervalBegin);

        throwIfIntervalHasBlocker(reservationRequest, truncatedByHoursTimeIntervalBegin, trainerReservationsAtGivenMoment);

        throwIfUserHasOtherReservationInTheSameTimeInterval(reservationRequest.username(), truncatedByHoursTimeIntervalBegin);

        throwIfReservationAlreadyExistAndNewOneIsBlocker(trainerReservationsAtGivenMoment, reservationRequest);

        throwIfOutsideWorkingHours(truncatedByHoursTimeIntervalBegin.toLocalTime(), trainer);

        if (trainerReservationsAtGivenMoment.size() >= trainer.getTotalClientsPerReservation()) {
            throw new ClientException(String.format("Trainer %s is fully booked for the interval %s", reservationRequest.trainerUsername(), truncatedByHoursTimeIntervalBegin));
        }

        checkForDuplicateReservation(reservationRequest, truncatedByHoursTimeIntervalBegin, trainerReservationsAtGivenMoment);
    }

    private void throwIfUserHasOtherReservationInTheSameTimeInterval(String username, LocalDateTime truncatedByHoursTimeIntervalBegin) {
        if (this.reservationRepository.findReservationsForUserAtGivenMoment(username, truncatedByHoursTimeIntervalBegin).size() > 0) {
            throw new ClientException(String.format("User %s has a reservation for the time interval %s", username, truncatedByHoursTimeIntervalBegin));
        }
    }

    private void throwIfReservationIsInPast(LocalDateTime reservationTimeIntervalBegin) {
        if (LocalDateTime.now().isAfter(reservationTimeIntervalBegin)) {
            throw new ClientException("Cannot create or modify a reservation in the past");
        }
    }

    private void throwIfOutsideWorkingHours(LocalTime reservationBegin, Trainer trainer) {
        if( reservationBegin.compareTo(trainer.getStartOfDay()) < 0 || reservationBegin.compareTo(trainer.getEndOfDay()) >= 0) {
            throw new ClientException(String.format("Invalid reservation time: %s", reservationBegin));
        }
    }

    private void checkForDuplicateReservation(ReservationRequest reservationRequest, LocalDateTime truncatedByHoursTimeIntervalBegin, List<Reservation> trainerReservationsAtGivenMoment) {
        boolean reservationForUserAlreadyExists = trainerReservationsAtGivenMoment.stream()
                .map(Reservation::getClient)
                .map(User::getUsername)
                .anyMatch(clientUsername -> clientUsername.equals(reservationRequest.username()));

        if (reservationForUserAlreadyExists) {
            throw new ClientException(
                    String.format("Client %s is already booked for trainer %s and interval %s",
                            reservationRequest.username(),
                            reservationRequest.trainerUsername(),
                            truncatedByHoursTimeIntervalBegin
                    ));
        }
    }

    private void throwIfReservationAlreadyExistAndNewOneIsBlocker(List<Reservation> trainerReservationsAtGivenMoment, ReservationRequest reservationRequest) {
        boolean trainingReservationExists = trainerReservationsAtGivenMoment.stream()
                .anyMatch(reservation -> reservation.getReservationType().equals(ReservationType.TRAINING));

        if (reservationRequest.reservationType().equals(ReservationType.BLOCKER) && trainingReservationExists) {
            throw new ClientException(String.format(
                    "Trainer %s already has a reservation for interval %s, make sure there is no reservation for the interval in order to add a blocker",
                    reservationRequest.trainerUsername(),
                    reservationRequest.timeIntervalBegin().truncatedTo(ChronoUnit.HOURS)
            ));
        }
    }

    private void throwIfIntervalHasBlocker(ReservationRequest reservationRequest, LocalDateTime truncatedByHoursTimeIntervalBegin, List<Reservation> trainerReservationsAtGivenMoment) {
        boolean reservationIsAlreadyHasBlocker = trainerReservationsAtGivenMoment.stream()
                .anyMatch(
                        reservation -> reservation.getReservationType().equals(ReservationType.BLOCKER) && reservation.getTimeIntervalBegin().equals(truncatedByHoursTimeIntervalBegin)
                );

        if (reservationIsAlreadyHasBlocker) {
            throw new ClientException(String.format("Trainer %s has a blocker for the interval %s", reservationRequest.trainerUsername(), truncatedByHoursTimeIntervalBegin));
        }
    }

    public List<Reservation> getCurrentMonthReservationsForUser(String username, Integer year, Integer month) {
        return reservationRepository.findReservationsForUserByMonth(username, year, month);
    }

    public List<Reservation> getCurrentDayReservationsForUser(String username, Integer year, Integer month, Integer day) {
        return reservationRepository.findReservationsForUserByMonthAndDay(username, year, month, day);
    }

    public List<Reservation> getCurrentMonthReservationsForTrainer(String username, Integer year, Integer month) {
        return reservationRepository.findReservationsForTrainerByMonth(username, year, month);
    }

    public List<Reservation> getCurrentDayReservationsForTrainer(String username, Integer year, Integer month, Integer day) {
        return reservationRepository.findReservationsForTrainerByMonthAndDay(username, year, month, day);
    }

    public List<ReservationsStatistics> getReservationsStatisticsByYearAndMonth(Integer year, Integer month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1,  0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.toLocalDate()
                .withDayOfMonth(startOfMonth.toLocalDate().lengthOfMonth())
                .atTime(LocalTime.MAX);
        List<ReservationCount> reservationCountPerDay = this.reservationRepository.findReservationsByYearAndMonth(startOfMonth, endOfMonth);

        return LocalDate.from(startOfMonth)
                .datesUntil(LocalDate.from(endOfMonth).plusDays(1))
                .map(date -> new ReservationsStatistics(date, getCountForDate(reservationCountPerDay, date).intValue()))
                .collect(Collectors.toList());
    }

    private static Long getCountForDate(List<ReservationCount> reservations, LocalDate date) {
        return reservations.stream()
                .filter(reservation -> date.equals(reservation.date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                .mapToLong(ReservationCount::count)
                .findFirst()
                .orElse(0L);
    }
}
