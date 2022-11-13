package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.customer.Customer;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Builder(toBuilder = true)
@Value
public class Project {
    @NonNull
    Optional<ProjectId> projectId;
    @NonNull
    ProjectName projectName;
    @NonNull
    Optional<ProjectDescription> projectDescription;
    @NonNull
    ProjectLead projectLead;
    @NonNull
    Customer customer;
    @NonNull
    CustomerContact customerContact;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;
    @NonNull
    Optional<ActualEndDate> actualEndDate;
    @NonNull
    @Builder.Default
    Set<TeamMember> teamMembers = Collections.emptySet();

    public Optional<RelevantEndDate> getRelevantEndDate(){
        return RelevantEndDate.of(plannedEndDate, actualEndDate);
    }

    public Optional<ProjectTimespan> getProjectTimespan(){
     return ProjectTimespan.of(this.getStartDate(), this.getRelevantEndDate());
    }
}