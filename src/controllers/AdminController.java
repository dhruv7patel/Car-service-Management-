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
import dao.DBConnection;
import dao.ServiceDAO;
import dao.UserDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
    
    private int adminId;
    private ServiceDAO serviceDAO;
    private UserDAO userDAO;
    
    private ObservableList<Service> services = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    
    public void setAdmin(User admin) {
        this.adminId = admin.getId();
        try {
            this.serviceDAO = new ServiceDAO(DBConnection.getConnection());
            this.userDAO = new UserDAO();
            loadDataFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to database.");
        }
    }

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
        
        usersTable.setItems(users);
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateUserFields(newSelection);
                }
            });
    }
    
    private void loadDataFromDatabase() {
        try {
            // Load services
            List<Service> serviceList = serviceDAO.getAllServices();
            services.setAll(serviceList);
            
            // Load users
            List<User> userList = userDAO.getAllUsers();
            users.setAll(userList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load data from database.");
        }
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
            if (serviceNameField.getText().isEmpty() || servicePriceField.getText().isEmpty()) {
                showAlert("Input Error", "Service name and price are required.");
                return;
            }

            Service service = new Service(
                0, // ID will be set by database
                serviceNameField.getText(),
                serviceDescriptionField.getText(),
                Double.parseDouble(servicePriceField.getText())
            );
            
            serviceDAO.addService(service);
            services.add(service);
            clearServiceFields();
            showAlert("Success", "Service added successfully.");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid price.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add service: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdateService() {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            try {
                if (serviceNameField.getText().isEmpty() || servicePriceField.getText().isEmpty()) {
                    showAlert("Input Error", "Service name and price are required.");
                    return;
                }

                selectedService.setName(serviceNameField.getText());
                selectedService.setDescription(serviceDescriptionField.getText());
                selectedService.setPrice(Double.parseDouble(servicePriceField.getText()));
                
                serviceDAO.updateService(selectedService);
                servicesTable.refresh();
                clearServiceFields();
                showAlert("Success", "Service updated successfully.");
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid price.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to update service: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a service to update.");
        }
    }
    
    @FXML
    private void handleDeleteService() {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            try {
                serviceDAO.deleteService(selectedService.getServiceId());
                services.remove(selectedService);
                clearServiceFields();
                showAlert("Success", "Service deleted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete service: " + e.getMessage());
            }
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
        try {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || 
                emailField.getText().isEmpty() || roleComboBox.getValue() == null) {
                showAlert("Input Error", "Username, password, email and role are required.");
                return;
            }

            User user = new User(
                usernameField.getText(),
                passwordField.getText(),
                emailField.getText(),
                phoneField.getText(),
                roleComboBox.getValue()
            );
            
            if (userDAO.registerUser(user)) {
                // Refresh user list
                List<User> userList = userDAO.getAllUsers();
                users.setAll(userList);
                clearUserFields();
                showAlert("Success", "User added successfully.");
            } else {
                showAlert("Error", "Failed to add user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add user: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || 
                    emailField.getText().isEmpty() || roleComboBox.getValue() == null) {
                    showAlert("Input Error", "Username, password, email and role are required.");
                    return;
                }

                selectedUser.setUsername(usernameField.getText());
                selectedUser.setPassword(passwordField.getText());
                selectedUser.setEmail(emailField.getText());
                selectedUser.setPhone(phoneField.getText());
                selectedUser.setRole(roleComboBox.getValue());
                
                if (userDAO.updateUser(selectedUser)) {
                    usersTable.refresh();
                    clearUserFields();
                    showAlert("Success", "User updated successfully.");
                } else {
                    showAlert("Error", "Failed to update user.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to update user: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a user to update.");
        }
    }
    
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                if (userDAO.deleteUser(selectedUser.getId())) {
                    users.remove(selectedUser);
                    clearUserFields();
                    showAlert("Success", "User deleted successfully.");
                } else {
                    showAlert("Error", "Failed to delete user.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to delete user: " + e.getMessage());
            }
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
        
        try {
            ObservableList<String> reportData = FXCollections.observableArrayList();
            
            // Get service count
            int serviceCount = serviceDAO.getAllServices().size();
            reportData.add("Services Available\t" + serviceCount + " services in the system");
            
            // Get user count
            int userCount = userDAO.getAllUsers().size();
            reportData.add("Users Registered\t" + userCount + " users in the system");
            
            reportData.add("Time Period\tFrom " + startDate + " to " + endDate);
            
            reportsTable.setItems(reportData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to generate report: " + e.getMessage());
        }
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
            showAlert("Error", "Failed to logout: " + e.getMessage());
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
