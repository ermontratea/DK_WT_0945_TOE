package pl.edu.agh.to.clinic.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.agh.to.clinic.doctor.DoctorApiClient;
import javafx.application.Application;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.duty.DutyApiClient;
import pl.edu.agh.to.clinic.duty.DutyDto;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;
import pl.edu.agh.to.clinic.office.OfficeApiClient;
import pl.edu.agh.to.clinic.patient.PatientApiClient;
import pl.edu.agh.to.clinic.doctor.DoctorDto;
import pl.edu.agh.to.clinic.doctor.DoctorListDto;
import pl.edu.agh.to.clinic.office.OfficeDto;
import pl.edu.agh.to.clinic.patient.PatientDto;
import pl.edu.agh.to.clinic.patient.PatientListDto;
import pl.edu.agh.to.clinic.appointment.AppointmentApiClient;
import pl.edu.agh.to.clinic.appointment.AppointmentDto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.lang.Integer.parseInt;

public class ClinicApplicationFX extends Application{
    private final DoctorApiClient doctorApi=new DoctorApiClient();
    private final PatientApiClient patientApi=new PatientApiClient();
    private final OfficeApiClient officeApi=new OfficeApiClient();
    private final DutyApiClient dutyApi=new DutyApiClient();
    private final AppointmentApiClient appointmentApi=new AppointmentApiClient();
    private final ListView<DoctorListDto> doctorListView=new ListView<>();
    private final ListView<OfficeDto> officeListView=new ListView<>();
    private final ListView<PatientListDto> patientListView=new ListView<>();

    private VBox doctorsPanel;
    private PatientsPanel patientsPanel = new PatientsPanel();
    private VBox officesPanel;


