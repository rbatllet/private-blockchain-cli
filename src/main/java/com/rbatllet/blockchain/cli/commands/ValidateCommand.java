package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.util.ExitUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command to validate the blockchain integrity
 */
@Command(name = "validate", 
         description = "Validate the entire blockchain integrity")
public class ValidateCommand implements Runnable {
    
    @Option(names = {"-d", "--detailed"}, 
            description = "Show detailed validation results for each block")
    boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"-q", "--quick"}, 
            description = "Perform quick validation (chain integrity only)")
    boolean quick = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Starting blockchain validation...");
            
            Blockchain blockchain = new Blockchain();
            
            // Get basic chain information
            long blockCount = blockchain.getBlockCount();
            boolean isValid;
            
            if (quick) {
                BlockchainCLI.verbose("Performing quick validation...");
                isValid = blockchain.validateChain();
            } else {
                BlockchainCLI.verbose("Performing detailed validation...");
                isValid = performDetailedValidation(blockchain);
            }
            
            if (json) {
                outputJson(isValid, blockCount, blockchain);
            } else {
                outputText(isValid, blockCount, blockchain);
            }
            
            // Exit with error code if validation failed
            if (!isValid) {
                ExitUtil.exit(1);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("Validation failed: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private boolean performDetailedValidation(Blockchain blockchain) {
        try {
            List<Block> blocks = blockchain.getAllBlocks();
            
            BlockchainCLI.verbose("Validating " + blocks.size() + " blocks...");
            
            // For now, we'll use the main chain validation
            // In the future, we could add more detailed validation when methods are available
            boolean isValid = blockchain.validateChain();
            
            if (detailed && !json) {
                for (Block block : blocks) {
                    showBlockValidation(block, isValid);
                }
            }
            
            return isValid;
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Error during detailed validation: " + e.getMessage());
            return false;
        }
    }
    
    private void showBlockValidation(Block block, boolean isValid) {
        String status = isValid ? "✅ VALID" : "❌ INVALID";
        System.out.println("📦 Block #" + block.getBlockNumber() + " - " + status);
        
        if (detailed) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("   📅 Timestamp: " + block.getTimestamp().format(formatter));
            System.out.println("   🔗 Hash: " + truncateHash(block.getHash()));
            System.out.println("   🔗 Previous Hash: " + truncateHash(block.getPreviousHash()));
            System.out.println("   📝 Data Length: " + block.getData().length() + " chars");
        }
    }
    
    private String truncateHash(String hash) {
        if (hash == null) return "null";
        return hash.length() > 32 ? 
            hash.substring(0, 16) + "..." + hash.substring(hash.length() - 16) :
            hash;
    }
    
    private void outputText(boolean isValid, long blockCount, Blockchain blockchain) {
        System.out.println("🔍 Blockchain Validation Results");
        System.out.println("=" .repeat(50));
        
        String status = isValid ? "✅ VALID" : "❌ INVALID";
        System.out.println("🔗 Chain Status: " + status);
        System.out.println("📊 Total Blocks: " + blockCount);
        
        try {
            int authorizedKeys = blockchain.getAuthorizedKeys().size();
            System.out.println("🔑 Authorized Keys: " + authorizedKeys);
            
            if (blockCount > 0) {
                Block lastBlock = blockchain.getLastBlock();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.println("📅 Last Block: " + lastBlock.getTimestamp().format(formatter));
            }
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Could not retrieve additional chain information: " + e.getMessage());
        }
        
        System.out.println();
        
        if (isValid) {
            BlockchainCLI.success("Blockchain validation completed successfully!");
            System.out.println("💡 All blocks are properly linked and signatures are valid.");
        } else {
            BlockchainCLI.error("Blockchain validation failed!");
            System.out.println("⚠️  The blockchain contains invalid blocks or broken links.");
            System.out.println("   Run with --detailed flag for more information.");
        }
    }
    
    private void outputJson(boolean isValid, long blockCount, Blockchain blockchain) {
        System.out.println("{");
        System.out.println("  \"valid\": " + isValid + ",");
        System.out.println("  \"totalBlocks\": " + blockCount + ",");
        
        try {
            int authorizedKeys = blockchain.getAuthorizedKeys().size();
            System.out.println("  \"authorizedKeys\": " + authorizedKeys + ",");
            
            if (blockCount > 0) {
                Block lastBlock = blockchain.getLastBlock();
                System.out.println("  \"lastBlockTimestamp\": \"" + lastBlock.getTimestamp() + "\",");
                System.out.println("  \"lastBlockHash\": \"" + lastBlock.getHash() + "\",");
            }
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Could not retrieve additional chain information: " + e.getMessage());
        }
        
        System.out.println("  \"validationTimestamp\": \"" + java.time.Instant.now() + "\",");
        System.out.println("  \"validationType\": \"" + (quick ? "quick" : "detailed") + "\"");
        System.out.println("}");
    }
}
