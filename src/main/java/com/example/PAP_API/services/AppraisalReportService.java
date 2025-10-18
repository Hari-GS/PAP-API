package com.example.PAP_API.services;

import com.example.PAP_API.enums.Stage;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.repository.AppraisalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppraisalReportService {

    private final AppraisalRepository appraisalRepository;

    public AppraisalReportService(AppraisalRepository appraisalRepository) {
        this.appraisalRepository = appraisalRepository;
    }

    public List<Appraisal> getClosedAppraisalsByHr(Long hrManagerId) {
        return appraisalRepository.findByHrManagerIdAndStage(hrManagerId, Stage.CLOSED);
    }
}
