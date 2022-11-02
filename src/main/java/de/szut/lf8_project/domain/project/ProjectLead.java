package de.szut.lf8_project.domain.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.szut.lf8_project.common.ValueTypeSerializer;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@JsonSerialize(using = ValueTypeSerializer.class)
public class ProjectLead {

    private final ProjectLeadId projectLeadId;

    public ProjectLead(ProjectLeadId projectLeadId) {
        this.projectLeadId = projectLeadId;
    }
}
