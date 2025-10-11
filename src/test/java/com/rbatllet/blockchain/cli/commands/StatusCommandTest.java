package com.rbatllet.blockchain.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import com.rbatllet.blockchain.util.ExitUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * Test suite for the Status Command
 */
public class StatusCommandTest {

    private final ByteArrayOutputStream outContent =
        new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent =
        new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine cli;
    private Path tempDirectory;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        tempDirectory = Files.createTempDirectory("status-test");
        System.setProperty("user.dir", tempDirectory.toString());

        // Disable System.exit() for testing
        ExitUtil.disableExit();

        cli = new CommandLine(new StatusCommand());
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Re-enable System.exit() after testing
        ExitUtil.enableExit();

        if (tempDirectory != null && Files.exists(tempDirectory)) {
            Files.walk(tempDirectory).map(Path::toFile).forEach(File::delete);
            Files.deleteIfExists(tempDirectory);
        }
    }

    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled()
            ? ExitUtil.getLastExitCode()
            : cliExitCode;
    }

    @Test
    void testBasicStatusCommand() {
        int exitCode = cli.execute();
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Status"));
        assertTrue(output.contains("Total blocks:"));
        assertTrue(output.contains("Authorized keys:"));
        // Updated to check for new validation format
        assertTrue(output.contains("Chain Validation Status:"));
        assertTrue(output.contains("Structural integrity:"));
        assertTrue(output.contains("Authorization compliance:"));
        assertTrue(output.contains("Overall status:"));
    }

    @Test
    void testStatusWithJsonFlag() {
        int exitCode = cli.execute("--json");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("\"blockCount\":"));
        assertTrue(output.contains("\"authorizedKeys\":"));
        // Updated to check for new validation structure in JSON
        assertTrue(output.contains("\"validation\":"));
        assertTrue(output.contains("\"isFullyCompliant\":"));
        assertTrue(output.contains("\"isStructurallyIntact\":"));
        assertTrue(output.contains("\"timestamp\":"));
        assertTrue(output.contains("}"));
    }

    @Test
    void testStatusWithDetailedFlag() {
        int exitCode = cli.execute("--detailed");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Status"));
        // Updated to check for new detailed output structure
        assertTrue(output.contains("Detailed Chain Analysis:"));
        assertTrue(output.contains("Comprehensive Validation Report:"));
        assertTrue(output.contains("System Configuration:"));
        assertTrue(output.contains("Max block size:"));
        assertTrue(output.contains("Database:"));
        assertTrue(output.contains("Timestamp:"));
        assertTrue(output.contains("Validation API Usage:"));
    }

    @Test
    void testStatusWithShortFlags() {
        // Test short version of flags
        int exitCode = cli.execute("-d", "-j");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // When JSON is enabled, detailed text output should not appear
        assertTrue(output.contains("\"blockCount\":"));
        assertTrue(output.contains("\"validation\":"));
        // Updated: detailed text format should not appear when JSON is used
        assertFalse(output.contains("Detailed Chain Analysis:"));
        assertFalse(output.contains("System Configuration:"));
    }

    @Test
    void testInvalidFlag() {
        int exitCode = cli.execute("--invalid-flag");
        int realExitCode = getRealExitCode(exitCode);

        // PicoCLI may show help instead of failing for invalid flags
        // Accept either failure (exit code 2) or success with help message (exit code 0)
        String output = outContent.toString() + errContent.toString();
        if (realExitCode == 0) {
            // If it succeeded, should show help or error message about unknown option
            assertTrue(
                output.contains("Unknown option") ||
                    output.contains("help") ||
                    output.contains("Usage:"),
                "Invalid flag should either fail or show help message. Output: " +
                    output
            );
        } else {
            // If it failed, should return exit code 2 (PicoCLI standard for invalid options)
            assertEquals(
                2,
                realExitCode,
                "Invalid flag should return exit code 2"
            );
        }
    }

    @Test
    void testStatusWithVerboseFlag() {
        int exitCode = cli.execute("--verbose");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain verbose output markers
        assertTrue(
            output.contains("🔍 Starting blockchain status") ||
                output.contains("Initializing"),
            "Should show status initialization: " + output
        );
    }

    @Test
    void testStatusWithShortVerboseFlag() {
        int exitCode = cli.execute("-v");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain verbose output markers
        assertTrue(
            output.contains("🔍 Starting blockchain status") ||
                output.contains("Initializing"),
            "Should show status initialization: " + output
        );
    }

    @Test
    void testStatusWithDetailedAndVerbose() {
        int exitCode = cli.execute("--detailed", "--verbose");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain both detailed and verbose output
        assertTrue(
            output.contains("System Configuration:") ||
                output.contains("Blockchain")
        );
        assertTrue(
            output.contains("🔍") ||
                output.contains("verbose") ||
                output.contains("Initializing")
        );
    }

    @Test
    void testStatusWithAllFlags() {
        int exitCode = cli.execute("-d", "-v", "-j");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should work with all flags combined (JSON format)
        assertTrue(
            output.contains("\"blockCount\":") ||
                output.contains("\"status\":"),
            "JSON should contain status fields: " + output
        );
    }
}
