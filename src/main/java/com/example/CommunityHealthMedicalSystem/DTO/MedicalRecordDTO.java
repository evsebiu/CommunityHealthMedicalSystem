package com.example.CommunityHealthMedicalSystem.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MedicalRecordDTO {

    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Medical Staff ID is required.")
    private Long medicalStaffId;

    @Size(max = 1850, message = "Diagnosis cannot exceed 1.850 characters.")
    @NotBlank(message = "Patient diagnosis is required.")
    private String diagnosis;

    @NotBlank(message = "Patient prescription is required.")
    @Size(max = 1000, message = "Prescription cannot exceed 1.000 characters.")
    private String prescription;

    @NotBlank(message = "Patient notes are required.")
    @Size(max = 500 , message = "Notes cannot exceed 500 characters.")
    private String notes;

    @PastOrPresent(message = "Record date cannot be in the future.")
    @NotNull(message = "Patient record date is a required field.")
    private LocalDate recordDate;
}
