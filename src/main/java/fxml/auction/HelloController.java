package fxml.auction;

import core.sys.log.AuthResult;
import core.sys.log.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Supplier;

public class HelloController {
    private Log auth;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;


    @FXML
    protected void onSignInButtonClick() {
        executeAuth("Sign in", () -> getAuth().login(usernameField.getText(), passwordField.getText()), true);
    }

    @FXML
    protected void onSignUpButtonClick() {
        executeAuth("Sign up", () -> getAuth().signup(usernameField.getText(), emailField.getText(), passwordField.getText()), false);
    }

    private void executeAuth(String actionName, Supplier<AuthResult> action, boolean openMainAppOnSuccess) {
        AuthResult result;
        try {
            result = action.get();
        } catch (Exception exception) {
            result = AuthResult.fail(simplifyMessage(exception));
        }

        updateStatus(result);
        showPopup(actionName, result);
        if (result.isSuccess() && openMainAppOnSuccess) {
            passwordField.clear();
            openMainApplication(usernameField.getText());
        }
    }

    private void openMainApplication(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setCurrentUser(username);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, Math.max(stage.getWidth(), 1200), Math.max(stage.getHeight(), 800));
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException exception) {
            AuthResult result = AuthResult.fail("Cannot open application interface: " + simplifyMessage(exception));
            updateStatus(result);
            showPopup("Open app", result);
        }
    }

    private void showPopup(String actionName, AuthResult result) {
        Alert.AlertType alertType = result.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(alertType);
        alert.setTitle(actionName + " status");
        alert.setHeaderText(result.isSuccess() ? actionName + " successful" : actionName + " failed");
        alert.setContentText(result.getMessage());
        alert.showAndWait();
    }

    private Log getAuth() {
        if (auth != null) {
            return auth;
        }

        String dbPath = readConfig("db.path", "DB_PATH", "data/auction.db");
        auth = new Log(dbPath);
        return auth;
    }

    private String readConfig(String propertyKey, String envKey, String defaultValue) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }

    private void updateStatus(AuthResult result) {
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        if (result.isSuccess()) {
            statusLabel.getStyleClass().add("status-success");
        } else {
            statusLabel.getStyleClass().add("status-error");
        }
        statusLabel.setText(result.getMessage());
    }


    private String simplifyMessage(Exception exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return "Authentication failed. Please check database configuration.";
        }

        String message = exception.getMessage();
        int newLineIndex = message.indexOf('\n');
        if (newLineIndex > 0) {
            return message.substring(0, newLineIndex);
        }
        return message;
    }

}
