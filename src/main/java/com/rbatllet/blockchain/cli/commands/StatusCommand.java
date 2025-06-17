package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;

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
            boolean isValid = blockchain.validateChain();
            
            if (json) {
                outputJson(blockCount, authorizedKeys, isValid);
            } else {
                outputText(blockCount, authorizedKeys, isValid, detailed);
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
    
    private void outputJson(long blockCount, int authorizedKeys, boolean isValid) {
        System.out.println("{");
        System.out.println("  \"blockCount\": " + blockCount + ",");
        System.out.println("  \"authorizedKeys\": " + authorizedKeys + ",");
        System.out.println("  \"isValid\": " + isValid + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputText(long blockCount, int authorizedKeys, boolean isValid, boolean detailed) {
        System.out.println("🔗 Blockchain Status");
        System.out.println("=" .repeat(50));
        System.out.println("📊 Total blocks: " + blockCount);
        System.out.println("👥 Authorized keys: " + authorizedKeys);
        System.out.println("✅ Chain integrity: " + (isValid ? "VALID" : "INVALID"));
        
        if (detailed) {
            System.out.println();
            System.out.println("📋 Configuration:");
            System.out.println("   Max block size: 1,048,576 bytes (1MB)");
            System.out.println("   Max data length: 10,000 characters");
            System.out.println("   Database: SQLite (blockchain.db)");
            System.out.println("   Timestamp: " + java.time.LocalDateTime.now());
        }
    }
}
