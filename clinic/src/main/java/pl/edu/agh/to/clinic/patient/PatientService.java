package pl.edu.agh.to.clinic.patient;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.appointment.AppointmentRepository;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Adds a new patient to the system.
     * Checks if a patient with the given PESEL already exists in the database.
     * If so, {@link PeselDuplicationException} is thrown.
     *
     * @param dto       patient data to be added
     * @return          the saved patient as dto
     * @throws          PeselDuplicationException if a patient with the given pesel already exists
     */
    public PatientDto addPatient(PatientDto dto) throws PeselDuplicationException {
        if (patientRepository.existsByPesel(dto.getPesel())) {
            throw new PeselDuplicationException(dto.getPesel());
        }

        Patient patient = new Patient(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPesel(),
                dto.getAddress());

        Patient saved = patientRepository.save(patient);
        return new PatientDto(saved);
    }

    /**
     * Retrieves all patients from the system.
     * @return  a list of all saved patients as DTOs
     */
    public List<PatientListDto> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientListDto::new)
                .toList();
    }

    /**
     * Retrieves a patient by their ID
     * If no patient with a given ID exists {@link PatientNotFoundException} is thrown.
     *
     * @param id    the ID of the patient to retrieve
     * @return      the patient with the specified ID as DTO
     * @throws      PatientNotFoundException if no patient with the given ID is found
     */
    public PatientListDto getPatientById(Long id) throws PatientNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        return new PatientListDto(patient);
    }

    /**
     * Deletes patient by their ID
     * If no patient with the given ID exists, {@link PatientNotFoundException} is thrown.
     * Checks if the patient has any booked appointments.
     * @param id    the ID of the patient to delete
     * @throws      PatientNotFoundException if no patient with the given ID is found
     * @throws      IllegalStateException if the patient has booked appointments
     */
    public void deletePatientById(Long id) throws PatientNotFoundException {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        if (appointmentRepository.existsByPatientId(id)) {
            throw new IllegalStateException("Cannot delete patient with existing appointments");
        }
        patientRepository.deleteById(id);
    }
}
