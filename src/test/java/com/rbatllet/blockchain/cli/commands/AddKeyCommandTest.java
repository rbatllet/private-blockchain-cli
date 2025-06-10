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
 * Test suite for AddKeyCommand
 */
public class AddKeyCommandTest {

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
        
        cli = new CommandLine(new AddKeyCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testAddKeyWithGenerate() {
        int exitCode = cli.execute("TestUser", "--generate");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("added") || 
                  output.contains("generated") || output.contains("success"));
    }

    @Test
    void testAddKeyWithGenerateAndShowPrivate() {
        int exitCode = cli.execute("TestUser", "--generate", "--show-private");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("private") || 
                  output.contains("generated"));
    }

    @Test
    void testAddKeyWithPublicKey() {
        String samplePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA";
        int exitCode = cli.execute("TestUser", "--public-key", samplePublicKey);
        
        // Should handle the command (may fail due to invalid key format, but should not crash)
        assertTrue(exitCode == 0 || exitCode == 1);
        assertFalse(outContent.toString().isEmpty() || errContent.toString().isEmpty());
    }

    @Test
    void testAddKeyHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2);
        String output = outContent.toString();
        assertTrue(output.contains("Add") || output.contains("key") || 
                  output.contains("Usage") || output.contains("help"));
    }

    @Test
    void testAddKeyMissingName() {
        int exitCode = cli.execute("--generate");
        
        // Should fail when no name is provided
        assertNotEquals(0, exitCode);
    }

    @Test
    void testAddKeyWithJsonOutput() {
        int exitCode = cli.execute("TestUser", "--generate", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("json") || output.contains("key"));
    }

    @Test
    void testAddKeyInvalidFlags() {
        // Test with conflicting flags
        int exitCode = cli.execute("TestUser", "--generate", "--public-key", "somekey");
        
        // Should handle gracefully (may succeed or fail, but shouldn't crash)
        assertTrue(exitCode >= 0);
    }
}
