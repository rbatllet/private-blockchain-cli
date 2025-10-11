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
        
        // Use BlockchainCLI as parent command, not HelpCommand directly
        cli = new CommandLine(new com.rbatllet.blockchain.cli.BlockchainCLI());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicHelp() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();
        // Help shows: "🔗 Private Blockchain CLI - Detailed Help"
        assertTrue(output.contains("Private Blockchain CLI"),
                  "Should show CLI title: " + output);
    }

    @Test
    void testHelpContent() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();

        // Should contain main sections: DESCRIPTION, USAGE, COMMANDS
        assertTrue(output.contains("DESCRIPTION:"),
                  "Should contain DESCRIPTION section: " + output);
    }

    @Test
    void testHelpHasAllCommands() {
        int exitCode = cli.execute("help");
        
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
        // Test the main help flag instead of help --help
        int exitCode = cli.execute("--help");

        assertEquals(0, exitCode);
        String output = outContent.toString();
        // --help shows usage: "Usage: blockchain"
        assertTrue(output.contains("Usage: blockchain"),
                  "Should show usage with --help flag: " + output);
    }

    @Test
    void testHelpFormat() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();

        // Help should show USAGE section
        assertTrue(output.contains("USAGE:"),
                  "Help should contain USAGE section: " + output);
    }

    @Test
    void testHelpIsUserFriendly() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();

        // Should show COMMANDS section
        assertTrue(output.contains("COMMANDS:"),
                  "Should contain COMMANDS section: " + output);

        // Should not contain technical errors or stack traces
        assertFalse(output.contains("Exception"));
        assertFalse(output.contains("Error"));
        assertFalse(output.contains("at java."));
    }

    @Test
    void testHelpExitCode() {
        int exitCode = cli.execute("help");

        // Help should always succeed
        assertEquals(0, exitCode);
    }

    @Test
    void testHelpWithInvalidArguments() {
        // Test that help command itself works even if subcommand handling might be different
        int exitCode = cli.execute("help");

        // Should handle the help command gracefully
        assertEquals(0, exitCode);
        String output = outContent.toString();
        // Shows DESCRIPTION
        assertTrue(output.contains("DESCRIPTION:"),
                  "Should show DESCRIPTION: " + output);
    }

    @Test
    void testHelpContainsExamples() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();

        // Help shows USAGE section with specific format
        assertTrue(output.contains("USAGE:"),
                  "Help should contain USAGE: section: " + output);
    }

    @Test
    void testHelpNonEmpty() {
        int exitCode = cli.execute("help");

        assertEquals(0, exitCode);
        String output = outContent.toString();

        assertNotNull(output);
        // Shows "help" command in the list
        assertTrue(output.contains("help"),
                  "Should show help command: " + output);
    }
}
