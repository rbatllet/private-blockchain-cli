package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.DatabaseConfig;
import com.rbatllet.blockchain.config.util.DatabasePropertiesParser;
import com.rbatllet.blockchain.config.util.FilePermissionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads database configuration from properties file.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *   <li>Loading configuration from {@code ~/.blockchain-cli/database.properties}</li>
 *   <li>Parsing properties using CORE {@link DatabasePropertiesParser}</li>
 *   <li>Verifying file permissions (must be 600 for security)</li>
 *   <li>Warning if password found in file</li>
 *   <li>Generating default configuration file template</li>
 * </ul>

 *
 * <p><b>Security Features:</b></p>
 * <ul>
 *   <li>Automatically sets file permissions to 600 (rw-------) on creation</li>
 *   <li>Warns if file permissions are insecure</li>
 *   <li>Logs warning if password found in configuration file</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * CLIDatabasePropertiesLoader loader = new CLIDatabasePropertiesLoader();
 *
 * // Load configuration
 * DatabaseConfig config = loader.loadConfig();
 *
 * // Generate default file if it doesn't exist
 * if (config == null) {
 *     Path configFile = loader.generateDefaultFile();
 *     System.out.println("Default configuration created: " + configFile);
 * }
 * }</pre>
 *
 * @since 1.0.5
 */
public class CLIDatabasePropertiesLoader {
    private static final Logger logger = LoggerFactory.getLogger(CLIDatabasePropertiesLoader.class);

    private static final String CONFIG_DIR = ".blockchain-cli";
    private static final String CONFIG_FILE = "database.properties";

    /**
     * Creates a new CLIDatabasePropertiesLoader with default settings.
     */
    public CLIDatabasePropertiesLoader() {
        // No initialization needed - FilePermissionsUtil has only static methods
    }

    /**
     * Loads database configuration from the properties file.
     *
     * <p>File location: {@code ~/.blockchain-cli/database.properties}</p>
     *
     * <p>If the file doesn't exist or cannot be parsed, returns {@code null}.</p>
     *
     * @return DatabaseConfig loaded from file, or null if file doesn't exist or parsing failed
     */
    public DatabaseConfig loadConfig() {
        Path configFile = getConfigFilePath();

        if (!Files.exists(configFile)) {
            logger.debug("Configuration file not found: {}", configFile);
            return null;
        }

        // Check file permissions
        checkFilePermissions(configFile);

        // Load and parse properties
        try (InputStream input = Files.newInputStream(configFile)) {
            DatabasePropertiesParser.ParseResult result = DatabasePropertiesParser.parse(input);

            if (result.isSuccess()) {
                DatabaseConfig config = result.getConfig();
                logger.info("Configuration loaded from: {}", configFile);

                // Warn if password found in file
                warnIfPasswordInFile(config);

                return config;
            } else {
                logger.error("Failed to parse configuration file: {}", configFile);
                result.getErrors().forEach(error -> logger.error("  - {}", error));
                return null;
            }
        } catch (IOException e) {
            logger.error("Failed to read configuration file: {}", configFile, e);
            return null;
        }
    }

