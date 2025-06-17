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
 * Command to export blockchain data to a file
 */
@Command(name = "export", 
         description = "Export blockchain data to a file")
public class ExportCommand implements Runnable {
    
    @Parameters(index = "0", 
                description = "Output file path for the export")
    String outputFile;
    
    @Option(names = {"-f", "--format"}, 
            description = "Export format: json (default)")
    String format = "json";
    
    @Option(names = {"--overwrite"}, 
            description = "Overwrite existing file if it exists")
    boolean overwrite = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output operation result in JSON format")
    boolean jsonOutput = false;
    
    @Option(names = {"-c", "--compress"}, 
            description = "Compress the export file")
    boolean compress = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Starting blockchain export...");
            
            // Validate output file path
            if (outputFile == null || outputFile.trim().isEmpty()) {
                BlockchainCLI.error("❌ Output file path cannot be empty");
                ExitUtil.exit(1);
            }
            
            // Check if file exists and handle overwrite
            Path outputPath = Paths.get(outputFile);
            File file = outputPath.toFile();
            
            if (file.exists() && !overwrite) {
                BlockchainCLI.error("❌ File already exists: " + outputFile + ". Use --overwrite flag to replace existing file");
                ExitUtil.exit(1);
            }
            
            // Ensure parent directories exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                BlockchainCLI.verbose("Creating parent directories: " + parentDir.getAbsolutePath());
                if (!parentDir.mkdirs()) {
                    BlockchainCLI.error("❌ Failed to create parent directories");
                    ExitUtil.exit(1);
                }
            }
            
            // Validate format
            if (!"json".equalsIgnoreCase(format)) {
                BlockchainCLI.error("❌ Unsupported export format: " + format + ". Supported formats: json");
                ExitUtil.exit(1);
            }
            
            Blockchain blockchain = new Blockchain();
            
            // Get blockchain statistics before export
            long blockCount = blockchain.getBlockCount();
            int keyCount = blockchain.getAuthorizedKeys().size();
            
            BlockchainCLI.verbose("Exporting " + blockCount + " blocks and " + keyCount + " keys...");
            
            // Perform the export
            boolean success = blockchain.exportChain(outputFile);
            
            if (success) {
                // Get file size
                long fileSize = file.length();
                String fileSizeStr = formatFileSize(fileSize);
                
                if (jsonOutput) {
                    outputJson(true, outputFile, blockCount, keyCount, fileSize);
                } else {
                    BlockchainCLI.success("Blockchain exported successfully!");
                    System.out.println("📄 Export file: " + outputFile);
                    System.out.println("📊 Exported blocks: " + blockCount);
                    System.out.println("🔑 Exported keys: " + keyCount);
                    System.out.println("📦 File size: " + fileSizeStr);
                    System.out.println("📅 Export time: " + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            } else {
                if (jsonOutput) {
                    outputJson(false, outputFile, blockCount, keyCount, 0);
                } else {
                    BlockchainCLI.error("❌ Failed to export blockchain");
                }
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Export failed: Security error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Export failed: Runtime error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Export failed: Unexpected error - " + e.getMessage());
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
    
    private void outputJson(boolean success, String file, long blocks, int keys, long fileSize) {
        System.out.println("{");
        System.out.println("  \"success\": " + success + ",");
        System.out.println("  \"exportFile\": \"" + file + "\",");
        System.out.println("  \"exportedBlocks\": " + blocks + ",");
        System.out.println("  \"exportedKeys\": " + keys + ",");
        System.out.println("  \"fileSizeBytes\": " + fileSize + ",");
        System.out.println("  \"format\": \"" + format + "\",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
