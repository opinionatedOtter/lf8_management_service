package de.szut.lf8_project.repository.projectRepository;


import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "team_member")
public class TeamMemberData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long randomId;
    private Long employeeId;
    private String role;

}