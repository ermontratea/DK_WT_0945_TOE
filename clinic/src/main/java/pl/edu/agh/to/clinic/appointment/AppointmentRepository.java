package pl.edu.agh.to.clinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.patient.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorAndDateAndStartTimeBeforeAndEndTimeAfter(Doctor doctor, LocalDate date, LocalTime endTime, LocalTime startTime);

    boolean existsByPatientAndDateAndStartTimeBeforeAndEndTimeAfter(Patient patient, LocalDate date, LocalTime endTime, LocalTime startTime);

    boolean existsByDoctorAndOfficeAndDateAndStartTimeBeforeAndEndTimeAfter(
            Doctor doctor,
            Office office,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );

    Collection<Appointment> findAllByDoctorAndOffice(Doctor doctor, Office office);
}
