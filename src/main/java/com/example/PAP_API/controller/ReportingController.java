package com.example.PAP_API.controller;

import com.example.PAP_API.dto.*;
import com.example.PAP_API.mappers.AppraisalParticipantMapper;
import com.example.PAP_API.model.AppraisalParticipant;
import com.example.PAP_API.services.ReportPdfService;
import com.example.PAP_API.services.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @Autowired
    private AppraisalParticipantMapper appraisalParticipantMapper;

    @Autowired
    private ReportPdfService pdfService;

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

    @GetMapping("/{appraisalId}/participants")
    public ResponseEntity<List<AppraisalParticipantDto>> getAppraisalParticipants(@PathVariable Long appraisalId) {
        List<AppraisalParticipant> participants = reportingService.getParticipantsByAppraisal(appraisalId);
        return ResponseEntity.ok(appraisalParticipantMapper.toDto(participants));
    }

    @GetMapping("/participant/{participantId}")
    public ParticipantReportDto getParticipantReport(@PathVariable Long participantId) {
        return reportingService.getParticipantReport(participantId);
    }

    @GetMapping("/participant/{participantId}/download")
    public ResponseEntity<byte[]> downloadParticipantReport(@PathVariable Long participantId) {
        // 1️⃣ Get report data (from existing method)
        ParticipantReportDto reportDto = reportingService.getParticipantReport(participantId);

        // 2️⃣ Generate PDF
        ByteArrayInputStream pdfStream = pdfService.generateParticipantReport(reportDto);

        // 3️⃣ Return as downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AppraisalReport_" + reportDto.getEmployeeName() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    @GetMapping("/participant/{participantId}/download-styled")
    public ResponseEntity<byte[]> downloadStyledReport(@PathVariable Long participantId) {
        ParticipantReportDto reportDto = reportingService.getParticipantReport(participantId);
        ByteArrayInputStream pdfStream = pdfService.generateParticipantReportStyled(reportDto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AppraisalReport_" + reportDto.getEmployeeName() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

}
