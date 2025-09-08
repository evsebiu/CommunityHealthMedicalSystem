package com.example.CommunityHealthMedicalSystem.Service;


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
    List<Appointment> getAppointmentByDeparmentId(Long departmentId);
    List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    //business logic
    Appointment createAppointment(Appointment appointment);
    void deleteAppointment(Long id);
    Appointment updateAppointment(Long id, Appointment appointmentDetails);

    //validation methods

    boolean isAppointmentSlotAvailable(MedicalStaff medicalStaff, LocalDateTime appointmentTime);
    boolean hasPatientExistingAppointment(Patient patient, LocalDateTime date);

    List<Appointment> getAppointmentByDepartmentId(Long departmentId);
}
