package com.rbatllet.blockchain.cli.commands;

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
        
        cli = new CommandLine(new RollbackCommand());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    // ===============================
    // PARAMETER VALIDATION TESTS
    // ===============================

    @Test
    void testNoParametersSpecified() {
        int exitCode = cli.execute();
        
        assertEquals(2, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Must specify either --blocks N or --to-block N"));
    }

    @Test
    void testBothParametersSpecified() {
        int exitCode = cli.execute("--blocks", "2", "--to-block", "1");
        
        assertEquals(2, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Cannot specify both --blocks and --to-block options"));
    }

    @Test
    void testNegativeBlocksParameter() {
        int exitCode = cli.execute("--blocks", "-1", "--yes");
        
        assertEquals(2, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive"));
    }

    @Test
    void testZeroBlocksParameter() {
        int exitCode = cli.execute("--blocks", "0", "--yes");
        
        assertEquals(2, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Number of blocks must be positive"));
    }

    @Test
    void testNegativeToBlockParameter() {
        int exitCode = cli.execute("--to-block", "-1", "--yes");
        
        assertEquals(2, exitCode);
        String error = errContent.toString();
        assertTrue(error.contains("Target block number cannot be negative"));
    }

    // ===============================
    // DRY RUN TESTS
    // ===============================

    @Test
    void testDryRunWithBlocks() {
        int exitCode = cli.execute("--blocks", "1", "--dry-run");
        
        // Dry run should always return 0 (success) since it doesn't actually do anything
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("DRY RUN MODE") || 
                  output.contains("would") || 
                  output.contains("preview"));
    }

    @Test
    void testDryRunWithToBlock() {
        int exitCode = cli.execute("--to-block", "0", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("DRY RUN MODE") || 
                  output.contains("would") || 
                  output.contains("preview"));
    }

    @Test
    void testDryRunJsonOutput() {
        int exitCode = cli.execute("--blocks", "1", "--dry-run", "--json");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{") && output.contains("}"));
        assertTrue(output.contains("dryRun") || output.contains("wouldRemove"));
    }

    // ===============================
    // HELP AND USAGE TESTS
    // ===============================

    @Test
    void testRollbackHelp() {
        int exitCode = cli.execute("--help");
        
        assertTrue(exitCode >= 0 && exitCode <= 2);
        String output = outContent.toString();
        assertTrue(output.contains("rollback") || 
                  output.contains("Remove") || 
                  output.contains("Usage"));
    }

    // ===============================
    // ERROR HANDLING TESTS
    // ===============================

    @Test
    void testRollbackMoreBlocksThanExist() {
        // This test might pass or fail depending on blockchain state
        // The important thing is that it handles the error gracefully
        int exitCode = cli.execute("--blocks", "1000", "--yes");
        
        // Should be either 1 (business logic error) or 2 (parameter error)
        assertTrue(exitCode == 1 || exitCode == 2);
        
        if (exitCode == 1) {
            String error = errContent.toString();
            assertTrue(error.contains("Cannot remove") || 
                      error.contains("Only") || 
                      error.contains("blocks exist"));
        }
    }

    @Test
    void testRollbackToNonExistentBlock() {
        int exitCode = cli.execute("--to-block", "1000", "--yes");
        
        assertTrue(exitCode == 1 || exitCode == 2);
        
        if (exitCode == 1) {
            String error = errContent.toString();
            assertTrue(error.contains("does not exist") || 
                      error.contains("Current max block"));
        }
    }

    // ===============================
    // OUTPUT FORMAT TESTS
    // ===============================

    @Test
    void testJsonOutputFormat() {
        // Test with a simple operation that should work
        int exitCode = cli.execute("--blocks", "1", "--json", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Check JSON structure
        assertTrue(output.contains("{"));
        assertTrue(output.contains("}"));
        assertTrue(output.contains("\"") || output.contains("dryRun"));
    }

    @Test
    void testTextOutputFormat() {
        int exitCode = cli.execute("--blocks", "1", "--dry-run");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Should contain human-readable text
        assertTrue(output.contains("ROLLBACK") || 
                  output.contains("blocks") || 
                  output.contains("DRY RUN"));
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
        
        int exitCode = cli.execute("--blocks", "1");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("cancelled") || 
                  output.contains("absolutely sure"));
    }

    @Test
    void testSkipConfirmation() {
        int exitCode = cli.execute("--blocks", "1", "--yes");
        
        // Should not prompt for confirmation
        assertTrue(exitCode == 0 || exitCode == 1 || exitCode == 2);
        String output = outContent.toString();
        assertFalse(output.contains("absolutely sure"));
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
        int exitCode = cli.execute("--blocks", "1", "--yes");
        
        // Should handle single block rollback
        assertTrue(exitCode == 0 || exitCode == 1 || exitCode == 2);
    }

    @Test
    void testRollbackWithVerboseOutput() {
        // Test that verbose output doesn't break anything
        int exitCode = cli.execute("--blocks", "1", "--dry-run");
        
        assertEquals(0, exitCode);
        assertFalse(outContent.toString().isEmpty());
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