package com.example.PAP_API.model;

import com.example.PAP_API.enums.Statuses;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class AppraisalParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;
    private String employeeName;
    private String designation;
    private String managerName;
    private Statuses selfAppraisalStatus= Statuses.NOT_STARTED;
    private Long totalQns;
    private Integer totalQnsAnswered=0;

    @ManyToOne
    @JoinColumn(name = "appraisal_id")
    private Appraisal appraisal;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppraisalQuestion> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "reporting_person_id")
    private NewEmployee reportingPerson;
}
