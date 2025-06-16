package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
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
        // Configurar la salida estándar para capturar la salida
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Configurar el directorio temporal para los datos de la blockchain
        System.setProperty("user.dir", tempDir.toString());
        
        // Inicializar los comandos y la blockchain
        validateCommand = new ValidateCommand();
        blockchain = new Blockchain();
        
        // Ensure we use a clean blockchain instance
        // Configure a unique temporary directory for each test
        System.setProperty("blockchain.data.dir", tempDir.toString());
        // Create a new instance that will use this clean directory
        blockchain = new Blockchain();
        // Clean and reinitialize the blockchain
        blockchain.clearAndReinitialize();
        
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
        // Restaurar la salida estándar
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Limpiar las propiedades del sistema
        System.clearProperty("user.dir");
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
        
        // Test validation of the test block against genesis block
        ValidateCommand.BlockValidationResult result = validateCommand.validateBlockDetailed(
            blockchain, testBlock, genesisBlock);
        
        // Verify the result is not null
        assertNotNull(result);
        
        // Verify the validation was successful
        assertTrue(result.isValid());
        
        // Verify all individual checks passed
        assertTrue(result.isPreviousHashValid());
        assertTrue(result.isBlockNumberValid());
        assertTrue(result.isHashIntegrityValid());
        assertTrue(result.isSignatureValid());
        assertTrue(result.isAuthorizedKeyValid());
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
        ValidateCommand.BlockValidationResult result = validateCommand.validateBlockDetailed(
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
        ValidateCommand.BlockValidationResult result = validateCommand.validateBlockDetailed(
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
