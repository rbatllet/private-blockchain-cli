package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.search.SearchFrameworkEngine;
import com.rbatllet.blockchain.search.SearchSpecialistAPI;
import com.rbatllet.blockchain.search.SearchLevel;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.format.FormatUtil;
import com.rbatllet.blockchain.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Modern search command using SearchSpecialistAPI and UserFriendlyEncryptionAPI
 */
@Command(name = "search", 
         description = "Search blocks using modern search APIs")
public class SearchCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchCommand.class);
    
    @Parameters(index = "0", arity = "0..1",
                description = "Search query")
    String searchQuery;
    
    @Option(names = {"-t", "--type"},
            description = "Search type: SIMPLE (fast public), SECURE (encrypted), INTELLIGENT (adaptive), ADVANCED (full featured)")
    String searchType = "SIMPLE";

    @Option(names = {"-c", "--content"},
            description = "Search by content (alternative to positional search query)")
    String contentSearch;

    @Option(names = {"-l", "--limit"},
            description = "Limit number of results (default: 50)")
    int limit = 50;

    @Option(names = {"--level"},
            description = "Search level: FAST_ONLY, INCLUDE_DATA, EXHAUSTIVE_OFFCHAIN")
    SearchLevel searchLevel = SearchLevel.INCLUDE_DATA;

    @Option(names = {"--fast"},
            description = "Fast search - keywords only (equivalent to --level FAST_ONLY)")
    boolean fastSearch = false;

    @Option(names = {"--complete"},
            description = "Complete search - all content including off-chain (equivalent to --level EXHAUSTIVE_OFFCHAIN)")
    boolean completeSearch = false;
    
    @Option(names = {"--category"}, 
            description = "Search by content category")
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

    @Option(names = {"--validate-term"},
            description = "Validate search term before searching")
    boolean validateTerm = false;
    
    @Option(names = {"-u", "--username"}, 
            description = "Username for user-specific searches")
    String username;
    
    @Option(names = {"--password"}, 
            description = "Password for encrypted content search")
    String password;
    
    @Option(names = {"--my-blocks"}, 
            description = "Show only blocks created by the specified username")
    boolean myBlocksOnly = false;
    
    @Option(names = {"--received"}, 
            description = "Show only blocks received by the specified username")
    boolean receivedOnly = false;
    
    @Option(names = {"--encrypted-only"}, 
            description = "Show only encrypted blocks")
    boolean encryptedOnly = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output results in JSON format")
    boolean json = false;
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed block information")
    boolean detailed = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    boolean verbose = false;
    
    private UserFriendlyEncryptionAPI encryptionAPI;
    private SearchSpecialistAPI searchAPI;
    
    @Override
    public void run() {
        try {
            LoggingUtil.logOperationStart(logger, "search with modern APIs", verbose || BlockchainCLI.verbose);

            // Handle contentSearch as alternative to searchQuery
            if (contentSearch != null && !contentSearch.trim().isEmpty()) {
                if (searchQuery == null || searchQuery.trim().isEmpty()) {
                    searchQuery = contentSearch;
                }
            }

            // Handle fast/complete search level shortcuts
            if (fastSearch) {
                searchLevel = SearchLevel.FAST_ONLY;
            } else if (completeSearch) {
                searchLevel = SearchLevel.EXHAUSTIVE_OFFCHAIN;
            }

            // Validate search term if requested
            if (validateTerm && searchQuery != null && !searchQuery.trim().isEmpty()) {
                if (searchQuery.length() < 2) {
                    BlockchainCLI.error("❌ Search term too short (minimum 2 characters)");
                    ExitUtil.exit(1);
                }
                if (searchQuery.length() > 500) {
                    BlockchainCLI.error("❌ Search term too long (maximum 500 characters)");
                    ExitUtil.exit(1);
                }
                verboseLog("✅ Search term validated: " + searchQuery.length() + " characters");
            }

            Blockchain blockchain = new Blockchain();
            encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);

            // Initialize SearchSpecialistAPI with required constructor parameters
            // Generate a temporary key pair for search operations if not provided
            KeyPair tempKeyPair = CryptoUtil.generateKeyPair();

            if (password != null && !password.trim().isEmpty()) {
                // Use secure search with password
                searchAPI = new SearchSpecialistAPI(blockchain, password, tempKeyPair.getPrivate());
            } else {
                // Use public search only - provide empty password and temp key
                searchAPI = new SearchSpecialistAPI(blockchain, "", tempKeyPair.getPrivate());
            }

            List<Block> results = new ArrayList<>();
            String searchDescription = "unknown";
            long startTime = System.nanoTime();
            
            // Handle special case searches first
            if (hashSearch != null && !hashSearch.trim().isEmpty()) {
                searchDescription = "hash";
                LoggingUtil.verboseLog(logger, "Searching by hash: " + hashSearch, verbose || BlockchainCLI.verbose);
                Block block = blockchain.getBlockByHash(hashSearch);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (blockNumber != null) {
                searchDescription = "block-number";
                verboseLog("Searching by block number: " + blockNumber);
                Block block = blockchain.getBlock(blockNumber);
                if (block != null) {
                    results.add(block);
                }
                
            } else if (encryptedOnly) {
                searchDescription = "encrypted-blocks";
                verboseLog("Finding encrypted blocks");
                
                // Use available search method for encrypted data
                String searchTerm = username != null ? username : "";
                results = encryptionAPI.findEncryptedData(searchTerm);
                
            } else if (categorySearch != null && !categorySearch.trim().isEmpty()) {
                searchDescription = "category";
                verboseLog("Searching by category: " + categorySearch);
                
                // Use specific category search from blockchain
                results = blockchain.searchByCategory(categorySearch.toUpperCase());
                
            } else if (dateFrom != null || dateTo != null || datetimeFrom != null || datetimeTo != null) {
                searchDescription = "date-range";
                verboseLog("Searching by date/datetime range");
                results = searchByDateRange(blockchain);
                
            } else if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                searchDescription = searchType.toLowerCase();
                verboseLog("Performing " + searchType + " search for: '" + searchQuery + "'");
                
                // Use SearchSpecialistAPI based on search type
                List<SearchFrameworkEngine.EnhancedSearchResult> searchResults = null;
                
                switch (searchType.toUpperCase()) {
                    case "SIMPLE":
                        searchResults = searchAPI.searchAll(searchQuery, limit);
                        break;
                        
                    case "SECURE":
                        if (password == null || password.trim().isEmpty()) {
                            BlockchainCLI.error("❌ Password required for secure search");
                            BlockchainCLI.error("   Use --password to provide password");
                            ExitUtil.exit(1);
                        }
                        searchResults = searchAPI.searchSecure(searchQuery, password, limit);
                        break;
                        
                    case "INTELLIGENT":
                        String searchPassword = password != null ? password : "";
                        searchResults = searchAPI.searchIntelligent(searchQuery, searchPassword, limit);
                        break;
                        
                    case "ADVANCED":
                        if (password == null) password = "";
                        SearchFrameworkEngine.SearchResult advancedResult = searchAPI.searchAdvanced(
                            searchQuery, password, null, limit);
                        if (advancedResult != null && advancedResult.getResults() != null) {
                            searchResults = advancedResult.getResults();
                        }
                        break;
                        
                    default:
                        BlockchainCLI.error("❌ Unknown search type: " + searchType);
                        ExitUtil.exit(1);
                }
                
                // Convert EnhancedSearchResult to Block objects
                if (searchResults != null) {
                    for (SearchFrameworkEngine.EnhancedSearchResult result : searchResults) {
                        Block block = blockchain.getBlockByHash(result.getBlockHash());
                        if (block != null) {
                            results.add(block);
                        }
                    }
                }
                
            } else {
                BlockchainCLI.error("❌ No search criteria specified");
                displaySearchHelp();
                ExitUtil.exit(1);
            }
            
            long endTime = System.nanoTime();
            long searchTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            LoggingUtil.logSearchOperation(logger, searchType, searchQuery, results.size(), searchTime, verbose || BlockchainCLI.verbose);
            
            // Apply limit if needed
            boolean isLimited = false;
            if (results.size() > limit) {
                results = results.subList(0, limit);
                isLimited = true;
            }
            
            if (json) {
                outputJson(results, searchDescription, searchTime, isLimited);
            } else {
                outputText(results, searchDescription, searchTime, isLimited);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Search failed: Security error - " + e.getMessage());
            logger.error("Search failed with security error", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Search failed: Runtime error - " + e.getMessage());
            logger.error("Search failed with runtime error", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Search failed: Unexpected error - " + e.getMessage());
            logger.error("Search failed with unexpected error", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void displaySearchHelp() {
        BlockchainCLI.error("   Available search types:");
        BlockchainCLI.error("   • SIMPLE: Fast public metadata search (default)");
        BlockchainCLI.error("   • SECURE: Encrypted content search (requires --password)");
        BlockchainCLI.error("   • INTELLIGENT: Adaptive search strategy");
        BlockchainCLI.error("   • ADVANCED: Full-featured search with level control");
        BlockchainCLI.error("");
        BlockchainCLI.error("   Special searches:");
        BlockchainCLI.error("   • --hash <block-hash>: Find specific block");
        BlockchainCLI.error("   • --block-number <number>: Find by number");
        BlockchainCLI.error("   • --my-blocks --username <user>: All blocks by user");
        BlockchainCLI.error("   • --received --username <user>: All blocks received by user");
        BlockchainCLI.error("   • --encrypted-only: All encrypted blocks");
        BlockchainCLI.error("   • --category <category>: Search by category");
    }
    
    private List<Block> searchByDateRange(Blockchain blockchain) {
        try {
            // Prioritize datetime if specified
            if (datetimeFrom != null || datetimeTo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                LocalDateTime fromDateTime = datetimeFrom != null
                    ? LocalDateTime.parse(datetimeFrom, formatter)
                    : LocalDateTime.MIN;
                LocalDateTime toDateTime = datetimeTo != null
                    ? LocalDateTime.parse(datetimeTo, formatter)
                    : LocalDateTime.MAX;

                // Convert to LocalDate for the blockchain API
                LocalDate fromDate = fromDateTime.toLocalDate();
                LocalDate toDate = toDateTime.toLocalDate();

                verboseLog("Searching by datetime range: " + fromDateTime + " to " + toDateTime);

                return blockchain.getBlocksByDateRange(fromDate, toDate);
            } else {
                // Use date-only search
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate fromDate = dateFrom != null ? LocalDate.parse(dateFrom, formatter) : LocalDate.MIN;
                LocalDate toDate = dateTo != null ? LocalDate.parse(dateTo, formatter) : LocalDate.MAX;

                verboseLog("Searching by date range: " + fromDate + " to " + toDate);

                return blockchain.getBlocksByDateRange(fromDate, toDate);
            }

        } catch (DateTimeParseException e) {
            BlockchainCLI.error("❌ Invalid date/datetime format");
            BlockchainCLI.error("   Use yyyy-MM-dd for dates or yyyy-MM-dd HH:mm for datetimes");
            ExitUtil.exit(1);
            return new ArrayList<>();
        }
    }
    
    private void outputText(List<Block> results, String searchDescription, long searchTime, boolean isLimited) {
        System.out.println("🔍 Search Results (" + searchDescription + ")");
        System.out.println("=" .repeat(60));
        
        if (results.isEmpty()) {
            System.out.println("No blocks found matching search criteria.");
            if (searchDescription.contains("secure") && password == null) {
                System.out.println("\n💡 Try using --password for encrypted content search");
            }
            return;
        }
        
        System.out.println("Found " + results.size() + " block(s) in " + searchTime + "ms");
        if (isLimited) {
            System.out.println("💡 Results limited to " + limit + ". Use --limit to see more.");
        }
        System.out.println();
        
        
        for (int i = 0; i < results.size(); i++) {
            Block block = results.get(i);
            
            System.out.println("📦 Block #" + block.getBlockNumber());
            System.out.println("   📅 Timestamp: " + FormatUtil.formatTimestamp(block.getTimestamp()));
            System.out.println("   🔗 Hash: " + FormatUtil.truncateHash(block.getHash()));
            
            // Show owner information (simplified)
            if (block.getSignerPublicKey() != null) {
                System.out.println("   🔑 Signer: " + FormatUtil.truncateKey(block.getSignerPublicKey()));
            }
            
            if (detailed) {
                System.out.println("   🔗 Previous Hash: " + FormatUtil.truncateHash(block.getPreviousHash()));
                System.out.println("   🔑 Signer Key: " + FormatUtil.truncateKey(block.getSignerPublicKey()));
                System.out.println("   ✍️  Signature: " + FormatUtil.truncateHash(block.getSignature()));
            }
            
            // Show metadata
            if (block.getContentCategory() != null) {
                System.out.println("   📂 Category: " + block.getContentCategory());
            }
            
            if (detailed) {
                if (block.getManualKeywords() != null && !block.getManualKeywords().trim().isEmpty()) {
                    System.out.println("   🏷️  Keywords: " + block.getManualKeywords());
                }
                if (block.getAutoKeywords() != null && !block.getAutoKeywords().trim().isEmpty()) {
                    System.out.println("   🤖 Auto Keywords: " + block.getAutoKeywords());
                }
            }
            
            // Show encryption status
            if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
                System.out.println("   🔐 Encrypted: Yes");
            }
            
            // Show off-chain information
            if (block.hasOffChainData()) {
                var offChainData = block.getOffChainData();
                System.out.println("   💾 Off-chain: " + FormatUtil.formatBytes(offChainData.getFileSize()) + " (encrypted)");
                if (detailed) {
                    System.out.println("   🔐 Content Type: " + offChainData.getContentType());
                    System.out.println("   📁 File Path: " + offChainData.getFilePath());
                }
            }
            
            // Show data content
            String data = block.getData();
            if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
                System.out.println("   📝 Data: [Encrypted content]");
            } else if (block.hasOffChainData()) {
                System.out.println("   📝 Data: [Stored off-chain]");
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
        System.out.println("⚡ Search performance: " + searchTime + "ms (" + searchType + ")");
    }
    
    // Removed getBlockOwner method as it depends on non-available APIs
    
    private void outputJson(List<Block> results, String searchDescription, long searchTime, boolean isLimited) {
        System.out.println("{");
        System.out.println("  \"searchType\": \"" + searchDescription + "\",");
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
            
            // Owner information simplified
            if (block.getSignerPublicKey() != null) {
                System.out.println("      \"signerKey\": \"" + FormatUtil.truncateKey(block.getSignerPublicKey()) + "\",");
            }
            
            System.out.println("      \"encrypted\": " + (block.getIsEncrypted() != null ? block.getIsEncrypted() : false) + ",");
            
            System.out.println("      \"data\": \"" + FormatUtil.escapeJson(block.getData()) + "\",");
            System.out.println("      \"signerPublicKey\": \"" + block.getSignerPublicKey() + "\",");
            System.out.println("      \"signature\": \"" + block.getSignature() + "\",");
            
            // Metadata
            if (block.getContentCategory() != null) {
                System.out.println("      \"category\": \"" + block.getContentCategory() + "\",");
            }
            if (block.getManualKeywords() != null) {
                System.out.println("      \"keywords\": \"" + FormatUtil.escapeJson(block.getManualKeywords()) + "\",");
            }
            
            // Off-chain information
            System.out.println("      \"hasOffChainData\": " + block.hasOffChainData());
            if (block.hasOffChainData()) {
                var offChainData = block.getOffChainData();
                System.out.println("      ,\"offChainData\": {");
                System.out.println("        \"fileSize\": " + offChainData.getFileSize() + ",");
                System.out.println("        \"contentType\": \"" + offChainData.getContentType() + "\",");
                System.out.println("        \"filePath\": \"" + FormatUtil.escapeJson(offChainData.getFilePath()) + "\",");
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
    
    // Utility methods removed - now using FormatUtil
    
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
        logger.debug("🔍 {}", message);
    }
}