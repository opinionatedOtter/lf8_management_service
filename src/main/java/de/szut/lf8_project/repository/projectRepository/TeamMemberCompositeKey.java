package de.szut.lf8_project.repository.projectRepository;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamMemberCompositeKey implements Serializable {

    private ProjectData projectData;

    private Long employeeId;
}
