package com.example.PAP_API.services;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.enums.Stage;
import com.example.PAP_API.mappers.AppraisalMapper;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.model.AppraisalQuestion;
import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.repository.AppraisalRepository;
import com.example.PAP_API.repository.HRManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppraisalService {

    @Autowired
    private AppraisalRepository appraisalRepository;

    @Autowired
    private AppraisalMapper appraisalMapper;

    @Autowired
    HRManagerRepository hrManagerRepository;

    public Appraisal saveAppraisal(AppraisalDto dto, Long hrId) {
        Appraisal appraisal = appraisalMapper.toEntity(dto);

        // Set HR manager for this appraisal
        appraisal.setHrManager(hrManagerRepository.findById(hrId).orElseThrow(() -> new RuntimeException("HRManager not found")));

        // Set back-references for participants and questions
        if (appraisal.getParticipants() != null) {
            for (AppraisalParticipant participant : appraisal.getParticipants()) {
                participant.setAppraisal(appraisal);
                participant.setTotalQns(participant.getQuestions().stream().count());
                if (participant.getQuestions() != null) {
                    for (AppraisalQuestion question : participant.getQuestions()) {
                        question.setId(null); // ✅ Critical line
                        question.setParticipant(participant);
                    }
                }
            }
        }

        return appraisalRepository.save(appraisal);
    }


    public List<Appraisal> getAllAppraisals(Long hrId) {
        return appraisalRepository.findByHrManagerId(hrId);
    }

    public Optional<AppraisalDto> getAppraisalById(Long id, Long hrId) {
        return appraisalRepository.findByIdAndHrManagerId(id, hrId)
                .map(appraisalMapper::toDto);
    }

    public Appraisal moveToNextStage(Long appraisalId, Long hrId) {
        Appraisal appraisal = appraisalRepository.findByIdAndHrManagerId(appraisalId, hrId)
                .orElseThrow(() -> new RuntimeException("Appraisal not found for this HR"));

        Stage currentStage = appraisal.getStage();
        Stage nextStage = getNextStage(currentStage);

        if (nextStage == null) {
            throw new RuntimeException("Appraisal is already in the final stage.");
        }

        appraisal.setStage(nextStage);
        return appraisalRepository.save(appraisal);
    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case CREATED: return Stage.SELF_REVIEW;
            case SELF_REVIEW: return Stage.REPORTING_REVIEW;
            case REPORTING_REVIEW: return Stage.HR_REVIEW;
            case HR_REVIEW: return Stage.CLOSED;
            case CLOSED: return null;
            default: return null;
        }
    }

}
