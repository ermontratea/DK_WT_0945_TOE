package pl.edu.agh.to.clinic.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPesel(String pesel);
}
