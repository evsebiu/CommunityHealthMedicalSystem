package com.example.CommunityHealthMedicalSystem.DTO;

import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MedicalStaffDTO {
    private Long id;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @Email(message = "E-mail should be valid.")
    @NotBlank(message = "E-mail is required.")
    private String email;

    @NotBlank(message = "License number is unique and required.")
    private String licenseNumber;

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    @NotNull(message = "Role is required.")
    private MedicalStaff.Role role;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
