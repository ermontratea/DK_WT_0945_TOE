package pl.edu.agh.to.clinic.doctor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.common.Person;

import java.util.List;

@Entity
public class Doctor extends Person {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Specialization cannot be null")
    private Specialization specialization;

    @OneToMany(mappedBy = "doctor")
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
    public void setDuties(List<Duty> duties) {
        this.duties = duties;
    }
}
