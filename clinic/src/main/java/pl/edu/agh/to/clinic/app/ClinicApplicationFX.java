package pl.edu.agh.to.clinic.app;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorApiClient;
import javafx.application.Application;
import pl.edu.agh.to.clinic.doctor.Specialization;

import java.util.List;

public class ClinicApplicationFX extends Application{
    private final DoctorApiClient api=new DoctorApiClient();
    private final ListView<Doctor> doctorListView=new ListView<>();
    private final TextArea textArea=new TextArea();

    @Override
    public void start(Stage stage) throws Exception{
        // VERTICAL LAYOUT
        VBox root=new VBox(10);

        // ADD DOCTOR BUTTON
        Button addDoctorsBtn=new Button("ADD DOCTORS");
        addDoctorsBtn.setOnAction(e->addDoctors());
        root.getChildren().add(0, addDoctorsBtn);

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
        }catch (Exception e){
            System.err.println("Error loading doctors list: "+e.getMessage());
        }
    }

    // DOCTOR DETAILS
    private void showDoctorDetails(long id){
        try{
            Doctor doctor=api.getDoctorById(id);
            textArea.setText(
                    "Doctor details:\n"+
                    "Name: "+doctor.getFirstName()+" "+doctor.getLastName()+"\n"+
                    "Specialization: "+doctor.getSpecialization()+"\n"+
                            "Address: "+doctor.getAddress()
            );
        }catch (Exception e){
            textArea.setText("Error loading doctor details: "+e.getMessage());
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
        }catch (Exception e){
            textArea.setText("Error adding doctors: "+e.getMessage());
        }}

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
        } catch (Exception e){
            textArea.setText("Error deleting doctor: "+e.getMessage());
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
        }catch (Exception e){
            textArea.setText("Error deleting doctors: "+e.getMessage());
        }
    }

    // LAUNCH JAVAFX APP
    public static void main(String[] args) {
        launch();
    }
}
