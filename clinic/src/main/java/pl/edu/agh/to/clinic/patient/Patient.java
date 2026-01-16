package pl.edu.agh.to.clinic.patient;

import jakarta.persistence.Entity;
import pl.edu.agh.to.clinic.common.Person;

@Entity
public class Patient extends Person {
    protected Patient() {
        super();
    };
    public Patient(String firstName, String lastName, String pesel, String address) {
        super(firstName, lastName, pesel, address);
    }
}
