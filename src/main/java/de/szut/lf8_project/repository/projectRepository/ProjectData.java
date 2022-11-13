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
    @Column(name = "project_id", insertable = false, updatable = false)
    Long projectId;
    String projectName;
    String projectDescription;
    Long projectLeadId;
    Long customerId;
    String customerContact;
    LocalDate startDate;
    LocalDate plannedEndDate;
    LocalDate actualEndDate;

    @OneToMany(mappedBy = "projectData" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<TeamMemberData> teamMembers;
}
