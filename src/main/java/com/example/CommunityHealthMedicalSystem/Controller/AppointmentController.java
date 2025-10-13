package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.AppointmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Service.AppointmentServiceImpl;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private final AppointmentServiceImpl appointmentService;

    public AppointmentController(AppointmentServiceImpl appointmentService){
        this.appointmentService=appointmentService;
    }

    @GetMapping
    public List<Appointment> getAllAppointments(){
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{reason}")
    public ResponseEntity<List<Appointment>> getAppointmentByReason(@PathVariable String reason){
        List<Appointment> appointment = appointmentService.getAppointmentByReason(reason);
        if (appointment.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentByStatus(@PathVariable Appointment.Status status){
        List<Appointment> appointments = appointmentService.getAppointmentByStatus(status);
        if (appointments.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentByPatientId(@PathVariable Long patientId){
        List<Appointment> appointments = appointmentService.getAppointmentByPatientId(patientId);
        if (appointments.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-medical-id/{medicalId}")
    public ResponseEntity<List<Appointment>> getAppointmentByMedicalId(@PathVariable Long medicalId){
        List<Appointment> appointments = appointmentService.getAppointmentByMedicalId(medicalId);
        if (appointments.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-department-id/{departmentId}")
    public ResponseEntity<List<Appointment>> getAppointmentByDepartmentId(@PathVariable Long departmentId){
        List<Appointment> appointments = appointmentService.getAppointmentByDepartmentId(departmentId);
        if (appointments.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<Appointment>> getAppointmentByDateRange(
            @RequestParam("startDate")LocalDateTime startDate,
            @RequestParam("endDate")LocalDateTime endDate
            ){
        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(startDate,endDate);
        return ResponseEntity.ok(appointments);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id){
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO){
        try {
            AppointmentDTO savedAppointment = appointmentService.createAppointment(appointmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
        } catch (DuplicateResourceException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Appointment>> searchAppointment(@RequestParam(required = false) String reason )


}
