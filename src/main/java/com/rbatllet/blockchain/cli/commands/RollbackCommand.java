package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import java.util.Scanner;

/**
 * Command to rollback blockchain by removing recent blocks
 * 
 * CRITICAL OPERATION: This command removes blocks permanently.
 * Always requires confirmation to prevent accidental data loss.
 */
@Command(name = "rollback", 
         description = "Remove recent blocks from the blockchain (DANGEROUS OPERATION)")
public class RollbackCommand implements Runnable {
    
    @Option(names = {"-b", "--blocks"}, 
            description = "Number of blocks to remove from the end")
    Long blocksToRemove;
    
    @Option(names = {"-t", "--to-block"}, 
            description = "Rollback to specific block number (keeps blocks 0 to N)")
    Long targetBlock;
    
    @Option(names = {"-y", "--yes", "--confirm"}, 
            description = "Skip confirmation prompt (USE WITH CAUTION)")
    boolean skipConfirmation = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"--dry-run"}, 
            description = "Show what would be removed without actually doing it")
    boolean dryRun = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Initializing blockchain connection...");
            Blockchain blockchain = new Blockchain();
            
            // Validate input parameters
            if (blocksToRemove != null && targetBlock != null) {
                BlockchainCLI.error("❌ Cannot specify both --blocks and --to-block options");
                ExitUtil.exit(2);
                return;
            }
            
            if (blocksToRemove == null && targetBlock == null) {
                BlockchainCLI.error("❌ Must specify either --blocks N or --to-block N");
                ExitUtil.exit(2);
                return;
            }
            
            // Get current blockchain state
            long currentBlockCount = blockchain.getBlockCount();
            long blocksToDelete = 0;
            long finalBlockCount = 0;
            
            if (blocksToRemove != null) {
                if (blocksToRemove <= 0) {
                    BlockchainCLI.error("❌ Invalid block count: Number of blocks must be positive");
                    ExitUtil.exit(2);
                    return;
                }
                
                // Check if trying to remove more blocks than exist - this should fail even in dry-run
                if (blocksToRemove >= currentBlockCount) {
                    if (dryRun) {
                        BlockchainCLI.error("❌ Cannot remove " + blocksToRemove + " blocks: Only " + currentBlockCount + " blocks exist (including genesis block)");
                    } else {
                        BlockchainCLI.error("❌ Cannot remove " + blocksToRemove + " blocks: Only " + currentBlockCount + " blocks exist (including genesis block)");
                    }
                    ExitUtil.exit(1);
                    return;
                }
                
                blocksToDelete = blocksToRemove;
                finalBlockCount = currentBlockCount - blocksToRemove;
                
            } else if (targetBlock != null) {
                if (targetBlock < 0) {
                    BlockchainCLI.error("❌ Invalid target block: Target block number cannot be negative");
                    ExitUtil.exit(2);
                    return;
                }
                
                // Check if target block doesn't exist - this should fail even in dry-run
                if (targetBlock >= currentBlockCount) {
                    if (dryRun) {
                        BlockchainCLI.error("❌ Target block " + targetBlock + " does not exist: Current max block is " + (currentBlockCount - 1));
                    } else {
                        BlockchainCLI.error("❌ Target block " + targetBlock + " does not exist: Current max block is " + (currentBlockCount - 1));
                    }
                    ExitUtil.exit(1);
                    return;
                }
                
                finalBlockCount = targetBlock + 1; // Keep blocks 0 to targetBlock (inclusive)
                blocksToDelete = currentBlockCount - finalBlockCount;
            }
            
            // Show what will be done
            if (dryRun || !skipConfirmation) {
                showRollbackPreview(currentBlockCount, finalBlockCount, blocksToDelete);
            }
            
            // Dry run mode - just show preview and exit
            if (dryRun) {
                if (json) {
                    outputDryRunJson(currentBlockCount, finalBlockCount, blocksToDelete);
                } else {
                    // Always ensure there's output for dry run mode
                    System.out.println();
                    System.out.println("🔍 DRY RUN MODE - No changes made");
                    System.out.println("This was a simulation only. No blocks were actually removed.");
                    // Ensure output is flushed
                    System.out.flush();
                }
                ExitUtil.exit(0);
                return;
            }
            
            // Confirmation prompt (unless skipped)
            if (!skipConfirmation && !confirmRollback()) {
                System.out.println("⚠️ Operation cancelled by user");
                System.out.flush();
                ExitUtil.exit(0);
                return;
            }
            
            // Perform the rollback
            BlockchainCLI.verbose("Performing rollback operation...");
            boolean success = false;
            
            if (blocksToRemove != null) {
                success = blockchain.rollbackBlocks(blocksToRemove);
            } else {
                success = blockchain.rollbackToBlock(targetBlock);
            }
            
            if (success) {
                if (json) {
                    outputSuccessJson(currentBlockCount, finalBlockCount, blocksToDelete);
                } else {
                    outputSuccessText(currentBlockCount, finalBlockCount, blocksToDelete);
                }
                ExitUtil.exit(0);
            } else {
                if (json) {
                    outputErrorJson("Rollback operation failed");
                } else {
                    BlockchainCLI.error("❌ Rollback operation failed");
                }
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            if (json) {
                outputErrorJson("Rollback failed: Security error - " + e.getMessage());
            } else {
                BlockchainCLI.error("❌ Rollback failed: Security error - " + e.getMessage());
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            if (json) {
                outputErrorJson("Rollback failed: Runtime error - " + e.getMessage());
            } else {
                BlockchainCLI.error("❌ Rollback failed: Runtime error - " + e.getMessage());
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            if (json) {
                outputErrorJson("Rollback failed: Unexpected error - " + e.getMessage());
            } else {
                BlockchainCLI.error("❌ Rollback failed: Unexpected error - " + e.getMessage());
            }
            ExitUtil.exit(1);
        }
    }
    
    private void showRollbackPreview(long current, long finalCount, long toDelete) {
        System.out.println("🔄 ROLLBACK PREVIEW");
        System.out.println("==================");
        System.out.println("Current blocks:     " + current);
        System.out.println("Blocks to remove:   " + toDelete);
        System.out.println("Final blocks:       " + finalCount);
        
        if (finalCount > 0) {
            System.out.println("Block range kept:   0 to " + (finalCount - 1));
        } else {
            System.out.println("Block range kept:   (none - would remove all blocks)");
        }
        
        if (toDelete > 0) {
            System.out.println("Block range removed: " + finalCount + " to " + (current - 1));
        } else {
            System.out.println("Block range removed: (none - insufficient blocks)");
        }
        
        System.out.println();
        System.out.println("⚠️  WARNING: This operation is IRREVERSIBLE!");
        System.out.println("⚠️  Removed blocks cannot be recovered!");
        System.out.println();
        // Ensure output is flushed
        System.out.flush();
    }
    
    private boolean confirmRollback() {
        System.out.print("Are you absolutely sure you want to proceed? (type 'yes' to confirm): ");
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine().trim();
            return "yes".equalsIgnoreCase(input);
        }
    }
    
    private void outputSuccessText(long currentBlocks, long finalBlocks, long removedBlocks) {
        System.out.println("✅ Rollback completed successfully!");
        System.out.println("📊 Removed " + removedBlocks + " blocks");
        System.out.println("📊 Blockchain now has " + finalBlocks + " blocks");
        System.out.println("💡 Run 'blockchain validate' to verify chain integrity");
        System.out.flush();
    }
    
    private void outputSuccessJson(long currentBlocks, long finalBlocks, long removedBlocks) {
        System.out.println("{");
        System.out.println("  \"success\": true,");
        System.out.println("  \"operation\": \"rollback\",");
        System.out.println("  \"blocksRemoved\": " + removedBlocks + ",");
        System.out.println("  \"previousBlockCount\": " + currentBlocks + ",");
        System.out.println("  \"currentBlockCount\": " + finalBlocks + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
        System.out.flush();
    }
    
    private void outputDryRunJson(long currentBlocks, long finalBlocks, long toRemove) {
        System.out.println("{");
        System.out.println("  \"dryRun\": true,");
        System.out.println("  \"operation\": \"rollback\",");
        System.out.println("  \"wouldRemove\": " + toRemove + ",");
        System.out.println("  \"currentBlockCount\": " + currentBlocks + ",");
        System.out.println("  \"finalBlockCount\": " + finalBlocks);
        System.out.println("}");
        // Ensure output is flushed
        System.out.flush();
    }
    
    private void outputErrorJson(String errorMessage) {
        System.out.println("{");
        System.out.println("  \"success\": false,");
        System.out.println("  \"error\": \"" + errorMessage.replace("\"", "\\\"") + "\",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
        System.out.flush();
    }
}
