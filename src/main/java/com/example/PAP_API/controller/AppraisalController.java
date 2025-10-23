package com.example.PAP_API.controller;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.mappers.AppraisalMapper;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.repository.AppraisalRepository;
import com.example.PAP_API.services.AppraisalService;
import com.example.PAP_API.services.UserContextService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appraisals")
public class AppraisalController {

    @Autowired
    private AppraisalService appraisalService;

    @Autowired
    private AppraisalMapper appraisalMapper;

    @Autowired
    UserContextService userContextService;

    @Autowired
    AppraisalRepository appraisalRepo;

    @PostMapping
    public ResponseEntity<String> createAppraisal(@RequestBody AppraisalDto dto) {
        Appraisal saved = appraisalService.saveAppraisal(dto,userContextService.getCurrentUserId());
        return ResponseEntity.ok("Appraisal created with ID: " + saved.getId());
    }

    @GetMapping
    public ResponseEntity<List<AppraisalDto>> getAllAppraisals() {
        List<Appraisal> appraisals = appraisalService.getAllAppraisals(userContextService.getCurrentUserId());
        List<AppraisalDto> dtoList = appraisalMapper.toDtoList(appraisals);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppraisalDto> getAppraisalById(@PathVariable Long id) {
        AppraisalDto dto = appraisalService.getAppraisalById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/force-move")
    public ResponseEntity<String> forceMoveToNextStage(@PathVariable Long id) {
        Long hrId = userContextService.getCurrentUserId();
        Appraisal updated = appraisalService.moveToNextStage(id, hrId);
        return ResponseEntity.ok("Appraisal forcibly moved to stage: " + updated.getStage());
    }

    @GetMapping("/recent")
    public ResponseEntity<AppraisalDto> getMostRecentAppraisal() {
        Long hrId = userContextService.getCurrentUserId();
        Appraisal recent = appraisalRepo.findTopByHrManagerIdOrderByCreatedAtDescIdDesc(hrId)
                .orElseThrow(() -> new EntityNotFoundException("No recent appraisals found"));
        return ResponseEntity.ok(appraisalMapper.toDto(recent));
    }

}
