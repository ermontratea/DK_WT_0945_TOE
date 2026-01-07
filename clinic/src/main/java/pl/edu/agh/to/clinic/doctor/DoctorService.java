package pl.edu.agh.to.clinic.doctor;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
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
     * If so, {@link PeselDuplicationException} is thrown.
     *
     * @param dto    doctor data to be added
     * @return          the saved doctor as DTO
     * @throws          PeselDuplicationException if a doctor with the given PESEL already exists
     */
    public DoctorDto addDoctor(DoctorDto dto) throws PeselDuplicationException {
        if (doctorRepository.existsByPesel(dto.getPesel())) {
            throw new PeselDuplicationException(dto.getPesel());
        }

        Doctor doctor = new Doctor(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPesel(),
                dto.getSpecialization(),
                dto.getAddress()
        );
        Doctor saved = doctorRepository.save(doctor);
        return new DoctorDto(saved);
    }

    /**
     * Retrieves all doctors from the system.
     * @return  a list of all saved doctors as DTOs
     */
    public List<DoctorDto> getDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorDto::new)
                .toList();
    }

    /**
     * Retrieves a doctor by their ID
     * If no doctor with a given ID exists {@link DoctorNotFoundException} is thrown.
     *
     * @param id    the ID of the doctor to retrieve
     * @return      the doctor with the specified ID as DTO
     * @throws      DoctorNotFoundException if no doctor with the given ID is found
     */
    public DoctorDto getDoctorById(Long id) throws DoctorNotFoundException {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
        return new DoctorDto(doctor);
    }

    /**
     * Deletes doctor by their ID
     * If no doctor with the given ID exists, {@link DoctorNotFoundException} is thrown.
     * If doctor with given ID has assigned duties, {@link IllegalStateException} is thrown.
     * @param id    the ID of the doctor to delete
     * @throws      DoctorNotFoundException if no doctor with the given ID is found
     * @throws      IllegalStateException if the doctor has assigned duties
     */
    public void deleteDoctorById(Long id) throws DoctorNotFoundException {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
        if (doctor.getDuties()!=null && !doctor.getDuties().isEmpty())
        {
            throw new IllegalStateException("You can't delete a doctor with assigned duties ");
        }
        doctorRepository.deleteById(id);
    }
}
