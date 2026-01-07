package pl.edu.agh.to.clinic.office;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.clinic.duty.Duty;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room number is required")
    @Column(unique = true, nullable = false)
    private int roomNumber;

    @OneToMany(mappedBy = "office")
    private List<Duty> duties;

    public Office() {}
    public Office(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
    public List<Duty> getDuties() {
        return duties;
    }
    public Long getId() {return id;}
    protected Office() {}
    public Office(int roomNumber){
        this.roomNumber = roomNumber;
//        this.duties = new ArrayList<>();
    }
    public String toString(){
        return "Office " + roomNumber;
    }
}
