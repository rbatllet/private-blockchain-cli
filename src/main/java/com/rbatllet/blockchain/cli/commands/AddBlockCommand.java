package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.security.SecureKeyStorage;
import com.rbatllet.blockchain.cli.security.PasswordUtil;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.cli.util.ExitUtil;

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
                ExitUtil.exit(1);
            }
            
            Blockchain blockchain = new Blockchain();
            
            // Check data length limits (we'll use simple length check)
            if (data.length() > blockchain.getMaxBlockDataLength()) {
                BlockchainCLI.error("Block data exceeds maximum length limit");
                ExitUtil.exit(1);
            }
            
            // Handle key generation or loading
            KeyPair keyPair = null;
            PublicKey publicKey = null;
            PrivateKey privateKey = null;
            
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
                    ExitUtil.exit(1);
                }
            } else if (signerName != null) {
                // Use existing authorized key by signer name
                BlockchainCLI.verbose("Looking up authorized key for signer: " + signerName);
                
                var authorizedKey = blockchain.getAuthorizedKeyByOwner(signerName);
                if (authorizedKey == null) {
                    BlockchainCLI.error("Signer '" + signerName + "' not found in authorized keys");
                    BlockchainCLI.error("Use 'blockchain list-keys' to see available signers");
                    ExitUtil.exit(1);
                }
                
                // Check if we have the private key stored securely
                if (SecureKeyStorage.hasPrivateKey(signerName)) {
                    BlockchainCLI.verbose("Found stored private key for signer: " + signerName);
                    
                    String password = PasswordUtil.readPassword("🔐 Enter password for " + signerName + ": ");
                    if (password == null) {
                        BlockchainCLI.error("Password input cancelled");
                        ExitUtil.exit(1);
                    }
                    
                    privateKey = SecureKeyStorage.loadPrivateKey(signerName, password);
                    if (privateKey == null) {
                        BlockchainCLI.error("Failed to load private key (wrong password?)");
                        ExitUtil.exit(1);
                    }
                    
                    // Use the existing authorized public key
                    try {
                        publicKey = CryptoUtil.stringToPublicKey(authorizedKey.getPublicKey());
                        BlockchainCLI.info("✅ Using stored private key for signer: " + signerName);
                    } catch (Exception e) {
                        BlockchainCLI.error("Failed to parse authorized public key for signer: " + signerName);
                        BlockchainCLI.error("Error: " + e.getMessage());
                        ExitUtil.exit(1);
                    }
                } else {
                    // Fallback to demo mode if no private key is stored
                    BlockchainCLI.verbose("No stored private key found for signer: " + signerName);
                    BlockchainCLI.info("⚠️  DEMO MODE: No stored private key found for signer: " + signerName);
                    BlockchainCLI.info("💡 Use 'add-key " + signerName + " --generate --store-private' to store private key");
                    
                    keyPair = CryptoUtil.generateKeyPair();
                    publicKey = keyPair.getPublic();
                    privateKey = keyPair.getPrivate();
                    
                    // Update the authorized key with the new temporary public key for this demo
                    String tempPublicKeyString = CryptoUtil.publicKeyToString(publicKey);
                    LocalDateTime tempKeyTime = LocalDateTime.now().minusSeconds(1);
                    
                    if (blockchain.addAuthorizedKey(tempPublicKeyString, signerName + "-TempDemo", tempKeyTime)) {
                        BlockchainCLI.info("🔑 DEMO: Created temporary key for existing signer: " + signerName);
                        BlockchainCLI.info("🔑 DEMO: This simulates using the --signer functionality");
                        System.out.println("🔑 Temp Public Key: " + tempPublicKeyString);
                    } else {
                        BlockchainCLI.error("Failed to create temporary demo key");
                        ExitUtil.exit(1);
                    }
                }
            } else if (keyFilePath != null) {
                // Load private key from file
                BlockchainCLI.error("--key-file functionality is not yet implemented");
                BlockchainCLI.error("Use --generate-key to create a new key pair for now");
                ExitUtil.exit(1);
            } else {
                // Default case: no signer specified, no key generation
                BlockchainCLI.error("No signing method specified");
                BlockchainCLI.error("Use one of the following options:");
                BlockchainCLI.error("  --generate-key: Generate a new key pair");
                BlockchainCLI.error("  --signer <name>: Use an existing authorized key");
                BlockchainCLI.error("  --key-file <path>: Load private key from file (not yet implemented)");
                ExitUtil.exit(1);
            }
            
            // Verify that keys are properly initialized
            if (privateKey == null || publicKey == null) {
                BlockchainCLI.error("Failed to initialize cryptographic keys");
                ExitUtil.exit(1);
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
                ExitUtil.exit(1);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("Failed to add block: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
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
