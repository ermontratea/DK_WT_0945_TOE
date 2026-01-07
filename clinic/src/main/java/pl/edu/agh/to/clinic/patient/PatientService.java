package pl.edu.agh.to.clinic.patient;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;
import pl.edu.agh.to.clinic.patient.PatientDto;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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
    public List<PatientDto> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientDto::new)
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
    public PatientDto getPatientById(Long id) throws PatientNotFoundException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        return new PatientDto(patient);
    }

    /**
     * Deletes patient by their ID
     * If no patient with the given ID exists, {@link PatientNotFoundException} is thrown.
     * @param id    the ID of the patient to delete
     * @throws      PatientNotFoundException if no patient with the given ID is found
     */
    public void deletePatientById(Long id) throws PatientNotFoundException {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }
}
