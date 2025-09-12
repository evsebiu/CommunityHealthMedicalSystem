package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Exception.*;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    private final AppointmentRepository appointmentRepo;

    private AppointmentService appointmentService;

    AppointmentServiceImpl(AppointmentService appointmentService, AppointmentRepository appointmentRepo){
        this.appointmentService=appointmentService;
        this.appointmentRepo = appointmentRepo;
    }

    @Override
    public List<Appointment> getAllAppointments(){
        return appointmentRepo.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id){
        return appointmentRepo.findById(id);
    }

    @Override
    public List<Appointment> getAppointmentByReason(String reason){
        return appointmentRepo.findByReason(reason);
    }

    @Override
    public List<Appointment> getAppointmentByStatus(Appointment.Status status){
        return appointmentRepo.findByStatus(status);
    }

    @Override
    public List<Appointment> getAppointmentByPatient(Patient patient){
        return appointmentRepo.findByPatient(patient);
    }

    @Override
    public List<Appointment> getAppointmentByMedicalStaff(MedicalStaff medicalStaff){
        return appointmentRepo.findByMedicalStaff(medicalStaff);
    }

    @Override
    public List<Appointment> getAppointmentByDepartmentId(Long departmentId){
        return appointmentRepo.findByDepartmentId(departmentId);
    }

    @Override
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate){
        return appointmentRepo.findByAppointmentDateTimeBetween(startDate, endDate);
    }

    @Override
    public Appointment createAppointment(Appointment appointment, MedicalStaff medicalStaff, Patient patient){

        // - Used Deepseek external Ai to improve this method.

        if (appointment == null){
          throw new IllegalArgumentException("Appointment cannot be null");
      }
      if (medicalStaff == null){
          throw new IllegalArgumentException("Medical staff cannot be null");
      }
      if (patient == null){
          throw new IllegalArgumentException("Patient cannot be null");
      }

      appointment.setMedicalStaff(medicalStaff);
      appointment.setPatient(patient);

      Optional<Appointment> existing = appointmentRepo.findByMedicalStaffAndDateTime(medicalStaff, appointment.getAppointmentDateTime());

      if (existing.isPresent()){
          throw new DuplicateResourceException("Appointment already exists at this time for this medical staff.");
      }

      Optional<Appointment> patientConflict= appointmentRepo.findByPatiendDateAndTime(patient,
              appointment.getAppointmentDateTime());

      if (patientConflict.isPresent()){
          throw new ConflictException("Patient already have appointment at this time.");
      }

      Appointment savedAppointment = appointmentRepo.save(appointment);
        System.out.println("Appointment created with id: " + savedAppointment.getId());
      return savedAppointment;
    }

    @Override
    public void  deleteAppointment(Long id, Patient patient, MedicalStaff medicalStaff){

        // - Used Deepseek external Ai to finish this method.

        // first step :  find the appointment
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Appointment with id " + id + " not found."));

        // second step: Validate that the logged-in patient and medical staff are the
        // ones associated with this appointment.
        if (!appointment.getPatient().getId().equals(patient.getId())){
            throw new SecurityException("You do not have permission to delete this appointment.");
        }

        // optional validation : check if medical staff is associated with appointment.
        if (!appointment.getMedicalStaff().getId().equals(medicalStaff.getId())){
            throw new SecurityException("Medical Staff does not have permission for this operation.");
        }

        appointmentRepo.deleteById(id);
        System.out.println("Appointment #" + id + " deleted for patinet " + patient.getFirstName() +
                " " + patient.getLastName());
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment appointmentDetails){
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment with id #" + id + " not found."));
        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setNotes(appointmentDetails.getNotes());
        appointment.setMedicalStaff(appointmentDetails.getMedicalStaff());
        appointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());
        appointment.setDepartment(appointmentDetails.getDepartment());
        appointment.setPatient(appointmentDetails.getPatient());
        appointment.setId(appointmentDetails.getId());

        return appointmentRepo.save(appointment);
    }
}
