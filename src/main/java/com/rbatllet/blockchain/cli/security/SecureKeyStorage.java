package com.rbatllet.blockchain.cli.security;

import java.security.PrivateKey;

/**
 * Adapter class that delegates to the core SecureKeyStorage implementation
 * This class is maintained for backward compatibility with existing CLI code
 */
public class SecureKeyStorage {
    
    /**
     * Save a private key encrypted with password
     */
    public static boolean savePrivateKey(String ownerName, PrivateKey privateKey, String password) {
        return com.rbatllet.blockchain.security.SecureKeyStorage.savePrivateKey(ownerName, privateKey, password);
    }
    
    /**
     * Load a private key by decrypting with password
     */
    public static PrivateKey loadPrivateKey(String ownerName, String password) {
        return com.rbatllet.blockchain.security.SecureKeyStorage.loadPrivateKey(ownerName, password);
    }
    
    /**
     * Check if a private key exists for an owner
     */
    public static boolean hasPrivateKey(String ownerName) {
        return com.rbatllet.blockchain.security.SecureKeyStorage.hasPrivateKey(ownerName);
    }
    
    /**
     * Delete a private key file
     */
    public static boolean deletePrivateKey(String ownerName) {
        return com.rbatllet.blockchain.security.SecureKeyStorage.deletePrivateKey(ownerName);
    }
    
    /**
     * List all stored private key owners
     */
    public static String[] listStoredKeys() {
        return com.rbatllet.blockchain.security.SecureKeyStorage.listStoredKeys();
    }
}