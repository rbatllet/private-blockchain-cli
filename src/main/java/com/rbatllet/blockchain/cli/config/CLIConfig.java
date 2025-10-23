package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.EncryptionConfig;
import com.rbatllet.blockchain.config.util.SensitiveDataMasker;
import java.util.Map;
import java.util.HashMap;

/**
 * Configuration class for CLI settings
 * Provides centralized management of CLI parameters and preferences
 */
public class CLIConfig {
    
    // Default CLI settings
    public static final String DEFAULT_OUTPUT_FORMAT = "text";
    public static final int DEFAULT_SEARCH_LIMIT = 50;
    public static final boolean DEFAULT_VERBOSE_MODE = false;
    public static final boolean DEFAULT_DETAILED_OUTPUT = false;
    public static final String DEFAULT_SEARCH_TYPE = "SIMPLE";
    public static final String DEFAULT_SEARCH_LEVEL = "INCLUDE_DATA";
    public static final long DEFAULT_OFF_CHAIN_THRESHOLD = 512 * 1024; // 512KB
    public static final String DEFAULT_CONFIG_FILE = "blockchain-cli.properties";
    public static final String DEFAULT_LOG_LEVEL = "INFO";
    
    // Performance settings
    public static final int DEFAULT_COMMAND_TIMEOUT = 120; // seconds
    public static final int DEFAULT_MAX_RESULTS = 1000;
    public static final boolean DEFAULT_ENABLE_METRICS = true;
    public static final boolean DEFAULT_AUTO_CLEANUP = true;
    
    // Security settings
    public static final boolean DEFAULT_STORE_CREDENTIALS = false;
    public static final boolean DEFAULT_REQUIRE_CONFIRMATION = true;
    public static final boolean DEFAULT_ENABLE_AUDIT_LOG = true;
    
    // Instance fields for customizable configuration
    private String outputFormat;
    private int searchLimit;
    private boolean verboseMode;
    private boolean detailedOutput;
    private String defaultSearchType;
    private String defaultSearchLevel;
    private long offChainThreshold;
    private String configFile;
    private String logLevel;
    private int commandTimeout;
    private int maxResults;
    private boolean enableMetrics;
    private boolean autoCleanup;
    private boolean storeCredentials;
    private boolean requireConfirmation;
    private boolean enableAuditLog;
    private EncryptionConfig encryptionConfig;
    private Map<String, String> customProperties;
    
    /**
     * Create configuration with default settings
     */
    public CLIConfig() {
        this.outputFormat = DEFAULT_OUTPUT_FORMAT;
        this.searchLimit = DEFAULT_SEARCH_LIMIT;
        this.verboseMode = DEFAULT_VERBOSE_MODE;
        this.detailedOutput = DEFAULT_DETAILED_OUTPUT;
        this.defaultSearchType = DEFAULT_SEARCH_TYPE;
        this.defaultSearchLevel = DEFAULT_SEARCH_LEVEL;
        this.offChainThreshold = DEFAULT_OFF_CHAIN_THRESHOLD;
        this.configFile = DEFAULT_CONFIG_FILE;
        this.logLevel = DEFAULT_LOG_LEVEL;
        this.commandTimeout = DEFAULT_COMMAND_TIMEOUT;
        this.maxResults = DEFAULT_MAX_RESULTS;
        this.enableMetrics = DEFAULT_ENABLE_METRICS;
        this.autoCleanup = DEFAULT_AUTO_CLEANUP;
        this.storeCredentials = DEFAULT_STORE_CREDENTIALS;
        this.requireConfirmation = DEFAULT_REQUIRE_CONFIRMATION;
        this.enableAuditLog = DEFAULT_ENABLE_AUDIT_LOG;
        this.encryptionConfig = new EncryptionConfig(); // Default encryption
        this.customProperties = new HashMap<>();
    }
    
    /**
     * Create a builder for custom configuration
     * @return Configuration builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Create configuration optimized for development
     * @return Development configuration
     */
    public static CLIConfig createDevelopmentConfig() {
        return new Builder()
                .verboseMode(true)
                .detailedOutput(true)
                .enableMetrics(true)
                .autoCleanup(false)
                .requireConfirmation(false)
                .encryptionConfig(EncryptionConfig.createTestConfig())
                .logLevel("DEBUG")
                .build();
    }
    
