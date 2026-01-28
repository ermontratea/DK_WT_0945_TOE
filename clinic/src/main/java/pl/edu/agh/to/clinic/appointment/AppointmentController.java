package pl.edu.agh.to.clinic.appointment;

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
@RequestMapping("appointments")
@Tag(name = "Appointments", description = "Operations related to scheduling patients appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "Get all appointments",
            description = "Returns a list of all scheduled appointments"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments returned"
            )
    })
    public List<AppointmentDto> getAppointments() {
        return appointmentService.getAppointments();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            summary = "Get appointment by ID",
            description = "Returns appointment details for given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment found",
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    public AppointmentDto getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Add a new appointment",
            description = "Schedules a new appointment for a patient with given doctor in a given office and time range"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment added successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or patient not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Doctor or patient already busy in the given time range, or doctor does not have a duty in given office in given time range"
            )
    })
    public AppointmentDto addAppointment(@RequestBody @Valid AppointmentDto appointment) {
        return appointmentService.addAppointment(appointment);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete appointment",
            description = "Deletes appointment with given id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    public void deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
    }
}
