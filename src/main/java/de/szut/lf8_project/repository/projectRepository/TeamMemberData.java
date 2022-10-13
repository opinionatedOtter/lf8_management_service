package de.szut.lf8_project.repository.projectRepository;


import javax.persistence.Table;

@Table(name = "teamMembers")
public record TeamMemberData(
        Long employeeId,
        String role
        ){
}