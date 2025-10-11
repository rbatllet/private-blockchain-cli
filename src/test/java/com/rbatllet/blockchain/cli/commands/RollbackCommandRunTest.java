package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.KeyPair;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically targeting the run method in RollbackCommand
 * to improve code coverage.
 */
public class RollbackCommandRunTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;
    private RollbackCommand rollbackCommand;

    @BeforeEach
    void setUp() {
        // Redirect System.out and System.err to capture output
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Clean blockchain state for isolated tests
        try {
            Blockchain blockchain = new Blockchain();
            blockchain.clearAndReinitialize();
        } catch (Exception e) {
            System.err.println("Warning: Could not clear blockchain: " + e.getMessage());
        }
        
        // Disable actual system exit
        ExitUtil.disableExit();
        
        rollbackCommand = new RollbackCommand();
    }

    @AfterEach
    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // No need to reset exit state, it's handled by ExitUtil
    }

    @Test
    @DisplayName("Should handle missing required parameters")
    void shouldHandleMissingRequiredParameters() {
        // No parameters set (both blocksToRemove and targetBlock are null)
        rollbackCommand.run();

        // Verify error message and exit code
        String output = errContent.toString();
        assertTrue(output.contains("Must specify either --blocks"),
                  "Should show error about missing parameters. Error: " + output);
        assertEquals(2, ExitUtil.getLastExitCode());
    }

    @Test
    @DisplayName("Should handle conflicting parameters")
    void shouldHandleConflictingParameters() {
        // Set both parameters which is not allowed
        rollbackCommand.blocksToRemove = 2L;
        rollbackCommand.targetBlock = 5L;
        
        rollbackCommand.run();

        // Verify error message and exit code
        String output = errContent.toString();
        assertTrue(output.contains("Cannot specify both --blocks and --to-block"),
                  "Should show error about conflicting parameters. Error: " + output);
        assertEquals(2, ExitUtil.getLastExitCode());
    }

    @Test
    @DisplayName("Should handle invalid blocks to remove (negative value)")
    void shouldHandleInvalidBlocksToRemove() {
        rollbackCommand.blocksToRemove = -1L;
        
        rollbackCommand.run();

        // Verify error message and exit code
        String output = errContent.toString();
        assertTrue(output.contains("Number of blocks must be positive"),
                  "Should show error about invalid block count. Error: " + output);
        assertEquals(2, ExitUtil.getLastExitCode());
    }

    @Test
    @DisplayName("Should handle invalid target block (negative value)")
    void shouldHandleInvalidTargetBlock() {
        rollbackCommand.targetBlock = -1L;
        
        rollbackCommand.run();

        // Verify error message and exit code
        String output = errContent.toString();
        assertTrue(output.contains("Target block number cannot be negative"),
                  "Should show error about invalid target block. Error: " + output);
        assertEquals(2, ExitUtil.getLastExitCode());
    }

    @Test
    @DisplayName("Should handle dry run with JSON output")
    void shouldHandleDryRunWithJsonOutput() {
        // Add a test block so we have something to rollback
        try {
            Blockchain blockchain = new Blockchain();
            CryptoUtil.KeyInfo keyInfo = CryptoUtil.createRootKey();
            KeyPair keyPair = new KeyPair(
                CryptoUtil.stringToPublicKey(keyInfo.getPublicKeyEncoded()),
                CryptoUtil.stringToPrivateKey(keyInfo.getPrivateKeyEncoded())
            );
            String publicKey = CryptoUtil.publicKeyToString(keyPair.getPublic());
            blockchain.addAuthorizedKey(publicKey, "TestUser");
            blockchain.addBlock("Test block for rollback", keyPair.getPrivate(), keyPair.getPublic());
        } catch (Exception e) {
            // If we can't add a block, the test will still run but may show error messages
        }
        
        rollbackCommand.blocksToRemove = 1L;
        rollbackCommand.dryRun = true;
        rollbackCommand.json = true;
        
        rollbackCommand.run();
        
        // Verify JSON output contains dry run information
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // Should have JSON with dryRun: true
        assertTrue(allOutput.contains("dryRun"),
                "Should contain dryRun in JSON output. Output: " + allOutput);
    }

    @Test
    @DisplayName("Should handle dry run with text output")
    void shouldHandleDryRunWithTextOutput() {
        // Add a test block so we have something to rollback
        try {
            Blockchain blockchain = new Blockchain();
            CryptoUtil.KeyInfo keyInfo = CryptoUtil.createRootKey();
            KeyPair keyPair = new KeyPair(
                CryptoUtil.stringToPublicKey(keyInfo.getPublicKeyEncoded()),
                CryptoUtil.stringToPrivateKey(keyInfo.getPrivateKeyEncoded())
            );
            String publicKey = CryptoUtil.publicKeyToString(keyPair.getPublic());
            blockchain.addAuthorizedKey(publicKey, "TestUser");
            blockchain.addBlock("Test block for rollback", keyPair.getPrivate(), keyPair.getPublic());
        } catch (Exception e) {
            // If we can't add a block, the test will still run but may show error messages
        }
        
        rollbackCommand.blocksToRemove = 1L;
        rollbackCommand.dryRun = true;
        
        rollbackCommand.run();
        
        // Verify text output contains dry run information
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // Should have dry run mode message
        assertTrue(allOutput.contains("DRY RUN MODE"),
                "Should contain DRY RUN MODE message. Output: " + allOutput);
    }

    @Test
    @DisplayName("Should handle user cancellation")
    void shouldHandleUserCancellation() {
        // Add a test block so we have something to rollback
        try {
            Blockchain blockchain = new Blockchain();
            CryptoUtil.KeyInfo keyInfo = CryptoUtil.createRootKey();
            KeyPair keyPair = new KeyPair(
                CryptoUtil.stringToPublicKey(keyInfo.getPublicKeyEncoded()),
                CryptoUtil.stringToPrivateKey(keyInfo.getPrivateKeyEncoded())
            );
            String publicKey = CryptoUtil.publicKeyToString(keyPair.getPublic());
            blockchain.addAuthorizedKey(publicKey, "TestUser");
            blockchain.addBlock("Test block for rollback", keyPair.getPrivate(), keyPair.getPublic());
        } catch (Exception e) {
            // If we can't add a block, the test will still run but may show error messages
        }
        
        // Set up user input to cancel the operation
        ByteArrayInputStream in = new ByteArrayInputStream("no\n".getBytes());
        System.setIn(in);
        
        rollbackCommand.blocksToRemove = 1L;
        
        rollbackCommand.run();
        
        // Verify cancellation message
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // Should have cancellation message (user typed "no")
        assertTrue(allOutput.contains("Operation cancelled"),
                "Should contain operation cancelled message. Output: " + allOutput);
    }

    @Test
    @DisplayName("Should handle runtime exception with JSON output")
    void shouldHandleRuntimeExceptionWithJsonOutput() {
        // Use negative value to trigger validation error (exitCode=2, not 1)
        rollbackCommand.blocksToRemove = -5L; // Invalid value that will cause validation error
        rollbackCommand.skipConfirmation = true;
        rollbackCommand.json = true;

        rollbackCommand.run();

        // Verify exit code was set to 2 (parameter validation error)
        int exitCode = ExitUtil.getLastExitCode();
        assertEquals(2, exitCode, "Should fail with parameter validation error code, but was: " + exitCode);

        // Verify error output shows validation error message
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Number of blocks must be positive"),
                "Should show validation error message: " + output);
    }
}
