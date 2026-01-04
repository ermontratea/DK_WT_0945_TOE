package pl.edu.agh.to.clinic.office;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;

import java.util.List;

@Service
public class OfficeService {
    private final OfficeRepository officeRepository;
    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    /**
     * Adds a new doctor's office to the system.
     * Checks if an office with the given room number already exists in the database.
     * If so, {@link RoomNumberDuplicationException} is thrown.
     *
     * @param office   office object to be added
     * @return          the saved office object
     * @throws          RoomNumberDuplicationException if an office with the given room number already exists
     */
    public Office addOffice(Office office) throws RoomNumberDuplicationException {
        if (officeRepository.existsByRoomNumber(office.getRoomNumber())) {
            throw new RoomNumberDuplicationException(office.getRoomNumber());
        }
        return officeRepository.save(office);
    }

    /**
     * Retrieves all offices from the system.
     * @return  a list of all saved offices
     */
    public List<Office> getOffices() {
        return officeRepository.findAll();
    }

    /**
     * Retrieves an office by its ID
     * If no office with a given ID exists {@link OfficeNotFoundException} is thrown.
     *
     * @param id    the ID of the office to retrieve
     * @return      the office with the specified ID
     * @throws      OfficeNotFoundException if no office with the given ID is found
     */
    public Office getOfficeById(Long id) throws OfficeNotFoundException {
        return officeRepository.findById(id)
                .orElseThrow(() -> new OfficeNotFoundException(id));
    }

    /**
     * Deletes office by its ID
     * Checks if the office exists and if it has any assigned duties.
     * If the office has duties, {@link IllegalStateException} is thrown to prevent data inconsistency.
     * @param id    the ID of the office to delete
     * @throws      OfficeNotFoundException if no office with the given ID is found
     * @throws      IllegalStateException if the office has assigned duties
     */
    public void deleteOfficeById(Long id) throws OfficeNotFoundException {
        Office office = officeRepository.findById(id).orElseThrow(() -> new OfficeNotFoundException(id));
        if (office.getDuties()!=null && !office.getDuties().isEmpty()) {
            throw new IllegalStateException("You can't delete an office with assigned duties ");
        }
        officeRepository.delete(office);
    }
}
