package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/atm_db";
    private static final String USER = "atm_user";
    private static final String PASSWORD = "atm123";

    private static Connection instance;

    private DatabaseConnection(){

    }
    public static Connection get() throws SQLException {
        if(instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }
}
