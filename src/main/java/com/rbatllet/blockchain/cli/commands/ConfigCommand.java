package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.rbatllet.blockchain.cli.config.CLIConfig;
import com.rbatllet.blockchain.cli.config.CLIConfigAdapter;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.DisplayConstants;
import com.rbatllet.blockchain.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Command to manage CLI configuration settings
 */
@Command(name = "config", 
         description = "Manage CLI configuration settings")
public class ConfigCommand implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigCommand.class);
    
    @Parameters(index = "0", arity = "0..1",
                description = "Configuration action: show, set, reset, export, import, profiles")
    String action = "show";
    
    @Option(names = {"--key"}, 
            description = "Configuration key to set")
    String configKey;
    
    @Option(names = {"--value"}, 
            description = "Configuration value to set")
    String configValue;
    
    @Option(names = {"--profile"}, 
            description = "Configuration profile: development, production, performance, testing")
    String profile;
    
    @Option(names = {"--file"}, 
            description = "File path for export/import operations")
    String filePath;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output in JSON format")
    boolean json = false;
    
    @Option(names = {"--detailed"}, 
            description = "Show detailed configuration information")
    boolean detailed = false;
    
    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    boolean verbose = false;
    
    private CLIConfigAdapter configAdapter;
    
    @Override
    public void run() {
        try {
            LoggingUtil.logOperationStart(logger, "configuration management", verbose || BlockchainCLI.verbose);
            
            configAdapter = new CLIConfigAdapter();
            
            switch (action.toLowerCase()) {
                case "show":
                case "display":
                    showConfig();
                    break;
                    
                case "set":
                    setConfig();
                    break;
                    
                case "reset":
                    resetConfig();
                    break;
                    
                case "export":
                    exportConfig();
                    break;
                    
                case "import":
                    importConfig();
                    break;
                    
                case "profiles":
                    showProfiles();
                    break;
                    
                case "apply-profile":
                    applyProfile();
                    break;
                    
                default:
                    BlockchainCLI.error(DisplayConstants.ERROR + " Unknown action: " + action);
                    showHelp();
                    ExitUtil.exit(1);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error(DisplayConstants.ERROR + " Configuration operation failed: " + e.getMessage());
            logger.error("Configuration operation failed", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void showConfig() {
        LoggingUtil.verboseLog(logger, "Loading current configuration...", verbose || BlockchainCLI.verbose);
        
        CLIConfig config = configAdapter.loadConfig();
        
        if (json) {
            outputConfigJson(config);
        } else {
            outputConfigText(config);
        }
    }
    
    private void setConfig() {
        if (configKey == null || configValue == null) {
            BlockchainCLI.error("❌ Both --key and --value are required for set operation");
            BlockchainCLI.error("   Example: blockchain config set --key search.limit --value 100");
            ExitUtil.exit(1);
        }
        
        LoggingUtil.logConfigChange(logger, configKey, configValue, verbose || BlockchainCLI.verbose);
        
        CLIConfig config = configAdapter.loadConfig();
        
        // Set the configuration value
        if (setConfigurationValue(config, configKey, configValue)) {
            if (configAdapter.saveConfig(config)) {
                BlockchainCLI.success("Configuration updated: " + configKey + " = " + configValue);
            } else {
                BlockchainCLI.error("❌ Failed to save configuration");
                ExitUtil.exit(1);
            }
        } else {
            BlockchainCLI.error("❌ Unknown configuration key: " + configKey);
            showAvailableKeys();
            ExitUtil.exit(1);
        }
    }
    
    private void resetConfig() {
        LoggingUtil.verboseLog(logger, "Resetting configuration to defaults...", verbose || BlockchainCLI.verbose);
        
        if (configAdapter.resetConfig()) {
            BlockchainCLI.success("Configuration reset to defaults");
        } else {
            BlockchainCLI.error("❌ Failed to reset configuration");
            ExitUtil.exit(1);
        }
    }
    
    private void exportConfig() {
        if (filePath == null) {
            BlockchainCLI.error("❌ --file parameter is required for export");
            ExitUtil.exit(1);
        }
        
        LoggingUtil.verboseLog(logger, "Exporting configuration to: " + filePath, verbose || BlockchainCLI.verbose);
        
        CLIConfig config = configAdapter.loadConfig();
        
        if (configAdapter.exportConfig(Paths.get(filePath), config)) {
            BlockchainCLI.success("Configuration exported to: " + filePath);
        } else {
            BlockchainCLI.error("❌ Failed to export configuration");
            ExitUtil.exit(1);
        }
    }
    
    private void importConfig() {
        if (filePath == null) {
            BlockchainCLI.error("❌ --file parameter is required for import");
            ExitUtil.exit(1);
        }
        
        LoggingUtil.verboseLog(logger, "Importing configuration from: " + filePath, verbose || BlockchainCLI.verbose);
        
        CLIConfig config = configAdapter.importConfig(Paths.get(filePath));
        
        if (config != null) {
            if (configAdapter.saveConfig(config)) {
                BlockchainCLI.success("Configuration imported from: " + filePath);
                if (verbose) {
                    System.out.println("\nImported configuration:");
                    System.out.println(config.getSummary());
                }
            } else {
                BlockchainCLI.error("❌ Failed to save imported configuration");
                ExitUtil.exit(1);
            }
        } else {
            BlockchainCLI.error("❌ Failed to import configuration");
            ExitUtil.exit(1);
        }
    }
    
    private void showProfiles() {
        System.out.println("📋 Available Configuration Profiles");
        System.out.println("=" .repeat(60));
        
        System.out.println("🔧 DEVELOPMENT");
        System.out.println("   " + CLIConfig.ConfigProfile.DEVELOPMENT.getDescription());
        System.out.println("   • Verbose output enabled");
        System.out.println("   • Detailed output enabled");
        System.out.println("   • Test encryption config");
        System.out.println("   • No confirmation required");
        System.out.println();
        
        System.out.println("🏭 PRODUCTION");
        System.out.println("   " + CLIConfig.ConfigProfile.PRODUCTION.getDescription());
        System.out.println("   • High security encryption");
        System.out.println("   • Audit logging enabled");
        System.out.println("   • Confirmation required");
        System.out.println("   • Auto cleanup enabled");
        System.out.println();
        
        System.out.println("⚡ PERFORMANCE");
        System.out.println("   " + CLIConfig.ConfigProfile.PERFORMANCE.getDescription());
        System.out.println("   • Higher search limits");
        System.out.println("   • Metrics disabled");
        System.out.println("   • Performance encryption");
        System.out.println("   • Reduced timeout");
        System.out.println();
        
        System.out.println("🧪 TESTING");
        System.out.println("   " + CLIConfig.ConfigProfile.TESTING.getDescription());
        System.out.println("   • Minimal overhead");
        System.out.println("   • Fast encryption settings");
        System.out.println("   • Short timeouts");
        System.out.println("   • No audit logging");
        System.out.println();
        
        System.out.println("💡 Usage:");
        System.out.println("   blockchain config apply-profile --profile development");
        System.out.println("   blockchain config apply-profile --profile production");
    }
    
    private void applyProfile() {
        if (profile == null) {
            BlockchainCLI.error("❌ --profile parameter is required");
            showProfiles();
            ExitUtil.exit(1);
        }
        
        LoggingUtil.verboseLog(logger, "Applying configuration profile: " + profile, verbose || BlockchainCLI.verbose);
        
        CLIConfig config;
        
        switch (profile.toLowerCase()) {
            case "development":
            case "dev":
                config = CLIConfig.createDevelopmentConfig();
                break;
                
            case "production":
            case "prod":
                config = CLIConfig.createProductionConfig();
                break;
                
            case "performance":
            case "perf":
                config = CLIConfig.createPerformanceConfig();
                break;
                
            case "testing":
            case "test":
                config = CLIConfig.createTestConfig();
                break;
                
            default:
                BlockchainCLI.error("❌ Unknown profile: " + profile);
                showProfiles();
                ExitUtil.exit(1);
                return;
        }
        
        if (configAdapter.saveConfig(config)) {
            BlockchainCLI.success("Applied configuration profile: " + profile);
            if (verbose) {
                System.out.println("\nApplied configuration:");
                System.out.println(config.getSummary());
            }
        } else {
            BlockchainCLI.error("❌ Failed to save configuration profile");
            ExitUtil.exit(1);
        }
    }
    
    private boolean setConfigurationValue(CLIConfig config, String key, String value) {
        try {
            switch (key.toLowerCase()) {
                case "output.format":
                    config.setOutputFormat(value);
                    return true;
                case "search.limit":
                    config.setSearchLimit(Integer.parseInt(value));
                    return true;
                case "verbose.mode":
                    config.setVerboseMode(Boolean.parseBoolean(value));
                    return true;
                case "detailed.output":
                    config.setDetailedOutput(Boolean.parseBoolean(value));
                    return true;
                case "search.type.default":
                    config.setDefaultSearchType(value.toUpperCase());
                    return true;
                case "search.level.default":
                    config.setDefaultSearchLevel(value.toUpperCase());
                    return true;
                case "offchain.threshold":
                    config.setOffChainThreshold(Long.parseLong(value));
                    return true;
                case "log.level":
                    config.setLogLevel(value.toUpperCase());
                    return true;
                case "command.timeout":
                    config.setCommandTimeout(Integer.parseInt(value));
                    return true;
                case "max.results":
                    config.setMaxResults(Integer.parseInt(value));
                    return true;
                case "enable.metrics":
                    config.setEnableMetrics(Boolean.parseBoolean(value));
                    return true;
                case "auto.cleanup":
                    config.setAutoCleanup(Boolean.parseBoolean(value));
                    return true;
                case "require.confirmation":
                    config.setRequireConfirmation(Boolean.parseBoolean(value));
                    return true;
                case "enable.audit.log":
                    config.setEnableAuditLog(Boolean.parseBoolean(value));
                    return true;
                default:
                    // Check if it's a custom property
                    if (key.startsWith("custom.")) {
                        String customKey = key.substring(7);
                        config.addCustomProperty(customKey, value);
                        return true;
                    }
                    return false;
            }
        } catch (Exception e) {
            BlockchainCLI.error("❌ Invalid value for " + key + ": " + value);
            return false;
        }
    }
    
    private void outputConfigText(CLIConfig config) {
        System.out.println("⚙️  Current CLI Configuration");
        System.out.println("=" .repeat(80));
        
        System.out.println(config.getSummary());
        
        if (detailed) {
            System.out.println("\n📁 Configuration Storage:");
            System.out.println("   Storage Type: " + configAdapter.getStorageType());
            System.out.println("   Storage Location: " + configAdapter.getStorageLocation());
            System.out.println("   Config Exists: " + (configAdapter.configExists() ? "✅ Yes" : "❌ No"));
            
            System.out.println("\n💡 Management Commands:");
            System.out.println("   • blockchain config set --key <key> --value <value>");
            System.out.println("   • blockchain config reset");
            System.out.println("   • blockchain config export --file config-backup.properties");
            System.out.println("   • blockchain config apply-profile --profile production");
        }
    }
    
    private void outputConfigJson(CLIConfig config) {
        System.out.println("{");
        System.out.println("  \"cliConfig\": {");
        System.out.printf("    \"profile\": \"%s\",%n", config.getProfile().name());
        System.out.printf("    \"outputFormat\": \"%s\",%n", config.getOutputFormat());
        System.out.printf("    \"searchLimit\": %d,%n", config.getSearchLimit());
        System.out.printf("    \"verboseMode\": %s,%n", config.isVerboseMode());
        System.out.printf("    \"detailedOutput\": %s,%n", config.isDetailedOutput());
        System.out.printf("    \"defaultSearchType\": \"%s\",%n", config.getDefaultSearchType());
        System.out.printf("    \"defaultSearchLevel\": \"%s\",%n", config.getDefaultSearchLevel());
        System.out.printf("    \"offChainThreshold\": %d,%n", config.getOffChainThreshold());
        System.out.printf("    \"logLevel\": \"%s\",%n", config.getLogLevel());
        System.out.printf("    \"commandTimeout\": %d,%n", config.getCommandTimeout());
        System.out.printf("    \"maxResults\": %d,%n", config.getMaxResults());
        System.out.printf("    \"enableMetrics\": %s,%n", config.isEnableMetrics());
        System.out.printf("    \"autoCleanup\": %s,%n", config.isAutoCleanup());
        System.out.printf("    \"requireConfirmation\": %s,%n", config.isRequireConfirmation());
        System.out.printf("    \"enableAuditLog\": %s%n", config.isEnableAuditLog());
        System.out.println("  },");
        System.out.println("  \"encryptionConfig\": {");
        var encConfig = config.getEncryptionConfig();
        System.out.printf("    \"algorithm\": \"%s\",%n", encConfig.getEncryptionTransformation());
        System.out.printf("    \"keyLength\": %d,%n", encConfig.getKeyLength());
        System.out.printf("    \"securityLevel\": \"%s\"%n", encConfig.getSecurityLevel().name());
        System.out.println("  },");
        System.out.printf("  \"storageLocation\": \"%s\",%n", configAdapter.getStorageLocation());
        System.out.printf("  \"configExists\": %s%n", configAdapter.configExists());
        System.out.println("}");
    }
    
    private void showAvailableKeys() {
        System.out.println("\n📋 Available Configuration Keys:");
        System.out.println("   Basic Settings:");
        System.out.println("     • output.format (text, json, csv)");
        System.out.println("     • search.limit (1-10000)");
        System.out.println("     • verbose.mode (true, false)");
        System.out.println("     • detailed.output (true, false)");
        System.out.println("   Search Settings:");
        System.out.println("     • search.type.default (SIMPLE, SECURE, INTELLIGENT, ADVANCED)");
        System.out.println("     • search.level.default (FAST_ONLY, INCLUDE_DATA, EXHAUSTIVE_OFFCHAIN)");
        System.out.println("     • max.results (1-10000)");
        System.out.println("   System Settings:");
        System.out.println("     • offchain.threshold (bytes)");
        System.out.println("     • log.level (TRACE, DEBUG, INFO, WARN, ERROR)");
        System.out.println("     • command.timeout (10-3600 seconds)");
        System.out.println("   Feature Settings:");
        System.out.println("     • enable.metrics (true, false)");
        System.out.println("     • auto.cleanup (true, false)");
        System.out.println("     • require.confirmation (true, false)");
        System.out.println("     • enable.audit.log (true, false)");
        System.out.println("   Custom Properties:");
        System.out.println("     • custom.<key> (any value)");
    }
    
    private void showHelp() {
        System.out.println("\n📖 Configuration Command Help");
        System.out.println("=" .repeat(50));
        System.out.println("Available actions:");
        System.out.println("  show         Display current configuration");
        System.out.println("  set          Set a configuration value");
        System.out.println("  reset        Reset to default configuration");
        System.out.println("  export       Export configuration to file");
        System.out.println("  import       Import configuration from file");
        System.out.println("  profiles     Show available configuration profiles");
        System.out.println("  apply-profile Apply a configuration profile");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  blockchain config show");
        System.out.println("  blockchain config set --key search.limit --value 100");
        System.out.println("  blockchain config apply-profile --profile production");
        System.out.println("  blockchain config export --file my-config.properties");
    }
    
    // Verbose logging now handled by LoggingUtil
}