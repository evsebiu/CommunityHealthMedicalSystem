package com.example.CommunityHealthMedicalSystem.Security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable()) // for testing (enable for production)
                .authorizeHttpRequests(authz-> authz
                        .requestMatchers("/api/patients/**").permitAll() // allow patients to acces their own
        // endpoints


        //for MedicalStaff only:
                        .requestMatchers("/medicalStaff/**").hasAnyRole("DOCTOR", "TECHNICIAN","NURSE",
                                "ADMINISTRATOR")
                        .requestMatchers("/api/medical-records/**").hasAnyRole("DOCTOR", "TECHNICIAN",
                                "NURSE", "ADMINISTRATOR")
                        .requestMatchers("/api/appointments/**").hasAnyRole("DOCTOR", "TECHNICIAN",
                                "NURSE", "ADMINISTRATOR")

                        //admin only endpoints:
                        .requestMatchers("/api/departments/**").hasRole("ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); //

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
