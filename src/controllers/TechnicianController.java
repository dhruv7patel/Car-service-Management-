package controller;

import dao.AppointmentDAO;
import dao.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TechnicianController {

    @FXML
    private TableView<Appointment> tasksTable;
    @FXML
    private TableColumn<Appointment, String> taskIdColumn;
    @FXML
    private TableColumn<Appointment, String> serviceNameColumn;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;
    @FXML
    private TableColumn<Appointment, String> actionsColumn;

    @FXML
    private Button logoutBtn;
    @FXML
    private TextArea recommendationTextArea;
    @FXML
    private VBox recommendationBox;
    @FXML
    private Button submitRecommendationBtn;

    private AppointmentDAO appointmentDAO;
    private int technicianId;
    private int selectedAppointmentId;

    public TechnicianController() {
        try {
            Connection connection = DBConnection.getConnection();
            this.appointmentDAO = new AppointmentDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize AppointmentDAO", e);
        }
    }

    public void setTechnician(User technician) {
        this.technicianId = technician.getId();
        loadAssignedAppointments();
    }

 // In your initialize() method:
    @FXML
    private void initialize() {
        try {
            // Initialize table columns
            taskIdColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.valueOf(cellData.getValue().getAppointmentId())));
            
            serviceNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getServiceName()));
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getAppointmentDate().toString()));
            
            statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus()));

            // Set up action column
            actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(""));
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button completeBtn = new Button("Complete");
                
                {
                    completeBtn.setOnAction(event -> {
                        Appointment appointment = getTableView().getItems().get(getIndex());
                        showRecommendationBox(appointment.getAppointmentId());
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Appointment appointment = getTableView().getItems().get(getIndex());
                        setGraphic("ASSIGNED".equals(appointment.getStatus()) ? completeBtn : null);
                    }
                }
            });

            recommendationBox.setVisible(false);
            loadAssignedAppointments();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Initialization failed: " + e.getMessage());
        }
    }

    private void showRecommendationBox(int appointmentId) {
        this.selectedAppointmentId = appointmentId;
        recommendationTextArea.clear();
        recommendationBox.setVisible(true);
    }

    @FXML
    private void handleSubmitRecommendation() {
        if (recommendationTextArea.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter recommendations before submitting");
            return;
        }

        try {
            appointmentDAO.updateAppointmentStatus(
                selectedAppointmentId,
                "COMPLETED",
                recommendationTextArea.getText().trim()
            );
            
            showAlert("Success", "Appointment marked as completed");
            recommendationBox.setVisible(false);
            loadAssignedAppointments();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Car Service Management System");
        } catch (IOException e) {
            showAlert("Error", "Failed to load login screen");
        }
    }

    private void loadAssignedAppointments() {
        try {
            List<Appointment> appointments = appointmentDAO.getAssignedAppointments(technicianId);
            tasksTable.setItems(FXCollections.observableArrayList(appointments));
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
