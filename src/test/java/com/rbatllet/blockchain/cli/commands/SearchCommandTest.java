package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

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
        
        cli = new CommandLine(new SearchCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicSearch() {
        int exitCode = cli.execute("Genesis");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("search") || 
                  output.contains("block") || output.contains("Genesis"));
    }

    @Test
    void testSearchWithContent() {
        int exitCode = cli.execute("--content", "transaction");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("search") || 
                  output.contains("block") || output.contains("0"));
    }

    @Test
    void testSearchWithShortContentFlag() {
        int exitCode = cli.execute("-c", "payment");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testSearchWithBlockNumber() {
        int exitCode = cli.execute("--block-number", "0");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("block") || 
                  output.contains("0") || output.contains("Genesis"));
    }

    @Test
    void testSearchWithShortBlockNumberFlag() {
        int exitCode = cli.execute("-n", "0");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testSearchWithHash() {
        // Using a dummy hash - should return no results
        int exitCode = cli.execute("--hash", "a1b2c3d4e5f6");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("No") || 
                  output.contains("0") || output.contains("hash"));
    }

    @Test
    void testSearchWithShortHashFlag() {
        int exitCode = cli.execute("-h", "dummy_hash");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testSearchWithDateRange() {
        int exitCode = cli.execute("--date-from", "2025-01-01", "--date-to", "2025-12-31");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("search") || 
                  output.contains("block") || output.contains("date"));
    }

    @Test
    void testSearchWithDateTimeRange() {
        int exitCode = cli.execute("--datetime-from", "2025-01-01 00:00", 
                                 "--datetime-to", "2025-12-31 23:59");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("search") || 
                  output.contains("block"));
    }

    @Test
    void testSearchWithLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "5");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("search") || 
                  output.contains("block"));
    }

    @Test
    void testSearchWithShortLimitFlag() {
        int exitCode = cli.execute("Genesis", "-l", "10");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testSearchWithDetailed() {
        int exitCode = cli.execute("Genesis", "--detailed");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("detailed") || 
                  output.contains("block") || output.contains("information"));
    }

    @Test
    void testSearchWithJson() {
        int exitCode = cli.execute("Genesis", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("[") || 
                  output.contains("\"") || output.contains("json"));
    }

    @Test
    void testSearchWithShortJsonFlag() {
        int exitCode = cli.execute("Genesis", "-j");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("[") || 
                  output.contains("\""));
    }

    @Test
    void testSearchHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2);
        String output = outContent.toString();
        assertTrue(output.contains("Search") || output.contains("search") || 
                  output.contains("Usage") || output.contains("help"));
    }

    @Test
    void testSearchNonExistentTerm() {
        int exitCode = cli.execute("NonExistentTerm12345");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("No") || 
                  output.contains("0") || output.contains("search"));
    }

    @Test
    void testSearchWithAllFlags() {
        int exitCode = cli.execute("Genesis", "--detailed", "--json", "--limit", "5");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testSearchWithInvalidDateFormat() {
        int exitCode = cli.execute("--date-from", "invalid-date", "--date-to", "2025-12-31");
        
        // Should handle invalid date gracefully
        assertTrue(exitCode == 0 || exitCode == 1);
        assertFalse(outContent.toString().isEmpty() || errContent.toString().isEmpty());
    }

    @Test
    void testSearchWithNegativeLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "-1");
        
        // Should handle negative limit gracefully
        assertTrue(exitCode == 0 || exitCode == 1);
    }

    @Test
    void testSearchWithZeroLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "0");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("found") || output.contains("0") || 
                  output.contains("No") || output.contains("search"));
    }

    @Test
    void testSearchWithVeryHighLimit() {
        int exitCode = cli.execute("Genesis", "--limit", "1000");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }
}
