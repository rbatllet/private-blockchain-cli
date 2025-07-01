package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.security.SecureKeyStorage;
import com.rbatllet.blockchain.security.PasswordUtil;
import com.rbatllet.blockchain.security.KeyFileLoader;
import com.rbatllet.blockchain.security.ECKeyDerivation;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.util.ExitUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Command to add a new block to the blockchain
 */
@Command(name = "add-block", 
         description = "Add a new block to the blockchain")
public class AddBlockCommand implements Runnable {
    
    @Parameters(index = "0", arity = "0..1",
                description = "Data content for the new block")
    String data;
    
    @Option(names = {"-f", "--file"}, 
            description = "Read block content from file instead of command line parameter")
    String inputFile;
    
    @Option(names = {"-s", "--signer"}, 
            description = "Name of the key signer (must be authorized). If no private key is stored for this signer,\n" +
                          "                  a demo mode will be activated that creates a temporary key for demonstration purposes.\n" +
                          "                  Use 'add-key <signer-name> --generate --store-private' to store a permanent key.")
    String signerName;
    
    @Option(names = {"-k", "--key-file"}, 
            description = "Path to private key file (PEM/DER/Base64 formats supported)")
    String keyFilePath;
    
    @Option(names = {"-g", "--generate-key"}, 
            description = "Generate a new key pair for signing")
    boolean generateKey = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output with detailed information")
    boolean verbose = false;
    
    @Option(names = {"-w", "--keywords"}, 
            description = "Manual keywords for search indexing (comma-separated)")
    String keywords;
    
    @Option(names = {"-c", "--category"}, 
            description = "Content category (e.g., MEDICAL, FINANCE, TECHNICAL, LEGAL)")
    String category;
    
