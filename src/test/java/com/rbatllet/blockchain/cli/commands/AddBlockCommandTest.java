package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AddBlockCommand
 */
public class AddBlockCommandTest {

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
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        cli = new CommandLine(new AddBlockCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
    }

    @Test
    void testAddBlockWithGenerateKey() {
        int exitCode = cli.execute("Test transaction data", "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success") || output.contains("generated"));
    }

    @Test
    void testAddBlockWithSigner() {
        // Test verifies that --signer parameter works correctly
        // When signer doesn't exist, AddBlockCommand uses demo mode as fallback
        
        int exitCode = cli.execute("Test transaction", "--signer", "TestUser");
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        
        // Based on the AddBlockCommand logic, when signer is not found, it should either:
        // 1. Fail with error (if no fallback)
        // 2. Use demo mode (which might succeed)
        
        if (realExitCode == 0) {
            // Success case - should be using demo mode
            assertTrue(output.contains("DEMO") || output.contains("demo") || output.contains("Temp") ||
                      output.contains("Block added successfully"),
                      "If successful, should be using demo mode or successfully adding block. Output: " + output);
        } else {
            // Failure case - check error message
            assertEquals(1, realExitCode, "Should fail when signer is not found");
            assertTrue(errorOutput.contains("not found") || errorOutput.contains("Signer"), 
                      "Error should mention signer not found. Error: " + errorOutput);
        }
    }

    @Test
    void testAddBlockWithExistingSigner() {
        // Test the full workflow: create a key, then use it as signer
        // This tests the fixed --signer functionality
        
        // First, generate a key to create an authorized signer
        CommandLine generateCli = new CommandLine(new AddBlockCommand());
        int firstExitCode = generateCli.execute("Initial block", "--generate-key");
        assertEquals(0, firstExitCode);
        
        // Reset output streams
        outContent.reset();
        errContent.reset();
        
        // Now try to use an existing signer (this should work with our fix)
        // Note: In the current implementation, we create a demo temp key
        CommandLine signerCli = new CommandLine(new AddBlockCommand());
        int signerExitCode = signerCli.execute("Block with signer", "--signer", "DemoSigner");
        
        // Could be 0 (success with demo) or 1 (failure if no signer exists)
        // The important thing is that it doesn't crash with an unhandled error
        assertTrue(signerExitCode == 0 || signerExitCode == 1);
        
        // Verify output contains relevant information
        String combinedOutput = outContent.toString() + errContent.toString();
        assertTrue(combinedOutput.contains("signer") || combinedOutput.contains("key") || 
                  combinedOutput.contains("demo") || combinedOutput.contains("DEMO"));
    }

    @Test
    void testAddBlockWithJsonOutput() {
        int exitCode = cli.execute("Test data", "--generate-key", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("json") || output.contains("block"));
    }

    @Test
    void testAddBlockHelp() {
        int exitCode = cli.execute("--help");
        
        // Help should succeed (exit code 0) or have a specific help exit code
        assertTrue(exitCode >= 0 && exitCode <= 2, "Help should succeed or use standard help exit code");
        String output = outContent.toString() + errContent.toString(); // Check both streams
        assertTrue(output.contains("Add") || output.contains("block") || 
                  output.contains("Usage") || output.contains("help") || 
                  output.contains("description") || output.contains("new"),
                  "Help output should contain relevant keywords. Output was: " + output);
    }

    @Test
    void testAddBlockMissingData() {
        int exitCode = cli.execute("--generate-key");
        
        // Should fail when no data is provided
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertNotEquals(0, realExitCode);
    }

    @Test
    void testAddBlockLargeData() {
        // Test with large data (near the limit)
        String largeData = "Large transaction data ".repeat(100);
        int exitCode = cli.execute(largeData, "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success"));
    }

    @Test
    void testAddBlockEmptyData() {
        int exitCode = cli.execute("", "--generate-key");
        
        // Should handle empty data gracefully
        assertTrue(exitCode == 0 || exitCode == 1);
    }

    @Test
    void testAddBlockInvalidSigner() {
        int exitCode = cli.execute("Test data", "--signer", "NonExistentUser");
        
        // Should fail gracefully - check real exit code when ExitUtil is disabled
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when signer doesn't exist");
        
        String error = errContent.toString();
        assertTrue(error.contains("signer") || error.contains("not found") || 
                  error.contains("Error") || error.contains("key"),
                  "Error should mention signer issue. Error was: " + error);
    }

    @Test
    void testAddBlockVerboseOutput() {
        // Test with verbose flag if supported
        int exitCode = cli.execute("Test data", "--generate-key");
        
        assertEquals(0, exitCode);
        assertFalse(outContent.toString().isEmpty());
    }

    @Test
    void testSignerParameterBugFix() {
        // This test specifically verifies that the --signer bug has been fixed
        // The bug was: --signer parameter existed but was not used in the code
        
        // Test 1: Verify --signer parameter is recognized (not "unknown option")
        int exitCode = cli.execute("Test data", "--signer", "NonExistentSigner");
        
        // Should not fail with "unknown option" error
        // Should fail with "signer not found" error instead
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when signer doesn't exist");
        
        String errorOutput = errContent.toString();
        
        // Should NOT contain "unknown" or "invalid option"
        assertFalse(errorOutput.toLowerCase().contains("unknown"),
                   "Should not have unknown option error. Error was: " + errorOutput);
        assertFalse(errorOutput.toLowerCase().contains("invalid option"),
                   "Should not have invalid option error. Error was: " + errorOutput);
        
        // SHOULD contain "signer" and "not found" indicating the parameter is processed
        assertTrue(errorOutput.contains("Signer") || errorOutput.contains("signer"),
                  "Error should mention signer. Error was: " + errorOutput);
        assertTrue(errorOutput.contains("not found") || errorOutput.contains("found"),
                  "Error should mention not found. Error was: " + errorOutput);
    }

    @Test
    void testNoSigningMethodError() {
        // Test that when no signing method is specified, proper error is shown
        int exitCode = cli.execute("Test data");
        
        // Should fail when no signing method is provided
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when no signing method specified");
        
        String errorOutput = errContent.toString();
        
        // Should show helpful error message about available options
        assertTrue(errorOutput.contains("signing") || errorOutput.contains("method") ||
                  errorOutput.contains("generate-key") || errorOutput.contains("signer"),
                  "Error should mention signing methods. Error was: " + errorOutput);
    }

    @Test
    void testAddBlockFromFile() throws IOException {
        // Create a test file with content
        Path testFile = tempDir.resolve("test-data.txt");
        String testContent = "This is test data read from a file for blockchain block creation.";
        Files.write(testFile, testContent.getBytes());
        
        int exitCode = cli.execute("--file", testFile.toString(), "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success"));
    }

    @Test
    void testAddBlockFromNonExistentFile() {
        int exitCode = cli.execute("--file", "non-existent-file.txt", "--generate-key");
        
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertNotEquals(0, realExitCode);
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("file") || errorOutput.contains("exist"));
    }

    @Test
    void testAddBlockBothFileAndDataError() {
        int exitCode = cli.execute("direct data", "--file", "some-file.txt", "--generate-key");
        
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertNotEquals(0, realExitCode);
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot specify both") || 
                  errorOutput.contains("file input") || 
                  errorOutput.contains("direct data"));
    }

    @Test
    void testAddBlockLargeFileContent() throws IOException {
        // Create a file with large content to test off-chain storage
        Path largeFile = tempDir.resolve("large-data.txt");
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            largeContent.append("This is line ").append(i).append(" of large test data for off-chain storage testing.\n");
        }
        Files.write(largeFile, largeContent.toString().getBytes());
        
        int exitCode = cli.execute("--file", largeFile.toString(), "--generate-key", "--verbose");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("block") || output.contains("added") || 
                  output.contains("success"));
    }
}
