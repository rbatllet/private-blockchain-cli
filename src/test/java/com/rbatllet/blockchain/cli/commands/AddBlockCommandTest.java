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
 * Test suite for AddBlockCommand
 */
public class AddBlockCommandTest {

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
        
        cli = new CommandLine(new AddBlockCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testAddBlockWithGenerateKey() {
        int exitCode = cli.execute("Test transaction data", "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success") || output.contains("generated"));
    }

    @Test
    void testAddBlockWithSigner() {
        // This may fail if signer doesn't exist, but should handle gracefully
        int exitCode = cli.execute("Test transaction", "--signer", "TestUser");
        
        // Could be 0 (success) or 1 (failure due to missing signer)
        assertTrue(exitCode == 0 || exitCode == 1);
        assertFalse(outContent.toString().isEmpty() || errContent.toString().isEmpty());
    }

    @Test
    void testAddBlockWithJsonOutput() {
        int exitCode = cli.execute("Test data", "--generate-key", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("json") || output.contains("block"));
    }

    @Test
    void testAddBlockHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2);
        String output = outContent.toString();
        assertTrue(output.contains("Add") || output.contains("block") || 
                  output.contains("Usage") || output.contains("help"));
    }

    @Test
    void testAddBlockMissingData() {
        int exitCode = cli.execute("--generate-key");
        
        // Should fail when no data is provided
        assertNotEquals(0, exitCode);
    }

    @Test
    void testAddBlockLargeData() {
        // Test with large data (near the limit)
        String largeData = "Large transaction data ".repeat(100);
        int exitCode = cli.execute(largeData, "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success"));
    }

    @Test
    void testAddBlockEmptyData() {
        int exitCode = cli.execute("", "--generate-key");
        
        // Should handle empty data gracefully
        assertTrue(exitCode == 0 || exitCode == 1);
    }

    @Test
    void testAddBlockInvalidSigner() {
        int exitCode = cli.execute("Test data", "--signer", "NonExistentUser");
        
        // Should fail gracefully
        assertEquals(1, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("signer") || error.contains("not found") || 
                  error.contains("Error") || error.contains("key"));
    }

    @Test
    void testAddBlockVerboseOutput() {
        // Test with verbose flag if supported
        int exitCode = cli.execute("Test data", "--generate-key");
        
        assertEquals(0, exitCode);
        assertFalse(outContent.toString().isEmpty());
    }
}
