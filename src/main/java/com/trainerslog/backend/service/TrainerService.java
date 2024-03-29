package com.trainerslog.backend.service;

import com.trainerslog.backend.lib.entity.Trainer;
import com.trainerslog.backend.lib.exception.ClientException;
import com.trainerslog.backend.lib.exception.NotFoundException;
import com.trainerslog.backend.lib.repository.TrainerRepository;
import com.trainerslog.backend.lib.types.PatchTrainerBody;
import com.trainerslog.backend.lib.types.TrainerFullNameAndUsername;
import com.trainerslog.backend.lib.types.TrainerPresence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public Trainer addPresence(String trainerUsername, TrainerPresence trainerPresence) {
        TrainerPresence truncatedTrainerPresence = trainerPresence.truncatedToHours();
        if( truncatedTrainerPresence.startOfDay().compareTo(truncatedTrainerPresence.endOfDay()) >= 0 ) {
            throw new ClientException("Invalid time interval");
        }
        Trainer trainer = getTrainer(trainerUsername);
        trainer.setStartOfDay(truncatedTrainerPresence.startOfDay());
        trainer.setEndOfDay(truncatedTrainerPresence.endOfDay());

        return trainerRepository.save(trainer);
    }

    public void setTotalClientsPerSessionForTrainer(String trainerUsername, Integer totalClients) {
        Trainer trainer = getTrainer(trainerUsername);

        trainer.setTotalClientsPerReservation(totalClients);

        trainerRepository.save(trainer);
    }

    public List<TrainerFullNameAndUsername> getAllUsernamesForTrainers() {
        return trainerRepository.findAllUsernamesForTrainers();
    }

    public Trainer getTrainer(String username) {
        return trainerRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("No trainer with username %s found.", username)));
    }

    public void patchTrainer(String username, PatchTrainerBody patchTrainerBody) {
        this.addPresence(username, new TrainerPresence(patchTrainerBody.startOfDay(), patchTrainerBody.endOfDay()));
        this.setTotalClientsPerSessionForTrainer(username, patchTrainerBody.totalClientsPerReservation());
    }
}
