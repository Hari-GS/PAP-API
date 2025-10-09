package com.example.PAP_API.controller;

import com.example.PAP_API.dto.AppraisalParticipantDto;
import com.example.PAP_API.dto.AppraisalParticipantSummaryDto;
import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.mappers.AppraisalParticipantMapper;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.services.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @Autowired
    private AppraisalParticipantMapper appraisalParticipantMapper;

    @GetMapping("/{appraisalId}")
    public ResponseEntity<List<AppraisalParticipantDto>> getParticipantsForReview(
            @PathVariable Long appraisalId,
            @AuthenticationPrincipal UserDto userDetails) {

        Long reportingPersonId = userDetails.getId(); // from JWT
        List<AppraisalParticipant> participants =
                reportingService.getParticipantsForReporting(appraisalId, reportingPersonId);

        List<AppraisalParticipantDto> dtos = appraisalParticipantMapper.toDto(participants);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/summary/{appraisalId}")
    public ResponseEntity<List<AppraisalParticipantSummaryDto>> getParticipantsSummaryForReview(
            @PathVariable Long appraisalId,
            @AuthenticationPrincipal UserDto userDetails) {

        Long reportingPersonId = userDetails.getId(); // from JWT
        List<AppraisalParticipant> participants =
                reportingService.getParticipantsForReporting(appraisalId, reportingPersonId);

        List<AppraisalParticipantSummaryDto> dtos = appraisalParticipantMapper.toSummaryDto(participants);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{appraisalId}/{id}")
    public ResponseEntity<AppraisalParticipantDto> getParticipantDetails(
            @PathVariable Long appraisalId,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDto userDetails) {

        Long reportingPersonId = userDetails.getId();

        AppraisalParticipant participant = reportingService.getParticipantDetailsForReporting(
                appraisalId, reportingPersonId, id);

        AppraisalParticipantDto dto = appraisalParticipantMapper.toDto(participant);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/self-appraisal/{appraisalId}/participants/{employeeId}/answers")
    public ResponseEntity<List<SelfAppraisalAnswerDto>> getParticipantAnswersForReview(
            @PathVariable Long appraisalId,
            @PathVariable String employeeId,
            @AuthenticationPrincipal UserDto reportingPerson) {

        // reportingPerson.getId() -> used for authorization
        List<SelfAppraisalAnswerDto> answers =
                reportingService.getAnswersForParticipant(appraisalId, employeeId, reportingPerson.getId());

        return ResponseEntity.ok(answers);
    }


}
