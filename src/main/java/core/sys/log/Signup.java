package core.sys.log;

import core.sys.obj.User;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class Signup {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final AuthRepository repository;
    private final ValidationHandler<SignupRequest> validationChain;

    public Signup(AuthRepository repository) {
        this.repository = repository;
        this.validationChain = ValidationChain.link(
                new SignupRequiredFieldsValidator(),
                new SignupUsernameValidator(),
                new SignupEmailValidator(),
                new SignupPasswordValidator(),
                new SignupUniqueUserValidator(repository)
        );
    }

    public AuthResult execute(SignupRequest request) {
        AuthResult validationResult = validationChain.handle(request);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        try {
            String username = request.getUsername().trim();
            String email = request.getEmail().trim();
            String passwordHash = PasswordHasher.hash(request.getPassword());
            int insertedRows = repository.createUser(username, email, passwordHash);

            if (insertedRows <= 0) {
                return AuthResult.fail("Signup failed");
            }

            User user = new User(username, null, email);
            return AuthResult.success(user, "Signup successful");
        } catch (SQLException exception) {
            return AuthResult.fail("Signup failed: " + exception.getMessage());
        }
    }

    private static class SignupRequiredFieldsValidator extends AbstractValidationHandler<SignupRequest> {
        @Override
        protected AuthResult validate(SignupRequest request) {
            if (request == null) {
                return AuthResult.fail("Signup request is required");
            }

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return AuthResult.fail("Username is required");
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return AuthResult.fail("Email is required");
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return AuthResult.fail("Password is required");
            }

            return AuthResult.success("Signup request is present");
        }
    }

    private static class SignupUsernameValidator extends AbstractValidationHandler<SignupRequest> {
        @Override
        protected AuthResult validate(SignupRequest request) {
            String username = request.getUsername().trim();
            if (username.length() < 3 || username.length() > 30) {
                return AuthResult.fail("Username must be between 3 and 30 characters");
            }
            if (!username.matches("^[A-Za-z0-9_]+$")) {
                return AuthResult.fail("Username can only contain letters, numbers, and underscore");
            }
            return AuthResult.success("Username is valid");
        }
    }

    private static class SignupEmailValidator extends AbstractValidationHandler<SignupRequest> {
        @Override
        protected AuthResult validate(SignupRequest request) {
            String email = request.getEmail().trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return AuthResult.fail("Email format is invalid");
            }
            return AuthResult.success("Email is valid");
        }
    }

    private static class SignupPasswordValidator extends AbstractValidationHandler<SignupRequest> {
        @Override
        protected AuthResult validate(SignupRequest request) {
            String password = request.getPassword();
            if (password.length() < 8) {
                return AuthResult.fail("Password must be at least 8 characters");
            }

            boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
            boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
            boolean hasDigit = password.chars().anyMatch(Character::isDigit);
            if (!hasUpper || !hasLower || !hasDigit) {
                return AuthResult.fail("Password must include uppercase, lowercase, and number");
            }

            return AuthResult.success("Password is valid");
        }
    }

    private static class SignupUniqueUserValidator extends AbstractValidationHandler<SignupRequest> {
        private final AuthRepository repository;

        private SignupUniqueUserValidator(AuthRepository repository) {
            this.repository = repository;
        }

        @Override
        protected AuthResult validate(SignupRequest request) {
            try {
                boolean exists = repository.existsByUsernameOrEmail(request.getUsername().trim(), request.getEmail().trim());
                if (exists) {
                    return AuthResult.fail("Username or email already exists");
                }
                return AuthResult.success("User is unique");
            } catch (SQLException exception) {
                return AuthResult.fail("Could not validate user uniqueness: " + exception.getMessage());
            }
        }
    }
}

