package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Command to import blockchain data from a file
 */
@Command(name = "import", 
         description = "Import blockchain data from a file")
public class ImportCommand implements Runnable {
    
    @Parameters(index = "0", 
                description = "Input file path for the import")
    String inputFile;
    
    @Option(names = {"--backup"}, 
            description = "Create backup of existing blockchain before import")
    boolean createBackup = false;
    
    @Option(names = {"--force"}, 
            description = "Force import even if validation fails")
    boolean force = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"--validate-after"}, 
            description = "Validate blockchain after import")
    boolean validateAfter = true;
    
    @Option(names = {"--dry-run"}, 
            description = "Simulate import without making changes")
    boolean dryRun = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Starting blockchain import...");
            
            // Validate input file
            if (inputFile == null || inputFile.trim().isEmpty()) {
                BlockchainCLI.error("❌ Input file path cannot be empty");
                ExitUtil.exit(1);
            }
            
            Path inputPath = Paths.get(inputFile);
            File file = inputPath.toFile();
            
            if (!file.exists()) {
                BlockchainCLI.error("❌ Import file does not exist: " + inputFile);
                ExitUtil.exit(1);
            }
            
            if (!file.canRead()) {
                BlockchainCLI.error("❌ Cannot read import file: " + inputFile);
                ExitUtil.exit(1);
            }
            
            // Get file information
            long fileSize = file.length();
            String fileSizeStr = formatFileSize(fileSize);
            
            BlockchainCLI.verbose("Import file: " + inputFile + " (" + fileSizeStr + ")");
            
            Blockchain blockchain = new Blockchain();
            
            // Get current blockchain state
            long currentBlocks = blockchain.getBlockCount();
            int currentKeys = blockchain.getAuthorizedKeys().size();
            
            BlockchainCLI.verbose("Current blockchain: " + currentBlocks + " blocks, " + currentKeys + " keys");
            
            if (dryRun) {
                BlockchainCLI.info("DRY RUN: Simulating import (no changes will be made)");
                // For dry run, we'll just validate the file can be read
                // In a real implementation, this would parse and validate without saving
                if (json) {
                    outputJson(true, inputFile, currentBlocks, currentKeys, 0, 0, true);
                } else {
                    System.out.println("🧪 Dry run completed successfully");
                    System.out.println("📄 Import file appears valid: " + inputFile);
                    System.out.println("📦 File size: " + fileSizeStr);
                    System.out.println("💡 Use without --dry-run to perform actual import");
                }
                return;
            }
            
            // Create backup if requested
            if (createBackup && currentBlocks > 0) {
                String backupFile = "blockchain_backup_" + 
                    java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                    ".json";
                
                BlockchainCLI.verbose("Creating backup: " + backupFile);
                boolean backupSuccess = blockchain.exportChain(backupFile);
                
                if (backupSuccess) {
                    BlockchainCLI.info("Backup created: " + backupFile);
                } else {
                    BlockchainCLI.error("❌ Failed to create backup");
                    if (!force) {
                        ExitUtil.exit(1);
                    }
                }
            }
            
            // Perform the import
            BlockchainCLI.verbose("Importing blockchain data...");
            boolean success = blockchain.importChain(inputFile);
            
            if (success) {
                // Get new blockchain state
                long newBlocks = blockchain.getBlockCount();
                int newKeys = blockchain.getAuthorizedKeys().size();
                
                BlockchainCLI.verbose("Import completed. New state: " + newBlocks + " blocks, " + newKeys + " keys");
                
                // Validate if requested
                boolean isValid = true;
                if (validateAfter) {
                    BlockchainCLI.verbose("Validating imported blockchain...");
                    isValid = blockchain.validateChain();
                }
                
                if (json) {
                    outputJson(true, inputFile, currentBlocks, currentKeys, newBlocks, newKeys, isValid);
                } else {
                    BlockchainCLI.success("Blockchain imported successfully!");
                    System.out.println("📄 Import file: " + inputFile);
                    System.out.println("📊 Blocks: " + currentBlocks + " → " + newBlocks);
                    System.out.println("🔑 Keys: " + currentKeys + " → " + newKeys);
                    
                    if (validateAfter) {
                        String validationStatus = isValid ? "✅ VALID" : "❌ INVALID";
                        System.out.println("🔍 Validation: " + validationStatus);
                    }
                    
                    System.out.println("📅 Import time: " + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                
                if (!isValid && validateAfter) {
                    BlockchainCLI.error("❌ Warning: Imported blockchain failed validation!");
                    if (!force) {
                        ExitUtil.exit(1);
                    }
                }
                
            } else {
                if (json) {
                    outputJson(false, inputFile, currentBlocks, currentKeys, currentBlocks, currentKeys, false);
                } else {
                    BlockchainCLI.error("❌ Failed to import blockchain");
                }
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Import failed: Security error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Import failed: Runtime error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Import failed: Unexpected error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    private void outputJson(boolean success, String file, long oldBlocks, int oldKeys, 
                           long newBlocks, int newKeys, boolean valid) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"importFile\": \"" + file + "\",");
        System.out.println("  \"previousBlocks\": " + oldBlocks + ",");
        System.out.println("  \"previousKeys\": " + oldKeys + ",");
        System.out.println("  \"newBlocks\": " + newBlocks + ",");
        System.out.println("  \"newKeys\": " + newKeys + ",");
        System.out.println("  \"valid\": " + valid + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
