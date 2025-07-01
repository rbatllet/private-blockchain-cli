package com.rbatllet.blockchain.cli.demos;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.search.SearchLevel;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Demonstration of hybrid search functionality in the CLI
 * This demo shows all search levels and different search types
 */
public class HybridSearchDemo {
    
    private static final String SEPARATOR = "=".repeat(80);
    private static final String SUB_SEPARATOR = "-".repeat(60);
    
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("🔍 PRIVATE BLOCKCHAIN - HYBRID SEARCH DEMO");
        System.out.println(SEPARATOR);
        System.out.println();
        
        try {
            // Initialize blockchain and create test data
            System.out.println("📋 Setting up test data...");
            Blockchain blockchain = setupTestData();
            System.out.println("✅ Created " + (blockchain.getBlockCount() - 1) + " test blocks");
            System.out.println();
            
            // Demo 1: Fast Search (Keywords Only)
            System.out.println("🚀 DEMO 1: Fast Search (Keywords Only)");
            System.out.println(SUB_SEPARATOR);
            System.out.println("⚡ Fastest search - only searches in manual and auto keywords");
            System.out.println("💡 Use when you need quick results and know the keywords");
            System.out.println();
            
            performSearch(blockchain, "PATIENT-001", SearchLevel.FAST_ONLY, "Fast search for patient ID");
            performSearch(blockchain, "CARDIOLOGY", SearchLevel.FAST_ONLY, "Fast search for department");
            performSearch(blockchain, "TXN-2024", SearchLevel.FAST_ONLY, "Fast search for transaction prefix");
            
            System.out.println();
            
            // Demo 2: Include Data Search
            System.out.println("⚖️ DEMO 2: Include Data Search (Keywords + Block Data)");
            System.out.println(SUB_SEPARATOR);
            System.out.println("🔍 Balanced search - searches keywords and block content");
            System.out.println("💡 Default search level - good balance of speed and completeness");
            System.out.println();
            
            performSearch(blockchain, "patient", SearchLevel.INCLUDE_DATA, "Search for 'patient' in content");
            performSearch(blockchain, "transaction", SearchLevel.INCLUDE_DATA, "Search for 'transaction' in content");
            performSearch(blockchain, "analysis", SearchLevel.INCLUDE_DATA, "Search for 'analysis' in content");
            performSearch(blockchain, "admin@company.com", SearchLevel.INCLUDE_DATA, "Search for email address");
            
            System.out.println();
            
            // Demo 3: Exhaustive Search (Including Off-Chain)
            System.out.println("🔍 DEMO 3: Exhaustive Search (All Content Including Off-Chain)");
            System.out.println(SUB_SEPARATOR);
            System.out.println("🏆 Most comprehensive - searches everything including off-chain data");
            System.out.println("💡 Use when you need to find content that might be in large off-chain files");
            System.out.println();
            
            performSearch(blockchain, "observation", SearchLevel.EXHAUSTIVE_OFFCHAIN, "Exhaustive search for 'observation'");
            performSearch(blockchain, "XML", SearchLevel.EXHAUSTIVE_OFFCHAIN, "Exhaustive search for XML content");
            performSearch(blockchain, "processing", SearchLevel.EXHAUSTIVE_OFFCHAIN, "Exhaustive search for processing");
            
            System.out.println();
            
            // Demo 4: Category Search
            System.out.println("📂 DEMO 4: Category Search");
            System.out.println(SUB_SEPARATOR);
            System.out.println("🏷️  Search blocks by content category");
            System.out.println("💡 Useful for filtering content by type or department");
            System.out.println();
            
            performCategorySearch(blockchain, "MEDICAL", "Search medical records");
            performCategorySearch(blockchain, "FINANCE", "Search financial transactions");
            performCategorySearch(blockchain, "TECHNICAL", "Search technical documentation");
            
            System.out.println();
            
            // Demo 5: Search Performance Comparison
            System.out.println("⏱️ DEMO 5: Search Performance Comparison");
            System.out.println(SUB_SEPARATOR);
            System.out.println("📊 Compare search times across different levels");
            System.out.println();
            
            String searchTerm = "data";
            System.out.println("🔍 Searching for: '" + searchTerm + "'");
            System.out.println();
            
            long startTime, endTime;
            
            // Fast search
            startTime = System.nanoTime();
            List<Block> fastResults = blockchain.searchBlocksFast(searchTerm);
            endTime = System.nanoTime();
            long fastTime = (endTime - startTime) / 1_000_000;
            System.out.println("⚡ FAST_ONLY: " + fastResults.size() + " results in " + fastTime + "ms");
            
            // Include data search
            startTime = System.nanoTime();
            List<Block> dataResults = blockchain.searchBlocks(searchTerm, SearchLevel.INCLUDE_DATA);
            endTime = System.nanoTime();
            long dataTime = (endTime - startTime) / 1_000_000;
            System.out.println("⚖️ INCLUDE_DATA: " + dataResults.size() + " results in " + dataTime + "ms");
            
            // Exhaustive search
            startTime = System.nanoTime();
            List<Block> exhaustiveResults = blockchain.searchBlocksComplete(searchTerm);
            endTime = System.nanoTime();
            long exhaustiveTime = (endTime - startTime) / 1_000_000;
            System.out.println("🔍 EXHAUSTIVE_OFFCHAIN: " + exhaustiveResults.size() + " results in " + exhaustiveTime + "ms");
            
            System.out.println();
            System.out.println("📈 Performance Analysis:");
            System.out.println("   • Fast search is " + String.format("%.1fx", (double)dataTime / fastTime) + " faster than include data");
            System.out.println("   • Include data is " + String.format("%.1fx", (double)exhaustiveTime / dataTime) + " faster than exhaustive");
            System.out.println("   • Fast search is " + String.format("%.1fx", (double)exhaustiveTime / fastTime) + " faster than exhaustive");
            
            System.out.println();
            
            // Demo 6: Advanced Search Features
            System.out.println("🎯 DEMO 6: Advanced Search Features");
            System.out.println(SUB_SEPARATOR);
            
            // Auto-keyword extraction demo
            System.out.println("🤖 Auto-Keyword Extraction:");
            List<Block> allBlocks = blockchain.getAllBlocks();
            for (Block block : allBlocks) {
                if (block.getBlockNumber() > 0 && block.getAutoKeywords() != null && !block.getAutoKeywords().trim().isEmpty()) {
                    System.out.println("   Block #" + block.getBlockNumber() + " auto keywords: " + block.getAutoKeywords());
                }
            }
            
            System.out.println();
            
            // Search metadata display
            System.out.println("📋 Search Metadata:");
            List<Block> results = blockchain.searchBlocks("PATIENT", SearchLevel.INCLUDE_DATA);
            for (Block block : results) {
                System.out.println("   📦 Block #" + block.getBlockNumber() + ":");
                System.out.println("      🏷️  Manual Keywords: " + (block.getManualKeywords() != null ? block.getManualKeywords() : "none"));
                System.out.println("      🤖 Auto Keywords: " + (block.getAutoKeywords() != null ? block.getAutoKeywords() : "none"));
                System.out.println("      📂 Category: " + (block.getContentCategory() != null ? block.getContentCategory() : "none"));
                System.out.println("      💾 Off-chain: " + (block.hasOffChainData() ? "Yes" : "No"));
                System.out.println();
            }
            
            System.out.println();
            
            // Summary
            System.out.println("📊 SEARCH CAPABILITIES SUMMARY");
            System.out.println(SUB_SEPARATOR);
            System.out.println("✅ Search Levels Available:");
            System.out.println("   🚀 FAST_ONLY: Keywords only (fastest)");
            System.out.println("   ⚖️ INCLUDE_DATA: Keywords + block data (balanced)");
            System.out.println("   🔍 EXHAUSTIVE_OFFCHAIN: All content including off-chain (complete)");
            System.out.println();
            System.out.println("✅ Search Types:");
            System.out.println("   📝 Content search: Search in block data");
            System.out.println("   📂 Category search: Filter by content type");
            System.out.println("   🔗 Hash search: Find specific block by hash");
            System.out.println("   🔢 Block number search: Get block by number");
            System.out.println("   📅 Date range search: Find blocks by date/time");
            System.out.println();
            System.out.println("✅ Advanced Features:");
            System.out.println("   🤖 Automatic keyword extraction");
            System.out.println("   🏷️  Manual keyword management");
            System.out.println("   💾 Off-chain data search");
            System.out.println("   🔐 Encrypted content search");
            System.out.println("   ⚡ Performance optimization");
            System.out.println();
            System.out.println("💡 CLI Usage Examples:");
            System.out.println("   java -jar blockchain-cli.jar search \"keyword\" --fast");
            System.out.println("   java -jar blockchain-cli.jar search \"keyword\" --level INCLUDE_DATA");
            System.out.println("   java -jar blockchain-cli.jar search \"keyword\" --complete --detailed");
            System.out.println("   java -jar blockchain-cli.jar search --category MEDICAL --limit 10");
            System.out.println("   java -jar blockchain-cli.jar search --block-number 5");
            System.out.println("   java -jar blockchain-cli.jar search --date-from 2024-01-01 --date-to 2024-12-31");
            
            System.out.println();
            System.out.println("✅ HYBRID SEARCH DEMO COMPLETED SUCCESSFULLY!");
            
        } catch (Exception e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println(SEPARATOR);
    }
    
