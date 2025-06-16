package com.rbatllet.blockchain.cli.util.format;

import com.rbatllet.blockchain.entity.Block;
import java.time.LocalDateTime;

/**
 * Adapter class that delegates to the core FormatUtil implementation
 * @see com.rbatllet.blockchain.util.format.FormatUtil
 */
public class FormatUtil {
    
    /**
     * Format a timestamp using the default format
     * @param timestamp The timestamp to format
     * @return The formatted timestamp string
     */
    public static String formatTimestamp(LocalDateTime timestamp) {
        return com.rbatllet.blockchain.util.format.FormatUtil.formatTimestamp(timestamp);
    }
    
    /**
     * Format a timestamp using a custom format
     * @param timestamp The timestamp to format
     * @param pattern The pattern to use for formatting
     * @return The formatted timestamp string
     */
    public static String formatTimestamp(LocalDateTime timestamp, String pattern) {
        return com.rbatllet.blockchain.util.format.FormatUtil.formatTimestamp(timestamp, pattern);
    }
    
    /**
     * Truncate a hash string for display purposes
     * @param hash The hash string to truncate
     * @return The truncated hash string
     */
    public static String truncateHash(String hash) {
        return com.rbatllet.blockchain.util.format.FormatUtil.truncateHash(hash);
    }
    
    /**
     * Format block information for display
     * @param block The block to format
     * @return A formatted string with block information
     */
    public static String formatBlockInfo(Block block) {
        return com.rbatllet.blockchain.util.format.FormatUtil.formatBlockInfo(block);
    }
    
    /**
     * Format a string to a fixed width by truncating or padding
     * @param input The input string
     * @param width The desired width
     * @return The formatted string
     */
    public static String fixedWidth(String input, int width) {
        return com.rbatllet.blockchain.util.format.FormatUtil.fixedWidth(input, width);
    }
}
