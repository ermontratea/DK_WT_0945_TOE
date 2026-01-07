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
     * @param dto   office data to be added
     * @return          the saved office as DTO
     * @throws          RoomNumberDuplicationException if an office with the given room number already exists
     */
    public OfficeDto addOffice(OfficeDto dto) throws RoomNumberDuplicationException {
        if (officeRepository.existsByRoomNumber(dto.getRoomNumber())) {
            throw new RoomNumberDuplicationException(dto.getRoomNumber());
        }
        Office office = new Office(dto.getRoomNumber());
        Office saved = officeRepository.save(office);
        return new OfficeDto(saved);
    }

    /**
     * Retrieves all offices from the system as DTOs.
     * @return  a list of all saved offices as DTOs
     */
    public List<OfficeDto> getOffices() {
        return officeRepository.findAll()
                .stream()
                .map(OfficeDto::new)
                .toList();
    }

    /**
     * Retrieves an office by its ID as DTO
     * If no office with a given ID exists {@link OfficeNotFoundException} is thrown.
     *
     * @param id    the ID of the office to retrieve
     * @return      the office with the specified ID as DTO
     * @throws      OfficeNotFoundException if no office with the given ID is found
     */
    public OfficeDto getOfficeById(Long id) throws OfficeNotFoundException {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new OfficeNotFoundException(id));
        return new OfficeDto(office);
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
