package fxml.auction;

import core.dao.DBUser;
import core.sys.log.AuthResult;
import core.sys.log.Log;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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
        AuthResult result = getAuth().login(usernameField.getText(), passwordField.getText());
        updateStatus(result);
    }

    @FXML
    protected void onSignUpButtonClick() {
        AuthResult result = getAuth().signup(usernameField.getText(), emailField.getText(), passwordField.getText());
        updateStatus(result);
    }

    private Log getAuth() {
        if (auth != null) {
            return auth;
        }

        String dbName = readConfig("db.name", "DB_NAME", "management_system");
        String dbUsername = readConfig("db.user", "DB_USERNAME", "root");
        String dbPassword = readConfig("db.password", "DB_PASSWORD", "MOTHERman123@");

        DBUser dbUser = DBUser.getInstance();
        dbUser.setUsername(dbUsername);
        dbUser.setPassword(dbPassword);
        auth = new Log(dbName);
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
        if (result.isSuccess()) {
            statusLabel.setTextFill(Color.FORESTGREEN);
        } else {
            statusLabel.setTextFill(Color.FIREBRICK);
        }
        statusLabel.setText(result.getMessage());
    }

}