    @Override
    public void start(Stage stage) throws Exception{
        // VERTICAL LAYOUT
        VBox root=new VBox(10);
        //TOP NAV
        Button doctorsListBtn = new Button("DOCTORS LIST");
        Button patientsListBtn = new Button("PATIENTS LIST");
        Button officesListBtn = new Button("OFFICES LIST");
        String navBtnStyle = "-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-size: 14px;";
        doctorsListBtn.setStyle(navBtnStyle);
        patientsListBtn.setStyle(navBtnStyle);
        officesListBtn.setStyle(navBtnStyle);
        doctorsListBtn.setPrefWidth(120);
        patientsListBtn.setPrefWidth(120);
        officesListBtn.setPrefWidth(120);
        doctorsListBtn.setPrefHeight(45);
        patientsListBtn.setPrefHeight(45);
        officesListBtn.setPrefHeight(45);
        HBox nav = new HBox(10, doctorsListBtn, patientsListBtn, officesListBtn);
        nav.setPadding(new Insets(0, 0, 10, 0));

        // ADD DOCTOR BUTTON
        Button addDoctorBtn=new Button("ADD DOCTOR");
        addDoctorBtn.setOnAction(e->addCustomDoctor());


        // DELETE ALL DOCTORS BUTTON
        Button deleteDoctorsBtn=new Button("DELETE ALL DOCTORS");
        deleteDoctorsBtn.setOnAction(e->deleteAllDoctors());

        // ADD SAMPLE DATA BUTTON
        Button addSampleDataBtn = new Button("ADD SAMPLE DATA");
        addSampleDataBtn.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white;");
        addSampleDataBtn.setOnAction(e -> addSampleData());

        VBox doctorBtns = new VBox(10, addDoctorBtn, deleteDoctorsBtn, addSampleDataBtn);
        doctorBtns.setPadding(new Insets(10));
        doctorBtns.setPrefWidth(200);

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
        rightBtns.setPrefWidth(200);


        HBox actions = new HBox(10, doctorBtns, rightBtns);


        doctorsPanel = new VBox(10, new Label("Doctors:"), doctorListView);
        officesPanel = new VBox(10, new Label("Offices:"), officeListView);

        StackPane content = new StackPane(doctorsPanel, patientsPanel, officesPanel);

        // default view: doctors
        showPanel(doctorsPanel, patientsPanel, officesPanel);

//LIST CLICK HANDLERS
        doctorListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) showDoctorDetails(newV.getId());
        });

        officeListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) showOfficeDetails(newV.getId());
        });

        //NAV BUTTONS ACTIONS
        doctorsListBtn.setOnAction(e -> showPanel(doctorsPanel, patientsPanel, officesPanel));
        patientsListBtn.setOnAction(e -> showPanel(patientsPanel, doctorsPanel, officesPanel));
        officesListBtn.setOnAction(e -> showPanel(officesPanel, doctorsPanel, patientsPanel));

        root.getChildren().addAll(nav, actions, content);

        stage.setScene(new Scene(root));
        stage.setWidth(650);
        stage.setHeight(780);
        stage.setTitle("Clinic Application");
        stage.show();

        //LOAD LISTS
        loadDoctors();
        patientsPanel.loadPatients();
        loadOffices();
    }

    private void showPanel(VBox show, VBox hide1, VBox hide2) {
        show.setVisible(true);
        show.setManaged(true);

        hide1.setVisible(false);
        hide1.setManaged(false);

        hide2.setVisible(false);
        hide2.setManaged(false);
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
            List<DoctorListDto> doctorList = doctorApi.getDoctors();
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
            DoctorListDto doctor = doctorApi.getDoctorById(id);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Doctor Details");
            dialog.setHeaderText(doctor.getFirstName() + " " + doctor.getLastName());
            VBox content=new VBox(10);
            Label specializationLabel=new Label("Specialization: " +  doctor.getSpecialization());
            Label addressLabel=new Label("Address: " +  doctor.getAddress());

            List<DutyDto> duties =dutyApi.getDuties().stream().filter(d->d.getDoctorId().equals(doctor.getId())).toList();
            List<OfficeDto> offices=officeApi.getOffices();
            ListView<DutyDto> dutyListView = new ListView<>();
            dutyListView.getItems().addAll(duties);

            dutyListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(DutyDto duty, boolean empty) {
                    super.updateItem(duty, empty);
                    if (empty || duty == null) {
                        setText(null);
                    } else {
                        OfficeDto office = offices.stream()
                                .filter(d -> d.getId().equals(duty.getOfficeId()))
                                .findFirst()
                                .orElse(null);

                        String room = office != null
                                ? "Office " + office.getRoomNumber()
                                : "Unknown office";

                        setText(
                                duty.getDayOfWeek() + " | " +
                                        room + " | " +
                                        duty.getStartTime() + " - " +
                                        duty.getEndTime()
                        );
                    }
                }
            });
            Button deleteDutyButton = new Button("Delete selected duty");
            deleteDutyButton.setOnAction(e -> {
                DutyDto selected = dutyListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    showMessage("Select a duty to delete.");
                    return;
                }
                try {
                    dutyApi.deleteDuty(selected.getId());
                    dutyListView.getItems().remove(selected);
                    showMessage("Duty deleted.");
                } catch (Exception ex) {
                    showMessage("Error deleting duty: " + ex.getMessage());
                }
            });
            content.getChildren().addAll(specializationLabel, addressLabel, new Label("Duties: "),dutyListView, deleteDutyButton);
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
    }

    private void addDutyOrShowError(DoctorListDto doctor, OfficeDto office, DayOfWeek day, LocalTime start, LocalTime end) {
        DutyDto duty = new DutyDto();
        duty.setDoctorId(doctor.getId());
        duty.setOfficeId(office.getId());
        duty.setDayOfWeek(day);
        duty.setStartTime(start);
        duty.setEndTime(end);

        try {
            dutyApi.addDuty(duty);
        } catch (Exception ex) {
            showMessage("Could not add duty for doctorId=" + doctor.getId() + ": " + ex.getMessage());
        }
    }

    // ADDING SAMPLE DATA
    private void addSampleData(){
        try{
            List<DoctorDto> doctorsToAdd = List.of(
                    makeDoctor("Anna", "Nowak", "00000000000", Specialization.CARDIOLOGY, "A 1"),
                    makeDoctor("Jan", "Kowalski", "11111111111", Specialization.CARDIOLOGY, "B 1"),
                    makeDoctor("Marta", "Zielińska", "22222222222", Specialization.CARDIOLOGY, "C 1"),
                    makeDoctor("Tomasz", "Nowak", "33333333333", Specialization.DERMATOLOGY, "D 1"),
                    makeDoctor("Mateusz", "Wiśniewski", "44444444444", Specialization.DERMATOLOGY, "E 1"),
                    makeDoctor("Andrzej", "Dąb", "55555555555", Specialization.ORTHOPEDICS, "F 1"),
                    makeDoctor("Karolina", "Kamień", "66666666666", Specialization.PEDIATRICS, "G 1")
            );

            for (DoctorDto doctor : doctorsToAdd) {
                doctorApi.addDoctor(doctor);
            }

            List<OfficeDto> officesToAdd = List.of(
                    makeOffice(101),
                    makeOffice(102),
                    makeOffice(103)
            );
            for (OfficeDto o : officesToAdd) {
                try {
                    officeApi.addOffice(o);
                } catch (RoomNumberDuplicationException ignored) {
                }
            }

            List<PatientDto> patientsToAdd = List.of(
                    makePatient("Piotr", "Lis", "77777777777", "Kraków"),
                    makePatient("Alicja", "Wójcik", "88888888888", "Warszawa"),
                    makePatient("Kasia", "Krawczyk", "99999999999", "Gdańsk"),
                    makePatient("Ola", "Mazur", "12121212121", "Wrocław"),
                    makePatient("Bartek", "Zając", "13131313131", "Poznań")
            );

            for (PatientDto p : patientsToAdd) {
                try {
                    patientApi.addPatient(p);
                } catch (PeselDuplicationException ignored) {
                }
            }
            List<DoctorListDto> doctors = doctorApi.getDoctors().stream()
                    .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                    .toList();

            List<OfficeDto> offices = officeApi.getOffices().stream()
                    .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                    .toList();

            if (doctors.size() < 7 || offices.size() < 3) {
                showMessage("Not enough doctors/offices to assign duties. Doctors=" + doctors.size() + ", Offices=" + offices.size());
                loadDoctors();
                loadOffices();
                patientsPanel.loadPatients();
                return;
            }

            DoctorListDto  d1 = doctors.get(0);
            DoctorListDto  d2 = doctors.get(1);
            DoctorListDto  d3 = doctors.get(2);
            DoctorListDto  d4 = doctors.get(3);
            DoctorListDto  d5 = doctors.get(4);
            DoctorListDto  d6 = doctors.get(5);
//            Doctor d7 = doctors.get(6);

            OfficeDto o1 = offices.get(0);
            OfficeDto o2 = offices.get(1);
            OfficeDto o3 = offices.get(2);

            addDutyOrShowError(d1, o1, DayOfWeek.MONDAY,    LocalTime.of(8, 0),  LocalTime.of(10, 0));
            addDutyOrShowError(d1, o2, DayOfWeek.MONDAY,    LocalTime.of(10, 0), LocalTime.of(12, 0));
            addDutyOrShowError(d1, o3, DayOfWeek.MONDAY,    LocalTime.of(12, 0), LocalTime.of(14, 0));

            addDutyOrShowError(d2, o1, DayOfWeek.TUESDAY,   LocalTime.of(8, 0),  LocalTime.of(10, 0));
            addDutyOrShowError(d2, o2, DayOfWeek.TUESDAY,   LocalTime.of(10, 0), LocalTime.of(12, 0));
            addDutyOrShowError(d2, o3, DayOfWeek.TUESDAY,   LocalTime.of(12, 0), LocalTime.of(14, 0));

            addDutyOrShowError(d3, o1, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0),  LocalTime.of(10, 0));
            addDutyOrShowError(d3, o2, DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
            addDutyOrShowError(d3, o3, DayOfWeek.WEDNESDAY, LocalTime.of(12, 0), LocalTime.of(14, 0));

            addDutyOrShowError(d4, o1, DayOfWeek.THURSDAY,  LocalTime.of(8, 0),  LocalTime.of(10, 0));
            addDutyOrShowError(d4, o2, DayOfWeek.THURSDAY,  LocalTime.of(10, 0), LocalTime.of(12, 0));

            addDutyOrShowError(d5, o3, DayOfWeek.FRIDAY,    LocalTime.of(8, 0),  LocalTime.of(10, 0));
            addDutyOrShowError(d5, o1, DayOfWeek.FRIDAY,    LocalTime.of(10, 0), LocalTime.of(12, 0));

            addDutyOrShowError(d6, o2, DayOfWeek.FRIDAY,    LocalTime.of(12, 0), LocalTime.of(14, 0));

            List<PatientListDto> patients = patientApi.getPatients().stream()
                    .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                    .toList();

            PatientListDto p1 = patients.get(0);
            PatientListDto p2 = patients.get(1);
            PatientListDto p3 = patients.get(2);
            PatientListDto p4 = patients.get(3);


            LocalDate monday    = nextDateForDay(DayOfWeek.MONDAY);
            LocalDate tuesday   = nextDateForDay(DayOfWeek.TUESDAY);
            LocalDate wednesday = nextDateForDay(DayOfWeek.WEDNESDAY);
            LocalDate thursday  = nextDateForDay(DayOfWeek.THURSDAY);
            LocalDate friday    = nextDateForDay(DayOfWeek.FRIDAY);


            addSampleAppointment(p1, d1, monday, LocalTime.of(8, 0),  LocalTime.of(8, 15), o1);
            addSampleAppointment(p1, d1, monday, LocalTime.of(12, 15), LocalTime.of(12, 30), o3);
            addSampleAppointment(p1, d2, tuesday, LocalTime.of(10, 0), LocalTime.of(10, 15), o2);

            addSampleAppointment(p2, d2, tuesday, LocalTime.of(10, 15), LocalTime.of(10, 30), o2);
            addSampleAppointment(p2, d3, wednesday, LocalTime.of(12, 0),  LocalTime.of(12, 15), o3);
            addSampleAppointment(p2, d3, wednesday, LocalTime.of(12, 15), LocalTime.of(12, 30), o3);

            addSampleAppointment(p3, d4, thursday, LocalTime.of(8, 0),  LocalTime.of(8, 15), o1);
            addSampleAppointment(p3, d4, thursday, LocalTime.of(8, 15), LocalTime.of(8, 30), o1);

            addSampleAppointment(p4, d5, friday, LocalTime.of(10, 0), LocalTime.of(10, 15), o1);

            loadDoctors();
            loadOffices();
            patientsPanel.loadPatients();
            showMessage("Sample data added successfully!");

        }catch (Exception ex){
            showMessage("Unexpected error: " + ex.getMessage());

        }
    }

    private LocalDate nextDateForDay(DayOfWeek day) {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() != day) {
            date = date.plusDays(1);
        }
        return date;
    }

    private void addSampleAppointment(
            PatientListDto patient,
            DoctorListDto doctor,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            OfficeDto office
    ) {
        try {
            AppointmentDto appt = new AppointmentDto();
            appt.setPatientId(patient.getId());
            appt.setDoctorId(doctor.getId());
            appt.setDate(date);
            appt.setStartTime(start);
            appt.setEndTime(end);
            appt.setOfficeId(office.getId());

            appointmentApi.addAppointment(appt);
        } catch (Exception ex) {
            showMessage("Error adding appointment: " + ex.getMessage());
        }
    }

    private DoctorDto makeDoctor(String firstName, String lastName, String pesel, Specialization spec, String address) {
        DoctorDto d = new DoctorDto();
        d.setFirstName(firstName);
        d.setLastName(lastName);
        d.setPesel(pesel);
        d.setSpecialization(spec);
        d.setAddress(address);
        return d;
    }

    private OfficeDto makeOffice(int roomNumber) {
        OfficeDto o = new OfficeDto();
        o.setRoomNumber(roomNumber);
        return o;
    }

    private PatientDto makePatient(String firstName, String lastName, String pesel, String address) {
        PatientDto p = new PatientDto();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setPesel(pesel);
        p.setAddress(address);
        return p;
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
                    DoctorDto doctor = new DoctorDto();
                    doctor.setFirstName(firstNameField.getText());
                    doctor.setLastName(lastNameField.getText());
                    doctor.setPesel(peselField.getText());
                    doctor.setSpecialization(specializationBox.getValue());
                    doctor.setAddress(addressField.getText());

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

    //DELETE DOCTORS
    private void deleteAllDoctors(){
        try{
            List<DoctorListDto> doctors=doctorApi.getDoctors();
            for(DoctorListDto doctor:doctors){
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
                    PatientDto patient = new PatientDto();
                    patient.setFirstName(firstNameField.getText());
                    patient.setLastName(lastNameField.getText());
                    patient.setPesel(peselField.getText());
                    patient.setAddress(addressField.getText());

                    patientApi.addPatient(patient);

                    patientsPanel.loadPatients();
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
            List<OfficeDto> officesList = officeApi.getOffices();
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
            OfficeDto office=officeApi.getOfficeById(id);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Office Details");
            dialog.setHeaderText("Room: " + office.getRoomNumber());
            VBox content=new VBox(10);

            List<DutyDto> duties =dutyApi.getDuties().stream().filter(d->d.getOfficeId().equals(office.getId())).toList();
            List<DoctorListDto> doctors=doctorApi.getDoctors();
            ListView<DutyDto> dutyListView = new ListView<>();
            dutyListView.getItems().addAll(duties);
            dutyListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(DutyDto duty, boolean empty) {
                    super.updateItem(duty, empty);
                    if (empty || duty == null) {
                        setText(null);
                    } else {
                        DoctorListDto doctor = doctors.stream()
                                .filter(d -> d.getId().equals(duty.getDoctorId()))
                                .findFirst()
                                .orElse(null);

                        String doctorName = doctor != null
                                ? doctor.getFirstName() + " " + doctor.getLastName()
                                : "Unknown";

                        setText(
                                doctorName + " | " +
                                        duty.getDayOfWeek() + " | " +
                                        duty.getStartTime() + " - " +
                                        duty.getEndTime()
                        );
                    }
                }
            });
            Button deleteDutyButton = new Button("Delete selected duty");

            deleteDutyButton.setOnAction(e -> {
                DutyDto selected = dutyListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    showMessage("Select a duty to delete.");
                    return;
                }

                try {
                    dutyApi.deleteDuty(selected.getId());
                    dutyListView.getItems().remove(selected);
                    showMessage("Duty deleted.");
                } catch (Exception ex) {
                    showMessage("Error deleting duty: " + ex.getMessage());
                }
            });
            content.getChildren().addAll(new Label("Duties: "),dutyListView,deleteDutyButton);
            dialog.getDialogPane().setContent(content);
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
                    OfficeDto office = new OfficeDto();
                    office.setRoomNumber(parseInt(roomNumberField.getText()));
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

        ListView<DoctorListDto> availableDoctorsView = new ListView<>();
        ListView<OfficeDto> availableOfficesView = new ListView<>();

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
                        List<DoctorListDto> doctors = doctorApi.getDoctors();
                        List<OfficeDto> offices = officeApi.getOffices();

                        availableDoctorsView.getItems().setAll(doctors.stream()
                                .filter(d -> isDoctorAvailable(d, dayBox.getValue(), startTime, endTime, duties)).toList());
                        availableOfficesView.getItems().setAll(offices.stream()
                                .filter(o -> isOfficeAvailable(o, dayBox.getValue(), startTime, endTime, duties)).toList());

                    } catch (Exception ex) {
                        showMessage("Invalid input: " + ex.getMessage());
                    }
                });
        assignButton.setOnAction(e->{
            DoctorListDto doctor = availableDoctorsView.getSelectionModel().getSelectedItem();
            OfficeDto office = availableOfficesView.getSelectionModel().getSelectedItem();
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

    private boolean isDoctorAvailable(DoctorListDto doctor, DayOfWeek day, LocalTime start, LocalTime end, List<DutyDto> duties) {
        return duties.stream()
                .filter(d -> d.getDoctorId().equals(doctor.getId()))
                .filter(d->d.getDayOfWeek().equals(day))
                .noneMatch(d ->
                        d.getStartTime().isBefore(end)
                                && d.getEndTime().isAfter(start)
                );
    }
    private boolean isOfficeAvailable(OfficeDto office, DayOfWeek day, LocalTime start, LocalTime end, List<DutyDto> duties) {
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
