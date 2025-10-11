package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.EncryptionConfig;
import com.rbatllet.blockchain.config.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Manager for CLI configuration persistence and loading
 * Handles saving/loading configuration to/from files
 */
public class CLIConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CLIConfigManager.class);
    
    private static final String DEFAULT_CONFIG_DIR = System.getProperty("user.home") + "/.blockchain-cli";
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final String ENCRYPTION_CONFIG_FILE = "encryption.properties";
    
    private final Path configDir;
    private final Path configFile;
    private final Path encryptionConfigFile;
    
    /**
     * Create config manager with default paths
     */
    public CLIConfigManager() {
        this(DEFAULT_CONFIG_DIR);
    }
    
    /**
     * Create config manager with custom config directory
     * @param configDirPath Path to config directory
     * @throws IllegalArgumentException if config path is invalid or insecure
     */
    public CLIConfigManager(String configDirPath) {
        if (configDirPath == null || configDirPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Config directory path cannot be null or empty");
        }

        // Validate and normalize path to prevent path traversal
        Path normalizedPath = Paths.get(configDirPath).normalize().toAbsolutePath();

        // Prevent path traversal attacks - ensure path doesn't escape expected directories
        String normalizedStr = normalizedPath.toString();
        if (normalizedStr.contains("..") || normalizedStr.contains("~")) {
            throw new IllegalArgumentException("Invalid config directory path: contains forbidden characters");
        }

        this.configDir = normalizedPath;
        this.configFile = configDir.resolve(DEFAULT_CONFIG_FILE);
        this.encryptionConfigFile = configDir.resolve(ENCRYPTION_CONFIG_FILE);

        // Create config directory if it doesn't exist
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            logger.warn("Failed to create config directory: {}", e.getMessage());
        }
    }
    
    /**
     * Load CLI configuration from file
     * @return CLIConfig instance with loaded settings
     */
    public CLIConfig loadConfig() {
        CLIConfig config = new CLIConfig();
        
        try {
            if (Files.exists(configFile)) {
                Properties props = new Properties();
                try (InputStream input = Files.newInputStream(configFile)) {
                    props.load(input);
                    populateConfigFromProperties(config, props);
                    logger.debug("Loaded CLI configuration from: {}", configFile);
                }
            } else {
                logger.debug("Config file not found, using defaults: {}", configFile);
            }
            
            // Load encryption config separately
            EncryptionConfig encryptionConfig = loadEncryptionConfig();
            config.setEncryptionConfig(encryptionConfig);
            
        } catch (IOException e) {
            logger.warn("Failed to load configuration: {}", e.getMessage());
        }
        
        return config;
    }
    
    /**
     * Save CLI configuration to file
     * @param config Configuration to save
     * @return true if saved successfully
     */
    public boolean saveConfig(CLIConfig config) {
        try {
            Properties props = new Properties();
            populatePropertiesFromConfig(props, config);
            
            try (OutputStream output = Files.newOutputStream(configFile)) {
                props.store(output, "Blockchain CLI Configuration");
                logger.info("Saved CLI configuration to: {}", configFile);
            }
            
            // Save encryption config separately
            saveEncryptionConfig(config.getEncryptionConfig());
            
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to save configuration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Load encryption configuration
     * @return EncryptionConfig instance
     */
    private EncryptionConfig loadEncryptionConfig() {
        try {
            if (Files.exists(encryptionConfigFile)) {
                Properties props = new Properties();
                try (InputStream input = Files.newInputStream(encryptionConfigFile)) {
                    props.load(input);
                    return ConfigurationUtils.createEncryptionConfigFromProperties(props);
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to load encryption config: {}", e.getMessage());
        }
        
        return new EncryptionConfig(); // Default config
    }
    
    /**
     * Save encryption configuration
     * @param encryptionConfig Configuration to save
     */
    private void saveEncryptionConfig(EncryptionConfig encryptionConfig) {
        try {
            Properties props = new Properties();
            ConfigurationUtils.populatePropertiesFromEncryptionConfig(props, encryptionConfig);
            
            try (OutputStream output = Files.newOutputStream(encryptionConfigFile)) {
                props.store(output, "Blockchain CLI Encryption Configuration");
                logger.debug("Saved encryption configuration to: {}", encryptionConfigFile);
            }
            
        } catch (IOException e) {
            logger.error("Failed to save encryption configuration: {}", e.getMessage());
        }
    }
    
    /**
     * Populate CLIConfig from Properties
     */
    private void populateConfigFromProperties(CLIConfig config, Properties props) {
        // Basic settings
        if (props.containsKey("output.format")) {
            config.setOutputFormat(props.getProperty("output.format"));
        }
        if (props.containsKey("search.limit")) {
            try {
                int searchLimit = Integer.parseInt(props.getProperty("search.limit"));
                if (searchLimit <= 0) {
                    logger.warn("Invalid search.limit value (must be > 0), using default");
                } else {
                    config.setSearchLimit(searchLimit);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid search.limit format, using default: {}", e.getMessage());
            }
        }
        if (props.containsKey("verbose.mode")) {
            config.setVerboseMode(Boolean.parseBoolean(props.getProperty("verbose.mode")));
        }
        if (props.containsKey("detailed.output")) {
            config.setDetailedOutput(Boolean.parseBoolean(props.getProperty("detailed.output")));
        }
        if (props.containsKey("search.type.default")) {
            config.setDefaultSearchType(props.getProperty("search.type.default"));
        }
        if (props.containsKey("search.level.default")) {
            config.setDefaultSearchLevel(props.getProperty("search.level.default"));
        }
        if (props.containsKey("offchain.threshold")) {
            try {
                long threshold = Long.parseLong(props.getProperty("offchain.threshold"));
                if (threshold <= 0) {
                    logger.warn("Invalid offchain.threshold value (must be > 0), using default");
                } else {
                    config.setOffChainThreshold(threshold);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid offchain.threshold format, using default: {}", e.getMessage());
            }
        }
        if (props.containsKey("log.level")) {
            config.setLogLevel(props.getProperty("log.level"));
        }
        if (props.containsKey("command.timeout")) {
            try {
                int timeout = Integer.parseInt(props.getProperty("command.timeout"));
                if (timeout <= 0) {
                    logger.warn("Invalid command.timeout value (must be > 0), using default");
                } else {
                    config.setCommandTimeout(timeout);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid command.timeout format, using default: {}", e.getMessage());
            }
        }
        if (props.containsKey("max.results")) {
            try {
                int maxResults = Integer.parseInt(props.getProperty("max.results"));
                if (maxResults <= 0) {
                    logger.warn("Invalid max.results value (must be > 0), using default");
                } else {
                    config.setMaxResults(maxResults);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid max.results format, using default: {}", e.getMessage());
            }
        }
        if (props.containsKey("enable.metrics")) {
            config.setEnableMetrics(Boolean.parseBoolean(props.getProperty("enable.metrics")));
        }
        if (props.containsKey("auto.cleanup")) {
            config.setAutoCleanup(Boolean.parseBoolean(props.getProperty("auto.cleanup")));
        }
        if (props.containsKey("store.credentials")) {
            config.setStoreCredentials(Boolean.parseBoolean(props.getProperty("store.credentials")));
        }
        if (props.containsKey("require.confirmation")) {
            config.setRequireConfirmation(Boolean.parseBoolean(props.getProperty("require.confirmation")));
        }
        if (props.containsKey("enable.audit.log")) {
            config.setEnableAuditLog(Boolean.parseBoolean(props.getProperty("enable.audit.log")));
        }

        // Custom properties
        props.forEach((key, value) -> {
            String keyStr = (String) key;
            if (keyStr.startsWith("custom.")) {
                String customKey = keyStr.substring(7); // Remove "custom." prefix
                config.addCustomProperty(customKey, (String) value);
            }
        });
    }
    
    /**
     * Populate Properties from CLIConfig
     */
    private void populatePropertiesFromConfig(Properties props, CLIConfig config) {
        props.setProperty("output.format", config.getOutputFormat());
        props.setProperty("search.limit", String.valueOf(config.getSearchLimit()));
        props.setProperty("verbose.mode", String.valueOf(config.isVerboseMode()));
        props.setProperty("detailed.output", String.valueOf(config.isDetailedOutput()));
        props.setProperty("search.type.default", config.getDefaultSearchType());
        props.setProperty("search.level.default", config.getDefaultSearchLevel());
        props.setProperty("offchain.threshold", String.valueOf(config.getOffChainThreshold()));
        props.setProperty("log.level", config.getLogLevel());
        props.setProperty("command.timeout", String.valueOf(config.getCommandTimeout()));
        props.setProperty("max.results", String.valueOf(config.getMaxResults()));
        props.setProperty("enable.metrics", String.valueOf(config.isEnableMetrics()));
        props.setProperty("auto.cleanup", String.valueOf(config.isAutoCleanup()));
        props.setProperty("store.credentials", String.valueOf(config.isStoreCredentials()));
        props.setProperty("require.confirmation", String.valueOf(config.isRequireConfirmation()));
        props.setProperty("enable.audit.log", String.valueOf(config.isEnableAuditLog()));
        
        // Custom properties
        config.getCustomProperties().forEach((key, value) -> 
            props.setProperty("custom." + key, value));
    }
    
    
    
    /**
     * Reset configuration to defaults
     * @return true if reset successfully
     */
    public boolean resetConfig() {
        try {
            if (Files.exists(configFile)) {
                Files.delete(configFile);
            }
            if (Files.exists(encryptionConfigFile)) {
                Files.delete(encryptionConfigFile);
            }
            logger.info("Configuration reset to defaults");
            return true;
        } catch (IOException e) {
            logger.error("Failed to reset configuration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if configuration file exists
     * @return true if config file exists
     */
    public boolean configExists() {
        return Files.exists(configFile);
    }
    
    /**
     * Get config file path
     * @return Path to config file
     */
    public Path getConfigPath() {
        return configFile;
    }
    
    /**
     * Get config directory path
     * @return Path to config directory
     */
    public Path getConfigDirectory() {
        return configDir;
    }
    
    /**
     * Export configuration to a specific file
     * @param exportPath Path to export file
     * @param config Configuration to export
     * @return true if exported successfully
     * @throws IllegalArgumentException if exportPath or config are null
     */
    public boolean exportConfig(Path exportPath, CLIConfig config) {
        if (exportPath == null) {
            throw new IllegalArgumentException("Export path cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        // Validate and normalize path to prevent path traversal
        Path normalizedPath = exportPath.normalize().toAbsolutePath();
        String pathStr = normalizedPath.toString();
        if (pathStr.contains("..")) {
            throw new IllegalArgumentException("Invalid export path: contains path traversal");
        }

        try {
            Properties props = new Properties();
            populatePropertiesFromConfig(props, config);

            // Add encryption config to the same file
            if (config.getEncryptionConfig() != null) {
                ConfigurationUtils.populatePropertiesFromEncryptionConfig(props, config.getEncryptionConfig());
            }

            try (OutputStream output = Files.newOutputStream(normalizedPath)) {
                props.store(output, "Exported Blockchain CLI Configuration");
                logger.info("Exported configuration successfully");
            }

            return true;

        } catch (IOException e) {
            logger.error("Failed to export configuration: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Import configuration from a specific file
     * @param importPath Path to import file
     * @return CLIConfig instance or null if failed
     * @throws IllegalArgumentException if importPath is null or invalid
     */
    public CLIConfig importConfig(Path importPath) {
        if (importPath == null) {
            throw new IllegalArgumentException("Import path cannot be null");
        }

        // Validate and normalize path to prevent path traversal
        Path normalizedPath = importPath.normalize().toAbsolutePath();
        String pathStr = normalizedPath.toString();
        if (pathStr.contains("..")) {
            throw new IllegalArgumentException("Invalid import path: contains path traversal");
        }

        try {
            if (!Files.exists(normalizedPath)) {
                logger.error("Import file does not exist");
                return null;
            }

            // Check if file is readable and is a regular file
            if (!Files.isReadable(normalizedPath) || !Files.isRegularFile(normalizedPath)) {
                logger.error("Import path is not a readable regular file");
                return null;
            }

            Properties props = new Properties();
            try (InputStream input = Files.newInputStream(normalizedPath)) {
                props.load(input);
            }

            CLIConfig config = new CLIConfig();
            populateConfigFromProperties(config, props);

            // Create encryption config from the same properties
            EncryptionConfig encryptionConfig = ConfigurationUtils.createEncryptionConfigFromProperties(props);
            config.setEncryptionConfig(encryptionConfig);

            logger.info("Imported configuration successfully");
            return config;

        } catch (IOException e) {
            logger.error("Failed to import configuration: {}", e.getMessage());
            return null;
        }
    }
}