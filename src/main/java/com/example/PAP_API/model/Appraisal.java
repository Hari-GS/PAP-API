package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.Data;
import com.example.PAP_API.enums.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type;
    private LocalDate startDate;
    private LocalDate selfAppraisalEndDate;
    private LocalDate endDate;

    @Lob
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private Stage stage = Stage.CREATED; // Default stage

    @OneToMany(mappedBy = "appraisal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppraisalParticipant> participants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_manager_id", nullable = false)
    private HRManager hrManager;

    private LocalDate createdAt;
}
