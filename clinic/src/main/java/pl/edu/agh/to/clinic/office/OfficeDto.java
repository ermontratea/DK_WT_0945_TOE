package pl.edu.agh.to.clinic.office;

import jakarta.validation.constraints.NotNull;


public class OfficeDto {

    private Long id;

    @NotNull(message = "Room number is required")
    private int roomNumber;

    public OfficeDto() {}

    public OfficeDto(Office office) {
        this.id = office.getId();
        this.roomNumber = office.getRoomNumber();
    }

    public Long getId() {return id;}
    public int getRoomNumber() {return roomNumber;}

    public void setId(Long id) {this.id = id;}
    public void setRoomNumber(int roomNumber) {this.roomNumber = roomNumber;}
}

