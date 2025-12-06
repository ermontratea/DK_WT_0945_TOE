package pl.edu.agh.to.clinic.doctor;

import jakarta.persistence.*;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String pesel;

    private Specialization specialization;
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

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setPesel(String pesel) {this.pesel = pesel;}
    public void setSpecialization(Specialization specialization) {this.specialization = specialization;}
    public void setAddress(String address) {this.address = address;}
}
