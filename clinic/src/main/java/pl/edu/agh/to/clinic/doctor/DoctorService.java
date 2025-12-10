package pl.edu.agh.to.clinic.doctor;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Adds a new doctor to the system.
     * Checks if a doctor with the given PESEL already exists in the database.
     * If so, {@link IllegalArgumentException} is thrown.
     *
     * @param doctor    doctor object to be added
     * @return          the saved doctor object
     * @throws PeselDuplicationException     if a doctor with the given pesel already exists
     */
    public Doctor addDoctor(Doctor doctor) throws PeselDuplicationException {
        if (doctorRepository.existsByPesel(doctor.getPesel())) {
            throw new PeselDuplicationException(doctor.getPesel());
        }
            return doctorRepository.save(doctor);
    }

    /**
     * Retrieves all doctors from the system.
     * @return  a list of all saved doctors
     */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Retrieves a doctor by their ID
     * If no doctor with a given ID exists {@link IllegalArgumentException} is thrown.
     *
     * @param id    the ID of the doctor to retrieve
     * @return      the doctor with the specified ID
     * @throws IllegalArgumentException if no doctor with the given ID is found
     */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with id: " + id + " not found"));
    }

    /**
     * Deletes doctor by their ID
     * If no doctor with the given ID exists, {@link IllegalArgumentException} is thrown.
     * @param id    the ID of the doctor to delete
     * @throws      IllegalArgumentException if no doctor with the given ID is found
     */
    public void deleteDoctorById(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor with id: " + id + " not found");
        }
        doctorRepository.deleteById(id);
    }
}
