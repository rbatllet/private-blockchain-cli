package com.rbatllet.blockchain.cli.demos;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.validation.ChainValidationResult;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demonstration of off-chain storage functionality in the CLI
 * This demo shows how large data is automatically stored off-chain with encryption
 */
public class OffChainStorageDemo {
    
    private static final String SEPARATOR = "=".repeat(80);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("🔗 PRIVATE BLOCKCHAIN - OFF-CHAIN STORAGE DEMO");
        System.out.println(SEPARATOR);
        System.out.println();
        
        try {
            // Initialize blockchain
            System.out.println("📋 Initializing blockchain...");
            Blockchain blockchain = new Blockchain();
            KeyPair keyPair = CryptoUtil.generateKeyPair();
            
            // Authorize the key
            String publicKeyString = CryptoUtil.publicKeyToString(keyPair.getPublic());
            String ownerName = "OffChainDemo-" + System.currentTimeMillis();
            LocalDateTime keyTime = LocalDateTime.now().minusSeconds(1);
            
            if (blockchain.addAuthorizedKey(publicKeyString, ownerName, keyTime)) {
                System.out.println("✅ Generated and authorized demo key: " + ownerName);
            } else {
                System.out.println("❌ Failed to authorize demo key");
                return;
            }
            
            System.out.println();
            
            // Demo 1: Small data (stored on-chain)
            System.out.println("📦 DEMO 1: Small Data (On-Chain Storage)");
            System.out.println("-".repeat(50));
            
            String smallData = "Small medical record for patient PAT-001: Normal vital signs, temperature 36.5°C, blood pressure 120/80 mmHg.";
            System.out.println("📝 Data size: " + smallData.length() + " bytes");
            
            int decision = blockchain.validateAndDetermineStorage(smallData);
            System.out.println("🔍 Storage decision: " + (decision == 1 ? "ON-CHAIN" : "OFF-CHAIN"));
            
            Block block1 = blockchain.addBlockWithKeywords(
                smallData, 
                new String[]{"PAT-001", "MEDICAL", "VITAL-SIGNS"}, 
                "MEDICAL", 
                keyPair.getPrivate(), 
                keyPair.getPublic()
            );
            
            if (block1 != null) {
                System.out.println("✅ Block #" + block1.getBlockNumber() + " created successfully");
                System.out.println("📊 Storage type: " + (block1.hasOffChainData() ? "OFF-CHAIN" : "ON-CHAIN"));
                System.out.println("🏷️  Keywords: " + block1.getManualKeywords());
                System.out.println("📂 Category: " + block1.getContentCategory());
            }
            
            System.out.println();
            
            // Demo 2: Large data (stored off-chain)
            System.out.println("📦 DEMO 2: Large Data (Off-Chain Storage)");
            System.out.println("-".repeat(50));
            
            StringBuilder largeData = new StringBuilder();
            largeData.append("COMPREHENSIVE MEDICAL REPORT - PATIENT: PAT-002\\n\\n");
            largeData.append("DATE: ").append(LocalDateTime.now().format(FORMATTER)).append("\\n");
            largeData.append("DOCTOR: Dr. Sarah Johnson, MD\\n");
            largeData.append("DEPARTMENT: Cardiology\\n\\n");
            
            // Add large amount of data to trigger off-chain storage
            for (int i = 0; i < 8000; i++) {
                largeData.append("Medical observation #").append(i + 1).append(": ");
                largeData.append("Patient shows stable condition with normal heart rate, ");
                largeData.append("blood pressure within normal limits, and no signs of arrhythmia. ");
                largeData.append("ECG reading shows sinus rhythm with rate of 72 bpm. ");
                largeData.append("Laboratory results indicate normal electrolyte levels. ");
                largeData.append("Recommendation: Continue current medication regimen.\\n");
            }
            
            String largeMedicalData = largeData.toString();
            System.out.println("📝 Data size: " + formatBytes(largeMedicalData.length()));
            
            decision = blockchain.validateAndDetermineStorage(largeMedicalData);
            System.out.println("🔍 Storage decision: " + (decision == 1 ? "ON-CHAIN" : "OFF-CHAIN"));
            
            System.out.println("⏳ Creating block with large data (may take a moment for encryption)...");
            
            Block block2 = blockchain.addBlockWithKeywords(
                largeMedicalData, 
                new String[]{"PAT-002", "CARDIOLOGY", "COMPREHENSIVE-REPORT", "ECG"}, 
                "MEDICAL", 
                keyPair.getPrivate(), 
                keyPair.getPublic()
            );
            
            if (block2 != null) {
                System.out.println("✅ Block #" + block2.getBlockNumber() + " created successfully");
                System.out.println("📊 Storage type: " + (block2.hasOffChainData() ? "OFF-CHAIN" : "ON-CHAIN"));
                
                if (block2.hasOffChainData()) {
                    var offChainData = block2.getOffChainData();
                    System.out.println("💾 Off-chain file size: " + formatBytes(offChainData.getFileSize()));
                    System.out.println("🔐 Encryption: AES-256-CBC");
                    System.out.println("📁 File path: " + offChainData.getFilePath());
                    System.out.println("📄 Content type: " + offChainData.getContentType());
                }
                
                System.out.println("🏷️  Keywords: " + block2.getManualKeywords());
                System.out.println("📂 Category: " + block2.getContentCategory());
                
                // Show automatic keywords
                if (block2.getAutoKeywords() != null && !block2.getAutoKeywords().trim().isEmpty()) {
                    System.out.println("🤖 Auto Keywords: " + block2.getAutoKeywords());
                }
            }
            
            System.out.println();
            
            // Demo 3: Financial data with off-chain storage
            System.out.println("📦 DEMO 3: Financial Transaction Data (Off-Chain)");
            System.out.println("-".repeat(50));
            
            StringBuilder financialData = new StringBuilder();
            financialData.append("FINANCIAL TRANSACTION BATCH REPORT\\n\\n");
            financialData.append("PROCESSING DATE: ").append(LocalDateTime.now().format(FORMATTER)).append("\\n");
            financialData.append("BANK: International Finance Corp\\n");
            financialData.append("DEPARTMENT: Wire Transfers\\n");
            financialData.append("APPROVAL: Manager John Smith\\n\\n");
            
            // Add many transactions
            for (int i = 0; i < 10000; i++) {
                financialData.append("TXN-").append(String.format("%06d", i + 1)).append(": ");
                financialData.append("Amount: ").append(String.format("%.2f", Math.random() * 50000)).append(" EUR, ");
                financialData.append("From: ACC-").append(String.format("%08d", (int)(Math.random() * 99999999))).append(", ");
                financialData.append("To: ACC-").append(String.format("%08d", (int)(Math.random() * 99999999))).append(", ");
                financialData.append("Status: COMPLETED, ");
                financialData.append("Fee: ").append(String.format("%.2f", Math.random() * 25)).append(" EUR, ");
                financialData.append("Timestamp: ").append(LocalDateTime.now().minusMinutes((int)(Math.random() * 1440)).format(FORMATTER));
                financialData.append("\\n");
            }
            
            String financialBatchData = financialData.toString();
            System.out.println("📝 Data size: " + formatBytes(financialBatchData.length()));
            
            decision = blockchain.validateAndDetermineStorage(financialBatchData);
            System.out.println("🔍 Storage decision: " + (decision == 1 ? "ON-CHAIN" : "OFF-CHAIN"));
            
            System.out.println("⏳ Creating financial block (encrypting large transaction data)...");
            
            Block block3 = blockchain.addBlockWithKeywords(
                financialBatchData, 
                new String[]{"BATCH-TRANSACTIONS", "WIRE-TRANSFERS", "EUR", "INTERNATIONAL"}, 
                "FINANCE", 
                keyPair.getPrivate(), 
                keyPair.getPublic()
            );
            
            if (block3 != null) {
                System.out.println("✅ Block #" + block3.getBlockNumber() + " created successfully");
                System.out.println("📊 Storage type: " + (block3.hasOffChainData() ? "OFF-CHAIN" : "ON-CHAIN"));
                
                if (block3.hasOffChainData()) {
                    var offChainData = block3.getOffChainData();
                    System.out.println("💾 Off-chain file size: " + formatBytes(offChainData.getFileSize()));
                    System.out.println("🔐 Encryption: AES-256-CBC");
                    System.out.println("📁 File path: " + offChainData.getFilePath());
                }
                
                System.out.println("🏷️  Keywords: " + block3.getManualKeywords());
                System.out.println("📂 Category: " + block3.getContentCategory());
            }
            
            System.out.println();
            
            // Summary
            System.out.println("📊 BLOCKCHAIN SUMMARY");
            System.out.println("-".repeat(50));
            System.out.println("🔗 Total blocks: " + blockchain.getBlockCount());
            
            // Use the new detailed validation API
            ChainValidationResult validationResult = blockchain.validateChainDetailed();
            boolean isValid = validationResult.isFullyCompliant();
            System.out.println("🔍 Validation: " + (isValid ? "✅ VALID" : "❌ INVALID"));
            
            // Show validation details if there are issues
            if (!isValid) {
                System.out.println("   📋 Summary: " + validationResult.getSummary());
                if (validationResult.getRevokedBlocks() > 0) {
                    System.out.println("   🔄 Revoked blocks: " + validationResult.getRevokedBlocks());
                }
                if (validationResult.getInvalidBlocks() > 0) {
                    System.out.println("   💥 Invalid blocks: " + validationResult.getInvalidBlocks());
                }
            }
            
            // Count off-chain blocks
            long offChainBlocks = blockchain.getAllBlocks().stream()
                .filter(Block::hasOffChainData)
                .count();
            System.out.println("💾 Off-chain blocks: " + offChainBlocks + " / " + (blockchain.getBlockCount() - 1)); // Exclude genesis
            
            System.out.println();
            System.out.println("✅ DEMO COMPLETED SUCCESSFULLY!");
            System.out.println();
            System.out.println("🔍 Key Features Demonstrated:");
            System.out.println("   • Automatic storage decision based on data size");
            System.out.println("   • AES-256-CBC encryption for off-chain data");
            System.out.println("   • Keyword and category management");
            System.out.println("   • Blockchain integrity preservation");
            System.out.println("   • Large data handling (up to 100MB per block)");
            System.out.println();
            System.out.println("💡 CLI Usage Examples:");
            System.out.println("   java -jar blockchain-cli.jar add-block \"small data\" --generate-key");
            System.out.println("   java -jar blockchain-cli.jar add-block \"large data...\" --keywords \"KEY1,KEY2\" --category MEDICAL --generate-key");
            System.out.println("   java -jar blockchain-cli.jar search \"keyword\" --complete --detailed");
            
        } catch (Exception e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println(SEPARATOR);
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}