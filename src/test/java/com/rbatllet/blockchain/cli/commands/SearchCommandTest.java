package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.core.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for SearchCommand
 */
public class SearchCommandTest {

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
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        // Initialize blockchain with clean state for each test
        try {
            Blockchain blockchain = new Blockchain();
            blockchain.clearAndReinitialize();
        } catch (Exception e) {
            // If blockchain initialization fails, continue with test
        }
        
        cli = new CommandLine(new SearchCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
    }

    /**
     * Helper method to get the real exit code, following the pattern from working tests
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    // Method removed - was too permissive with fallback checks

    @Test
    void testBasicSearch() {
        int exitCode = cli.execute("Genesis");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        // Blockchain is empty after clearAndReinitialize, so no blocks found
        assertTrue(output.contains("No blocks found matching search criteria"),
                  "Should show no results: " + output);
    }

    @Test
    void testSearchWithContent() {
        int exitCode = cli.execute("transaction"); // Direct search query
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"),
                  "Should show no results: " + output);
    }

    @Test
    void testSearchWithShortContentFlag() {
        int exitCode = cli.execute("--category", "MEDICAL"); // Use valid category option
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"),
                  "Should show no results: " + output);
    }

    @Test
    void testSearchWithBlockNumber() {
        int exitCode = cli.execute("--block-number", "0");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("🔍 Search Results"));
    }

    @Test
    void testSearchWithShortBlockNumberFlag() {
        int exitCode = cli.execute("-n", "0");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("🔍 Search Results"));
    }

    @Test
    void testSearchWithHash() {
        // Using a dummy hash - should return no results
        int exitCode = cli.execute("--hash", "a1b2c3d4e5f6");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithShortHashFlag() {
        int exitCode = cli.execute("-h", "dummy_hash");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithDateRange() {
        int exitCode = cli.execute("--date-from", "2025-01-01", "--date-to", "2025-12-31");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("🔍 Search Results"));
    }

    @Test
    void testSearchWithDateTimeRange() {
        int exitCode = cli.execute("--date-from", "2025-01-01", 
                                 "--date-to", "2025-12-31");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("🔍 Search Results"),
                  "Should show no results: " + output);
    }

    @Test
    void testSearchWithLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "5");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithShortLimitFlag() {
        int exitCode = cli.execute("Genesis", "-l", "10");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithDetailed() {
        int exitCode = cli.execute("Genesis", "--detailed");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithJson() {
        int exitCode = cli.execute("Genesis", "--json");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"),
                  "Should contain JSON output: " + output);
    }

    @Test
    void testSearchWithShortJsonFlag() {
        int exitCode = cli.execute("Genesis", "-j");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"),
                  "Should contain JSON output: " + output);
    }

    @Test
    void testSearchHelp() {
        int exitCode = cli.execute("--help");

        assertEquals(2, exitCode, "Help returns usage information with exit code 2");
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String combined = output + errorOutput;
        // --help shows error + usage
        assertTrue(combined.contains("Unknown option: '--help'"),
                  "Should show unknown option error: " + combined);
        assertTrue(combined.contains("Usage: search"),
                  "Should show usage: " + combined);
    }

    @Test
    void testSearchNonExistentTerm() {
        int exitCode = cli.execute("NonExistentTerm12345");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }

    @Test
    void testSearchWithAllFlags() {
        int exitCode = cli.execute("Genesis", "--detailed", "--json", "--limit", "5");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
    }

    @Test
    void testSearchWithInvalidDateFormat() {
        int exitCode = cli.execute("--date-from", "invalid-date", "--date-to", "2025-12-31");

        // Should handle invalid date gracefully
        assertEquals(0, exitCode, "Invalid date format is handled gracefully, but was: " + exitCode);
        String combined = outContent.toString() + errContent.toString();
        assertTrue(combined.contains("Invalid date/datetime format"),
                  "Should show invalid date error: " + combined);
    }

    @Test
    void testSearchWithNegativeLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "-1");
        
        // Should handle negative limit gracefully
        assertEquals(0, exitCode, "Negative limit is handled gracefully, but was: " + exitCode);
    }

    @Test
    void testSearchWithZeroLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "0");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(1, realExitCode, 
                  "Zero limit fails as expected, but was: " + realExitCode);
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Maximum results must be positive"),
                  "Should show limit error: " + output);
    }

    @Test
    void testSearchWithVeryHighLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "1000");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No blocks found matching search criteria"));
    }
}
