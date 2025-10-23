package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.DatabaseConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CLIDatabasePropertiesLoader
 * Tests configuration file loading, security checks, default file generation, and error handling
 */
@DisplayName("CLIDatabasePropertiesLoader Tests")
public class CLIDatabasePropertiesLoaderTest {

    private CLIDatabasePropertiesLoader loader;
    private Path configFile;
    private Path backupFile;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        loader = new CLIDatabasePropertiesLoader();
        configFile = loader.getConfigFilePath();

        // Backup existing config file if it exists
        if (Files.exists(configFile)) {
            backupFile = configFile.getParent().resolve("database.properties.test-backup");
            Files.copy(configFile, backupFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // Delete config file for clean tests
        Files.deleteIfExists(configFile);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Restore backup if it existed
        if (backupFile != null && Files.exists(backupFile)) {
            Files.move(backupFile, configFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } else {
            // Clean up test file
            Files.deleteIfExists(configFile);
        }
    }

    // ======================== Constructor Tests ========================

    @Test
    @DisplayName("Should use default directory when created")
    void testConstructorWithDefaultDirectory() {
        CLIDatabasePropertiesLoader defaultLoader = new CLIDatabasePropertiesLoader();

        assertNotNull(defaultLoader, "Loader should be created");
        Path configPath = defaultLoader.getConfigFilePath();
        assertNotNull(configPath, "Config file path should be set");
        assertTrue(configPath.toString().contains(".blockchain-cli"),
                "Should use .blockchain-cli directory: " + configPath);
    }

    // ======================== Load Config Tests ========================

    @Test
    @DisplayName("Should return null when config file doesn't exist")
    void testLoadConfigFileNotExists() {
        assertFalse(Files.exists(configFile), "Config file should not exist");

        DatabaseConfig config = loader.loadConfig();

        assertNull(config, "Should return null when file doesn't exist");
    }

    @Test
    @DisplayName("Should load valid SQLite configuration")
    void testLoadValidSQLiteConfig() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "sqlite");
        props.setProperty("db.file", "test-blockchain.db");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test SQLite config");
        }

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should be SQLite type");
    }

    @Test
    @DisplayName("Should load valid PostgreSQL configuration")
    void testLoadValidPostgreSQLConfig() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "postgresql");
        props.setProperty("db.postgresql.host", "pg-server");
        props.setProperty("db.postgresql.port", "5432");
        props.setProperty("db.postgresql.database", "test_blockchain");
        props.setProperty("db.postgresql.username", "test_user");
        props.setProperty("db.postgresql.password", "test_password");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test PostgreSQL config");
        }

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "Should be PostgreSQL type");
        assertTrue(config.getDatabaseUrl().contains("pg-server"),
                "Should contain host: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("5432"),
                "Should contain port: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("test_blockchain"),
                "Should contain database name: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should load valid MySQL configuration")
    void testLoadValidMySQLConfig() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "mysql");
        props.setProperty("db.mysql.host", "mysql-server");
        props.setProperty("db.mysql.port", "3306");
        props.setProperty("db.mysql.database", "blockchain_db");
        props.setProperty("db.mysql.username", "mysql_user");
        props.setProperty("db.mysql.password", "mysql_password");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test MySQL config");
        }

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.MYSQL, config.getDatabaseType(),
                "Should be MySQL type");
        assertTrue(config.getDatabaseUrl().contains("mysql-server"),
                "Should contain host: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should load configuration with complete JDBC URL")
    void testLoadConfigWithCompleteUrl() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "postgresql");  // Database type is required
        props.setProperty("db.url", "jdbc:postgresql://custom-host:5555/custom-db");
        props.setProperty("db.user", "custom_user");
        props.setProperty("db.password", "custom_password");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test custom URL config");
        }

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "Should be PostgreSQL type");
        assertEquals("jdbc:postgresql://custom-host:5555/custom-db", config.getDatabaseUrl(),
                "Should use custom URL exactly");
    }

    @Test
    @DisplayName("Should return default config for invalid configuration")
    void testLoadInvalidConfig() throws Exception {
        Properties props = new Properties();
        props.setProperty("invalid.key", "invalid-value");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Invalid config");
        }

        DatabaseConfig config = loader.loadConfig();

        // DatabasePropertiesParser returns default SQLite config for invalid input
        assertNotNull(config, "Should return default config for invalid config");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    @Test
    @DisplayName("Should handle corrupted properties file gracefully")
    void testLoadCorruptedFile() throws Exception {
        Files.writeString(configFile, "This is not a valid properties file\n==corrupted==\n");

        DatabaseConfig config = loader.loadConfig();

        // Parser returns default H2 config for corrupted file
        assertNotNull(config, "Should return default config for corrupted file");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    // ======================== Security Tests ========================

    @Test
    @DisplayName("Should warn about insecure file permissions on Unix systems")
    void testWarningForInsecurePermissions() throws Exception {
        // Skip test on Windows
        if (!configFile.getFileSystem().supportedFileAttributeViews().contains("posix")) {
            return;
        }

        Properties props = new Properties();
        props.setProperty("db.type", "sqlite");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test config");
        }

        // Set insecure permissions (world-readable)
        Set<PosixFilePermission> insecurePerms = Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.OTHERS_READ
        );
        Files.setPosixFilePermissions(configFile, insecurePerms);

        errContent.reset();
        loader.loadConfig();

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("SECURITY WARNING") || errorOutput.contains("WARNING"),
                "Should display security warning for insecure permissions: " + errorOutput);
    }

    @Test
    @DisplayName("Should warn when password stored in configuration file")
    void testWarningForPasswordInFile() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "postgresql");
        props.setProperty("db.host", "localhost");
        props.setProperty("db.password", "insecure-password");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Config with password");
        }

        errContent.reset();
        loader.loadConfig();

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("SECURITY") || errorOutput.contains("password"),
                "Should warn about password in file: " + errorOutput);
    }

    // ======================== Generate Default File Tests ========================

    @Test
    @DisplayName("Should generate default configuration file")
    void testGenerateDefaultFile() throws Exception {
        assertFalse(Files.exists(configFile), "Config file should not exist initially");

        Path generatedPath = loader.generateDefaultFile();

        assertNotNull(generatedPath, "Generated path should not be null");
        assertTrue(Files.exists(generatedPath), "Config file should be created");
        assertEquals(configFile, generatedPath, "Should match expected path");
    }

    @Test
    @DisplayName("Generated default file should have secure permissions on Unix")
    void testGeneratedFileHasSecurePermissions() throws Exception {
        // Skip test on Windows
        if (!configFile.getFileSystem().supportedFileAttributeViews().contains("posix")) {
            return;
        }

        Path generatedPath = loader.generateDefaultFile();

        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(generatedPath);

        assertTrue(perms.contains(PosixFilePermission.OWNER_READ),
                "Owner should have read permission");
        assertTrue(perms.contains(PosixFilePermission.OWNER_WRITE),
                "Owner should have write permission");
        assertFalse(perms.contains(PosixFilePermission.GROUP_READ),
                "Group should not have read permission");
        assertFalse(perms.contains(PosixFilePermission.OTHERS_READ),
                "Others should not have read permission");
    }

    @Test
    @DisplayName("Generated default file should contain valid properties")
    void testGeneratedFileContent() throws Exception {
        Path generatedPath = loader.generateDefaultFile();

        String content = Files.readString(generatedPath);

        assertTrue(content.contains("db.type"),
                "Should contain db.type property: " + content);
        assertTrue(content.contains("sqlite") || content.contains("Database type"),
                "Should mention SQLite or database type: " + content);
        assertTrue(content.contains("#") || content.contains("db."),
                "Should contain comments or properties: " + content);
    }

    @Test
    @DisplayName("Generated default file should be loadable")
    void testGeneratedFileIsLoadable() throws Exception {
        loader.generateDefaultFile();

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Should be able to load generated config");
        assertNotNull(config.getDatabaseType(), "Database type should be set");
    }

    @Test
    @DisplayName("Should not overwrite existing file when generating default")
    void testGenerateDefaultKeepsExisting() throws Exception {
        // Create initial file
        Files.writeString(configFile, "# Old config\ndb.type=postgresql\n");
        assertTrue(Files.exists(configFile), "Initial file should exist");

        // Generate default - should NOT overwrite
        Path generatedPath = loader.generateDefaultFile();

        assertEquals(configFile, generatedPath, "Should return existing file path");

        String content = Files.readString(configFile);
        assertTrue(content.contains("Old config"),
                "Should keep old content: " + content);
    }

    // ======================== Config File Path Tests ========================

    @Test
    @DisplayName("Should return correct config file path")
    void testGetConfigFilePath() {
        Path configPath = loader.getConfigFilePath();

        assertNotNull(configPath, "Config path should not be null");
        assertTrue(configPath.toString().endsWith("database.properties"),
                "Should end with database.properties: " + configPath);
        assertTrue(configPath.toString().contains(".blockchain-cli"),
                "Should contain .blockchain-cli: " + configPath);
    }

    // ======================== Edge Cases Tests ========================

    @Test
    @DisplayName("Should handle empty configuration file")
    void testLoadEmptyFile() throws Exception {
        Files.writeString(configFile, "");

        DatabaseConfig config = loader.loadConfig();

        // Parser returns default H2 config for empty file
        assertNotNull(config, "Should return default config for empty file");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    @Test
    @DisplayName("Should handle file with only comments")
    void testLoadFileWithOnlyComments() throws Exception {
        Files.writeString(configFile, "# This is a comment\n# Another comment\n");

        DatabaseConfig config = loader.loadConfig();

        // Parser returns default H2 config for file with only comments
        assertNotNull(config, "Should return default config for file with only comments");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    @Test
    @DisplayName("Should handle file with whitespace-only values")
    void testLoadFileWithWhitespaceValues() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "   ");
        props.setProperty("db.host", "  ");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Whitespace values");
        }

        DatabaseConfig config = loader.loadConfig();

        // Parser returns default H2 config for whitespace-only values
        assertNotNull(config, "Should return default config for whitespace-only values");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    @Test
    @DisplayName("Should handle missing required properties")
    void testLoadFileMissingRequiredProperties() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.host", "localhost");
        // Missing db.type

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Missing db.type");
        }

        DatabaseConfig config = loader.loadConfig();

        // Parser returns default H2 config when required properties missing
        assertNotNull(config, "Should return default config when required properties missing");
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should default to SQLite");
    }

    @Test
    @DisplayName("Should trim whitespace from property values")
    void testLoadFileTrimmsWhitespace() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "  postgresql  ");
        props.setProperty("db.postgresql.host", "  localhost  ");
        props.setProperty("db.postgresql.database", "  blockchain  ");
        props.setProperty("db.postgresql.username", "  user  ");
        props.setProperty("db.postgresql.password", "  pass  ");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Values with whitespace");
        }

        DatabaseConfig config = loader.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "Database type should be trimmed");
    }

    // ======================== Complete Workflow Tests ========================

    @Test
    @DisplayName("Should handle complete generate-load workflow")
    void testCompleteGenerateLoadWorkflow() throws Exception {
        // 1. Generate default file
        Path generatedPath = loader.generateDefaultFile();
        assertTrue(Files.exists(generatedPath), "File should be generated");

        // 2. Load the generated file
        DatabaseConfig config = loader.loadConfig();
        assertNotNull(config, "Should load generated config");

        // 3. Verify the config is valid
        assertNotNull(config.getDatabaseType(), "Database type should be set");
        assertNotNull(config.getDatabaseUrl(), "Database URL should be set");
    }

    @Test
    @DisplayName("Should handle multiple load operations")
    void testMultipleLoadOperations() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.type", "sqlite");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test config");
        }

        // Load multiple times
        for (int i = 0; i < 5; i++) {
            DatabaseConfig config = loader.loadConfig();
            assertNotNull(config, "Config should be loaded on iteration " + i);
            assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                    "Database type should be consistent on iteration " + i);
        }
    }

    @Test
    @DisplayName("Should handle file modification between loads")
    void testFileModificationBetweenLoads() throws Exception {
        // Create initial SQLite config
        Properties props1 = new Properties();
        props1.setProperty("db.type", "sqlite");

        try (var out = Files.newOutputStream(configFile)) {
            props1.store(out, "SQLite config");
        }

        DatabaseConfig config1 = loader.loadConfig();
        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config1.getDatabaseType(),
                "Should load SQLite first");

        // Modify to PostgreSQL config
        Properties props2 = new Properties();
        props2.setProperty("db.type", "postgresql");
        props2.setProperty("db.postgresql.host", "localhost");
        props2.setProperty("db.postgresql.database", "blockchain");
        props2.setProperty("db.postgresql.username", "user");
        props2.setProperty("db.postgresql.password", "pass");

        try (var out = Files.newOutputStream(configFile)) {
            props2.store(out, "PostgreSQL config");
        }

        DatabaseConfig config2 = loader.loadConfig();
        assertNotNull(config2, "PostgreSQL config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config2.getDatabaseType(),
                "Should load PostgreSQL after modification");
    }
}
