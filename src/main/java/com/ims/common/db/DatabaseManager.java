package com.ims.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the application-wide HikariCP connection pool.
 *
 * <p>The pool is initialised once from {@code db.properties} on the classpath.
 * Call {@link #getConnection()} to borrow a connection; always close it in a
 * try-with-resources block so it is returned to the pool.
 *
 * <p>Call {@link #shutdown()} when the application exits.
 */
public final class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static final HikariDataSource dataSource;

    static {
        try (InputStream in = DatabaseManager.class
                .getClassLoader().getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new ExceptionInInitializerError("db.properties not found on classpath.");
            }

            Properties props = new Properties();
            props.load(in);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));   // never logged
            config.setMaximumPoolSize(
                    Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(
                    Integer.parseInt(props.getProperty("db.pool.minimumIdle", "2")));
            config.setConnectionTimeout(
                    Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
            config.setIdleTimeout(
                    Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
            config.setMaxLifetime(
                    Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));
            config.setPoolName(props.getProperty("db.pool.poolName", "IMS-Pool"));

            // Validate on borrow to catch stale connections early
            config.setConnectionTestQuery("SELECT 1");

            dataSource = new HikariDataSource(config);
            log.info("HikariCP pool '{}' initialised (maxPoolSize={}).",
                    config.getPoolName(), config.getMaximumPoolSize());

        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
        }
    }

    private DatabaseManager() { /* utility class */ }

    /**
     * Borrows a {@link Connection} from the pool.
     * Always use in a try-with-resources block.
     *
     * @return an active database connection
     * @throws SQLException if a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /** Shuts down the connection pool gracefully. Call on application exit. */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("HikariCP pool shut down.");
        }
    }
}
