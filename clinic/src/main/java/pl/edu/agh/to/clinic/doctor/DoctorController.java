package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

@RestController
@RequestMapping("doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) throws PeselDuplicationException {
        return doctorService.addDoctor(doctor);
    }

    @GetMapping
    @JsonView(Doctor.Views.List.class)
    public List<Doctor> getDoctors() {
        return doctorService.getDoctors();
    }

    @GetMapping("{id}")
    @JsonView(Doctor.Views.Details.class)
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @DeleteMapping("{id}")
    public void deleteDoctorById(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
    }
}