    @Override
    public void run() {
        try {
            verboseLog("Adding new block to blockchain...");
            
            // Get block content either from command line or file
            String blockContent = getBlockContent();
            
            // Validate data length
            if (blockContent == null || blockContent.trim().isEmpty()) {
                BlockchainCLI.error("❌ Block data cannot be empty");
                ExitUtil.exit(1);
            }
            
            Blockchain blockchain = new Blockchain();
            
            // Check data validation and storage decision
            verboseLog("Data size: " + blockContent.length() + " bytes");
            int storageDecision = blockchain.validateAndDetermineStorage(blockContent);
            if (storageDecision == 0) {
                verboseLog("Storage decision returned: 0 (invalid)");
                BlockchainCLI.error("❌ Block data validation failed. Data size: " + blockContent.length() + " bytes");
                BlockchainCLI.error("❌ Note: Data may exceed the current blockchain size limit (~32KB)");
                BlockchainCLI.info("📝 Tip: Try reducing data size or check if off-chain storage is properly configured");
                verboseLog("Storage decision: 0 indicates core blockchain validation failure");
                ExitUtil.exit(1);
            }
            verboseLog("Storage decision: " + storageDecision + " (1=on-chain, 2=off-chain)");
            
            // Inform user about storage decision
            if (storageDecision == 2) {
                verboseLog("Large data detected - will be stored off-chain with encryption");
            } else {
                verboseLog("Data will be stored on-chain");
            }
            
            // Handle key generation or loading
            KeyPair keyPair = null;
            PublicKey publicKey = null;
            PrivateKey privateKey = null;
            
            if (generateKey) {
                verboseLog("Generating new key pair...");
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
                    BlockchainCLI.error("❌ Failed to authorize generated key");
                    ExitUtil.exit(1);
                }
            } else if (signerName != null) {
                // Use existing authorized key by signer name
                verboseLog("Looking up authorized key for signer: " + signerName);
                
                var authorizedKey = blockchain.getAuthorizedKeyByOwner(signerName);
                if (authorizedKey == null) {
                    BlockchainCLI.error("❌ Signer '" + signerName + "' not found in authorized keys: Use 'blockchain list-keys' to see available signers");
                    ExitUtil.exit(1);
                }
                
                // Check if we have the private key stored securely
                if (SecureKeyStorage.hasPrivateKey(signerName)) {
                    verboseLog("Found stored private key for signer: " + signerName);
                    
                    String password = PasswordUtil.readPassword("🔐 Enter password for " + signerName + ": ");
                    if (password == null) {
                        BlockchainCLI.error("❌ Password input cancelled");
                        ExitUtil.exit(1);
                    }
                    
                    privateKey = SecureKeyStorage.loadPrivateKey(signerName, password);
                    if (privateKey == null) {
                        BlockchainCLI.error("❌ Failed to load private key: Wrong password?");
                        ExitUtil.exit(1);
                    }
                    
                    // Use the existing authorized public key
                    try {
                        publicKey = CryptoUtil.stringToPublicKey(authorizedKey.getPublicKey());
                        BlockchainCLI.info("✅ Using stored private key for signer: " + signerName);
                    } catch (Exception e) {
                        BlockchainCLI.error("❌ Failed to parse authorized public key for signer '" + signerName + "': " + e.getMessage());
                        ExitUtil.exit(1);
                    }
                } else {
                    // DEMO MODE: Activated when a signer is specified but no private key is stored for them
                    // This mode is intended for demonstration and learning purposes only
                    // In a real-world scenario, you should always store the private key securely
                    verboseLog("No stored private key found for signer: " + signerName);
                    BlockchainCLI.info("⚠️  DEMO MODE: No stored private key found for signer: " + signerName);
                    BlockchainCLI.info("💡 Use 'add-key " + signerName + " --generate --store-private' to store private key");
                    
                    // Generate a temporary key pair for this demonstration
                    keyPair = CryptoUtil.generateKeyPair();
                    publicKey = keyPair.getPublic();
                    privateKey = keyPair.getPrivate();
                    
                    // Add the temporary public key to the blockchain's authorized keys
                    // The key is backdated by 1 second to ensure it's valid for the current block
                    String tempPublicKeyString = CryptoUtil.publicKeyToString(publicKey);
                    LocalDateTime tempKeyTime = LocalDateTime.now().minusSeconds(1);
                    
                    if (blockchain.addAuthorizedKey(tempPublicKeyString, signerName + "-TempDemo", tempKeyTime)) {
                        BlockchainCLI.info("🔑 DEMO: Created temporary key for existing signer: " + signerName);
                        BlockchainCLI.info("🔑 DEMO: This simulates using the --signer functionality");
                        System.out.println("🔑 Temp Public Key: " + tempPublicKeyString);
                    } else {
                        BlockchainCLI.error("❌ Failed to create temporary demo key");
                        ExitUtil.exit(1);
                    }
                }
            } else if (keyFilePath != null) {
                // Load private key from file
                verboseLog("Loading private key from file: " + keyFilePath);
                
                // Validate key file path
                if (!KeyFileLoader.isValidKeyFilePath(keyFilePath)) {
                    BlockchainCLI.error("❌ Invalid key file path or file is not accessible: " + keyFilePath);
                    ExitUtil.exit(1);
                }
                
                // Detect and display file format for user info
                String format = KeyFileLoader.detectKeyFileFormat(keyFilePath);
                verboseLog("Detected key file format: " + format);
                
                // Load private key from file
                privateKey = KeyFileLoader.loadPrivateKeyFromFile(keyFilePath);
                if (privateKey == null) {
                    BlockchainCLI.error("❌ Failed to load private key from file: " + keyFilePath);
                    BlockchainCLI.error("   Supported formats: PEM (PKCS#8), DER, Base64");
                    BlockchainCLI.error("   For PEM files, use PKCS#8 format:");
                    BlockchainCLI.error("   openssl pkcs8 -topk8 -nocrypt -in ec_key.pem -out pkcs8_key.pem");
                    ExitUtil.exit(1);
                }
                
                // Derive public key from private key
                try {
                    // Use the ECKeyDerivation class from the core library
                    ECKeyDerivation keyDerivation = new ECKeyDerivation();
                    publicKey = keyDerivation.derivePublicKeyFromPrivate(privateKey);
                    BlockchainCLI.info("✅ Successfully loaded private key from file");
                    // These specific verbose messages are expected by the tests
                    if (verbose || BlockchainCLI.verbose) {
                        System.out.println("🔍 Key file: " + keyFilePath);
                        System.out.println("🔍 Format: " + format);
                    }
                } catch (Exception e) {
                    BlockchainCLI.error("❌ Failed to derive public key from private key: " + e.getMessage());
                    ExitUtil.exit(1);
                }
                
                // Check if this public key is authorized
                String publicKeyString = CryptoUtil.publicKeyToString(publicKey);
                var authorizedKeys = blockchain.getAuthorizedKeys();
                boolean isAuthorized = authorizedKeys.stream()
                    .anyMatch(key -> key.getPublicKey().equals(publicKeyString));
                
                if (!isAuthorized) {
                    BlockchainCLI.info("⚠️  Public key from file is not currently authorized");
                    BlockchainCLI.info("💡 Auto-authorizing key for this operation...");
                    
                    // Auto-authorize the key with file-based naming
                    String fileName = java.nio.file.Paths.get(keyFilePath).getFileName().toString();
                    String autoOwnerName = "KeyFile-" + fileName + "-" + System.currentTimeMillis();
                    
                    LocalDateTime keyCreationTime = LocalDateTime.now().minusSeconds(1);
                    
                    if (blockchain.addAuthorizedKey(publicKeyString, autoOwnerName, keyCreationTime)) {
                        BlockchainCLI.info("✅ Auto-authorized key from file as: " + autoOwnerName);
                        System.out.println("🔑 Public Key: " + publicKeyString);
                    } else {
                        BlockchainCLI.error("❌ Failed to authorize key from file");
                        ExitUtil.exit(1);
                    }
                } else {
                    BlockchainCLI.info("✅ Key from file is already authorized");
                }
            } else {
                // Default case: no signer specified, no key generation
                BlockchainCLI.error("❌ No signing method specified");
                BlockchainCLI.error("   Use one of the following options:");
                BlockchainCLI.error("   --generate-key: Generate a new key pair");
                BlockchainCLI.error("   --signer <n>: Use an existing authorized key");
                BlockchainCLI.error("   --key-file <path>: Load private key from file (PEM/DER/Base64 supported)");
                ExitUtil.exit(1);
            }
            
            // Verify that keys are properly initialized
            if (privateKey == null || publicKey == null) {
                BlockchainCLI.error("❌ Failed to initialize cryptographic keys");
                ExitUtil.exit(1);
            }
            
            // Add the block
            verboseLog("Attempting to add block with derived public key: " + 
                    CryptoUtil.publicKeyToString(publicKey));
            
            // Parse keywords if provided
            String[] keywordArray = null;
            if (keywords != null && !keywords.trim().isEmpty()) {
                keywordArray = keywords.split(",");
                // Trim whitespace from each keyword
                for (int i = 0; i < keywordArray.length; i++) {
                    keywordArray[i] = keywordArray[i].trim();
                }
                verboseLog("Using manual keywords: " + String.join(", ", keywordArray));
            }
            
            // Use category or default
            String blockCategory = (category != null && !category.trim().isEmpty()) ? category.trim().toUpperCase() : null;
            if (blockCategory != null) {
                verboseLog("Using content category: " + blockCategory);
            }
            
            // Add the block to the blockchain with enhanced functionality
            Block createdBlock;
            if (keywordArray != null || blockCategory != null) {
                createdBlock = blockchain.addBlockWithKeywords(blockContent, keywordArray, blockCategory, privateKey, publicKey);
            } else {
                // Use legacy method for backward compatibility
                boolean success = blockchain.addBlock(blockContent, privateKey, publicKey);
                createdBlock = success ? blockchain.getLastBlock() : null;
            }
            
            if (createdBlock != null) {
                long blockCount = blockchain.getBlockCount();
                
                if (json) {
                    outputJsonEnhanced(true, createdBlock, blockContent);
                } else {
                    // Always show success message regardless of verbose mode
                    // Use the exact message format expected by the tests
                    BlockchainCLI.success("Block added successfully!");
                    System.out.println("📦 Block number: " + createdBlock.getBlockNumber());
                    System.out.println("📝 Data: " + (createdBlock.getData().startsWith("OFF_CHAIN_REF:") ? "[Stored off-chain]" : blockContent));
                    
                    // Show search metadata if available
                    if (createdBlock.getManualKeywords() != null && !createdBlock.getManualKeywords().trim().isEmpty()) {
                        System.out.println("🏷️  Manual Keywords: " + createdBlock.getManualKeywords());
                    }
                    if (createdBlock.getAutoKeywords() != null && !createdBlock.getAutoKeywords().trim().isEmpty()) {
                        System.out.println("🤖 Auto Keywords: " + createdBlock.getAutoKeywords());
                    }
                    if (createdBlock.getContentCategory() != null) {
                        System.out.println("📂 Category: " + createdBlock.getContentCategory());
                    }
                    
                    // Show off-chain information if applicable
                    if (createdBlock.hasOffChainData()) {
                        var offChainData = createdBlock.getOffChainData();
                        System.out.println("💾 Off-chain storage: " + formatBytes(offChainData.getFileSize()));
                        System.out.println("🔐 Encrypted: Yes (AES-128-CBC)");
                    }
                    
                    System.out.println("🔗 Total blocks in chain: " + blockCount);
                }
            } else {
                if (json) {
                    outputJson(false, blockchain.getBlockCount(), blockContent);
                } else {
                    BlockchainCLI.error("❌ Failed to add block to blockchain");
                }
                ExitUtil.exit(1);
            }
            
        } catch (IOException e) {
            BlockchainCLI.error("❌ Failed to read input file: " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Failed to add block: Security error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Failed to add block: Runtime error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to add block: Unexpected error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    /**
     * Get block content either from command line parameter or file
     */
    private String getBlockContent() throws IOException {
        // Validate that only one input method is specified
        if (inputFile != null && data != null) {
            throw new IllegalArgumentException("Cannot specify both file input (-f/--file) and direct data input. Please use only one method.");
        }
        
        if (inputFile == null && data == null) {
            throw new IllegalArgumentException("Must specify either block data directly or use --file option to read from file.");
        }
        
        // Read from file if specified
        if (inputFile != null) {
            verboseLog("Reading block content from file: " + inputFile);
            
            Path filePath = Paths.get(inputFile);
            if (!Files.exists(filePath)) {
                throw new IOException("Input file does not exist: " + inputFile);
            }
            
            if (!Files.isReadable(filePath)) {
                throw new IOException("Input file is not readable: " + inputFile);
            }
            
            // Read file content
            byte[] fileBytes = Files.readAllBytes(filePath);
            String content = new String(fileBytes, "UTF-8");
            
            verboseLog("Successfully read " + fileBytes.length + " bytes from file");
            return content;
        }
        
        // Use command line data
        return data;
    }
    
    private void outputJson(boolean success, long totalBlocks, String data) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"blockNumber\": " + totalBlocks + ",");
        System.out.println("  \"data\": \"" + data.replace("\"", "\\\"") + "\",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputJsonEnhanced(boolean success, Block block, String originalData) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"blockNumber\": " + block.getBlockNumber() + ",");
        System.out.println("  \"hash\": \"" + block.getHash() + "\",");
        System.out.println("  \"data\": \"" + (block.getData().startsWith("OFF_CHAIN_REF:") ? "[Stored off-chain]" : originalData.replace("\"", "\\\"")) + "\",");
        
        if (block.getManualKeywords() != null) {
            System.out.println("  \"manualKeywords\": \"" + block.getManualKeywords().replace("\"", "\\\"") + "\",");
        }
        if (block.getAutoKeywords() != null) {
            System.out.println("  \"autoKeywords\": \"" + block.getAutoKeywords().replace("\"", "\\\"") + "\",");
        }
        if (block.getContentCategory() != null) {
            System.out.println("  \"category\": \"" + block.getContentCategory() + "\",");
        }
        
        System.out.println("  \"offChainStorage\": " + block.hasOffChainData() + ",");
        if (block.hasOffChainData()) {
            var offChainData = block.getOffChainData();
            System.out.println("  \"offChainSize\": " + offChainData.getFileSize() + ",");
            System.out.println("  \"encrypted\": true,");
        }
        
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Helper method for verbose logging
     * Uses local --verbose flag if set, otherwise falls back to global verbose setting
     */
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
    }
}
