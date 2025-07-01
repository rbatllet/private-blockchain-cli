package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.search.SearchLevel;
import com.rbatllet.blockchain.search.SearchValidator;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to search blocks in the blockchain with hybrid search system
 */
@Command(name = "search", 
         description = "Search blocks with hybrid search system: keywords, content, categories, hash, or date")
public class SearchCommand implements Runnable {
    
    @Parameters(index = "0", arity = "0..1",
                description = "Search term (for content search)")
    String searchTerm;
    
    @Option(names = {"-c", "--content"}, 
            description = "Search by content (default if search term provided)")
    String contentSearch;
    
    @Option(names = {"--level"}, 
            description = "Search level: FAST_ONLY (keywords), INCLUDE_DATA (keywords+data), EXHAUSTIVE_OFFCHAIN (all content)")
    SearchLevel searchLevel = SearchLevel.INCLUDE_DATA;
    
    @Option(names = {"-l", "--limit"}, 
            description = "Limit number of results (default: 50)")
    int limit = 50;
    
    @Option(names = {"--fast"}, 
            description = "Fast search - keywords only (equivalent to --level FAST_ONLY)")
    boolean fastSearch = false;
    
    @Option(names = {"--complete"}, 
            description = "Complete search - all content including off-chain (equivalent to --level EXHAUSTIVE_OFFCHAIN)")
    boolean completeSearch = false;
    
    @Option(names = {"--category"}, 
            description = "Search by content category (MEDICAL, FINANCE, TECHNICAL, LEGAL, etc.)")
    String categorySearch;
    
    @Option(names = {"-h", "--hash"}, 
            description = "Search by exact block hash")
    String hashSearch;
    
    @Option(names = {"-n", "--block-number"}, 
            description = "Search by block number")
    Long blockNumber;
    
    @Option(names = {"--date-from"}, 
            description = "Search from date (yyyy-MM-dd)")
    String dateFrom;
    
    @Option(names = {"--date-to"}, 
            description = "Search to date (yyyy-MM-dd)")
    String dateTo;
    
    @Option(names = {"--datetime-from"}, 
            description = "Search from datetime (yyyy-MM-dd HH:mm)")
    String datetimeFrom;
    
    @Option(names = {"--datetime-to"}, 
            description = "Search to datetime (yyyy-MM-dd HH:mm)")
    String datetimeTo;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output results in JSON format")
    boolean json = false;
    
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed block information including keywords and off-chain data")
    boolean detailed = false;
    
    @Option(names = {"--validate-term"}, 
            description = "Validate search term before searching")
    boolean validateTerm = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output with search performance information")
    boolean verbose = false;
    
