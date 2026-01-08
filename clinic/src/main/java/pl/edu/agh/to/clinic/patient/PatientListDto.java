package pl.edu.agh.to.clinic.patient;

public class PatientListDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;

    public PatientListDto() {}

    public PatientListDto(Patient patient) {
        this.id = patient.getId();
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.address = patient.getAddress();
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }

    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
