package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.DatabaseConfig;
import com.rbatllet.blockchain.config.util.ConfigurationPriorityResolver;
import com.rbatllet.blockchain.config.util.ConfigurationSecurityAnalyzer;
import com.rbatllet.blockchain.config.util.SecurityWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Manages database configuration for the CLI application.
 *
 * <p>This class is responsible for loading database configuration from multiple sources
 * and applying the correct priority order:</p>
 * <ol>
 *   <li>CLI arguments (highest priority)</li>
 *   <li>Environment variables</li>
 *   <li>Configuration file (~/.blockchain-cli/database.properties)</li>
 *   <li>Default (H2 with ./blockchain)</li>
 * </ol>
 *
 * <p><b>Singleton Pattern:</b> Use {@link #getInstance()} to get the shared instance.</p>
 *
 * <p><b>Thread Safety:</b> This class is thread-safe.</p>
 *
 * <p><b>Security:</b> This class uses CORE security utilities to:</p>
 * <ul>
 *   <li>Display security warnings to console</li>
 *   <li>Validate file permissions</li>
 *   <li>Warn about password storage in files</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * // Get current configuration
 * DatabaseConfig config = CLIDatabaseConfigManager.getInstance().getConfig();
 *
 * // Reload configuration (if settings changed)
 * CLIDatabaseConfigManager.getInstance().reload();
 *
 * // Set CLI arguments for current session
 * CLIDatabaseConfigManager.getInstance().setCliArguments(cliArgs);
 * }</pre>
 *
 * @since 1.0.5
 */
