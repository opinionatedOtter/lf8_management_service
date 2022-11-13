package de.szut.lf8_project.repository.projectRepository;


import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "team_member")
@IdClass(TeamMemberCompositeKey.class)
public class TeamMemberData {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectData projectData;

    @Id
    private Long employeeId;

    private String role;

}