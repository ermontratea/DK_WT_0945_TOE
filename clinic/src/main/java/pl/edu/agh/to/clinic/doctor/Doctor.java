package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.common.Person;
import pl.edu.agh.to.clinic.common.Views;

import java.util.List;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends Person {

    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class, Views.Internal.class})
    @NotNull(message = "Specialization cannot be null")
    private Specialization specialization;

    @JsonView({Views.Internal.class})
    @OneToMany(mappedBy = "doctor")
    @JsonManagedReference(value = "doctor-duty")
    private List<Duty> duties;

    protected Doctor() {
        super();
    }

    public Doctor(String firstName, String lastName, String pesel, Specialization specialization, String address) {
        super(firstName,lastName,pesel,address);
        this.specialization = specialization;
    }
    public Specialization getSpecialization() {return specialization;}

    public void setSpecialization(Specialization specialization) {this.specialization = specialization;}
    public List<Duty> getDuties() {return duties;}
}
