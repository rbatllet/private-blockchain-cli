package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically targeting uncovered methods in ManageKeysCommand
 * to improve complexity coverage metrics
 */
@DisplayName("ManageKeysCommand Coverage Tests")
public class ManageKeysCommandCoverageTest {

    @TempDir
    Path tempDir;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    private ManageKeysCommand manageKeysCommand;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        // Initialize ManageKeysCommand
        manageKeysCommand = new ManageKeysCommand();
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
    @DisplayName("Should output test JSON correctly")
    void shouldOutputTestJsonCorrectly() throws Exception {
        // Access the private outputTestJson method using reflection
        Method outputTestJsonMethod = ManageKeysCommand.class.getDeclaredMethod(
            "outputTestJson", String.class, boolean.class);
        outputTestJsonMethod.setAccessible(true);
        
        // Test with success=true
        outputTestJsonMethod.invoke(manageKeysCommand, "TestOwner", true);
        String successOutput = outContent.toString();
        assertTrue(successOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(successOutput.contains("\"passwordValid\": true"), "Should output passwordValid as true");
        
        // Clear output and test with success=false
        outContent.reset();
        outputTestJsonMethod.invoke(manageKeysCommand, "TestOwner", false);
        String failureOutput = outContent.toString();
        assertTrue(failureOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(failureOutput.contains("\"passwordValid\": false"), "Should output passwordValid as false");
    }
    
    // We'll skip this test as it requires user input for password
    // and focus on directly testing the JSON output method instead
    
    @Test
    @DisplayName("Should handle error when key doesn't exist")
    void shouldHandleOperationCancellationDuringDelete() {
        // Set up the command with a key that doesn't exist
        manageKeysCommand.deleteKey = "NonExistentKey";
        
        // Run the command
        manageKeysCommand.run();
        
        // Check error output
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("No private key stored") || 
                  errorOutput.contains("Error") || 
                  errorOutput.contains("❌"), 
                "Should show error message for non-existent key");
    }
    
    @Test
    @DisplayName("Should output delete JSON correctly")
    void shouldOutputDeleteJsonCorrectly() throws Exception {
        // Access the private outputDeleteJson method using reflection
        Method outputDeleteJsonMethod = ManageKeysCommand.class.getDeclaredMethod(
            "outputDeleteJson", String.class, boolean.class);
        outputDeleteJsonMethod.setAccessible(true);
        
        // Test with success=true
        outputDeleteJsonMethod.invoke(manageKeysCommand, "TestOwner", true);
        String successOutput = outContent.toString();
        assertTrue(successOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(successOutput.contains("\"deleted\": true"), "Should output deleted as true");
        
        // Clear output and test with success=false
        outContent.reset();
        outputDeleteJsonMethod.invoke(manageKeysCommand, "TestOwner", false);
        String failureOutput = outContent.toString();
        assertTrue(failureOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(failureOutput.contains("\"deleted\": false"), "Should output deleted as false");
    }
    
    @Test
    @DisplayName("Should output check JSON correctly")
    void shouldOutputCheckJsonCorrectly() throws Exception {
        // Access the private outputCheckJson method using reflection
        Method outputCheckJsonMethod = ManageKeysCommand.class.getDeclaredMethod(
            "outputCheckJson", String.class, boolean.class);
        outputCheckJsonMethod.setAccessible(true);
        
        // Test with hasKey=true
        outputCheckJsonMethod.invoke(manageKeysCommand, "TestOwner", true);
        String hasKeyOutput = outContent.toString();
        assertTrue(hasKeyOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(hasKeyOutput.contains("\"hasPrivateKey\": true"), "Should output hasPrivateKey as true");
        
        // Clear output and test with hasKey=false
        outContent.reset();
        outputCheckJsonMethod.invoke(manageKeysCommand, "TestOwner", false);
        String noKeyOutput = outContent.toString();
        assertTrue(noKeyOutput.contains("\"owner\": \"TestOwner\""), "Should output owner name");
        assertTrue(noKeyOutput.contains("\"hasPrivateKey\": false"), "Should output hasPrivateKey as false");
    }
}
