package com.example.CommunityHealthMedicalSystem.Repository;


import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatus(Appointment.Status status);
    List<Appointment> findByReason(String reason);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByMedicalStaff(MedicalStaff medicalStaff);
    List<Appointment> findAll();
    List<Appointment> findByDepartmentId(Long departmentId);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndAppointmentDateTimeBetween(Long patientId, LocalDate startDate, LocalDate endDate);
    List<Appointment> findByMedicalStaffIdAndAppointmentDateTimeBetween(Long staffId, LocalDate startDate, LocalDate endDate);
}
