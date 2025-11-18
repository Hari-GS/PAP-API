package com.example.PAP_API.services;

import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppraisalParticipantService {

    private final AppraisalParticipantRepository participantRepo;

    public AppraisalParticipantService(AppraisalParticipantRepository participantRepo) {
        this.participantRepo = participantRepo;
    }

    public Optional<AppraisalParticipant> getCurrentAppraisalForEmployee(Long id) {
        Optional<List<AppraisalParticipant>> activeAppraisals = participantRepo.findActiveAppraisalsByEmployeeId(id);

        // Return the most recent active one if exists
        if (!activeAppraisals.isEmpty()) {
            return Optional.of(activeAppraisals.get().get(0)); // already ordered DESC in query
        }

        // Otherwise, return latest closed one
        return participantRepo.findLatestAppraisalByEmployeeId(id);
    }

}
