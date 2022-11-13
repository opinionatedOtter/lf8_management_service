package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Value
public class TeamMember {
    @NotNull EmployeeId employeeId;
    @NotNull ProjectRole projectRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(employeeId, that.employeeId);
    }
}
