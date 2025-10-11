package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.security.SecureKeyStorage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stress and performance tests for the complete secure key management system
 * Tests system behavior under high load and edge conditions
 */
public class SecureKeyManagementStressTest {

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
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Cleanup
        for (int i = 0; i < 50; i++) {
            SecureKeyStorage.deletePrivateKey("StressUser" + i);
        }
    }

    @Test
    void testHighVolumeKeyOperations() {
        final int keyCount = 10; // Reduced for CI
        final String basePassword = "StressTestPassword123";
        
        long startTime = System.currentTimeMillis();
        
        // Create many keys in sequence
        for (int i = 0; i < keyCount; i++) {
            String owner = "StressUser" + i;
            String password = basePassword + i;
            String passwordInput = password + "\n" + password + "\n";
            
            ByteArrayInputStream input = new ByteArrayInputStream(passwordInput.getBytes());
            System.setIn(input);
            
            CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
            int exitCode = addKeyCmd.execute(owner, "--generate", "--store-private");
            assertEquals(0, exitCode, "Should successfully create key " + i);
            
            assertTrue(SecureKeyStorage.hasPrivateKey(owner), "Key should be stored for " + owner);
        }
        
        long creationTime = System.currentTimeMillis() - startTime;
        System.out.println("Created " + keyCount + " keys in " + creationTime + "ms");
        
        // Test signing with some keys
        for (int i = 0; i < Math.min(keyCount, 5); i++) {
            String owner = "StressUser" + i;
            String password = basePassword + i;
            
            outContent.reset();
            ByteArrayInputStream signInput = new ByteArrayInputStream((password + "\n").getBytes());
            System.setIn(signInput);
            
            CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
            int exitCode = addBlockCmd.execute("Stress test block " + i, "--signer", owner);
            assertEquals(0, exitCode, "Block creation should succeed");
        }
        
        // Performance should be reasonable
        assertTrue(creationTime < 30000, "Key creation should complete within 30 seconds");
    }
}