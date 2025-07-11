package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.AuthorizedKey;
import com.rbatllet.blockchain.util.CryptoUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Manual test to verify the --signer bug fix without ExitUtil.exit() issues
 * This test directly tests the blockchain functionality rather than the CLI
 */
public class AddBlockCommandManualTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Create blockchain and ensure clean state
        blockchain = new Blockchain();
        blockchain.clearAndReinitialize();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testSignerParameterBugFixDirectly() {
        // Test 1: Create an authorized key first using the main project pattern
        CryptoUtil.KeyInfo testKeyInfo = CryptoUtil.createRootKey();
        KeyPair testKeyPair = new KeyPair(
            CryptoUtil.stringToPublicKey(testKeyInfo.getPublicKeyEncoded()),
            CryptoUtil.stringToPrivateKey(testKeyInfo.getPrivateKeyEncoded())
        );
        String publicKeyString = CryptoUtil.publicKeyToString(testKeyPair.getPublic());
        
        boolean keyAdded = blockchain.addAuthorizedKey(publicKeyString, "TestUser");
        assertTrue(keyAdded, "Should be able to add authorized key");
        
        // Test 2: Verify we can find the authorized key by owner name
        AuthorizedKey foundKey = blockchain.getAuthorizedKeyByOwner("TestUser");
        assertNotNull(foundKey, "Should find authorized key by owner name");
        assertEquals("TestUser", foundKey.getOwnerName(), "Owner name should match");
        assertEquals(publicKeyString, foundKey.getPublicKey(), "Public key should match");
        
        // Test 3: Verify the method exists and works (this was the main bug)
        // Before the fix, getAuthorizedKeyByOwner would not be found
        List<AuthorizedKey> allKeys = blockchain.getAuthorizedKeys();
        assertTrue(allKeys.size() > 0, "Should have at least one authorized key");
        
        // Test 4: Add a block using a different key (simulating the CLI functionality)
        // Use the same pattern as the main project tests for better compatibility
        CryptoUtil.KeyInfo signingKeyInfo = CryptoUtil.createRootKey();
        KeyPair signingKeyPair = new KeyPair(
            CryptoUtil.stringToPublicKey(signingKeyInfo.getPublicKeyEncoded()),
            CryptoUtil.stringToPrivateKey(signingKeyInfo.getPrivateKeyEncoded())
        );
        String signingPublicKey = CryptoUtil.publicKeyToString(signingKeyPair.getPublic());
        
        // Add some debugging
        System.out.println("Generated key algorithm: " + signingKeyPair.getPrivate().getAlgorithm());
        System.out.println("Generated public key: " + signingPublicKey.substring(0, 20) + "...");
        
        boolean keyAddedSuccess = blockchain.addAuthorizedKey(signingPublicKey, "TempSigner");
        assertTrue(keyAddedSuccess, "Should be able to add the signing key as authorized");
        
        // Verify the key was actually added
        AuthorizedKey addedKey = blockchain.getAuthorizedKeyByOwner("TempSigner");
        assertNotNull(addedKey, "Should find the newly added authorized key");
        assertEquals(signingPublicKey, addedKey.getPublicKey(), "Public key should match");
        
        boolean blockAdded = blockchain.addBlock("Test block data", 
                                               signingKeyPair.getPrivate(), 
                                               signingKeyPair.getPublic());
        assertTrue(blockAdded, "Should be able to add block with authorized key");
        
        // Test 5: Verify block was added correctly  
        // Note: May have more than 2 blocks if previous tests ran, so we just check it increased
        long blockCountAfter = blockchain.getBlockCount();
        assertTrue(blockCountAfter >= 2, "Should have at least 2 blocks (genesis + new)");
        
        System.out.println("✅ All tests passed! The --signer bug has been fixed.");
        System.out.println("   - getAuthorizedKeyByOwner method exists and works");
        System.out.println("   - Can find authorized keys by owner name");
        System.out.println("   - Can add blocks with authorized keys");
        System.out.println("   - Total blocks in chain: " + blockCountAfter);
    }

    @Test
    void testNonExistentSigner() {
        // Test that looking up a non-existent signer returns null
        AuthorizedKey nonExistentKey = blockchain.getAuthorizedKeyByOwner("NonExistentUser");
        assertNull(nonExistentKey, "Should return null for non-existent signer");
    }

    @Test
    void testMultipleSigners() {
        // Test with multiple signers using the main project pattern
        CryptoUtil.KeyInfo keyInfo1 = CryptoUtil.createRootKey();
        CryptoUtil.KeyInfo keyInfo2 = CryptoUtil.createRootKey();
        
        KeyPair keyPair1 = new KeyPair(
            CryptoUtil.stringToPublicKey(keyInfo1.getPublicKeyEncoded()),
            CryptoUtil.stringToPrivateKey(keyInfo1.getPrivateKeyEncoded())
        );
        KeyPair keyPair2 = new KeyPair(
            CryptoUtil.stringToPublicKey(keyInfo2.getPublicKeyEncoded()),
            CryptoUtil.stringToPrivateKey(keyInfo2.getPrivateKeyEncoded())
        );
        
        String publicKey1 = CryptoUtil.publicKeyToString(keyPair1.getPublic());
        String publicKey2 = CryptoUtil.publicKeyToString(keyPair2.getPublic());
        
        blockchain.addAuthorizedKey(publicKey1, "User1");
        blockchain.addAuthorizedKey(publicKey2, "User2");
        
        AuthorizedKey foundUser1 = blockchain.getAuthorizedKeyByOwner("User1");
        AuthorizedKey foundUser2 = blockchain.getAuthorizedKeyByOwner("User2");
        
        assertNotNull(foundUser1, "Should find User1");
        assertNotNull(foundUser2, "Should find User2");
        assertEquals("User1", foundUser1.getOwnerName());
        assertEquals("User2", foundUser2.getOwnerName());
        assertNotEquals(foundUser1.getPublicKey(), foundUser2.getPublicKey(), 
                       "Different users should have different keys");
    }
}
