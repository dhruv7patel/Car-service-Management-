package dao;

import model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
	
    private Connection connection;

    public AppointmentDAO(Connection connection) {
        this.connection = connection;
    }
 
    // Book an appointment
    public void bookAppointment(Appointment appointment) throws SQLException {
        String checkCustomerQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        String insertQuery = "INSERT INTO appointments (service_id, customer_id, date, status, service_name, assigned_user_id) VALUES (?, ?, ?, ?, ?, ?)";

            // Check if customer exists
            try (PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
                checkStmt.setInt(1, appointment.getCustomerId());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    throw new SQLException("Error: Customer ID " + appointment.getCustomerId() + " does not exist.");
                }
            }

            // Insert appointment
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, appointment.getServiceId());
                stmt.setInt(2, appointment.getCustomerId());
                stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
                stmt.setString(4, "PENDING");
                stmt.setString(5, appointment.getServiceName());

                // If unassigned, set to NULL
                if (appointment.getAssignedUserId() == 0) {
                    stmt.setNull(6, Types.INTEGER);
                } else {
                    stmt.setInt(6, appointment.getAssignedUserId());
                }

                stmt.executeUpdate();
                
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                }
            }
    }

    // Assign Appointment to Technician
    public void assignAppointmentToTechnician(int appointmentId, int technicianId) throws SQLException {
        // Check if the technician exists
        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                throw new SQLException("Technician with ID " + technicianId + " does not exist.");
            }
        }

        // Update the appointment if technician exists
        String query = "UPDATE appointments SET assigned_user_id = ?, status = 'ASSIGNED' WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            stmt.setInt(2, appointmentId);
            stmt.executeUpdate();
        }
    }

    // Get active appointments by customer ID
    public List<Appointment> getActiveAppointmentsByCustomer(int customerId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE customer_id = ? AND status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS')";

        try (Connection conn = DBConnection.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAssignedAppointments(int technicianId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE assigned_user_id = ? AND status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS')";

        try (Connection conn = DBConnection.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    public void updateAppointmentStatus(int appointmentId, String status, String recommendation) throws SQLException {
        String query = "UPDATE appointments SET status = ?, recommendation = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, recommendation);
            stmt.setInt(3, appointmentId);
            stmt.executeUpdate();
        }
    }
    
    public List<Appointment> getCompletedAppointmentsByCustomer(int customerId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE customer_id = ? AND status = 'COMPLETED'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }
    
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointment_id"),
            rs.getInt("service_id"),
            rs.getInt("customer_id"),
            rs.getDate("date").toLocalDate(),
            rs.getString("status"),
            rs.getString("service_name"),
            rs.getString("recommendation"),
            rs.getInt("assigned_user_id"));
    }
}
