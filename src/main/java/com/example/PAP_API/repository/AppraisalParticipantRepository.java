package com.example.PAP_API.repository;

import com.example.PAP_API.model.AppraisalParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppraisalParticipantRepository extends JpaRepository<AppraisalParticipant, Long> {
    List<AppraisalParticipant> findByEmployeeId(String employeeId);

    List<AppraisalParticipant> findAllById(Long id);

    Optional<AppraisalParticipant> findByAppraisalIdAndEmployeeId(Long appraisalId, String empId);

    List<AppraisalParticipant> findByAppraisalIdAndReportingPersonId(Long appraisalId, Long reportingPersonId);

    Optional<AppraisalParticipant> findByAppraisalIdAndReportingPersonIdAndId(
            Long appraisalId, Long reportingPersonId, Long id);

}