    /**
     * Gets the path to the configuration file.
     *
     * @return Path to ~/.blockchain-cli/database.properties
     */
    public Path getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, CONFIG_DIR, CONFIG_FILE);
    }

    /**
     * Gets the path to the configuration directory.
     *
     * @return Path to ~/.blockchain-cli/
     */
    public Path getConfigDirPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, CONFIG_DIR);
    }

    /**
     * Generates a default configuration file template.
     *
     * <p>This creates {@code ~/.blockchain-cli/database.properties} with:</p>
     * <ul>
     *   <li>Security warnings and best practices</li>
     *   <li>Example configurations for all supported databases</li>
     *   <li>File permissions set to 600 (rw-------)</li>
     *   <li>Empty password fields by default</li>
     * </ul>
     *
     * @return Path to the created configuration file
     * @throws IOException if file creation fails
     */
    public Path generateDefaultFile() throws IOException {
        Path configDir = getConfigDirPath();
        Path configFile = getConfigFilePath();

        // Create directory if needed
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
            logger.info("Created configuration directory: {}", configDir);
        }

        // Check if file already exists
        if (Files.exists(configFile)) {
            logger.warn("Configuration file already exists: {}", configFile);
            return configFile;
        }

        // Create file with default content
        String defaultContent = generateDefaultContent();
        Files.writeString(configFile, defaultContent);

        // Set secure permissions (600 = rw-------)
        try {
            FilePermissionsUtil.setSecurePermissions(configFile);
            logger.info("✅ Configuration file created with secure permissions (600): {}", configFile);
        } catch (UnsupportedOperationException e) {
            logger.warn("⚠️  Cannot set file permissions on this platform (not POSIX): {}", e.getMessage());
        } catch (IOException e) {
            logger.warn("⚠️  Failed to set file permissions: {}", e.getMessage());
        }

        return configFile;
    }

    /**
     * Checks file permissions and warns if insecure.
     *
     * @param configFile the configuration file to check
     */
    private void checkFilePermissions(Path configFile) {
        try {
            FilePermissionsUtil.PermissionStatus status = FilePermissionsUtil.checkPermissions(configFile);

            if (!status.isSecure()) {
                String perms = FilePermissionsUtil.getPermissionsString(configFile);
                System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.err.println("⚠️  SECURITY WARNING ⚠️");
                System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.err.println("Configuration file has insecure permissions: " + perms);
                System.err.println("Recommended permissions: 600 (rw-------)");
                System.err.println();
                System.err.println("Fix with:");
                System.err.println("  chmod 600 " + configFile);
                System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                logger.warn("Insecure file permissions detected: {} on {}", perms, configFile);
            }
        } catch (UnsupportedOperationException e) {
            logger.debug("Cannot check file permissions on this platform: {}", e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to check file permissions: {}", e.getMessage());
        }
    }

    /**
     * Warns if password is found in configuration file.
     *
     * @param config the loaded configuration
     */
    private void warnIfPasswordInFile(DatabaseConfig config) {
        // Check if password is set (not empty)
        if (config.getPassword() != null && !config.getPassword().isEmpty()) {
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println("⚠️  SECURITY WARNING ⚠️");
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.err.println("Database password loaded from configuration file.");
            System.err.println();
            System.err.println("PRODUCTION BEST PRACTICE:");
            System.err.println("Use DB_PASSWORD environment variable instead:");
            System.err.println("  export DB_PASSWORD='your-secure-password'");
            System.err.println();
            System.err.println("Then remove password from configuration file:");
            System.err.println("  " + getConfigFilePath());
            System.err.println();
            System.err.println("See: https://12factor.net/config");
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            logger.warn("Password found in configuration file (insecure for production)");
        }
    }

    /**
     * Generates default configuration file content.
     *
     * @return the default properties file content
     */
    private String generateDefaultContent() {
        return "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Database Configuration for Private Blockchain CLI\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Priority Order: CLI args > Environment variables > This file > Defaults\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "\n" +
               "# ⚠️  SECURITY WARNING ⚠️\n" +
               "# This file may contain sensitive information (passwords, connection strings).\n" +
               "#\n" +
               "# PRODUCTION BEST PRACTICES:\n" +
               "# 1. Use environment variables for passwords (DB_PASSWORD)\n" +
               "# 2. Leave db.*.password fields EMPTY in this file\n" +
               "# 3. Verify file permissions: 600 (rw-------)\n" +
               "# 4. Never commit this file to version control\n" +
               "#\n" +
               "# See: https://12factor.net/config\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Database Type Selection\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Options: sqlite, postgresql, mysql, h2\n" +
               "db.type=sqlite\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# SQLite Configuration (Default - Development/Single User)\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# ✅ SAFE: No password required, suitable for local development\n" +
               "db.sqlite.file=blockchain.db\n" +
               "db.sqlite.journal_mode=WAL\n" +
               "db.sqlite.synchronous=NORMAL\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# PostgreSQL Configuration (Production Recommended)\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# To use PostgreSQL:\n" +
               "# 1. Uncomment the lines below\n" +
               "# 2. Set db.type=postgresql\n" +
               "# 3. Configure connection details\n" +
               "# 4. ⚠️  LEAVE PASSWORD EMPTY and use environment variable instead:\n" +
               "#    export DB_PASSWORD='your-secure-password'\n" +
               "\n" +
               "# db.type=postgresql\n" +
               "# db.postgresql.host=localhost\n" +
               "# db.postgresql.port=5432\n" +
               "# db.postgresql.database=blockchain\n" +
               "# db.postgresql.username=blockchain_user\n" +
               "# db.postgresql.password=\n" +
               "# ⚠️  DO NOT store password here for production! Use DB_PASSWORD env var.\n" +
               "#\n" +
               "# Connection Pool (Production Tuning)\n" +
               "# db.postgresql.pool.min=10\n" +
               "# db.postgresql.pool.max=60\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# MySQL Configuration\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# db.type=mysql\n" +
               "# db.mysql.host=localhost\n" +
               "# db.mysql.port=3306\n" +
               "# db.mysql.database=blockchain\n" +
               "# db.mysql.username=blockchain_user\n" +
               "# db.mysql.password=\n" +
               "# ⚠️  DO NOT store password here! Use DB_PASSWORD env var.\n" +
               "#\n" +
               "# Connection Pool\n" +
               "# db.mysql.pool.min=10\n" +
               "# db.mysql.pool.max=50\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# H2 Configuration (Testing Only - NOT for Production)\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# db.type=h2\n" +
               "# db.h2.mode=memory\n" +
               "# db.h2.file=./test-blockchain\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Advanced Connection Pool Settings\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "db.pool.connection_timeout=30000\n" +
               "db.pool.idle_timeout=600000\n" +
               "db.pool.max_lifetime=1800000\n" +
               "\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Hibernate Schema Management\n" +
               "# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "# Options: validate, update, create, create-drop\n" +
               "# Production: validate or update\n" +
               "# Development: update\n" +
               "# Testing: create-drop\n" +
               "db.hibernate.hbm2ddl.auto=update\n" +
               "db.hibernate.show_sql=false\n" +
               "db.hibernate.format_sql=false\n";
    }
}
