package com.example.CommunityHealthMedicalSystem.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(unique = true)
    private String licenseNumber;

    public enum Role{
        DOCTOR, TECHNICIAN, NURSE, ADMINISTRATOR
    }

    @OneToMany(mappedBy = "medicalStaff", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
