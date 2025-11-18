package com.example.PAP_API.controller;

import com.example.PAP_API.dto.AppraisalQuestionDto;
import com.example.PAP_API.dto.ReportingPersonDto;
import com.example.PAP_API.dto.SelfAppraisalAnswerDto;
import com.example.PAP_API.dto.UserDto;
import com.example.PAP_API.model.SelfAppraisalAnswer;
import com.example.PAP_API.services.SelfAppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SelfAppraisalController {

    @Autowired
    SelfAppraisalService selfAppraisalService;

    @PostMapping("/self-appraisal/{appraisalId}/submit")
    public ResponseEntity<String> submitAnswers(@PathVariable Long appraisalId, @RequestBody List<SelfAppraisalAnswerDto> answersDto, @AuthenticationPrincipal UserDto participant) {

        selfAppraisalService.saveAnswers(appraisalId, participant, answersDto);
        return ResponseEntity.ok("Self appraisal submitted successfully.");
    }

    @GetMapping("/self-appraisal/{appraisalId}/questions")
    public ResponseEntity<List<AppraisalQuestionDto>> getQuestionsForEmployeeAppraisal(@PathVariable Long appraisalId, @AuthenticationPrincipal UserDto participant) {

        List<AppraisalQuestionDto> questions = selfAppraisalService.getSelfAppraisalQuestions(appraisalId, participant);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/self-appraisal/{appraisalId}/answers")
    public List<SelfAppraisalAnswerDto> getSavedAnswers(@PathVariable Long appraisalId, @AuthenticationPrincipal UserDto participant) {
        return selfAppraisalService.getAnswersByParticipant(appraisalId, participant);
    }

    //Endpoint for reporting person to add comments
    @PutMapping("/self-appraisal/reporting-person-comment")
    public ResponseEntity<List<SelfAppraisalAnswerDto>> addReportingPersonComment(@RequestBody List<ReportingPersonDto> dtos){
        return ResponseEntity.ok(selfAppraisalService.addReportingPersonComment(dtos));
    }
}