    /**
     * Create configuration optimized for production
     * @return Production configuration
     */
    public static CLIConfig createProductionConfig() {
        return new Builder()
                .verboseMode(false)
                .detailedOutput(false)
                .enableMetrics(true)
                .autoCleanup(true)
                .requireConfirmation(true)
                .enableAuditLog(true)
                .encryptionConfig(EncryptionConfig.createHighSecurityConfig())
                .logLevel("INFO")
                .commandTimeout(300) // 5 minutes for production
                .build();
    }
    
    /**
     * Create configuration optimized for performance
     * @return Performance configuration
     */
    public static CLIConfig createPerformanceConfig() {
        return new Builder()
                .searchLimit(100)
                .maxResults(2000)
                .enableMetrics(false)
                .detailedOutput(false)
                .encryptionConfig(EncryptionConfig.createPerformanceConfig())
                .commandTimeout(60)
                .build();
    }
    
    /**
     * Create configuration for testing purposes
     * @return Testing configuration
     */
    public static CLIConfig createTestConfig() {
        return new Builder()
                .verboseMode(true)
                .enableMetrics(false)
                .autoCleanup(false)
                .requireConfirmation(false)
                .enableAuditLog(false)
                .encryptionConfig(EncryptionConfig.createTestConfig())
                .logLevel("WARN")
                .commandTimeout(30)
                .build();
    }
    
    // Getters and setters
    
    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
    
    public int getSearchLimit() { return searchLimit; }
    public void setSearchLimit(int searchLimit) { this.searchLimit = searchLimit; }
    
    public boolean isVerboseMode() { return verboseMode; }
    public void setVerboseMode(boolean verboseMode) { this.verboseMode = verboseMode; }
    
    public boolean isDetailedOutput() { return detailedOutput; }
    public void setDetailedOutput(boolean detailedOutput) { this.detailedOutput = detailedOutput; }
    
    public String getDefaultSearchType() { return defaultSearchType; }
    public void setDefaultSearchType(String defaultSearchType) { this.defaultSearchType = defaultSearchType; }
    
    public String getDefaultSearchLevel() { return defaultSearchLevel; }
    public void setDefaultSearchLevel(String defaultSearchLevel) { this.defaultSearchLevel = defaultSearchLevel; }
    
    public long getOffChainThreshold() { return offChainThreshold; }
    public void setOffChainThreshold(long offChainThreshold) { this.offChainThreshold = offChainThreshold; }
    
    public String getConfigFile() { return configFile; }
    public void setConfigFile(String configFile) { this.configFile = configFile; }
    
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    
    public int getCommandTimeout() { return commandTimeout; }
    public void setCommandTimeout(int commandTimeout) { this.commandTimeout = commandTimeout; }
    
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
    
    public boolean isEnableMetrics() { return enableMetrics; }
    public void setEnableMetrics(boolean enableMetrics) { this.enableMetrics = enableMetrics; }
    
    public boolean isAutoCleanup() { return autoCleanup; }
    public void setAutoCleanup(boolean autoCleanup) { this.autoCleanup = autoCleanup; }
    
    public boolean isStoreCredentials() { return storeCredentials; }
    public void setStoreCredentials(boolean storeCredentials) { this.storeCredentials = storeCredentials; }
    
    public boolean isRequireConfirmation() { return requireConfirmation; }
    public void setRequireConfirmation(boolean requireConfirmation) { this.requireConfirmation = requireConfirmation; }
    
    public boolean isEnableAuditLog() { return enableAuditLog; }
    public void setEnableAuditLog(boolean enableAuditLog) { this.enableAuditLog = enableAuditLog; }
    
    public EncryptionConfig getEncryptionConfig() { return encryptionConfig; }
    public void setEncryptionConfig(EncryptionConfig encryptionConfig) { this.encryptionConfig = encryptionConfig; }
    
    public Map<String, String> getCustomProperties() { return customProperties; }
    public void setCustomProperties(Map<String, String> customProperties) { this.customProperties = customProperties; }
    
    /**
     * Add a custom property
     * @param key Property key
     * @param value Property value
     */
    public void addCustomProperty(String key, String value) {
        this.customProperties.put(key, value);
    }
    
    /**
     * Get a custom property
     * @param key Property key
     * @param defaultValue Default value if not found
     * @return Property value or default
     */
    public String getCustomProperty(String key, String defaultValue) {
        return customProperties.getOrDefault(key, defaultValue);
    }
    
