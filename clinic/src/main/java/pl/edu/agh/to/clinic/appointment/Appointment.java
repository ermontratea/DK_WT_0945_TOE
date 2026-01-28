package pl.edu.agh.to.clinic.appointment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.patient.Patient;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "Office is required")
    private Office office;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    public Appointment() {
    }

    public Appointment(Patient patient, Doctor doctor, Office office, LocalDate date,LocalTime startTime, LocalTime endTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.office = office;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    public Patient getPatient() {return patient;}

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDate getDate() {return date;}

    public Office getOffice() {
        return office;
    }

    public Long getId() {
        return id;
    }

    public void setPatient(Patient patient) { this.patient = patient; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setOffice(Office office) { this.office = office; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

}
