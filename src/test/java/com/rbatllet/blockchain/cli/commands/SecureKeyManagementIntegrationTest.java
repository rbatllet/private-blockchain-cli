package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.cli.security.SecureKeyStorage;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for secure key management workflow
 * Tests the complete workflow from key generation to block signing
 */
public class SecureKeyManagementIntegrationTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;
    
    private final String testPassword = "IntegrationTest123";
    private final String testOwner = "IntegrationUser";

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Clean up any existing test keys
        SecureKeyStorage.deletePrivateKey(testOwner);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Clean up test keys
        SecureKeyStorage.deletePrivateKey(testOwner);
    }

    @Test
    void testCompleteWorkflowGenerateKeyAndStoreSecurely() {
        // Simulate password input for key generation
        String passwordInput = testPassword + "\n" + testPassword + "\n";
        ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
        System.setIn(input);
        
        // Step 1: Generate and store a secure key
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        int exitCode1 = addKeyCmd.execute(testOwner, "--generate", "--store-private");
        
        assertEquals(0, exitCode1);
        String output1 = outContent.toString();
        assertTrue(output1.contains("Authorized key added successfully"));
        assertTrue(output1.contains("Private key stored securely"));
        
        // Verify key is stored
        assertTrue(SecureKeyStorage.hasPrivateKey(testOwner));
        
        // Step 2: Use the stored key to sign a block
        outContent.reset();
        ByteArrayInputStream signInput = new ByteArrayInputStream((testPassword + "\n").getBytes());
        System.setIn(signInput);
        
        CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
        int exitCode3 = addBlockCmd.execute("Integration test block", "--signer", testOwner);
        
        assertEquals(0, exitCode3);
        String output3 = outContent.toString();
        assertTrue(output3.contains("Using stored private key for signer: " + testOwner));
        assertTrue(output3.contains("Block added successfully"));
    }

    @Test
    void testPracticalBusinessExample() {
        // Demonstrate practical usage for business scenario
        
        // Create department manager with secure key
        String passwordInput = "SecureManagerPass123\nSecureManagerPass123\n";
        ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
        System.setIn(input);
        
        CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
        assertEquals(0, addKeyCmd.execute("DepartmentManager", "--generate", "--store-private"));
        
        // Manager signs important documents
        String[] documents = {
            "Employee Performance Review Q4 2024",
            "Budget Approval for IT Infrastructure",
            "Compliance Audit Results - PASSED"
        };
        
        for (String document : documents) {
            outContent.reset();
            ByteArrayInputStream signInput = new ByteArrayInputStream("SecureManagerPass123\n".getBytes());
            System.setIn(signInput);
            
            CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
            int exitCode = addBlockCmd.execute(document, "--signer", "DepartmentManager");
            assertEquals(0, exitCode);
            
            String output = outContent.toString();
            assertTrue(output.contains("Using stored private key"));
            assertTrue(output.contains("Block added successfully"));
        }
        
        // Clean up
        ByteArrayInputStream deleteInput = new ByteArrayInputStream("yes\n".getBytes());
        System.setIn(deleteInput);
        new CommandLine(new ManageKeysCommand()).execute("--delete", "DepartmentManager");
    }
}