package com.group7.sis.facultyModule.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeConfigurationDTO {
    private Long facultyId;
    
    @NotNull(message = "Please enter assignment weight")
    private Integer assignmentWeight;

    @NotNull(message = "Please enter quiz weight")
    private Integer quizWeight;

    @NotNull(message = "Please enter activity weight")
    private Integer activityWeight;

    @NotNull(message = "Please enter final exam weight")
    private Integer finalExamWeight;
}
