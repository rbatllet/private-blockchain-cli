package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically targeting the JSON output methods in RollbackCommand
 * to improve code coverage.
 */
public class RollbackCommandJsonOutputTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private RollbackCommand rollbackCommand;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));
        rollbackCommand = new RollbackCommand();
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should output success JSON format correctly")
    void shouldOutputSuccessJsonCorrectly() throws Exception {
        // Use reflection to access private method
        var method = RollbackCommand.class.getDeclaredMethod(
                "outputSuccessJson", long.class, long.class, long.class);
        method.setAccessible(true);
        
        // Call the method with test values
        method.invoke(rollbackCommand, 10L, 5L, 5L);
        
        String output = outContent.toString();
        
        // Verify JSON structure and content
        assertTrue(output.contains("\"success\": true"));
        assertTrue(output.contains("\"operation\": \"rollback\""));
        assertTrue(output.contains("\"blocksRemoved\": 5"));
        assertTrue(output.contains("\"previousBlockCount\": 10"));
        assertTrue(output.contains("\"currentBlockCount\": 5"));
        
        // Check timestamp format (should be ISO-8601)
        assertTrue(output.contains("\"timestamp\": \""));
        
        // Validate JSON structure
        String jsonPattern = "\\{\\s*" +
                "\"success\":\\s*true,\\s*" +
                "\"operation\":\\s*\"rollback\",\\s*" +
                "\"blocksRemoved\":\\s*5,\\s*" +
                "\"previousBlockCount\":\\s*10,\\s*" +
                "\"currentBlockCount\":\\s*5,\\s*" +
                "\"timestamp\":\\s*\".*\"\\s*" +
                "\\}";
        
        assertTrue(Pattern.compile(jsonPattern, Pattern.DOTALL).matcher(output).find(),
                "JSON output format is incorrect");
    }

    @Test
    @DisplayName("Should output error JSON format correctly")
    void shouldOutputErrorJsonCorrectly() throws Exception {
        // Use reflection to access private method
        var method = RollbackCommand.class.getDeclaredMethod(
                "outputErrorJson", String.class);
        method.setAccessible(true);
        
        // Call the method with test error message
        method.invoke(rollbackCommand, "Test error message");
        
        String output = outContent.toString();
        
        // Verify JSON structure and content
        assertTrue(output.contains("\"success\": false"));
        assertTrue(output.contains("\"error\": \"Test error message\""));
        
        // Check timestamp format (should be ISO-8601)
        assertTrue(output.contains("\"timestamp\": \""));
        
        // Validate JSON structure
        String jsonPattern = "\\{\\s*" +
                "\"success\":\\s*false,\\s*" +
                "\"error\":\\s*\"Test error message\",\\s*" +
                "\"timestamp\":\\s*\".*\"\\s*" +
                "\\}";
        
        assertTrue(Pattern.compile(jsonPattern, Pattern.DOTALL).matcher(output).find(),
                "JSON error output format is incorrect");
    }

    @Test
    @DisplayName("Should escape quotes in error messages")
    void shouldEscapeQuotesInErrorMessages() throws Exception {
        // Use reflection to access private method
        var method = RollbackCommand.class.getDeclaredMethod(
                "outputErrorJson", String.class);
        method.setAccessible(true);
        
        // Call the method with a message containing quotes
        method.invoke(rollbackCommand, "Error with \"quoted\" text");
        
        String output = outContent.toString();
        
        // Verify quotes are escaped
        assertTrue(output.contains("\"error\": \"Error with \\\"quoted\\\" text\""));
    }
    
    @Test
    @DisplayName("Should output dry run JSON format correctly")
    void shouldOutputDryRunJsonCorrectly() throws Exception {
        // Use reflection to access private method
        var method = RollbackCommand.class.getDeclaredMethod(
                "outputDryRunJson", long.class, long.class, long.class);
        method.setAccessible(true);
        
        // Call the method with test values
        method.invoke(rollbackCommand, 10L, 7L, 3L);
        
        String output = outContent.toString();
        
        // Verify JSON structure and content
        assertTrue(output.contains("\"dryRun\": true"));
        assertTrue(output.contains("\"operation\": \"rollback\""));
        assertTrue(output.contains("\"wouldRemove\": 3"));
        assertTrue(output.contains("\"currentBlockCount\": 10"));
        assertTrue(output.contains("\"finalBlockCount\": 7"));
        
        // Validate JSON structure
        String jsonPattern = "\\{\\s*" +
                "\"dryRun\":\\s*true,\\s*" +
                "\"operation\":\\s*\"rollback\",\\s*" +
                "\"wouldRemove\":\\s*3,\\s*" +
                "\"currentBlockCount\":\\s*10,\\s*" +
                "\"finalBlockCount\":\\s*7\\s*" +
                "\\}";
        
        assertTrue(Pattern.compile(jsonPattern, Pattern.DOTALL).matcher(output).find(),
                "JSON dry run output format is incorrect");
    }
}
