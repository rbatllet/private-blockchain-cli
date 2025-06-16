package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test específic per la nova opció --verbose del AddBlockCommand
 */
@DisplayName("AddBlockCommand --verbose Tests")
public class AddBlockCommandVerboseTest {

    @TempDir
    Path tempDir;

    private KeyPair testKeyPair;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Generate test key pair
        testKeyPair = CryptoUtil.generateKeyPair();
        
        // Capture output
        System.setOut(new PrintStream(outContent));
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
    }

    @AfterEach
    void tearDown() {
        // Restore output and exit functionality
        System.setOut(originalOut);
        ExitUtil.enableExit();
    }

    @Test
    @DisplayName("Should show verbose output with --verbose flag")
    void shouldShowVerboseOutputWithFlag() throws Exception {
        // Create test key file
        String privateKeyBase64 = Base64.getEncoder().encodeToString(testKeyPair.getPrivate().getEncoded());
        String pemContent = "-----BEGIN PRIVATE KEY-----\n" +
                splitBase64(privateKeyBase64, 64) + "\n" +
                "-----END PRIVATE KEY-----\n";
        
        Path keyFile = tempDir.resolve("verbose_test.pem");
        Files.write(keyFile, pemContent.getBytes());
        
        // Execute command with --verbose flag
        CommandLine cli = new CommandLine(new AddBlockCommand());
        int exitCode = cli.execute("Test verbose output", "--key-file", keyFile.toString(), "--verbose");
        
        String output = outContent.toString();
        
        // Verify exit code
        assertEquals(0, exitCode);
        
        // Verify verbose messages are present
        assertTrue(output.contains("🔍 Adding new block to blockchain..."), 
            "Should show verbose message for blockchain operation");
        assertTrue(output.contains("🔍 Loading private key from file:"), 
            "Should show verbose message for key file loading");
        assertTrue(output.contains("🔍 Detected key file format:"), 
            "Should show verbose message for format detection");
        assertTrue(output.contains("🔍 Key file:"), 
            "Should show verbose message with key file path");
        assertTrue(output.contains("🔍 Format:"), 
            "Should show verbose message with detected format");
            
        // Verify normal success messages are also present
        assertTrue(output.contains("✅ Block added successfully!"), 
            "Should show success message");
    }

    @Test
    @DisplayName("Should not show verbose output without --verbose flag")
    void shouldNotShowVerboseOutputWithoutFlag() throws Exception {
        // Create test key file
        String privateKeyBase64 = Base64.getEncoder().encodeToString(testKeyPair.getPrivate().getEncoded());
        String pemContent = "-----BEGIN PRIVATE KEY-----\n" +
                splitBase64(privateKeyBase64, 64) + "\n" +
                "-----END PRIVATE KEY-----\n";
        
        Path keyFile = tempDir.resolve("non_verbose_test.pem");
        Files.write(keyFile, pemContent.getBytes());
        
        // Ensure global verbose is disabled
        boolean originalGlobalVerbose = BlockchainCLI.verbose;
        BlockchainCLI.verbose = false;
        
        try {
            // Execute command without --verbose flag
            CommandLine cli = new CommandLine(new AddBlockCommand());
            int exitCode = cli.execute("Test without verbose", "--key-file", keyFile.toString());
            
            String output = outContent.toString();
            
            // Verify exit code
            assertEquals(0, exitCode);
            
            // Verify verbose messages are NOT present
            assertFalse(output.contains("🔍 Adding new block to blockchain..."), 
                "Should not show verbose message for blockchain operation");
            assertFalse(output.contains("🔍 Loading private key from file:"), 
                "Should not show verbose message for key file loading");
            assertFalse(output.contains("🔍 Detected key file format:"), 
                "Should not show verbose message for format detection");
            
            // Verify normal success messages are still present
            assertTrue(output.contains("✅ Block added successfully!"), 
                "Should still show success message");
                
        } finally {
            BlockchainCLI.verbose = originalGlobalVerbose;
        }
    }

    @Test
    @DisplayName("Should show verbose output with --generate-key --verbose")
    void shouldShowVerboseOutputWithGenerateKey() throws Exception {
        // Execute command with --generate-key and --verbose
        CommandLine cli = new CommandLine(new AddBlockCommand());
        int exitCode = cli.execute("Test generate key verbose", "--generate-key", "--verbose");
        
        String output = outContent.toString();
        
        // Verify exit code
        assertEquals(0, exitCode);
        
        // Verify verbose messages are present
        assertTrue(output.contains("🔍 Adding new block to blockchain..."), 
            "Should show verbose message for blockchain operation");
        assertTrue(output.contains("🔍 Generating new key pair..."), 
            "Should show verbose message for key generation");
            
        // Verify key generation messages
        assertTrue(output.contains("Generated new key pair for signing"), 
            "Should show key generation success message");
        assertTrue(output.contains("✅ Block added successfully!"), 
            "Should show success message");
    }

    @Test
    @DisplayName("Should show verbose help message")
    void shouldShowVerboseInHelp() throws Exception {
        // Capture both output and error streams since help goes to error
        ByteArrayOutputStream errorContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorContent));
        
        try {
            // Execute help command
            CommandLine cli = new CommandLine(new AddBlockCommand());
            cli.execute("--help");
            
            String output = outContent.toString() + errorContent.toString();
            
            // Verify --verbose option is documented in help
            assertTrue(output.contains("--verbose") || output.contains("-v"), 
                "Help should show --verbose option");
            assertTrue(output.contains("Enable verbose output"), 
                "Help should mention verbose functionality");
        } finally {
            System.setErr(originalErr);
        }
    }

    // Helper method to split Base64 into lines
    private String splitBase64(String base64, int lineLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < base64.length(); i += lineLength) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(base64.substring(i, Math.min(i + lineLength, base64.length())));
        }
        return result.toString();
    }
}