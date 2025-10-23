package com.rbatllet.blockchain.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.JPAUtil;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test suite for DatabaseCommand.ExportCommand
 * Tests database configuration export to various formats (Properties, JSON, ENV)
 */
@DisplayName("DatabaseCommand.ExportCommand Tests")
public class DatabaseCommandExportTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine cli;
    private CLIDatabaseConfigManager configManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        ExitUtil.disableExit();

        cli = new CommandLine(new DatabaseCommand());
        configManager = CLIDatabaseConfigManager.getInstance();

        configManager.clearCliArguments();
        configManager.setIgnoreEnvironmentVariables(true);
        configManager.reload();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);

        ExitUtil.enableExit();

        configManager.resetForTesting();
        configManager.setIgnoreEnvironmentVariables(true);
        configManager.reload();

        JPAUtil.shutdown();
        JPAUtil.initialize(configManager.getConfig());
    }

    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    // ======================== Export to Properties Format Tests ========================

    @Test
    @DisplayName("Should export configuration to properties file with auto-detection")
    void testExportToPropertiesAutoDetect() throws Exception {
        Path outputFile = tempDir.resolve("database.properties");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export should succeed");
        assertTrue(Files.exists(outputFile), "File should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("# Database Configuration"),
                "Should have database configuration header: " + content);
        assertTrue(content.contains("db.type="),
                "Should contain db.type property: " + content);

        String output = outContent.toString();
        assertTrue(output.contains("Database configuration exported successfully"),
                "Should show success message: " + output);
        assertTrue(output.contains(outputFile.toString()),
                "Should show output file path: " + output);
    }

    @Test
    @DisplayName("Should export configuration to properties file with explicit format")
    void testExportToPropertiesExplicitFormat() throws Exception {
        Path outputFile = tempDir.resolve("config.txt");

        int exitCode = cli.execute("export", "--file", outputFile.toString(),
                "--format", "PROPERTIES");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with explicit format should succeed");
        assertTrue(Files.exists(outputFile), "File should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("# Database Configuration"),
                "Should have properties header: " + content);
        assertTrue(content.contains("db.type="),
                "Should contain db.type property: " + content);
    }

    @Test
    @DisplayName("Should mask passwords in properties export by default")
    void testExportToPropertiesMaskingDefault() throws Exception {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "testdb", "testuser", "secretpassword123");

        Path outputFile = tempDir.resolve("database.properties");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export should succeed");
        String content = Files.readString(outputFile);

        assertFalse(content.contains("secretpassword123"),
                "Should NOT contain actual password in masked export: " + content);

        String output = outContent.toString();
        assertTrue(output.contains("Passwords are masked"),
                "Should indicate passwords are masked: " + output);
    }

    @Test
    @DisplayName("Should export properties without masking when --no-mask flag is used")
    void testExportToPropertiesNoMask() throws Exception {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "testdb", "testuser", "secretpassword123");

        Path outputFile = tempDir.resolve("database.properties");

        int exitCode = cli.execute("export", "--file", outputFile.toString(), "--no-mask");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with --no-mask should succeed");
        String content = Files.readString(outputFile);

        assertTrue(content.contains("secretpassword123"),
                "Should contain actual password when --no-mask is used: " + content);

        String output = outContent.toString();
        assertFalse(output.contains("Passwords are masked"),
                "Should NOT mention masking when --no-mask is used: " + output);
    }

    // ======================== Export to JSON Format Tests ========================

    @Test
    @DisplayName("Should export configuration to JSON file with auto-detection")
    void testExportToJsonAutoDetect() throws Exception {
        Path outputFile = tempDir.resolve("database.json");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export to JSON should succeed");
        assertTrue(Files.exists(outputFile), "JSON file should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("{"),
                "JSON should start with opening brace: " + content);
        assertTrue(content.contains("\"type\""),
                "Should contain 'type' field: " + content);
        assertTrue(content.trim().endsWith("}"),
                "JSON should end with closing brace: " + content);

        // Verify valid JSON by parsing
        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> mapper.readTree(content),
                "Should be valid JSON");
    }

    @Test
    @DisplayName("Should export configuration to JSON file with explicit format")
    void testExportToJsonExplicitFormat() throws Exception {
        Path outputFile = tempDir.resolve("config.txt");

        int exitCode = cli.execute("export", "--file", outputFile.toString(),
                "--format", "JSON");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with explicit JSON format should succeed");
        assertTrue(Files.exists(outputFile), "File should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("{"),
                "Should have JSON structure: " + content);
        assertTrue(content.contains("\"type\""),
                "Should contain type field: " + content);
    }

    @Test
    @DisplayName("Should mask passwords in JSON export by default")
    void testExportToJsonMaskingDefault() throws Exception {
        configManager.setCliArguments("mysql", null, "localhost", 3306,
                "mydb", "myuser", "mysecret789");

        Path outputFile = tempDir.resolve("database.json");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export should succeed");
        String content = Files.readString(outputFile);

        assertFalse(content.contains("mysecret789"),
                "Should NOT contain actual password in masked JSON export: " + content);
        assertTrue(content.contains("\"password\" : \"********\""),
                "Should have masked password with asterisks: " + content);
    }

    @Test
    @DisplayName("Should export JSON without masking when --no-mask flag is used")
    void testExportToJsonNoMask() throws Exception {
        configManager.setCliArguments("mysql", null, "localhost", 3306,
                "mydb", "myuser", "mysecret789");

        Path outputFile = tempDir.resolve("database.json");

        int exitCode = cli.execute("export", "--file", outputFile.toString(), "--no-mask");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with --no-mask should succeed");
        String content = Files.readString(outputFile);

        assertTrue(content.contains("mysecret789"),
                "Should contain actual password when --no-mask is used: " + content);
    }

    // ======================== Export to ENV Format Tests ========================

    @Test
    @DisplayName("Should export configuration to env file with auto-detection")
    void testExportToEnvAutoDetect() throws Exception {
        Path outputFile = tempDir.resolve("database.env");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export to ENV should succeed");
        assertTrue(Files.exists(outputFile), "ENV file should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("# Database Environment Variables"),
                "Should have ENV header: " + content);
        assertTrue(content.contains("DB_TYPE="),
                "Should contain DB_TYPE variable: " + content);
    }

    @Test
    @DisplayName("Should export configuration to env file with explicit format")
    void testExportToEnvExplicitFormat() throws Exception {
        Path outputFile = tempDir.resolve("config.txt");

        int exitCode = cli.execute("export", "--file", outputFile.toString(),
                "--format", "ENV");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with explicit ENV format should succeed");
        assertTrue(Files.exists(outputFile), "File should be created");

        String content = Files.readString(outputFile);
        assertTrue(content.contains("DB_TYPE="),
                "Should contain DB_TYPE in ENV format: " + content);
    }

    @Test
    @DisplayName("Should mask passwords in ENV export by default")
    void testExportToEnvMaskingDefault() throws Exception {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "envdb", "envuser", "envsecret999");

        Path outputFile = tempDir.resolve("database.env");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export should succeed");
        String content = Files.readString(outputFile);

        assertFalse(content.contains("envsecret999"),
                "Should NOT contain actual password in masked ENV export: " + content);
        assertTrue(content.contains("DB_PASSWORD="),
                "Should contain DB_PASSWORD variable: " + content);
    }

    @Test
    @DisplayName("Should export ENV without masking when --no-mask flag is used")
    void testExportToEnvNoMask() throws Exception {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "envdb", "envuser", "envsecret999");

        Path outputFile = tempDir.resolve("database.env");

        int exitCode = cli.execute("export", "--file", outputFile.toString(), "--no-mask");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Export with --no-mask should succeed");
        String content = Files.readString(outputFile);

        assertTrue(content.contains("envsecret999"),
                "Should contain actual password when --no-mask is used: " + content);
    }

    // ======================== Format Auto-Detection Tests ========================

    @Test
    @DisplayName("Should auto-detect properties format from .properties extension")
    void testAutoDetectPropertiesExtension() throws Exception {
        Path outputFile = tempDir.resolve("my-config.properties");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should auto-detect .properties format");
        String content = Files.readString(outputFile);
        assertTrue(content.contains("# Database Configuration"),
                "Should export as properties format: " + content);
    }

    @Test
    @DisplayName("Should auto-detect json format from .json extension")
    void testAutoDetectJsonExtension() throws Exception {
        Path outputFile = tempDir.resolve("my-config.json");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should auto-detect .json format");
        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("{"),
                "Should export as JSON format: " + content);
    }

    @Test
    @DisplayName("Should auto-detect env format from .env extension")
    void testAutoDetectEnvExtension() throws Exception {
        Path outputFile = tempDir.resolve("my-config.env");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should auto-detect .env format");
        String content = Files.readString(outputFile);
        assertTrue(content.contains("DB_TYPE="),
                "Should export as ENV format: " + content);
    }

    @Test
    @DisplayName("Should prefer explicit --format flag over auto-detection")
    void testExplicitFormatOverridesAutoDetect() throws Exception {
        Path outputFile = tempDir.resolve("config.properties");

        // Force JSON format even though file extension is .properties
        int exitCode = cli.execute("export", "--file", outputFile.toString(),
                "--format", "JSON");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should accept explicit format");
        String content = Files.readString(outputFile);
        assertTrue(content.startsWith("{"),
                "Should export as JSON despite .properties extension: " + content);
    }

    // ======================== Database Type Specific Tests ========================

    @Test
    @DisplayName("Should export H2 database configuration")
    void testExportH2Configuration() throws Exception {
        configManager.setCliArguments("h2", null, null, null, null, null, null);

        Path outputFile = tempDir.resolve("h2-config.properties");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should export H2 configuration");
        String content = Files.readString(outputFile);
        assertTrue(content.contains("db.type=h2"),
                "Should show H2 type: " + content);
    }

    @Test
    @DisplayName("Should export PostgreSQL database configuration")
    void testExportPostgreSQLConfiguration() throws Exception {
        configManager.setCliArguments("postgresql", null, "pg-host", 5432,
                "pgdb", "pguser", "pgpass");

        Path outputFile = tempDir.resolve("pg-config.json");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should export PostgreSQL configuration");
        String content = Files.readString(outputFile);
        assertTrue(content.contains("postgresql"),
                "Should show PostgreSQL type: " + content);
        assertTrue(content.contains("pg-host"),
                "Should contain host: " + content);
    }

    @Test
    @DisplayName("Should export MySQL database configuration")
    void testExportMySQLConfiguration() throws Exception {
        configManager.setCliArguments("mysql", null, "mysql-host", 3306,
                "mysqldb", "mysqluser", "mysqlpass");

        Path outputFile = tempDir.resolve("mysql-config.env");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Should export MySQL configuration");
        String content = Files.readString(outputFile);
        assertTrue(content.contains("mysql"),
                "Should show MySQL type: " + content);
        assertTrue(content.contains("mysql-host"),
                "Should contain host: " + content);
    }

    // ======================== Error Handling Tests ========================

    @Test
    @DisplayName("Should report error when --file option is missing")
    void testMissingFileOption() {
        int exitCode = cli.execute("export");
        int realExitCode = getRealExitCode(exitCode);

        String allOutput = outContent.toString() + errContent.toString();
        assertTrue(allOutput.contains("--file"),
                "Should report missing --file option in output. Exit code: " + realExitCode + ", Output: " + allOutput);
    }

    @Test
    @DisplayName("Should fail with invalid format option")
    void testInvalidFormatOption() throws Exception {
        Path outputFile = tempDir.resolve("test.txt");

        int exitCode = cli.execute("export", "--file", outputFile.toString(),
                "--format", "INVALID_FORMAT");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(1, realExitCode, "Should fail with invalid format");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Invalid format"),
                "Should report invalid format error: " + errorOutput);
        assertTrue(errorOutput.contains("PROPERTIES"),
                "Should list PROPERTIES as valid format: " + errorOutput);
        assertTrue(errorOutput.contains("JSON"),
                "Should list JSON as valid format: " + errorOutput);
        assertTrue(errorOutput.contains("ENV"),
                "Should list ENV as valid format: " + errorOutput);
    }

    @Test
    @DisplayName("Should fail with invalid file path")
    void testInvalidFilePath() {
        String invalidPath = "/nonexistent/directory/that/does/not/exist/file.properties";

        int exitCode = cli.execute("export", "--file", invalidPath);
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(1, realExitCode, "Should fail with invalid path");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Failed to export"),
                "Should report error: " + errorOutput);
    }

    @Test
    @DisplayName("Should fail with unrecognizable file extension")
    void testUnrecognizableFileExtension() {
        Path outputFile = tempDir.resolve("config.unknown");

        int exitCode = cli.execute("export", "--file", outputFile.toString());
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(1, realExitCode, "Should fail with unrecognizable extension");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot determine format"),
                "Should report format detection error: " + errorOutput);
    }

    // ======================== Output Message Tests ========================

    @Test
    @DisplayName("Should show success message with file path")
    void testSuccessMessageIncludesFilePath() throws Exception {
        Path outputFile = tempDir.resolve("database.json");

        cli.execute("export", "--file", outputFile.toString());

        String output = outContent.toString();
        assertTrue(output.contains("Database configuration exported successfully"),
                "Should show success message: " + output);
        assertTrue(output.contains(outputFile.toString()),
                "Should show file path: " + output);
    }

    @Test
    @DisplayName("Should show detected format in output")
    void testOutputShowsDetectedFormat() throws Exception {
        Path outputFile = tempDir.resolve("database.properties");

        cli.execute("export", "--file", outputFile.toString());

        String output = outContent.toString();
        assertTrue(output.contains("Format:"),
                "Should show format label: " + output);
        assertTrue(output.contains("auto-detected"),
                "Should indicate auto-detection: " + output);
    }

    @Test
    @DisplayName("Should remind about masking in success message")
    void testMaskingReminderInOutput() throws Exception {
        Path outputFile = tempDir.resolve("database.json");

        cli.execute("export", "--file", outputFile.toString());

        String output = outContent.toString();
        assertTrue(output.contains("Passwords are masked"),
                "Should mention password masking: " + output);
    }

    @Test
    @DisplayName("Should not mention masking when --no-mask is used")
    void testNoMaskingReminderWhenDisabled() throws Exception {
        Path outputFile = tempDir.resolve("database.json");

        cli.execute("export", "--file", outputFile.toString(), "--no-mask");

        String output = outContent.toString();
        assertFalse(output.contains("Passwords are masked"),
                "Should NOT mention masking when --no-mask is used: " + output);
    }

    // ======================== Multiple Exports Tests ========================

    @Test
    @DisplayName("Should support exporting same config in multiple formats")
    void testMultipleFormatExports() throws Exception {
        configManager.setCliArguments("postgresql", null, "localhost", 5432,
                "testdb", "testuser", "testpass");

        Path propFile = tempDir.resolve("config.properties");
        cli.execute("export", "--file", propFile.toString());
        assertTrue(Files.exists(propFile), "Properties file should be created");

        outContent.reset();
        errContent.reset();

        Path jsonFile = tempDir.resolve("config.json");
        cli.execute("export", "--file", jsonFile.toString());
        assertTrue(Files.exists(jsonFile), "JSON file should be created");

        outContent.reset();
        errContent.reset();

        Path envFile = tempDir.resolve("config.env");
        cli.execute("export", "--file", envFile.toString());
        assertTrue(Files.exists(envFile), "ENV file should be created");

        assertTrue(Files.size(propFile) > 0, "Properties file should have content");
        assertTrue(Files.size(jsonFile) > 0, "JSON file should have content");
        assertTrue(Files.size(envFile) > 0, "ENV file should have content");
    }

    @Test
    @DisplayName("Should overwrite existing files")
    void testOverwriteExistingFile() throws Exception {
        Path outputFile = tempDir.resolve("database.properties");

        cli.execute("export", "--file", outputFile.toString());
        String firstContent = Files.readString(outputFile);

        configManager.setCliArguments("mysql", null, "newhost", 3306,
                "newdb", "newuser", "newpass");

        outContent.reset();
        errContent.reset();

        cli.execute("export", "--file", outputFile.toString());
        String secondContent = Files.readString(outputFile);

        assertTrue(Files.exists(outputFile), "File should still exist");
        assertNotEquals(firstContent, secondContent,
                "File content should have changed after reconfiguration");
        assertTrue(secondContent.contains("mysql"),
                "Should contain new MySQL configuration: " + secondContent);
    }
}
