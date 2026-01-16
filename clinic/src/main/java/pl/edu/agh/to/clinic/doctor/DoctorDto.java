package pl.edu.agh.to.clinic.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DoctorDto {
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

    @NotNull(message = "Specialization cannot be null")
    private Specialization specialization;

    public DoctorDto(){}

    public DoctorDto(Doctor doctor) {
        this.id = doctor.getId();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.pesel = doctor.getPesel();
        this.address = doctor.getAddress();
        this.specialization = doctor.getSpecialization();
    }

    public Long getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getPesel() {return pesel;}
    public String getAddress() {return address;}
    public Specialization getSpecialization() {return specialization;}

    public void setId(Long id) {this.id = id;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setPesel(String pesel) {this.pesel = pesel;}
    public void setAddress(String address) {this.address = address;}
    public void setSpecialization(Specialization specialization) {this.specialization = specialization;}
}