    /**
     * Get the configuration profile based on current settings
     * @return ConfigProfile enum value
     */
    public ConfigProfile getProfile() {
        if (verboseMode && !requireConfirmation && encryptionConfig.getSecurityLevel() == EncryptionConfig.SecurityLevel.PERFORMANCE) {
            return ConfigProfile.DEVELOPMENT;
        } else if (!verboseMode && requireConfirmation && enableAuditLog && encryptionConfig.getSecurityLevel() == EncryptionConfig.SecurityLevel.MAXIMUM) {
            return ConfigProfile.PRODUCTION;
        } else if (!enableMetrics && maxResults > 1500 && encryptionConfig.getSecurityLevel() == EncryptionConfig.SecurityLevel.PERFORMANCE) {
            return ConfigProfile.PERFORMANCE;
        } else if (!enableMetrics && !requireConfirmation && !enableAuditLog) {
            return ConfigProfile.TESTING;
        } else {
            return ConfigProfile.CUSTOM;
        }
    }
    
    /**
     * Configuration profiles enum
     */
    public enum ConfigProfile {
        DEVELOPMENT("Development environment with verbose output"),
        PRODUCTION("Production environment with high security"),
        PERFORMANCE("Performance optimized configuration"),
        TESTING("Testing environment with minimal overhead"),
        CUSTOM("Custom user-defined configuration");
        
        private final String description;
        
