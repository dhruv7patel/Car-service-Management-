package controller;

import dao.ServiceDAO;
import dao.DBConnection;
import dao.AppointmentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Service;
import model.User;
import model.Appointment;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Button bookServiceBtn;

    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentServiceNameColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDateColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentStatusColumn;
    @FXML
    private TableView<Appointment> historyTable;
    @FXML
    private TableColumn<Appointment, Integer> historyIdColumn;
    @FXML
    private TableColumn<Appointment, String> historyServiceNameColumn;
    @FXML
    private TableColumn<Appointment, String> historyDateColumn;
    @FXML
    private TableColumn<Appointment, String> historyStatusColumn;
    @FXML
    private TableColumn<Appointment, String> historyRecommendationColumn;

    private ServiceDAO serviceDAO;
    private AppointmentDAO appointmentDAO;
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    private Map<Integer, String> serviceMap = new HashMap<>();
    private int customerId;

    // Default constructor
    public CustomerController() {}

    // Method to set the customer information (called after login)
    public void setCustomer(User customer) {
        this.customerId = customer.getId();
        loadServiceHistory(customerId);
    }

    @FXML
    public void initialize() {
        try {
            Connection connection = DBConnection.getConnection();
            this.serviceDAO = new ServiceDAO(connection);
            this.appointmentDAO = new AppointmentDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set up service table columns
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Set up appointment table columns
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentServiceNameColumn.setCellValueFactory(cellData -> {
            int serviceId = cellData.getValue().getServiceId();
            return new SimpleStringProperty(serviceMap.getOrDefault(serviceId, "Unknown"));
        });
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        appointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
     // Set up history table columns
        historyIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        historyServiceNameColumn.setCellValueFactory(cellData -> {
            int serviceId = cellData.getValue().getServiceId();
            return new SimpleStringProperty(serviceMap.getOrDefault(serviceId, "Unknown"));
        });
        historyDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        historyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        historyRecommendationColumn.setCellValueFactory(new PropertyValueFactory<>("recommendation"));

        // Load data
        try {
            loadServiceNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadServicesAndAppointments();

        bookServiceBtn.setOnAction(event -> handleBookService());
    }

    private void loadServiceNames() throws SQLException {
        List<Service> services = serviceDAO.getAllServices();
        for (Service service : services) {
            serviceMap.put(service.getServiceId(), service.getName());
        }
    }

    private void loadServicesAndAppointments() {
        Task<List<Service>> loadServicesTask = new Task<>() {
            @Override
            protected List<Service> call() throws Exception {
                return serviceDAO.getAllServices();
            }
        };

        loadServicesTask.setOnSucceeded(event -> {
            ObservableList<Service> serviceList = FXCollections.observableArrayList(loadServicesTask.getValue());
            serviceTable.setItems(serviceList);
            loadAppointments(customerId);
        });

        loadServicesTask.setOnFailed(event -> loadServicesTask.getException().printStackTrace());

        new Thread(loadServicesTask).start();
    }

    private void loadAppointments(int customerId) {
        Task<List<Appointment>> loadAppointmentsTask = new Task<>() {
            @Override
            protected List<Appointment> call() throws Exception {
                return appointmentDAO.getActiveAppointmentsByCustomer(customerId);
            }
        };

        loadAppointmentsTask.setOnSucceeded(event -> {
            appointmentsList.setAll(loadAppointmentsTask.getValue());
            appointmentTable.setItems(appointmentsList);
        });

        loadAppointmentsTask.setOnFailed(event -> loadAppointmentsTask.getException().printStackTrace());

        new Thread(loadAppointmentsTask).start();
    }
    
    private void loadServiceHistory(int customerId) {
        Task<List<Appointment>> loadHistoryTask = new Task<>() {
            @Override
            protected List<Appointment> call() throws Exception {
                return appointmentDAO.getCompletedAppointmentsByCustomer(customerId);
            }
        };

        loadHistoryTask.setOnSucceeded(event -> {
            ObservableList<Appointment> historyList = FXCollections.observableArrayList(loadHistoryTask.getValue());
            historyTable.setItems(historyList);
        });

        loadHistoryTask.setOnFailed(event -> loadHistoryTask.getException().printStackTrace());
        new Thread(loadHistoryTask).start();
    }


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

    @FXML
    private void handleBookService() {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            openBookingDialog(selectedService);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Service Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a service to book.");
            alert.showAndWait();
        }
    }

    private void openBookingDialog(Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking.fxml"));
            Parent root = loader.load();
            BookingController bookingDialogController = loader.getController();
            bookingDialogController.setService(service, customerId);

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Book Service");
            dialogStage.setOnHidden(e -> loadAppointments(customerId)); 
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
