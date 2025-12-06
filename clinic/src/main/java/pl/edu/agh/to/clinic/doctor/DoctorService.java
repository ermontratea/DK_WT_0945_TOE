package pl.edu.agh.to.clinic.doctor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor addDoctor(Doctor doctor) {
        if (doctorRepository.existsByPesel(doctor.getPesel())) {
            throw new IllegalArgumentException("Doctor with this pesel already exists");
        }
            return doctorRepository.save(doctor);
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with id: " + id + " not found"));
    }
    public void deleteDoctorById(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor with id: " + id + " not found");
        }
        doctorRepository.deleteById(id);
    }
}
