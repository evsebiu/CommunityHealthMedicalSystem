package com.example.CommunityHealthMedicalSystem.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable()) // for testing (enable for production)
                .authorizeHttpRequests(authz-> authz
                        // Patient endpoints - more restrictive
                        .requestMatchers(HttpMethod.GET, "/api/patients/search").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/patients/{id}").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/patients").permitAll() // Allow registration
                        .requestMatchers(HttpMethod.PUT, "/api/patients/{id}").hasAnyRole("PATIENT",
                                "ADMINISTRATOR") // Patients can update own info
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("ADMINISTRATOR")

                        // Appointment endpoints - patients can only access their own
                        .requestMatchers(HttpMethod.GET, "/api/appointments/patient/{patientId}").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/**").hasAnyRole("DOCTOR",
                                "NURSE", "ADMINISTRATOR") // Staff can see all
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/{id}").hasAnyRole("PATIENT",
                                "DOCTOR", "NURSE", "ADMINISTRATOR")

                        // Medical staff operations - patients cannot access
                        .requestMatchers("/medicalStaff/**").hasAnyRole("DOCTOR", "NURSE", "ADMINISTRATOR")
                        .requestMatchers("/api/medical-records/**").hasAnyRole("DOCTOR", "NURSE", "ADMINISTRATOR")

                        // Admin only
                        .requestMatchers("/api/departments/**").hasRole("ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //TEST IN-MEMORY (REPLACE WITH DB for production)
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails patient = User.builder()
                .username("patient")
                .password(passwordEncoder().encode("password"))
                .roles("PATIENT")
                .build();

        UserDetails doctor  = User.builder()
                .username("doctor")
                .password(passwordEncoder().encode("password"))
                .roles("DOCTOR")
                .build();

        UserDetails nurse = User.builder()
                .username("nurse")
                .password(passwordEncoder().encode("password"))
                .roles("NURSE")
                .build();

        UserDetails technician = User.builder()
                .username("technician")
                .password(passwordEncoder().encode("password"))
                .roles("TECHNICIAN")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMINISTRATOR")
                .build();

        return new InMemoryUserDetailsManager(patient, doctor, nurse, technician, admin);
    }



}