    private static Blockchain setupTestData() throws Exception {
        Blockchain blockchain = new Blockchain();
        KeyPair keyPair = CryptoUtil.generateKeyPair();
        
        // Authorize the key
        String publicKeyString = CryptoUtil.publicKeyToString(keyPair.getPublic());
        String ownerName = "SearchDemo-" + System.currentTimeMillis();
        LocalDateTime keyTime = LocalDateTime.now().minusSeconds(1);
        
        blockchain.addAuthorizedKey(publicKeyString, ownerName, keyTime);
        
        // Create test blocks with different content types
        
        // Medical data
        blockchain.addBlockWithKeywords(
            "Medical patient data for PATIENT-001 with ECG results from 2024-01-15. Cardiology department examination showed normal heart rhythm.",
            new String[]{"PATIENT-001", "ECG", "CARDIOLOGY", "HEART-RHYTHM"},
            "MEDICAL",
            keyPair.getPrivate(),
            keyPair.getPublic()
        );
        
        // Financial data
        blockchain.addBlockWithKeywords(
            "Financial transaction TXN-2024-001 amount 50000 EUR processed via API integration. Wire transfer completed successfully.",
            new String[]{"TXN-2024-001", "FINANCE", "WIRE-TRANSFER", "EUR"},
            "FINANCE",
            keyPair.getPrivate(),
            keyPair.getPublic()
        );
        
        // Technical documentation
        blockchain.addBlockWithKeywords(
            "Technical documentation for API endpoint admin@company.com with JSON format. Database integration using SQL queries.",
            new String[]{"API", "DOCUMENTATION", "JSON", "SQL"},
            "TECHNICAL",
            keyPair.getPrivate(),
            keyPair.getPublic()
        );
        
        // Large technical document (off-chain)
        StringBuilder largeData = new StringBuilder();
        largeData.append("Comprehensive technical analysis document with detailed observations and XML processing information.\\n\\n");
        for (int i = 0; i < 5000; i++) {
            largeData.append("Technical observation #").append(i + 1).append(": ");
            largeData.append("System analysis shows optimal performance with data processing rates exceeding baseline metrics. ");
            largeData.append("XML parser handles complex nested structures efficiently. ");
            largeData.append("Database queries execute within acceptable time limits. ");
            largeData.append("Memory usage remains stable under load conditions.\\n");
        }
        
        blockchain.addBlockWithKeywords(
            largeData.toString(),
            new String[]{"TECHNICAL-ANALYSIS", "XML", "PERFORMANCE", "DATABASE"},
            "TECHNICAL",
            keyPair.getPrivate(),
            keyPair.getPublic()
        );
        
        // Legal document
        blockchain.addBlockWithKeywords(
            "Legal contract document for partnership agreement between Company A and Company B. Terms include revenue sharing and intellectual property rights.",
            new String[]{"CONTRACT", "PARTNERSHIP", "LEGAL", "IP-RIGHTS"},
            "LEGAL",
            keyPair.getPrivate(),
            keyPair.getPublic()
        );
        
        return blockchain;
    }
    
