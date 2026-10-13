package com.pms.util;

import com.ims.common.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Database Seeder utility for ims_db.
 * Populates sample data for test login credentials, categories, warehouses, products, and inventory stock counts.
 */
public class DatabaseSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    /**
     * Executes the demo seed script from classpath.
     */
    public static void runSeed() {
        String scriptPath = "/db/demo_seed.sql";
        logger.info("Executing database seed from: {}", scriptPath);

        try (InputStream in = DatabaseSeeder.class.getResourceAsStream(scriptPath)) {
            if (in == null) {
                throw new IllegalStateException("Seed script not found at " + scriptPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {

                StringBuilder sqlBatch = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                        continue;
                    }
                    sqlBatch.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String query = sqlBatch.toString().trim();
                        // Remove trailing semicolon for JDBC statement
                        if (query.endsWith(";")) {
                            query = query.substring(0, query.length() - 1);
                        }
                        if (!query.isEmpty()) {
                            stmt.execute(query);
                        }
                        sqlBatch.setLength(0);
                    }
                }

                logger.info("Database successfully seeded with demo data.");
            }
        } catch (Exception e) {
            logger.error("Failed to seed database", e);
            throw new RuntimeException("Database seeding failed", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Database Seeder for ims_db...");
        runSeed();
        System.out.println("Seeding completed successfully!");
        DatabaseManager.shutdown();
    }
}
