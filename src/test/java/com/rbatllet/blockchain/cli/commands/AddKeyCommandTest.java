package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AddKeyCommand
 * 
 * Note: These tests use ExitUtil.disableExit() to prevent System.exit() calls
 * from terminating the JVM during test execution.
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
        // Shows: "✅ Authorized key added successfully!"
        assertTrue(output.contains("Authorized key added successfully"),
                  "Should show success message: " + output);
    }

    @Test
    void testAddKeyWithGenerateAndStorePrivate() {
        // Save original System.in
        InputStream originalIn = System.in;

        try {
            // Create a stream with the password input (password + confirmation)
            String simulatedInput = "testPassword123\ntestPassword123\n";
            ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
            System.setIn(testIn);

            // Execute command with store-private flag
            int exitCode = cli.execute("SecureTestUser", "--generate", "--store-private");

            // Check exit code
            int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
            assertEquals(0, realExitCode, "Should succeed when generating key with store-private option");

            // Verify output shows success
            String output = outContent.toString();
            assertTrue(output.contains("Authorized key added successfully"),
                      "Should show success message: " + output);

        } finally {
            // Restore original System.in
            System.setIn(originalIn);
        }
    }

    @Test
    void testAddKeyMissingName() {
        int exitCode = cli.execute("--generate");

        String output = outContent.toString();
        String errorOutput = errContent.toString();
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;

        // PicoCLI shows missing parameter error with exit code 0
        assertEquals(0, realExitCode, "PicoCLI returns 0 for parameter errors");
        String combinedOutput = output + errorOutput;
        assertTrue(combinedOutput.contains("Missing required parameter"),
                  "Should show missing parameter error: " + combinedOutput);
    }

    @Test
    void testAddKeyWithGenerateAndShowPrivate() {
        int exitCode = cli.execute("TestUser", "--generate", "--show-private");

        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(0, realExitCode, "Should succeed when generating and showing private key");

        String output = outContent.toString();
        // Shows success and private key
        assertTrue(output.contains("Authorized key added successfully"),
                  "Should show success message: " + output);
    }

    @Test
    void testAddKeyWithPublicKey() {
        String samplePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA";
        int exitCode = cli.execute("TestUser", "--public-key", samplePublicKey);

        // May fail due to invalid key format, but should not crash
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode,
                  "Should fail with invalid public key format, but was: " + realExitCode);

        // Shows error about invalid key
        String combinedOutput = outContent.toString() + errContent.toString();
        assertTrue(combinedOutput.contains("Failed to add authorized key"),
                  "Should show error: " + combinedOutput);
    }

    @Test
    void testAddKeyHelp() {
        int exitCode = cli.execute("--help");

        assertEquals(2, exitCode, "Help returns usage information with exit code 2");
        String output = outContent.toString();
        String errorOutput = errContent.toString();

        // Help shows usage information
        String combinedOutput = output + errorOutput;
        assertTrue(combinedOutput.contains("Usage:"),
                  "Should show usage: " + combinedOutput);
    }

    @Test
    void testAddKeyWithJsonOutput() {
        int exitCode = cli.execute("TestUser", "--generate", "--json");

        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(0, realExitCode, "Should succeed with JSON output");

        String output = outContent.toString();
        assertTrue(output.contains("{"),
                  "JSON output should contain JSON structure. Output: " + output);
    }

    @Test
    void testAddKeyInvalidOptions() {
        // Test with both generate and public key (should be mutually exclusive or handled gracefully)
        int exitCode = cli.execute("TestUser", "--generate", "--public-key", "somekey");

        // Should handle invalid combination gracefully
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        // Either should succeed (if handled gracefully) or fail with error
        assertEquals(0, realExitCode,
                  "Options are handled gracefully (not mutually exclusive), but was: " + realExitCode);
    }

    @Test
    void testAddKeyStorePrivateWithPublicKey() {
        int exitCode = cli.execute("TestUser", "--public-key", "somekey", "--store-private");

        // Should fail because you can't store private key when providing public key
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when trying to store private key with provided public key");

        String errorOutput = errContent.toString();
        // Shows error about invalid combination
        assertTrue(errorOutput.contains("Cannot store private key when using provided public key"),
                  "Should show error: " + errorOutput);
    }
}
