package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.util.validation.BlockValidationResult;
import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ValidateCommand's detailed validation functionality
 */
public class ValidateCommandDetailedTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ValidateCommand validateCommand;
    private Blockchain blockchain;

    @BeforeEach
    void setUp() throws Exception {
        // Configure standard output to capture output
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Configure temporary directory for blockchain data
        System.setProperty("user.dir", tempDir.toString());
        
        // Initialize commands and blockchain
        validateCommand = new ValidateCommand();
        blockchain = new Blockchain();
        
        // Ensure we use a clean blockchain instance
        // Configure a unique temporary directory for each test
        System.setProperty("blockchain.data.dir", tempDir.toString());
        // Create a new instance that will use this clean directory
        blockchain = new Blockchain();
        // Clean and reinitialize the blockchain
        blockchain.clearAndReinitialize();
        
        // Disable ExitUtil to prevent System.exit() calls during tests
        ExitUtil.disableExit();
        
        // Create a test key
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Convert public key to string format
        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);
        
        // Add the key to the blockchain
        blockchain.addAuthorizedKey(publicKeyString, "Test User");
        
        // Add a test block
        PrivateKey privateKey = keyPair.getPrivate();
        blockchain.addBlock("Test Block", privateKey, publicKey);
    }

    @AfterEach
    void tearDown() {
        // Restore standard output
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Clear system properties
        System.clearProperty("user.dir");
        System.clearProperty("blockchain.data.dir");
        // Re-enable ExitUtil after tests
        ExitUtil.enableExit();
    }
    
    /**
     * Test the validateBlockDetailed method directly
     */
    @Test
    void testValidateBlockDetailedMethod() throws Exception {
        // Get the blocks from the blockchain
        List<Block> blocks = blockchain.getAllBlocks();
        assertEquals(2, blocks.size()); // Genesis block + our test block
        
        Block genesisBlock = blocks.get(0);
        Block testBlock = blocks.get(1);
        
        // Verify that blocks are correctly configured
        assertEquals(0, genesisBlock.getBlockNumber(), "Genesis block should have block number 0");
        assertEquals(1, testBlock.getBlockNumber(), "Test block should have block number 1");
        assertEquals(genesisBlock.getHash(), testBlock.getPreviousHash(), "Test block's previous hash should match genesis block hash");
        
        // Test validation of the test block against its previous block (genesis block)
        BlockValidationResult result = validateCommand.validateBlockDetailed(
            blockchain, testBlock, genesisBlock);
        
        // Verify the result is not null
        assertNotNull(result);
        
        // Verify each validation individually
        assertTrue(result.isPreviousHashValid(), "Previous hash validation failed");
        assertTrue(result.isBlockNumberValid(), "Block number validation failed");
        assertTrue(result.isHashIntegrityValid(), "Hash integrity validation failed");
        assertTrue(result.isSignatureValid(), "Signature validation failed");
        assertTrue(result.isAuthorizedKeyValid(), "Authorized key validation failed");
        
        // Verify that the overall validation is successful
        assertTrue(result.isValid(), "Overall validation failed");
        
        // Verify that there is no error message
        assertNull(result.getErrorMessage(), "Error message should be null for valid blocks");
    }
    
    /**
     * Test the detailed validation with a corrupted block
     */
    @Test
    void testValidateBlockDetailedWithCorruptedBlock() throws Exception {
        // Get the blocks from the blockchain
        List<Block> blocks = blockchain.getAllBlocks();
        Block genesisBlock = blocks.get(0);
        Block testBlock = blocks.get(1);
        
        // Create a corrupted copy of the test block
        Block corruptedBlock = new Block();
        corruptedBlock.setBlockNumber(testBlock.getBlockNumber());
        corruptedBlock.setPreviousHash("incorrect_previous_hash");
        corruptedBlock.setHash(testBlock.getHash());
        corruptedBlock.setData(testBlock.getData());
        corruptedBlock.setTimestamp(testBlock.getTimestamp());
        corruptedBlock.setSignature(testBlock.getSignature());
        corruptedBlock.setSignerPublicKey(testBlock.getSignerPublicKey());
        
        // Test validation of the corrupted block
        BlockValidationResult result = validateCommand.validateBlockDetailed(
            blockchain, corruptedBlock, genesisBlock);
        
        // Verify the validation failed
        assertFalse(result.isValid());
        
        // Verify the previous hash check failed
        assertFalse(result.isPreviousHashValid());
    }
    
    /**
     * Test the detailed validation with an unauthorized key
     */
    @Test
    void testValidateBlockDetailedWithUnauthorizedKey() throws Exception {
        // Get the blocks from the blockchain
        List<Block> blocks = blockchain.getAllBlocks();
        Block genesisBlock = blocks.get(0);
        Block testBlock = blocks.get(1);
        
        // Create a block with unauthorized key
        Block unauthorizedBlock = new Block();
        unauthorizedBlock.setBlockNumber(testBlock.getBlockNumber());
        unauthorizedBlock.setPreviousHash(testBlock.getPreviousHash());
        unauthorizedBlock.setHash(testBlock.getHash());
        unauthorizedBlock.setData(testBlock.getData());
        unauthorizedBlock.setTimestamp(testBlock.getTimestamp());
        unauthorizedBlock.setSignature(testBlock.getSignature());
        
        // Generate a different key that is not authorized
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair unauthorizedKeyPair = keyGen.generateKeyPair();
        String unauthorizedPublicKeyString = 
            com.rbatllet.blockchain.util.CryptoUtil.publicKeyToString(unauthorizedKeyPair.getPublic());
        
        unauthorizedBlock.setSignerPublicKey(unauthorizedPublicKeyString);
        
        // Test validation of the block with unauthorized key
        BlockValidationResult result = validateCommand.validateBlockDetailed(
            blockchain, unauthorizedBlock, genesisBlock);
        
        // The block should fail validation
        assertFalse(result.isValid());
        
        // The key authorization check should fail
        assertFalse(result.isAuthorizedKeyValid());
    }
    
    /**
     * Test the CLI command with detailed flag
     */
    @Test
    void testDetailedValidationOutput() {
        CommandLine cli = new CommandLine(validateCommand);
        int exitCode = cli.execute("--detailed");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        
        // Check for detailed validation output
        assertTrue(output.contains("Validation Details"));
        assertTrue(output.contains("Previous Hash:"));
        assertTrue(output.contains("Block Number:"));
        assertTrue(output.contains("Hash Integrity:"));
        assertTrue(output.contains("Digital Signature:"));
        assertTrue(output.contains("Key Authorization:"));
    }
}
