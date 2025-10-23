package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.validation.ChainValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ImportCommand's private methods
 * This class focuses on testing the formatFileSize and outputJson methods
 * Updated to work with the new validation API
 */
@DisplayName("ImportCommand Methods Tests")
public class ImportCommandMethodsTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ImportCommand importCommand;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        importCommand = new ImportCommand();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @ParameterizedTest
    @CsvSource({
        "0, 0 bytes",
        "1, 1 bytes",
        "500, 500 bytes",
        "1023, 1023 bytes",
        "1024, 1.0 KB",
        "1500, 1.5 KB",
        "1048576, 1.0 MB",
        "2097152, 2.0 MB",
        "1073741824, 1.0 GB"
    })
    @DisplayName("formatFileSize should correctly format file sizes")
    void formatFileSizeShouldCorrectlyFormatFileSizes(long bytes, String expected) throws Exception {
        // Get the private method using reflection
        Method formatFileSizeMethod = ImportCommand.class.getDeclaredMethod("formatFileSize", long.class);
        formatFileSizeMethod.setAccessible(true);
        
        // Invoke the method
        String result = (String) formatFileSizeMethod.invoke(importCommand, bytes);
        
        // Verify the result
        assertEquals(expected, result, "File size should be formatted correctly");
    }
    
    @Test
    @DisplayName("outputJson should generate correct JSON structure for success case with validation")
    void outputJsonShouldGenerateCorrectJsonStructureForSuccessCase() throws Exception {
        // Create a mock ChainValidationResult for successful validation
        ChainValidationResult validationResult = mock(ChainValidationResult.class);
        when(validationResult.isFullyCompliant()).thenReturn(true);
        when(validationResult.isStructurallyIntact()).thenReturn(true);
        when(validationResult.getRevokedBlocks()).thenReturn(0L);
        when(validationResult.getInvalidBlocks()).thenReturn(0L);
        when(validationResult.getSummary()).thenReturn("Chain is fully valid");
        
        // Get the private method using reflection with new signature
        Method outputJsonMethod = ImportCommand.class.getDeclaredMethod(
            "outputJson", boolean.class, String.class, long.class, int.class, 
            long.class, int.class, ChainValidationResult.class, boolean.class);
        outputJsonMethod.setAccessible(true);
        
        // Invoke the method with success parameters
        outputJsonMethod.invoke(importCommand, true, "test.json", 5L, 2, 10L, 3, validationResult, false);
        
        // Get the output
        String output = outContent.toString();
        
        // Verify JSON structure and values
        assertTrue(output.contains("\"success\": true"), "JSON should contain success: true");
        assertTrue(output.contains("\"importFile\": \"test.json\""), "JSON should contain the correct file path");
        assertTrue(output.contains("\"previousBlocks\": 5"), "JSON should contain the correct previous block count");
        assertTrue(output.contains("\"previousKeys\": 2"), "JSON should contain the correct previous key count");
        assertTrue(output.contains("\"newBlocks\": 10"), "JSON should contain the correct new block count");
        assertTrue(output.contains("\"newKeys\": 3"), "JSON should contain the correct new key count");
        assertTrue(output.contains("\"validation\""), "JSON should contain validation object");
        assertTrue(output.contains("\"isFullyCompliant\": true"), "JSON should contain isFullyCompliant: true");
        assertTrue(output.contains("\"isStructurallyIntact\": true"), "JSON should contain isStructurallyIntact: true");
        assertTrue(output.contains("\"revokedBlocks\": 0"), "JSON should contain revokedBlocks: 0");
        assertTrue(output.contains("\"invalidBlocks\": 0"), "JSON should contain invalidBlocks: 0");
        assertTrue(output.contains("\"summary\": \"Chain is fully valid\""), "JSON should contain validation summary");
        assertTrue(output.contains("\"isDryRun\": false"), "JSON should contain isDryRun: false");
        assertTrue(output.contains("\"timestamp\""), "JSON should contain a timestamp");
    }
    
    @Test
    @DisplayName("outputJson should generate correct JSON structure for failure case")
    void outputJsonShouldGenerateCorrectJsonStructureForFailureCase() throws Exception {
        // Clear previous output
        outContent.reset();
        
        // Get the private method using reflection with new signature
        Method outputJsonMethod = ImportCommand.class.getDeclaredMethod(
            "outputJson", boolean.class, String.class, long.class, int.class, 
            long.class, int.class, ChainValidationResult.class, boolean.class);
        outputJsonMethod.setAccessible(true);
        
        // Invoke the method with failure parameters (null validation result)
        outputJsonMethod.invoke(importCommand, false, "invalid.json", 5L, 2, 5L, 2, null, false);
        
        // Get the output
        String output = outContent.toString();
        
        // Verify JSON structure and values
        assertTrue(output.contains("\"success\": false"), "JSON should contain success: false");
        assertTrue(output.contains("\"importFile\": \"invalid.json\""), "JSON should contain the correct file path");
        assertTrue(output.contains("\"previousBlocks\": 5"), "JSON should contain the correct previous block count");
        assertTrue(output.contains("\"previousKeys\": 2"), "JSON should contain the correct previous key count");
        assertTrue(output.contains("\"newBlocks\": 5"), "JSON should contain the same block count (no change)");
        assertTrue(output.contains("\"newKeys\": 2"), "JSON should contain the same key count (no change)");
        assertTrue(output.contains("\"validation\": null"), "JSON should contain validation: null");
        assertTrue(output.contains("\"isDryRun\": false"), "JSON should contain isDryRun: false");
        assertTrue(output.contains("\"timestamp\""), "JSON should contain a timestamp");
    }
    
    @Test
    @DisplayName("outputJson should generate correct JSON structure for dry run")
    void outputJsonShouldGenerateCorrectJsonStructureForDryRun() throws Exception {
        // Clear previous output
        outContent.reset();
        
        // Get the private method using reflection with new signature
        Method outputJsonMethod = ImportCommand.class.getDeclaredMethod(
            "outputJson", boolean.class, String.class, long.class, int.class, 
            long.class, int.class, ChainValidationResult.class, boolean.class);
        outputJsonMethod.setAccessible(true);
        
        // Invoke the method with dry run parameters
        outputJsonMethod.invoke(importCommand, true, "test.json", 5L, 2, 0L, 0, null, true);
        
        // Get the output
        String output = outContent.toString();
        
        // Verify JSON structure and values
        assertTrue(output.contains("\"success\": true"), "JSON should contain success: true");
        assertTrue(output.contains("\"importFile\": \"test.json\""), "JSON should contain the correct file path");
        assertTrue(output.contains("\"previousBlocks\": 5"), "JSON should contain the correct previous block count");
        assertTrue(output.contains("\"previousKeys\": 2"), "JSON should contain the correct previous key count");
        assertTrue(output.contains("\"newBlocks\": 0"), "JSON should contain 0 for new blocks in dry run");
        assertTrue(output.contains("\"newKeys\": 0"), "JSON should contain 0 for new keys in dry run");
        assertTrue(output.contains("\"validation\": null"), "JSON should contain validation: null for dry run");
        assertTrue(output.contains("\"isDryRun\": true"), "JSON should contain isDryRun: true");
        assertTrue(output.contains("\"timestamp\""), "JSON should contain a timestamp");
    }
    
    @Test
    @DisplayName("outputJson should handle validation with revoked blocks")
    void outputJsonShouldHandleValidationWithRevokedBlocks() throws Exception {
        // Clear previous output
        outContent.reset();
        
        // Create a mock ChainValidationResult with revoked blocks
        ChainValidationResult validationResult = mock(ChainValidationResult.class);
        when(validationResult.isFullyCompliant()).thenReturn(false);
        when(validationResult.isStructurallyIntact()).thenReturn(true);
        when(validationResult.getRevokedBlocks()).thenReturn(2L);
        when(validationResult.getInvalidBlocks()).thenReturn(0L);
        when(validationResult.getSummary()).thenReturn("Chain is structurally intact but has authorization issues");
        
        // Get the private method using reflection with new signature
        Method outputJsonMethod = ImportCommand.class.getDeclaredMethod(
            "outputJson", boolean.class, String.class, long.class, int.class, 
            long.class, int.class, ChainValidationResult.class, boolean.class);
        outputJsonMethod.setAccessible(true);
        
        // Invoke the method with validation result containing revoked blocks
        outputJsonMethod.invoke(importCommand, true, "test.json", 5L, 2, 10L, 3, validationResult, false);
        
        // Get the output
        String output = outContent.toString();
        
        // Verify JSON structure and values
        assertTrue(output.contains("\"success\": true"), "JSON should contain success: true");
        assertTrue(output.contains("\"validation\""), "JSON should contain validation object");
        assertTrue(output.contains("\"isFullyCompliant\": false"), "JSON should contain isFullyCompliant: false");
        assertTrue(output.contains("\"isStructurallyIntact\": true"), "JSON should contain isStructurallyIntact: true");
        assertTrue(output.contains("\"revokedBlocks\": 2"), "JSON should contain revokedBlocks: 2");
        assertTrue(output.contains("\"invalidBlocks\": 0"), "JSON should contain invalidBlocks: 0");
        assertTrue(output.contains("\"summary\": \"Chain is structurally intact but has authorization issues\""), 
                   "JSON should contain correct validation summary");
    }
}
