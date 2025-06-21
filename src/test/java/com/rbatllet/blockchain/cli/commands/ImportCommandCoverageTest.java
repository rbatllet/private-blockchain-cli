package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImportCommand focusing on improving code coverage
 * especially for complex conditional paths
 */
@DisplayName("ImportCommand Coverage Tests")
public class ImportCommandCoverageTest {

    @TempDir
    Path tempDir;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    private ImportCommand importCommand;
    private Path validImportFile;
    private Path invalidImportFile;
    
    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        // Create test files
        validImportFile = tempDir.resolve("valid_import.json");
        String validContent = "{ \"valid\": \"blockchain data\" }";
        Files.write(validImportFile, validContent.getBytes());
        
        invalidImportFile = tempDir.resolve("invalid_import.json");
        String invalidContent = "{ invalid json }";
        Files.write(invalidImportFile, invalidContent.getBytes());
        
        // Initialize ImportCommand
        importCommand = new ImportCommand();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Clear output buffers
        outContent.reset();
        errContent.reset();
        
        // Re-enable ExitUtil after tests
        ExitUtil.enableExit();
    }
    
    @Test
    @DisplayName("Should handle dry run with JSON output")
    void shouldHandleDryRunWithJsonOutput() {
        // Set up the command
        importCommand.inputFile = validImportFile.toString();
        importCommand.dryRun = true;
        importCommand.json = true;
        
        // Run the command
        importCommand.run();
        
        // Verify JSON output
        String output = outContent.toString();
        assertTrue(output.contains("\"success\": true"), "Should output JSON with success=true");
        assertTrue(output.contains("\"importFile\":"), "Should include import file in JSON");
    }
    
    @Test
    @DisplayName("Should handle file not readable")
    void shouldHandleFileNotReadable() throws Exception {
        // Create a file that exists but is not readable
        Path nonReadableFile = tempDir.resolve("non_readable.json");
        Files.write(nonReadableFile, "test".getBytes());
        File file = nonReadableFile.toFile();
        file.setReadable(false);
        
        // Set up the command
        importCommand.inputFile = nonReadableFile.toString();
        
        // Run the command
        importCommand.run();
        
        // Verify error message
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot read import file"), 
                "Should show cannot read file error");
        
        // Verify exit code
        assertEquals(1, ExitUtil.getLastExitCode(), "Should exit with code 1");
    }
    
    @Test
    @DisplayName("Should handle non-existent file")
    void shouldHandleNonExistentFile() {
        // Set up the command with non-existent file
        Path nonExistentFile = tempDir.resolve("does_not_exist.json");
        importCommand.inputFile = nonExistentFile.toString();
        
        // Run the command
        importCommand.run();
        
        // Verify error message
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Import file does not exist"), 
                "Should show file does not exist error");
        
        // Verify exit code
        assertEquals(1, ExitUtil.getLastExitCode(), "Should exit with code 1");
    }
    
    @Test
    @DisplayName("Should format file sizes correctly")
    void shouldFormatFileSizesCorrectly() {
        // Test private method using reflection
        try {
            java.lang.reflect.Method formatMethod = ImportCommand.class.getDeclaredMethod("formatFileSize", long.class);
            formatMethod.setAccessible(true);
            
            // Test different file sizes
            assertEquals("500 bytes", formatMethod.invoke(importCommand, 500L), "Should format bytes correctly");
            assertEquals("1.5 KB", formatMethod.invoke(importCommand, 1536L), "Should format KB correctly");
            assertEquals("1.5 MB", formatMethod.invoke(importCommand, 1572864L), "Should format MB correctly");
            assertEquals("1.5 GB", formatMethod.invoke(importCommand, 1610612736L), "Should format GB correctly");
        } catch (Exception e) {
            fail("Failed to test formatFileSize method: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should output JSON format correctly")
    void shouldOutputJsonFormatCorrectly() {
        // Test private method using reflection with updated signature
        try {
            // Create a mock ChainValidationResult
            com.rbatllet.blockchain.validation.ChainValidationResult validationResult = 
                org.mockito.Mockito.mock(com.rbatllet.blockchain.validation.ChainValidationResult.class);
            org.mockito.Mockito.when(validationResult.isFullyCompliant()).thenReturn(true);
            org.mockito.Mockito.when(validationResult.isStructurallyIntact()).thenReturn(true);
            org.mockito.Mockito.when(validationResult.getRevokedBlocks()).thenReturn(0);
            org.mockito.Mockito.when(validationResult.getInvalidBlocks()).thenReturn(0);
            org.mockito.Mockito.when(validationResult.getSummary()).thenReturn("Chain is fully valid");
            
            java.lang.reflect.Method jsonMethod = ImportCommand.class.getDeclaredMethod(
                "outputJson", boolean.class, String.class, long.class, int.class, 
                long.class, int.class, com.rbatllet.blockchain.validation.ChainValidationResult.class, boolean.class);
            jsonMethod.setAccessible(true);
            
            // Call the method with test values
            jsonMethod.invoke(importCommand, true, "test.json", 5L, 2, 10L, 3, validationResult, false);
            
            // Verify JSON output format
            String output = outContent.toString();
            assertTrue(output.contains("\"success\": true"), "Should include success status");
            assertTrue(output.contains("\"importFile\": \"test.json\""), "Should include file name");
            assertTrue(output.contains("\"previousBlocks\": 5"), "Should include previous blocks count");
            assertTrue(output.contains("\"previousKeys\": 2"), "Should include previous keys count");
            assertTrue(output.contains("\"newBlocks\": 10"), "Should include new blocks count");
            assertTrue(output.contains("\"newKeys\": 3"), "Should include new keys count");
            assertTrue(output.contains("\"validation\""), "Should include validation object");
            assertTrue(output.contains("\"isFullyCompliant\": true"), "Should include isFullyCompliant status");
            assertTrue(output.contains("\"isDryRun\": false"), "Should include isDryRun status");
            assertTrue(output.contains("\"timestamp\""), "Should include timestamp");
        } catch (Exception e) {
            fail("Failed to test outputJson method: " + e.getMessage());
        }
    }
}
