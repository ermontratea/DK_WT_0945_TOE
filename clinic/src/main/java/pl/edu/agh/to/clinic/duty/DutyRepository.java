package pl.edu.agh.to.clinic.duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;

import java.time.LocalDateTime;

@Repository
public interface DutyRepository extends JpaRepository<Duty,Long> {
    boolean existsByDoctorAndStartTimeBeforeAndEndTimeAfter(Doctor doctor, LocalDateTime endTime, LocalDateTime startTime);

    boolean existsByOfficeAndStartTimeBeforeAndEndTimeAfter(Office office, LocalDateTime endTime, LocalDateTime startTime);

}
