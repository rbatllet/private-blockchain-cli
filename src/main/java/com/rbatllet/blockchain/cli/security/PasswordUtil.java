package com.rbatllet.blockchain.cli.security;

import java.io.InputStream;

/**
 * Adapter class that delegates to the core PasswordUtil implementation
 * This class is maintained for backward compatibility with existing CLI code
 */
public class PasswordUtil {
    
    /**
     * Read password securely from console
     * Falls back to Scanner if Console is not available (e.g., in IDEs)
     */
    public static String readPassword(String prompt) {
        return com.rbatllet.blockchain.security.PasswordUtil.readPassword(prompt);
    }
    
    /**
     * Read password securely from console with custom input stream (for testing)
     * Falls back to Scanner if Console is not available (e.g., in IDEs)
     */
    public static String readPassword(String prompt, InputStream inputStream) {
        return com.rbatllet.blockchain.security.PasswordUtil.readPassword(prompt, inputStream);
    }
    
    /**
     * Read password with confirmation
     */
    public static String readPasswordWithConfirmation(String prompt) {
        return com.rbatllet.blockchain.security.PasswordUtil.readPasswordWithConfirmation(prompt);
    }
    
    /**
     * Read password with confirmation using custom input stream (for testing)
     */
    public static String readPasswordWithConfirmation(String prompt, InputStream inputStream) {
        return com.rbatllet.blockchain.security.PasswordUtil.readPasswordWithConfirmation(prompt, inputStream);
    }
    
    /**
     * Validate password strength (basic validation)
     */
    public static boolean isValidPassword(String password) {
        return com.rbatllet.blockchain.security.PasswordUtil.isValidPassword(password);
    }
}
