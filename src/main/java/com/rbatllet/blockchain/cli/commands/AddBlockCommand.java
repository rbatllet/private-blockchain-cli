package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.security.KeyPair;

/**
 * Command to add a new block to the blockchain
 */
@Command(name = "add-block", 
         description = "Add a new block to the blockchain")
public class AddBlockCommand implements Runnable {
    
    @Parameters(index = "0", 
                description = "Data content for the new block")
    String data;
    
    @Option(names = {"-s", "--signer"}, 
            description = "Name of the key signer (must be authorized)")
    String signerName;
    
    @Option(names = {"-k", "--key-file"}, 
            description = "Path to private key file")
    String keyFilePath;
    
    @Option(names = {"-g", "--generate-key"}, 
            description = "Generate a new key pair for signing")
    boolean generateKey = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Adding new block to blockchain...");
            
            // Validate data length
            if (data == null || data.trim().isEmpty()) {
                BlockchainCLI.error("Block data cannot be empty");
                System.exit(1);
            }
            
            Blockchain blockchain = new Blockchain();
            
            // Check data length limits (we'll use simple length check)
            if (data.length() > blockchain.getMaxBlockDataLength()) {
                BlockchainCLI.error("Block data exceeds maximum length limit");
                System.exit(1);
            }
            
            // Handle key generation or loading
            KeyPair keyPair;
            PublicKey publicKey;
            PrivateKey privateKey;
            
            if (generateKey) {
                BlockchainCLI.verbose("Generating new key pair...");
                keyPair = CryptoUtil.generateKeyPair();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
                
                // Authorize the generated key automatically
                String publicKeyString = CryptoUtil.publicKeyToString(publicKey);
                String autoOwnerName = "CLI-Generated-" + System.currentTimeMillis();
                
                // FIXED: Create authorized key with timestamp before block creation
                // to ensure temporal consistency and avoid race condition issues
                LocalDateTime keyCreationTime = LocalDateTime.now().minusSeconds(1);
                
                if (blockchain.addAuthorizedKey(publicKeyString, autoOwnerName, keyCreationTime)) {
                    BlockchainCLI.info("Generated new key pair for signing");
                    BlockchainCLI.info("Automatically authorized new key as: " + autoOwnerName);
                    System.out.println("🔑 Public Key: " + publicKeyString);
                } else {
                    BlockchainCLI.error("Failed to authorize generated key");
                    System.exit(1);
                }
            } else {
                // For now, we'll generate a temporary key pair
                // In a real implementation, we would load from file or authorized keys
                BlockchainCLI.verbose("Using temporary key pair for demonstration...");
                keyPair = CryptoUtil.generateKeyPair();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
            }
            
            // Add the block
            boolean success = blockchain.addBlock(data, privateKey, publicKey);
            
            if (success) {
                long blockCount = blockchain.getBlockCount();
                
                if (json) {
                    outputJson(true, blockCount, data);
                } else {
                    BlockchainCLI.success("Block added successfully!");
                    System.out.println("📦 Block number: " + blockCount);
                    System.out.println("📝 Data: " + data);
                    System.out.println("🔗 Total blocks in chain: " + blockCount);
                }
            } else {
                if (json) {
                    outputJson(false, blockchain.getBlockCount(), data);
                } else {
                    BlockchainCLI.error("Failed to add block to blockchain");
                }
                System.exit(1);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("Failed to add block: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    private void outputJson(boolean success, long totalBlocks, String data) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"blockNumber\": " + totalBlocks + ",");
        System.out.println("  \"data\": \"" + data.replace("\"", "\\\"") + "\",");
        System.out.println("  \"totalBlocks\": " + totalBlocks + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
