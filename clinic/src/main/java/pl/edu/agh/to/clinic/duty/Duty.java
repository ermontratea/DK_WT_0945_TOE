package pl.edu.agh.to.clinic.duty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.common.Views;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.LocalDateTime;

@Entity
public class Duty {
    @JsonView({Views.Public.class})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonView({Views.Public.class, Views.Internal.class})
    @JsonIgnoreProperties({"duties", "handler", "hibernateLazyInitializer"})
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "office_id")
    @JsonView({Views.Public.class, Views.Internal.class})
    @JsonIgnoreProperties({"duties", "handler", "hibernateLazyInitializer"})
    @NotNull(message = "Office is required")
    private Office office;

    @NotNull(message = "Start time is required")
    @JsonView(Views.Public.class)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @JsonView(Views.Public.class)
    private LocalDateTime endTime;

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
