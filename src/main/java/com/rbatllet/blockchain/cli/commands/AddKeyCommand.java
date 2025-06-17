package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.security.SecureKeyStorage;
import com.rbatllet.blockchain.security.PasswordUtil;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * Command to add an authorized key to the blockchain
 */
@Command(name = "add-key", 
         description = "Add an authorized key to the blockchain")
public class AddKeyCommand implements Runnable {
    
    @Parameters(index = "0", 
                description = "Owner name for the key")
    String ownerName;
    
    @Option(names = {"-k", "--public-key"}, 
            description = "Public key string (if not provided, will generate new key pair)")
    String publicKeyString;
    
    @Option(names = {"-g", "--generate"}, 
            description = "Generate a new key pair")
    boolean generateKey = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"--show-private"}, 
            description = "Show private key when generating (CAUTION: Keep secure!)")
    boolean showPrivateKey = false;
    
    @Option(names = {"--store-private"}, 
            description = "Store private key securely for use with --signer")
    boolean storePrivateKey = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Adding authorized key...");
            
            // Validate owner name
            if (ownerName == null || ownerName.trim().isEmpty()) {
                BlockchainCLI.error("❌ Owner name cannot be empty");
                ExitUtil.exit(1);
            }
            
            Blockchain blockchain = new Blockchain();
            String finalPublicKey;
            String privateKeyForDisplay = null;
            
            if (generateKey || publicKeyString == null) {
                // Generate new key pair
                BlockchainCLI.verbose("Generating new key pair for owner: " + ownerName);
                KeyPair keyPair = CryptoUtil.generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                finalPublicKey = CryptoUtil.publicKeyToString(publicKey);
                
                if (showPrivateKey) {
                    privateKeyForDisplay = CryptoUtil.privateKeyToString(keyPair.getPrivate());
                }
                
                // Store private key securely if requested
                if (storePrivateKey) {
                    BlockchainCLI.verbose("Storing private key securely...");
                    
                    String password = PasswordUtil.readPasswordWithConfirmation("🔐 Enter password to protect private key: ");
                    if (password == null) {
                        BlockchainCLI.error("❌ Password input cancelled");
                        ExitUtil.exit(1);
                    }
                    
                    if (!PasswordUtil.isValidPassword(password)) {
                        ExitUtil.exit(1);
                    }
                    
                    if (SecureKeyStorage.savePrivateKey(ownerName, keyPair.getPrivate(), password)) {
                        BlockchainCLI.info("🔒 Private key stored securely for: " + ownerName);
                        BlockchainCLI.info("💡 You can now use --signer " + ownerName + " with add-block command");
                    } else {
                        BlockchainCLI.error("❌ Failed to store private key");
                        ExitUtil.exit(1);
                    }
                }
                
                BlockchainCLI.verbose("Generated key pair successfully");
            } else {
                // Use provided public key
                finalPublicKey = publicKeyString;
                BlockchainCLI.verbose("Using provided public key");
                
                if (storePrivateKey) {
                    BlockchainCLI.error("❌ Cannot store private key when using provided public key: Use --generate with --store-private to generate and store a key pair");
                    ExitUtil.exit(1);
                }
            }
            
            // Add the authorized key
            boolean success = blockchain.addAuthorizedKey(finalPublicKey, ownerName);
            
            if (success) {
                if (json) {
                    outputJson(true, ownerName, finalPublicKey, privateKeyForDisplay);
                } else {
                    BlockchainCLI.success("Authorized key added successfully!");
                    System.out.println("👤 Owner: " + ownerName);
                    System.out.println("🔑 Public Key: " + finalPublicKey);
                    
                    if (privateKeyForDisplay != null) {
                        System.out.println();
                        System.out.println("⚠️  PRIVATE KEY (KEEP SECURE!):");
                        System.out.println("🔐 " + privateKeyForDisplay);
                        System.out.println();
                        System.out.println("⚠️  WARNING: Store this private key securely!");
                        System.out.println("   You will need it to sign blocks.");
                    }
                    
                    // Show updated count
                    int totalKeys = blockchain.getAuthorizedKeys().size();
                    System.out.println("📊 Total authorized keys: " + totalKeys);
                }
            } else {
                if (json) {
                    outputJson(false, ownerName, finalPublicKey, null);
                } else {
                    BlockchainCLI.error("❌ Failed to add authorized key: Key may already exist");
                }
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Failed to add authorized key: Security error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Failed to add authorized key: Runtime error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to add authorized key: Unexpected error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void outputJson(boolean success, String owner, String publicKey, String privateKey) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"owner\": \"" + owner + "\",");
        System.out.println("  \"publicKey\": \"" + publicKey + "\",");
        if (privateKey != null) {
            System.out.println("  \"privateKey\": \"" + privateKey + "\",");
        }
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
