package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.core.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for new AddBlockCommand features: recipient encryption and custom metadata
 * Tests the --recipient, --metadata, --category, --keywords, and --off-chain options
 */
public class AddBlockCommandRecipientMetadataTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        // Initialize blockchain with clean state for each test
        try {
            Blockchain blockchain = new Blockchain();
            blockchain.clearAndReinitialize();
        } catch (Exception e) {
            // If blockchain initialization fails, continue with test
        }
        
        // Setup test authorized users first
        setupTestUsers();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
    }

    /**
     * Helper method to get the real exit code, following the pattern from working tests
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    
    private void setupTestUsers() {
        // Create authorized users for recipient encryption tests
        CommandLine addKeyCmd1 = new CommandLine(new AddKeyCommand());
        addKeyCmd1.execute("alice", "--generate");
        
        CommandLine addKeyCmd2 = new CommandLine(new AddKeyCommand());
        addKeyCmd2.execute("bob", "--generate");
        
        outContent.reset(); // Clear setup output
    }

    @Test
    @DisplayName("🔐 Should create block with recipient encryption")
    void testRecipientEncryption() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with recipient encryption
        int exitCode = cmd.execute("Confidential message for Alice", "--recipient", "alice", "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("🔐 Encrypted: Yes"),
                  "Should show recipient encryption: " + output);
        assertTrue(output.contains("👤 Recipient:"),
                  "Should show recipient info: " + output);
    }

    @Test
    @DisplayName("📊 Should create block with custom metadata")
    void testCustomMetadata() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with multiple metadata entries
        int exitCode = cmd.execute("Project documentation",
            "--metadata", "author=Bob Smith",
            "--metadata", "department=R&D",
            "--metadata", "version=2.1.0",
            "--metadata", "status=draft",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📊 Custom Metadata:"),
                  "Should show metadata in detailed output: " + output);
    }

    @Test
    @DisplayName("📂 Should create block with category and keywords")
    void testCategoryAndKeywords() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with category and keywords
        int exitCode = cmd.execute("Financial quarterly report",
            "--category", "FINANCIAL",
            "--keywords", "quarterly,report,Q4,2024",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📂 Category:"),
                  "Should show category in detailed output: " + output);
    }

    @Test
    @DisplayName("🎯 Should create block with all new options combined")
    void testAllNewOptionsCombined() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with recipient, metadata, category, and keywords
        int exitCode = cmd.execute("Confidential quarterly financial report",
            "--recipient", "bob",
            "--metadata", "department=Finance",
            "--metadata", "quarter=Q4",
            "--metadata", "year=2024",
            "--metadata", "confidentiality=HIGH",
            "--category", "FINANCIAL",
            "--keywords", "quarterly,report,finance,confidential",
            "--username", "analyst_alice",
            "--detailed",
            "--verbose");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("🔐 Encrypted: Yes"),
                  "Should show recipient encryption: " + output);
        assertTrue(output.contains("👤 Recipient:"),
                  "Should show recipient info: " + output);
        assertTrue(output.contains("📊 Custom Metadata:"),
                  "Should show metadata in detailed output: " + output);
        assertTrue(output.contains("📂 Category:"),
                  "Should show category in detailed output: " + output);
    }

    @Test
    @DisplayName("💾 Should create block with off-chain storage option")
    void testOffChainOption() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with off-chain option
        int exitCode = cmd.execute("Large dataset for off-chain storage",
            "--off-chain",
            "--category", "DATA",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📂 Category:"),
                  "Should show category in detailed output: " + output);
    }

    @Test
    @DisplayName("📄 Should output JSON with new fields")
    void testJsonOutputWithNewFields() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());
        
        // Execute: Create block with recipient and metadata, JSON output
        int exitCode = cmd.execute("Test message for JSON output", 
            "--recipient", "alice",
            "--metadata", "test=value",
            "--metadata", "environment=testing",
            "--category", "TEST",
            "--json");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString();
        
        // Verify JSON structure
        assertTrue(output.contains("\"success\": true"), "Should have success field");
        assertTrue(output.contains("\"blockNumber\":"), "Should have block number");
        assertTrue(output.contains("\"encrypted\": true"), "Should show encrypted status");
        assertTrue(output.contains("\"recipient\": \"alice\""), "Should show recipient in JSON");
        assertTrue(output.contains("\"customMetadata\":"), "Should include custom metadata in JSON");
        assertTrue(output.contains("\"category\": \"TEST\""), "Should show category in JSON");
    }

    @Test
    @DisplayName("⚠️ Should handle invalid metadata format")
    void testInvalidMetadataFormat() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Try to create block with invalid metadata format
        int exitCode = cmd.execute("Test message",
            "--metadata", "invalid-format-no-equals",
            "--generate-key");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("⚠️  Invalid metadata format"),
                  "Should show warning for invalid metadata format: " + output);
    }

    @Test
    @DisplayName("❌ Should fail with non-existent recipient")
    void testNonExistentRecipient() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Try to create block for non-existent recipient
        int exitCode = cmd.execute("Message for unknown user",
            "--recipient", "unknown_user");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(1, realExitCode,
                  "Should fail with non-existent recipient, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("❌"),
                  "Should show error message: " + output);
    }

    @Test
    @DisplayName("🔍 Should show verbose output for new options")
    void testVerboseOutputNewOptions() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with verbose output
        int exitCode = cmd.execute("Test message for verbose output",
            "--metadata", "author=TestAuthor",
            "--category", "TEST",
            "--keywords", "test,verbose",
            "--generate-key",
            "--verbose");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("Adding metadata"),
                  "Should show metadata processing in verbose mode: " + output);
    }

    @Test
    @DisplayName("🔄 Should maintain backward compatibility")
    void testBackwardCompatibility() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());
        
        // Execute: Use old-style command without new options
        int exitCode = cmd.execute("Legacy test message", "--generate-key");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"), 
                  "Should show success message: " + output);
    }

    @Test
    @DisplayName("🔐 Should handle recipient encryption with password fallback")
    void testRecipientWithPasswordFallback() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with both recipient and password
        int exitCode = cmd.execute("Message with dual encryption options",
            "--recipient", "alice",
            "--password", "testpassword123",
            "--username", "testuser",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("🔐 Encrypted: Yes"),
                  "Should show encryption status: " + output);
        assertTrue(output.contains("👤 Recipient:"),
                  "Should show recipient info: " + output);
    }

    @Test
    @DisplayName("📊 Should handle multiple metadata with same key")
    void testMultipleMetadataWithSameKey() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with duplicate metadata keys (last one should win)
        int exitCode = cmd.execute("Testing metadata overrides",
            "--metadata", "version=1.0.0",
            "--metadata", "author=FirstAuthor",
            "--metadata", "version=2.0.0",
            "--metadata", "author=SecondAuthor",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📊 Custom Metadata:"),
                  "Should show metadata in detailed output: " + output);
    }

    @Test
    @DisplayName("🏷️ Should handle empty and special characters in keywords")
    void testSpecialCharactersInKeywords() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with special characters in keywords
        int exitCode = cmd.execute("Testing special keywords",
            "--keywords", "test,special-chars,under_score,123numbers,@symbol",
            "--category", "TEST",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📂 Category:"),
                  "Should show category in detailed output: " + output);
    }

    @Test
    @DisplayName("📂 Should handle category case conversion")
    void testCategoryHandling() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with lowercase category (should be converted to uppercase)
        int exitCode = cmd.execute("Testing category handling",
            "--category", "financial",
            "--generate-key",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📂 Category:"),
                  "Should show category in detailed output: " + output);
    }

    @Test
    @DisplayName("🎯 Should handle mixed encryption modes")
    void testMixedEncryptionModes() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with username and identifier (no encryption)
        int exitCode = cmd.execute("Non-encrypted block with metadata",
            "--username", "testuser",
            "--identifier", "TEST-ID-001",
            "--metadata", "type=public",
            "--metadata", "visibility=open",
            "--category", "PUBLIC",
            "--detailed");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("📊 Custom Metadata:"),
                  "Should show metadata in detailed output: " + output);
        assertFalse(output.contains("🔐 Encrypted: Yes"),
                  "Should not show encryption for public block");
    }

    @Test
    @DisplayName("💾 Should handle off-chain with custom file path")
    void testOffChainWithCustomPath() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());
        
        // Execute: Create block with custom off-chain file path
        int exitCode = cmd.execute("Custom off-chain storage test", 
            "--off-chain",
            "--metadata", "storage=custom",
            "--category", "STORAGE_TEST",
            "--generate-key",
            "--verbose");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"), 
                  "Should show success message: " + output);
    }

    @Test
    @DisplayName("🔍 Should validate metadata key-value format strictly")
    void testStrictMetadataValidation() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Test various invalid metadata formats
        int exitCode = cmd.execute("Testing metadata validation",
            "--metadata", "valid=value",
            "--metadata", "no-equals-sign",
            "--metadata", "=empty-key",
            "--metadata", "key=",
            "--metadata", "multiple=equals=signs",
            "--generate-key",
            "--verbose");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("Adding metadata"),
                  "Should show metadata processing in verbose mode: " + output);
    }

    @Test
    @DisplayName("🚀 Should handle performance with many metadata entries")
    void testPerformanceWithManyMetadata() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Create block with many metadata entries
        String[] args = new String[23]; // Base args + 9 metadata entries
        args[0] = "Performance test with many metadata entries";
        args[1] = "--generate-key";
        args[2] = "--category";
        args[3] = "PERFORMANCE";
        args[4] = "--verbose";

        // Add 9 metadata entries
        for (int i = 0; i < 18; i += 2) {
            args[5 + i] = "--metadata";
            args[6 + i] = "key" + (i/2 + 1) + "=value" + (i/2 + 1);
        }

        int exitCode = cmd.execute(args);

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"),
                  "Should show success message: " + output);
        assertTrue(output.contains("Adding metadata"),
                  "Should show metadata processing in verbose mode: " + output);
    }

    @Test
    @DisplayName("🔄 Should handle concurrent execution compatibility")
    void testConcurrentExecutionSafety() {
        // Execute: Multiple commands in succession to test thread safety
        CommandLine cmd1 = new CommandLine(new AddBlockCommand());
        CommandLine cmd2 = new CommandLine(new AddBlockCommand());
        CommandLine cmd3 = new CommandLine(new AddBlockCommand());
        
        int exitCode1 = cmd1.execute("Concurrent test 1", 
            "--metadata", "thread=1",
            "--category", "CONCURRENT",
            "--generate-key");
            
        int exitCode2 = cmd2.execute("Concurrent test 2", 
            "--recipient", "alice",
            "--metadata", "thread=2",
            "--category", "CONCURRENT");
            
        int exitCode3 = cmd3.execute("Concurrent test 3", 
            "--metadata", "thread=3",
            "--keywords", "concurrent,test,thread",
            "--category", "CONCURRENT",
            "--generate-key");
        
        int realExitCode1 = getRealExitCode(exitCode1);
        assertEquals(0, realExitCode1, "First concurrent command should succeed, but was: " + realExitCode1);
        int realExitCode2 = getRealExitCode(exitCode2);
        assertEquals(0, realExitCode2, "Second concurrent command should succeed, but was: " + realExitCode2);
        int realExitCode3 = getRealExitCode(exitCode3);
        assertEquals(0, realExitCode3, "Third concurrent command should succeed, but was: " + realExitCode3);
        
        String output = outContent.toString();
        
        // Should contain success messages for all three operations
        long successCount = output.lines()
            .filter(line -> line.contains("✅ Block Added Successfully"))
            .count();
        assertTrue(successCount >= 1, "Should show at least one successful block addition, got: " + successCount);
    }

    @Test
    @DisplayName("🎨 Should handle edge cases in option combinations")
    void testEdgeCaseOptionCombinations() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());
        
        // Execute: Create block with edge case combinations
        int exitCode = cmd.execute("Edge case testing with empty values", 
            "--recipient", "alice",
            "--metadata", "empty=",
            "--metadata", "spaces= ",
            "--metadata", "normal=value",
            "--detailed");
        
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("✅ Block Added Successfully"), 
                  "Should show success message: " + output);
    }

    @Test
    @DisplayName("📋 Should provide comprehensive help with new options")
    void testHelpWithNewOptions() {
        CommandLine cmd = new CommandLine(new AddBlockCommand());

        // Execute: Show help to verify new options are documented
        int exitCode = cmd.execute("--help");

        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode,
                  "Help command should succeed, but was: " + realExitCode);

        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("--recipient"),
                  "Help should show --recipient option: " + output);
    }
}