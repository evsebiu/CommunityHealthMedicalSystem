package com.example.CommunityHealthMedicalSystem.Service;


import com.example.CommunityHealthMedicalSystem.DTO.AppointmentDTO;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface AppointmentService {

    //get methods
    List<Appointment> getAllAppointments();
    Optional<Appointment> getAppointmentById(Long id);
    List<Appointment> getAppointmentByReason(String reason);
    List<Appointment> getAppointmentByStatus(Appointment.Status status);
    List<Appointment> getAppointmentByPatient(Patient patient);
    List<Appointment> getAppointmentByMedicalStaff(MedicalStaff medicalStaff);
    List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Appointment> getAppointmentByPatientId(Long patientId);
    List<Appointment> getAppointmentByMedicalId(Long medicalId);


    //business logic
    Appointment createAppointment(Appointment appointment, MedicalStaff medicalStaff, Patient patient);
    void deleteAppointment(Long id,Patient patient, MedicalStaff medicalStaff);
    Appointment updateAppointment(Long id, Appointment appointmentDetails, Patient patient, MedicalStaff medicalStaff);


    //validation methods

    List<Appointment> getAppointmentByDepartmentId(Long departmentId);
}
