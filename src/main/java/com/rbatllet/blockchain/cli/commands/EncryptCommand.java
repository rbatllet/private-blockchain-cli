package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.format.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Command to analyze and display information about blockchain encryption
 */
@Command(name = "encrypt", 
         description = "Analyze blockchain encryption and encrypted blocks")
public class EncryptCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(EncryptCommand.class);
    
    @Parameters(index = "0", arity = "0..1",
                description = "Search term to find blocks to analyze (optional)")
    String searchTerm;
    
    @Option(names = {"-u", "--username"}, 
            description = "Username to analyze encrypted blocks for")
    String username;
    
    @Option(names = {"--password"}, 
            description = "Password for decrypting content")
    String password;
    
    @Option(names = {"--stats"}, 
            description = "Show encryption statistics")
    boolean stats = false;
    
    @Option(names = {"--encrypted-only"}, 
            description = "Show only encrypted blocks")
    boolean encryptedOnly = false;
    
    @Option(names = {"--validate"}, 
            description = "Validate encrypted blocks integrity")
    boolean validate = false;
    
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
            verboseLog("Initializing encryption analysis...");
            
            // Initialize blockchain and encryption API
            Blockchain blockchain = new Blockchain();
            encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);
            
            if (stats) {
                showEncryptionStatistics();
                return;
            }
            
            if (validate) {
                validateEncryptedBlocks();
                return;
            }
            
            // Search for blocks to analyze
            List<Block> blocksToAnalyze = new ArrayList<>();
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                verboseLog("Searching for blocks with term: " + searchTerm);
                
                if (password != null && !password.trim().isEmpty()) {
                    blocksToAnalyze = encryptionAPI.findAndDecryptData(searchTerm, password);
                } else {
                    blocksToAnalyze = encryptionAPI.findEncryptedData(searchTerm);
                }
                
            } else {
                // Get all blocks from blockchain
                verboseLog("Analyzing all blockchain blocks");
                long blockCount = blockchain.getBlockCount();
                
                for (long i = 1; i <= blockCount; i++) {
                    Block block = blockchain.getBlock(i);
                    if (block != null) {
                        if (!encryptedOnly || (block.getIsEncrypted() != null && block.getIsEncrypted())) {
                            blocksToAnalyze.add(block);
                        }
                    }
                }
            }
            
            if (blocksToAnalyze.isEmpty()) {
                BlockchainCLI.info("ℹ️  No blocks found matching the specified criteria");
                return;
            }
            
            verboseLog("Found " + blocksToAnalyze.size() + " blocks to analyze");
            
            // Output results
            if (json) {
                outputJson(blocksToAnalyze);
            } else {
                outputText(blocksToAnalyze);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Encryption analysis failed: " + e.getMessage());
            logger.error("Encryption command failed", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void showEncryptionStatistics() {
        verboseLog("Generating encryption statistics...");
        
        System.out.println("🔐 Blockchain Encryption Statistics");
        System.out.println(FormatUtil.createSeparator(60));
        
        try {
            // Use enhanced encryption analysis
            UserFriendlyEncryptionAPI.EncryptionAnalysis analysis = encryptionAPI.analyzeEncryption();
            
            System.out.println("📊 Block Statistics:");
            System.out.println("   📦 Total blocks: " + analysis.getTotalBlocks());
            System.out.println("   🔐 Encrypted blocks: " + analysis.getEncryptedBlocks());
            System.out.println("   📝 Unencrypted blocks: " + analysis.getUnencryptedBlocks());
            System.out.println("   💾 Off-chain blocks: " + analysis.getOffChainBlocks());
            System.out.println("   📈 Encryption rate: " + FormatUtil.formatPercentage(analysis.getEncryptionRate()));
            
            System.out.println();
            System.out.println("🔍 Analysis completed: " + FormatUtil.formatTimestamp(analysis.getAnalysisTime()));
            
            // Show category breakdown if available
            var categoryBreakdown = analysis.getCategoryBreakdown();
            if (!categoryBreakdown.isEmpty()) {
                System.out.println("\n📂 Category Breakdown:");
                categoryBreakdown.forEach((category, count) -> 
                    System.out.println("   " + category + ": " + count + " blocks"));
            }
            
            // Get blockchain summary
            String summary = encryptionAPI.getBlockchainSummary();
            System.out.println("\n📋 Summary:");
            System.out.println(summary);
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to generate statistics: " + e.getMessage());
            logger.error("Failed to generate encryption statistics", e);
        }
    }
    
    private void validateEncryptedBlocks() {
        verboseLog("Validating encrypted blocks integrity...");
        
        System.out.println("✅ Validating Encrypted Blocks");
        System.out.println("=" .repeat(60));
        
        try {
            boolean allValid = encryptionAPI.validateEncryptedBlocks();
            
            if (allValid) {
                BlockchainCLI.success("All encrypted blocks are valid");
            } else {
                BlockchainCLI.error("Some encrypted blocks failed validation");
            }
            
            // Get detailed validation report
            String validationReport = encryptionAPI.getValidationReport();
            System.out.println("\n📋 Validation Report:");
            System.out.println(validationReport);
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Validation failed: " + e.getMessage());
            logger.error("Encrypted blocks validation failed", e);
        }
    }
    
    private void outputText(List<Block> blocks) {
        System.out.println("🔐 Encryption Analysis Results");
        System.out.println("=" .repeat(60));
        
        long encryptedCount = blocks.stream().filter(b -> b.getIsEncrypted() != null && b.getIsEncrypted()).count();
        long unencryptedCount = blocks.size() - encryptedCount;
        
        System.out.println("📊 Analysis Summary:");
        System.out.println("   📦 Total blocks analyzed: " + blocks.size());
        System.out.println("   🔐 Encrypted: " + encryptedCount);
        System.out.println("   📝 Unencrypted: " + unencryptedCount);
        
        if (blocks.size() > 0) {
            double encryptionRate = (double) encryptedCount / blocks.size() * 100;
            System.out.println("   📈 Encryption rate: " + String.format("%.1f%%", encryptionRate));
        }
        
        System.out.println();
        
        for (Block block : blocks.subList(0, Math.min(blocks.size(), 10))) {
            System.out.println("📦 Block #" + block.getBlockNumber());
            System.out.println("   📅 " + block.getTimestamp());
            System.out.println("   🔐 Encrypted: " + ((block.getIsEncrypted() != null && block.getIsEncrypted()) ? "Yes" : "No"));
            
            if (block.hasOffChainData()) {
                System.out.println("   💾 Off-chain: " + FormatUtil.formatBytes(block.getOffChainData().getFileSize()));
            }
            
            String data = block.getData();
            if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
                System.out.println("   📝 Data: [Encrypted - " + data.length() + " chars]");
            } else if (data.length() > 50) {
                System.out.println("   📝 Data: " + data.substring(0, 47) + "...");
            } else {
                System.out.println("   📝 Data: " + data);
            }
            System.out.println();
        }
        
        if (blocks.size() > 10) {
            System.out.println("💡 Showing first 10 results. Use --json for complete data.");
        }
    }
    
    private void outputJson(List<Block> blocks) {
        System.out.println("{");
        System.out.println("  \"operation\": \"encrypt-analysis\",");
        System.out.println("  \"totalBlocks\": " + blocks.size() + ",");
        
        long encryptedCount = blocks.stream().filter(b -> b.getIsEncrypted() != null && b.getIsEncrypted()).count();
        System.out.println("  \"encryptedBlocks\": " + encryptedCount + ",");
        System.out.println("  \"unencryptedBlocks\": " + (blocks.size() - encryptedCount) + ",");
        
        if (blocks.size() > 0) {
            double encryptionRate = (double) encryptedCount / blocks.size() * 100;
            System.out.println("  \"encryptionRate\": " + String.format("%.1f", encryptionRate) + ",");
        }
        
        System.out.println("  \"blocks\": [");
        
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            System.out.println("    {");
            System.out.println("      \"blockNumber\": " + block.getBlockNumber() + ",");
            System.out.println("      \"timestamp\": \"" + block.getTimestamp() + "\",");
            System.out.println("      \"encrypted\": " + (block.getIsEncrypted() != null ? block.getIsEncrypted() : false) + ",");
            System.out.println("      \"hasOffChainData\": " + block.hasOffChainData() + ",");
            System.out.println("      \"dataLength\": " + block.getData().length());
            System.out.print("    }");
            
            if (i < blocks.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        
        System.out.println("  ],");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    // Utility method removed - now using FormatUtil
    
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
        logger.debug("🔍 {}", message);
    }
}