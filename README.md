🏥 Community Health Medical System
A modern Spring Boot medical management system for community health centers with complete patient, staff, appointment, and medical records management.

🌟 Key Features
🔐 Secure Authentication
JWT-based security with role-based access control

Four user roles: DOCTOR, NURSE, TECHNICIAN, ADMINISTRATOR

Protected API endpoints with proper authorization

👥 Core Modules
Patient Management - Complete profiles and medical history

Medical Staff - Role-based staff management with departments

Appointment System - Scheduling with conflict detection

Medical Records - Secure diagnosis and prescription tracking

Department Management - Organized staff assignment

🛠 Tech Stack
Backend: Java 17 • Spring Boot 3 • Spring Security • JPA/Hibernate • MySQL
Security:• Role-based Access
Tools: Maven • Lombok • Jakarta Validation

👥 User Roles & Access
Role	Permissions
DOCTOR	Full medical access + prescriptions
NURSE	Patient care + basic operations
TECHNICIAN	Lab tests + technical procedures
ADMIN	System management + user control

💡 Key Entities
MedicalRecord
Diagnosis, prescription, notes tracking

Linked to Patient and MedicalStaff

Date-based record keeping

Appointment
Status tracking (SCHEDULED/COMPLETED/CANCELLED)

Patient-Staff scheduling with time slots

Conflict prevention

MedicalStaff
Specialized roles and departments

License number validation

Appointment management

🎯 Business Logic
Duplicate prevention - No duplicate medical records per patient/date

Authorization checks - Staff can only modify their own records

Validation - Comprehensive input validation on all DTOs

Error handling - Custom exceptions for better error messages

📊 Sample Data Flow
Patient registers → System creates profile

Staff member logs in → JWT token issued

Schedule appointment → Availability checked

Create medical record → Authorization validated

Access records → Role-based filtering applied

Built with ❤️ by @evsebiu
