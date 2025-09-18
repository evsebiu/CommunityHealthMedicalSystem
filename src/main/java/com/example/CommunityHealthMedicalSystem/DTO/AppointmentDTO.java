package com.example.CommunityHealthMedicalSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long medicalStaffId;
    private String status;
    private LocalDateTime appointmentDateTime;

}
