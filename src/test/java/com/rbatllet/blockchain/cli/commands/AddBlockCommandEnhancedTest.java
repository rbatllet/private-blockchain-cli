package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.cli.security.SecureKeyStorage;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.cli.util.ExitUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced test suite for AddBlockCommand focusing on new secure key management features
 * Tests the improved --signer functionality and integration with SecureKeyStorage
 */
public class AddBlockCommandEnhancedTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;
    
    private final String testPassword = "EnhancedTestPassword123";
    private final String secureUser = "SecureTestUser";
    private final String demoUser = "DemoTestUser";

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        // Clean up any existing test keys
        SecureKeyStorage.deletePrivateKey(secureUser);
        SecureKeyStorage.deletePrivateKey(demoUser);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
        
        // Clean up test keys
        SecureKeyStorage.deletePrivateKey(secureUser);
        SecureKeyStorage.deletePrivateKey(demoUser);
    }

    @Test
    void testSignerWithStoredPrivateKey() {
        // Setup: Create a user with stored private key
        String passwordInput = testPassword + "\n" + testPassword + "\n";
        ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
        System.setIn(input);
        
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        assertEquals(0, addKeyCmd.execute(secureUser, "--generate", "--store-private"));
        
        // Test: Use --signer with stored private key
        outContent.reset();
        ByteArrayInputStream signInput = new ByteArrayInputStream((testPassword + "\n").getBytes());
        System.setIn(signInput);
        
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        int exitCode = addBlockCmd.execute("Test block with stored key", "--signer", secureUser);
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Using stored private key for signer: " + secureUser));
        assertTrue(output.contains("Block added successfully"));
        assertFalse(output.contains("DEMO MODE"));
    }

    @Test
    void testSignerWithoutStoredKey() {
        // Setup: Create a user without stored private key
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        assertEquals(0, addKeyCmd.execute(demoUser, "--generate"));
        
        // Verify no private key is stored
        assertFalse(SecureKeyStorage.hasPrivateKey(demoUser));
        
        // Test: Use --signer without stored private key (should use demo mode)
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        int exitCode = addBlockCmd.execute("Test block in demo mode", "--signer", demoUser);
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("DEMO MODE"));
        assertTrue(output.contains("No stored private key found for signer: " + demoUser));
    }

    @Test
    void testSignerWithWrongPassword() {
        // Setup: Create user with stored private key
        String passwordInput = testPassword + "\n" + testPassword + "\n";
        ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
        System.setIn(input);
        
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        assertEquals(0, addKeyCmd.execute(secureUser, "--generate", "--store-private"));
        
        // Test: Use wrong password
        outContent.reset();
        errContent.reset();
        String wrongPassword = "WrongPassword123";
        ByteArrayInputStream wrongInput = new ByteArrayInputStream((wrongPassword + "\n").getBytes());
        System.setIn(wrongInput);
        
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        addBlockCmd.execute("Test block", "--signer", secureUser);
        
        assertEquals(1, ExitUtil.getLastExitCode());
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Failed to load private key") || !errorOutput.trim().isEmpty());
    }

    @Test
    void testGenerateKeyMode() {
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        int exitCode = addBlockCmd.execute("Test block with generated key", "--generate-key");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Generated new key pair for signing"));
        assertTrue(output.contains("Block added successfully"));
        assertTrue(output.contains("Public Key:"));
    }

    @Test
    void testCompleteWorkflowWithRealKeys() {
        // This test demonstrates practical usage with real key loading
        
        // Step 1: Create secure user and store private key
        String passwordInput = testPassword + "\n" + testPassword + "\n";
        ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
        System.setIn(input);
        
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        assertEquals(0, addKeyCmd.execute("CompanyManager", "--generate", "--store-private"));
        
        // Step 2: Use real stored key for signing important business data
        outContent.reset();
        ByteArrayInputStream signInput = new ByteArrayInputStream((testPassword + "\n").getBytes());
        System.setIn(signInput);
        
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        int exitCode = addBlockCmd.execute("Q4 Financial Report Approved", "--signer", "CompanyManager");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Using stored private key for signer: CompanyManager"));
        assertTrue(output.contains("Block added successfully"));
        
        // Clean up
        ByteArrayInputStream deleteInput = new ByteArrayInputStream("yes\n".getBytes());
        System.setIn(deleteInput);
        
        CommandLine manageCmd = new CommandLine(new ManageKeysCommand());
        manageCmd.execute("--delete", "CompanyManager");
    }
}