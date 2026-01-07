package pl.edu.agh.to.clinic.doctor;

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
@RequestMapping("doctors")
@Tag(name = "Doctors", description = "Operations related to doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Add a new doctor",
            description = "Adds a new doctor to the system. PESEL must be 11 digits and unique and no attributes can be null or blank"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor added successfully",
                    content = @Content(schema = @Schema(implementation = DoctorDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Doctor with this PESEL already exists"
            )
    })
    public DoctorDto addDoctor(@RequestBody @Valid DoctorDto doctor){
        return doctorService.addDoctor(doctor);
    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get all doctors",
            description = "Returns a list of all doctors"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of doctors returned",
                    content = @Content(schema = @Schema(implementation = DoctorDto.class))
            )
    })
    public List<DoctorDto> getDoctors() {
        return doctorService.getDoctors();
    }

    @GetMapping(value ="{id}", produces = "application/json")
    @Operation(
            summary = "Get doctor by ID",
            description = "Returns doctor details for given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor found",
                    content = @Content(schema = @Schema(implementation = DoctorDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    public DoctorDto getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete doctor",
            description = "Deletes doctor with given id, but only if the doctor has no assigned duties"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Doctor has assigned duties, can't be deleted"
            )
    })
    public void deleteDoctorById(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
    }
}
