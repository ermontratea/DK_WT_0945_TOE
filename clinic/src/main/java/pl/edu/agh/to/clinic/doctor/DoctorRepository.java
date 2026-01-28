package pl.edu.agh.to.clinic.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    boolean existsByPesel(String pesel);
    List<Doctor> findBySpecialization(Specialization specialization);

}
