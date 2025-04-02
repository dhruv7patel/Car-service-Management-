package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Service;
import model.User;
import java.time.LocalDate;

public class AdminController {
    @FXML private Button logoutBtn;
    
    // Services tab components
    @FXML private TextField serviceNameField;
    @FXML private TextField serviceDescriptionField;
    @FXML private TextField servicePriceField;
    @FXML private TableView<Service> servicesTable;
    
    // Users tab components
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TableView<User> usersTable;
    
    // Reports tab components
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<String> reportsTable;
    
    private ObservableList<Service> services = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
    	roleComboBox.getItems().addAll("admin", "technician", "customer");
    	
        servicesTable.setItems(services);
        servicesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateServiceFields(newSelection);
                }
            });
        
        // Initialize users table
        usersTable.setItems(users);
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateUserFields(newSelection);
                }
            });
        
        // Load sample data (in a real app, this would come from a database)
        loadSampleData();
    }
    
    private void loadSampleData() {
        // Sample services
        services.add(new Service(1, "Oil Change", "Basic oil change service", 49.99));
        services.add(new Service(2, "Tire Rotation", "Rotate all four tires", 29.99));
        
        // Sample users
        users.add(new User("admin1", "pass123", "admin@example.com", "1234567890", "admin"));
        users.add(new User("tech1", "pass123", "tech@example.com", "0987654321", "technician"));
    }
    
    // Service management methods
    private void populateServiceFields(Service service) {
        serviceNameField.setText(service.getName());
        serviceDescriptionField.setText(service.getDescription());
        servicePriceField.setText(String.valueOf(service.getPrice()));
    }
    
    @FXML
    private void handleAddService() {
        try {
            Service service = new Service(
                services.size() + 1,
                serviceNameField.getText(),
                serviceDescriptionField.getText(),
                Double.parseDouble(servicePriceField.getText())
            );
            services.add(service);
            clearServiceFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid price.");
        }
    }
    
    @FXML
    private void handleUpdateService() {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            try {
                selectedService.setName(serviceNameField.getText());
                selectedService.setDescription(serviceDescriptionField.getText());
                selectedService.setPrice(Double.parseDouble(servicePriceField.getText()));
                servicesTable.refresh();
                clearServiceFields();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid price.");
            }
        } else {
            showAlert("No Selection", "Please select a service to update.");
        }
    }
    
    @FXML
    private void handleDeleteService() {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            services.remove(selectedService);
            clearServiceFields();
        } else {
            showAlert("No Selection", "Please select a service to delete.");
        }
    }
    
    private void clearServiceFields() {
        serviceNameField.clear();
        serviceDescriptionField.clear();
        servicePriceField.clear();
    }
    
    // User management methods
    private void populateUserFields(User user) {
        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        roleComboBox.setValue(user.getRole());
    }
    
    @FXML
    private void handleAddUser() {
        User user = new User(
            usernameField.getText(),
            passwordField.getText(),
            emailField.getText(),
            phoneField.getText(),
            roleComboBox.getValue()
        );
        user.setId(users.size() + 1);
        users.add(user);
        clearUserFields();
    }
    
    @FXML
    private void handleUpdateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setUsername(usernameField.getText());
            selectedUser.setPassword(passwordField.getText());
            selectedUser.setEmail(emailField.getText());
            selectedUser.setPhone(phoneField.getText());
            selectedUser.setRole(roleComboBox.getValue());
            usersTable.refresh();
            clearUserFields();
        } else {
            showAlert("No Selection", "Please select a user to update.");
        }
    }
    
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            users.remove(selectedUser);
            clearUserFields();
        } else {
            showAlert("No Selection", "Please select a user to delete.");
        }
    }
    
    private void clearUserFields() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }
    
    // Report generation methods
    @FXML
    private void handleGenerateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) {
            showAlert("Missing Dates", "Please select both start and end dates.");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            showAlert("Invalid Date Range", "Start date must be before end date.");
            return;
        }
        
        // In a real app, this would generate actual reports from database
        ObservableList<String> reportData = FXCollections.observableArrayList();
        reportData.add("Services Added\t" + services.size() + " services in the system");
        reportData.add("Users Registered\t" + users.size() + " users in the system");
        reportData.add("Time Period\tFrom " + startDate + " to " + endDate);
        
        reportsTable.setItems(reportData);
    }
    
    @FXML
    private void handleExportToPDF() {
        // In a real app, this would export the report to PDF
        showAlert("Export", "Report would be exported to PDF in a real implementation.");
    }
    
    // Common methods
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
