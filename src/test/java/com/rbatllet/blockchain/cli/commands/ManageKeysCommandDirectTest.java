package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.security.SecureKeyStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ManageKeysCommandDirectTest {

    private ManageKeysCommand command;

    @BeforeEach
    void setUp() {
        command = new ManageKeysCommand();
    }

    @Test
    void testDeleteNonExistentKeyDirect() {
        // Setup input stream to simulate "yes" confirmation
        String input = "yes\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        command.setInputStream(inputStream);
        
        // Try to delete a non-existent key
        String nonExistentOwner = "definitely-does-not-exist-12345";
        
        // Verify the key doesn't exist
        assertFalse(SecureKeyStorage.hasPrivateKey(nonExistentOwner), 
                   "Key should not exist before test");
        
        // Test the new method that doesn't call ExitUtil.exit()
        try {
            ManageKeysCommand.DeleteResult result = command.deleteStoredKeyWithResult(nonExistentOwner);
            
            // Should return failure result
            assertFalse(result.success, "Delete operation should fail for non-existent key");
            assertNotNull(result.errorMessage, "Error message should be provided");
            assertTrue(result.errorMessage.contains("No private key stored for"), 
                      "Error message should indicate key doesn't exist");
            
        } catch (ManageKeysCommand.OperationCancelledException e) {
            fail("Should not throw OperationCancelledException for non-existent key, got: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentKeyWithCancellation() {
        // Setup input stream to simulate "no" (cancel operation)
        String input = "no\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        command.setInputStream(inputStream);
        
        // For this test, let's use a key that does exist, but user cancels
        String nonExistentOwner = "definitely-does-not-exist-54321";
        
        try {
            ManageKeysCommand.DeleteResult result = command.deleteStoredKeyWithResult(nonExistentOwner);
            // If the key doesn't exist, we should get a failure result without asking for confirmation
            assertFalse(result.success, "Delete operation should fail for non-existent key");
            
        } catch (ManageKeysCommand.OperationCancelledException e) {
            fail("Should not ask for confirmation for non-existent key");
        }
    }

    @Test
    void testCheckNonExistentKey() {
        // This is a simpler test that shouldn't trigger any exit calls
        String nonExistentOwner = "definitely-does-not-exist-12321";
        
        // Verify the key doesn't exist using SecureKeyStorage directly
        assertFalse(SecureKeyStorage.hasPrivateKey(nonExistentOwner), 
                   "Key should not exist");
    }
}
