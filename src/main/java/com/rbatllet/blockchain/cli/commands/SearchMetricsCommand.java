package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.service.SearchMetrics;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.format.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to display search performance metrics and statistics
 */
@Command(name = "search-metrics", 
         description = "Display search performance metrics and statistics")
public class SearchMetricsCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchMetricsCommand.class);
    
    @Option(names = {"--reset"}, 
            description = "Reset all search metrics")
    boolean reset = false;
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed metrics breakdown")
    boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output metrics in JSON format")
    boolean json = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    boolean verbose = false;
    
    @Override
    public void run() {
        try {
            verboseLog("Retrieving search metrics...");
            
            // Initialize blockchain and encryption API to get metrics
            Blockchain blockchain = new Blockchain();
            UserFriendlyEncryptionAPI encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);
            SearchMetrics searchMetrics = encryptionAPI.getSearchMetrics();
            
            if (reset) {
                verboseLog("Resetting search metrics...");
                searchMetrics.reset();
                BlockchainCLI.success("Search metrics have been reset");
                logger.info("Search metrics reset by user");
                return;
            }
            
            if (json) {
                outputJson(searchMetrics);
            } else {
                outputText(searchMetrics);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to retrieve search metrics: " + e.getMessage());
            logger.error("Failed to retrieve search metrics", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void outputText(SearchMetrics searchMetrics) {
        if (searchMetrics == null) {
            throw new IllegalArgumentException("SearchMetrics cannot be null");
        }

        System.out.println("📊 Search Performance Metrics");
        System.out.println("=" .repeat(60));

        // Generate and display the full report
        String report = searchMetrics.getPerformanceReport();
        if (report == null) {
            throw new IllegalStateException("Performance report cannot be null");
        }
        System.out.println(report);

        if (detailed) {
            System.out.println("\n🔍 Detailed Metrics:");
            System.out.println("─" .repeat(40));

            // Additional detailed information
            var stats = searchMetrics.getSearchTypeStats();
            if (stats == null) {
                throw new IllegalStateException("Search type stats cannot be null");
            }
            if (!stats.isEmpty()) {
                System.out.println("\n📋 Search Type Breakdown:");
                for (var entry : stats.entrySet()) {
                    var perfStats = entry.getValue();
                    System.out.println("  🔹 " + entry.getKey() + ":");
                    System.out.println("     Searches: " + perfStats.getSearches());
                    System.out.println("     Avg Time: " + String.format("%.2f ms", perfStats.getAverageTimeMs()));
                    System.out.println("     Total Time: " + String.format("%.2f ms", (double)perfStats.getTotalTimeMs()));
                    System.out.println("     Cache Hits: " + String.format("%.1f%%", perfStats.getCacheHitRate()));
                    System.out.println("     Avg Results: " + String.format("%.1f", perfStats.getAverageResults()));
                }
            }
            
            System.out.println("\n⚡ Performance Insights:");
            
            // Calculate some insights
            double totalSearches = stats.values().stream()
                .mapToDouble(s -> s.getSearches())
                .sum();
            
            if (totalSearches > 0) {
                var fastestType = stats.entrySet().stream()
                    .min((a, b) -> Double.compare(a.getValue().getAverageTimeMs(), b.getValue().getAverageTimeMs()))
                    .map(entry -> entry.getKey())
                    .orElse("N/A");
                
                var slowestType = stats.entrySet().stream()
                    .max((a, b) -> Double.compare(a.getValue().getAverageTimeMs(), b.getValue().getAverageTimeMs()))
                    .map(entry -> entry.getKey())
                    .orElse("N/A");
                
                System.out.println("  🚀 Fastest search type: " + fastestType);
                System.out.println("  🐌 Slowest search type: " + slowestType);
                
                double overallCacheHitRate = stats.values().stream()
                    .mapToDouble(s -> (s.getCacheHitRate() / 100.0) * s.getSearches())
                    .sum() / totalSearches;
                
                System.out.println("  📈 Overall cache hit rate: " + String.format("%.1f%%", overallCacheHitRate * 100));
            } else {
                System.out.println("  ℹ️  No search operations recorded yet");
            }
        }
        
        System.out.println("\n💡 Tips:");
        System.out.println("  • Use --reset to clear all metrics");
        System.out.println("  • Use --json for machine-readable output");
        System.out.println("  • Run searches to populate metrics");
    }
    
    private void outputJson(SearchMetrics searchMetrics) {
        if (searchMetrics == null) {
            throw new IllegalArgumentException("SearchMetrics cannot be null");
        }

        System.out.println("{");
        System.out.println("  \"searchMetrics\": {");

        var stats = searchMetrics.getSearchTypeStats();
        if (stats == null) {
            throw new IllegalStateException("Search type stats cannot be null");
        }
        
        // Overall stats
        double totalSearches = stats.values().stream()
            .mapToDouble(s -> s.getSearches())
            .sum();
        
        double totalTime = stats.values().stream()
            .mapToDouble(s -> s.getTotalTimeMs())
            .sum();
        
        double avgTime = totalSearches > 0 ? totalTime / totalSearches : 0;
        
        double overallCacheHitRate = totalSearches > 0 ? 
            stats.values().stream()
                .mapToDouble(s -> (s.getCacheHitRate() / 100.0) * s.getSearches())
                .sum() / totalSearches : 0;
        
        System.out.println("    \"totalSearches\": " + (int)totalSearches + ",");
        System.out.println("    \"totalTimeMs\": " + String.format("%.2f", totalTime) + ",");
        System.out.println("    \"averageTimeMs\": " + String.format("%.2f", avgTime) + ",");
        System.out.println("    \"overallCacheHitRate\": " + String.format("%.3f", overallCacheHitRate) + ",");
        
        // Search type breakdown
        System.out.println("    \"searchTypes\": {");
        
        boolean first = true;
        for (var entry : stats.entrySet()) {
            if (!first) System.out.println(",");
            first = false;

            var perfStats = entry.getValue();
            String searchType = entry.getKey() != null ? FormatUtil.escapeJson(entry.getKey()) : "";
            System.out.println("      \"" + searchType + "\": {");
            System.out.println("        \"searchCount\": " + perfStats.getSearches() + ",");
            System.out.println("        \"totalTimeMs\": " + String.format("%.2f", (double)perfStats.getTotalTimeMs()) + ",");
            System.out.println("        \"averageTimeMs\": " + String.format("%.2f", perfStats.getAverageTimeMs()) + ",");
            System.out.println("        \"cacheHitRate\": " + String.format("%.3f", perfStats.getCacheHitRate() / 100.0) + ",");
            System.out.println("        \"averageResults\": " + String.format("%.2f", perfStats.getAverageResults()) + ",");
            System.out.println("        \"minTimeMs\": " + String.format("%.2f", (double)perfStats.getMinTimeMs()) + ",");
            System.out.println("        \"maxTimeMs\": " + String.format("%.2f", (double)perfStats.getMaxTimeMs()));
            System.out.print("      }");
        }
        
        if (!first) System.out.println();
        System.out.println("    }");
        System.out.println("  },");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void verboseLog(String message) {
        if (verbose || BlockchainCLI.verbose) {
            System.out.println("🔍 " + message);
        }
        logger.debug("🔍 {}", message);
    }
}