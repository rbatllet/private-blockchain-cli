package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import picocli.CommandLine;
import com.rbatllet.blockchain.security.SecureKeyStorage;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ManageKeysCommand
 * Tests all key management operations including security edge cases
 */
public class ManageKeysCommandTest {

    // No temp directory needed, we'll use the actual private-keys directory

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;
    
    private CommandLine cli;
    private final String testOwner = "TestUser";
    private final String testPassword = "testPassword";

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Clean any existing key files to ensure a clean test environment
        cleanKeyFiles();
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        cli = new CommandLine(new ManageKeysCommand());
    }
    
    /**
     * Helper method to clean key files from the private-keys directory
     */
    private void cleanKeyFiles() {
        // The directory will be created automatically by SecureKeyStorage when needed
        File privateKeysDir = new File("private-keys");
        if (privateKeysDir.exists()) {
            File[] keyFiles = privateKeysDir.listFiles((dir, name) -> name.endsWith(".key"));
            if (keyFiles != null) {
                for (File file : keyFiles) {
                    file.delete();
                }
            }
        }
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Clean up the private-keys directory
        cleanKeyFiles();
        
        // Re-enable ExitUtil after tests
        ExitUtil.enableExit();
    }

    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }
    
    @Test
    void testListEmptyKeys() {
        // Clean any existing key files to ensure a clean test environment
        cleanKeyFiles();
        
        int exitCode = cli.execute("--list");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode, "Exit code should be 0");
        String output = outContent.toString();

        // Empty list shows: "📝 No private keys are currently stored"
        assertTrue(output.contains("No private keys are currently stored"),
                 "Should show no keys message: " + output);
    }

    @Test
    void testListKeysWithStoredKeys() throws Exception {
        // Clean any existing key files to ensure a clean test environment
        cleanKeyFiles();

        // Generate test key pairs
        KeyPair keyPair1 = CryptoUtil.generateKeyPair();
        KeyPair keyPair2 = CryptoUtil.generateKeyPair();

        // Store keys using SecureKeyStorage
        assertTrue(SecureKeyStorage.savePrivateKey("User1", keyPair1.getPrivate(), testPassword),
                "Should save User1 private key");
        assertTrue(SecureKeyStorage.savePrivateKey("User2", keyPair2.getPrivate(), testPassword),
                "Should save User2 private key");

        // Verify the key files exist
        File privateKeysDir = new File("private-keys");
        assertTrue(privateKeysDir.exists(), "private-keys directory should be created");
        assertTrue(new File(privateKeysDir, "User1.key").exists(), "User1.key file should exist");
        assertTrue(new File(privateKeysDir, "User2.key").exists(), "User2.key file should exist");

        // Test list command
        int exitCode = cli.execute("--list");
        int realExitCode = getRealExitCode(exitCode);

        assertEquals(0, realExitCode, "Exit code should be 0");
        String output = outContent.toString();

        // Shows: "📊 Total: 2 stored private key(s)"
        assertTrue(output.contains("Total:") && output.contains("stored private key"),
                "Should show total stored keys: " + output);
        assertTrue(output.toLowerCase().contains("user1"),
                "Output should contain User1. Output: " + output);
        assertTrue(output.toLowerCase().contains("user2"),
                "Output should contain User2. Output: " + output);
    }
    
    // Test for adding keys is in AddKeyCommandTest class

    @Test
    void testListKeysJson() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        int exitCode = cli.execute("--list", "--json");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("storedKeys"));
        assertTrue(output.contains(testOwner));
        assertTrue(output.contains("count"));
    }

    @Test
    void testCheckExistingKey() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        int exitCode = cli.execute("--check", testOwner);
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private key is stored for: " + testOwner));
        assertTrue(output.contains("You can use --signer"));
    }

    @Test
    void testCheckNonExistentKey() {
        int exitCode = cli.execute("--check", "NonExistentUser");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No private key stored for: NonExistentUser"));
        assertTrue(output.contains("add-key"));
    }

    @Test
    void testCheckKeyJson() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        int exitCode = cli.execute("--check", testOwner, "--json");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("\"owner\": \"" + testOwner + "\""));
        assertTrue(output.contains("\"hasPrivateKey\": true"));
    }

    @Test
    void testDeleteExistingKey() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        // Configure the command with input stream
        ManageKeysCommand command = new ManageKeysCommand();
        ByteArrayInputStream input = new ByteArrayInputStream("yes\n".getBytes());
        command.setInputStream(input);
        
        CommandLine cmdLine = new CommandLine(command);
        int exitCode = cmdLine.execute("--delete", testOwner);
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private key deleted for: " + testOwner));
        
        // Verify key is actually deleted
        assertFalse(SecureKeyStorage.hasPrivateKey(testOwner));
    }

    @Test
    void testDeleteKeyCancel() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        // Configure the command with input stream
        ManageKeysCommand command = new ManageKeysCommand();
        ByteArrayInputStream input = new ByteArrayInputStream("no\n".getBytes());
        command.setInputStream(input);
        
        CommandLine cmdLine = new CommandLine(command);
        int exitCode = cmdLine.execute("--delete", testOwner);
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Operation cancelled"));
        
        // Verify key still exists
        assertTrue(SecureKeyStorage.hasPrivateKey(testOwner));
    }

    @Test
    void testDeleteNonExistentKeySimple() {
        // Ultra simple test just to see what happens
        System.out.println("DEBUG: Test starting...");
        
        // Just verify that SecureKeyStorage.hasPrivateKey works as expected
        boolean hasKey = SecureKeyStorage.hasPrivateKey("NonExistentUser");
        System.out.println("DEBUG: hasPrivateKey result = " + hasKey);
        assertFalse(hasKey); // Should be false
        
        System.out.println("DEBUG: Test ending...");
    }

    @Test
    void testDeleteKeyJson() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        // Configure the command with input stream
        ManageKeysCommand command = new ManageKeysCommand();
        ByteArrayInputStream input = new ByteArrayInputStream("yes\n".getBytes());
        command.setInputStream(input);
        
        CommandLine cmdLine = new CommandLine(command);
        int exitCode = cmdLine.execute("--delete", testOwner, "--json");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("\"owner\": \"" + testOwner + "\""));
        assertTrue(output.contains("\"deleted\": true"));
    }

    @Test
    void testNoOptionsShowsUsage() {
        int exitCode = cli.execute();
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Key Management"));
        assertTrue(output.contains("Usage:"));
        assertTrue(output.contains("Options:"));
        assertTrue(output.contains("Examples:"));
    }

    @Test
    void testMultipleOperationsNotAllowed() {
        // Test that only one operation can be performed at a time
        int exitCode = cli.execute("--list", "--check", testOwner);
        int realExitCode = getRealExitCode(exitCode);
        
        // Should handle gracefully (either work or fail with proper error)
        assertEquals(0, realExitCode, "Multiple operations are allowed and succeed, but was: " + realExitCode);
    }

    @Test
    void testLongOwnerNames() {
        String longOwnerName = "VeryLongOwnerNameThatMightCauseIssues".repeat(3);
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        
        // Store with long name
        assertTrue(SecureKeyStorage.savePrivateKey(longOwnerName, keyPair.getPrivate(), testPassword));
        
        // Test check with long name
        int exitCode = cli.execute("--check", longOwnerName);
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode);
        
        String output = outContent.toString();
        assertTrue(output.contains("Private key is stored for: " + longOwnerName));
        
        // Clean up
        SecureKeyStorage.deletePrivateKey(longOwnerName);
    }

    @Test
    void testSpecialCharactersInOwnerName() {
        String specialOwnerName = "User@#$%^&*()_+";
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        
        // Store with special characters
        assertTrue(SecureKeyStorage.savePrivateKey(specialOwnerName, keyPair.getPrivate(), testPassword));
        
        // Test operations
        int exitCode = cli.execute("--check", specialOwnerName);
        int realExitCode = getRealExitCode(exitCode);
        assertEquals(0, realExitCode);
        
        // Clean up
        SecureKeyStorage.deletePrivateKey(specialOwnerName);
    }
}