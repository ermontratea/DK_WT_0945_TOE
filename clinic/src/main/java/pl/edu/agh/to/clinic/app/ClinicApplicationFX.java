package pl.edu.agh.to.clinic.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorApiClient;
import javafx.application.Application;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.duty.DutyApiClient;
import pl.edu.agh.to.clinic.duty.DutyDto;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeApiClient;
import pl.edu.agh.to.clinic.patient.Patient;
import pl.edu.agh.to.clinic.patient.PatientApiClient;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.DayOfWeek;

import static java.lang.Integer.parseInt;

public class ClinicApplicationFX extends Application{
    private final DoctorApiClient doctorApi=new DoctorApiClient();
    private final PatientApiClient patientApi=new PatientApiClient();
    private final OfficeApiClient officeApi=new OfficeApiClient();
    private final DutyApiClient dutyApi=new DutyApiClient();
    private final ListView<Doctor> doctorListView=new ListView<>();
    private final ListView<Office> officeListView=new ListView<>();


    @Override
    public void start(Stage stage) throws Exception{
        // VERTICAL LAYOUT
        VBox root=new VBox(10);

        Button addDoctorBtn=new Button("ADD DOCTOR");
        addDoctorBtn.setOnAction(e->addCustomDoctor());

        // ADD DOCTOR BUTTON
        Button addDoctorsBtn=new Button("ADD SAMPLE DOCTORS");
        addDoctorsBtn.setOnAction(e->addSampleDoctors());

        // DELETE SELECTED DOCTOR BUTTON
//        Button deleteSelectedDoctorBtn=new Button("DELETE SELECTED DOCTOR");
//        deleteSelectedDoctorBtn.setOnAction(e->deleteSelectedDoctor());
//        root.getChildren().add(deleteSelectedDoctorBtn);

        // DELETE ALL DOCTORS BUTTON
        Button deleteDoctorsBtn=new Button("DELETE ALL DOCTORS");
        deleteDoctorsBtn.setOnAction(e->deleteAllDoctors());

        VBox doctorBtns=new VBox(10, addDoctorBtn, addDoctorsBtn,deleteDoctorsBtn);

        doctorBtns.setPadding(new Insets(10));

        // ADD PATIENT BUTTON
        Button addPatientBtn=new Button("ADD PATIENT");
        addPatientBtn.setOnAction(e->addCustomPatient());

        // ADD OFFICE BUTTON
        Button addOfficeBtn=new Button("ADD OFFICE");
        addOfficeBtn.setOnAction(e->addOffice());

        // ASSIGN DUTY BUTTON
        Button assignDutyBtn=new Button("ASSIGN DUTY");
        assignDutyBtn.setOnAction(e->assignDuty());

        VBox rightBtns=new VBox(10, addPatientBtn, addOfficeBtn,assignDutyBtn);

        rightBtns.setPadding(new Insets(10));

        HBox btns=new HBox(10,doctorBtns,rightBtns);
        root.getChildren().addAll(btns);

        // DOCTOR LIST
        doctorListView.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            if(newValue!=null) showDoctorDetails(newValue.getId()); //doctor details if clicked
        });


        //OFFICE LIST
        root.getChildren().addAll(new Label("Doctors: "), doctorListView);
        officeListView.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            if(newValue!=null) showOfficeDetails(newValue.getId()); //doctor details if clicked
        });

        root.getChildren().addAll(new Label("Offices: "), officeListView);

        stage.setScene(new Scene(root));
        stage.setWidth(600);
        stage.setHeight(700);
        stage.setTitle("Clinic Application");
        stage.show();

        //SHOW DOCTOR LIST
        loadDoctors();
        loadOffices();
    }

    private void showMessage(String message){
        Dialog<Void> dialog=new Dialog<>();
        dialog.setTitle("Message");
        TextArea textArea=new TextArea();
        textArea.setText(message);
        textArea.setEditable(false);
        dialog.getDialogPane().setContent(textArea);
        dialog.setHeight(200);
        dialog.setWidth(600);
        dialog.setResizable(false);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    // LOADING DOCTORS
    private void loadDoctors(){
        try {
            List<Doctor> doctorList = doctorApi.getDoctors();
            doctorListView.getItems().setAll(doctorList);
        }catch (RuntimeException ex){
            showMessage("Error loading doctors list: " + ex.getMessage());
        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());
        }
    }


    // DOCTOR DETAILS
    private void showDoctorDetails(long id){
        try {
            Doctor doctor = doctorApi.getDoctorById(id);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Doctor Details");
            dialog.setHeaderText(doctor.getFirstName() + " " + doctor.getLastName());
            VBox content=new VBox(10);
            Label specializationLabel=new Label("Specialization: " +  doctor.getSpecialization());
            Label addressLabel=new Label("Address: " +  doctor.getAddress());
            TextArea dutiesArea=new TextArea();
            dutiesArea.setWrapText(true);
            dutiesArea.setEditable(false);
            StringBuilder sb = new StringBuilder();
            List<DutyDto> duties =dutyApi.getDuties().stream().filter(d->d.getDoctorId().equals(doctor.getId())).toList();
            if (!duties.isEmpty()) {
                for (DutyDto d : duties) {
                    Office office = officeApi.getOfficeById(d.getOfficeId());
                    int roomNumber = office.getRoomNumber();
                    sb.append(d.getDayOfWeek()).append(" | Office ").append(roomNumber).append(" | ").append(d.getStartTime())
                            .append(" - ").append(d.getEndTime()).append("\n");
                }
            }else {
                sb.append("No duties assigned.");
            }

            dutiesArea.setText(sb.toString());
            content.getChildren().addAll(specializationLabel, addressLabel, new Label("Duties: "),dutiesArea);
            dialog.getDialogPane().setContent(content);
            dialog.setWidth(400);
            dialog.setHeight(500);
            ButtonType deleteButton=new ButtonType("Delete doctor", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButton, ButtonType.CLOSE);
            dialog.showAndWait().ifPresent(button-> {
                if (button == deleteButton) {
                    try {
                        doctorApi.deleteDoctorById(doctor.getId());
                        loadDoctors();
                        showMessage("Doctor " + doctor + " deleted successfully!");
                    } catch (DoctorNotFoundException e){
                        showMessage("Error deleting doctor: "+e.getMessage());
                    }catch (Exception ex){
                        showMessage("Unexpected error: " + ex.getMessage());
                    }
                }
            });

        } catch (DoctorNotFoundException e) {
            showMessage("Error deleting doctor: " + e.getMessage());
        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());
        }
