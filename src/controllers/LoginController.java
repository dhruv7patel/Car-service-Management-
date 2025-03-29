package controller;

import java.io.IOException;

import dao.UserDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {
    @FXML private Text titleText;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    
    private String role;
    private Stage currentStage;
    
    // Safe initialization alternative
    public void setRole(String role) {
        this.role = role;
        titleText.setText(role + " Login");
    }
    public void setStage(Stage stage) {
    	this.currentStage = stage;
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }
        
        try {
            User user = new UserDAO().authenticateUser(username, password);
            
            if (user == null || !user.getRole().equalsIgnoreCase(role)) {
                messageLabel.setText("Invalid credentials or wrong role");
                return;
            }
            
            String fxmlPath = switch (user.getRole().toUpperCase()) {
                case "ADMIN" -> "/fxml/admin.fxml";
                case "TECHNICIAN" -> "/fxml/technician.fxml";
                case "CUSTOMER" -> "/fxml/customer.fxml";
                default -> throw new IllegalStateException("Unknown role");
            };
            
            loadScene(fxmlPath, user.getRole() + " Dashboard");
            
        } catch (Exception e) {
            messageLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"));
            Parent root = loader.load();
            
            SignupController controller = loader.getController();
            controller.setRole(role);
            
            loadScene(root, role + " Sign Up");
        } catch (Exception e) {
            messageLabel.setText("Cannot load signup form");
            e.printStackTrace();
        }
    }
    
    private void loadScene(String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        loadScene(root, title);
    }
    
    private void loadScene(Parent root, String title) {
        currentStage.setScene(new Scene(root));
        currentStage.setTitle(title);
        currentStage.show();
    }
}
