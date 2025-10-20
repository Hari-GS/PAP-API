package com.example.PAP_API.services;

import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.repository.AppraisalParticipantRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AppraisalParticipantService {

    private final AppraisalParticipantRepository participantRepo;

    public AppraisalParticipantService(AppraisalParticipantRepository participantRepo) {
        this.participantRepo = participantRepo;
    }

    public Optional<AppraisalParticipant> getCurrentAppraisalForEmployee(Long id) {
        Optional<AppraisalParticipant> active = participantRepo.findActiveAppraisalByEmployeeId(id);
        if (active.isPresent()) {
            return active;
        }
        return participantRepo.findLatestAppraisalByEmployeeId(id);
    }
}
