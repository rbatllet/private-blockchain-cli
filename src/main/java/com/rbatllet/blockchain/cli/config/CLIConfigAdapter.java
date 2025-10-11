package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.service.ConfigurationService;
import com.rbatllet.blockchain.config.EncryptionConfig;
import com.rbatllet.blockchain.config.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/**
 * Adapter that bridges CLIConfig with the core ConfigurationService
 * Provides CLI-specific configuration management using the centralized service
 */
public class CLIConfigAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(CLIConfigAdapter.class);
    
    private static final String CLI_CONFIG_TYPE = "CLI";
    private static final String ENCRYPTION_CONFIG_TYPE = "ENCRYPTION";
    
    private final ConfigurationService configService;
    
    /**
     * Create adapter with default configuration service
     */
    public CLIConfigAdapter() {
        this.configService = ConfigurationService.getInstance();
        logger.debug("Initialized CLI configuration adapter");
    }
    
    /**
     * Create adapter with custom configuration service
     * @param configService Custom configuration service
     */
    public CLIConfigAdapter(ConfigurationService configService) {
        this.configService = configService;
        logger.debug("Initialized CLI configuration adapter with custom service");
    }
    
    /**
     * Load CLI configuration from the core service
     * @return CLIConfig instance with loaded settings
     */
    public CLIConfig loadConfig() {
        logger.debug("Loading CLI configuration through adapter");
        
        try {
            CLIConfig config = new CLIConfig();
            
            // Load CLI configuration
            Map<String, String> cliProps = configService.loadConfiguration(CLI_CONFIG_TYPE);
            populateConfigFromProperties(config, cliProps);
            
            // Load encryption configuration
            Map<String, String> encryptionProps = configService.loadConfiguration(ENCRYPTION_CONFIG_TYPE);
            EncryptionConfig encryptionConfig = ConfigurationUtils.createEncryptionConfigFromMap(encryptionProps);
            config.setEncryptionConfig(encryptionConfig);
            
            logger.debug("Loaded CLI configuration with {} CLI entries and {} encryption entries", 
                cliProps.size(), encryptionProps.size());
            
            return config;
            
        } catch (Exception e) {
            logger.error("Failed to load CLI configuration: {}", e.getMessage());
            // Return default configuration as fallback
            return new CLIConfig();
        }
    }
    
    /**
     * Save CLI configuration through the core service
     * @param config Configuration to save
     * @return true if saved successfully
     */
    public boolean saveConfig(CLIConfig config) {
        logger.debug("Saving CLI configuration through adapter");
        
        try {
            // Save CLI configuration
            Map<String, String> cliProps = convertConfigToProperties(config);
            boolean cliSuccess = configService.saveConfiguration(CLI_CONFIG_TYPE, cliProps);
            
            // Save encryption configuration
            Map<String, String> encryptionProps = ConfigurationUtils.encryptionConfigToMap(config.getEncryptionConfig());
            boolean encryptionSuccess = configService.saveConfiguration(ENCRYPTION_CONFIG_TYPE, encryptionProps);
            
            boolean success = cliSuccess && encryptionSuccess;
            if (success) {
                logger.info("Saved CLI configuration successfully");
            } else {
                logger.warn("CLI configuration save partially failed - CLI: {}, Encryption: {}", 
                    cliSuccess, encryptionSuccess);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("Failed to save CLI configuration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Reset CLI configuration to defaults
     * @return true if reset successfully
     */
    public boolean resetConfig() {
        logger.debug("Resetting CLI configuration through adapter");
        
        boolean cliReset = configService.resetConfiguration(CLI_CONFIG_TYPE);
        boolean encryptionReset = configService.resetConfiguration(ENCRYPTION_CONFIG_TYPE);
        
        return cliReset && encryptionReset;
    }
    
    /**
     * Check if CLI configuration exists
     * @return true if configuration exists
     */
    public boolean configExists() {
        return configService.configurationExists(CLI_CONFIG_TYPE) || 
               configService.configurationExists(ENCRYPTION_CONFIG_TYPE);
    }
    
    /**
     * Export CLI configuration to file
     * @param exportPath Path to export file
     * @param config Configuration to export
     * @return true if exported successfully
     */
    public boolean exportConfig(Path exportPath, CLIConfig config) {
        logger.debug("Exporting CLI configuration to: {}", exportPath);
        
        try {
            // Combine CLI and encryption properties for export
            Properties allProps = new Properties();
            
            Map<String, String> cliProps = convertConfigToProperties(config);
            allProps.putAll(cliProps);
            
            Map<String, String> encryptionProps = ConfigurationUtils.encryptionConfigToMap(config.getEncryptionConfig());
            encryptionProps.forEach((key, value) -> allProps.setProperty("encryption." + key, value));
            
            try (var output = java.nio.file.Files.newOutputStream(exportPath)) {
                allProps.store(output, "Exported CLI Configuration");
                logger.info("Exported CLI configuration to: {}", exportPath);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to export CLI configuration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Import CLI configuration from file
     * @param importPath Path to import file
     * @return CLIConfig instance or null if failed
     */
    public CLIConfig importConfig(Path importPath) {
        logger.debug("Importing CLI configuration from: {}", importPath);
        
        try {
            if (!java.nio.file.Files.exists(importPath)) {
                logger.error("Import file does not exist: {}", importPath);
                return null;
            }
            
            Properties props = new Properties();
            try (var input = java.nio.file.Files.newInputStream(importPath)) {
                props.load(input);
            }
            
            CLIConfig config = new CLIConfig();
            
            // Separate CLI and encryption properties
            Properties cliProps = new Properties();
            Properties encryptionProps = new Properties();
            
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("encryption.")) {
                    encryptionProps.setProperty(key.substring(11), props.getProperty(key));
                } else {
                    cliProps.setProperty(key, props.getProperty(key));
                }
            }
            
            // Populate configuration
            populateConfigFromProperties(config, ConfigurationUtils.propertiesToStringMap(cliProps));
            EncryptionConfig encryptionConfig = ConfigurationUtils.createEncryptionConfigFromMap(ConfigurationUtils.propertiesToStringMap(encryptionProps));
            config.setEncryptionConfig(encryptionConfig);
            
            // Save the imported configuration
            if (saveConfig(config)) {
                logger.info("Imported CLI configuration from: {}", importPath);
                return config;
            } else {
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Failed to import CLI configuration: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Get CLI configuration audit log
     * @param limit Maximum number of entries
     * @return Audit log as formatted string
     */
    public String getAuditLog(int limit) {
        StringBuilder log = new StringBuilder();
        log.append("📋 CLI Configuration Audit Log\n");
        log.append("=" .repeat(60)).append("\n\n");
        
        log.append("🖥️  CLI Configuration Changes:\n");
        log.append(configService.getAuditLog(CLI_CONFIG_TYPE, limit));
        
        log.append("\n🔐 Encryption Configuration Changes:\n");
        log.append(configService.getAuditLog(ENCRYPTION_CONFIG_TYPE, limit));
        
        return log.toString();
    }
    
    /**
     * Get configuration storage location
     * @return Storage location description
     */
    public String getStorageLocation() {
        return configService.getStorage().getStorageLocation();
    }
    
    /**
     * Get configuration storage type
     * @return Storage type identifier
     */
    public String getStorageType() {
        return configService.getStorage().getStorageType();
    }
    
    /**
     * Check if configuration storage is healthy
     * @return true if storage is healthy
     */
    public boolean isHealthy() {
        return configService.getStorage().isHealthy();
    }
    
    /**
     * Get configuration service health status
     * @return Health status report
     */
    public String getHealthStatus() {
        return configService.getHealthStatus();
    }
    
    /**
     * Clear configuration cache
     */
    public void clearCache() {
        configService.clearCache(CLI_CONFIG_TYPE);
        configService.clearCache(ENCRYPTION_CONFIG_TYPE);
    }
    
    // Private helper methods
    
    private void populateConfigFromProperties(CLIConfig config, Map<String, String> props) {
        if (props.containsKey("output.format")) {
            config.setOutputFormat(props.get("output.format"));
        }
        if (props.containsKey("search.limit")) {
            config.setSearchLimit(Integer.parseInt(props.get("search.limit")));
        }
        if (props.containsKey("verbose.mode")) {
            config.setVerboseMode(Boolean.parseBoolean(props.get("verbose.mode")));
        }
        if (props.containsKey("detailed.output")) {
            config.setDetailedOutput(Boolean.parseBoolean(props.get("detailed.output")));
        }
        if (props.containsKey("search.type.default")) {
            config.setDefaultSearchType(props.get("search.type.default"));
        }
        if (props.containsKey("search.level.default")) {
            config.setDefaultSearchLevel(props.get("search.level.default"));
        }
        if (props.containsKey("offchain.threshold")) {
            config.setOffChainThreshold(Long.parseLong(props.get("offchain.threshold")));
        }
        if (props.containsKey("log.level")) {
            config.setLogLevel(props.get("log.level"));
        }
        if (props.containsKey("command.timeout")) {
            config.setCommandTimeout(Integer.parseInt(props.get("command.timeout")));
        }
        if (props.containsKey("max.results")) {
            config.setMaxResults(Integer.parseInt(props.get("max.results")));
        }
        if (props.containsKey("enable.metrics")) {
            config.setEnableMetrics(Boolean.parseBoolean(props.get("enable.metrics")));
        }
        if (props.containsKey("auto.cleanup")) {
            config.setAutoCleanup(Boolean.parseBoolean(props.get("auto.cleanup")));
        }
        if (props.containsKey("store.credentials")) {
            config.setStoreCredentials(Boolean.parseBoolean(props.get("store.credentials")));
        }
        if (props.containsKey("require.confirmation")) {
            config.setRequireConfirmation(Boolean.parseBoolean(props.get("require.confirmation")));
        }
        if (props.containsKey("enable.audit.log")) {
            config.setEnableAuditLog(Boolean.parseBoolean(props.get("enable.audit.log")));
        }
        
        // Custom properties
        props.forEach((key, value) -> {
            if (key.startsWith("custom.")) {
                String customKey = key.substring(7);
                config.addCustomProperty(customKey, value);
            }
        });
    }
    
    private Map<String, String> convertConfigToProperties(CLIConfig config) {
        Map<String, String> props = new java.util.HashMap<>();
        
        props.put("output.format", config.getOutputFormat());
        props.put("search.limit", String.valueOf(config.getSearchLimit()));
        props.put("verbose.mode", String.valueOf(config.isVerboseMode()));
        props.put("detailed.output", String.valueOf(config.isDetailedOutput()));
        props.put("search.type.default", config.getDefaultSearchType());
        props.put("search.level.default", config.getDefaultSearchLevel());
        props.put("offchain.threshold", String.valueOf(config.getOffChainThreshold()));
        props.put("log.level", config.getLogLevel());
        props.put("command.timeout", String.valueOf(config.getCommandTimeout()));
        props.put("max.results", String.valueOf(config.getMaxResults()));
        props.put("enable.metrics", String.valueOf(config.isEnableMetrics()));
        props.put("auto.cleanup", String.valueOf(config.isAutoCleanup()));
        props.put("store.credentials", String.valueOf(config.isStoreCredentials()));
        props.put("require.confirmation", String.valueOf(config.isRequireConfirmation()));
        props.put("enable.audit.log", String.valueOf(config.isEnableAuditLog()));
        
        // Custom properties
        config.getCustomProperties().forEach((key, value) -> 
            props.put("custom." + key, value));
        
        return props;
    }
    
    
    
}