public class CLIDatabaseConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(CLIDatabaseConfigManager.class);

    private static volatile CLIDatabaseConfigManager instance;
    private static final Object lock = new Object();

    private DatabaseConfig cachedConfig;
    private CLIDatabasePropertiesLoader propertiesLoader;
    private ConfigurationSecurityAnalyzer securityAnalyzer;

    // CLI arguments (set by BlockchainCLI)
    private String cliDatabaseType;
    private String cliDatabaseUrl;
    private String cliDatabaseHost;
    private Integer cliDatabasePort;
    private String cliDatabaseName;
    private String cliDatabaseUser;
    private String cliDatabasePassword;

    // Testing flag to ignore environment variables
    private boolean ignoreEnvironmentVariables = false;

    /**
     * Private constructor for singleton pattern.
     */
    private CLIDatabaseConfigManager() {
        this.propertiesLoader = new CLIDatabasePropertiesLoader();
        this.securityAnalyzer = new ConfigurationSecurityAnalyzer();
    }

    /**
     * Gets the singleton instance of CLIDatabaseConfigManager.
     *
     * @return the shared CLIDatabaseConfigManager instance
     */
    public static CLIDatabaseConfigManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new CLIDatabaseConfigManager();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the current database configuration, applying priority order.
     *
     * <p>Configuration is cached after first load. Use {@link #reload()} to refresh.</p>
     *
     * @return the current DatabaseConfig (never null)
     */
    public DatabaseConfig getConfig() {
        if (cachedConfig == null) {
            synchronized (lock) {
                if (cachedConfig == null) {
                    cachedConfig = loadConfiguration();
                    displaySecurityWarnings(cachedConfig);
                }
            }
        }
        return cachedConfig;
    }

    /**
     * Reloads the configuration from all sources.
     *
     * <p>This clears the cache and forces a fresh load from all configuration sources.</p>
     */
    public void reload() {
        synchronized (lock) {
            cachedConfig = null;
            logger.debug("Configuration cache cleared, will reload on next access");
        }
    }

    /**
     * Sets CLI arguments for database configuration.
     *
     * <p>These arguments have the highest priority in configuration resolution.</p>
     *
     * @param type database type (sqlite, postgresql, mysql, h2)
     * @param url complete JDBC URL (overrides other settings if provided)
     * @param host database host
     * @param port database port
     * @param name database name
     * @param user database username
     * @param password database password
     */
    public void setCliArguments(String type, String url, String host, Integer port,
                                String name, String user, String password) {
        synchronized (lock) {
            this.cliDatabaseType = type;
            this.cliDatabaseUrl = url;
            this.cliDatabaseHost = host;
            this.cliDatabasePort = port;
            this.cliDatabaseName = name;
            this.cliDatabaseUser = user;
            this.cliDatabasePassword = password;

            // Clear cache to force reload with new CLI args
            cachedConfig = null;

            if (hasCliArguments()) {
                logger.debug("CLI database arguments set, configuration will be reloaded");

                // Security warning if password provided via CLI
                if (password != null && !password.isEmpty()) {
                    System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.err.println("⚠️  SECURITY WARNING ⚠️");
                    System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.err.println("Database password provided via command-line argument.");
                    System.err.println("This is INSECURE:");
                    System.err.println("  - Visible in process list (ps aux | grep blockchain)");
                    System.err.println("  - Stored in shell history (~/.zsh_history)");
                    System.err.println("  - May be logged by monitoring tools");
                    System.err.println();
                    System.err.println("For production, use DB_PASSWORD environment variable:");
                    System.err.println("  export DB_PASSWORD='your-secure-password'");
                    System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                }
            }
        }
    }

    /**
     * Sets whether to ignore environment variables (for testing purposes).
     * <p>
     * When set to true, environment variables (DB_TYPE, DB_HOST, etc.) will be
     * ignored during configuration loading. This is useful for tests that need
     * to validate default behavior without being affected by the test environment.
     * </p>
     *
     * @param ignore true to ignore environment variables, false to use them (default)
     */
    public void setIgnoreEnvironmentVariables(boolean ignore) {
        synchronized (lock) {
            this.ignoreEnvironmentVariables = ignore;
            // Clear cache to force reload with new setting
            cachedConfig = null;
            logger.debug("Environment variables will be {} during configuration loading",
                    ignore ? "ignored" : "used");
        }
    }

    /**
     * Clears all CLI arguments.
     * <p>
     * This is equivalent to calling {@code setCliArguments(null, null, null, null, null, null, null)}
     * but provides a more readable and maintainable API.
     * </p>
     * <p>
     * After clearing, the configuration will be reloaded using environment variables,
     * configuration file, or defaults (depending on priority order).
     * </p>
     */
    public void clearCliArguments() {
        setCliArguments(null, null, null, null, null, null, null);
    }

    /**
     * Resets configuration to defaults for testing purposes.
     * <p>
     * This method performs a complete reset suitable for test cleanup:
     * </p>
     * <ul>
     *   <li>Clears all CLI arguments</li>
     *   <li>Re-enables environment variables (sets ignoreEnvironmentVariables to false)</li>
     *   <li>Clears configuration cache</li>
     * </ul>
     * <p>
     * This is the recommended method to call in {@code @AfterEach} test cleanup
     * to ensure tests don't interfere with each other.
     * </p>
     *
     * @see #clearCliArguments()
     * @see #setIgnoreEnvironmentVariables(boolean)
     */
    public void resetForTesting() {
        synchronized (lock) {
            // Clear CLI arguments
            cliDatabaseType = null;
            cliDatabaseUrl = null;
            cliDatabaseHost = null;
            cliDatabasePort = null;
            cliDatabaseName = null;
            cliDatabaseUser = null;
            cliDatabasePassword = null;

            // Reset testing flags to normal operation
            ignoreEnvironmentVariables = false;

            // Clear cache to force reload
            cachedConfig = null;

            logger.debug("Configuration reset for testing (CLI args cleared, env vars enabled, cache cleared)");
        }
    }

    /**
     * Checks if CLI arguments have been set.
     *
     * @return true if any CLI argument is set, false otherwise
     */
    private boolean hasCliArguments() {
        return cliDatabaseType != null || cliDatabaseUrl != null ||
               cliDatabaseHost != null || cliDatabasePort != null ||
               cliDatabaseName != null || cliDatabaseUser != null ||
               cliDatabasePassword != null;
    }

    /**
     * Loads configuration from all sources and applies priority order.
     *
     * @return the resolved DatabaseConfig
     */
    private DatabaseConfig loadConfiguration() {
        // 1. CLI arguments (highest priority)
        DatabaseConfig cliConfig = loadFromCliArguments();

        // 2. Environment variables
        DatabaseConfig envConfig = loadFromEnvironment();

        // 3. Configuration file
        DatabaseConfig fileConfig = propertiesLoader.loadConfig();

        // 4. Default (always available as fallback)
        DatabaseConfig defaultConfig = DatabaseConfig.createH2Config();

        // Build resolver with all sources
        ConfigurationPriorityResolver.Builder builder = ConfigurationPriorityResolver.builder();

        if (cliConfig != null) {
            builder.withCliArgs(cliConfig);
            logger.debug("CLI arguments configuration loaded");
        }

        if (envConfig != null) {
            builder.withEnvironmentVars(envConfig);
            logger.debug("Environment variables configuration loaded");
        }

        if (fileConfig != null) {
            builder.withConfigFile(fileConfig);
            logger.debug("Configuration file loaded: {}", propertiesLoader.getConfigFilePath());
        }

        builder.withDefaults(defaultConfig);
        logger.debug("Default configuration available as fallback");

        // Resolve using priority order
        ConfigurationPriorityResolver resolver = builder.build();
        ConfigurationPriorityResolver.ResolvedConfiguration resolvedConfig = resolver.resolve();
        DatabaseConfig resolved = resolvedConfig.getConfig();

        // Log that configuration was loaded
        logger.info("Database configuration loaded successfully: {}", resolved.getDatabaseType());

        return resolved;
    }

    /**
     * Loads configuration from CLI arguments.
     *
     * @return DatabaseConfig from CLI args, or null if no CLI args set
     */
    private DatabaseConfig loadFromCliArguments() {
        if (!hasCliArguments()) {
            return null;
        }

        // If complete URL provided, use it
        if (cliDatabaseUrl != null && !cliDatabaseUrl.isEmpty()) {
            // Infer database type from URL if not provided
            String dbType = cliDatabaseType;
            if (dbType == null) {
                if (cliDatabaseUrl.startsWith("jdbc:sqlite:")) {
                    dbType = "sqlite";
                } else if (cliDatabaseUrl.startsWith("jdbc:postgresql:")) {
                    dbType = "postgresql";
                } else if (cliDatabaseUrl.startsWith("jdbc:mysql:")) {
                    dbType = "mysql";
                } else if (cliDatabaseUrl.startsWith("jdbc:h2:")) {
                    dbType = "h2";
                }
            }

            // If we still don't have a type, we can't build the config
            if (dbType == null) {
                logger.warn("Cannot infer database type from URL: {}", cliDatabaseUrl);
                return null;
            }

            // Set default pool sizes and timeouts based on database type
            DatabaseConfig.DatabaseType type = DatabaseConfig.DatabaseType.valueOf(dbType.toUpperCase());
            int poolMin, poolMax, connectionTimeout;
            switch (type) {
                case SQLITE:
                    poolMin = 2;
                    poolMax = 5;
                    connectionTimeout = 20000;
                    break;
                case POSTGRESQL:
                    poolMin = 10;
                    poolMax = 60;
                    connectionTimeout = 30000;
                    break;
                case MYSQL:
                    poolMin = 10;
                    poolMax = 50;
                    connectionTimeout = 30000;
                    break;
                case H2:
                    poolMin = 5;
                    poolMax = 10;
                    connectionTimeout = 10000;
                    break;
                default:
                    poolMin = 5;
                    poolMax = 10;
                    connectionTimeout = 20000;
            }

            return DatabaseConfig.builder()
                    .databaseType(type)
                    .databaseUrl(cliDatabaseUrl)
                    .username(cliDatabaseUser)
                    .password(cliDatabasePassword)
                    .poolMinSize(poolMin)
                    .poolMaxSize(poolMax)
                    .connectionTimeout(connectionTimeout)
                    .hbm2ddlAuto("update")
                    .build();
        }

        // Build config from individual arguments
        if (cliDatabaseType != null) {
            switch (cliDatabaseType.toLowerCase()) {
                case "sqlite":
                    return DatabaseConfig.createSQLiteConfig();

                case "postgresql":
                    String pgHost = cliDatabaseHost != null ? cliDatabaseHost : "localhost";
                    int pgPort = cliDatabasePort != null ? cliDatabasePort : 5432;
                    String pgDb = cliDatabaseName != null ? cliDatabaseName : "blockchain";
                    String pgUser = cliDatabaseUser != null ? cliDatabaseUser : "blockchain_user";
                    String pgPassword = cliDatabasePassword != null ? cliDatabasePassword : "";
                    return DatabaseConfig.createPostgreSQLConfig(pgHost, pgPort, pgDb, pgUser, pgPassword);

                case "mysql":
                    String myHost = cliDatabaseHost != null ? cliDatabaseHost : "localhost";
                    int myPort = cliDatabasePort != null ? cliDatabasePort : 3306;
                    String myDb = cliDatabaseName != null ? cliDatabaseName : "blockchain";
                    String myUser = cliDatabaseUser != null ? cliDatabaseUser : "blockchain_user";
                    String myPassword = cliDatabasePassword != null ? cliDatabasePassword : "";
                    return DatabaseConfig.createMySQLConfig(myHost, myPort, myDb, myUser, myPassword);

                case "h2":
                    return DatabaseConfig.createH2TestConfig();

                default:
                    logger.warn("Unknown database type: {}. Using default H2.", cliDatabaseType);
                    return null;
            }
        }

        return null;
    }

    /**
     * Loads configuration from environment variables.
     *
     * @return DatabaseConfig from environment, or null if not configured
     */
    private DatabaseConfig loadFromEnvironment() {
        // Skip environment variables if testing flag is set
        if (ignoreEnvironmentVariables) {
            logger.debug("Environment variables ignored (test mode)");
            return null;
        }

        String dbType = System.getenv("DB_TYPE");
        if (dbType == null || dbType.isEmpty()) {
            return null;
        }

        try {
            return DatabaseConfig.createProductionConfigFromEnv();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid environment variable configuration: {}", e.getMessage());
            return null;
        }
    }


    /**
     * Displays security warnings to the console if any are detected.
     *
     * @param config the configuration to analyze
     */
    private void displaySecurityWarnings(DatabaseConfig config) {
        List<SecurityWarning> warnings = securityAnalyzer.analyze(config);

        if (!warnings.isEmpty()) {
            for (SecurityWarning warning : warnings) {
                displaySecurityWarning(warning);
            }
        }
    }

    /**
     * Displays a single security warning to the console.
     *
     * @param warning the warning to display
     */
    private void displaySecurityWarning(SecurityWarning warning) {
        System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.err.println("⚠️  SECURITY WARNING (" + warning.getSeverity() + ") ⚠️");
        System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.err.println(warning.getMessage());

        if (!warning.getRemediationSteps().isEmpty()) {
            System.err.println();
            System.err.println("Recommended actions:");
            for (String step : warning.getRemediationSteps()) {
                System.err.println("  • " + step);
            }
        }
        System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * Gets the properties loader used by this manager.
     *
     * @return the CLIDatabasePropertiesLoader instance
     */
    public CLIDatabasePropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }
}
