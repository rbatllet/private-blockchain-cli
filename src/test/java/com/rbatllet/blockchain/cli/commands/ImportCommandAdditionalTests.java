package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for ImportCommand focusing on improving code coverage
 * especially for complex conditional paths
 */
@DisplayName("ImportCommand Additional Coverage Tests")
public class ImportCommandAdditionalTests {

    @TempDir
    Path tempDir;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    private ImportCommand importCommand;
    
    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
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
    @DisplayName("Should handle empty input file path")
    void shouldHandleEmptyInputFilePath() {
        // Set up the command with empty file path
        importCommand.inputFile = "";
        
        // Run the command
        importCommand.run();
        
        // Verify error message
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Input file path cannot be empty"), 
                "Should show empty path error");
        
        // Verify exit code
        assertEquals(1, ExitUtil.getLastExitCode(), "Should exit with code 1");
    }
    
    @Test
    @DisplayName("Should handle null input file path")
    void shouldHandleNullInputFilePath() {
        // Set up the command with null file path
        importCommand.inputFile = null;
        
        // Run the command
        importCommand.run();
        
        // Verify error message
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Input file path cannot be empty"), 
                "Should show empty path error");
        
        // Verify exit code
        assertEquals(1, ExitUtil.getLastExitCode(), "Should exit with code 1");
    }
    
    @Test
    @DisplayName("Should handle directory as input file")
    void shouldHandleDirectoryAsInputFile() {
        // Set up the command with directory path instead of file
        importCommand.inputFile = tempDir.toString();
        
        // Run the command
        importCommand.run();
        
        // Verify error message
        String errorOutput = errContent.toString();
        // Check for any error message related to file validation
        assertTrue(errorOutput.contains("❌"), 
                "Should show an error symbol");
        
        // Verify exit code
        assertEquals(1, ExitUtil.getLastExitCode(), "Should exit with code 1");
    }
}
