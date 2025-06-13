package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.util.ExitUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to search blocks in the blockchain
 */
@Command(name = "search", 
         description = "Search blocks by content, hash, or date")
public class SearchCommand implements Runnable {
    
    @Parameters(index = "0", arity = "0..1",
                description = "Search term (for content search)")
    String searchTerm;
    
    @Option(names = {"-c", "--content"}, 
            description = "Search by content (default if search term provided)")
    String contentSearch;
    
    @Option(names = {"-h", "--hash"}, 
            description = "Search by exact block hash")
    String hashSearch;
    
    @Option(names = {"-n", "--block-number"}, 
            description = "Search by block number")
    Integer blockNumber;
    
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
    
    @Option(names = {"-l", "--limit"}, 
            description = "Limit number of results (default: 50)")
    int limit = 50;
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed block information")
    boolean detailed = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Starting blockchain search...");
            
            Blockchain blockchain = new Blockchain();
            List<Block> results = new ArrayList<>();
            String searchType = "unknown";
            
            // Determine search type and execute
            if (hashSearch != null && !hashSearch.trim().isEmpty()) {
                searchType = "hash";
                BlockchainCLI.verbose("Searching by hash: " + hashSearch);
                Block block = blockchain.getBlockByHash(hashSearch);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (blockNumber != null) {
                searchType = "block-number";
                BlockchainCLI.verbose("Searching by block number: " + blockNumber);
                Block block = blockchain.getBlock(blockNumber);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (dateFrom != null || dateTo != null) {
                searchType = "date-range";
                BlockchainCLI.verbose("Searching by date range");
                results = searchByDateRange(blockchain);
                
            } else if (datetimeFrom != null || datetimeTo != null) {
                searchType = "datetime-range";
                BlockchainCLI.verbose("Searching by datetime range");
                results = searchByDateTimeRange(blockchain);
                
            } else if (contentSearch != null || searchTerm != null) {
                searchType = "content";
                String content = contentSearch != null ? contentSearch : searchTerm;
                BlockchainCLI.verbose("Searching by content: " + content);
                results = blockchain.searchBlocksByContent(content);
                
            } else {
                BlockchainCLI.error("No search criteria specified");
                System.out.println("Use one of: --content, --hash, --block-number, --date-from/--date-to, or provide a search term");
                ExitUtil.exit(1);
            }
            
            // Apply limit
            if (results.size() > limit) {
                results = results.subList(0, limit);
            }
            
            if (json) {
                outputJson(results, searchType);
            } else {
                outputText(results, searchType);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("Search failed: " + e.getMessage());
            if (BlockchainCLI.verbose) {
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
            BlockchainCLI.error("Invalid date format. Use yyyy-MM-dd");
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
            BlockchainCLI.error("Invalid datetime format. Use yyyy-MM-dd HH:mm");
            ExitUtil.exit(1);
            return new ArrayList<>();
        }
    }
    
    private void outputText(List<Block> results, String searchType) {
        System.out.println("🔍 Search Results (" + searchType + ")");
        System.out.println("=" .repeat(50));
        
        if (results.isEmpty()) {
            System.out.println("No blocks found matching search criteria.");
            return;
        }
        
        System.out.println("Found " + results.size() + " block(s):");
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
            
            // Show data content (truncated)
            String data = block.getData();
            if (data.length() > 100) {
                System.out.println("   📝 Data: " + data.substring(0, 97) + "...");
            } else {
                System.out.println("   📝 Data: " + data);
            }
            
            if (i < results.size() - 1) {
                System.out.println();
            }
        }
        
        if (results.size() >= limit) {
            System.out.println();
            System.out.println("💡 Results limited to " + limit + ". Use --limit to see more.");
        }
    }
    
    private void outputJson(List<Block> results, String searchType) {
        System.out.println("{");
        System.out.println("  \"searchType\": \"" + searchType + "\",");
        System.out.println("  \"resultCount\": " + results.size() + ",");
        System.out.println("  \"limited\": " + (results.size() >= limit) + ",");
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
            System.out.println("      \"signature\": \"" + block.getSignature() + "\"");
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
}
