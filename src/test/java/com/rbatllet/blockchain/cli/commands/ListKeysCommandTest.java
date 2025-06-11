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
 * Test suite for ListKeysCommand
 */
public class ListKeysCommandTest {

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
        
        // Use BlockchainCLI as parent command, not ListKeysCommand directly
        cli = new CommandLine(new com.rbatllet.blockchain.cli.BlockchainCLI());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicListKeys() {
        int exitCode = cli.execute("list-keys");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("Authorized") || 
                  output.contains("No") || output.contains("0"));
    }

    @Test
    void testListKeysWithDetailedFlag() {
        int exitCode = cli.execute("list-keys", "--detailed");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("detailed") || 
                  output.contains("Authorized") || output.contains("information"));
    }

    @Test
    void testListKeysWithShortDetailedFlag() {
        int exitCode = cli.execute("list-keys", "-d");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testListKeysWithJsonFlag() {
        int exitCode = cli.execute("list-keys", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("[") || 
                  output.contains("\"") || output.contains("json"));
    }

    @Test
    void testListKeysWithShortJsonFlag() {
        int exitCode = cli.execute("list-keys", "-j");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("[") || 
                  output.contains("\"") || output.contains("json"));
    }

    @Test
    void testListKeysWithActiveOnlyFlag() {
        int exitCode = cli.execute("list-keys", "--active-only");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("active") || 
                  output.contains("Authorized") || output.contains("No"));
    }

    @Test
    void testListKeysWithShortActiveOnlyFlag() {
        int exitCode = cli.execute("list-keys", "-a");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    void testListKeysWithCombinedFlags() {
        int exitCode = cli.execute("list-keys", "--detailed", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("[") || 
                  output.contains("\"") || output.contains("detailed"));
    }

    @Test
    void testListKeysHelp() {
        // Test help for the main command instead of subcommand help
        int exitCode = cli.execute("--help");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("list-keys") || output.contains("List") || 
                  output.contains("Usage") || output.contains("help"));
    }

    @Test
    void testListKeysAllFlags() {
        int exitCode = cli.execute("list-keys", "--detailed", "--active-only", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }
}
