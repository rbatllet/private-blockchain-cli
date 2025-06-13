package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.security.SecureKeyStorage;
import com.rbatllet.blockchain.cli.security.PasswordUtil;
import com.rbatllet.blockchain.cli.util.ExitUtil;
import java.io.InputStream;

/**
 * Command to manage stored private keys
 */
@Command(name = "manage-keys", 
         description = "Manage stored private keys")
public class ManageKeysCommand implements Runnable {
    
    // Configurable input stream for testing
    private InputStream inputStream = System.in;
    
    @Option(names = {"-l", "--list"}, 
            description = "List all stored private keys")
    boolean listKeys = false;
    
    @Option(names = {"-d", "--delete"}, 
            description = "Delete a stored private key")
    String deleteKey;
    
    @Option(names = {"-c", "--check"}, 
            description = "Check if a private key is stored for an owner")
    String checkKey;
    
    @Option(names = {"-t", "--test"}, 
            description = "Test password for a stored private key")
    String testKey;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    /**
     * Set input stream for testing purposes
     * @param inputStream the input stream to use
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    @Override
    public void run() {
        try {
            if (listKeys) {
                listStoredKeys();
            } else if (deleteKey != null) {
                deleteStoredKey(deleteKey);
            } else if (checkKey != null) {
                checkStoredKey(checkKey);
            } else if (testKey != null) {
                testStoredKey(testKey);
            } else {
                // Show help if no option provided
                showUsage();
            }
        } catch (Exception e) {
            BlockchainCLI.error("Error managing keys: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    private void listStoredKeys() {
        String[] storedKeys = SecureKeyStorage.listStoredKeys();
        
        if (json) {
            outputListJson(storedKeys);
        } else {
            if (storedKeys.length == 0) {
                System.out.println("📝 No private keys are currently stored");
                System.out.println("💡 Use 'add-key <owner> --generate --store-private' to store a private key");
            } else {
                System.out.println("🔐 Stored Private Keys:");
                System.out.println("====================");
                for (String owner : storedKeys) {
                    System.out.println("🔑 " + owner);
                }
                System.out.println("");
                System.out.println("📊 Total: " + storedKeys.length + " stored private key(s)");
                System.out.println("💡 Use --signer <owner> with add-block to use these keys");
            }
        }
    }
    
    public void deleteStoredKey(String owner) {
        try {
            DeleteResult result = deleteStoredKeyWithResult(owner);
            if (result.success) {
                if (json) {
                    outputDeleteJson(owner, true);
                } else {
                    BlockchainCLI.success("🗑️  Private key deleted for: " + owner);
                    System.out.println("⚠️  You can no longer use --signer " + owner + " (will fallback to demo mode)");
                }
            } else {
                if (json) {
                    outputDeleteJson(owner, false);
                } else {
                    BlockchainCLI.error(result.errorMessage);
                }
                ExitUtil.exit(1);
            }
        } catch (OperationCancelledException e) {
            System.out.println("❌ Operation cancelled");
        }
    }
    
    private void checkStoredKey(String owner) {
        boolean hasKey = SecureKeyStorage.hasPrivateKey(owner);
        
        if (json) {
            outputCheckJson(owner, hasKey);
        } else {
            if (hasKey) {
                System.out.println("✅ Private key is stored for: " + owner);
                System.out.println("💡 You can use --signer " + owner + " with add-block");
            } else {
                System.out.println("❌ No private key stored for: " + owner);
                System.out.println("💡 Use 'add-key " + owner + " --generate --store-private' to store a private key");
            }
        }
    }
    
    private void testStoredKey(String owner) {
        if (!SecureKeyStorage.hasPrivateKey(owner)) {
            BlockchainCLI.error("No private key stored for: " + owner);
            ExitUtil.exit(1);
        }
        
        String password = PasswordUtil.readPassword("🔐 Enter password for " + owner + ": ");
        if (password == null) {
            BlockchainCLI.error("Password input cancelled");
            ExitUtil.exit(1);
        }
        
        java.security.PrivateKey privateKey = SecureKeyStorage.loadPrivateKey(owner, password);
        boolean success = (privateKey != null);
        
        if (json) {
            outputTestJson(owner, success);
        } else {
            if (success) {
                BlockchainCLI.success("✅ Password is correct for: " + owner);
                System.out.println("🔓 Private key loaded successfully");
            } else {
                BlockchainCLI.error("❌ Wrong password for: " + owner);
                ExitUtil.exit(1);
            }
        }
    }
    
    private void showUsage() {
        System.out.println("🔐 Private Key Management");
        System.out.println("========================");
        System.out.println("");
        System.out.println("Usage: blockchain manage-keys [OPTIONS]");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -l, --list          List all stored private keys");
        System.out.println("  -c, --check <owner> Check if private key exists for owner");
        System.out.println("  -t, --test <owner>  Test password for stored private key");
        System.out.println("  -d, --delete <owner> Delete stored private key");
        System.out.println("  -j, --json          Output in JSON format");
        System.out.println("");
        System.out.println("Examples:");
        System.out.println("  blockchain manage-keys --list");
        System.out.println("  blockchain manage-keys --check Alice");
        System.out.println("  blockchain manage-keys --test Alice");
        System.out.println("  blockchain manage-keys --delete Alice");
        System.out.println("");
        System.out.println("💡 To store a new private key:");
        System.out.println("  blockchain add-key <owner> --generate --store-private");
    }
    
    /**
     * Result class for delete operations
     */
    public static class DeleteResult {
        public final boolean success;
        public final String errorMessage;
        
        public DeleteResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * Exception for cancelled operations
     */
    public static class OperationCancelledException extends Exception {
        public OperationCancelledException(String message) {
            super(message);
        }
    }
    
    /**
     * Delete a stored key and return the result without calling ExitUtil.exit()
     * This allows for better testability and error handling
     */
    public DeleteResult deleteStoredKeyWithResult(String owner) throws OperationCancelledException {
        if (!SecureKeyStorage.hasPrivateKey(owner)) {
            return new DeleteResult(false, "No private key stored for: " + owner);
        }
        
        // Confirm deletion
        System.out.print("⚠️  Are you sure you want to delete the private key for '" + owner + "'? (yes/no): ");
        java.util.Scanner scanner = new java.util.Scanner(inputStream);
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            throw new OperationCancelledException("User cancelled operation");
        }
        
        if (SecureKeyStorage.deletePrivateKey(owner)) {
            return new DeleteResult(true, null);
        } else {
            return new DeleteResult(false, "Failed to delete private key for: " + owner);
        }
    }
    
    // JSON output methods
    private void outputListJson(String[] keys) {
        System.out.println("{");
        System.out.println("  \"storedKeys\": [");
        for (int i = 0; i < keys.length; i++) {
            System.out.print("    \"" + keys[i] + "\"");
            if (i < keys.length - 1) {
                System.out.println(",");
            } else {
                System.out.println("");
            }
        }
        System.out.println("  ],");
        System.out.println("  \"count\": " + keys.length + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputCheckJson(String owner, boolean hasKey) {
        System.out.println("{");
        System.out.println("  \"owner\": \"" + owner + "\",");
        System.out.println("  \"hasPrivateKey\": " + hasKey + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputTestJson(String owner, boolean success) {
        System.out.println("{");
        System.out.println("  \"owner\": \"" + owner + "\",");
        System.out.println("  \"passwordValid\": " + success + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
    
    private void outputDeleteJson(String owner, boolean success) {
        System.out.println("{");
        System.out.println("  \"owner\": \"" + owner + "\",");
        System.out.println("  \"deleted\": " + success + ",");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
