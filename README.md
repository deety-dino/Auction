# Auction JDBC Query Layer

This project now includes a reusable query package at `src/main/java/core/dao/Query` for:

- `SELECT` via `SelectQuery<T>`
- `INSERT` via `InsertQuery`
- `UPDATE` via `UpdateQuery`
- `DELETE` via `DeleteQuery`

## Design

- **Command pattern**: each SQL action is represented by a `QueryCommand<R>` object.
- **Template Method pattern**: `AbstractQuery<R>` centralizes prepared-statement creation and parameter binding.
- **Factory helper**: `QueryFactory` creates typed query objects with minimal boilerplate.

## JDBC Connection

Connection is managed by `core.dao.JDBC` and uses:

- Base URL: `jdbc:mysql://localhost:3306/`
- Credentials from `core.dao.DBUser`
- Optional database name via `JDBC.connect("database_name")`

## Quick usage

```java
DBUser dbUser = DBUser.getInstance();
dbUser.setUsername("root");
dbUser.setPassword("secret");

JDBC jdbc = JDBC.getInstance();
jdbc.connect("auction_db");

QueryExecutor executor = new QueryExecutor(jdbc);

// SELECT
List<String> names = executor.execute(
    QueryFactory.select(
        "SELECT username FROM users WHERE active = ?",
        rs -> rs.getString("username"),
        true
    )
);

// INSERT
int inserted = executor.execute(
    QueryFactory.insert(
        "INSERT INTO users(username, password) VALUES(?, ?)",
        "alice",
        "pw"
    )
);
```

## Build

If Java is installed and `JAVA_HOME` is set:

```powershell
.\mvnw.cmd -DskipTests compile
```

## Login and Signup Module

`core.sys.log` now includes a complete auth flow with Chain of Responsibility validation.

- `Log`: facade service for login/signup actions
- `Login`: authenticates user credentials from DB
- `Signup`: validates and creates new users
- `AuthRepository`: DB operations through `QueryExecutor` and `QueryFactory`

### Validation chain

Signup uses a chain of validators in order:

1. Required fields
2. Username rules
3. Email format
4. Password strength
5. Unique username/email check

### Example

```java
DBUser dbUser = DBUser.getInstance();
dbUser.setUsername("root");
dbUser.setPassword("secret");

Log auth = new Log("auction_db");

AuthResult signupResult = auth.signup("alice", "alice@example.com", "Password1");
AuthResult loginResult = auth.login("alice", "Password1");
```

### Database table expectation

Current SQL expects a `users` table with at least:

- `user_id` (int, primary key)
- `username` (varchar, unique)
- `email` (varchar, unique)
- `password` (varchar)

