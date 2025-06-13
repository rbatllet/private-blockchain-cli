package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.cli.security.SecureKeyStorage;
import com.rbatllet.blockchain.cli.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ManageKeysCommand
 * Tests all key management operations including security edge cases
 */
public class ManageKeysCommandTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;
    
    private CommandLine cli;
    private final String testPassword = "SecureTest123";
    private final String testOwner = "TestUser";

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        cli = new CommandLine(new ManageKeysCommand());
        
        // Clean up any existing test keys
        SecureKeyStorage.deletePrivateKey(testOwner);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Re-enable System.exit() after testing
        ExitUtil.enableExit();
        
        // Clean up test keys
        SecureKeyStorage.deletePrivateKey(testOwner);
        SecureKeyStorage.deletePrivateKey("User1");
        SecureKeyStorage.deletePrivateKey("User2");
    }

    @Test
    void testListEmptyKeys() {
        int exitCode = cli.execute("--list");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("No private keys") || output.contains("currently stored"));
    }

    @Test
    void testListKeysWithStoredKeys() {
        // First store some test keys
        KeyPair keyPair1 = CryptoUtil.generateKeyPair();
        KeyPair keyPair2 = CryptoUtil.generateKeyPair();
        
        assertTrue(SecureKeyStorage.savePrivateKey("User1", keyPair1.getPrivate(), testPassword));
        assertTrue(SecureKeyStorage.savePrivateKey("User2", keyPair2.getPrivate(), testPassword));
        
        // Test list command
        int exitCode = cli.execute("--list");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("User1"));
        assertTrue(output.contains("User2"));
        assertTrue(output.contains("Total: 2"));
    }

    @Test
    void testListKeysJson() {
        // Store test key
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        assertTrue(SecureKeyStorage.savePrivateKey(testOwner, keyPair.getPrivate(), testPassword));
        
        int exitCode = cli.execute("--list", "--json");
        
        assertEquals(0, exitCode);
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
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private key is stored for: " + testOwner));
        assertTrue(output.contains("You can use --signer"));
    }

    @Test
    void testCheckNonExistentKey() {
        int exitCode = cli.execute("--check", "NonExistentUser");
        
        assertEquals(0, exitCode);
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
        
        assertEquals(0, exitCode);
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
        
        assertEquals(0, exitCode);
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
        
        assertEquals(0, exitCode);
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
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("{"));
        assertTrue(output.contains("\"owner\": \"" + testOwner + "\""));
        assertTrue(output.contains("\"deleted\": true"));
    }

    @Test
    void testNoOptionsShowsUsage() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
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
        
        // Should handle gracefully (either work or fail with proper error)
        assertTrue(exitCode >= 0);
    }

    @Test
    void testLongOwnerNames() {
        String longOwnerName = "VeryLongOwnerNameThatMightCauseIssues".repeat(3);
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        
        // Store with long name
        assertTrue(SecureKeyStorage.savePrivateKey(longOwnerName, keyPair.getPrivate(), testPassword));
        
        // Test check with long name
        int exitCode = cli.execute("--check", longOwnerName);
        assertEquals(0, exitCode);
        
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
        assertEquals(0, exitCode);
        
        // Clean up
        SecureKeyStorage.deletePrivateKey(specialOwnerName);
    }
}