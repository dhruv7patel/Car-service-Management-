package dao;

import model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    private Connection connection;

    // Constructor accepts a connection object
    public ServiceDAO(Connection connection) {
        this.connection = connection;
    }

    // Method to get all services from the database
    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services";

        // Try-with-resources ensures resources are automatically closed
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and create Service objects
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
}

