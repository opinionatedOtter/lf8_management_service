package de.szut.lf8_project.repository.projectRepository;


import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "team_member")
public class TeamMemberData {

    @Id
    private Long employeeId;
    private String role;

}