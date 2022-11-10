package de.szut.lf8_project.repository;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.TeamMember;
import de.szut.lf8_project.repository.projectRepository.TeamMemberData;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberMapper {

    public TeamMember mapTo(TeamMemberData teamMemberData) {

        return new TeamMember(new EmployeeId(teamMemberData.getEmployeeId()), new ProjectRole(teamMemberData.getRole()));

    }

    public TeamMemberData mapTo(TeamMember teamMember) {

        TeamMemberData teamMemberData = new TeamMemberData();
        teamMemberData.setRole(teamMember.getProjectRole().unbox());
        teamMemberData.setEmployeeId(teamMember.getEmployeeId().unbox());

        return teamMemberData;
    }
}
