package com.example.CommunityHealthMedicalSystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medical_staff")

public class MedicalStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(unique = true)
    @Email
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "License number is required.")
    @Column(unique = true)
    private String licenseNumber;

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    public enum Role{
        DOCTOR, TECHNICIAN, NURSE, ADMINISTRATOR
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required.")
    private Role role;

    public boolean canPerformMedicalOperations() {
        return role == Role.DOCTOR || role == Role.NURSE || role == Role.TECHNICIAN || role == Role.ADMINISTRATOR;
    }

    @OneToMany(mappedBy = "medicalStaff", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
