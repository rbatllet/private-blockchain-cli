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
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output with detailed information")
    boolean verbose = false;
    
    @Override
    public void run() {
        try {
            verboseLog("Initializing blockchain connection...");
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
        System.out.println("  \"status\": {");
        System.out.println("    \"blockCount\": " + blockCount + ",");
        System.out.println("    \"authorizedKeys\": " + authorizedKeys + ",");
        System.out.println("    \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("  },");
        System.out.println("  \"validation\": {");
        System.out.println("    \"isFullyCompliant\": " + isValid + ",");
        System.out.println("    \"isStructurallyIntact\": " + validationResult.isStructurallyIntact() + ",");
        System.out.println("    \"revokedBlocks\": " + validationResult.getRevokedBlocks() + ",");
        System.out.println("    \"invalidBlocks\": " + validationResult.getInvalidBlocks() + ",");
        System.out.println("    \"summary\": \"" + validationResult.getSummary().replace("\"", "\\\"") + "\"");
        System.out.println("  }");
        System.out.println("}");
    }
    
    private void outputText(long blockCount, int authorizedKeys, ChainValidationResult validationResult, boolean detailed, boolean isValid) {
        System.out.println("🔗 Blockchain Status");
        System.out.println("=" .repeat(50));
        System.out.println("📊 Total blocks: " + blockCount);
        System.out.println("👥 Authorized keys: " + authorizedKeys);
        System.out.println();
        
        // Enhanced validation status display using new API
        System.out.println("🔍 Chain Validation Status:");
        System.out.println("   Structural integrity: " + (validationResult.isStructurallyIntact() ? "✅ INTACT" : "❌ BROKEN"));
        System.out.println("   Authorization compliance: " + (validationResult.isFullyCompliant() ? "✅ COMPLIANT" : "⚠️ ISSUES"));
        
        // Show specific problem counts
        if (validationResult.getRevokedBlocks() > 0) {
            System.out.println("   🔄 Revoked blocks: " + validationResult.getRevokedBlocks() + " (signed by revoked keys)");
        }
        if (validationResult.getInvalidBlocks() > 0) {
            System.out.println("   💥 Invalid blocks: " + validationResult.getInvalidBlocks() + " (structural problems)");
        }
        
        // Overall status determination
        if (validationResult.isStructurallyIntact()) {
            if (isValid) {
                System.out.println("✅ Overall status: FULLY VALID");
            } else {
                System.out.println("⚠️  Overall status: STRUCTURALLY INTACT (authorization issues)");
                System.out.println("   💡 Chain can operate but has compliance issues");
            }
        } else {
            System.out.println("❌ Overall status: INVALID (structural problems)");
            System.out.println("   ⚠️  Chain integrity is compromised");
        }
        
        System.out.println();
        System.out.println("📋 Validation Summary: " + validationResult.getSummary());
        
        if (detailed) {
            System.out.println();
            System.out.println("🔍 Detailed Chain Analysis:");
            
            // Show detailed validation report if available
            String detailedReport = validationResult.getDetailedReport();
            if (detailedReport != null && !detailedReport.trim().isEmpty()) {
                System.out.println("📋 Comprehensive Validation Report:");
                System.out.println(detailedReport);
                System.out.println();
            }
            
            System.out.println("⚙️  System Configuration:");
            System.out.println("   Max block size: 1,048,576 bytes (1MB)");
            System.out.println("   Max data length: 10,000 characters");
            System.out.println("   Database: SQLite (blockchain.db)");
            System.out.println("   Timestamp: " + java.time.LocalDateTime.now());
            
            System.out.println();
            System.out.println("🎯 Validation API Usage:");
            System.out.println("   For production: Check isStructurallyIntact()");
            System.out.println("   For compliance: Check isFullyCompliant()");
            System.out.println("   For debugging: Use getDetailedReport()");
            
        } else {
            // Show hint about detailed mode if there are any issues or if user wants more info
            if (!validationResult.isFullyCompliant() || !validationResult.isStructurallyIntact()) {
                System.out.println();
                System.out.println("💡 Use 'blockchain status --detailed' for comprehensive analysis");
                System.out.println("💡 Use 'blockchain validate --detailed' for full validation report");
            } else {
                System.out.println();
                System.out.println("💡 Use 'blockchain status --detailed' for system configuration info");
            }
        }
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