        ConfigProfile(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Validate the configuration settings
     * @throws IllegalArgumentException if configuration is invalid
     */
    public void validate() {
        if (searchLimit < 1 || searchLimit > 10000) {
            throw new IllegalArgumentException("Search limit must be between 1 and 10000");
        }
        
        if (maxResults < searchLimit) {
            throw new IllegalArgumentException("Max results must be >= search limit");
        }
        
        if (commandTimeout < 10 || commandTimeout > 3600) {
            throw new IllegalArgumentException("Command timeout must be between 10 and 3600 seconds");
        }
        
        if (offChainThreshold < 1024) {
            throw new IllegalArgumentException("Off-chain threshold must be at least 1KB");
        }
        
        if (!outputFormat.matches("text|json|csv")) {
            throw new IllegalArgumentException("Output format must be: text, json, or csv");
        }
        
        if (!defaultSearchType.matches("SIMPLE|SECURE|INTELLIGENT|ADVANCED")) {
            throw new IllegalArgumentException("Search type must be: SIMPLE, SECURE, INTELLIGENT, or ADVANCED");
        }
        
        if (!defaultSearchLevel.matches("FAST_ONLY|INCLUDE_DATA|EXHAUSTIVE_OFFCHAIN")) {
            throw new IllegalArgumentException("Search level must be: FAST_ONLY, INCLUDE_DATA, or EXHAUSTIVE_OFFCHAIN");
        }
        
        if (!logLevel.matches("TRACE|DEBUG|INFO|WARN|ERROR")) {
            throw new IllegalArgumentException("Log level must be: TRACE, DEBUG, INFO, WARN, or ERROR");
        }
        
        // Validate encryption config
        if (encryptionConfig != null) {
            encryptionConfig.validate();
        }
    }
    
    /**
     * Get a human-readable summary of the configuration
     * @return Configuration summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("⚙️  CLI Configuration Summary:\n");
        sb.append("   Profile: ").append(getProfile().name()).append(" (").append(getProfile().getDescription()).append(")\n");
        sb.append("   Output Format: ").append(outputFormat).append("\n");
        sb.append("   Search Limit: ").append(searchLimit).append("\n");
        sb.append("   Default Search: ").append(defaultSearchType).append(" (").append(defaultSearchLevel).append(")\n");
        sb.append("   Off-chain Threshold: ").append(offChainThreshold / 1024).append(" KB\n");
        sb.append("   Verbose Mode: ").append(verboseMode ? "✅ Enabled" : "❌ Disabled").append("\n");
        sb.append("   Detailed Output: ").append(detailedOutput ? "✅ Enabled" : "❌ Disabled").append("\n");
        sb.append("   Metrics: ").append(enableMetrics ? "✅ Enabled" : "❌ Disabled").append("\n");
        sb.append("   Auto Cleanup: ").append(autoCleanup ? "✅ Enabled" : "❌ Disabled").append("\n");
        sb.append("   Audit Log: ").append(enableAuditLog ? "✅ Enabled" : "❌ Disabled").append("\n");
        sb.append("   Command Timeout: ").append(commandTimeout).append(" seconds\n");
        sb.append("   Log Level: ").append(logLevel).append("\n");
        
        if (encryptionConfig != null) {
            sb.append("\n").append(encryptionConfig.getSummary());
        }
        
        if (!customProperties.isEmpty()) {
            sb.append("\n   Custom Properties:\n");
            customProperties.forEach((key, value) -> {
                // Mask sensitive custom properties (password, secret, token, etc.)
                String displayValue = SensitiveDataMasker.isSensitiveKey(key) ?
                    "***" : SensitiveDataMasker.maskSensitiveData(value);
                sb.append("     ").append(key).append(": ").append(displayValue).append("\n");
            });
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
    
    /**
     * Builder class for creating custom CLI configurations
     */
    public static class Builder {
        private final CLIConfig config;
        
        public Builder() {
            this.config = new CLIConfig();
        }
        
        public Builder outputFormat(String format) {
            if (format == null || format.trim().isEmpty()) {
                throw new IllegalArgumentException("Output format cannot be null or empty");
            }
            if (!format.matches("text|json|csv")) {
                throw new IllegalArgumentException("Output format must be: text, json, or csv");
            }
            config.setOutputFormat(format);
            return this;
        }
        
        public Builder searchLimit(int limit) {
            config.setSearchLimit(limit);
            return this;
        }
        
        public Builder verboseMode(boolean verbose) {
            config.setVerboseMode(verbose);
            return this;
        }
        
        public Builder detailedOutput(boolean detailed) {
            config.setDetailedOutput(detailed);
            return this;
        }
        
        public Builder defaultSearchType(String type) {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Search type cannot be null or empty");
            }
            if (!type.matches("SIMPLE|SECURE|INTELLIGENT|ADVANCED")) {
                throw new IllegalArgumentException("Search type must be: SIMPLE, SECURE, INTELLIGENT, or ADVANCED");
            }
            config.setDefaultSearchType(type);
            return this;
        }

        public Builder defaultSearchLevel(String level) {
            if (level == null || level.trim().isEmpty()) {
                throw new IllegalArgumentException("Search level cannot be null or empty");
            }
            if (!level.matches("FAST_ONLY|INCLUDE_DATA|EXHAUSTIVE_OFFCHAIN")) {
                throw new IllegalArgumentException("Search level must be: FAST_ONLY, INCLUDE_DATA, or EXHAUSTIVE_OFFCHAIN");
            }
            config.setDefaultSearchLevel(level);
            return this;
        }
        
        public Builder offChainThreshold(long threshold) {
            if (threshold < 1024) {
                throw new IllegalArgumentException("Off-chain threshold must be at least 1KB (1024 bytes)");
            }
            if (threshold > 100 * 1024 * 1024) { // 100MB max
                throw new IllegalArgumentException("Off-chain threshold must be at most 100MB");
            }
            config.setOffChainThreshold(threshold);
            return this;
        }

        public Builder configFile(String file) {
            if (file == null || file.trim().isEmpty()) {
                throw new IllegalArgumentException("Config file cannot be null or empty");
            }
            // Check for path traversal attempts
            if (file.contains("..") || file.contains("~")) {
                throw new IllegalArgumentException("Config file path contains forbidden characters");
            }
            config.setConfigFile(file);
            return this;
        }
        
        public Builder logLevel(String level) {
            config.setLogLevel(level);
            return this;
        }
        
        public Builder commandTimeout(int timeout) {
            config.setCommandTimeout(timeout);
            return this;
        }
        
        public Builder maxResults(int maxResults) {
            config.setMaxResults(maxResults);
            return this;
        }
        
        public Builder enableMetrics(boolean enable) {
            config.setEnableMetrics(enable);
            return this;
        }
        
        public Builder autoCleanup(boolean cleanup) {
            config.setAutoCleanup(cleanup);
            return this;
        }
        
        public Builder storeCredentials(boolean store) {
            config.setStoreCredentials(store);
            return this;
        }
        
        public Builder requireConfirmation(boolean require) {
            config.setRequireConfirmation(require);
            return this;
        }
        
        public Builder enableAuditLog(boolean enable) {
            config.setEnableAuditLog(enable);
            return this;
        }
        
        public Builder encryptionConfig(EncryptionConfig encryptionConfig) {
            config.setEncryptionConfig(encryptionConfig);
            return this;
        }
        
        public Builder customProperty(String key, String value) {
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("Custom property key cannot be null or empty");
            }
            if (value == null) {
                throw new IllegalArgumentException("Custom property value cannot be null");
            }
            config.addCustomProperty(key, value);
            return this;
        }
        
        public CLIConfig build() {
            config.validate();
            return config;
        }
    }
}