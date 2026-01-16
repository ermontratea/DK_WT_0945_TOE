package pl.edu.agh.to.clinic.duty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;


    public Duty() {
    }

    public Duty(Doctor doctor, Office office, DayOfWeek dayOfWeek,LocalTime startTime, LocalTime endTime) {
        this.doctor = doctor;
        this.office = office;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public DayOfWeek getDayOfWeek() {return dayOfWeek;}

    public Office getOffice() {
        return office;
    }

    public Long getId() {
        return id;
    }

}
