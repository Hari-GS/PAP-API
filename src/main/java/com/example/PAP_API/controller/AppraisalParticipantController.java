package com.example.PAP_API.controller;

import com.example.PAP_API.dto.AppraisalDto;
import com.example.PAP_API.dto.EmployeeAppraisalSummaryDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.mappers.AppraisalMapper;
import com.example.PAP_API.model.Appraisal;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.repository.AppraisalRepository;
import com.example.PAP_API.repository.NewEmployeeRepository;
import com.example.PAP_API.services.AppraisalParticipantService;
import com.example.PAP_API.services.AppraisalService;
import com.example.PAP_API.services.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/appraisals")
public class AppraisalParticipantController {

    private final AppraisalParticipantService participantService;

    @Autowired
    UserContextService userContextService;

    @Autowired
    NewEmployeeRepository newEmployeeRepository;

    @Autowired
    AppraisalMapper appraisalMapper;

    @Autowired
    AppraisalRepository appraisalRepository;

    public AppraisalParticipantController(AppraisalParticipantService participantService) {
        this.participantService = participantService;
    }

//    @GetMapping("/current-participant")
//    public ResponseEntity<EmployeeAppraisalSummaryDto> getCurrentAppraisalForLoggedInEmployee() {
//        // ✅ If you use JWT, replace the above line with your actual way of getting userId/email.
//        // For example:
//        // String employeeId = jwtService.getLoggedInEmployeeId();
//
//        return  participantService.getCurrentAppraisalForEmployee(newEmployeeRepository.findById(userContextService.getCurrentUserId()).get().getEmployeeId())
//                .map(ap -> {
//                    Map<String, Object> response = Map.of(
//                            "title", ap.getAppraisal().getTitle(),
//                            "deadline", ap.getAppraisal().getEndDate(),
//                            "status", ap.getSelfAppraisalStatus().name(),
//                            "id",ap.getAppraisal().getId()
//                    );
//                    return ResponseEntity.ok(response);
//                })
//                .orElse(ResponseEntity.noContent().build());
//    }

    @GetMapping("/current-participant")
    public ResponseEntity<EmployeeAppraisalSummaryDto> getCurrentAppraisalForLoggedInEmployee() {
        Optional<AppraisalParticipant> appraisalParticipant = participantService.getCurrentAppraisalForEmployee(newEmployeeRepository.findById(userContextService.getCurrentUserId()).get().getEmployeeId());
        Appraisal appraisal = appraisalParticipant.get().getAppraisal();

        EmployeeAppraisalSummaryDto dto = new EmployeeAppraisalSummaryDto();
        dto.setTitle(appraisal.getTitle());
        dto.setSelfAppraisalEndDate(appraisal.getSelfAppraisalEndDate().toString());
        dto.setStatus(appraisalParticipant.get().getSelfAppraisalStatus().toString());
        dto.setAppraisalId(appraisal.getId());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/employee")
    public ResponseEntity<AppraisalDto> getAppraisal(@PathVariable Long id){
       Appraisal appraisal = appraisalRepository.findById(id).get();

       AppraisalDto dto = new AppraisalDto();
       dto.setTitle(appraisal.getTitle());
       dto.setSelfAppraisalEndDate(appraisal.getSelfAppraisalEndDate().toString());

       return ResponseEntity.ok(dto);
    }
}
