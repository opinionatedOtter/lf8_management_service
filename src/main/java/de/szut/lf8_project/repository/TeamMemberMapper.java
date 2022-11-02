package de.szut.lf8_project.repository;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.Qualification;
import de.szut.lf8_project.domain.project.TeamMember;
import de.szut.lf8_project.repository.projectRepository.TeamMemberData;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;

@Component
public class TeamMemberMapper {

    public TeamMember mapTo(TeamMemberData teamMemberData){

        TeamMember teamMember = new TeamMember();
        teamMember.setQualification(new Qualification(teamMemberData.getRole()));
        teamMember.setEmployeeId(new EmployeeId(teamMemberData.getEmployeeId()));

        return teamMember;

    }

    public TeamMemberData mapTo(TeamMember teamMember){
        
        TeamMemberData teamMemberData = new TeamMemberData();
        teamMemberData.setRole(teamMember.getQualification().unbox());
        teamMemberData.setEmployeeId(teamMember.getEmployeeId().unbox());

        return teamMemberData;
    }
}