//            StringBuilder sb = new StringBuilder();
//            sb.append("Doctor details:\n");
//            sb.append("Name: ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName()).append("\n");
//            sb.append("Specialization: ").append(doctor.getSpecialization()).append("\n");
//            sb.append("\nDuties:\n");
//            if (doctor.getDuties() != null && !doctor.getDuties().isEmpty()) {
//                for (var d : doctor.getDuties()) {
//                    sb.append(" - Room ").append(d.getOffice().getRoomNumber())
//                            .append(": ").append(d.getStartTime().toLocalTime())
//                            .append(" - ").append(d.getEndTime().toLocalTime()).append("\n");
//                }
//            } else {
//                sb.append("No duties assigned.");
//            }
//
//            textArea.setText(sb.toString());
//        } catch (Exception ex) {
//            textArea.setText("Error: " + ex.getMessage());
//        }
    }

    // ADDING DOCTORS
    private void addSampleDoctors(){
        try{
            List<Doctor> doctors=List.of(
                    new Doctor("Anna", "Nowak", "00000000000", Specialization.CARDIOLOGY, "A 1"),
                    new Doctor("Jan", "Kowalski", "11111111111", Specialization.CARDIOLOGY, "B 1"),
                    new Doctor("Marta", "Zielińska", "22222222222", Specialization.CARDIOLOGY, "C 1"),
                    new Doctor("Tomasz", "Nowak", "33333333333", Specialization.DERMATOLOGY, "D 1"),
                    new Doctor("Mateusz", "Wiśniewski", "44444444444", Specialization.DERMATOLOGY, "E 1"),
                    new Doctor("Andrzej", "Dąb", "55555555555", Specialization.ORTHOPEDICS, "F 1"),
                    new Doctor("Karolina", "Kamień", "66666666666", Specialization.PEDIATRICS, "G 1")
                    );
            for(Doctor doctor:doctors){
                doctorApi.addDoctor(doctor);
            }
            loadDoctors();
            showMessage("Doctors added successfully!");

        }catch (DoctorNotFoundException e){
            showMessage("Error adding doctors: " + e.getMessage());

        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());

        }
    }
    //ADD CUSTOM DOCTOR
    private void addCustomDoctor(){
        Stage stage=new Stage();
        stage.setTitle("Add Doctor");

        VBox box=new VBox(10);
        box.setPadding(new Insets(10));
        TextField firstNameField=new TextField();
        firstNameField.setPromptText("First name");
        TextField lastNameField=new TextField();
        lastNameField.setPromptText("Last name");
        TextField peselField=new TextField();
        peselField.setPromptText("PESEL ");

        ComboBox<Specialization> specializationBox=new ComboBox<>();
        specializationBox.getItems().addAll(Specialization.values());
        specializationBox.setPromptText("Specialization");

        TextField addressField=new TextField();
        addressField.setPromptText("Address");
        Button addButton=new Button("Add");
        Button cancelButton=new Button("Cancel");
        HBox buttons=new HBox(10,addButton,cancelButton);

        box.getChildren().addAll(
                new Label("First name:"),
                firstNameField,
                new Label("Last name:"),
                lastNameField,
                new Label("PESEL:"),
                peselField,
                new Label("Specialization:"),
                specializationBox,
                new Label("Address:"),
                addressField,
                buttons
        );

        addButton.setOnAction(e->{
                try {
                    Doctor doctor = new Doctor(
                            firstNameField.getText(),
                            lastNameField.getText(),
                            peselField.getText(),
                            specializationBox.getValue(),
                            addressField.getText()
                    );
                    doctorApi.addDoctor(doctor);
                    loadDoctors();
                    showMessage("Doctor added successfully!");
                    stage.close();
                }catch (PeselDuplicationException ex){
                showMessage(ex.getMessage());
            }
            catch (RuntimeException ex) {
                showMessage("Validation errors:\n" + ex.getMessage());
            }
            catch (Exception ex) {
                showMessage("Unexpected error: " + ex.getMessage());
            }
        });
        cancelButton.setOnAction(e->stage.close());
        stage.setScene(new Scene(box,400,600));
        stage.show();
    }

    // DELETE SELECTED DOCTOR
