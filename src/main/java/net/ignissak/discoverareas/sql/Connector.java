package net.ignissak.discoverareas.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Connector {

    protected HikariDataSource hikariDataSource;

    public abstract void setupHikari();

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void closePool() {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps) {
        if (conn != null) try {
            conn.close();
        } catch (Exception ignored) {}

        if (ps != null) try {
            ps.close();
        } catch (Exception ignored) {}
    }
}
