package de.szut.lf8_project.repository.projectRepository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Table(name = "project")
@Entity
@Getter
@Setter
@Builder
public class ProjectData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long projectId;
    String projectName;
    String projectDescription;
    Long projectLeadId;
    Long customerId;
    Long customerContactId;
    Date startDate;
    Date plannedEndDate;
    Date actualEndDate;

    @OneToMany(mappedBy = "projectData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<TeamMemberData> teamMembers;

    public ProjectData() {

    }
}
