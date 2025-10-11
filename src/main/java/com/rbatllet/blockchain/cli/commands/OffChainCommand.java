package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.format.FormatUtil;
import com.rbatllet.blockchain.util.ExitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to manage off-chain data storage and retrieval
 */
@Command(name = "offchain", 
         description = "Manage off-chain data storage and retrieval")
public class OffChainCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(OffChainCommand.class);
    
    @Parameters(index = "0", arity = "0..1",
                description = "Operation: store, retrieve, list, analyze")
    String operation;
    
    @Option(names = {"-f", "--file"}, 
            description = "File path for off-chain storage")
    String filePath;
    
    @Option(names = {"-b", "--block-hash"}, 
            description = "Block hash to retrieve off-chain data from")
    String blockHash;
    
    @Option(names = {"-n", "--block-number"}, 
            description = "Block number to retrieve off-chain data from")
    Long blockNumber;
    
    @Option(names = {"--output"}, 
            description = "Output file path for retrieved data")
    String outputPath;
    
    @Option(names = {"--password"}, 
            description = "Password for encrypted off-chain data")
    String password;
    
    @Option(names = {"--category"}, 
            description = "Content category for off-chain data")
    String category;
    
    @Option(names = {"--identifier"}, 
            description = "Identifier for the off-chain data")
    String identifier;
    
    @Option(names = {"--stats"}, 
            description = "Show off-chain storage statistics")
    boolean stats = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output results in JSON format")
    boolean json = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    boolean verbose = false;
    
    private UserFriendlyEncryptionAPI encryptionAPI;
    
    @Override
    public void run() {
        try {
            verboseLog("Initializing off-chain command...");
            
            // Initialize blockchain and encryption API
            Blockchain blockchain = new Blockchain();
            encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);
            
            if (operation == null || operation.trim().isEmpty()) {
                operation = "list"; // Default operation
            }
            
            switch (operation.toLowerCase()) {
                case "store":
                    storeOffChainData(blockchain);
                    break;
                    
                case "retrieve":
                    retrieveOffChainData(blockchain);
                    break;
                    
                case "list":
                    listOffChainData(blockchain);
                    break;
                    
                case "analyze":
                    analyzeOffChainData(blockchain);
                    break;
                    
                case "stats":
                    showOffChainStatistics(blockchain);
                    break;
                    
                default:
                    BlockchainCLI.error("❌ Unknown operation: " + operation);
                    showOperationHelp();
                    ExitUtil.exit(1);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Off-chain operation failed: " + e.getMessage());
            logger.error("Off-chain command failed", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void storeOffChainData(Blockchain blockchain) throws Exception {
        if (filePath == null || filePath.trim().isEmpty()) {
            BlockchainCLI.error("❌ File path is required for store operation");
            BlockchainCLI.error("   Use --file to specify the file path");
            ExitUtil.exit(1);
        }
        
        Path file = Paths.get(filePath);
        if (!Files.exists(file)) {
            BlockchainCLI.error("❌ File does not exist: " + filePath);
            ExitUtil.exit(1);
        }
        
        verboseLog("Storing off-chain data from file: " + filePath);
        
        // Use enhanced block creation with off-chain storage
        UserFriendlyEncryptionAPI.BlockCreationOptions options = 
            new UserFriendlyEncryptionAPI.BlockCreationOptions()
                .withCategory(category)
                .withIdentifier(identifier)
                .withPassword(password)
                .withEncryption(password != null && !password.trim().isEmpty());
        
        // Create block with file content (this will trigger off-chain storage for large files)
        String fileContent = Files.readString(file);
        Block block = encryptionAPI.createBlockWithOptions(fileContent, options);
        
        if (block == null) {
            BlockchainCLI.error("❌ Failed to create block with off-chain data");
            ExitUtil.exit(1);
        }
        
        if (json) {
            outputStoreResultJson(block, filePath);
        } else {
            outputStoreResultText(block, filePath);
        }
    }
    
    private void retrieveOffChainData(Blockchain blockchain) throws Exception {
        Block block = null;
        
        if (blockHash != null && !blockHash.trim().isEmpty()) {
            verboseLog("Retrieving block by hash: " + blockHash);
            block = blockchain.getBlockByHash(blockHash);
        } else if (blockNumber != null) {
            verboseLog("Retrieving block by number: " + blockNumber);
            block = blockchain.getBlock(blockNumber);
        } else {
            BlockchainCLI.error("❌ Block hash or block number is required for retrieve operation");
            BlockchainCLI.error("   Use --block-hash or --block-number");
            ExitUtil.exit(1);
        }
        
        if (block == null) {
            BlockchainCLI.error("❌ Block not found");
            ExitUtil.exit(1);
        }
        
        if (!block.hasOffChainData()) {
            BlockchainCLI.error("❌ Block does not contain off-chain data");
            ExitUtil.exit(1);
        }
        
        verboseLog("Found block with off-chain data");
        
        // Retrieve and optionally save off-chain data
        var offChainData = block.getOffChainData();
        
        if (outputPath != null && !outputPath.trim().isEmpty()) {
            // Save to specified output file
            Path outputFile = Paths.get(outputPath);
            
            // For encrypted content, we need password
            if (block.getIsEncrypted() != null && block.getIsEncrypted() && 
                (password == null || password.trim().isEmpty())) {
                BlockchainCLI.error("❌ Password required to retrieve encrypted off-chain data");
                BlockchainCLI.error("   Use --password to provide decryption password");
                ExitUtil.exit(1);
            }
            
            // Create directories if needed
            Files.createDirectories(outputFile.getParent());
            
            // For simplicity, we'll copy the original file content
            // In a real implementation, this would decrypt and retrieve the actual off-chain data
            String retrievedContent = block.getData(); // Simplified approach
            Files.writeString(outputFile, retrievedContent);
            
            verboseLog("Off-chain data saved to: " + outputPath);
        }
        
        if (json) {
            outputRetrieveResultJson(block, offChainData);
        } else {
            outputRetrieveResultText(block, offChainData);
        }
    }
    
    private void listOffChainData(Blockchain blockchain) {
        verboseLog("Listing all blocks with off-chain data...");
        
        List<Block> offChainBlocks = new ArrayList<>();
        long blockCount = blockchain.getBlockCount();
        
        for (long i = 1; i <= blockCount; i++) {
            Block block = blockchain.getBlock(i);
            if (block != null && block.hasOffChainData()) {
                offChainBlocks.add(block);
            }
        }
        
        if (json) {
            outputListJson(offChainBlocks);
        } else {
            outputListText(offChainBlocks);
        }
    }
    
    private void analyzeOffChainData(Blockchain blockchain) {
        verboseLog("Analyzing off-chain data usage...");

        List<Block> offChainBlocks = new ArrayList<>();
        long totalOffChainSize = 0;
        int encryptedOffChainCount = 0;
        long blockCount = blockchain.getBlockCount();

        for (long i = 1; i <= blockCount; i++) {
            Block block = blockchain.getBlock(i);
            if (block != null && block.hasOffChainData()) {
                offChainBlocks.add(block);
                var offChainData = block.getOffChainData();
                if (offChainData != null) {
                    totalOffChainSize += offChainData.getFileSize();
                }
                if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
                    encryptedOffChainCount++;
                }
            }
        }
        
        if (json) {
            outputAnalysisJson(offChainBlocks, totalOffChainSize, encryptedOffChainCount, blockCount);
        } else {
            outputAnalysisText(offChainBlocks, totalOffChainSize, encryptedOffChainCount, blockCount);
        }
    }
    
    private void showOffChainStatistics(Blockchain blockchain) {
        if (blockchain == null) {
            throw new IllegalArgumentException("Blockchain cannot be null");
        }

        verboseLog("Generating off-chain statistics...");

        System.out.println("💾 Off-Chain Storage Statistics");
        System.out.println(FormatUtil.createSeparator(60));

        List<Block> offChainBlocks = new ArrayList<>();
        long totalSize = 0;
        int encryptedCount = 0;
        long blockCount = blockchain.getBlockCount();

        for (long i = 1; i <= blockCount; i++) {
            Block block = blockchain.getBlock(i);
            if (block != null && block.hasOffChainData()) {
                offChainBlocks.add(block);
                var offChainData = block.getOffChainData();
                if (offChainData != null) {
                    totalSize += offChainData.getFileSize();
                }
                if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
                    encryptedCount++;
                }
            }
        }
        
        System.out.println("📊 Storage Statistics:");
        System.out.println("   📦 Total blocks: " + blockCount);
        System.out.println("   💾 Off-chain blocks: " + offChainBlocks.size());
        System.out.println("   🔐 Encrypted off-chain: " + encryptedCount);
        System.out.println("   📝 Unencrypted off-chain: " + (offChainBlocks.size() - encryptedCount));
        System.out.println("   💽 Total off-chain size: " + FormatUtil.formatBytes(totalSize));
        
        if (blockCount > 0) {
            double offChainRate = (double) offChainBlocks.size() / blockCount * 100;
            System.out.println("   📈 Off-chain usage rate: " + FormatUtil.formatPercentage(offChainRate));
        }
        
        if (offChainBlocks.size() > 0) {
            long avgSize = totalSize / offChainBlocks.size();
            System.out.println("   📏 Average off-chain size: " + FormatUtil.formatBytes(avgSize));
            
            double encryptionRate = (double) encryptedCount / offChainBlocks.size() * 100;
            System.out.println("   🔐 Off-chain encryption rate: " + FormatUtil.formatPercentage(encryptionRate));
        }
    }
    
    private void showOperationHelp() {
        System.out.println("   Available operations:");
        System.out.println("   • store: Store file as off-chain data in new block");
        System.out.println("   • retrieve: Retrieve off-chain data from existing block");
        System.out.println("   • list: List all blocks with off-chain data");
        System.out.println("   • analyze: Analyze off-chain data usage patterns");
        System.out.println("   • stats: Show off-chain storage statistics");
        System.out.println();
        System.out.println("   Examples:");
        System.out.println("   blockchain offchain store --file document.pdf --category DOCUMENT");
        System.out.println("   blockchain offchain retrieve --block-hash abc123 --output retrieved.pdf");
        System.out.println("   blockchain offchain list --json");
        System.out.println("   blockchain offchain stats");
    }
    
    private void outputStoreResultText(Block block, String originalFile) {
        System.out.println("✅ Off-Chain Data Stored Successfully");
        System.out.println("=".repeat(60));
        
        System.out.println("📦 Block #" + block.getBlockNumber());
        System.out.println("📅 Timestamp: " + FormatUtil.formatTimestamp(block.getTimestamp()));
        System.out.println("🔗 Hash: " + FormatUtil.truncateHash(block.getHash()));
        System.out.println("📁 Original File: " + originalFile);
        
        if (block.hasOffChainData()) {
            var offChainData = block.getOffChainData();
            System.out.println("💾 Off-chain Size: " + FormatUtil.formatBytes(offChainData.getFileSize()));
            System.out.println("🔐 Content Type: " + offChainData.getContentType());
            System.out.println("🔐 Encrypted: " + (block.getIsEncrypted() != null && block.getIsEncrypted() ? "Yes" : "No"));
        }
    }
    
    private void outputStoreResultJson(Block block, String originalFile) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (originalFile == null) {
            throw new IllegalArgumentException("Original file cannot be null");
        }

        System.out.println("{");
        System.out.println("  \"operation\": \"store\",");
        System.out.println("  \"success\": true,");
        System.out.println("  \"blockNumber\": " + block.getBlockNumber() + ",");
        System.out.println("  \"blockHash\": \"" + FormatUtil.escapeJson(block.getHash() != null ? block.getHash() : "") + "\",");
        System.out.println("  \"originalFile\": \"" + FormatUtil.escapeJson(originalFile) + "\",");
        System.out.println("  \"encrypted\": " + (block.getIsEncrypted() != null ? block.getIsEncrypted() : false) + ",");

        if (block.hasOffChainData()) {
            var offChainData = block.getOffChainData();
            if (offChainData != null) {
                System.out.println("  \"offChainData\": {");
                System.out.println("    \"fileSize\": " + offChainData.getFileSize() + ",");
                System.out.println("    \"contentType\": \"" + FormatUtil.escapeJson(offChainData.getContentType() != null ? offChainData.getContentType() : "") + "\"");
                System.out.println("  },");
            }
        }

        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputRetrieveResultText(Block block, Object offChainData) {
        System.out.println("📥 Off-Chain Data Retrieved");
        System.out.println("=".repeat(60));
        
        System.out.println("📦 Block #" + block.getBlockNumber());
        System.out.println("📅 Timestamp: " + FormatUtil.formatTimestamp(block.getTimestamp()));
        System.out.println("🔗 Hash: " + FormatUtil.truncateHash(block.getHash()));
        
        if (block.hasOffChainData()) {
            var data = block.getOffChainData();
            System.out.println("💾 File Size: " + FormatUtil.formatBytes(data.getFileSize()));
            System.out.println("🔐 Content Type: " + data.getContentType());
            System.out.println("📁 File Path: " + data.getFilePath());
        }
        
        if (outputPath != null) {
            System.out.println("💾 Saved to: " + outputPath);
        }
    }
    
    private void outputRetrieveResultJson(Block block, Object offChainData) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }

        System.out.println("{");
        System.out.println("  \"operation\": \"retrieve\",");
        System.out.println("  \"success\": true,");
        System.out.println("  \"blockNumber\": " + block.getBlockNumber() + ",");
        System.out.println("  \"blockHash\": \"" + FormatUtil.escapeJson(block.getHash() != null ? block.getHash() : "") + "\",");

        if (block.hasOffChainData()) {
            var data = block.getOffChainData();
            if (data != null) {
                System.out.println("  \"offChainData\": {");
                System.out.println("    \"fileSize\": " + data.getFileSize() + ",");
                System.out.println("    \"contentType\": \"" + FormatUtil.escapeJson(data.getContentType() != null ? data.getContentType() : "") + "\",");
                System.out.println("    \"filePath\": \"" + FormatUtil.escapeJson(data.getFilePath() != null ? data.getFilePath() : "") + "\"");
                System.out.println("  },");
            }
        }

        if (outputPath != null) {
            System.out.println("  \"outputPath\": \"" + FormatUtil.escapeJson(outputPath) + "\",");
        }

        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputListText(List<Block> offChainBlocks) {
        if (offChainBlocks == null) {
            throw new IllegalArgumentException("Off-chain blocks list cannot be null");
        }

        System.out.println("💾 Off-Chain Data Blocks");
        System.out.println("=".repeat(60));

        if (offChainBlocks.isEmpty()) {
            System.out.println("ℹ️  No blocks with off-chain data found.");
            return;
        }

        System.out.println("Found " + offChainBlocks.size() + " block(s) with off-chain data:");
        System.out.println();

        for (Block block : offChainBlocks) {
            if (block == null) {
                continue; // Skip null blocks
            }

            System.out.println("📦 Block #" + block.getBlockNumber());
            System.out.println("   📅 " + FormatUtil.formatTimestamp(block.getTimestamp()));
            System.out.println("   🔗 " + FormatUtil.truncateHash(block.getHash()));

            if (block.hasOffChainData()) {
                var data = block.getOffChainData();
                if (data != null) {
                    System.out.println("   💾 Size: " + FormatUtil.formatBytes(data.getFileSize()));
                    System.out.println("   🔐 Type: " + (data.getContentType() != null ? data.getContentType() : "Unknown"));
                    System.out.println("   🔒 Encrypted: " + (block.getIsEncrypted() != null && block.getIsEncrypted() ? "Yes" : "No"));
                }
            }
            System.out.println();
        }
    }
    
    private void outputListJson(List<Block> offChainBlocks) {
        if (offChainBlocks == null) {
            throw new IllegalArgumentException("Off-chain blocks list cannot be null");
        }

        System.out.println("{");
        System.out.println("  \"operation\": \"list\",");
        System.out.println("  \"totalBlocks\": " + offChainBlocks.size() + ",");
        System.out.println("  \"blocks\": [");

        for (int i = 0; i < offChainBlocks.size(); i++) {
            Block block = offChainBlocks.get(i);
            if (block == null) {
                continue; // Skip null blocks
            }

            System.out.println("    {");
            System.out.println("      \"blockNumber\": " + block.getBlockNumber() + ",");
            System.out.println("      \"timestamp\": \"" + FormatUtil.escapeJson(block.getTimestamp() != null ? block.getTimestamp().toString() : "") + "\",");
            System.out.println("      \"hash\": \"" + FormatUtil.escapeJson(block.getHash() != null ? block.getHash() : "") + "\",");
            System.out.println("      \"encrypted\": " + (block.getIsEncrypted() != null ? block.getIsEncrypted() : false));

            if (block.hasOffChainData()) {
                var data = block.getOffChainData();
                if (data != null) {
                    System.out.println("      ,\"offChainData\": {");
                    System.out.println("        \"fileSize\": " + data.getFileSize() + ",");
                    System.out.println("        \"contentType\": \"" + FormatUtil.escapeJson(data.getContentType() != null ? data.getContentType() : "") + "\"");
                    System.out.println("      }");
                }
            }

            System.out.print("    }");
            if (i < offChainBlocks.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }

        System.out.println("  ],");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputAnalysisText(List<Block> offChainBlocks, long totalSize, int encryptedCount, long totalBlocks) {
        System.out.println("🔍 Off-Chain Data Analysis");
        System.out.println("=".repeat(60));
        
        System.out.println("📊 Analysis Summary:");
        System.out.println("   📦 Total blockchain blocks: " + totalBlocks);
        System.out.println("   💾 Off-chain blocks: " + offChainBlocks.size());
        System.out.println("   🔐 Encrypted off-chain: " + encryptedCount);
        System.out.println("   💽 Total off-chain size: " + FormatUtil.formatBytes(totalSize));
        
        if (totalBlocks > 0) {
            double offChainRate = (double) offChainBlocks.size() / totalBlocks * 100;
            System.out.println("   📈 Off-chain usage: " + FormatUtil.formatPercentage(offChainRate));
        }
        
        if (offChainBlocks.size() > 0) {
            double encryptionRate = (double) encryptedCount / offChainBlocks.size() * 100;
            System.out.println("   🔒 Off-chain encryption: " + FormatUtil.formatPercentage(encryptionRate));
            
            long avgSize = totalSize / offChainBlocks.size();
            System.out.println("   📏 Average file size: " + FormatUtil.formatBytes(avgSize));
        }
        
        System.out.println();
        System.out.println("💡 Recommendations:");
        if (offChainBlocks.size() == 0) {
            System.out.println("   • Consider using off-chain storage for large files");
        } else {
            if ((double) encryptedCount / offChainBlocks.size() < 0.8) {
                System.out.println("   • Consider encrypting more off-chain data for security");
            }
            if (totalSize > 1024 * 1024 * 100) { // > 100MB
                System.out.println("   • Monitor off-chain storage size for performance");
            }
        }
    }
    
    private void outputAnalysisJson(List<Block> offChainBlocks, long totalSize, int encryptedCount, long totalBlocks) {
        System.out.println("{");
        System.out.println("  \"operation\": \"analyze\",");
        System.out.println("  \"totalBlocks\": " + totalBlocks + ",");
        System.out.println("  \"offChainBlocks\": " + offChainBlocks.size() + ",");
        System.out.println("  \"encryptedOffChain\": " + encryptedCount + ",");
        System.out.println("  \"totalOffChainSize\": " + totalSize + ",");
        
        if (totalBlocks > 0) {
            double offChainRate = (double) offChainBlocks.size() / totalBlocks;
            System.out.println("  \"offChainUsageRate\": " + String.format("%.3f", offChainRate) + ",");
        }
        
        if (offChainBlocks.size() > 0) {
            double encryptionRate = (double) encryptedCount / offChainBlocks.size();
            long avgSize = totalSize / offChainBlocks.size();
            System.out.println("  \"offChainEncryptionRate\": " + String.format("%.3f", encryptionRate) + ",");
            System.out.println("  \"averageFileSize\": " + avgSize + ",");
        }
        
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
        logger.debug("🔍 {}", message);
    }
}