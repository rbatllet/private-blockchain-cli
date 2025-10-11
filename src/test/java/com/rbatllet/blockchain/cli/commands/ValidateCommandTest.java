package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ValidateCommand
 */
public class ValidateCommandTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine cli;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable ExitUtil to prevent System.exit() calls during tests
        ExitUtil.disableExit();
        
        cli = new CommandLine(new ValidateCommand());
    }
    
    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable ExitUtil after tests
        ExitUtil.enableExit();
    }

    @Test
    void testBasicValidate() {
        int exitCode = cli.execute();
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("validation"),
                  "Should contain validation output: " + output);
    }

    @Test
    void testValidateWithDetailedFlag() {
        int exitCode = cli.execute("--detailed");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Validation Report"),
                  "Should show validation report: " + output);
    }

    @Test
    void testValidateWithShortDetailedFlag() {
        int exitCode = cli.execute("-d");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Validation Report"),
                  "Should produce validation output: " + output);
    }

    @Test
    void testValidateWithJsonFlag() {
        int exitCode = cli.execute("--json");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain JSON-like output
        assertTrue(output.contains("{"),
                  "Should contain JSON output: " + output);
    }

    @Test
    void testValidateWithShortJsonFlag() {
        int exitCode = cli.execute("-j");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"),
                  "Should contain JSON output: " + output);
    }

    @Test
    void testValidateWithQuickFlag() {
        int exitCode = cli.execute("--quick");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Validation Results"),
                  "Should produce quick validation output: " + output);
    }

    @Test
    void testValidateWithShortQuickFlag() {
        int exitCode = cli.execute("-q");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Validation Results"),
                  "Should produce quick validation output: " + output);
    }

    @Test
    void testValidateWithCombinedFlags() {
        int exitCode = cli.execute("--detailed", "--json");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"),
                  "Should contain JSON output: " + output);
    }

    @Test
    void testValidateHelp() {
        // Just verify that help doesn't crash
        try {
            int exitCode = cli.execute("--help");
            int realExitCode = getRealExitCode(exitCode);
            // Help should succeed
            assertEquals(0, realExitCode, "Help returns usage information with exit code 0");
            
            // Verify some output was produced
            String output = outContent.toString() + errContent.toString();
            assertTrue(output.contains("Usage: validate"),
                      "Should show usage text: " + output);
        } catch (Exception e) {
            // Help commands can behave differently, so just ensure no crash
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testValidateWithVerboseFlag() {
        int exitCode = cli.execute("--verbose");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain verbose validation output
        assertTrue(output.contains("🔍"),
                  "Should contain verbose markers: " + output);
    }

    @Test
    void testValidateWithShortVerboseFlag() {
        int exitCode = cli.execute("-v");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain verbose validation output
        assertTrue(output.contains("🔍"),
                  "Should contain verbose markers: " + output);
    }

    @Test
    void testValidateWithDetailedAndVerbose() {
        int exitCode = cli.execute("--detailed", "--verbose");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should contain both detailed and verbose output
        assertTrue(output.contains("Validation Report"),
                  "Should produce detailed and verbose output: " + output);
        assertTrue(output.contains("🔍"),
                  "Should contain verbose markers: " + output);
    }

    @Test
    void testValidateWithAllFlags() {
        int exitCode = cli.execute("-d", "-v", "-j");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        // Should work with all flags combined (detailed + verbose + json)
        assertTrue(output.contains("{"),
                  "Should contain JSON output with all flags: " + output);
    }
}
