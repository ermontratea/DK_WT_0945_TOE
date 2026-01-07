package pl.edu.agh.to.clinic.duty;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DutyDto {
    private Long id;

    @NotNull(message = "Doctor Id is required")
    private Long doctorId;

    @NotNull(message = "Office Id is required")
    private Long officeId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    public DutyDto() {}

    public DutyDto(Duty duty) {
        this.id = duty.getId();
        this.doctorId = duty.getDoctor().getId();
        this.officeId = duty.getOffice().getId();
        this.startTime = duty.getStartTime();
        this.endTime = duty.getEndTime();
    }

    public Long getId() {return id;}
    public Long getDoctorId() {return doctorId;}
    public Long getOfficeId() {return officeId;}
    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}

    public void setId(Long id) {this.id = id;}
    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public void setOfficeId(Long officeId) {this.officeId = officeId;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}
}
