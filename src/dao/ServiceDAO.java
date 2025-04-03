package dao;

import model.Service;
import model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
	
    private Connection connection;

    public ServiceDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("service_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price")
                );
                services.add(service);
            }
        }
        return services;
    }
    
    public void addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (name, description, price) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setServiceId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public void updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET name = ?, description = ?, price = ? WHERE service_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getServiceId());
            pstmt.executeUpdate();
        }
    }
    
    public void deleteService(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    public List<Appointment> getActiveAppointmentsByCustomer(int customerId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.appointment_id, a.service_id, a.date, a.status, a.serviceName, a.recommendation " +
                "FROM appointments a " +
                "WHERE a.customer_id = ? AND a.status IN ('PENDING', 'IN_PROGRESS')";

        try (Connection conn = DBConnection.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        customerId,
                        rs.getInt("service_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("status"),
                        rs.getString("serviceName"),
                        rs.getString("recommendation"),
                        rs.getInt("assigned_user_id")
                ));
            }
        }
        return appointments;
    }
}