//    private void deleteSelectedDoctor(){
//        Doctor doctor=doctorListView.getSelectionModel().getSelectedItem();
//        if(doctor==null){
//            showMessage("No doctor selected. Please select a doctor!");
//            return;
//        }
//        try {
//            api.deleteDoctorById(doctor.getId());
//            loadDoctors();
//            showMessage("Doctor " + doctor + " deleted successfully!");
//        } catch (DoctorNotFoundException e){
//            showMessage("Error deleting doctor: "+e.getMessage());
//        }catch (Exception ex){
//            showMessage("Unexpected error: " + ex.getMessage());
//        }
//    }
    //DELETE DOCTORS
    private void deleteAllDoctors(){
        try{
            List<Doctor> doctors=doctorApi.getDoctors();
            for(Doctor doctor:doctors){
                doctorApi.deleteDoctorById(doctor.getId());
            }
            loadDoctors();
            showMessage("All doctors deleted successfully!");
        }catch (DoctorNotFoundException e){
            showMessage("Error deleting doctors: "+e.getMessage());
        }catch (Exception e) {
            showMessage("Unexpected error: " + e.getMessage());
        }
    }

    //PATIENTS
    //ADD CUSTOM PATIENT
    private void addCustomPatient(){
        Stage stage=new Stage();
        stage.setTitle("Add Patient");

        VBox box=new VBox(10);
        box.setPadding(new Insets(10));
        TextField firstNameField=new TextField();
        firstNameField.setPromptText("First name");
        TextField lastNameField=new TextField();
        lastNameField.setPromptText("Last name");
        TextField peselField=new TextField();
        peselField.setPromptText("PESEL ");

        TextField addressField=new TextField();
        addressField.setPromptText("Address");
        Button addButton=new Button("Add");
        Button cancelButton=new Button("Cancel");
        HBox buttons=new HBox(10,addButton,cancelButton);


        box.getChildren().addAll(
                new Label("First name"),
                firstNameField,
                new Label("Last name"),
                lastNameField,
                new Label("Pesel"),
                peselField,
                new Label("Address"),
                addressField,
                buttons
        );

        addButton.setOnAction(e->{
                try {
                    Patient patient=new Patient(
                            firstNameField.getText(),
                            lastNameField.getText(),
                            peselField.getText(),
                            addressField.getText()
                    );
                    patientApi.addPatient(patient);
                    showMessage("Patient added successfully!");
                    stage.close();
                }catch (PeselDuplicationException ex){
                    showMessage(ex.getMessage());
                }
                catch (RuntimeException ex) {
                    showMessage("Validation errors:\n" + ex.getMessage());
                }
                catch (Exception ex) {
                    showMessage("Unexpected error: " + ex.getMessage());
                }

        });
        cancelButton.setOnAction(e->stage.close());
        stage.setScene(new Scene(box,400,600));
        stage.show();
    }

    // LOADING OFFICES
    private void loadOffices(){
        try {
            List<Office> officesList = officeApi.getOffices();
            officeListView.getItems().setAll(officesList);
        }catch (RuntimeException ex){
            showMessage("Error loading offices list: " + ex.getMessage());
        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());
        }
    }

    // OFFICE DETAILS
    private void showOfficeDetails(long id){
        try {
            Office office=officeApi.getOfficeById(id);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Office Details");
            dialog.setHeaderText("Room: " + office.getRoomNumber());
            VBox content=new VBox(10);
            TextArea dutiesArea=new TextArea();
            dutiesArea.setWrapText(true);
            dutiesArea.setEditable(false);
            StringBuilder sb = new StringBuilder();
            List<DutyDto> duties =dutyApi.getDuties().stream().filter(d->d.getOfficeId().equals(office.getId())).toList();
            if (!duties.isEmpty()) {
                List<Doctor> doctors=doctorApi.getDoctors();
                for (DutyDto d : duties) {
                    Doctor doctor=doctors.stream().filter(doc->doc.getId().equals(d.getDoctorId())).findFirst().orElse(null);
                    String doctorName = doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown";
                    sb.append(doctorName).append(" | ").append(d.getDayOfWeek()).append(" | ").append(d.getStartTime())
                            .append(" - ").append(d.getEndTime()).append("\n");
                }
            }else {
                sb.append("No duties assigned.");
            }
//            if (office.getDuties() != null && !office.getDuties().isEmpty()) {
//                for (var d : office.getDuties()) {
//                    sb.append("Doctor ").append(d.getDoctor().toString())
//                            .append(": ").append(d.getStartTime())
//                            .append(" - ").append(d.getEndTime()).append("\n");
//                }
//            } else {
//                sb.append("No duties assigned.");
//            }
            dutiesArea.setText(sb.toString());
            dialog.getDialogPane().setContent(content);
            content.getChildren().addAll(new Label("Duties: "),dutiesArea);
            dialog.setWidth(400);
            dialog.setHeight(500);
            ButtonType deleteButton=new ButtonType("Delete office", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButton, ButtonType.CLOSE);
            dialog.showAndWait().ifPresent(button-> {
                if (button == deleteButton) {
                    try {
                        officeApi.deleteOfficeById(office.getId());
                        loadOffices();
                        showMessage(office + " deleted successfully!");
                    } catch (OfficeNotFoundException e){
                        showMessage("Error deleting office: "+e.getMessage());
                    }catch (Exception ex){
                        showMessage("Unexpected error: " + ex.getMessage());
                    }
                }
            });

        } catch (OfficeNotFoundException e) {
            showMessage("Error deleting office: " + e.getMessage());
        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());
        }}

    //ADD OFFICE
    private void addOffice(){
        Stage stage=new Stage();
        stage.setTitle("Add Office");
//        Dialog<ButtonType> dialog = new Dialog<>();
//        dialog.setTitle("Add Office");
//        dialog.setHeaderText("Enter office details: ");
//        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        VBox box=new VBox(10);
        box.setPadding(new Insets(10));
        TextField roomNumberField=new TextField();
        roomNumberField.setPromptText("Room number");
        Button addButton=new Button("Add");
        Button cancelButton=new Button("Cancel");
        HBox buttons=new HBox(10,addButton,cancelButton);
        Label label=new Label("Room number: ");

        box.getChildren().addAll(
                label,
                roomNumberField,
                buttons
        );


        addButton.setOnAction(e->{
                try {
                    Office office=new Office(
                            parseInt(roomNumberField.getText())
                    );
                    officeApi.addOffice(office);
                    loadOffices();
                    showMessage("Office added successfully!");
                stage.close();}
                catch (RoomNumberDuplicationException ex) {
                    showMessage(ex.getMessage());
                }
                catch (Exception ex) {
                    showMessage("Unexpected error: " + ex.getMessage());
                }

        });
        cancelButton.setOnAction(e->stage.close());
        stage.setScene(new Scene(box,400,300));
        stage.show();
    }
    private void assignDuty(){
        Stage stage=new Stage();
        stage.setTitle("Assign Duty");

        VBox box=new VBox(10);
        box.setPadding(new Insets(10));

        TextField startHourField=new TextField();
        startHourField.setPromptText("HH:MM");
        TextField endHourField=new TextField();
        endHourField.setPromptText("HH:MM");

        ComboBox<DayOfWeek> dayBox=new ComboBox<>();
        dayBox.getItems().addAll(DayOfWeek.values());
        dayBox.setPromptText("Day of week");

        ListView<Doctor> availableDoctorsView=new ListView<>();
        ListView<Office> availableOfficesView=new ListView<>();

        Button checkBtn=new Button("Check availability");
        Button assignButton=new Button("Assign");
        Button cancelButton=new Button("Cancel");
        HBox buttons=new HBox(10,assignButton,cancelButton);


        box.getChildren().addAll(
                new Label("Day of week: "),
                dayBox,
                new Label("Start time "),
                startHourField,
                new Label("End time"),
                endHourField,
                checkBtn,
                new Label("Available doctors"),
                availableDoctorsView,
                new Label("Available offices"),
                availableOfficesView,
                buttons
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        //CHECK AVAILABILITY - SHOW AVAILABLE DOCTORS AND OFFICES LIST

        checkBtn.setOnAction(e-> {
                    try {
                        if (dayBox.getValue() == null) {
                            showMessage("Please select day of week");
                            return;
                        }
                        LocalTime startTime = LocalTime.parse(startHourField.getText(), formatter);
                        LocalTime endTime = LocalTime.parse(endHourField.getText(), formatter);

                        List<DutyDto> duties = dutyApi.getDuties();
                        List<Doctor> doctors = doctorApi.getDoctors();
                        List<Office> offices = officeApi.getOffices();

                        availableDoctorsView.getItems().setAll(doctors.stream()
                                .filter(d -> isDoctorAvailable(d, dayBox.getValue(), startTime, endTime, duties)).toList());
                        availableOfficesView.getItems().setAll(offices.stream()
                                .filter(o -> isOfficeAvailable(o, dayBox.getValue(), startTime, endTime, duties)).toList());

                    } catch (Exception ex) {
                        showMessage("Invalid input: " + ex.getMessage());
                    }
                });
        assignButton.setOnAction(e->{
            Doctor doctor = availableDoctorsView.getSelectionModel().getSelectedItem();
            Office office= availableOfficesView.getSelectionModel().getSelectedItem();
            if (doctor == null || office == null) {
                showMessage("Please select doctor and office");
                return;
            }
            try{
                LocalTime startTime=LocalTime.parse(startHourField.getText(),formatter);
                LocalTime endTime= LocalTime.parse(endHourField.getText(),formatter);
                DutyDto duty=new DutyDto();
                duty.setDoctorId(doctor.getId());
                duty.setOfficeId(office.getId());
                duty.setStartTime(startTime);
                duty.setEndTime(endTime);
                duty.setDayOfWeek(dayBox.getValue());
                dutyApi.addDuty(duty);

                loadDoctors();
                loadOffices();
                showMessage("Duty added successfully!");
                stage.close();

            }catch (Exception ex){
                showMessage("Error assigning duty: "+ex.getMessage());
            }
        });

        cancelButton.setOnAction(e->stage.close());

        stage.setScene(new Scene(box,400,600));
        stage.show();

    }

    private boolean isDoctorAvailable(Doctor doctor,DayOfWeek day, LocalTime start,LocalTime end, List<DutyDto> duties){
        return duties.stream()
                .filter(d -> d.getDoctorId().equals(doctor.getId()))
                .filter(d->d.getDayOfWeek().equals(day))
                .noneMatch(d ->
                        d.getStartTime().isBefore(end)
                                && d.getEndTime().isAfter(start)
                );
    }
    private boolean isOfficeAvailable(Office office,DayOfWeek day, LocalTime start,LocalTime end, List<DutyDto> duties){
        return duties.stream()
                .filter(d->d.getOfficeId().equals(office.getId()))
                .filter(d->d.getDayOfWeek().equals(day))
                .noneMatch(d-> d.getStartTime().isBefore(end) && d.getEndTime().isAfter(start));
    }





    // LAUNCH JAVAFX APP
    public static void main(String[] args) {
        launch();
    }
}
