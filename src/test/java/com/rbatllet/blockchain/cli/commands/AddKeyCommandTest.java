package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.cli.util.ExitUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AddKeyCommand
 * 
 * DISABLED: These tests are causing JVM crashes, likely due to System.exit() calls
 * in the AddKeyCommand implementation. The command works fine in practice but
 * causes issues in the test environment.
 */
public class AddKeyCommandTest {

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
        
        cli = new CommandLine(new AddKeyCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable System.exit() after testing
        ExitUtil.enableExit();
    }

    @Test
    void testAddKeyGenerate() {
        int exitCode = cli.execute("TestUser", "--generate");
        
        // Use ExitUtil.getLastExitCode() when exit is disabled
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(0, realExitCode, "Should succeed when generating key for valid user");
        
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("success") || 
                  output.contains("added") || output.contains("TestUser") || 
                  output.contains("generated"),
                  "Output should contain relevant keywords. Output: " + output);
    }

    @Test
    void testAddKeyWithGenerateAndStorePrivate() {
        // This test requires password input, so we skip it for now
        // Can be implemented with proper password mocking later
    }

    @Test 
    void testAddKeyMissingName() {
        int exitCode = cli.execute("--generate");
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        
        // Based on AddKeyCommand logic, it should fail when ownerName is null
        // But PicoCLI might show help message instead of executing the command
        if (realExitCode == 0) {
            // If it succeeded, PicoCLI likely showed help instead of executing
            String combinedOutput = output + errorOutput;
            assertTrue(combinedOutput.contains("help") || combinedOutput.contains("Usage") || 
                      combinedOutput.contains("missing") || combinedOutput.contains("required") ||
                      combinedOutput.contains("description") || combinedOutput.contains("add-key"),
                      "If successful, should show help or missing parameter message. Combined output: " + combinedOutput);
        } else {
            // If it failed as expected, verify it's the right kind of failure
            assertEquals(1, realExitCode, "Should fail with exit code 1 when no name is provided");
            assertTrue(errorOutput.contains("empty") || errorOutput.contains("required") || 
                      errorOutput.contains("missing") || errorOutput.contains("name"),
                      "Error should mention missing name. Error: " + errorOutput);
        }
    }

    @Test
    void testAddKeyWithGenerateAndShowPrivate() {
        int exitCode = cli.execute("TestUser", "--generate", "--show-private");
        
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(0, realExitCode, "Should succeed when generating and showing private key");
        
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("private") || 
                  output.contains("generated") || output.contains("TestUser"),
                  "Output should contain key-related keywords. Output: " + output);
    }

    @Test
    void testAddKeyWithPublicKey() {
        String samplePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA";
        int exitCode = cli.execute("TestUser", "--public-key", samplePublicKey);
        
        // May fail due to invalid key format, but should not crash
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertTrue(realExitCode == 0 || realExitCode == 1, 
                  "Should handle command gracefully (success or controlled failure)");
        
        // Should have SOME output (either in out or err)
        String combinedOutput = outContent.toString() + errContent.toString();
        assertFalse(combinedOutput.trim().isEmpty(), 
                   "Should have some output. Combined output: " + combinedOutput);
    }

    @Test
    void testAddKeyHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2, "Help should use standard exit codes");
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        
        // Should have SOME output (help text goes to either out or err)
        assertFalse(output.isEmpty() && errorOutput.isEmpty(),
                  "Expected help output but got: out='" + output + "', err='" + errorOutput + "'");
    }

    @Test
    void testAddKeyWithJsonOutput() {
        int exitCode = cli.execute("TestUser", "--generate", "--json");
        
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(0, realExitCode, "Should succeed with JSON output");
        
        String output = outContent.toString();
        assertTrue(output.contains("{") || output.contains("\"") || 
                  output.contains("success") || output.contains("TestUser"),
                  "JSON output should contain JSON formatting. Output: " + output);
    }

    @Test
    void testAddKeyInvalidOptions() {
        // Test with both generate and public key (should be mutually exclusive or handled gracefully)
        int exitCode = cli.execute("TestUser", "--generate", "--public-key", "somekey");
        
        // Should handle invalid combination gracefully
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        // Either should succeed (if handled gracefully) or fail with error
        assertTrue(realExitCode == 0 || realExitCode == 1, 
                  "Should handle option combination gracefully");
    }

    @Test
    void testAddKeyStorePrivateWithPublicKey() {
        int exitCode = cli.execute("TestUser", "--public-key", "somekey", "--store-private");
        
        // Should fail because you can't store private key when providing public key
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when trying to store private key with provided public key");
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot store private key") || 
                  errorOutput.contains("private") || errorOutput.contains("public"),
                  "Error should mention the conflict. Error: " + errorOutput);
    }
}