    private static void performSearch(Blockchain blockchain, String searchTerm, SearchLevel level, String description) {
        System.out.println("🔍 " + description + ": '" + searchTerm + "'");
        
        long startTime = System.nanoTime();
        
        List<Block> results;
        switch (level) {
            case FAST_ONLY:
                results = blockchain.searchBlocksFast(searchTerm);
                break;
            case INCLUDE_DATA:
                results = blockchain.searchBlocks(searchTerm, level);
                break;
            case EXHAUSTIVE_OFFCHAIN:
                results = blockchain.searchBlocksComplete(searchTerm);
                break;
            default:
                results = blockchain.searchBlocks(searchTerm, level);
        }
        
        long endTime = System.nanoTime();
        long searchTime = (endTime - startTime) / 1_000_000;
        
        System.out.println("   📊 Results: " + results.size() + " blocks found in " + searchTime + "ms");
        
        for (Block block : results) {
            System.out.println("      📦 Block #" + block.getBlockNumber() + " (" + 
                (block.getContentCategory() != null ? block.getContentCategory() : "No category") + ")" +
                (block.hasOffChainData() ? " [Off-chain]" : ""));
        }
        
        System.out.println();
    }
    
    private static void performCategorySearch(Blockchain blockchain, String category, String description) {
        System.out.println("🏷️  " + description + ": '" + category + "'");
        
        long startTime = System.nanoTime();
        List<Block> results = blockchain.searchByCategory(category);
        long endTime = System.nanoTime();
        long searchTime = (endTime - startTime) / 1_000_000;
        
        System.out.println("   📊 Results: " + results.size() + " blocks found in " + searchTime + "ms");
        
        for (Block block : results) {
            System.out.println("      📦 Block #" + block.getBlockNumber() + 
                " - Keywords: " + (block.getManualKeywords() != null ? block.getManualKeywords() : "none") +
                (block.hasOffChainData() ? " [Off-chain]" : ""));
        }
        
        System.out.println();
    }
}