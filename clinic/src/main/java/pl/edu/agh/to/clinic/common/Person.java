package pl.edu.agh.to.clinic.common;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.Id;
import pl.edu.agh.to.clinic.doctor.Specialization;

@MappedSuperclass
public abstract class Person {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView({Views.Public.class})
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @JsonView({Views.Public.class})
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @JsonView({Views.Internal.class})
    @NotBlank(message = "PESEL cannot be blank")
    @Pattern(regexp = "\\d{11}", message = "PESEL must have exactly 11 digits")
    @Column(unique = true, nullable = false)
    private String pesel;

    @JsonView({Views.Internal.class})
    @NotBlank(message = "Address cannot be blank")
    private String address;

    protected Person(){    }
    public Person(String firstName, String lastName, String pesel, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.address = address;
    }

    public Long getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getPesel() {return pesel;}

    public String getAddress() {return address;}
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setPesel(String pesel) {this.pesel = pesel;}
    public void setAddress(String address) {this.address = address;}
}
