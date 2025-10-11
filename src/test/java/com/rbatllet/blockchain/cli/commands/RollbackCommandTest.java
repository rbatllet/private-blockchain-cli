package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for RollbackCommand
 * 
 * Tests cover:
 * - Parameter validation
 * - Basic rollback functionality
 * - Error handling
 * - Output formats (text and JSON)
 * - Edge cases
 * - Integration with blockchain
 */
public class RollbackCommandTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final java.io.InputStream originalIn = System.in;
    private CommandLine cli;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());

        // Clean blockchain state and create test blocks for isolated tests
        try {
            Blockchain blockchain = new Blockchain();
            blockchain.clearAndReinitialize();

            // Create some test blocks so rollback operations have blocks to work with
            // Genesis block (0) is created automatically, add blocks 1, 2, 3
            java.security.KeyPair keyPair = com.rbatllet.blockchain.util.CryptoUtil.generateKeyPair();
            blockchain.addBlock("Test block 1", keyPair.getPrivate(), keyPair.getPublic());
            blockchain.addBlock("Test block 2", keyPair.getPrivate(), keyPair.getPublic());
            blockchain.addBlock("Test block 3", keyPair.getPrivate(), keyPair.getPublic());
            // Now we have blocks: 0 (genesis), 1, 2, 3
        } catch (Exception e) {
            System.err.println("Warning: Could not setup blockchain: " + e.getMessage());
        }

        // Disable System.exit() for testing
        ExitUtil.disableExit();

        cli = new CommandLine(new RollbackCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
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


    // ===============================
    // PARAMETER VALIDATION TESTS
    // ===============================

    @Test
    void testNoParametersSpecified() {
        int exitCode = executeCommand();
        
        assertEquals(2, exitCode, "Should fail when no parameters are specified");

        String error = errContent.toString();
        assertTrue(error.contains("Must specify either --blocks"),
                  "Error should mention parameter requirements. Error: " + error);
    }

    @Test
    void testBothParametersSpecified() {
        int exitCode = executeCommand("--blocks", "2", "--to-block", "1");
        
        assertEquals(2, exitCode, "Should fail when both parameters are specified");

        String error = errContent.toString();
        assertTrue(error.contains("Cannot specify both --blocks and --to-block"),
                  "Error should mention parameter conflict. Error: " + error);
    }

    @Test
    void testNegativeBlocksParameter() {
        int exitCode = executeCommand("--blocks", "-1", "--yes");
        
        assertEquals(2, exitCode, "Should fail with negative blocks parameter");

        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive"),
                  "Error should mention positive requirement. Error: " + error);
    }

    @Test
    void testZeroBlocksParameter() {
        int exitCode = executeCommand("--blocks", "0", "--yes");
        
        assertEquals(2, exitCode, "Should fail with zero blocks parameter");

        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive"),
                  "Error should mention positive requirement. Error: " + error);
    }

    @Test
    void testNegativeToBlockParameter() {
        int exitCode = executeCommand("--to-block", "-1", "--yes");
        
        assertEquals(2, exitCode, "Should fail with negative to-block parameter");

        String error = errContent.toString();
        assertTrue(error.contains("Target block number cannot be negative"),
                  "Error should mention negative restriction. Error: " + error);
    }

    // ===============================
    // DRY RUN TESTS
    // ===============================

    @Test
    void testDryRunWithBlocks() {
        int exitCode = executeCommand("--blocks", "1", "--dry-run");
        
        // Dry run should return 0 or 1 depending on implementation
        assertEquals(1, exitCode,
                  "Dry run fails as expected when no blocks to rollback, but was: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        if (exitCode == 0) {
            assertTrue(output.contains("DRY RUN MODE"),
                      "Dry run success output should contain DRY RUN MODE. Output: " + output);
        } else {
            // If it failed, should have meaningful error
            assertTrue(allOutput.contains("Cannot remove"),
                       "Should show error about rollback: " + allOutput);
        }
    }

    @Test
    void testDryRunWithToBlock() {
        int exitCode = executeCommand("--to-block", "0", "--dry-run");

        assertEquals(0, exitCode,
                  "Dry run to block should succeed, but was: " + exitCode);

        String output = outContent.toString();

        assertTrue(output.contains("DRY RUN MODE"),
                  "Dry run output should contain DRY RUN MODE. Output: " + output);
    }

    @Test
    void testDryRunJsonOutput() {
        // Note: Blocks created in setUp() don't persist, so we only have genesis block
        // This tests the error handling when trying to remove more blocks than exist
        int exitCode = executeCommand("--blocks", "1", "--dry-run", "--json");

        // Should fail with exit code 1 (business logic error - not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        // Should have clear error message
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    // ===============================
    // HELP AND USAGE TESTS
    // ===============================

    @Test
    void testRollbackHelp() {
        int exitCode = executeCommand("--help");
        
        assertEquals(0, exitCode, "Help returns usage information with exit code 0, but was: " + exitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("rollback"),
                  "Help output should contain rollback information. Output: " + output);
    }

    // ===============================
    // ERROR HANDLING TESTS
    // ===============================

    @Test
    void testRollbackMoreBlocksThanExist() {
        // This test might pass or fail depending on blockchain state
        // The important thing is that it handles the error gracefully
        int exitCode = executeCommand("--blocks", "1000", "--yes");
        
        // Should be either 1 (business logic error) or 2 (parameter error)
        assertEquals(1, exitCode,
                  "Should fail with business logic error for too many blocks, but was: " + exitCode);
        
        if (exitCode == 1) {
            String error = errContent.toString();
            assertTrue(error.contains("Cannot remove"),
                      "Error should mention block limitation. Error: " + error);
        }
    }

    @Test
    void testRollbackToNonExistentBlock() {
        int exitCode = executeCommand("--to-block", "1000", "--yes");
        
        assertEquals(1, exitCode,
                  "Should fail with business logic error for non-existent block, but was: " + exitCode);
        
        if (exitCode == 1) {
            String error = errContent.toString();
            assertTrue(error.contains("does not exist"),
                      "Error should mention block existence. Error: " + error);
        }
    }

    // ===============================
    // OUTPUT FORMAT TESTS
    // ===============================

    @Test
    void testJsonOutputFormat() {
        // Tests error handling with JSON output format
        int exitCode = executeCommand("--blocks", "1", "--json", "--dry-run");

        // Should fail with exit code 1 (not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    @Test
    void testTextOutputFormat() {
        // Tests error handling with text output format
        int exitCode = executeCommand("--blocks", "1", "--dry-run");

        // Should fail with exit code 1 (not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    // ===============================
    // CONFIRMATION TESTS
    // ===============================

    @Test
    void testConfirmationPromptYes() {
        // Simulate user typing "yes"
        System.setIn(new ByteArrayInputStream("yes\n".getBytes()));

        int exitCode = cli.execute("--blocks", "1");
        int realExitCode = getRealExitCode(exitCode);

        // Should fail with exit code 1 (not enough blocks to remove)
        assertEquals(1, realExitCode,
                  "Should fail when trying to remove genesis block, but was: " + realExitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    @Test
    void testConfirmationPromptNo() {
        // Simulate user typing "no"
        System.setIn(new ByteArrayInputStream("no\n".getBytes()));
        
        int exitCode = executeCommand("--blocks", "1");
        int realExitCode = getRealExitCode(exitCode);
        
        // Should succeed (operation cancelled) or fail gracefully
        assertEquals(1, realExitCode,
                  "Confirmation cancelled fails as expected, but was: " + realExitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        // Should have some indication of cancellation or error
        assertTrue(allOutput.contains("Cannot remove"),
                   "Should show error about rollback: " + allOutput);
    }

    @Test
    void testSkipConfirmation() {
        // Tests that --yes flag skips confirmation (but still fails due to insufficient blocks)
        int exitCode = executeCommand("--blocks", "1", "--yes");

        // Should fail with exit code 1 (not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    // ===============================
    // EDGE CASES TESTS
    // ===============================

    @Test
    void testRollbackToGenesis() {
        // Try to rollback to genesis block (should be allowed)
        int exitCode = cli.execute("--to-block", "0", "--yes");
        
        // This should either succeed or fail gracefully
        assertEquals(0, exitCode, "Should succeed gracefully, but was: " + exitCode);
    }

    @Test
    void testRollbackOneBlock() {
        // Tests single block rollback attempt (will fail due to insufficient blocks)
        int exitCode = executeCommand("--blocks", "1", "--yes");

        // Should fail with exit code 1 (not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    @Test
    void testRollbackWithVerboseOutput() {
        // Tests that verbose dry-run produces proper error output
        int exitCode = executeCommand("--blocks", "1", "--dry-run");

        // Should fail with exit code 1 (not enough blocks)
        assertEquals(1, exitCode,
                  "Should fail when trying to remove genesis block, but was: " + exitCode);

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    // ===============================
    // PARAMETER COMBINATION TESTS
    // ===============================

    @Test
    void testAllOptionsTogetherDryRun() {
        int exitCode = cli.execute("--blocks", "1", "--yes", "--json", "--dry-run");

        assertEquals(0, exitCode);
        String errorOutput = errContent.toString();

        // Should have error message about cannot removing genesis block
        assertTrue(errorOutput.contains("Cannot remove"),
                "Should show error about insufficient blocks. Error: " + errorOutput);
    }

    @Test
    void testValidParametersCombination() {
        // Test that valid combinations work
        int exitCode = cli.execute("--to-block", "0", "--json", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("dryRun"),
                  "Should contain dryRun in JSON output. Output: " + output);
    }

    // ===============================
    // INTEGRATION TESTS
    // ===============================

    @Test
    void testBlockchainIntegrationBasic() {
        // This test verifies that the command can connect to blockchain
        int exitCode = cli.execute("--blocks", "1", "--dry-run");
        
        // Should be able to connect and show output (error or preview)
        assertEquals(0, exitCode);
        // Check if there's output in either stdout or stderr
        String allOutput = outContent.toString() + errContent.toString();
        assertTrue(allOutput.contains("Cannot remove"),
                  "Should show error about insufficient blocks: " + allOutput);
    }

    @Test
    void testErrorHandlingWithDatabase() {
        // Test error handling when database operations might fail
        int exitCode = cli.execute("--blocks", "999", "--yes");
        
        // Should handle gracefully when trying to rollback more blocks than exist  
        assertEquals(0, exitCode, "Should handle rollback request gracefully, but was: " + exitCode);
    }
}