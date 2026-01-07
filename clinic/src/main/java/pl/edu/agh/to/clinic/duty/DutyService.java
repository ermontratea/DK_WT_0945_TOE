package pl.edu.agh.to.clinic.duty;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;
import pl.edu.agh.to.clinic.patient.PatientRepository;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;

import java.util.List;

@Service
public class DutyService {
    private final DutyRepository dutyRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    public DutyService(DutyRepository dutyRepository, DoctorRepository doctorRepository, OfficeRepository officeRepository) {
        this.dutyRepository = dutyRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
    }

    /**
     * Adds a new duty.
     * Checks if doctor and office exist and are not busy in the given time range.
     *
     * @param dto data of the duty to be added
     * @return    saved duty as DTO
     * @throws DoctorNotFoundException if doctor with given id does not exist
     * @throws OfficeNotFoundException if office with given id does not exist
     * @throws IllegalStateException   if doctor or office is already busy in this time range
     */
    public DutyDto addDuty(DutyDto dto){
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(dto.getDoctorId()));
        Office office = officeRepository.findById(dto.getOfficeId())
                .orElseThrow(() -> new OfficeNotFoundException(dto.getOfficeId()));

        boolean doctorBusy = dutyRepository.existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                doctor, dto.getEndTime(), dto.getStartTime());

        if (doctorBusy) {
            throw new IllegalStateException("Doctor already has a duty assigned during these hours!");
        }

        boolean officeBusy = dutyRepository.existsByOfficeAndStartTimeBeforeAndEndTimeAfter(
                office, dto.getEndTime(), dto.getStartTime());

        if (officeBusy) {
            throw new IllegalStateException("Office is already occupied during these hours!");
        }

        Duty duty = new Duty(
                doctor,
                office,
                dto.getStartTime(),
                dto.getEndTime()
        );

        Duty saved = dutyRepository.save(duty);
        return new DutyDto(saved);
    }

    /**
     * Returns all duties as DTOs.
     * @return  a list of all saved duties as DTOs
     */
    public List<DutyDto> getDuties() {
        return dutyRepository.findAll()
                .stream()
                .map(DutyDto::new)
                .toList();
    }

    /**
     * Returns duty by id as DTO.
     *
     * @param id id of the duty to retrieve
     * @return   duty with the given id as DTO
     * @throws   DutyNotFoundException if duty with the given id is not found
     */
    public DutyDto getDutyById(Long id) throws DutyNotFoundException {
        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() -> new DutyNotFoundException(id));
        return new DutyDto(duty);
    }

    /**
     * Deletes duty by id.
     *
     * @param id id of the duty to delete
     * @throws   DutyNotFoundException if duty with the given id is not found
     */
    public void deleteDutyById(Long id) throws DutyNotFoundException {
        if(!dutyRepository.existsById(id)) {
            throw new DutyNotFoundException(id);
        }
        dutyRepository.deleteById(id);
    }
}
