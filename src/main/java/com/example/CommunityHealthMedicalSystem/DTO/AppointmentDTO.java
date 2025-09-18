package com.example.CommunityHealthMedicalSystem.DTO;

import com.example.CommunityHealthMedicalSystem.Model.Appointment;
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
    private Appointment.Status status;
    private LocalDateTime appointmentDateTime;
    private Long departmentId;
    private String reason;
    private String notes;

}
