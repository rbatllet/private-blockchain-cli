package com.rbatllet.blockchain.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.cli.commands.*;
import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.JPAUtil;

/**
 * Main CLI application for Private Blockchain
 * 
 * Usage: blockchain [COMMAND] [OPTIONS]
 */
@Command(name = "blockchain",
         description = "Private Blockchain Command Line Interface",
         version = "1.0.5",
         subcommands = {
             StatusCommand.class,
             ValidateCommand.class,
             AddBlockCommand.class,
             AddKeyCommand.class,
             ListKeysCommand.class,
             ManageKeysCommand.class,
             ExportCommand.class,
             ImportCommand.class,
             SearchCommand.class,
             EncryptCommand.class,
             SearchMetricsCommand.class,
             PerformanceCommand.class,
             ConfigCommand.class,
             OffChainCommand.class,
             RollbackCommand.class,
             DatabaseCommand.class,
             MigrateCommand.class,
             HelpCommand.class
         })
public class BlockchainCLI implements Runnable {
    
    @Option(names = {"-V", "--version"}, versionHelp = true, 
            description = "Display version information")
    boolean versionInfoRequested;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, 
            description = "Show this help message and exit")
    boolean helpRequested;
    
    @Option(names = {"-v", "--verbose"},
            description = "Enable verbose output")
    public static volatile boolean verbose = false;

    // Database configuration options (inherited by all subcommands)
    @Option(names = {"--db-type"},
            description = "Database type: sqlite, postgresql, mysql, h2 (default: sqlite)",
            scope = CommandLine.ScopeType.INHERIT)
    static String databaseType;

    @Option(names = {"--db-url"},
            description = "Database connection URL (overrides auto-generated URL)",
            scope = CommandLine.ScopeType.INHERIT)
    static String databaseUrl;

    @Option(names = {"--db-host"},
            description = "Database host (default: localhost)",
            scope = CommandLine.ScopeType.INHERIT)
    static String databaseHost;

    @Option(names = {"--db-port"},
            description = "Database port (default: depends on database type)",
            scope = CommandLine.ScopeType.INHERIT)
    static Integer databasePort;

    @Option(names = {"--db-name"},
            description = "Database name (default: blockchain)",
            scope = CommandLine.ScopeType.INHERIT)
    static String databaseName;

    @Option(names = {"--db-user"},
            description = "Database username",
            scope = CommandLine.ScopeType.INHERIT)
    static String databaseUser;

    @Option(names = {"--db-password"},
            description = "Database password (⚠️  INSECURE: visible in process list. Use DB_PASSWORD env var instead)",
            scope = CommandLine.ScopeType.INHERIT)
    static String databasePassword;
    
    /**
     * Main method - entry point for CLI
     */
    public static void main(String[] args) {
        try {
            // Create command line
            CommandLine cmd = new CommandLine(new BlockchainCLI());

            // Set custom execution strategy to initialize database before command execution
            cmd.setExecutionStrategy(new CommandLine.RunLast() {
                @Override
                public int execute(CommandLine.ParseResult parseResult) throws CommandLine.ExecutionException {
                    // Initialize database configuration before executing command
                    initializeDatabaseConfiguration();
                    // Execute the command
                    return super.execute(parseResult);
                }
            });

            int exitCode = cmd.execute(args);
            ExitUtil.exit(exitCode);
        } catch (SecurityException e) {
            System.err.println("❌ Security error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            System.err.println("❌ Runtime error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }

    /**
     * Initializes database configuration from CLI arguments.
     * This is called automatically by PicoCLI after parsing arguments.
     */
    private static void initializeDatabaseConfiguration() {
        // Set CLI arguments in the database config manager
        CLIDatabaseConfigManager configManager = CLIDatabaseConfigManager.getInstance();
        configManager.setCliArguments(
            databaseType,
            databaseUrl,
            databaseHost,
            databasePort,
            databaseName,
            databaseUser,
            databasePassword
        );

        // Initialize JPAUtil with the resolved configuration
        try {
            JPAUtil.initialize(configManager.getConfig());
            if (verbose) {
                verbose("Database initialized: " + configManager.getConfig().getDatabaseType());
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    @Override
    public void run() {
        // Default behavior when no subcommand is specified
        System.out.println("🔗 Private Blockchain CLI v1.0.5");
        System.out.println();
        System.out.println("Usage: blockchain [GLOBAL-OPTIONS] [COMMAND] [COMMAND-OPTIONS]");
        System.out.println();
        System.out.println("Global Options:");
        System.out.println("  -v, --verbose           Enable verbose output");
        System.out.println("  -h, --help              Show this help message");
        System.out.println("  -V, --version           Display version information");
        System.out.println();
        System.out.println("Database Options (optional, inherited by all commands):");
        System.out.println("  --db-type <type>        Database type: sqlite, postgresql, mysql, h2");
        System.out.println("  --db-host <host>        Database host (default: localhost)");
        System.out.println("  --db-port <port>        Database port");
        System.out.println("  --db-name <name>        Database name (default: blockchain)");
        System.out.println("  --db-user <user>        Database username");
        System.out.println("  --db-password <pwd>     Database password (⚠️  insecure, use DB_PASSWORD env var)");
        System.out.println();
        System.out.println("Available Commands:");
        System.out.println("  status          Show blockchain status and statistics");
        System.out.println("  validate        Validate the entire blockchain");
        System.out.println("  add-block       Add a new block to the blockchain");
        System.out.println("  add-key         Add an authorized key");
        System.out.println("  list-keys       List authorized keys");
        System.out.println("  export          Export blockchain to file");
        System.out.println("  import          Import blockchain from file");
        System.out.println("  search          Search blocks by content, hash, or date");
        System.out.println("  encrypt         Analyze blockchain encryption and encrypted blocks");
        System.out.println("  search-metrics  Display search performance metrics and statistics");
        System.out.println("  performance     Display comprehensive system performance metrics");
        System.out.println("  config          Manage CLI configuration settings and profiles");
        System.out.println("  database        Manage database configuration (show, test)");
        System.out.println("  migrate         Database schema migration management");
        System.out.println("  offchain        Manage off-chain data storage and retrieval");
        System.out.println("  rollback        Remove recent blocks (DANGEROUS)");
        System.out.println("  help            Show detailed help");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  blockchain status");
        System.out.println("  blockchain --db-type postgresql --db-host localhost status");
        System.out.println("  DB_TYPE=postgresql DB_HOST=localhost blockchain status");
        System.out.println();
        System.out.println("Use 'blockchain [COMMAND] --help' for command-specific help");
    }
    
    /**
     * Utility method for verbose logging
     */
    public static void verbose(String message) {
        if (verbose) {
            System.out.println("🔍 [VERBOSE] " + message);
        }
    }
    
    /**
     * Utility method for error output
     */
    public static void error(String message) {
        System.err.println("❌ Error: " + message);
    }
    
    /**
     * Utility method for success output
     */
    public static void success(String message) {
        System.out.println("✅ " + message);
    }
    
    /**
     * Utility method for info output
     */
    public static void info(String message) {
        System.out.println("ℹ️  " + message);
    }
}
