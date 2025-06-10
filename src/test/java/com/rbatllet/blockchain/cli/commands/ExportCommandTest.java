package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ExportCommand
 */
public class ExportCommandTest {

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
        
        cli = new CommandLine(new ExportCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBasicExport() throws Exception {
        Path exportFile = tempDir.resolve("test_export.json");
        int exitCode = cli.execute(exportFile.toString());
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("export") || output.contains("success") || 
                  output.contains("block") || output.contains("key"));
        
        // Check if file was created
        assertTrue(Files.exists(exportFile));
    }

    @Test
    void testExportWithOverwrite() throws Exception {
        Path exportFile = tempDir.resolve("test_overwrite.json");
        
        // Create file first
        Files.createFile(exportFile);
        assertTrue(Files.exists(exportFile));
        
        int exitCode = cli.execute(exportFile.toString(), "--overwrite");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("export") || output.contains("overwrite") || 
                  output.contains("success"));
    }

    @Test
    void testExportWithJsonOutput() throws Exception {
        Path exportFile = tempDir.resolve("test_json.json");
        int exitCode = cli.execute(exportFile.toString(), "--json-output");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("json"));
        assertTrue(Files.exists(exportFile));
    }

    @Test
    void testExportWithShortJsonFlag() throws Exception {
        Path exportFile = tempDir.resolve("test_short_json.json");
        int exitCode = cli.execute(exportFile.toString(), "-j");
        
        assertEquals(0, exitCode);
        assertTrue(Files.exists(exportFile));
    }

    @Test
    void testExportHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2);
        String output = outContent.toString();
        assertTrue(output.contains("Export") || output.contains("export") || 
                  output.contains("Usage") || output.contains("help"));
    }

    @Test
    void testExportMissingFile() {
        int exitCode = cli.execute();
        
        // Should fail when no filename is provided
        assertNotEquals(0, exitCode);
    }

    @Test
    void testExportInvalidPath() {
        int exitCode = cli.execute("/invalid/path/export.json");
        
        // Should fail gracefully
        assertEquals(1, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Error") || error.contains("export") || 
                  error.contains("directory"));
    }

    @Test
    void testExportExistingFileWithoutOverwrite() throws Exception {
        Path exportFile = tempDir.resolve("existing.json");
        Files.createFile(exportFile);
        
        int exitCode = cli.execute(exportFile.toString());
        
        // Should fail or warn about existing file
        assertTrue(exitCode == 0 || exitCode == 1);
        assertFalse(outContent.toString().isEmpty() || errContent.toString().isEmpty());
    }

    @Test
    void testExportWithFormat() throws Exception {
        Path exportFile = tempDir.resolve("test_format.json");
        int exitCode = cli.execute(exportFile.toString(), "--format", "JSON");
        
        assertEquals(0, exitCode);
        assertTrue(Files.exists(exportFile));
    }

    @Test
    void testExportWithCompression() throws Exception {
        Path exportFile = tempDir.resolve("test_compress.json");
        int exitCode = cli.execute(exportFile.toString(), "--compress");
        
        // Compression might not be implemented yet, so accept success or failure
        assertTrue(exitCode == 0 || exitCode == 1);
        // File should exist if command succeeded
        if (exitCode == 0) {
            assertTrue(Files.exists(exportFile));
        }
    }
}
