package pl.edu.agh.to.clinic.duty;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.to.clinic.common.Views;

import java.util.List;

@RestController
@RequestMapping("duties")
@Tag(name = "Duties", description = "Operations related to scheduling doctor duties")
public class DutyController {
    private final DutyService dutyService;

    public DutyController(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public List<Duty> getAllDuties() {
        return dutyService.getDuties();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Internal.class)
    public Duty getDutyById(@PathVariable Long id) {
        return dutyService.getDutyById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(Views.Internal.class)
    public Duty addDuty(@RequestBody @Valid Duty duty) {
        return dutyService.addDuty(duty);
    }

    @DeleteMapping("{id}")
    public void deleteDuty(@PathVariable Long id) {
        dutyService.deleteDutyById(id);
    }
}
