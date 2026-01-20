package pl.edu.agh.to.clinic.app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.agh.to.clinic.appointment.AppointmentApiClient;
import pl.edu.agh.to.clinic.appointment.AppointmentDto;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.duty.DutyApiClient;
import pl.edu.agh.to.clinic.duty.DutyDto;
import pl.edu.agh.to.clinic.doctor.DoctorApiClient;
import pl.edu.agh.to.clinic.doctor.DoctorListDto;
import pl.edu.agh.to.clinic.office.OfficeApiClient;
import pl.edu.agh.to.clinic.patient.PatientApiClient;
import pl.edu.agh.to.clinic.patient.PatientListDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PatientsPanel extends VBox {

    private final PatientApiClient patientApi = new PatientApiClient();
    private final AppointmentApiClient appointmentApi = new AppointmentApiClient();
    private final DoctorApiClient doctorApi = new DoctorApiClient();
    private final DutyApiClient dutyApi = new DutyApiClient();
    private final OfficeApiClient officeApi = new OfficeApiClient();

    private final ListView<PatientListDto> patientsList = new ListView<>();


    public PatientsPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
                new Label("Patients"),
                patientsList
        );
        loadPatients();
        setupListeners();
    }


    protected void loadPatients() {
        try {
            List<PatientListDto> patients = patientApi.getPatients();
            patientsList.getItems().setAll(patients);
        } catch (Exception ex) {
            showError("Error loading patients: " + ex.getMessage());
        }
    }


    private void setupListeners() {
        patientsList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, patient) -> {
                    if (patient != null) {
                        showPatientDetails(patient);
                        Platform.runLater(() -> {
                            if (!patientsList.getItems().isEmpty() && patientsList.getScene() != null) {
                                patientsList.getSelectionModel().clearSelection();
                            }
                        });
                    }
                });
    }

    private void scheduleAppointment(PatientListDto patient) {
        Stage stage = new Stage();
        stage.setTitle("Schedule Appointment for " + patient.getFirstName() + " " + patient.getLastName());

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        ComboBox<Specialization> specializationBox = new ComboBox<>();
        specializationBox.getItems().addAll(Specialization.values());
        specializationBox.setPromptText("Select specialization");

        ComboBox<DoctorListDto> doctorBox = new ComboBox<>();
        doctorBox.setPromptText("Select doctor");
        doctorBox.setDisable(true);

        specializationBox.setOnAction(e -> {
            Specialization specialization = specializationBox.getValue();

            Runnable disableDoctorBoxNoDoctors = () -> {
                doctorBox.getItems().clear();
                doctorBox.setValue(null);
                doctorBox.setDisable(true);
                doctorBox.setPromptText("No doctors found with the selected specialization");
            };

            doctorBox.getItems().clear();
            doctorBox.setValue(null);
            doctorBox.setDisable(true);
            doctorBox.setPromptText("Select doctor");

            if (specialization == null) {
                return;
            }

            try {
                List<DoctorListDto> doctors = doctorApi.getDoctorsBySpecialization(specialization);

                if (doctors == null || doctors.isEmpty()) {
                    disableDoctorBoxNoDoctors.run();
                    return;
                }

                doctorBox.getItems().setAll(doctors);
                doctorBox.setDisable(false);
                doctorBox.setPromptText("Select doctor");

            } catch (Exception ex) {
                showError("Could not fetch doctors for selected specialization: " + ex.getMessage());
                disableDoctorBoxNoDoctors.run();
            }
        });




        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(7));

        ListView<String> availableSlotsList = new ListView<>();

        Button checkSlotsBtn = new Button("Show available slots");
        checkSlotsBtn.setOnAction(e -> {
            DoctorListDto doctor = doctorBox.getValue();
            if (doctor == null) {
                showInfo("Please select a doctor first.");
                return;
            }

            try {
                List<String> slots = generateAvailableSlots(doctor.getId(), startDatePicker.getValue(), endDatePicker.getValue());
                if (slots.isEmpty()) {
                    showInfo("No available slots for the selected period.");
                }
                availableSlotsList.getItems().setAll(slots);
            } catch (Exception ex) {
                showError("Error generating slots: " + ex.getMessage());
            }
        });

        Button scheduleBtn = new Button("Schedule Appointment");
        scheduleBtn.setOnAction(e -> {
            String selectedSlot = availableSlotsList.getSelectionModel().getSelectedItem();
            DoctorListDto doctor = doctorBox.getValue();
            if (selectedSlot == null || doctor == null) {
                showInfo("Please select a slot and doctor.");
                return;
            }

            try {
                String[] parts = selectedSlot.split(" ");
                LocalDate date = LocalDate.parse(parts[0]);
                LocalTime startTime = LocalTime.parse(parts[1]);
                LocalTime endTime = LocalTime.parse(parts[2]);

                var appt = new AppointmentDto();
                appt.setPatientId(patient.getId());
                appt.setDoctorId(doctor.getId());
                appt.setDate(date);
                appt.setStartTime(startTime);
                appt.setEndTime(endTime);
                appt.setOfficeId(getOfficeForDoctorAndTime(doctor.getId(), date, startTime, endTime));

                appointmentApi.addAppointment(appt);

                showInfo("Appointment scheduled successfully!");
                showPatientDetails(patient);// odśwież listę
                stage.close();
            } catch (Exception ex) {
                showError("Error scheduling appointment: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Select specialization:"), specializationBox,
                new Label("Select doctor:"), doctorBox,
                new Label("Start date:"), startDatePicker,
                new Label("End date:"), endDatePicker,
                checkSlotsBtn,
                new Label("Available slots:"), availableSlotsList,
                scheduleBtn
        );

        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }

    private List<String> generateAvailableSlots(Long doctorId, LocalDate startDate, LocalDate endDate) throws Exception {
        List<String> slots = new java.util.ArrayList<>();

        List<DutyDto> duties = dutyApi.getDuties().stream()
                .filter(d -> d.getDoctorId().equals(doctorId))
                .toList();

        List<AppointmentDto> appointments = appointmentApi.getAppointments().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .toList();

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek dow = date.getDayOfWeek();
            for (DutyDto duty : duties) {
                if (duty.getDayOfWeek() != dow) continue;

                LocalTime slotStart = duty.getStartTime();
                LocalTime slotEnd = slotStart.plusMinutes(15);

                while (!slotEnd.isAfter(duty.getEndTime())) {
                    LocalTime fStart = slotStart;
                    LocalTime fEnd = slotEnd;
                    LocalDate fDate = date;

                    boolean occupied = appointments.stream()
                            .anyMatch(a -> a.getDate().equals(fDate) &&
                                    a.getStartTime().isBefore(fEnd) &&
                                    a.getEndTime().isAfter(fStart));

                    if (!occupied) {
                        slots.add(fDate + " " + slotStart + " " + slotEnd);
                    }

                    slotStart = slotStart.plusMinutes(15);
                    slotEnd = slotStart.plusMinutes(15);
                }
            }
            date = date.plusDays(1);
        }

        return slots;
    }

    private Long getOfficeForDoctorAndTime(Long doctorId, LocalDate date, LocalTime start, LocalTime end) throws Exception {
        return dutyApi.getDuties().stream()
                .filter(d -> d.getDoctorId().equals(doctorId))
                .filter(d -> d.getDayOfWeek() == date.getDayOfWeek())
                .filter(d -> !start.isBefore(d.getStartTime()) && !end.isAfter(d.getEndTime()))
                .map(DutyDto::getOfficeId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No duty found for this doctor at selected time"));
    }

    private void showPatientDetails(PatientListDto patient) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Patient details");
        dialog.setHeaderText(patient.getFirstName() + " " + patient.getLastName());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label addressLabel = new Label("Address: " + patient.getAddress());

        ListView<AppointmentDto> appointmentsList = new ListView<>();
        loadAppointmentsIntoList(patient.getId(), appointmentsList);

        appointmentsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AppointmentDto appt, boolean empty) {
                super.updateItem(appt, empty);
                if (empty || appt == null) {
                    setText(null);
                } else {
                    try {
                        var doctor = doctorApi.getDoctorById(appt.getDoctorId());
                        var office = officeApi.getOfficeById(appt.getOfficeId());

                        setText(
                                appt.getDate() + " " +
                                        appt.getStartTime() + " - " + appt.getEndTime() +
                                        " | Doctor: " + doctor.getLastName() +
                                        " | Room: " + office.getRoomNumber()
                        );
                    } catch (Exception e) {
                        setText("Error loading appointment details");
                    }
                }
            }
        });

        Button deleteAppointmentBtn = new Button("Delete appointment");
        deleteAppointmentBtn.setOnAction(e -> {
            AppointmentDto appt = appointmentsList.getSelectionModel().getSelectedItem();
            if (appt == null) {
                showInfo("Select appointment to delete");
                return;
            }
            try {
                appointmentApi.deleteAppointment(appt.getId());
                loadAppointmentsIntoList(patient.getId(), appointmentsList);
                showInfo("Appointment deleted successfully.");
            } catch (Exception ex) {
                showError("Error deleting appointment: " + ex.getMessage());
            }
        });

        Button addAppointmentBtn = new Button("Schedule appointment");
        addAppointmentBtn.setOnAction(e -> {
            dialog.close();
            scheduleAppointment(patient);
        });

        Button deletePatientBtn = new Button("Delete patient");
        deletePatientBtn.setOnAction(e -> {
            try {
                patientApi.deletePatientById(patient.getId());
                loadPatients();
                Platform.runLater(() -> {
                    if (!patientsList.getItems().isEmpty() && patientsList.getScene() != null) {
                        patientsList.getSelectionModel().clearSelection();
                    }
                });
                dialog.close();
                showInfo("Patient deleted successfully.");
            } catch (Exception ex) {
                showError("Cannot delete patient.\nThe patient has existing appointments.");
            }
        });

        content.getChildren().addAll(
                addressLabel,
                new Label("Appointments:"),
                appointmentsList,
                deleteAppointmentBtn,
                addAppointmentBtn,
                new Separator(),
                deletePatientBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setWidth(500);
        dialog.setHeight(500);

        dialog.showAndWait();
    }
    private void loadAppointmentsIntoList(long patientId, ListView<AppointmentDto> list) {
        try {
            List<AppointmentDto> appointments = appointmentApi.getAppointments().stream()
                    .filter(a -> a.getPatientId().equals(patientId))
                    .toList();
            list.getItems().setAll(appointments);

            if (!appointments.isEmpty() && list.getScene() != null) {
                list.getSelectionModel().selectFirst();
            }

        } catch (Exception ex) {
            showError("Error loading appointments: " + ex.getMessage());
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }
}
