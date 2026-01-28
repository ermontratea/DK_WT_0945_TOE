package pl.edu.agh.to.clinic.appointment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDto {
    private Long id;

    @NotNull(message = "Patient Id is required")
    private Long patientId;

    @NotNull(message = "Doctor Id is required")
    private Long doctorId;

    @NotNull(message = "Office Id is required")
    private Long officeId;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Date is required")
    private LocalDate date;

    public AppointmentDto() {}

    public AppointmentDto(Appointment appointment) {
        this.id = appointment.getId();
        this.patientId = appointment.getPatient().getId();
        this.doctorId = appointment.getDoctor().getId();
        this.officeId = appointment.getOffice().getId();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
        this.date = appointment.getDate();
    }

    public Long getId() {return id;}
    public Long getPatientId() {return patientId;}
    public Long getDoctorId() {return doctorId;}
    public Long getOfficeId() {return officeId;}
    public LocalTime getStartTime() {return startTime;}
    public LocalTime getEndTime() {return endTime;}
    public LocalDate getDate() {return date;}

    public void setId(Long id) {this.id = id;}
    public void setPatientId(Long patientId) {this.patientId = patientId;}
    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public void setOfficeId(Long officeId) {this.officeId = officeId;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}
    public void setEndTime(LocalTime endTime) {this.endTime = endTime;}
    public void setDate(LocalDate date) {this.date= date;}
}
