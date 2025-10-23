package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.DatabaseConfig;
import com.rbatllet.blockchain.util.JPAUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CLIDatabaseConfigManager
 * Tests singleton pattern, configuration priority, CLI arguments, environment variables, and security
 */
@DisplayName("CLIDatabaseConfigManager Tests")
public class CLIDatabaseConfigManagerTest {

    private CLIDatabaseConfigManager manager;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        manager = CLIDatabaseConfigManager.getInstance();

        // Clear any CLI arguments from previous tests
        manager.clearCliArguments();

        // Ignore environment variables for default behavior tests
        manager.setIgnoreEnvironmentVariables(true);

        manager.reload();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Reset configuration to defaults for next test
        manager.resetForTesting();

        // Keep ignoring environment variables for test consistency
        manager.setIgnoreEnvironmentVariables(true);

        // Force reload to apply default configuration
        manager.reload();

        // Shutdown and reinitialize JPAUtil with clean configuration
        // This is critical because this test modifies database configuration
        JPAUtil.shutdown();
        JPAUtil.initialize(manager.getConfig());
    }

    // ======================== Singleton Pattern Tests ========================

    @Test
    @DisplayName("Should return singleton instance")
    void testSingletonInstance() {
        CLIDatabaseConfigManager instance1 = CLIDatabaseConfigManager.getInstance();
        CLIDatabaseConfigManager instance2 = CLIDatabaseConfigManager.getInstance();

        assertNotNull(instance1, "Instance should not be null");
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    @DisplayName("Should maintain same instance across multiple calls")
    void testSingletonConsistency() {
        for (int i = 0; i < 10; i++) {
            CLIDatabaseConfigManager instance = CLIDatabaseConfigManager.getInstance();
            assertSame(manager, instance, "Should return same instance on call " + i);
        }
    }

    // ======================== Default Configuration Tests ========================

    @Test
    @DisplayName("Should return default H2 config when no configuration provided")
    void testDefaultConfiguration() {
        DatabaseConfig config = manager.getConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.H2, config.getDatabaseType(),
                "Should default to H2");
        assertNotNull(config.getDatabaseUrl(), "Database URL should not be null");
        assertTrue(config.getDatabaseUrl().contains("jdbc:h2:"),
                "Should be H2 JDBC URL: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should cache configuration after first load")
    void testConfigurationCaching() {
        DatabaseConfig config1 = manager.getConfig();
        DatabaseConfig config2 = manager.getConfig();

        assertSame(config1, config2, "Should return cached instance");
    }

    // ======================== CLI Arguments Tests ========================

    @Test
    @DisplayName("Should use CLI arguments for database type")
    void testCliArgumentsDatabaseType() {
        manager.setCliArguments("h2", null, null, null, null, null, null);
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.H2, config.getDatabaseType(),
                "Should use CLI database type");
    }

    @Test
    @DisplayName("Should use CLI arguments for PostgreSQL configuration")
    void testCliArgumentsPostgreSQL() {
        manager.setCliArguments("postgresql", null, "db-server", 5433,
                               "testdb", "testuser", "testpass");
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "Should be PostgreSQL");
        assertNotNull(config.getDatabaseUrl(), "Database URL should not be null");
        assertTrue(config.getDatabaseUrl().contains("postgresql"),
                "URL should contain postgresql: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("db-server"),
                "URL should contain host: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("5433"),
                "URL should contain port: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("testdb"),
                "URL should contain database name: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should use CLI arguments for MySQL configuration")
    void testCliArgumentsMySQL() {
        manager.setCliArguments("mysql", null, "mysql-server", 3307,
                               "mydb", "myuser", "mypass");
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.MYSQL, config.getDatabaseType(),
                "Should be MySQL");
        assertTrue(config.getDatabaseUrl().contains("mysql"),
                "URL should contain mysql: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("mysql-server"),
                "URL should contain host: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("3307"),
                "URL should contain port: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should use complete JDBC URL when provided via CLI")
    void testCliArgumentsCompleteUrl() {
        String customUrl = "jdbc:postgresql://custom-host:5432/custom-db";
        manager.setCliArguments("postgresql", customUrl, null, null, null, "user", "pass");
        DatabaseConfig config = manager.getConfig();

        assertEquals(customUrl, config.getDatabaseUrl(),
                "Should use custom URL exactly");
    }

    @Test
    @DisplayName("Should default to localhost when host not provided")
    void testCliArgumentsDefaultHost() {
        manager.setCliArguments("postgresql", null, null, null, null, "user", "pass");
        DatabaseConfig config = manager.getConfig();

        assertTrue(config.getDatabaseUrl().contains("localhost"),
                "Should default to localhost: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should use default port for PostgreSQL when not provided")
    void testCliArgumentsDefaultPostgreSQLPort() {
        manager.setCliArguments("postgresql", null, "host", null, null, "user", "pass");
        DatabaseConfig config = manager.getConfig();

        assertTrue(config.getDatabaseUrl().contains("5432"),
                "Should default to port 5432: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should use default port for MySQL when not provided")
    void testCliArgumentsDefaultMySQLPort() {
        manager.setCliArguments("mysql", null, "host", null, null, "user", "pass");
        DatabaseConfig config = manager.getConfig();

        assertTrue(config.getDatabaseUrl().contains("3306"),
                "Should default to port 3306: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should use default database name when not provided")
    void testCliArgumentsDefaultDatabaseName() {
        manager.setCliArguments("postgresql", null, "host", null, null, "user", "pass");
        DatabaseConfig config = manager.getConfig();

        assertTrue(config.getDatabaseUrl().contains("blockchain"),
                "Should default to 'blockchain': " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should handle unknown database type gracefully")
    void testCliArgumentsUnknownDatabaseType() {
        manager.setCliArguments("unknown-db", null, null, null, null, null, null);
        DatabaseConfig config = manager.getConfig();

        // Should fall back to default H2
        assertEquals(DatabaseConfig.DatabaseType.H2, config.getDatabaseType(),
                "Should fall back to H2 for unknown type");
    }

    // ======================== Security Tests ========================

    @Test
    @DisplayName("Should display security warning when password provided via CLI")
    void testSecurityWarningForCliPassword() {
        errContent.reset();

        manager.setCliArguments("postgresql", null, "host", null, null, "user", "insecure-password");

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("SECURITY WARNING"),
                "Should display security warning: " + errorOutput);
        assertTrue(errorOutput.contains("INSECURE"),
                "Should mention insecure: " + errorOutput);
        assertTrue(errorOutput.contains("command-line argument"),
                "Should mention command-line: " + errorOutput);
        assertTrue(errorOutput.contains("DB_PASSWORD"),
                "Should recommend environment variable: " + errorOutput);
    }

    @Test
    @DisplayName("Should not display warning when password not provided")
    void testNoSecurityWarningWithoutPassword() {
        errContent.reset();

        manager.setCliArguments("postgresql", null, "host", null, null, "user", null);

        String errorOutput = errContent.toString();
        assertFalse(errorOutput.contains("SECURITY WARNING"),
                "Should not display security warning: " + errorOutput);
    }

    @Test
    @DisplayName("Should not display warning for empty password")
    void testNoSecurityWarningForEmptyPassword() {
        errContent.reset();

        manager.setCliArguments("postgresql", null, "host", null, null, "user", "");

        String errorOutput = errContent.toString();
        assertFalse(errorOutput.contains("SECURITY WARNING"),
                "Should not display security warning for empty password: " + errorOutput);
    }

    // ======================== Reload Tests ========================

    @Test
    @DisplayName("Should reload configuration when requested")
    void testReloadConfiguration() {
        // Get initial config
        DatabaseConfig config1 = manager.getConfig();
        assertEquals(DatabaseConfig.DatabaseType.H2, config1.getDatabaseType(),
                "Should start with H2");

        // Change CLI arguments and reload
        manager.setCliArguments("sqlite", null, null, null, null, null, null);
        DatabaseConfig config2 = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config2.getDatabaseType(),
                "Should use new SQLite type after reload");
        assertNotSame(config1, config2, "Should be different instance after reload");
    }

    @Test
    @DisplayName("Should clear cache on reload")
    void testReloadClearsCache() {
        DatabaseConfig config1 = manager.getConfig();

        manager.reload();

        DatabaseConfig config2 = manager.getConfig();
        assertNotSame(config1, config2,
                "Should return new instance after explicit reload");
    }

    // ======================== Properties Loader Integration Tests ========================

    @Test
    @DisplayName("Should have properties loader available")
    void testPropertiesLoaderAvailability() {
        CLIDatabasePropertiesLoader loader = manager.getPropertiesLoader();

        assertNotNull(loader, "Properties loader should not be null");
    }

    @Test
    @DisplayName("Should use properties loader for configuration file")
    void testPropertiesLoaderIntegration() {
        CLIDatabasePropertiesLoader loader = manager.getPropertiesLoader();

        assertNotNull(loader.getConfigFilePath(),
                "Config file path should be available");
        assertTrue(loader.getConfigFilePath().toString().contains(".blockchain-cli"),
                "Should use .blockchain-cli directory: " + loader.getConfigFilePath());
    }

    // ======================== Priority Order Tests ========================

    @Test
    @DisplayName("CLI arguments should override defaults")
    void testPriorityCliOverridesDefaults() {
        manager.setCliArguments("postgresql", null, "cli-host", null, null, null, null);
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "CLI database type should override default");
        assertTrue(config.getDatabaseUrl().contains("cli-host"),
                "CLI host should be used: " + config.getDatabaseUrl());
    }

    // ======================== Thread Safety Tests ========================

    @Test
    @DisplayName("Should handle concurrent getInstance calls")
    void testConcurrentGetInstance() throws InterruptedException {
        final int threadCount = 10;
        final CLIDatabaseConfigManager[] instances = new CLIDatabaseConfigManager[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = CLIDatabaseConfigManager.getInstance();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All instances should be the same
        for (int i = 1; i < threadCount; i++) {
            assertSame(instances[0], instances[i],
                    "Instance " + i + " should be same as instance 0");
        }
    }

    @Test
    @DisplayName("Should handle concurrent getConfig calls")
    void testConcurrentGetConfig() throws InterruptedException {
        final int threadCount = 10;
        final DatabaseConfig[] configs = new DatabaseConfig[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                configs[index] = manager.getConfig();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All configs should be the same (cached)
        for (int i = 1; i < threadCount; i++) {
            assertSame(configs[0], configs[i],
                    "Config " + i + " should be same as config 0");
        }
    }

    // ======================== Edge Cases Tests ========================

    @Test
    @DisplayName("Should handle null CLI arguments gracefully")
    void testNullCliArguments() {
        manager.clearCliArguments();
        DatabaseConfig config = manager.getConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals(DatabaseConfig.DatabaseType.H2, config.getDatabaseType(),
                "Should default to H2 with null arguments");
    }

    @Test
    @DisplayName("Should handle partial CLI arguments")
    void testPartialCliArguments() {
        manager.setCliArguments("postgresql", null, null, null, "customdb", null, null);
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config.getDatabaseType(),
                "Should use provided database type");
        assertTrue(config.getDatabaseUrl().contains("customdb"),
                "Should use provided database name: " + config.getDatabaseUrl());
        assertTrue(config.getDatabaseUrl().contains("localhost"),
                "Should use default host: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should handle SQLite type correctly")
    void testSQLiteType() {
        manager.setCliArguments("sqlite", null, null, null, null, null, null);
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.SQLITE, config.getDatabaseType(),
                "Should be SQLite");
        assertTrue(config.getDatabaseUrl().startsWith("jdbc:sqlite:"),
                "Should have SQLite JDBC URL: " + config.getDatabaseUrl());
    }

    @Test
    @DisplayName("Should handle H2 type correctly")
    void testH2Type() {
        manager.setCliArguments("h2", null, null, null, null, null, null);
        DatabaseConfig config = manager.getConfig();

        assertEquals(DatabaseConfig.DatabaseType.H2, config.getDatabaseType(),
                "Should be H2");
        assertNotNull(config.getDatabaseUrl(), "Database URL should not be null");
    }

    // ======================== Complete Workflow Tests ========================

    @Test
    @DisplayName("Should handle complete configuration lifecycle")
    void testCompleteConfigurationLifecycle() {
        // 1. Start with default
        DatabaseConfig config1 = manager.getConfig();
        assertEquals(DatabaseConfig.DatabaseType.H2, config1.getDatabaseType(),
                "Should start with H2");

        // 2. Set PostgreSQL via CLI
        manager.setCliArguments("postgresql", null, "pg-host", 5433,
                               "pgdb", "pguser", null);
        DatabaseConfig config2 = manager.getConfig();
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config2.getDatabaseType(),
                "Should switch to PostgreSQL");

        // 3. Reload
        manager.reload();
        DatabaseConfig config3 = manager.getConfig();
        assertEquals(DatabaseConfig.DatabaseType.POSTGRESQL, config3.getDatabaseType(),
                "Should maintain PostgreSQL after reload");

        // 4. Clear CLI arguments
        manager.clearCliArguments();
        DatabaseConfig config4 = manager.getConfig();
        assertEquals(DatabaseConfig.DatabaseType.H2, config4.getDatabaseType(),
                "Should revert to H2 when CLI args cleared");
    }
}
