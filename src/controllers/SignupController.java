package controller;

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

public class SignupController {
    @FXML private Text titleText;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label messageLabel;
    
    private String role;
    
    public void setRole(String role) {
        this.role = role;
        titleText.setText(role + " Sign Up");
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Please fill in all required fields");
            return;
        }
        
        User user = new User(username, password, email, phone, role);
        UserDAO userDAO = new UserDAO();
        
        if (userDAO.registerUser(user)) {
            messageLabel.setText("Registration successful! Please login.");
        } else {
            messageLabel.setText("Registration failed. Username may already exist.");
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(role + " Login");
            
            LoginController controller = loader.getController();
            controller.setRole(role);
            controller.setStage(currentStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
