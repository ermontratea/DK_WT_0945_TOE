package pl.edu.agh.to.clinic.duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Repository
public interface DutyRepository extends JpaRepository<Duty,Long> {
    boolean existsByDoctorAndStartTimeBeforeAndEndTimeAfter(Doctor doctor, LocalTime endTime, LocalTime startTime);

    boolean existsByOfficeAndStartTimeBeforeAndEndTimeAfter(Office office, LocalTime endTime, LocalTime startTime);

}
