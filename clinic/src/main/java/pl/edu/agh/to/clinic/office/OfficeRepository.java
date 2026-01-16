package pl.edu.agh.to.clinic.office;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    public boolean existsByRoomNumber(int roomNumber);
}
