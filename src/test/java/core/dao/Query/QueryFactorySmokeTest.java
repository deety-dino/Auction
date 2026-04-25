package core.dao.Query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class QueryFactorySmokeTest {

    @Test
    void shouldCreateCrudQueries() {
        SelectQuery<String> select = QueryFactory.select(
                "SELECT username FROM users WHERE user_id = ?",
                resultSet -> resultSet.getString("username"),
                1
        );
        InsertQuery insert = QueryFactory.insert("INSERT INTO users(username) VALUES(?)", "alice");
        UpdateQuery update = QueryFactory.update("UPDATE users SET username = ? WHERE user_id = ?", "bob", 1);
        DeleteQuery delete = QueryFactory.delete("DELETE FROM users WHERE user_id = ?", 1);

        assertNotNull(select);
        assertNotNull(insert);
        assertNotNull(update);
        assertNotNull(delete);
    }
}

