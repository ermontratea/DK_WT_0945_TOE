package pl.edu.agh.to.clinic.duty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.LocalDateTime;

@Entity
public class Duty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "office_id")
    @NotNull(message = "Office is required")
    private Office office;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    public Duty() {}
    public Duty(Doctor doctor, Office office, LocalDateTime startTime, LocalDateTime endTime) {
        this.doctor = doctor;
        this.office = office;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Doctor getDoctor() {
        return doctor;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public Office getOffice() {
        return office;
    }
    public Long getId() {return id;}
}
