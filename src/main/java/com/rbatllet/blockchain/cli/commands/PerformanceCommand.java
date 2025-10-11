package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.service.PerformanceMetricsService;
import com.rbatllet.blockchain.service.SearchMetrics;
import com.rbatllet.blockchain.service.MemoryManagementService;
import com.rbatllet.blockchain.service.AlertService;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Command to display comprehensive system performance metrics and statistics
 */
@Command(name = "performance", 
         description = "Display comprehensive system performance metrics and statistics")
public class PerformanceCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceCommand.class);
    
    @Option(names = {"--system"}, 
            description = "Show system metrics (memory, threads, health)")
    boolean system = false;
    
    @Option(names = {"--search"}, 
            description = "Show search performance metrics")
    boolean search = false;
    
    @Option(names = {"--cache"}, 
            description = "Show cache performance and efficiency")
    boolean cache = false;
    
    @Option(names = {"--alerts"}, 
            description = "Show active alerts and statistics")
    boolean alerts = false;
    
    @Option(names = {"--memory"}, 
            description = "Show detailed memory management metrics")
    boolean memory = false;
    
    @Option(names = {"--reset"}, 
            description = "Reset performance metrics")
    boolean reset = false;
    
    @Option(names = {"--reset-search"}, 
            description = "Reset only search metrics")
    boolean resetSearch = false;
    
    @Option(names = {"--reset-alerts"}, 
            description = "Reset only alert statistics")
    boolean resetAlerts = false;
    
    @Option(names = {"--format"}, 
            description = "Output format: text, json, csv, summary")
    String format = "text";
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed performance breakdown")
    boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output in JSON format")
    boolean json = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    boolean verbose = false;
    
    @Override
    public void run() {
        try {
            LoggingUtil.logOperationStart(logger, "performance metrics collection", verbose || BlockchainCLI.verbose);
            
            // Initialize services
            Blockchain blockchain = new Blockchain();
            UserFriendlyEncryptionAPI encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);
            PerformanceMetricsService performanceService = PerformanceMetricsService.getInstance();
            SearchMetrics searchMetrics = encryptionAPI.getSearchMetrics();
            // MemoryManagementService now uses static methods
            AlertService alertService = AlertService.getInstance();
            
            // Handle reset operations first
            if (reset) {
                LoggingUtil.verboseLog(logger, "Resetting all performance metrics...", verbose || BlockchainCLI.verbose);
                performanceService.resetMetrics();
                searchMetrics.reset();
                alertService.resetStatistics();
                MemoryManagementService.forceCleanup();
                BlockchainCLI.success("📊 All performance metrics have been reset");
                logger.info("All performance metrics reset by user");
                return;
            }
            
            if (resetSearch) {
                LoggingUtil.verboseLog(logger, "Resetting search metrics...", verbose || BlockchainCLI.verbose);
                searchMetrics.reset();
                BlockchainCLI.success("🔍 Search metrics have been reset");
                logger.info("Search metrics reset by user");
                return;
            }
            
            if (resetAlerts) {
                LoggingUtil.verboseLog(logger, "Resetting alert statistics...", verbose || BlockchainCLI.verbose);
                alertService.resetStatistics();
                BlockchainCLI.success("🚨 Alert statistics have been reset");
                logger.info("Alert statistics reset by user");
                return;
            }
            
            // Handle JSON output override
            if (json) {
                format = "json";
            }
            
            // Display metrics based on options
            if (!system && !search && !cache && !alerts && !memory) {
                // Show all metrics if no specific category selected
                outputOverallMetrics(performanceService, searchMetrics, alertService);
            } else {
                // Show specific categories
                if (system) {
                    outputSystemMetrics(performanceService);
                }
                if (search) {
                    outputSearchMetrics(searchMetrics);
                }
                if (cache) {
                    outputCacheMetrics(searchMetrics);
                }
                if (alerts) {
                    outputAlertMetrics(alertService);
                }
                if (memory) {
                    outputMemoryMetrics();
                }
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("❌ Failed to retrieve performance metrics: " + e.getMessage());
            logger.error("Failed to retrieve performance metrics", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void outputOverallMetrics(PerformanceMetricsService performanceService, 
                                     SearchMetrics searchMetrics, 
                                     AlertService alertService) {
        
        if ("json".equals(format)) {
            outputOverallJson(performanceService, searchMetrics, alertService);
        } else {
            outputOverallText(performanceService, searchMetrics, alertService);
        }
    }
    
    private void outputOverallText(PerformanceMetricsService performanceService, 
                                  SearchMetrics searchMetrics, 
                                  AlertService alertService) {
        
        System.out.println("📊 System Performance Overview");
        System.out.println("=" .repeat(80));
        System.out.println("📅 Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println();
        
        // Performance Overview
        System.out.println("⚡ Performance Summary");
        System.out.println("─" .repeat(50));
        String performanceReport = performanceService.getPerformanceReport();
        System.out.println(performanceReport);
        
        // System Health
        System.out.println("\n💚 System Health");
        System.out.println("─" .repeat(50));
        String healthSummary = performanceService.getSystemHealthSummary();
        System.out.println(healthSummary);
        
        // Search Performance (Brief)
        System.out.println("\n🔍 Search Performance");
        System.out.println("─" .repeat(50));
        System.out.printf("Average Search Time: %.2f ms%n", searchMetrics.getAverageSearchTimeMs());
        System.out.printf("Cache Hit Rate: %.1f%%%n", searchMetrics.getCacheHitRate());
        System.out.printf("Total Searches: %d%n", searchMetrics.getTotalSearches());
        
        // Memory Status (Brief)
        System.out.println("\n💾 Memory Status");
        System.out.println("─" .repeat(50));
        var memStats = MemoryManagementService.getMemoryStats();
        System.out.printf("Used Memory: %.1f MB (%.1f%%)%n", 
            memStats.getUsedMemory() / (1024.0 * 1024.0), memStats.getUsagePercentage());
        System.out.printf("Free Memory: %.1f MB%n", memStats.getFreeMemory() / (1024.0 * 1024.0));
        System.out.printf("Max Memory: %.1f MB%n", memStats.getMaxMemory() / (1024.0 * 1024.0));
        
        // Active Alerts
        System.out.println("\n🚨 Alert Status");
        System.out.println("─" .repeat(50));
        String alertsSummary = performanceService.getAlertsSummary();
        if (alertsSummary.trim().isEmpty()) {
            System.out.println("✅ No active alerts");
        } else {
            System.out.println(alertsSummary);
        }
        
        if (detailed) {
            System.out.println("\n📋 Tips for Performance Analysis:");
            System.out.println("  • Use --system for detailed system metrics");
            System.out.println("  • Use --search for comprehensive search analysis");  
            System.out.println("  • Use --memory for memory management details");
            System.out.println("  • Use --alerts for alert statistics");
            System.out.println("  • Use --json for machine-readable output");
            System.out.println("  • Use --reset to clear all metrics");
        }
    }
    
    private void outputOverallJson(PerformanceMetricsService performanceService, 
                                  SearchMetrics searchMetrics, 
                                  AlertService alertService) {
        
        System.out.println("{");
        System.out.println("  \"performanceOverview\": {");
        System.out.println("    \"timestamp\": \"" + LocalDateTime.now() + "\",");
        
        // System Health
        var memStats = MemoryManagementService.getMemoryStats();
        System.out.println("    \"systemHealth\": {");
        System.out.printf("      \"memoryUsagePercent\": %.1f,%n", memStats.getUsagePercentage());
        System.out.printf("      \"usedMemoryMB\": %.1f,%n", memStats.getUsedMemory() / (1024.0 * 1024.0));
        System.out.printf("      \"freeMemoryMB\": %.1f,%n", memStats.getFreeMemory() / (1024.0 * 1024.0));
        System.out.printf("      \"maxMemoryMB\": %.1f%n", memStats.getMaxMemory() / (1024.0 * 1024.0));
        System.out.println("    },");
        
        // Search Performance
        System.out.println("    \"searchPerformance\": {");
        System.out.printf("      \"averageSearchTimeMs\": %.2f,%n", searchMetrics.getAverageSearchTimeMs());
        System.out.printf("      \"cacheHitRate\": %.3f,%n", searchMetrics.getCacheHitRate() / 100.0);
        System.out.printf("      \"totalSearches\": %d%n", searchMetrics.getTotalSearches());
        System.out.println("    },");
        
        // Alert Statistics
        String alertStats = alertService.getAlertStatistics();
        System.out.println("    \"alerts\": " + alertStats);
        
        System.out.println("  }");
        System.out.println("}");
    }
    
    private void outputSystemMetrics(PerformanceMetricsService performanceService) {
        
        if ("json".equals(format)) {
            System.out.println("{ \"systemMetrics\": ");
            System.out.println(performanceService.getSystemHealthSummary());
            System.out.println("}");
        } else {
            System.out.println("🖥️  System Performance Metrics");
            System.out.println("=" .repeat(60));
            
            // Detailed performance report
            String report = performanceService.getPerformanceReport();
            System.out.println(report);
            
            // Memory details
            var memStats = MemoryManagementService.getMemoryStats();
            System.out.println("\n💾 Memory Management Details");
            System.out.println("─" .repeat(40));
            System.out.printf("Used Memory: %.1f MB%n", memStats.getUsedMemory() / (1024.0 * 1024.0));
            System.out.printf("Free Memory: %.1f MB%n", memStats.getFreeMemory() / (1024.0 * 1024.0));
            System.out.printf("Total Memory: %.1f MB%n", memStats.getTotalMemory() / (1024.0 * 1024.0));
            System.out.printf("Max Memory: %.1f MB%n", memStats.getMaxMemory() / (1024.0 * 1024.0));
            System.out.printf("Memory Usage: %.1f%%%n", memStats.getUsagePercentage());
            System.out.printf("Service Running: %s%n", memStats.isServiceRunning() ? "✅ Yes" : "❌ No");
            
            if (memStats.getLastCleanupTime() > 0) {
                System.out.printf("Last Cleanup: %s%n", new java.util.Date(memStats.getLastCleanupTime()));
            }
            if (memStats.getLastForceGCTime() > 0) {
                System.out.printf("Last GC: %s%n", new java.util.Date(memStats.getLastForceGCTime()));
            }
        }
    }
    
    private void outputSearchMetrics(SearchMetrics searchMetrics) {
        if ("json".equals(format)) {
            String jsonReport = searchMetrics.getFormattedReport(SearchMetrics.ReportFormat.JSON);
            System.out.println(jsonReport);
        } else {
            System.out.println("🔍 Search Performance Metrics");
            System.out.println("=" .repeat(60));
            
            if (detailed) {
                String detailedReport = searchMetrics.getFormattedReport(SearchMetrics.ReportFormat.DETAILED);
                System.out.println(detailedReport);
                
                // Performance insights
                System.out.println("\n💡 Performance Insights");
                System.out.println("─" .repeat(40));
                String insights = searchMetrics.getPerformanceInsights().toString();
                System.out.println(insights);
            } else {
                String summaryReport = searchMetrics.getFormattedReport(SearchMetrics.ReportFormat.SUMMARY);
                System.out.println(summaryReport);
            }
        }
    }
    
    private void outputCacheMetrics(SearchMetrics searchMetrics) {
        System.out.println("🗄️  Cache Performance Metrics");
        System.out.println("=" .repeat(60));
        
        if ("json".equals(format)) {
            System.out.println("{");
            System.out.println("  \"cacheMetrics\": {");
            System.out.printf("    \"hitRate\": %.3f,%n", searchMetrics.getCacheHitRate() / 100.0);
            System.out.printf("    \"totalSearches\": %d,%n", searchMetrics.getTotalSearches());
            System.out.printf("    \"averageSearchTimeMs\": %.2f%n", searchMetrics.getAverageSearchTimeMs());
            System.out.println("  }");
            System.out.println("}");
        } else {
            System.out.printf("📊 Cache Hit Rate: %.1f%%%n", searchMetrics.getCacheHitRate());
            System.out.printf("🔍 Total Searches: %d%n", searchMetrics.getTotalSearches());
            System.out.printf("⏱️  Average Response: %.2f ms%n", searchMetrics.getAverageSearchTimeMs());
            
            // Additional cache insights
            var searchStats = searchMetrics.getSearchTypeStats();
            if (!searchStats.isEmpty()) {
                System.out.println("\n📋 Cache Performance by Search Type:");
                System.out.println("─" .repeat(40));
                for (var entry : searchStats.entrySet()) {
                    var stats = entry.getValue();
                    System.out.printf("%-15s: %.1f%% hit rate (%d searches)%n", 
                        entry.getKey(), stats.getCacheHitRate(), stats.getSearches());
                }
            }
            
            if (detailed) {
                System.out.println("\n💡 Cache Optimization Tips:");
                System.out.println("  • High hit rates (>80%) indicate good cache utilization");
                System.out.println("  • Low hit rates may suggest need for cache tuning");
                System.out.println("  • Monitor search patterns for cache sizing");
            }
        }
    }
    
    private void outputAlertMetrics(AlertService alertService) {
        System.out.println("🚨 Alert Statistics");
        System.out.println("=" .repeat(60));
        
        String alertStats = alertService.getAlertStatistics();
        
        if ("json".equals(format)) {
            System.out.println(alertStats);
        } else {
            // Parse and display alert statistics in human-readable format
            System.out.println("Alert Statistics Summary:");
            System.out.println(alertStats);
            
            if (detailed) {
                System.out.println("\n💡 Alert Management Tips:");
                System.out.println("  • Monitor CRITICAL alerts immediately");
                System.out.println("  • Review WARNING alerts regularly");
                System.out.println("  • Use --reset-alerts to clear statistics");
                System.out.println("  • Check logs for detailed alert information");
            }
        }
    }
    
    private void outputMemoryMetrics() {
        System.out.println("💾 Memory Management Metrics");
        System.out.println("=" .repeat(60));
        
        var memStats = MemoryManagementService.getMemoryStats();
        
        if ("json".equals(format)) {
            System.out.println("{");
            System.out.println("  \"memoryMetrics\": {");
            System.out.printf("    \"usedMemoryMB\": %.1f,%n", memStats.getUsedMemory() / (1024.0 * 1024.0));
            System.out.printf("    \"freeMemoryMB\": %.1f,%n", memStats.getFreeMemory() / (1024.0 * 1024.0));
            System.out.printf("    \"totalMemoryMB\": %.1f,%n", memStats.getTotalMemory() / (1024.0 * 1024.0));
            System.out.printf("    \"maxMemoryMB\": %.1f,%n", memStats.getMaxMemory() / (1024.0 * 1024.0));
            System.out.printf("    \"memoryUsagePercentage\": %.1f,%n", memStats.getUsagePercentage());
            System.out.printf("    \"serviceRunning\": %s,%n", memStats.isServiceRunning());
            if (memStats.getLastCleanupTime() > 0) {
                System.out.printf("    \"lastCleanupTime\": \"%s\",%n", new java.util.Date(memStats.getLastCleanupTime()));
            }
            if (memStats.getLastForceGCTime() > 0) {
                System.out.printf("    \"lastGCTime\": \"%s\"%n", new java.util.Date(memStats.getLastForceGCTime()));
            }
            System.out.println("  }");
            System.out.println("}");
        } else {
            System.out.println("Current Memory Status:");
            System.out.println("─" .repeat(40));
            
            // Memory bars for visual representation
            printMemoryBar("Used Memory", memStats.getUsedMemory() / (1024.0 * 1024.0), memStats.getMaxMemory() / (1024.0 * 1024.0), "MB");
            printMemoryBar("Memory Usage", memStats.getUsagePercentage(), 100.0, "%");
            
            System.out.println("\nDetailed Memory Statistics:");
            System.out.println("─" .repeat(40));
            System.out.printf("Used: %.1f MB%n", memStats.getUsedMemory() / (1024.0 * 1024.0));
            System.out.printf("Free: %.1f MB%n", memStats.getFreeMemory() / (1024.0 * 1024.0));
            System.out.printf("Total: %.1f MB%n", memStats.getTotalMemory() / (1024.0 * 1024.0));
            System.out.printf("Max: %.1f MB%n", memStats.getMaxMemory() / (1024.0 * 1024.0));
            System.out.printf("Usage: %.1f%%%n", memStats.getUsagePercentage());
            System.out.printf("Service: %s%n", memStats.isServiceRunning() ? "✅ Running" : "❌ Stopped");
            
            if (memStats.getLastCleanupTime() > 0) {
                System.out.printf("Last Cleanup: %s%n", new java.util.Date(memStats.getLastCleanupTime()));
            }
            if (memStats.getLastForceGCTime() > 0) {
                System.out.printf("Last GC: %s%n", new java.util.Date(memStats.getLastForceGCTime()));
            }
            
            if (detailed) {
                System.out.println("\n🧹 Memory Management Options:");
                System.out.println("  • Force cleanup: Use memoryService.forceCleanup()");
                System.out.println("  • Monitor usage: Check percentage regularly");
                System.out.println("  • Automatic cleanup: Service handles routine maintenance");
                
                // Memory health assessment
                double usage = memStats.getUsagePercentage();
                if (usage > 90) {
                    System.out.println("\n⚠️  HIGH MEMORY USAGE - Consider forcing cleanup");
                } else if (usage > 70) {
                    System.out.println("\n📊 MODERATE MEMORY USAGE - Monitor closely");
                } else {
                    System.out.println("\n✅ HEALTHY MEMORY USAGE");
                }
            }
        }
    }
    
    private void printMemoryBar(String label, double value, double maxValue, String unit) {
        System.out.printf("%-15s: ", label);
        
        int barLength = 30;
        int filled = (int)((value / maxValue) * barLength);
        filled = Math.min(filled, barLength);
        filled = Math.max(0, filled);
        
        // Color coding for memory usage
        String color = "";
        double percentage = value / maxValue;
        if (percentage < 0.6) {
            color = "\033[32m"; // Green
        } else if (percentage < 0.8) {
            color = "\033[33m"; // Yellow
        } else {
            color = "\033[31m"; // Red
        }
        
        System.out.print(color);
        System.out.print("█".repeat(filled));
        System.out.print("░".repeat(barLength - filled));
        System.out.print("\033[0m"); // Reset color
        System.out.printf(" %.1f %s%n", value, unit);
    }
    
    // Verbose logging now handled by LoggingUtil
}