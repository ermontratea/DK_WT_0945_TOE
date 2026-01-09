package pl.edu.agh.to.clinic.duty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("duties")
@Tag(name = "Duties", description = "Operations related to scheduling doctor duties")
public class DutyController {
    private final DutyService dutyService;

    public DutyController(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get all duties",
            description = "Returns a list of all scheduled duties"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of duties returned"
            )
    })
    public List<DutyDto> getDuties() {
        return dutyService.getDuties();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            summary = "Get duty by ID",
            description = "Returns duty details for given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Duty found",
                    content = @Content(schema = @Schema(implementation = DutyDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Duty not found"
            )
    })
    public DutyDto getDutyById(@PathVariable Long id) {
        return dutyService.getDutyById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Add new duty",
            description = "Schedules a new duty for a doctor in a given office and time range"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Duty added successfully",
                    content = @Content(schema = @Schema(implementation = DutyDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or office not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Doctor or office already busy in the given time range"
            )
    })
    public DutyDto addDuty(@RequestBody @Valid DutyDto duty) {
        return dutyService.addDuty(duty);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete duty",
            description = "Deletes duty with given id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Duty deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Duty not found"
            )
    })
    public void deleteDutyById(@PathVariable Long id) {
        dutyService.deleteDutyById(id);
    }
}
