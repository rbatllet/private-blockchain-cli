package com.rbatllet.blockchain.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
import com.rbatllet.blockchain.config.util.DatabaseMigrator;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.JPAUtil;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import picocli.CommandLine;

/**
 * Comprehensive test suite for MigrateCommand.
 * Tests all subcommands: run, show-history, validate, current-version.
 *
 * <p><b>Important Architectural Note:</b></p>
 * <p>Each test starts with a fresh database with migration history reset.
 * The setUp() method initializes JPAUtil and then calls resetForTesting() to drop
 * the schema_version table, ensuring a clean state for each test.</p>
 *
 * <p>This means tests start with NO migrations applied. The first call to any
 * migrate command will apply V1 as the baseline migration.</p>
 */
@DisplayName("MigrateCommand Tests")
public class MigrateCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine cli;
    private CLIDatabaseConfigManager configManager;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        // Disable System.exit() for testing
        ExitUtil.disableExit();

        cli = new CommandLine(new MigrateCommand());
        configManager = CLIDatabaseConfigManager.getInstance();

        // Clear any previous CLI arguments
        configManager.clearCliArguments();

        // Ignore environment variables for default behavior tests
        configManager.setIgnoreEnvironmentVariables(true);

        // Use unique database name for each test to ensure isolation
        String uniqueDbName = "test_migrate_" + System.currentTimeMillis();
        configManager.setCliArguments("h2", null, null, null, uniqueDbName, null, null);

        configManager.reload();

        // Initialize JPAUtil with the configuration
        // This is essential because MigrateCommand uses DatabaseMigrator which requires JPAUtil
        JPAUtil.initialize(configManager.getConfig());

        // Reset migration history to ensure clean state for each test
        // This drops the schema_version table so each test starts with NO migrations applied
        DatabaseMigrator migrator = new DatabaseMigrator(configManager.getConfig());
        migrator.resetForTesting();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Re-enable System.exit() after testing
        ExitUtil.enableExit();

        // Reset configuration to defaults for next test
        configManager.resetForTesting();

        // Keep ignoring environment variables for test consistency
        configManager.setIgnoreEnvironmentVariables(true);

        // Force reload to apply default configuration
        configManager.reload();

        // Shutdown and reinitialize JPAUtil with clean configuration
        // This is critical because this test modifies database configuration
        JPAUtil.shutdown();
        JPAUtil.initialize(configManager.getConfig());
    }

    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    // ======================== Main Command Tests ========================

    @Test
    @DisplayName("Should show error when no subcommand is provided")
    void testNoSubcommand() {
        int exitCode = cli.execute();
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(1, realExitCode, "Should fail when no subcommand is provided");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Please specify a subcommand"),
                "Should show subcommand requirement: " + errorOutput);
        assertTrue(errorOutput.contains("migrate"),
                "Should mention 'migrate' subcommand: " + errorOutput);
        assertTrue(errorOutput.contains("show-history"),
                "Should mention 'show-history' subcommand: " + errorOutput);
        assertTrue(errorOutput.contains("validate"),
                "Should mention 'validate' subcommand: " + errorOutput);
        assertTrue(errorOutput.contains("current-version"),
                "Should mention 'current-version' subcommand: " + errorOutput);
    }

    // ======================== Current Version Tests ========================

    @Test
    @DisplayName("Should show no migrations on fresh database")
    void testCurrentVersionWithAutoAppliedV1() {
        int exitCode = cli.execute("current-version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show header: " + output);
        // Test starts fresh with no migrations applied
        assertTrue(output.contains("No migrations have been applied yet"),
                "Should show no migrations message: " + output);
    }

    @Test
    @DisplayName("Should show no current version in JSON format on fresh database")
    void testCurrentVersionJson() {
        int exitCode = cli.execute("current-version", "--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed");
        String output = outContent.toString();
        // Test starts fresh with no migrations
        assertTrue(output.contains("\"currentVersion\"") && output.contains("null"),
                "Should contain currentVersion null: " + output);
        assertTrue(output.contains("\"hasMigrations\"") && output.contains("false"),
                "Should contain hasMigrations false: " + output);
    }

    // ======================== Run Migration Tests ========================

    @Test
    @DisplayName("Should apply V1 migration on fresh database")
    void testRunMigrationWithV1AutoApplied() {
        int exitCode = cli.execute("run");
        int realExitCode = getRealExitCode(exitCode);

        String output = outContent.toString();
        String errorOutput = errContent.toString();

        assertEquals(0, realExitCode, "Should succeed. Error output: " + errorOutput + "\nStandard output: " + output);
        assertTrue(output.contains("Database Migration"),
                "Should show migration header: " + output);
        // V1 should be applied during this run (fresh database)
        assertTrue(output.contains("Migration completed successfully"),
                "Should show V1 was applied: " + output);
        assertTrue(output.contains("V1"),
                "Should mention V1 version: " + output);
    }

    @Test
    @DisplayName("Should show JSON with 1 migration applied on fresh database")
    void testRunMigrationJsonUpToDate() {
        int exitCode = cli.execute("run", "--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("\"success\"") && output.contains("true"),
                "Should have success true: " + output);
        // V1 is applied during this first run
        assertTrue(output.contains("\"migrationsApplied\"") && output.contains("1"),
                "Should have 1 migration applied (V1): " + output);
    }

    // ======================== Show History Tests ========================

    @Test
    @DisplayName("Should show no history on fresh database")
    void testShowHistoryWithV1AutoApplied() {
        int exitCode = cli.execute("show-history");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("Migration History"),
                "Should show header: " + output);
        // Test starts fresh with no migrations
        assertTrue(output.contains("No migrations have been applied yet"),
                "Should show no migrations message: " + output);
    }

    @Test
    @DisplayName("Should show empty history in JSON format on fresh database")
    void testShowHistoryJson() {
        int exitCode = cli.execute("show-history", "--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed");
        String output = outContent.toString();
        // Test starts fresh with no migrations - should be empty JSON array
        // Output may have logs before the JSON, so check it contains the empty array
        assertTrue(output.contains("[ ]"), "Should contain empty JSON array: " + output);
    }

    // ======================== Validate Tests ========================

    @Test
    @DisplayName("Should validate successfully on fresh database")
    void testValidateWithV1AutoApplied() {
        int exitCode = cli.execute("validate");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Validation should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("Schema Validation"),
                "Should show header: " + output);
        // Fresh database with no migrations applied is valid
        assertTrue(output.contains("No migrations applied yet"),
                "Should show no migrations message: " + output);
    }

    @Test
    @DisplayName("Should show validation result in JSON format on fresh database")
    void testValidateJson() {
        int exitCode = cli.execute("validate", "--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed. Output: " + outContent.toString());
        String output = outContent.toString();
        assertTrue(output.contains("\"valid\"") && output.contains("true"),
                "Should have valid true: " + output);
        assertTrue(output.contains("\"message\""),
                "Should have message field: " + output);
    }

    // ======================== Alias Tests ========================

    @Test
    @DisplayName("Should accept 'migrate' alias for 'run'")
    void testMigrateAlias() {
        int exitCode = cli.execute("migrate");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed with alias");
        String output = outContent.toString();
        assertTrue(output.contains("Database Migration"),
                "Should show migration header: " + output);
    }

    @Test
    @DisplayName("Should accept 'history' alias for 'show-history'")
    void testHistoryAlias() {
        int exitCode = cli.execute("history");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed with alias");
        String output = outContent.toString();
        assertTrue(output.contains("Migration History"),
                "Should show history header: " + output);
    }

    @Test
    @DisplayName("Should accept 'version' alias for 'current-version'")
    void testVersionAlias() {
        int exitCode = cli.execute("version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should succeed with alias");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show version header: " + output);
    }

    // ======================== Complete Workflow Tests ========================

    @Test
    @DisplayName("Should handle complete migration workflow")
    void testCompleteWorkflow() {
        // 1. Check version (fresh database, no migrations)
        int exitCode1 = cli.execute("current-version");
        assertEquals(0, getRealExitCode(exitCode1), "Current version should succeed");
        assertTrue(outContent.toString().contains("No migrations have been applied yet"),
                "Should show no migrations on fresh database");
        outContent.reset();

        // 2. Run migration (should apply V1)
        int exitCode2 = cli.execute("run");
        assertEquals(0, getRealExitCode(exitCode2), "Migration should succeed");
        assertTrue(outContent.toString().contains("Migration completed successfully"),
                "Should show migration success");
        outContent.reset();

        // 3. Show history (should have V1 now)
        int exitCode3 = cli.execute("show-history");
        assertEquals(0, getRealExitCode(exitCode3), "Show history should succeed");
        assertTrue(outContent.toString().contains("Total Migrations: 1"),
                "Should show 1 migration in history");
        outContent.reset();

        // 4. Validate schema
        int exitCode4 = cli.execute("validate");
        assertEquals(0, getRealExitCode(exitCode4), "Validation should succeed");
        assertTrue(outContent.toString().contains("Schema Validation"),
                "Should show validation header");
    }

    // ======================== Database Configuration Tests ========================

    @Test
    @DisplayName("Should work with H2 configuration")
    void testH2Configuration() {
        // Already configured with H2 in setUp()
        int exitCode = cli.execute("current-version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should work with H2 config");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show header: " + output);
        // Fresh database has no migrations
        assertTrue(output.contains("No migrations have been applied yet"),
                "Should show no migrations: " + output);
    }

    @Test
    @DisplayName("Should work with SQLite configuration")
    void testSQLiteConfiguration() {
        String uniqueDbName = "test_sqlite_" + System.currentTimeMillis();
        configManager.setCliArguments("sqlite", null, null, null, uniqueDbName, null, null);

        int exitCode = cli.execute("current-version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should work with SQLite config");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show header: " + output);
    }

    @Test
    @DisplayName("Should work with PostgreSQL configuration")
    void testPostgreSQLConfiguration() {
        // Skip if PostgreSQL is not available
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (host == null || dbName == null) {
            System.out.println("⚠️  Skipping PostgreSQL test - DB_HOST or DB_NAME not set");
            return;
        }

        // Configure PostgreSQL
        configManager.setCliArguments("postgresql", null, host,
            port != null ? Integer.parseInt(port) : 5432,
            dbName, user, password);

        // Reinitialize with PostgreSQL config
        JPAUtil.shutdown();
        JPAUtil.initialize(configManager.getConfig());

        // Reset migration history for clean test
        DatabaseMigrator migrator = new DatabaseMigrator(configManager.getConfig());
        migrator.resetForTesting();

        int exitCode = cli.execute("current-version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should work with PostgreSQL config");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show header: " + output);
    }

    @Test
    @DisplayName("Should work with MySQL configuration")
    void testMySQLConfiguration() {
        // Skip if MySQL is not available
        String host = System.getenv("MYSQL_HOST");
        String port = System.getenv("MYSQL_PORT");
        String dbName = System.getenv("MYSQL_DATABASE");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        if (host == null || dbName == null) {
            System.out.println("⚠️  Skipping MySQL test - MYSQL_HOST or MYSQL_DATABASE not set");
            return;
        }

        // Configure MySQL
        configManager.setCliArguments("mysql", null, host,
            port != null ? Integer.parseInt(port) : 3306,
            dbName, user, password);

        // Reinitialize with MySQL config
        JPAUtil.shutdown();
        JPAUtil.initialize(configManager.getConfig());

        // Reset migration history for clean test
        DatabaseMigrator migrator = new DatabaseMigrator(configManager.getConfig());
        migrator.resetForTesting();

        int exitCode = cli.execute("current-version");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should work with MySQL config");
        String output = outContent.toString();
        assertTrue(output.contains("Current Schema Version"),
                "Should show header: " + output);
    }

    // ======================== Edge Cases Tests ========================

    @Test
    @DisplayName("Should handle multiple rapid migrations gracefully")
    void testMultipleRapidMigrations() {
        // Run migration 3 times in quick succession
        // First run: applies V1
        int exitCode1 = cli.execute("run");
        assertEquals(0, getRealExitCode(exitCode1), "First run should succeed");
        outContent.reset();

        // Second and third runs: up to date
        int exitCode2 = cli.execute("run");
        assertEquals(0, getRealExitCode(exitCode2), "Second run should succeed");
        outContent.reset();

        int exitCode3 = cli.execute("run");
        assertEquals(0, getRealExitCode(exitCode3), "Third run should succeed");

        // After V1 is applied, subsequent runs show up to date
        assertTrue(outContent.toString().contains("Database is up to date"),
                "Should show up to date after V1 applied");
    }

    @Test
    @DisplayName("Should maintain migration history consistency")
    void testMigrationHistoryConsistency() {
        // Apply V1 first
        cli.execute("run");
        outContent.reset();

        // Check history multiple times - should be consistent
        cli.execute("show-history");
        String firstHistory = outContent.toString();
        outContent.reset();

        cli.execute("show-history");
        String secondHistory = outContent.toString();

        // Both should show same V1 migration
        assertTrue(firstHistory.contains("V1") && secondHistory.contains("V1"),
                "History should consistently show V1");
        assertTrue(firstHistory.contains("Total Migrations: 1") &&
                   secondHistory.contains("Total Migrations: 1"),
                "History count should be consistent");
    }

    @Test
    @DisplayName("Should show consistent version across calls")
    void testVersionConsistency() {
        // Apply V1 first
        cli.execute("run");
        outContent.reset();

        // Check version multiple times
        cli.execute("current-version");
        String firstVersion = outContent.toString();
        outContent.reset();

        cli.execute("current-version");
        String secondVersion = outContent.toString();

        // Both should show V1
        assertTrue(firstVersion.contains("Current Version: V1"),
                "First call should show V1");
        assertTrue(secondVersion.contains("Current Version: V1"),
                "Second call should show V1");
    }

    @Test
    @DisplayName("Should validate successfully multiple times")
    void testMultipleValidations() {
        int exitCode1 = cli.execute("validate");
        assertEquals(0, getRealExitCode(exitCode1), "First validation should succeed");
        outContent.reset();

        int exitCode2 = cli.execute("validate");
        assertEquals(0, getRealExitCode(exitCode2), "Second validation should succeed");

        assertTrue(outContent.toString().contains("Schema Validation"),
                "Should show validation header on repeated validations");
    }

    @Test
    @DisplayName("Should output valid JSON for all JSON commands")
    void testJsonOutputValidity() {
        // Test current-version JSON
        cli.execute("current-version", "--json");
        String versionJson = outContent.toString();
        assertTrue(versionJson.contains("{") && versionJson.contains("}"),
                "Version JSON should be valid");
        outContent.reset();

        // Test show-history JSON
        cli.execute("show-history", "--json");
        String historyJson = outContent.toString();
        assertTrue(historyJson.contains("[") && historyJson.contains("]"),
                "History JSON should be valid array");
        outContent.reset();

        // Test validate JSON
        cli.execute("validate", "--json");
        String validateJson = outContent.toString();
        assertTrue(validateJson.contains("{") && validateJson.contains("}"),
                "Validate JSON should be valid");
        outContent.reset();

        // Test run JSON
        cli.execute("run", "--json");
        String runJson = outContent.toString();
        assertTrue(runJson.contains("{") && runJson.contains("}"),
                "Run JSON should be valid");
    }
}
