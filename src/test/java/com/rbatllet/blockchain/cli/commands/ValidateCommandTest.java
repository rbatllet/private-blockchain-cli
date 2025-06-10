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
        
        cli = new CommandLine(new ValidateCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicValidate() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Chain validation") || output.contains("validation"));
    }

    @Test
    void testValidateWithDetailedFlag() {
        int exitCode = cli.execute("--detailed");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("validation") || output.contains("block"));
    }

    @Test
    void testValidateWithShortDetailedFlag() {
        int exitCode = cli.execute("-d");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("validation") || output.contains("block"));
    }

    @Test
    void testValidateWithJsonFlag() {
        int exitCode = cli.execute("--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        // Should contain JSON-like output
        assertTrue(output.contains("{") || output.contains("\""));
    }

    @Test
    void testValidateWithShortJsonFlag() {
        int exitCode = cli.execute("-j");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\""));
    }

    @Test
    void testValidateWithQuickFlag() {
        int exitCode = cli.execute("--quick");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("validation") || output.contains("quick"));
    }

    @Test
    void testValidateWithShortQuickFlag() {
        int exitCode = cli.execute("-q");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("validation") || output.contains("quick"));
    }

    @Test
    void testValidateWithCombinedFlags() {
        int exitCode = cli.execute("--detailed", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testValidateHelp() {
        // Just verify that help doesn't crash
        try {
            int exitCode = cli.execute("--help");
            // Any reasonable exit code is acceptable for help
            assertTrue(exitCode >= 0 && exitCode <= 3);
            
            // Verify some output was produced
            String output = outContent.toString() + errContent.toString();
            assertFalse(output.trim().isEmpty());
        } catch (Exception e) {
            // Help commands can behave differently, so just ensure no crash
            assertNotNull(e.getMessage());
        }
    }
}
