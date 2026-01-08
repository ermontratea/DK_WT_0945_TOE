package pl.edu.agh.to.clinic.doctor;

public class DoctorListDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private Specialization specialization;

    public DoctorListDto() {}

    public DoctorListDto(Doctor doctor) {
        this.id = doctor.getId();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.address = doctor.getAddress();
        this.specialization = doctor.getSpecialization();
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public Specialization getSpecialization() { return specialization; }

    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
