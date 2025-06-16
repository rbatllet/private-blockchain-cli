package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;

/**
 * Command to show detailed help information
 */
@Command(name = "help", 
         description = "Show detailed help for commands")
public class HelpCommand implements Runnable {
    
    @Override
    public void run() {
        System.out.println("🔗 Private Blockchain CLI - Detailed Help");
        System.out.println("=" .repeat(50));
        System.out.println();
        
        System.out.println("DESCRIPTION:");
        System.out.println("  Command line interface for managing a private blockchain.");
        System.out.println("  Provides secure, authorized-only blockchain operations.");
        System.out.println();
        
        System.out.println("USAGE:");
        System.out.println("  blockchain [GLOBAL_OPTIONS] COMMAND [COMMAND_OPTIONS]");
        System.out.println();
        
        System.out.println("GLOBAL OPTIONS:");
        System.out.println("  -h, --help      Show help message");
        System.out.println("  -V, --version   Show version information");
        System.out.println("  -v, --verbose   Enable verbose output");
        System.out.println();
        
        System.out.println("COMMANDS:");
        System.out.println();
        
        System.out.println("  📊 status          Show blockchain status and statistics");
        System.out.println("     Options: -j/--json, -d/--detailed");
        System.out.println();
        
        System.out.println("  🔍 validate        Validate the entire blockchain");
        System.out.println("     Options: -j/--json, -d/--detailed, -q/--quick");
        System.out.println();
        
        System.out.println("  📦 add-block       Add a new block to the blockchain");
        System.out.println("     Usage: add-block \"data\" [options]");
        System.out.println("     Options: -s/--signer, -k/--key-file, -g/--generate-key, -j/--json");
        System.out.println();
        
        System.out.println("  🔑 add-key         Add an authorized key");
        System.out.println("     Usage: add-key \"owner\" [options]");
        System.out.println("     Options: -k/--public-key, -g/--generate, -j/--json, --show-private");
        System.out.println();
        
        System.out.println("  📋 list-keys       List authorized keys");
        System.out.println("     Options: -j/--json, -a/--active-only, -d/--detailed");
        System.out.println();
        
        System.out.println("  📤 export          Export blockchain to file");
        System.out.println("     Usage: export \"filename\" [options]");
        System.out.println("     Options: -f/--format, --overwrite, -j/--json, -c/--compress");
        System.out.println();
        
        System.out.println("  📥 import          Import blockchain from file");
        System.out.println("     Usage: import \"filename\" [options]");
        System.out.println("     Options: --backup, --force, -j/--json, --validate-after, --dry-run");
        System.out.println();
        
        System.out.println("  🔍 search          Search blocks by content, hash, or date");
        System.out.println("     Usage: search [term] [options]");
        System.out.println("     Options: -c/--content, -h/--hash, -n/--block-number,");
        System.out.println("              --date-from, --date-to, --datetime-from, --datetime-to,");
        System.out.println("              -j/--json, -l/--limit, --detailed");
        System.out.println();
        
        System.out.println("EXAMPLES:");
        System.out.println();
        
        System.out.println("  # Basic operations");
        System.out.println("  blockchain status");
        System.out.println("  blockchain validate --detailed");
        System.out.println("  blockchain status --json");
        System.out.println();
        
        System.out.println("  # Key management");
        System.out.println("  blockchain add-key \"Alice\" --generate");
        System.out.println("  blockchain list-keys --detailed");
        System.out.println("  blockchain add-key \"Bob\" --public-key \"MIIBIjANBgkq...\"");
        System.out.println();
        
        System.out.println("  # Block operations");
        System.out.println("  blockchain add-block \"Transaction: Alice pays Bob 100 coins\" --generate-key");
        System.out.println("  blockchain add-block \"System update\" --signer Alice");
        System.out.println();
        
        System.out.println("  # Search operations");
        System.out.println("  blockchain search \"Transaction\"");
        System.out.println("  blockchain search --block-number 5");
        System.out.println("  blockchain search --date-from 2025-01-01 --date-to 2025-01-31");
        System.out.println("  blockchain search --hash \"a1b2c3d4e5f6...\"");
        System.out.println();
        
        System.out.println("  # Import/Export");
        System.out.println("  blockchain export blockchain_backup.json");
        System.out.println("  blockchain import blockchain_backup.json --backup");
        System.out.println("  blockchain export data.json --overwrite");
        System.out.println("  blockchain import data.json --dry-run");
        System.out.println();
        
        System.out.println("TIPS:");
        System.out.println("  • Use --json flag for machine-readable output");
        System.out.println("  • Use --verbose for detailed operation logs");
        System.out.println("  • Always backup before importing data");
        System.out.println("  • Keep private keys secure and never share them");
        System.out.println("  • Use --dry-run to test operations before execution");
        System.out.println();
        
        System.out.println("For command-specific help: blockchain COMMAND --help");
    }
}
