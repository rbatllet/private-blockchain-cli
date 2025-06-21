package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.validation.ChainValidationResult;

/**
 * Command to show blockchain status and statistics
 */
@Command(name = "status", 
         description = "Show blockchain status and statistics")
public class StatusCommand implements Runnable {
    
    @Option(names = {"-d", "--detailed"}, 
            description = "Show detailed statistics")
    boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output in JSON format")
    boolean json = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Initializing blockchain connection...");
            Blockchain blockchain = new Blockchain();
            
            // Gather statistics
            long blockCount = blockchain.getBlockCount();
            int authorizedKeys = blockchain.getAuthorizedKeys().size();
            ChainValidationResult validationResult = blockchain.validateChainDetailed();
            boolean isValid = validationResult.isFullyCompliant();
            
            if (json) {
                outputJson(blockCount, authorizedKeys, validationResult, isValid);
            } else {
                outputText(blockCount, authorizedKeys, validationResult, detailed, isValid);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Failed to get blockchain status: Security error - " + e.getMessage());
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Failed to get blockchain status: Runtime error - " + e.getMessage());
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to get blockchain status: Unexpected error - " + e.getMessage());
            ExitUtil.exit(1);
        }
    }
    
    private void outputJson(long blockCount, int authorizedKeys, ChainValidationResult validationResult, boolean isValid) {
        System.out.println("{");
        System.out.println("  \"blockCount\": " + blockCount + ",");
        System.out.println("  \"authorizedKeys\": " + authorizedKeys + ",");
        System.out.println("  \"isValid\": " + isValid + ",");
        System.out.println("  \"isStructurallyIntact\": " + validationResult.isStructurallyIntact() + ",");
        System.out.println("  \"revokedBlocks\": " + validationResult.getRevokedBlocks() + ",");
        System.out.println("  \"invalidBlocks\": " + validationResult.getInvalidBlocks() + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputText(long blockCount, int authorizedKeys, ChainValidationResult validationResult, boolean detailed, boolean isValid) {
        System.out.println("🔗 Blockchain Status");
        System.out.println("=" .repeat(50));
        System.out.println("📊 Total blocks: " + blockCount);
        System.out.println("👥 Authorized keys: " + authorizedKeys);
        
        // Use new API to show more detailed information
        if (validationResult.isStructurallyIntact()) {
            if (isValid) {
                System.out.println("✅ Chain integrity: FULLY VALID");
            } else {
                System.out.println("⚠️ Chain integrity: STRUCTURALLY INTACT (but has authorization issues)");
                System.out.println("   🔄 Revoked blocks: " + validationResult.getRevokedBlocks());
            }
        } else {
            System.out.println("❌ Chain integrity: INVALID (structural problems)");
            System.out.println("   💥 Invalid blocks: " + validationResult.getInvalidBlocks());
        }
        
        if (detailed) {
            System.out.println();
            System.out.println("📋 Configuration:");
            System.out.println("   Max block size: 1,048,576 bytes (1MB)");
            System.out.println("   Max data length: 10,000 characters");
            System.out.println("   Database: SQLite (blockchain.db)");
            System.out.println("   Timestamp: " + java.time.LocalDateTime.now());
            
            // Add detailed validation information if available
            System.out.println();
            System.out.println("🔍 Validation Summary:");
            System.out.println("   " + validationResult.getSummary());
        }
    }
}
