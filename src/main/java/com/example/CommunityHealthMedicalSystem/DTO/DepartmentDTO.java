package com.example.CommunityHealthMedicalSystem.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DepartmentDTO {

    private Long id;


    @NotBlank(message = "Name for department is required.")
    private String name;

    @NotNull(message = "Description is required.")
    private String description;

    private Integer activeAppointmentsCount;
    private Integer medicalStaffCount;
}
