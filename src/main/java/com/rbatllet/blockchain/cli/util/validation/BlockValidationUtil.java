package com.rbatllet.blockchain.cli.util.validation;

import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.core.Blockchain;
import java.time.LocalDateTime;

/**
 * Adapter class that delegates to the core BlockValidationUtil implementation
 * @see com.rbatllet.blockchain.util.validation.BlockValidationUtil
 */
public class BlockValidationUtil {
    
    /**
     * Validate genesis block specifically
     * @param genesisBlock The genesis block to validate
     * @return true if the genesis block is valid
     */
    public static boolean validateGenesisBlock(Block genesisBlock) {
        return com.rbatllet.blockchain.util.validation.BlockValidationUtil.validateGenesisBlock(genesisBlock);
    }
    
    /**
     * Helper method to check if a key was authorized at a specific timestamp
     * @param blockchain The blockchain instance
     * @param publicKeyString The public key string to check
     * @param timestamp The timestamp to check authorization at
     * @return true if the key was authorized at the given timestamp
     */
    public static boolean wasKeyAuthorizedAt(Blockchain blockchain, String publicKeyString, LocalDateTime timestamp) {
        return com.rbatllet.blockchain.util.validation.BlockValidationUtil.wasKeyAuthorizedAt(blockchain, publicKeyString, timestamp);
    }
    
    /**
     * Truncate a hash string for display purposes
     * @param hash The hash string to truncate
     * @return The truncated hash string
     */
    public static String truncateHash(String hash) {
        return com.rbatllet.blockchain.util.validation.BlockValidationUtil.truncateHash(hash);
    }
}