    @Override
    public void run() {
        try {
            verboseLog("Starting hybrid blockchain search...");
            
            Blockchain blockchain = new Blockchain();
            List<Block> results = new ArrayList<>();
            String searchType = "unknown";
            
            // Determine search level
            if (fastSearch) {
                searchLevel = SearchLevel.FAST_ONLY;
            } else if (completeSearch) {
                searchLevel = SearchLevel.EXHAUSTIVE_OFFCHAIN;
            }
            
            long startTime = System.nanoTime();
            
            // Determine search type and execute
            if (hashSearch != null && !hashSearch.trim().isEmpty()) {
                searchType = "hash";
                verboseLog("Searching by hash: " + hashSearch);
                Block block = blockchain.getBlockByHash(hashSearch);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (blockNumber != null) {
                searchType = "block-number";
                verboseLog("Searching by block number: " + blockNumber);
                Block block = blockchain.getBlock(blockNumber);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (categorySearch != null && !categorySearch.trim().isEmpty()) {
                searchType = "category";
                verboseLog("Searching by category: " + categorySearch + " (level: " + searchLevel + ")");
                results = blockchain.searchByCategory(categorySearch);
                
            } else if (dateFrom != null || dateTo != null) {
                searchType = "date-range";
                verboseLog("Searching by date range");
                results = searchByDateRange(blockchain);
                
            } else if (datetimeFrom != null || datetimeTo != null) {
                searchType = "datetime-range";
                verboseLog("Searching by datetime range");
                results = searchByDateTimeRange(blockchain);
                
            } else if (contentSearch != null || searchTerm != null) {
                searchType = "hybrid-content";
                String content = contentSearch != null ? contentSearch : searchTerm;
                
                // Validate search term if requested
                if (validateTerm) {
                    if (!SearchValidator.isValidSearchTerm(content)) {
                        BlockchainCLI.error("❌ Invalid search term: '" + content + "'");
                        BlockchainCLI.error("   Search terms must be at least 4 characters long");
                        BlockchainCLI.error("   Exceptions: years (2024), acronyms (API, SQL), technical terms (XML, JSON), numbers, IDs");
                        ExitUtil.exit(1);
                    }
                }
                
                verboseLog("Hybrid search for: '" + content + "' (level: " + searchLevel + ")");
                
                // Use the appropriate search method based on level
                switch (searchLevel) {
                    case FAST_ONLY:
                        results = blockchain.searchBlocksFast(content);
                        break;
                    case INCLUDE_DATA:
                        results = blockchain.searchBlocks(content, SearchLevel.INCLUDE_DATA);
                        break;
                    case EXHAUSTIVE_OFFCHAIN:
                        results = blockchain.searchBlocksComplete(content);
                        break;
                }
                
            } else {
                BlockchainCLI.error("❌ No search criteria specified");
                BlockchainCLI.error("   Available options:");
                BlockchainCLI.error("   • Content: provide search term or use --content");
                BlockchainCLI.error("   • Category: use --category MEDICAL|FINANCE|TECHNICAL|LEGAL");
                BlockchainCLI.error("   • Hash: use --hash <block-hash>");
                BlockchainCLI.error("   • Block number: use --block-number <number>");
                BlockchainCLI.error("   • Date range: use --date-from/--date-to");
                BlockchainCLI.error("   • Search levels: --fast (keywords only), --complete (all content)");
                ExitUtil.exit(1);
            }
            
            long endTime = System.nanoTime();
            long searchTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            verboseLog("Search completed in " + searchTime + "ms, found " + results.size() + " results");
            
            // Apply limit
            boolean isLimited = false;
            if (results.size() > limit) {
                results = results.subList(0, limit);
                isLimited = true;
            }
            
            if (json) {
                outputJson(results, searchType, searchTime, isLimited);
            } else {
                outputText(results, searchType, searchTime, isLimited);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Search failed: Security error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Search failed: Runtime error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Search failed: Unexpected error - " + e.getMessage());
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private List<Block> searchByDateRange(Blockchain blockchain) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            LocalDate fromDate = dateFrom != null ? LocalDate.parse(dateFrom, formatter) : LocalDate.MIN;
            LocalDate toDate = dateTo != null ? LocalDate.parse(dateTo, formatter) : LocalDate.MAX;
            
            return blockchain.getBlocksByDateRange(fromDate, toDate);
            
        } catch (DateTimeParseException e) {
            BlockchainCLI.error("❌ Invalid date format: Use yyyy-MM-dd");
            ExitUtil.exit(1);
            return new ArrayList<>();
        }
    }
    
    private List<Block> searchByDateTimeRange(Blockchain blockchain) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            LocalDateTime fromDateTime = datetimeFrom != null ? 
                LocalDateTime.parse(datetimeFrom, formatter) : LocalDateTime.MIN;
            LocalDateTime toDateTime = datetimeTo != null ? 
                LocalDateTime.parse(datetimeTo, formatter) : LocalDateTime.MAX;
            
            return blockchain.getBlocksByTimeRange(fromDateTime, toDateTime);
            
        } catch (DateTimeParseException e) {
            BlockchainCLI.error("❌ Invalid datetime format: Use yyyy-MM-dd HH:mm");
            ExitUtil.exit(1);
            return new ArrayList<>();
        }
    }
    
    private void outputText(List<Block> results, String searchType, long searchTime, boolean isLimited) {
        System.out.println("🔍 Hybrid Search Results (" + searchType + " - " + searchLevel + ")");
        System.out.println("=" .repeat(60));
        
        if (results.isEmpty()) {
            System.out.println("No blocks found matching search criteria.");
            if (searchType.equals("hybrid-content")) {
                System.out.println();
                System.out.println("💡 Try different search levels:");
                System.out.println("   --fast           Keywords only (fastest)");
                System.out.println("   --level INCLUDE_DATA    Keywords + block data (default)");
                System.out.println("   --complete       All content including off-chain (comprehensive)");
            }
            return;
        }
        
        System.out.println("Found " + results.size() + " block(s) in " + searchTime + "ms");
        if (isLimited) {
            System.out.println("💡 Results limited to " + limit + ". Use --limit to see more.");
        }
        System.out.println();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < results.size(); i++) {
            Block block = results.get(i);
            
            System.out.println("📦 Block #" + block.getBlockNumber());
            System.out.println("   📅 Timestamp: " + block.getTimestamp().format(formatter));
            System.out.println("   🔗 Hash: " + truncateHash(block.getHash()));
            
            if (detailed) {
                System.out.println("   🔗 Previous Hash: " + truncateHash(block.getPreviousHash()));
                System.out.println("   🔑 Signer: " + truncateKey(block.getSignerPublicKey()));
                System.out.println("   ✍️  Signature: " + truncateHash(block.getSignature()));
            }
            
            // Show search metadata
            if (block.getContentCategory() != null) {
                System.out.println("   📂 Category: " + block.getContentCategory());
            }
            
            if (detailed) {
                if (block.getManualKeywords() != null && !block.getManualKeywords().trim().isEmpty()) {
                    System.out.println("   🏷️  Manual Keywords: " + block.getManualKeywords());
                }
                if (block.getAutoKeywords() != null && !block.getAutoKeywords().trim().isEmpty()) {
                    System.out.println("   🤖 Auto Keywords: " + block.getAutoKeywords());
                }
            }
            
            // Show off-chain information
            if (block.hasOffChainData()) {
                var offChainData = block.getOffChainData();
                System.out.println("   💾 Off-chain: " + formatBytes(offChainData.getFileSize()) + " (encrypted)");
                if (detailed) {
                    System.out.println("   🔐 Content Type: " + offChainData.getContentType());
                    System.out.println("   📁 File Path: " + offChainData.getFilePath());
                }
            }
            
            // Show data content (truncated or reference)
            String data = block.getData();
            if (block.hasOffChainData()) {
                System.out.println("   📝 Data: [Stored off-chain - use --detailed for more info]");
            } else if (data.length() > 100) {
                System.out.println("   📝 Data: " + data.substring(0, 97) + "...");
            } else {
                System.out.println("   📝 Data: " + data);
            }
            
            if (i < results.size() - 1) {
                System.out.println();
            }
        }
        
        System.out.println();
        System.out.println("⚡ Search performance: " + searchTime + "ms (" + searchLevel + ")");
    }
    
