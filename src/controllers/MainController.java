package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private Button customerBtn;
    
    @FXML
    private void handleCustomerButton() {
        loadScene("/fxml/login.fxml", "Customer");
    }
    
    @FXML
    private void handleTechnicianButton() {
        loadScene("/fxml/login.fxml", "Technician");
    }
    
    @FXML
    private void handleAdminButton() {
        loadScene("/fxml/login.fxml", "Admin");
    }
    
    private void loadScene(String fxmlPath, String title) {
        try {
            // Get the current stage from any node in your current scene
            Stage currentStage = (Stage) customerBtn.getScene().getWindow(); // Using customerBtn as reference
            
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            controller.setRole(title);
            controller.setStage(currentStage);
            // Set up new scene
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Failed to load: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
