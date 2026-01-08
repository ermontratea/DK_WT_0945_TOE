package pl.edu.agh.to.clinic.duty;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DutyDto {
    private Long id;

    @NotNull(message = "Doctor Id is required")
    private Long doctorId;

    @NotNull(message = "Office Id is required")
    private Long officeId;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    public DutyDto() {}

    public DutyDto(Duty duty) {
        this.id = duty.getId();
        this.doctorId = duty.getDoctor().getId();
        this.officeId = duty.getOffice().getId();
        this.startTime = duty.getStartTime();
        this.endTime = duty.getEndTime();
        this.dayOfWeek = duty.getDayOfWeek();
    }

    public Long getId() {return id;}
    public Long getDoctorId() {return doctorId;}
    public Long getOfficeId() {return officeId;}
    public LocalTime getStartTime() {return startTime;}
    public LocalTime getEndTime() {return endTime;}
    public DayOfWeek getDayOfWeek() {return dayOfWeek;}

    public void setId(Long id) {this.id = id;}
    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public void setOfficeId(Long officeId) {this.officeId = officeId;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}
    public void setEndTime(LocalTime endTime) {this.endTime = endTime;}
    public void setDayOfWeek(DayOfWeek dayOfWeek) {this.dayOfWeek = dayOfWeek;}
}
