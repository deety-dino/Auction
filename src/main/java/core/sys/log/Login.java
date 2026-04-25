package core.sys.log;

import java.sql.SQLException;
import java.util.Optional;

public class Login {
    private final AuthRepository repository;
    private final ValidationHandler<LoginRequest> validationChain;

    public Login(AuthRepository repository) {
        this.repository = repository;
        this.validationChain = ValidationChain.link(new LoginRequiredFieldsValidator());
    }

    public AuthResult execute(LoginRequest request) {
        AuthResult validationResult = validationChain.handle(request);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        try {
            Optional<AuthUser> user = repository.findByUsername(request.getUsername().trim());
            if (user.isEmpty()) {
                return AuthResult.fail("Invalid username or password");
            }

            AuthUser authUser = user.get();
            if (!PasswordHasher.matches(request.getPassword(), authUser.getPasswordHash())) {
                return AuthResult.fail("Invalid username or password");
            }

            return AuthResult.success(authUser.toSystemUser(), "Login successful");
        } catch (SQLException exception) {
            return AuthResult.fail("Login failed: " + exception.getMessage());
        }
    }

    private static class LoginRequiredFieldsValidator extends AbstractValidationHandler<LoginRequest> {
        @Override
        protected AuthResult validate(LoginRequest request) {
            if (request == null) {
                return AuthResult.fail("Login request is required");
            }

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return AuthResult.fail("Username is required");
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return AuthResult.fail("Password is required");
            }

            return AuthResult.success("Login request is valid");
        }
    }
}

