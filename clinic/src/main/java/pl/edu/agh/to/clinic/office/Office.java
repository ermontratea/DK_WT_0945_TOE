package pl.edu.agh.to.clinic.office;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.common.Views;

import java.util.List;

@Entity
public class Office {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    @Column(unique = true, nullable = false)
    private int roomNumber;

    @OneToMany(mappedBy = "office")
    @JsonView(Views.Internal.class)
    @JsonManagedReference(value="office-duty")
    private List<Duty> duties;

    public int getRoomNumber() {
        return roomNumber;
    }
    public List<Duty> getDuties() {
        return duties;
    }
}
