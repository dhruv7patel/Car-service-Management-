package controller;

import model.Service;
import model.Appointment;
import dao.AppointmentDAO;
import dao.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BookingController {

    @FXML
    private DatePicker appointmentDatePicker;

    private Service selectedService;
    private AppointmentDAO appointmentDAO;
    private int customerId;  // Set this dynamically from the logged-in customer
    
    public BookingController() {
		try {
			Connection connection = DBConnection.getConnection();
			this.appointmentDAO = new AppointmentDAO(connection); // Initialize appointmentDAO
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Set the selected service and customer ID
    public void setService(Service service, int customerId) {
        this.selectedService = service;
        this.customerId = customerId;
    }

    // Handle confirm booking action
    @FXML
    private void handleConfirmBooking() {
        if (appointmentDatePicker.getValue() != null && selectedService != null) {
            LocalDate appointmentDate = appointmentDatePicker.getValue();

            try {
                // First create the appointment
                Appointment appointment = new Appointment(
                        selectedService.getServiceId(), 
                        customerId,  
                        appointmentDate,
                        selectedService.getName());
                
                appointmentDAO.bookAppointment(appointment);
                
                // Then assign a technician
                int technicianId = getAvailableTechnician();
                if (technicianId != -1) {
                    appointmentDAO.assignAppointmentToTechnician(appointment.getAppointmentId(), technicianId);
                    showAlert("Appointment Confirmed", 
                        "Your appointment has been successfully booked and assigned to a technician.");
                } else {
                    showAlert("Appointment Confirmed", 
                        "Your appointment has been booked. A technician will be assigned soon.");
                }

                Stage stage = (Stage) appointmentDatePicker.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to book the appointment: " + e.getMessage());
            }
        } else {
            showAlert("Invalid Input", "Please select both a service and an appointment date.");
        }
    }
    // Get a random available technician
    private int getAvailableTechnician() throws SQLException {
        String query = "SELECT id FROM users WHERE role = 'TECHNICIAN' ORDER BY RAND() LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
