package com.example.PAP_API.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    private String teamIdNumber;

    private String teamName;
    private String projectTitle;
    private String teamLeadEmployeeId;
    private String managerEmployeeId;

    @Temporal(TemporalType.DATE)
    private Date effectiveStartDate;

    @ManyToOne
    @JoinColumn(name = "hr_manager_id")
    private HRManager hrManager;

    @ElementCollection
    @CollectionTable(name = "team_employees", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "employee_id")
    private List<String> employeesIds; // store employee IDs as strings
}
