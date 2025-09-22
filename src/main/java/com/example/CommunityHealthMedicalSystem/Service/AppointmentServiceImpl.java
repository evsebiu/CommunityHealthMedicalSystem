package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.AppointmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.*;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.AppointmentRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    private final AppointmentRepository appointmentRepo;
    @Autowired
    private final PatientRepository patientRepo;
    @Autowired
    private final MedicalStaffRepository medicalStaffRepo;

    private AppointmentService appointmentService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepo, PatientRepository patientRepo,
                                  MedicalStaffRepository medicalStaffRepo){
        this.appointmentRepo = appointmentRepo;
        this.patientRepo=patientRepo;
        this.medicalStaffRepo=medicalStaffRepo;
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
    public List<Appointment> getAppointmentByPatientId(Long patientId){
        return appointmentRepo.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentByMedicalStaff(MedicalStaff medicalStaff){
        return appointmentRepo.findByMedicalStaff(medicalStaff);
    }

    @Override
    public List<Appointment> getAppointmentByMedicalId(Long medicalId){
        return appointmentRepo.findByMedicalId(medicalId);
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
    public Appointment createAppointment(AppointmentDTO appointmentDTO){

        // - Used Deepseek external Ai to improve this method.

        if (appointmentDTO == null){
          throw new IllegalArgumentException("Appointment cannot be null");
      }
      if (appointmentDTO.getMedicalStaffId() == null){
          throw new IllegalArgumentException("Medical staff cannot be null");
      }
      if (appointmentDTO.getPatientId() == null){
          throw new IllegalArgumentException("Patient cannot be null");
      }

      Patient patient = patientRepo.findById(appointmentDTO.getPatientId())
              .orElseThrow(()-> new ResourceNotFound("Patient not found with id: " +
                      appointmentDTO.getPatientId()));

      MedicalStaff medicalStaff = medicalStaffRepo.findById(appointmentDTO.getMedicalStaffId())
              .orElseThrow(()-> new ResourceNotFound("Medical staff not found with id: " +
                      appointmentDTO.getMedicalStaffId()));

      Optional<Appointment> existing = appointmentRepo.findByMedicalStaffAndDateTime(medicalStaff,
              appointmentDTO.getAppointmentDateTime());

      if (existing.isPresent()){
          throw new DuplicateResourceException("Appointment already exists at this time for this medical staff.");
      }

      Optional<Appointment> patientConflict= appointmentRepo.findByPatientDateAndTime(patient,
              appointmentDTO.getAppointmentDateTime());

      if (patientConflict.isPresent()){
          throw new ConflictException("Patient already have appointment at this time.");
      }

      //convert dto to entity
        Appointment appointment = new Appointment();
      appointment.setPatient(patient);
      appointment.setMedicalStaff(medicalStaff);
      appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
      appointment.setReason(appointmentDTO.getReason());
      appointment.setStatus(appointmentDTO.getStatus());

      Appointment savedAppointment = appointmentRepo.save(appointment);
        System.out.println("Appointment created with id: " + savedAppointment.getId());
      return savedAppointment;
    }

    @Override
    public void  deleteAppointment(Long id, Patient patient, MedicalStaff medicalStaff){


        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Appointment with id " + id + " not found."));


        if (!appointment.getPatient().getId().equals(patient.getId())){
            throw new SecurityException("You do not have permission to delete this appointment.");
        }

        // optional validation : check if medical staff is associated with appointment.
        if (!appointment.getMedicalStaff().getId().equals(medicalStaff.getId())){
            throw new SecurityException("Medical Staff does not have permission for this operation.");
        }

        appointmentRepo.deleteById(id);
        System.out.println("Appointment #" + id + " deleted for patient " + patient.getFirstName() +
                " " + patient.getLastName());
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment appointmentDetails, Patient patient, MedicalStaff medicalStaff){
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment with id #" + id + " not found."));

        // - Used DeepSeek external Ai to add Security checks.
        if (!appointment.getPatient().getId().equals(patient.getId())){
            throw new SecurityException("Patient does not have permission to update appointment.");
        }

        if (!appointment.getMedicalStaff().getId().equals(medicalStaff.getId())){
            throw new SecurityException("Medical staff does not have permission to update appointment.");
        }

        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setNotes(appointmentDetails.getNotes());
        appointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());
        appointment.setDepartment(appointmentDetails.getDepartment());

        return appointmentRepo.save(appointment);
    }

    // dto test
    public AppointmentDTO toDTO(Appointment appointment){
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setMedicalStaffId(appointment.getMedicalStaff().getId());
        dto.setStatus(appointment.getStatus());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        dto.setAppointmentDuration(appointment.getAppointmentDuration());

        if (appointment.getDepartment() !=null){
            dto.setDepartmentId(appointment.getDepartment().getId());
        }
       return dto;
    }
}
