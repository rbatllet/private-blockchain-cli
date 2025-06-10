package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.AuthorizedKey;
import com.rbatllet.blockchain.cli.BlockchainCLI;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command to list authorized keys in the blockchain
 */
@Command(name = "list-keys", 
         description = "List all authorized keys in the blockchain")
public class ListKeysCommand implements Runnable {
    
    @Option(names = {"-j", "--json"}, 
            description = "Output in JSON format")
    boolean json = false;
    
    @Option(names = {"-a", "--active-only"}, 
            description = "Show only active keys")
    boolean activeOnly = false;
    
    @Option(names = {"-d", "--detailed"}, 
            description = "Show detailed information including full public keys")
    boolean detailed = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Retrieving authorized keys...");
            
            Blockchain blockchain = new Blockchain();
            List<AuthorizedKey> keys = blockchain.getAuthorizedKeys();
            
            // Filter active keys if requested
            if (activeOnly) {
                keys = keys.stream()
                          .filter(AuthorizedKey::isActive)
                          .toList();
            }
            
            if (json) {
                outputJson(keys);
            } else {
                outputText(keys);
            }
            
        } catch (Exception e) {
            BlockchainCLI.error("Failed to retrieve authorized keys: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    private void outputText(List<AuthorizedKey> keys) {
        System.out.println("🔑 Authorized Keys");
        System.out.println("=" .repeat(50));
        
        if (keys.isEmpty()) {
            System.out.println("No authorized keys found.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < keys.size(); i++) {
            AuthorizedKey key = keys.get(i);
            
            System.out.println("📋 Key #" + (i + 1));
            System.out.println("   👤 Owner: " + key.getOwnerName());
            System.out.println("   📅 Created: " + key.getCreatedAt().format(formatter));
            System.out.println("   ✅ Status: " + (key.isActive() ? "Active" : "Inactive"));
            
            if (detailed) {
                System.out.println("   🔑 Public Key: " + key.getPublicKey());
            } else {
                // Show truncated public key
                String pubKey = key.getPublicKey();
                String truncated = pubKey.length() > 40 ? 
                    pubKey.substring(0, 20) + "..." + pubKey.substring(pubKey.length() - 20) :
                    pubKey;
                System.out.println("   🔑 Public Key: " + truncated);
            }
            
            if (i < keys.size() - 1) {
                System.out.println();
            }
        }
        
        System.out.println();
        System.out.println("📊 Total: " + keys.size() + " authorized key(s)");
        
        long activeCount = keys.stream().filter(AuthorizedKey::isActive).count();
        System.out.println("📊 Active: " + activeCount + " key(s)");
    }
    
    private void outputJson(List<AuthorizedKey> keys) {
        System.out.println("{");
        System.out.println("  \"totalKeys\": " + keys.size() + ",");
        System.out.println("  \"activeKeys\": " + keys.stream().filter(AuthorizedKey::isActive).count() + ",");
        System.out.println("  \"keys\": [");
        
        for (int i = 0; i < keys.size(); i++) {
            AuthorizedKey key = keys.get(i);
            
            System.out.println("    {");
            System.out.println("      \"id\": " + key.getId() + ",");
            System.out.println("      \"owner\": \"" + key.getOwnerName() + "\",");
            System.out.println("      \"publicKey\": \"" + key.getPublicKey() + "\",");
            System.out.println("      \"active\": " + key.isActive() + ",");
            System.out.println("      \"createdAt\": \"" + key.getCreatedAt() + "\"");
            System.out.print("    }");
            
            if (i < keys.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        
        System.out.println("  ],");
        System.out.println("  \"timestamp\": \"" + java.time.Instant.now() + "\"");
        System.out.println("}");
    }
}
