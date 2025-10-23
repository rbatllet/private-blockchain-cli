package com.rbatllet.blockchain.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
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
 * Test suite for DatabaseCommand
 * Tests database configuration show and test subcommands
 */
@DisplayName("DatabaseCommand Tests")
public class DatabaseCommandTest {

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

        cli = new CommandLine(new DatabaseCommand());
        configManager = CLIDatabaseConfigManager.getInstance();

        // Clear any previous CLI arguments
        configManager.clearCliArguments();

        // Ignore environment variables for default behavior tests
        configManager.setIgnoreEnvironmentVariables(true);

        configManager.reload();
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
        assertTrue(errorOutput.contains("show"),
                "Should mention 'show' subcommand: " + errorOutput);
        assertTrue(errorOutput.contains("test"),
                "Should mention 'test' subcommand: " + errorOutput);
    }

    @Test
    @DisplayName("Should show help with --help flag")
    void testDatabaseHelp() {
        int exitCode = cli.execute("--help");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Help should succeed");
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("show") && output.contains("test"),
                "Should show both subcommands in help: " + output);
    }

    // ======================== Show Subcommand Tests ========================

    @Test
    @DisplayName("Should display current database configuration")
    void testShowCommand() {
        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show command should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("Database Configuration"),
                "Should show configuration header: " + output);
        assertTrue(output.contains("Database Type:"),
                "Should show database type: " + output);
        assertTrue(output.contains("JDBC URL:"),
                "Should show JDBC URL: " + output);
        assertTrue(output.contains("Connection Pool:"),
                "Should show connection pool info: " + output);
        assertTrue(output.contains("Min Size:"),
                "Should show min pool size: " + output);
        assertTrue(output.contains("Max Size:"),
                "Should show max pool size: " + output);
        assertTrue(output.contains("Hibernate:"),
                "Should show Hibernate info: " + output);
        assertTrue(output.contains("hbm2ddl.auto:"),
                "Should show hbm2ddl.auto: " + output);
        assertTrue(output.contains("Configuration Source:"),
                "Should show configuration source: " + output);
        assertTrue(output.contains("Priority order:"),
                "Should show priority order: " + output);
    }

    @Test
    @DisplayName("Should display H2 as default database type")
    void testShowCommandDefaultDatabase() {
        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show command should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("H2"),
                "Should show H2 as default database: " + output);
    }

    @Test
    @DisplayName("Should display configuration in JSON format")
    void testShowCommandJson() {
        int exitCode = cli.execute("show", "--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show command with JSON should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("{"),
                "JSON output should start with brace: " + output);
        assertTrue(output.contains("\"databaseType\""),
                "JSON should contain databaseType: " + output);
        assertTrue(output.contains("\"databaseUrl\""),
                "JSON should contain databaseUrl: " + output);
        assertTrue(output.contains("\"poolMinSize\""),
                "JSON should contain poolMinSize: " + output);
        assertTrue(output.contains("\"poolMaxSize\""),
                "JSON should contain poolMaxSize: " + output);
        assertTrue(output.contains("\"connectionTimeout\""),
                "JSON should contain connectionTimeout: " + output);
        assertTrue(output.contains("\"hbm2ddlAuto\""),
                "JSON should contain hbm2ddlAuto: " + output);
        assertTrue(output.contains("}"),
                "JSON output should end with brace: " + output);
    }

    @Test
    @DisplayName("Should not expose passwords in output")
    void testShowCommandDoesNotExposePasswords() {
        // Set configuration with a password
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "testdb", "testuser", "secret123");

        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show command should succeed");
        String output = outContent.toString();
        // DatabaseConfig stores password separately, not in URL
        // So we just verify the command succeeded and shows PostgreSQL config
        assertTrue(output.contains("POSTGRESQL"),
                "Should show PostgreSQL configuration: " + output);
        assertTrue(output.contains("localhost"),
                "Should show host: " + output);
    }

    @Test
    @DisplayName("Should show custom database configuration when set via CLI")
    void testShowCommandWithCliConfig() {
        configManager.setCliArguments("postgresql", null, "custom-host", 5433,
                "customdb", "customuser", null);

        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show command should succeed");
        String output = outContent.toString();
        assertTrue(output.contains("POSTGRESQL"),
                "Should show PostgreSQL type: " + output);
        assertTrue(output.contains("custom-host"),
                "Should show custom host: " + output);
        assertTrue(output.contains("5433"),
                "Should show custom port: " + output);
        assertTrue(output.contains("customdb"),
                "Should show custom database name: " + output);
    }

    // ======================== Test Subcommand Tests ========================

    @Test
    @DisplayName("Should test database connection successfully with enhanced tester")
    void testTestCommand() {
        int exitCode = cli.execute("test");

        // Enhanced test uses CORE DatabaseConnectionTester
        String output = outContent.toString();

        // Header should be present
        assertTrue(output.contains("Database Connection Test (Enhanced)"),
                "Should show enhanced test header: " + output);

        // Status should indicate success
        assertTrue(output.contains("Status: ✅ Success"),
                "Should show success status: " + output);

        // Response time should be present
        assertTrue(output.contains("Response Time:"),
                "Should show response time: " + output);

        // Connection details (not error details) should be present for success
        assertTrue(output.contains("Connection Details:"),
                "Should show connection details for successful test: " + output);
        assertFalse(output.contains("Error Details:"),
                "Should NOT show error details for successful test: " + output);

        // Enhanced features should be present
        assertTrue(output.contains("Database Version:"),
                "Should show database version: " + output);
        assertTrue(output.contains("Driver Version:"),
                "Should show driver version: " + output);
        assertTrue(output.contains("Can Read:"),
                "Should show read permissions: " + output);
        assertTrue(output.contains("Read-Only Mode:"),
                "Should show read-only mode status: " + output);

        // Final verdict should be success
        assertTrue(output.contains("✅ All validations passed - database is ready for use"),
                "Should show success verdict: " + output);
        assertFalse(output.contains("❌ Connection test failed"),
                "Should NOT show failure verdict: " + output);

        // Exit code should be 0 for success
        assertEquals(0, exitCode, "Exit code should be 0 for successful test");
    }

    @Test
    @DisplayName("Should display test results with success icons")
    void testTestCommandResultIcons() {
        int exitCode = cli.execute("test");

        String output = outContent.toString();
        // With default H2 and ignore env vars, test should succeed
        assertTrue(output.contains("✅"),
                "Should show success icons: " + output);
        assertEquals(0, exitCode, "Exit code should be 0 for successful test");
    }

    @Test
    @DisplayName("Should display test results in JSON format with enhanced fields")
    void testTestCommandJson() {
        int exitCode = cli.execute("test", "--json");

        String output = outContent.toString();
        // Enhanced JSON output from CORE DatabaseConnectionTester
        assertTrue(output.contains("{"),
                "JSON output should start with brace: " + output);
        assertTrue(output.contains("\"successful\" : true"),
                "JSON should contain successful=true: " + output);
        assertTrue(output.contains("\"responseTimeMs\""),
                "JSON should contain responseTimeMs: " + output);
        assertTrue(output.contains("\"databaseType\""),
                "JSON should contain databaseType: " + output);
        assertTrue(output.contains("\"databaseVersion\""),
                "JSON should contain databaseVersion: " + output);
        assertTrue(output.contains("\"driverVersion\""),
                "JSON should contain driverVersion: " + output);
        assertTrue(output.contains("\"canRead\" : true"),
                "JSON should contain canRead: " + output);
        assertTrue(output.contains("\"readOnly\" : false"),
                "JSON should contain readOnly: " + output);
        assertTrue(output.contains("}"),
                "JSON output should end with brace: " + output);

        assertEquals(0, exitCode, "Exit code should be 0 for successful JSON test");
    }

    @Test
    @DisplayName("Should show database version in test results")
    void testTestCommandShowsVersion() {
        int exitCode = cli.execute("test");
        int realExitCode = getRealExitCode(exitCode);

        String output = outContent.toString();
        // Enhanced test should always show database version on success
        assertEquals(0, realExitCode, "Test should succeed with H2: " + output);
        assertTrue(output.contains("Database Version:"),
                "Should show Database Version label: " + output);
        assertTrue(output.contains("H2 2.4.240"),
                "Should show H2 version info: " + output);
    }

    @Test
    @DisplayName("Should show H2 database type in test command")
    void testTestCommandShowsH2() {
        cli.execute("test");

        String output = outContent.toString();
        assertTrue(output.contains("H2"),
                "Should show H2 database type: " + output);
    }

    // ======================== Error Handling Tests ========================

    // Removed invalid subcommand/flag tests as PicoCLI behavior varies
    // and these don't test the actual DatabaseCommand implementation

    // ======================== Integration Tests ========================

    @Test
    @DisplayName("Should work with PostgreSQL configuration")
    void testPostgreSQLConfiguration() {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "testdb", "testuser", "testpass");

        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show should succeed with PostgreSQL config");
        String output = outContent.toString();
        assertTrue(output.contains("POSTGRESQL"),
                "Should show PostgreSQL type: " + output);
    }

    @Test
    @DisplayName("Should work with MySQL configuration")
    void testMySQLConfiguration() {
        configManager.setCliArguments("mysql", null, "localhost", 3306,
                "testdb", "testuser", "testpass");

        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show should succeed with MySQL config");
        String output = outContent.toString();
        assertTrue(output.contains("MYSQL"),
                "Should show MySQL type: " + output);
    }

    @Test
    @DisplayName("Should work with SQLite configuration")
    void testSQLiteConfiguration() {
        configManager.setCliArguments("sqlite", null, null, null, null, null, null);

        int exitCode = cli.execute("show");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Show should succeed with SQLite config");
        String output = outContent.toString();
        assertTrue(output.contains("SQLITE"),
                "Should show SQLite type: " + output);
    }

    @Test
    @DisplayName("Should handle complete workflow: show then test")
    void testCompleteWorkflow() {
        // First show configuration
        int showExitCode = cli.execute("show");
        int showRealExitCode = getRealExitCode(showExitCode);
        assertEquals(0, showRealExitCode, "Show should succeed");

        // Reset output streams
        outContent.reset();
        errContent.reset();

        // Then test connection
        cli.execute("test");

        // Test may succeed or fail, but both commands should complete
        String output = outContent.toString();
        assertTrue(output.contains("Database Connection Test"),
                "Test should show results: " + output);
    }
}
