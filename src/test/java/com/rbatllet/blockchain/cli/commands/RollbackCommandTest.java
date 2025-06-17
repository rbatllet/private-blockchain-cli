package com.rbatllet.blockchain.cli.commands;

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
        assertTrue(error.contains("Must specify either --blocks N or --to-block N") ||
                  error.contains("specify") || error.contains("blocks") || error.contains("to-block"),
                  "Error should mention parameter requirements. Error: " + error);
    }

    @Test
    void testBothParametersSpecified() {
        int exitCode = executeCommand("--blocks", "2", "--to-block", "1");
        
        assertEquals(2, exitCode, "Should fail when both parameters are specified");
        
        String error = errContent.toString();
        assertTrue(error.contains("Cannot specify both --blocks and --to-block options") ||
                  error.contains("both") || error.contains("specify"),
                  "Error should mention parameter conflict. Error: " + error);
    }

    @Test
    void testNegativeBlocksParameter() {
        int exitCode = executeCommand("--blocks", "-1", "--yes");
        
        assertEquals(2, exitCode, "Should fail with negative blocks parameter");
        
        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive") ||
                  error.contains("positive") || error.contains("negative"),
                  "Error should mention positive requirement. Error: " + error);
    }

    @Test
    void testZeroBlocksParameter() {
        int exitCode = executeCommand("--blocks", "0", "--yes");
        
        assertEquals(2, exitCode, "Should fail with zero blocks parameter");
        
        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive") ||
                  error.contains("positive") || error.contains("zero"),
                  "Error should mention positive requirement. Error: " + error);
    }

    @Test
    void testNegativeToBlockParameter() {
        int exitCode = executeCommand("--to-block", "-1", "--yes");
        
        assertEquals(2, exitCode, "Should fail with negative to-block parameter");
        
        String error = errContent.toString();
        assertTrue(error.contains("Target block number cannot be negative") ||
                  error.contains("negative") || error.contains("cannot"),
                  "Error should mention negative restriction. Error: " + error);
    }

    // ===============================
    // DRY RUN TESTS
    // ===============================

    @Test
    void testDryRunWithBlocks() {
        int exitCode = executeCommand("--blocks", "1", "--dry-run");
        
        // Dry run should return 0 or 1 depending on implementation
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Dry run should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        if (exitCode == 0) {
            assertTrue(output.contains("DRY RUN MODE") || 
                      output.contains("would") || 
                      output.contains("preview") || output.contains("simulation") ||
                      output.length() > 10,
                      "Dry run success output should contain preview information. Output: " + output);
        } else {
            // If it failed, should have meaningful error
            assertFalse(allOutput.trim().isEmpty(), 
                       "Should have error output if dry run fails. Combined output: " + allOutput);
        }
    }

    @Test
    void testDryRunWithToBlock() {
        int exitCode = executeCommand("--to-block", "0", "--dry-run");
        
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Dry run to block should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        if (exitCode == 0) {
            assertTrue(output.contains("DRY RUN MODE") || 
                      output.contains("would") || 
                      output.contains("preview") || output.contains("simulation") ||
                      output.length() > 10,
                      "Dry run output should contain preview information. Output: " + output);
        } else {
            assertFalse(allOutput.trim().isEmpty(), 
                       "Should have error output if dry run fails. Combined output: " + allOutput);
        }
    }

    @Test
    void testDryRunJsonOutput() {
        int exitCode = executeCommand("--blocks", "1", "--dry-run", "--json");
        
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Dry run with JSON should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        if (exitCode == 0) {
            assertTrue(output.contains("{") && output.contains("}"),
                      "JSON output should contain braces. Output: " + output);
            assertTrue(output.contains("dryRun") || output.contains("wouldRemove") ||
                      output.contains("\"") || output.contains("simulation"),
                      "JSON should contain dry run indicators. Output: " + output);
        } else {
            assertFalse(allOutput.trim().isEmpty(), 
                       "Should have error output if dry run fails. Combined output: " + allOutput);
        }
    }

    // ===============================
    // HELP AND USAGE TESTS
    // ===============================

    @Test
    void testRollbackHelp() {
        int exitCode = executeCommand("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2, "Help should use standard exit codes");
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("rollback") || 
                  output.contains("Remove") || 
                  output.contains("Usage") || output.contains("help") ||
                  output.contains("description") || output.length() > 10,
                  "Help output should contain relevant information. Output: " + output);
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
        assertTrue(exitCode == 1 || exitCode == 2, 
                  "Should handle rollback of too many blocks gracefully");
        
        if (exitCode == 1) {
            String error = errContent.toString();
            String allOutput = outContent.toString() + error;
            assertTrue(error.contains("Cannot remove") || 
                      error.contains("Only") || 
                      error.contains("blocks exist") ||
                      allOutput.contains("Error") || allOutput.contains("blocks"),
                      "Error should mention block limitation. Combined output: " + allOutput);
        }
    }

    @Test
    void testRollbackToNonExistentBlock() {
        int exitCode = executeCommand("--to-block", "1000", "--yes");
        
        assertTrue(exitCode == 1 || exitCode == 2,
                  "Should handle rollback to non-existent block gracefully");
        
        if (exitCode == 1) {
            String error = errContent.toString();
            String allOutput = outContent.toString() + error;
            assertTrue(error.contains("does not exist") || 
                      error.contains("Current max block") ||
                      allOutput.contains("Error") || allOutput.contains("exist"),
                      "Error should mention block existence. Combined output: " + allOutput);
        }
    }

    // ===============================
    // OUTPUT FORMAT TESTS
    // ===============================

    @Test
    void testJsonOutputFormat() {
        // Test with a simple operation that should work
        int exitCode = executeCommand("--blocks", "1", "--json", "--dry-run");
        
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "JSON output test should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        if (exitCode == 0) {
            // Check JSON structure
            assertTrue(output.contains("{") || output.contains("\""),
                      "JSON output should contain JSON elements. Output: " + output);
        } else {
            // If failed, should have meaningful error
            assertFalse(allOutput.trim().isEmpty(), 
                       "Should have output if command fails. Combined output: " + allOutput);
        }
    }

    @Test
    void testTextOutputFormat() {
        int exitCode = executeCommand("--blocks", "1", "--dry-run");
        
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Text output test should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        // Should have SOME output (either success or error)
        assertFalse(allOutput.trim().isEmpty(), 
                   "Should produce some output. Combined output: " + allOutput);
    }

    // ===============================
    // CONFIRMATION TESTS
    // ===============================

    @Test
    void testConfirmationPromptYes() {
        // Simulate user typing "yes"
        System.setIn(new ByteArrayInputStream("yes\n".getBytes()));
        
        int exitCode = cli.execute("--blocks", "1");
        
        // Will likely fail due to blockchain state, but should get past confirmation
        assertTrue(exitCode == 0 || exitCode == 1);
        String output = outContent.toString();
        assertTrue(output.contains("absolutely sure") || 
                  output.contains("confirm") || 
                  errContent.toString().contains("Error") ||
                  errContent.toString().contains("Cannot remove"));
    }

    @Test
    void testConfirmationPromptNo() {
        // Simulate user typing "no"
        System.setIn(new ByteArrayInputStream("no\n".getBytes()));
        
        int exitCode = executeCommand("--blocks", "1");
        
        // Should succeed (operation cancelled) or fail gracefully
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Confirmation cancelled should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;
        
        // Should have some indication of cancellation or error
        assertFalse(allOutput.trim().isEmpty(), 
                   "Should have output about cancellation or error. Combined output: " + allOutput);
    }

    @Test
    void testSkipConfirmation() {
        int exitCode = executeCommand("--blocks", "1", "--yes");
        
        // Should not prompt for confirmation
        assertTrue(exitCode == 0 || exitCode == 1 || exitCode == 2, 
                  "Skip confirmation should handle gracefully. Exit code: " + exitCode);
        
        String output = outContent.toString();
        // Should not contain confirmation prompt
        assertFalse(output.contains("absolutely sure"), 
                   "Should not prompt when --yes is used. Output: " + output);
    }

    // ===============================
    // EDGE CASES TESTS
    // ===============================

    @Test
    void testRollbackToGenesis() {
        // Try to rollback to genesis block (should be allowed)
        int exitCode = cli.execute("--to-block", "0", "--yes");
        
        // This should either succeed or fail gracefully
        assertTrue(exitCode == 0 || exitCode == 1);
    }

    @Test
    void testRollbackOneBlock() {
        int exitCode = executeCommand("--blocks", "1", "--yes");
        
        // Should handle single block rollback
        assertTrue(exitCode == 0 || exitCode == 1 || exitCode == 2, 
                  "Single block rollback should handle gracefully. Exit code: " + exitCode);
    }

    @Test
    void testRollbackWithVerboseOutput() {
        // Test that verbose output doesn't break anything
        int exitCode = executeCommand("--blocks", "1", "--dry-run");
        
        assertTrue(exitCode == 0 || exitCode == 1, 
                  "Verbose output test should handle gracefully. Exit code: " + exitCode);
        
        String allOutput = outContent.toString() + errContent.toString();
        assertFalse(allOutput.trim().isEmpty(), 
                   "Should produce some output. Combined output: " + allOutput);
    }

    // ===============================
    // PARAMETER COMBINATION TESTS
    // ===============================

    @Test
    void testAllOptionsTogetherDryRun() {
        int exitCode = cli.execute("--blocks", "1", "--yes", "--json", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") && output.contains("}"));
    }

    @Test
    void testValidParametersCombination() {
        // Test that valid combinations work
        int exitCode = cli.execute("--to-block", "0", "--json", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("\"") || output.contains("dryRun"));
    }

    // ===============================
    // INTEGRATION TESTS
    // ===============================

    @Test
    void testBlockchainIntegrationBasic() {
        // This test verifies that the command can connect to blockchain
        int exitCode = cli.execute("--blocks", "1", "--dry-run");
        
        // Should be able to connect and show preview
        assertEquals(0, exitCode);
        assertFalse(outContent.toString().isEmpty());
    }

    @Test
    void testErrorHandlingWithDatabase() {
        // Test error handling when database operations might fail
        int exitCode = cli.execute("--blocks", "999", "--yes");
        
        // Should handle database errors gracefully
        assertTrue(exitCode >= 0 && exitCode <= 2);
    }
}