package pl.edu.agh.to.clinic.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    boolean existsByPesel(String pesel);
}
