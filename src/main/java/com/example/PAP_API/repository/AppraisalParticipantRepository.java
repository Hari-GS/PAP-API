package com.example.PAP_API.repository;

import com.example.PAP_API.enums.Statuses;
import com.example.PAP_API.model.AppraisalParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppraisalParticipantRepository extends JpaRepository<AppraisalParticipant, Long> {
    List<AppraisalParticipant> findByEmployeeId(String employeeId);

    List<AppraisalParticipant> findAllById(Long id);

    Optional<AppraisalParticipant> findByAppraisalIdAndEmployeeId(Long appraisalId, String empId);

    List<AppraisalParticipant> findByAppraisalIdAndReportingPersonId(Long appraisalId, Long reportingPersonId);

    Integer countByAppraisalIdAndReportingPersonId(Long appraisalId, Long reportingPersonId);

    Optional<AppraisalParticipant> findByAppraisalIdAndReportingPersonIdAndId(
            Long appraisalId, Long reportingPersonId, Long id);

    List<AppraisalParticipant> findAllByAppraisalId(Long appraisalId);

    @Query("SELECT COUNT(p) FROM AppraisalParticipant p WHERE p.appraisal.id = :appraisalId")
    long countByAppraisalId(@Param("appraisalId") Long appraisalId);

    @Query("SELECT COUNT(p) FROM AppraisalParticipant p WHERE p.appraisal.id = :appraisalId AND p.selfAppraisalStatus = :status")
    long countSelfReviewsCompleted(@Param("appraisalId") Long appraisalId, @Param("status") Statuses status);

    @Query("SELECT COUNT(p) FROM AppraisalParticipant p WHERE p.appraisal.id = :appraisalId AND p.reviewAppraisalStatus = :status")
    long countManagerReviewsCompleted(@Param("appraisalId") Long appraisalId, @Param("status") Statuses status);

    Integer countByAppraisalIdAndReportingPersonIdAndReviewAppraisalStatus(
            Long appraisalId,
            Long reportingPersonId,
            Statuses reviewAppraisalStatus
    );


    // ✅ New method: count participants that actually have a reporting person
    @Query("SELECT COUNT(p) FROM AppraisalParticipant p WHERE p.appraisal.id = :appraisalId AND p.reportingPerson IS NOT NULL")
    long countParticipantsWithReportingPerson(@Param("appraisalId") Long appraisalId);

    // Find the active appraisal for the employee
    @Query("""
    SELECT ap 
    FROM AppraisalParticipant ap
    WHERE ap.participant.id = :id
      AND ap.appraisal.stage <> 'CLOSED'
    ORDER BY ap.appraisal.startDate DESC, ap.appraisal.id DESC
    """)
    Optional<List<AppraisalParticipant>> findActiveAppraisalsByEmployeeId(@Param("id") Long id);


    // Find the most recent appraisal if no active one
    @Query("SELECT ap FROM AppraisalParticipant ap " +
            "WHERE ap.participant.id = :id " +
            "ORDER BY ap.appraisal.endDate DESC LIMIT 1")
    Optional<AppraisalParticipant> findLatestAppraisalByEmployeeId(Long id);

    List<AppraisalParticipant> findByParticipantId(Long id);

    @Query("SELECT ap FROM AppraisalParticipant ap WHERE ap.participant.id = :id AND ap.appraisal.stage = 'CLOSED'")
    List<AppraisalParticipant> findClosedAppraisalsByEmployeeId(Long id);

    @Query("SELECT ap FROM AppraisalParticipant ap WHERE ap.participant.id = :id AND ap.appraisal.stage <> 'CLOSED'")
    List<AppraisalParticipant> findActiveAppraisals(Long id);

}