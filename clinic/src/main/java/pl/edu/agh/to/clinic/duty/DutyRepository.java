package pl.edu.agh.to.clinic.duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Repository
public interface DutyRepository extends JpaRepository<Duty,Long> {
    boolean existsByDoctorAndDayOfWeekAndStartTimeBeforeAndEndTimeAfter(Doctor doctor, DayOfWeek dayOfWeek, LocalTime endTime, LocalTime startTime);

    boolean existsByOfficeAndDayOfWeekAndStartTimeBeforeAndEndTimeAfter(Office office, DayOfWeek dayOfWeek, LocalTime endTime, LocalTime startTime);

    boolean existsByDoctorAndOfficeAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Doctor doctor, Office office, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
