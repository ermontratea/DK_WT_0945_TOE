package pl.edu.agh.to.clinic.patient;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

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
     * @param patient    patient object to be added
     * @return          the saved patient object
     * @throws          PeselDuplicationException if a doctor with the given pesel already exists
     */
    public Patient addPatient(Patient patient) throws PeselDuplicationException {
        if (patientRepository.existsByPesel(patient.getPesel())) {
            throw new PeselDuplicationException(patient.getPesel());
        }
        return patientRepository.save(patient);
    }

    /**
     * Retrieves all patients from the system.
     * @return  a list of all saved patients
     */
    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieves a patient by their ID
     * If no patient with a given ID exists {@link PatientNotFoundException} is thrown.
     *
     * @param id    the ID of the patient to retrieve
     * @return      the patient with the specified ID
     * @throws      PatientNotFoundException if no patient with the given ID is found
     */
    public Patient getPatientById(Long id) throws PatientNotFoundException {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
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
