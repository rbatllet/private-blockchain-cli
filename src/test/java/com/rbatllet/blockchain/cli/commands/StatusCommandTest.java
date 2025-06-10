package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Status Command
 */
public class StatusCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
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
        
        cli = new CommandLine(new StatusCommand());
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        if (tempDirectory != null && Files.exists(tempDirectory)) {
            Files.walk(tempDirectory)
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.deleteIfExists(tempDirectory);
        }
    }

    @Test
    void testBasicStatusCommand() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Status"));
        assertTrue(output.contains("Total blocks:"));
        assertTrue(output.contains("Authorized keys:"));
        assertTrue(output.contains("Chain integrity:"));
    }

    @Test
    void testStatusWithJsonFlag() {
        int exitCode = cli.execute("--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("\"blockCount\":"));
        assertTrue(output.contains("\"authorizedKeys\":"));
        assertTrue(output.contains("\"isValid\":"));
        assertTrue(output.contains("\"timestamp\":"));
        assertTrue(output.contains("}"));
    }

    @Test
    void testStatusWithDetailedFlag() {
        int exitCode = cli.execute("--detailed");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Blockchain Status"));
        assertTrue(output.contains("Configuration:"));
        assertTrue(output.contains("Max block size:"));
        assertTrue(output.contains("Database:"));
        assertTrue(output.contains("Timestamp:"));
    }

    @Test
    void testStatusWithShortFlags() {
        // Test short version of flags
        int exitCode = cli.execute("-d", "-j");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        // When JSON is enabled, detailed text output should not appear
        assertTrue(output.contains("\"blockCount\":"));
        assertFalse(output.contains("Configuration:"));
    }

    @Test
    void testInvalidFlag() {
        int exitCode = cli.execute("--invalid-flag");
        
        assertNotEquals(0, exitCode);
    }
}
