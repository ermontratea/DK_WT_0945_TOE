package pl.edu.agh.to.clinic.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PatientDto {
    private Long id;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "PESEL cannot be blank")
    @Pattern(regexp = "\\d{11}", message = "PESEL must have exactly 11 digits")
    private String pesel;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    public PatientDto() {}

    public PatientDto(Patient patient) {
        this.id = patient.getId();
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.pesel = patient.getPesel();
        this.address = patient.getAddress();
    }


    public Long getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getPesel() {return pesel;}
    public String getAddress() {return address;}

    public void setId(Long id) {this.id = id;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setPesel(String pesel) {this.pesel = pesel;}
    public void setAddress(String address) {this.address = address;}
}
