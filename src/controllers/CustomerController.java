package controller;

import dao.ServiceDAO;
import dao.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Service;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerController {

    @FXML
    private Button logoutBtn;
    @FXML
    private TableView<Service> serviceTable;
    @FXML
    private TableColumn<Service, String> serviceNameColumn;
    @FXML
    private TableColumn<Service, String> serviceDescriptionColumn;
    @FXML
    private TableColumn<Service, Double> servicePriceColumn;  

    @FXML
    private Button bookServiceBtn;  // Button to book service

    private ServiceDAO serviceDAO;

    // Constructor
    public CustomerController() {
        try {
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                serviceDAO = new ServiceDAO(connection);
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialize method to set up the TableView
    @FXML
    public void initialize() {
        // Set up table columns using PropertyValueFactory for regular getters
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        try {
            // Load services from the database and set them in the table
            List<Service> services = serviceDAO.getAllServices();
            ObservableList<Service> serviceList = FXCollections.observableArrayList(services);
            serviceTable.setItems(serviceList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add an action listener to the book service button
        bookServiceBtn.setOnAction(event -> handleBookService());
    }

    // Handle logout action
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Service Management System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle booking service action
    @FXML
    private void handleBookService() {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            // Open a dialog to get booking details
            openBookingDialog(selectedService);
        } else {
            // Alert if no service is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Service Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a service to book.");
            alert.showAndWait();
        }
    }

    // Open a dialog to get the booking details (e.g., date, time)
    
    private void openBookingDialog(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking.fxml"));
            Parent root = loader.load();

            // Pass selected service to the booking dialog
            BookingController bookingDialogController = loader.getController();
            bookingDialogController.setService(service);

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Book Service");
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