    private void outputJson(List<Block> results, String searchType, long searchTime, boolean isLimited) {
        System.out.println("{");
        System.out.println("  \"searchType\": \"" + searchType + "\",");
        System.out.println("  \"searchLevel\": \"" + searchLevel + "\",");
        System.out.println("  \"searchTimeMs\": " + searchTime + ",");
        System.out.println("  \"resultCount\": " + results.size() + ",");
        System.out.println("  \"limited\": " + isLimited + ",");
        System.out.println("  \"blocks\": [");
        
        for (int i = 0; i < results.size(); i++) {
            Block block = results.get(i);
            
            System.out.println("    {");
            System.out.println("      \"blockNumber\": " + block.getBlockNumber() + ",");
            System.out.println("      \"timestamp\": \"" + block.getTimestamp() + "\",");
            System.out.println("      \"hash\": \"" + block.getHash() + "\",");
            System.out.println("      \"previousHash\": \"" + block.getPreviousHash() + "\",");
            System.out.println("      \"data\": \"" + escapeJson(block.getData()) + "\",");
            System.out.println("      \"signerPublicKey\": \"" + block.getSignerPublicKey() + "\",");
            System.out.println("      \"signature\": \"" + block.getSignature() + "\",");
            
            // Search metadata
            if (block.getContentCategory() != null) {
                System.out.println("      \"category\": \"" + block.getContentCategory() + "\",");
            }
            if (block.getManualKeywords() != null) {
                System.out.println("      \"manualKeywords\": \"" + escapeJson(block.getManualKeywords()) + "\",");
            }
            if (block.getAutoKeywords() != null) {
                System.out.println("      \"autoKeywords\": \"" + escapeJson(block.getAutoKeywords()) + "\",");
            }
            
            // Off-chain information
            System.out.println("      \"hasOffChainData\": " + block.hasOffChainData());
            if (block.hasOffChainData()) {
                var offChainData = block.getOffChainData();
                System.out.println("      ,\"offChainData\": {");
                System.out.println("        \"fileSize\": " + offChainData.getFileSize() + ",");
                System.out.println("        \"contentType\": \"" + offChainData.getContentType() + "\",");
                System.out.println("        \"filePath\": \"" + escapeJson(offChainData.getFilePath()) + "\",");
                System.out.println("        \"encrypted\": true");
                System.out.print("      }");
            }
            
            System.out.print("    }");
            
            if (i < results.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        
        System.out.println("  ],");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private String truncateHash(String hash) {
        if (hash == null) return "null";
        return hash.length() > 32 ? 
            hash.substring(0, 16) + "..." + hash.substring(hash.length() - 16) :
            hash;
    }
    
    private String truncateKey(String key) {
        if (key == null) return "null";
        return key.length() > 40 ? 
            key.substring(0, 20) + "..." + key.substring(key.length() - 20) :
            key;
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
    }
}