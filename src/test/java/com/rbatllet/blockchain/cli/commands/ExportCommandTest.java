package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;

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
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        cli = new CommandLine(new ExportCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable System.exit() after testing
        ExitUtil.enableExit();
    }
    
    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }
    
    /**
     * Execute command and return real exit code
     */
    private int executeCommand(String... args) {
        int exitCode = cli.execute(args);
        return getRealExitCode(exitCode);
    }

    @Test
    void testBasicExport() throws Exception {
        Path exportFile = tempDir.resolve("test_export.json");
        int exitCode = executeCommand(exportFile.toString());
        
        assertEquals(0, exitCode, "Basic export should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Blockchain exported successfully"),
                  "Should show export success: " + output);
        
        // Check if file was created
        assertTrue(Files.exists(exportFile), "Export file should be created");
        assertTrue(Files.size(exportFile) > 0, "Export file should not be empty");
    }

    @Test
    void testExportWithOverwrite() throws Exception {
        Path exportFile = tempDir.resolve("test_overwrite.json");
        
        // Create file first
        Files.createFile(exportFile);
        assertTrue(Files.exists(exportFile), "Initial file should exist");
        
        int exitCode = executeCommand(exportFile.toString(), "--overwrite");
        
        assertEquals(0, exitCode, "Export with overwrite should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Blockchain exported successfully"),
                  "Should show export success: " + output);
    }

    @Test
    void testExportWithJsonOutput() throws Exception {
        Path exportFile = tempDir.resolve("test_json.json");
        int exitCode = executeCommand(exportFile.toString(), "--json");
        
        assertEquals(0, exitCode, "Export with JSON output should succeed");

        String output = outContent.toString();
        // JSON output shows success in JSON format
        assertTrue(output.contains("\"success\": true"),
                  "Should show JSON success: " + output);
        assertTrue(Files.exists(exportFile), "Export file should be created");
    }

    @Test
    void testExportWithShortJsonFlag() throws Exception {
        Path exportFile = tempDir.resolve("test_short_json.json");
        int exitCode = executeCommand(exportFile.toString(), "-j");
        
        assertEquals(0, exitCode, "Export with short JSON flag should succeed");
        assertTrue(Files.exists(exportFile), "Export file should be created");
    }

    @Test
    void testExportHelp() {
        int exitCode = executeCommand("--help");
        
        assertEquals(0, exitCode, "Help returns usage information with exit code 0");

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Usage:"),
                  "Should show usage: " + output);
    }

    @Test
    void testExportMissingFile() {
        executeCommand();

        // PicoCLI shows parameter error (exit code varies by version)
        // Should show parameter error or usage information
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Missing required parameter"),
                  "Should show parameter error: " + output);
    }

    @Test
    void testExportInvalidPath() {
        int exitCode = executeCommand("/invalid/path/export.json");

        // Should fail gracefully
        assertEquals(1, exitCode, "Should fail when export path is invalid");

        String error = errContent.toString();
        String allOutput = outContent.toString() + error;
        assertTrue(allOutput.contains("Failed to export blockchain"),
                  "Should show export error: " + allOutput);
    }

    @Test
    void testExportExistingFileWithoutOverwrite() throws Exception {
        Path exportFile = tempDir.resolve("existing.json");
        Files.createFile(exportFile);
        assertTrue(Files.exists(exportFile), "Initial file should exist");
        
        int exitCode = executeCommand(exportFile.toString());
        
        // Should fail or warn about existing file, but handle gracefully
        assertEquals(1, exitCode, "Should warn about existing file, but was: " + exitCode);
        
        String allOutput = outContent.toString() + errContent.toString();
        assertTrue(allOutput.contains("File already exists"),
                  "Should show file exists error: " + allOutput);
    }

    @Test
    void testExportWithFormat() throws Exception {
        Path exportFile = tempDir.resolve("test_format.json");
        int exitCode = executeCommand(exportFile.toString(), "--format", "JSON");
        
        assertEquals(0, exitCode, "Export with format should succeed");
        assertTrue(Files.exists(exportFile), "Export file should be created");
    }

    @Test
    void testExportWithCompression() throws Exception {
        Path exportFile = tempDir.resolve("test_compress.json");
        int exitCode = executeCommand(exportFile.toString(), "--compress");
        
        // Compression might not be implemented yet, so accept success or failure
        assertEquals(0, exitCode, "Compression should succeed, but was: " + exitCode);
        
        // File should exist if command succeeded
        if (exitCode == 0) {
            assertTrue(Files.exists(exportFile), "Export file should be created if command succeeded");
        }
    }
}
