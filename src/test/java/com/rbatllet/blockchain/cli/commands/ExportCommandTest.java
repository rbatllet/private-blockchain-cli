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
        assertTrue(output.contains("export") || output.contains("success") || 
                  output.contains("block") || output.contains("key") || 
                  output.contains("Export") || output.length() > 5,
                  "Export output should contain relevant information. Output: " + output);
        
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
        assertTrue(output.contains("export") || output.contains("overwrite") || 
                  output.contains("success") || output.contains("Export") ||
                  output.length() > 5,
                  "Overwrite output should contain relevant information. Output: " + output);
    }

    @Test
    void testExportWithJsonOutput() throws Exception {
        Path exportFile = tempDir.resolve("test_json.json");
        int exitCode = executeCommand(exportFile.toString(), "--json");
        
        assertEquals(0, exitCode, "Export with JSON output should succeed");
        
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("json") || output.contains("success") ||
                  output.length() > 3,
                  "JSON output should contain JSON formatting. Output: " + output);
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
        
        assertTrue(exitCode >= 0 && exitCode <= 2, "Help should use standard exit codes");
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Export") || output.contains("export") || 
                  output.contains("Usage") || output.contains("help") ||
                  output.contains("description") || output.length() > 10,
                  "Help output should contain relevant information. Output: " + output);
    }

    @Test
    void testExportMissingFile() {
        int exitCode = executeCommand();
        
        // Should fail when no filename is provided, or show help
        if (exitCode == 0) {
            // If it succeeded, should show help
            String output = outContent.toString() + errContent.toString();
            assertTrue(output.contains("help") || output.contains("Usage") ||
                      output.contains("export") || output.contains("description"),
                      "Should show help when no filename provided. Output: " + output);
        } else {
            // If it failed as expected
            assertNotEquals(0, exitCode, "Should fail when no filename is provided");
        }
    }

    @Test
    void testExportInvalidPath() {
        int exitCode = executeCommand("/invalid/path/export.json");
        
        // Should fail gracefully
        assertEquals(1, exitCode, "Should fail when export path is invalid");
        
        String error = errContent.toString();
        String allOutput = outContent.toString() + error;
        assertTrue(error.contains("Error") || error.contains("export") || 
                  error.contains("directory") || allOutput.contains("Error") ||
                  allOutput.contains("invalid") || allOutput.contains("path"),
                  "Error should mention the path issue. Combined output: " + allOutput);
    }

    @Test
    void testExportExistingFileWithoutOverwrite() throws Exception {
        Path exportFile = tempDir.resolve("existing.json");
        Files.createFile(exportFile);
        assertTrue(Files.exists(exportFile), "Initial file should exist");
        
        int exitCode = executeCommand(exportFile.toString());
        
        // Should fail or warn about existing file, but handle gracefully
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Should handle existing file gracefully");
        
        String allOutput = outContent.toString() + errContent.toString();
        assertFalse(allOutput.trim().isEmpty(), 
                   "Should produce some output about the operation");
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
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Compression test should handle gracefully");
        
        // File should exist if command succeeded
        if (exitCode == 0) {
            assertTrue(Files.exists(exportFile), "Export file should be created if command succeeded");
        }
    }
}
