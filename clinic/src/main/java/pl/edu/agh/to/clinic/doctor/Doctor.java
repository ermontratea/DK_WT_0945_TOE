package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor {

    public static class Views{
        public interface List{}
        public interface Details extends List{}
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.List.class,Views.Details.class})
    private Long id;

    @JsonView({Views.List.class, Views.Details.class})
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @JsonView({Views.List.class, Views.Details.class})
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "PESEL cannot be blank")
    @Pattern(regexp = "\\d{11}", message = "PESEL must have exactly 11 digits")
    @Column(unique = true, nullable = false)
    private String pesel;

    @Enumerated(EnumType.STRING)
    @JsonView({Views.List.class, Views.Details.class})
    @NotNull(message = "Specialization cannot be null")
    private Specialization specialization;


    @JsonView({Views.Details.class})
    @NotBlank(message = "Address cannot be blank")
    private String address;

    protected Doctor() {}

    public Doctor(String firstName, String lastName, String pesel, Specialization specialization, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.specialization = specialization;
        this.address = address;
    }

    public Long getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getPesel() {return pesel;}
    public Specialization getSpecialization() {return specialization;}
    public String getAddress() {return address;}
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setPesel(String pesel) {this.pesel = pesel;}
    public void setSpecialization(Specialization specialization) {this.specialization = specialization;}
    public void setAddress(String address) {this.address = address;}
}
