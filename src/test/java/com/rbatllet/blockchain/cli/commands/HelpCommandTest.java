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
 * Test suite for HelpCommand
 */
public class HelpCommandTest {

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
        
        cli = new CommandLine(new HelpCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicHelp() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Help") || output.contains("help") || 
                  output.contains("Usage") || output.contains("Commands"));
    }

    @Test
    void testHelpContent() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Should contain information about main commands
        assertTrue(output.contains("status") || output.contains("Status"));
        assertTrue(output.contains("validate") || output.contains("Validate"));
        assertTrue(output.contains("blockchain") || output.contains("Blockchain"));
    }

    @Test
    void testHelpHasAllCommands() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString().toLowerCase();
        
        // Check that help mentions the key commands
        boolean hasStatus = output.contains("status");
        boolean hasValidate = output.contains("validate");
        boolean hasAddKey = output.contains("add-key") || output.contains("key");
        boolean hasExport = output.contains("export");
        boolean hasImport = output.contains("import");
        boolean hasSearch = output.contains("search");
        
        // At least some commands should be mentioned
        int commandCount = 0;
        if (hasStatus) commandCount++;
        if (hasValidate) commandCount++;
        if (hasAddKey) commandCount++;
        if (hasExport) commandCount++;
        if (hasImport) commandCount++;
        if (hasSearch) commandCount++;
        
        assertTrue(commandCount >= 3, "Help should mention at least 3 commands");
    }

    @Test
    void testHelpWithFlag() {
        int exitCode = cli.execute("--help");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Help") || output.contains("help") || 
                  output.contains("Usage"));
    }

    @Test
    void testHelpFormat() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Help should be well-formatted and readable
        assertFalse(output.isEmpty());
        assertTrue(output.length() > 100, "Help should be substantial");
        
        // Should have some structure (headers, sections, etc.)
        assertTrue(output.contains(":") || output.contains("-") || 
                  output.contains("=") || output.contains("*"));
    }

    @Test
    void testHelpIsUserFriendly() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Should contain user-friendly elements
        assertTrue(output.contains("blockchain") || output.contains("Blockchain"));
        assertTrue(output.contains("command") || output.contains("Command"));
        
        // Should not contain technical errors or stack traces
        assertFalse(output.contains("Exception"));
        assertFalse(output.contains("Error"));
        assertFalse(output.contains("at java."));
    }

    @Test
    void testHelpExitCode() {
        int exitCode = cli.execute();
        
        // Help should always succeed
        assertEquals(0, exitCode);
    }

    @Test
    void testHelpWithInvalidArguments() {
        int exitCode = cli.execute("invalid", "arguments");
        
        // Should handle extra arguments gracefully
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testHelpContainsExamples() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString().toLowerCase();
        
        // Good help should contain examples or usage patterns
        boolean hasExamples = output.contains("example") || 
                             output.contains("usage") || 
                             output.contains("blockchain ") ||
                             output.contains("java -jar");
        
        assertTrue(hasExamples, "Help should contain usage examples");
    }

    @Test
    void testHelpNonEmpty() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        assertNotNull(output);
        assertFalse(output.trim().isEmpty());
        assertTrue(output.length() > 50, "Help should be comprehensive");
    }
}
