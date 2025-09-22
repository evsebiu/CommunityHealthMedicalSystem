package com.example.CommunityHealthMedicalSystem.DTO;

import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class AppointmentDTO {
    private Long id;

    @NotNull(message = "Patient ID is required.")
    private Long patientId;

    @NotNull(message = "Doctor ID is required.")
    private Long medicalStaffId;

    @Pattern(regexp = "SCHEDULED|COMPLETED|CANCELLED", message = "Invalid status.")
    private Appointment.Status status;

    @Future(message = "Appointment have to be in future.")
    @NotNull(message = "Appointment date is required.")
    private LocalDateTime appointmentDateTime;

    private Long departmentId;

    @NotBlank(message = "Reason is required.")
    @Size(max = 500, message = "Reason cannot exceed 500 characters.")
    private String reason;

    private String notes;

}
