package pl.edu.agh.to.clinic.duty;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;

import java.util.List;

@Service
public class DutyService {
    private final DutyRepository dutyRepository;
    public DutyService(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    public Duty addDuty(Duty duty){
        boolean doctorBusy = dutyRepository.existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                duty.getDoctor(), duty.getEndTime(), duty.getStartTime());

        if (doctorBusy) {
            throw new IllegalStateException("Doctor already has a duty assigned during these hours!");
        }

        boolean officeBusy = dutyRepository.existsByOfficeAndStartTimeBeforeAndEndTimeAfter(
                duty.getOffice(), duty.getEndTime(), duty.getStartTime());

        if (officeBusy) {
            throw new IllegalStateException("Office is already occupied during these hours!");
        }
        return dutyRepository.save(duty);
    }

    public List<Duty> getDuties() {
        return dutyRepository.findAll();
    }


    public Duty getDutyById(Long id) throws DutyNotFoundException {
        return dutyRepository.findById(id)
                .orElseThrow(() -> new DutyNotFoundException(id));
    }

    public void deleteDutyById(Long id) throws DutyNotFoundException {
        if(!dutyRepository.existsById(id)) {
            throw new DutyNotFoundException(id);
        }
        dutyRepository.deleteById(id);
    }
}
