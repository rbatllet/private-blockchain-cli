package com.rbatllet.blockchain.cli.security;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Adapter class that delegates to the core KeyFileLoader implementation
 * This class is maintained for backward compatibility with existing CLI code
 */
public class KeyFileLoader {
    
    /**
     * Load a private key from a file
     * Supports multiple formats: PEM, DER, and raw Base64
     * 
     * @param keyFilePath Path to the key file
     * @return PrivateKey object or null if loading fails
     */
    public static PrivateKey loadPrivateKeyFromFile(String keyFilePath) {
        return com.rbatllet.blockchain.security.KeyFileLoader.loadPrivateKeyFromFile(keyFilePath);
    }
    
    /**
     * Load a public key from a file
     * This method is provided for completeness, though typically
     * public keys are derived from authorized keys in the blockchain
     * 
     * @param keyFilePath Path to the public key file
     * @return PublicKey object or null if loading fails
     */
    public static PublicKey loadPublicKeyFromFile(String keyFilePath) {
        return com.rbatllet.blockchain.security.KeyFileLoader.loadPublicKeyFromFile(keyFilePath);
    }
    
    /**
     * Validate that a file path is safe to read
     */
    public static boolean isValidKeyFilePath(String keyFilePath) {
        return com.rbatllet.blockchain.security.KeyFileLoader.isValidKeyFilePath(keyFilePath);
    }
    
    /**
     * Get file format information for debugging
     */
    public static String detectKeyFileFormat(String keyFilePath) {
        return com.rbatllet.blockchain.security.KeyFileLoader.detectKeyFileFormat(keyFilePath);
    }
}
