package controller;

import model.Service;
import model.Appointment;
import dao.AppointmentDAO;
import dao.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingController {

    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private TextField appointmentTimeField;

    private Service selectedService;
    private AppointmentDAO appointmentDAO;

    // Set the selected service from CustomerController
    public void setService(Service service) {
        this.selectedService = service;
    }

    // Handle confirm booking action
    @FXML
    private void handleConfirmBooking() {
        if (appointmentDatePicker.getValue() != null && !appointmentTimeField.getText().isEmpty()) {
            // Get the input values
            LocalDate appointmentDate = appointmentDatePicker.getValue();
            LocalTime appointmentTime = LocalTime.parse(appointmentTimeField.getText());

            try {
                // Create appointment and save to DB
                Connection connection = DBConnection.getConnection();
                if (connection != null) {
                    appointmentDAO = new AppointmentDAO(connection);
                    Appointment appointment = new Appointment(
                            selectedService.getServiceId(),  // Service ID
                            1,  // Placeholder customer ID (should be dynamically set)
                            appointmentDate,
                            appointmentTime
                    );
                    appointmentDAO.bookAppointment(appointment);
                }

                // Close the dialog after booking
                Stage stage = (Stage) appointmentDatePicker.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Show an error message if the date or time is missing
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the fields.");
            alert.showAndWait();
        }
    }
}
