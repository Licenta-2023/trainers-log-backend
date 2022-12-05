package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Trainer;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.exception.NotFoundException;
import com.trainerslog.backend.lib.repository.TrainerRepository;
import com.trainerslog.backend.lib.types.TrainerPresence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public Trainer addPresence(String trainerUsername, TrainerPresence trainerPresence) {
        TrainerPresence truncatedTrainerPresence = trainerPresence.truncatedToHours();
        if( truncatedTrainerPresence.startOfDay().compareTo(truncatedTrainerPresence.endOfDay()) >= 0 ) {
            throw new ClientException("Invalid time interval");
        }
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow(() -> new NotFoundException(String.format("No trainer with username %s found.", trainerUsername)));
        trainer.setStartOfDay(truncatedTrainerPresence.startOfDay());
        trainer.setEndOfDay(truncatedTrainerPresence.endOfDay());

        return trainerRepository.save(trainer);
    }

    public void setTotalClientsPerSessionForTrainer(String trainerUsername, Integer totalClients) {
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow(() -> new NotFoundException(String.format("No trainer with username %s found.", trainerUsername)));

        trainer.setTotalClientsPerReservation(totalClients);

        trainerRepository.save(trainer);
    }
}
