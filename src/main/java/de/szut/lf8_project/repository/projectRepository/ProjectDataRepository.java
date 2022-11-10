package de.szut.lf8_project.repository.projectRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectDataRepository extends JpaRepository<ProjectData, Long> {

    @Query(nativeQuery = true, value = "Select * from project join team_member on project.project_id = team_member.project_id WHERE employee_id = :teamMemberId")
    List<ProjectData> findAllByTeamMemberId(Long teamMemberId);
}
