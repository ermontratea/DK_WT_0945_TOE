package pl.edu.agh.to.clinic.office;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.to.clinic.common.Views;
import pl.edu.agh.to.clinic.patient.Patient;


import java.util.List;

@RestController
@RequestMapping("offices")
@Tag(name = "Offices", description = "Operations related to doctor's offices")
public class OfficeController {
    private final OfficeService officeService;
    public OfficeController(OfficeService officeService) {this.officeService = officeService;}

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Adds a new office",
            description = "Adds a new office to the database, but first checks if an office with given room number already exists"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Office added successfully",
                    content = @Content(schema = @Schema(implementation = Office.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Office with this room number already exists"
            )
    })
    public Office addOffice(@RequestBody @Valid Office office){
        return officeService.addOffice(office);
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get all offices", description = "Returns a list of all offices")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of offices returned"
            )
    })
    @JsonView(Views.Public.class)
    public List<Office> getOffices(){
        return officeService.getOffices();
    }

    @GetMapping(value = "{id}",produces = "application/json")
    @Operation(summary = "Get office by ID",
    description = "Returns office details for given ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Office found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Office not found"
            )
    })
    @JsonView(Views.Internal.class)
    public Office getOfficeById(@PathVariable Long id){
        return officeService.getOfficeById(id);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Deletes an office with given ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Office deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Office not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Office has assigned duties, can't be deleted"
            )
    })
    public void deleteOfficeById(@PathVariable Long id){
        officeService.deleteOfficeById(id);
    }
}
