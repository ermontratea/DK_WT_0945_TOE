package pl.edu.agh.to.clinic.patient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

@RestController
@RequestMapping("patients")
@Tag(name = "Patients", description = "Operations related to patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Add a new patient",
            description = "Adds a new patient to the system. PESEL must be 11 digits and unique and no attributes can be null or blank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient added successfully",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Patient with this PESEL already exists"
            )
    })
    public PatientDto addPatient(@RequestBody @Valid PatientDto patient) throws PeselDuplicationException {
        return patientService.addPatient(patient);
    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get all patients",
            description = "Returns a list of all patients"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of patients returned"
            )
    })
    public List<PatientDto> getPatients() {
        return patientService.getPatients();
    }

    @GetMapping(value ="{id}", produces = "application/json")
    @Operation(
            summary = "Get patient by ID",
            description = "Returns patient details for given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    public PatientDto getPatientById(@PathVariable Long id) throws PatientNotFoundException {
        return patientService.getPatientById(id);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete patient",
            description = "Deletes patient with given id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    public void deletePatientById(@PathVariable Long id) throws PatientNotFoundException {
        patientService.deletePatientById(id);
    }
}