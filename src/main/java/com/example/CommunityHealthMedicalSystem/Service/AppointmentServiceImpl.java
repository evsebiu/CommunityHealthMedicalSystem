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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_PATIENT"))){

            // Use findByEmail (returns Optional<Patient>) instead of findByEmailContainingIgnoreCase
            Patient currentPatient = patientRepo.findByEmail(authentication.getName())
                    .orElseThrow(() -> new SecurityException("Patient not found"));

            if (!currentPatient.getId().equals(patientId)){
                throw new SecurityException("Patient can access only their own appointments.");
            }
        }

        return appointmentRepo.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentByMedicalStaff(MedicalStaff medicalStaff){
        return appointmentRepo.findByMedicalStaff(medicalStaff);
    }

    @Override
    public List<Appointment> getAppointmentByMedicalId(Long medicalId){
        return appointmentRepo.findByMedicalStaffId(medicalId);
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
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO){

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

      Optional<Appointment> existing = appointmentRepo.findByMedicalStaffAndAppointmentDateTime(medicalStaff,
              appointmentDTO.getAppointmentDateTime());

      if (existing.isPresent()){
          throw new DuplicateResourceException("Appointment already exists at this time for this medical staff.");
      }

      Optional<Appointment> patientConflict= appointmentRepo.findByPatientAndAppointmentDateTime(patient,
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

      if (appointmentDTO.getStatus() != null) {
          try {
              appointment.setStatus(appointmentDTO.getStatus());
          } catch (IllegalArgumentException e) {
              appointment.setStatus(Appointment.Status.SCHEDULED);
          }
      } else{
          appointment.setStatus(Appointment.Status.SCHEDULED);
          }

      // save and return to convertToDTO
      Appointment savedAppointment = appointmentRepo.save(appointment);
        System.out.println("Appointment created with id: " + savedAppointment.getId());

      return convertToDTO(savedAppointment);
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
    public AppointmentDTO updateAppointment(Long id,AppointmentDTO appointmentDTO){

        // 1. find existing appointment.
        Appointment existingAppointment = appointmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment with id #" + id + " not found."));

        //2. validate input
        if (appointmentDTO == null){
            throw new IllegalArgumentException("AppointmentDTO cannot be null.");
        }

        // 3. update field from DTO (only non-null fields)
        if (appointmentDTO.getAppointmentDateTime() !=null){
            if (!existingAppointment.getAppointmentDateTime().equals(appointmentDTO.getAppointmentDateTime())){

                MedicalStaff medicalStaff = existingAppointment.getMedicalStaff();
                if (appointmentDTO.getMedicalStaffId() != null ){
                    medicalStaff= medicalStaffRepo.findById(appointmentDTO.getMedicalStaffId())
                            .orElse(existingAppointment.getMedicalStaff());
                }
                existingAppointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());

                Patient patient  = existingAppointment.getPatient();
                if (appointmentDTO.getPatientId() !=null ){
                    patient = patientRepo.findById(appointmentDTO.getPatientId())
                            .orElse(existingAppointment.getPatient());
                }

                //staff conflict check
                Optional<Appointment> staffConflict  = appointmentRepo
                        .findByMedicalStaffAndAppointmentDateTime(medicalStaff, appointmentDTO.getAppointmentDateTime());
                if (staffConflict.isPresent() && !staffConflict.get().getId().equals(id)){
                    throw new ConflictException("Medical staff already has appointment at this time.");
                }

            }
        }
        if (appointmentDTO.getReason() != null){
            existingAppointment.setReason(appointmentDTO.getReason());
        }

        if (appointmentDTO.getStatus() != null){
            try{
                existingAppointment.setStatus(appointmentDTO.getStatus());
            } catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid status : " + appointmentDTO.getStatus());
            }
        }
        //handle realtionship updates.
        if (appointmentDTO.getPatientId() != null){
            Patient patient = patientRepo.findById(appointmentDTO.getPatientId())
                    .orElseThrow(()-> new ResourceNotFound("Patient not found."));
            existingAppointment.setPatient(patient);
        }
        if (appointmentDTO.getMedicalStaffId() != null){
            MedicalStaff medicalStaff = medicalStaffRepo.findById(appointmentDTO.getMedicalStaffId())
                    .orElseThrow(()-> new ResourceNotFound("Medical staff not found."));
            existingAppointment.setMedicalStaff(medicalStaff);
        }

        //save and return DTO

        Appointment updatedAppointment = appointmentRepo.save(existingAppointment);
        return convertToDTO(updatedAppointment);
    }

    // dto test
    public AppointmentDTO convertToDTO(Appointment appointment){
        AppointmentDTO dto = new AppointmentDTO();

        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setMedicalStaffId(appointment.getMedicalStaff().getId());
        dto.setStatus(Appointment.Status.valueOf(appointment.getStatus().name()));
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
