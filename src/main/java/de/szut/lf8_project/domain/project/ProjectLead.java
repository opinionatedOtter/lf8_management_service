package de.szut.lf8_project.domain.project;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class ProjectLead {

    ProjectLeadId projectLeadId;

    public ProjectLead(ProjectLeadId projectLeadId) {
        this.projectLeadId = projectLeadId;
    }
}
