package core.sys.log;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginSignupModuleTest {

    @Test
    void signupShouldFailOnWeakPassword() {
        InMemoryAuthRepository repository = new InMemoryAuthRepository();
        Signup signup = new Signup(repository);

        AuthResult result = signup.execute(new SignupRequest("alice", "alice@example.com", "weak"));

        assertFalse(result.isSuccess());
    }

    @Test
    void signupAndLoginShouldSucceed() {
        InMemoryAuthRepository repository = new InMemoryAuthRepository();
        Signup signup = new Signup(repository);
        Login login = new Login(repository);

        AuthResult signupResult = signup.execute(new SignupRequest("alice", "alice@example.com", "Password1"));
        AuthResult loginResult = login.execute(new LoginRequest("alice", "Password1"));

        assertTrue(signupResult.isSuccess());
        assertTrue(loginResult.isSuccess());
    }

    private static class InMemoryAuthRepository extends AuthRepository {
        private final Map<String, AuthUser> users = new HashMap<>();

        private InMemoryAuthRepository() {
            super(null);
        }

        @Override
        public Optional<AuthUser> findByUsername(String username) {
            return Optional.ofNullable(users.get(username));
        }

        @Override
        public boolean existsByUsernameOrEmail(String username, String email) {
            return users.values().stream()
                    .anyMatch(user -> user.getUsername().equals(username) || user.getEmail().equals(email));
        }

        @Override
        public int createUser(String username, String email, String passwordHash) {
            if (existsByUsernameOrEmail(username, email)) {
                return 0;
            }
            users.put(username, new AuthUser(users.size() + 1, username, email, passwordHash));
            return 1;
        }
    }
}

