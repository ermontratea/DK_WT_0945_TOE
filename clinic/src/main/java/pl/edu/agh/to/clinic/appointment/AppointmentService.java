package pl.edu.agh.to.clinic.appointment;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.duty.DutyRepository;
import pl.edu.agh.to.clinic.exceptions.*;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;
import pl.edu.agh.to.clinic.patient.Patient;
import pl.edu.agh.to.clinic.patient.PatientRepository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final PatientRepository patientRepository;
    private final DutyRepository dutyRepository;
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              OfficeRepository officeRepository,
                              PatientRepository patientRepository,
                              DutyRepository dutyRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
        this.patientRepository = patientRepository;
        this.dutyRepository = dutyRepository;
    }

    /**
     * Adds a new appointment.
     * Checks if doctor has a duty at this office and is not busy with other appointments in the given time range.
     * Checks if patient does not have any other appointments in the given time range.
     *
     * @param dto data of the appointment to be added
     * @return    saved appointment as DTO
     * @throws DoctorNotFoundException if doctor with given id does not exist
     * @throws PatientNotFoundException if patient with given id does not exist
     * @throws OfficeNotFoundException if office with given id does not exist
     * @throws IllegalStateException   if doctor or patient is already busy in this time range
     */
    public AppointmentDto addAppointment(AppointmentDto dto) {

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time!");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(dto.getDoctorId()));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(dto.getPatientId()));

        Office office = officeRepository.findById(dto.getOfficeId())
                .orElseThrow(() -> new OfficeNotFoundException(dto.getOfficeId()));

        DayOfWeek dayOfWeek = dto.getDate().getDayOfWeek();

        boolean hasDuty = dutyRepository.existsByDoctorAndOfficeAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                doctor,
                office,
                dayOfWeek,
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!hasDuty) {
            throw new IllegalStateException("Doctor does not have a duty at this office at this time");
        }

        boolean doctorBusy = appointmentRepository
                .existsByDoctorAndOfficeAndDateAndStartTimeBeforeAndEndTimeAfter(
                        doctor,
                        office,
                        dto.getDate(),
                        dto.getEndTime(),
                        dto.getStartTime()
                );

        if (doctorBusy) {
            throw new IllegalStateException("Doctor already has an appointment at this time");
        }

        boolean patientBusy = appointmentRepository
                .existsByPatientAndDateAndStartTimeBeforeAndEndTimeAfter(
                        patient,
                        dto.getDate(),
                        dto.getEndTime(),
                        dto.getStartTime()
                );

        if (patientBusy) {
            throw new IllegalStateException("Patient already has an appointment at this time");
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                office,
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        Appointment saved = appointmentRepository.save(appointment);
        return new AppointmentDto(saved);
    }


    /**
     * Returns all appointments as DTOs.
     * @return  a list of all saved appointments as DTOs
     */
    public List<AppointmentDto> getAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentDto::new)
                .toList();
    }

    /**
     * Returns appointment by id as DTO.
     *
     * @param id id of the appointment to retrieve
     * @return  appointment with the given id as DTO
     * @throws AppointmentNotFoundException if duty with the given id is not found
     */
    public AppointmentDto getAppointmentById(Long id) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        return new AppointmentDto(appointment);
    }

    /**
     * Deletes appointment by id.
     *
     * @param id id of the appointment to delete
     * @throws  AppointmentNotFoundException if duty with the given id is not found
     */
    public void deleteAppointmentById(Long id) throws AppointmentNotFoundException {
        if(!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException(id);
        }
        appointmentRepository.deleteById(id);
    }
}
