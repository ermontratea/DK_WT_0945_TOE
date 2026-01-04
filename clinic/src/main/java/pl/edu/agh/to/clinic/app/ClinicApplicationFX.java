package pl.edu.agh.to.clinic.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorApiClient;
import javafx.application.Application;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

public class ClinicApplicationFX extends Application{
    private final DoctorApiClient api=new DoctorApiClient();
    private final ListView<Doctor> doctorListView=new ListView<>();
    private final TextArea textArea=new TextArea();

    @Override
    public void start(Stage stage) throws Exception{
        // VERTICAL LAYOUT
        VBox root=new VBox(10);

        VBox addDoctorBox=new VBox(5);
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

        Button addDoctorBtn=new Button("ADD DOCTOR");

        addDoctorBtn.setOnAction(e-> {
            try {
                Doctor doctor = new Doctor(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        peselField.getText(),
                        specializationBox.getValue(),
                        addressField.getText()
                );
                api.addDoctor(doctor);
                textArea.setText("Doctor added successfully!");
                loadDoctors();
            }catch (PeselDuplicationException ex){
                    textArea.setText(ex.getMessage());
            }
            catch (RuntimeException ex) {
                textArea.setText("Validation errors:\n" + ex.getMessage());
            }
            catch (Exception ex) {
                    textArea.setText("Unexpected error: " + ex.getMessage());
            }
    });

        addDoctorBox.getChildren().addAll(
                firstNameField,
                lastNameField,
                peselField,
                specializationBox,
                addressField,
                addDoctorBtn
        );
        addDoctorBox.setPadding(new Insets(20,20,20,20));
        TitledPane addDoctorPane=new TitledPane("Enter doctor information", addDoctorBox);
        addDoctorPane.setCollapsible(false);

        root.getChildren().addAll(addDoctorPane);

        // ADD DOCTOR BUTTON
        Button addDoctorsBtn=new Button("ADD DOCTORS");
        addDoctorsBtn.setOnAction(e->addDoctors());
        root.getChildren().add(addDoctorsBtn);

        // DELETE SELECTED DOCTOR BUTTON
        Button deleteSelectedDoctorBtn=new Button("DELETE SELECTED DOCTOR");
        deleteSelectedDoctorBtn.setOnAction(e->deleteSelectedDoctor());
        root.getChildren().add(deleteSelectedDoctorBtn);

        // DELETE ALL DOCTORS BUTTON
        Button deleteDoctorsBtn=new Button("DELETE ALL DOCTORS");
        deleteDoctorsBtn.setOnAction(e->deleteAllDoctors());
        root.getChildren().add(deleteDoctorsBtn);

        // DOCTOR LIST
        doctorListView.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            if(newValue!=null) showDoctorDetails(newValue.getId()); //doctor details if clicked
        });
        textArea.setEditable(false);


        root.getChildren().addAll(doctorListView,textArea);
        stage.setScene(new Scene(root));
        stage.setTitle("Clinic Application");
        stage.show();

        //SHOW DOCTOR LIST
        loadDoctors();
    }

    // LOADING DOCTORS
    private void loadDoctors(){
        try {
            List<Doctor> doctorList = api.getDoctors();
            doctorListView.getItems().setAll(doctorList);
        }catch (RuntimeException ex){
        textArea.setText("Error loading doctors list: " + ex.getMessage());
        }catch (Exception ex){
            textArea.setText("Unexpected error: " + ex.getMessage());
        }
    }

    // DOCTOR DETAILS
    private void showDoctorDetails(long id){
//        try{
//            Doctor doctor=api.getDoctorById(id);
//            textArea.setText(
//                    "Doctor details:\n"+
//                    "Name: "+doctor.getFirstName()+" "+doctor.getLastName()+"\n"+
//                    "Specialization: "+doctor.getSpecialization()+"\n"+
//                            "Address: "+doctor.getAddress()
//            );
//        }catch (DoctorNotFoundException e){
//            textArea.setText("Error loading doctor details: "+e.getMessage());
//        }catch (Exception ex) {
//            textArea.setText("Unexpected error: " + ex.getMessage());
//        }
        try {
            Doctor doctor = api.getDoctorById(id);
            StringBuilder sb = new StringBuilder();
            sb.append("Doctor details:\n");
            sb.append("Name: ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName()).append("\n");
            sb.append("Specialization: ").append(doctor.getSpecialization()).append("\n");

            sb.append("\nDuties:\n");
            if (doctor.getDuties() != null && !doctor.getDuties().isEmpty()) {
                for (var d : doctor.getDuties()) {
                    sb.append(" - Room ").append(d.getOffice().getRoomNumber())
                            .append(": ").append(d.getStartTime().toLocalTime())
                            .append(" - ").append(d.getEndTime().toLocalTime()).append("\n");
                }
            } else {
                sb.append("No duties assigned.");
            }

            textArea.setText(sb.toString());
        } catch (Exception ex) {
            textArea.setText("Error: " + ex.getMessage());
        }
    }

    // ADDING DOCTORS
    private void addDoctors(){
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
                api.addDoctor(doctor);
            }
            textArea.setText("Doctors added successfully!");
            loadDoctors();
        }catch (DoctorNotFoundException e){
            textArea.setText("Error adding doctors: " + e.getMessage());
        }catch (Exception ex){
            textArea.setText("Unexpected error: " + ex.getMessage());
        }
    }

    // DELETE SELECTED DOCTOR
    private void deleteSelectedDoctor(){
        Doctor doctor=doctorListView.getSelectionModel().getSelectedItem();
        if(doctor==null){
            textArea.setText("No doctor selected!");
            return;
        }
        try {
            api.deleteDoctorById(doctor.getId());
            textArea.setText("Doctor " + doctor +" deleted successfully!");
            loadDoctors();
        } catch (DoctorNotFoundException e){
            textArea.setText("Error deleting doctor: "+e.getMessage());
        }catch (Exception ex){
            textArea.setText("Unexpected error: " + ex.getMessage());
        }
    }
    //DELETE DOCTORS
    private void deleteAllDoctors(){
        try{
            List<Doctor> doctors=api.getDoctors();
            for(Doctor doctor:doctors){
                api.deleteDoctorById(doctor.getId());
            }
            textArea.setText("All doctors deleted successfully!");
            loadDoctors();
        }catch (DoctorNotFoundException e){
            textArea.setText("Error deleting doctors: "+e.getMessage());
        }catch (Exception e) {
            textArea.setText("Unexpected error: " + e.getMessage());
        }
    }

    // LAUNCH JAVAFX APP
    public static void main(String[] args) {
        launch();
    }
}
