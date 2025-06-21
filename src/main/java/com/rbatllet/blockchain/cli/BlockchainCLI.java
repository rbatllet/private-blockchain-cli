package com.rbatllet.blockchain.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.cli.commands.*;
import com.rbatllet.blockchain.util.ExitUtil;

/**
 * Main CLI application for Private Blockchain
 * 
 * Usage: blockchain [COMMAND] [OPTIONS]
 */
@Command(name = "blockchain", 
         description = "Private Blockchain Command Line Interface",
         version = "1.0.3",
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
             RollbackCommand.class,
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
    
    /**
     * Main method - entry point for CLI
     */
    public static void main(String[] args) {
        try {
            int exitCode = new CommandLine(new BlockchainCLI()).execute(args);
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
    
    @Override
    public void run() {
        // Default behavior when no subcommand is specified
        System.out.println("🔗 Private Blockchain CLI v1.0.3");
        System.out.println();
        System.out.println("Usage: blockchain [COMMAND] [OPTIONS]");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  status      Show blockchain status and statistics");
        System.out.println("  validate    Validate the entire blockchain");
        System.out.println("  add-block   Add a new block to the blockchain");
        System.out.println("  add-key     Add an authorized key");
        System.out.println("  list-keys   List authorized keys");
        System.out.println("  export      Export blockchain to file");
        System.out.println("  import      Import blockchain from file");
        System.out.println("  search      Search blocks by content, hash, or date");
        System.out.println("  rollback    Remove recent blocks (DANGEROUS)");
        System.out.println("  help        Show detailed help");
        System.out.println();
        System.out.println("Use 'blockchain [COMMAND] --help' for command-specific help");
        System.out.println("Use 'blockchain --version' to show version information");
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
