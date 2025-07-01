package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.validation.ChainValidationResult;
import java.time.format.DateTimeFormatter;

/**
 * Command to validate the blockchain integrity
 */
@Command(name = "validate", 
         description = "Validate the entire blockchain integrity")
public class ValidateCommand implements Runnable {
    
    @Option(names = {"-d", "--detailed"}, description = "Show detailed validation results for each block")
    private boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, description = "Output result in JSON format")
    private boolean json = false;
    
    @Option(names = {"-q", "--quick"}, description = "Perform quick validation (chain integrity only)")
    private boolean quick = false;
    
    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output with detailed information")
    private boolean verbose = false;
    
    @Override
    public void run() {
        try {
            verboseLog("Starting blockchain validation...");
            
            Blockchain blockchain = new Blockchain();
            
            // Get basic chain information
            long blockCount = blockchain.getBlockCount();
            ChainValidationResult validationResult;
            
            if (quick) {
                verboseLog("Performing quick validation...");
                // Use the new official API for quick validation
                validationResult = blockchain.validateChainDetailed();
            } else {
                verboseLog("Performing detailed validation...");
                // Use the new official API for detailed validation
                validationResult = blockchain.validateChainDetailed();
                
                // If detailed is requested and not json, show extra information
                if (detailed && !json) {
                    showDetailedValidationResults(blockchain, validationResult);
                }
            }
            
            boolean isValid = validationResult.isFullyCompliant();
            
            if (json) {
                outputJson(validationResult, blockCount, blockchain);
            } else {
                outputText(validationResult, blockCount, blockchain);
            }
            
            // Exit with error code if validation failed
            if (!isValid) {
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Validation failed: Security error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Validation failed: Runtime error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Validation failed: Unexpected error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    /**
     * Show detailed validation results using the new validation API
     */
    private void showDetailedValidationResults(Blockchain blockchain, ChainValidationResult result) {
        try {
            System.out.println();
            System.out.println("🔍 Detailed Validation Report");
            System.out.println("=" .repeat(50));
            
            // Show general summary
            System.out.println("📊 Summary: " + result.getSummary());
            System.out.println();
            
            // Show detailed report if available
            String detailedReport = result.getDetailedReport();
            if (detailedReport != null && !detailedReport.trim().isEmpty()) {
                System.out.println("📋 Detailed Report:");
                System.out.println(detailedReport);
                System.out.println();
            }
            
            // Show specific information about revoked/invalid blocks
            if (result.getRevokedBlocks() > 0) {
                System.out.println("⚠️ Revoked Blocks: " + result.getRevokedBlocks());
                System.out.println("   These blocks were signed by keys that were later revoked.");
                System.out.println();
            }
            
            if (result.getInvalidBlocks() > 0) {
                System.out.println("❌ Invalid Blocks: " + result.getInvalidBlocks());
                System.out.println("   These blocks have structural or cryptographic problems.");
                System.out.println();
            }
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Error showing detailed validation results: " + e.getMessage());
        }
    }
    
    private void outputText(ChainValidationResult validationResult, long blockCount, Blockchain blockchain) {
        System.out.println("🔍 Blockchain Validation Results");
        System.out.println("=" .repeat(50));
        
        // Use the new API to show more detailed information
        if (validationResult.isStructurallyIntact()) {
            if (validationResult.isFullyCompliant()) {
                System.out.println("🔗 Chain Status: ✅ FULLY VALID");
            } else {
                System.out.println("🔗 Chain Status: ⚠️ STRUCTURALLY INTACT (authorization issues)");
                System.out.println("   🔄 Revoked blocks: " + validationResult.getRevokedBlocks());
            }
        } else {
            System.out.println("🔗 Chain Status: ❌ INVALID (structural problems)");
            System.out.println("   💥 Invalid blocks: " + validationResult.getInvalidBlocks());
        }
        
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
        System.out.println("📋 Validation Summary: " + validationResult.getSummary());
        System.out.println();
        
        if (validationResult.isFullyCompliant()) {
            BlockchainCLI.success("Blockchain validation completed successfully!");
            System.out.println("💡 All blocks are properly linked and signatures are valid.");
        } else if (validationResult.isStructurallyIntact()) {
            BlockchainCLI.error("⚠️ Blockchain has authorization issues!");
            System.out.println("💡 The chain structure is intact but some blocks were signed by revoked keys.");
            System.out.println("   Run with --detailed flag for more information.");
        } else {
            BlockchainCLI.error("❌ Blockchain validation failed!");
            System.out.println("⚠️  The blockchain contains invalid blocks or broken links.");
            System.out.println("   Run with --detailed flag for more information.");
        }
    }
    
    private void outputJson(ChainValidationResult validationResult, long blockCount, Blockchain blockchain) {
        System.out.println("{");
        System.out.println("  \"validation\": {");
        System.out.println("    \"isFullyCompliant\": " + validationResult.isFullyCompliant() + ",");
        System.out.println("    \"isStructurallyIntact\": " + validationResult.isStructurallyIntact() + ",");
        System.out.println("    \"revokedBlocks\": " + validationResult.getRevokedBlocks() + ",");
        System.out.println("    \"invalidBlocks\": " + validationResult.getInvalidBlocks() + ",");
        System.out.println("    \"summary\": \"" + validationResult.getSummary() + "\"");
        System.out.println("  },");
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
