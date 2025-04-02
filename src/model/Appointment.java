package model;

import java.time.LocalDate;

public class Appointment {
    private int appointmentId;
    private int serviceId;
    private int customerId;
    private LocalDate appointmentDate;
    private String status;
    private String serviceName;
    private String recommendation;
    private int assignedUserId;

    // Constructor for creating NEW appointments (without appointmentId)
    public Appointment(int serviceId, int customerId, LocalDate appointmentDate, String serviceName) {
        this.serviceId = serviceId;
        this.customerId = customerId;
        this.appointmentDate = appointmentDate;
        this.status = "PENDING"; // Default status
        this.serviceName = serviceName;
        this.recommendation = null; // Initially null
        this.assignedUserId = 0; // 0 or -1 for unassigned
    }

    // Constructor for fetching EXISTING appointments from DB (all fields)
    public Appointment(int appointmentId, int serviceId, int customerId, LocalDate appointmentDate,
                      String status, String serviceName, String recommendation, int assignedUserId) {
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.customerId = customerId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.serviceName = serviceName;
        this.recommendation = recommendation;
        this.assignedUserId = assignedUserId;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(int assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
}
