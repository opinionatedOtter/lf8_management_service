package de.szut.lf8_project.repository.projectRepository;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Table(name = "project")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long projectId;
    String projectName;
    String projectDescription;
    Long projectLeadId;
    Long customerId;
    String customerContact;
    LocalDate startDate;
    LocalDate plannedEndDate;
    LocalDate actualEndDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    Set<TeamMemberData> teamMembers;
}
