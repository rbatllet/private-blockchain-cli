package com.rbatllet.blockchain.cli.util.validation;

/**
 * Adapter class that delegates to the core BlockValidationResult implementation
 * @see com.rbatllet.blockchain.util.validation.BlockValidationResult
 */
public class BlockValidationResult {
    
    private com.rbatllet.blockchain.util.validation.BlockValidationResult coreResult;
    
    /**
     * Default constructor
     */
    public BlockValidationResult() {
        this.coreResult = new com.rbatllet.blockchain.util.validation.BlockValidationResult();
    }
    
    /**
     * Constructor with core result
     * @param coreResult The core result to wrap
     */
    public BlockValidationResult(com.rbatllet.blockchain.util.validation.BlockValidationResult coreResult) {
        this.coreResult = coreResult;
    }
    
    /**
     * Check if the block is valid overall
     * @return true if all validations pass
     */
    public boolean isValid() {
        return coreResult.isValid();
    }
    
    /**
     * Set the validation result for previous hash
     * @param valid true if valid
     * @return this object for chaining
     */
    public BlockValidationResult setPreviousHashValid(boolean valid) {
        coreResult.setPreviousHashValid(valid);
        return this;
    }
    
    /**
     * Set the validation result for block number
     * @param valid true if valid
     * @return this object for chaining
     */
    public BlockValidationResult setBlockNumberValid(boolean valid) {
        coreResult.setBlockNumberValid(valid);
        return this;
    }
    
    /**
     * Set the validation result for hash integrity
     * @param valid true if valid
     * @return this object for chaining
     */
    public BlockValidationResult setHashIntegrityValid(boolean valid) {
        coreResult.setHashIntegrityValid(valid);
        return this;
    }
    
    /**
     * Set the validation result for signature
     * @param valid true if valid
     * @return this object for chaining
     */
    public BlockValidationResult setSignatureValid(boolean valid) {
        coreResult.setSignatureValid(valid);
        return this;
    }
    
    /**
     * Set the validation result for authorized key
     * @param valid true if valid
     * @return this object for chaining
     */
    public BlockValidationResult setAuthorizedKeyValid(boolean valid) {
        coreResult.setAuthorizedKeyValid(valid);
        return this;
    }
    
    /**
     * Set an error message
     * @param message the error message
     * @return this object for chaining
     */
    public BlockValidationResult setErrorMessage(String message) {
        coreResult.setErrorMessage(message);
        return this;
    }
    
    /**
     * Check if previous hash is valid
     * @return true if valid
     */
    public boolean isPreviousHashValid() {
        return coreResult.isPreviousHashValid();
    }
    
    /**
     * Check if block number is valid
     * @return true if valid
     */
    public boolean isBlockNumberValid() {
        return coreResult.isBlockNumberValid();
    }
    
    /**
     * Check if hash integrity is valid
     * @return true if valid
     */
    public boolean isHashIntegrityValid() {
        return coreResult.isHashIntegrityValid();
    }
    
    /**
     * Check if signature is valid
     * @return true if valid
     */
    public boolean isSignatureValid() {
        return coreResult.isSignatureValid();
    }
    
    /**
     * Check if authorized key is valid
     * @return true if valid
     */
    public boolean isAuthorizedKeyValid() {
        return coreResult.isAuthorizedKeyValid();
    }
    
    /**
     * Get the error message if any
     * @return the error message or null if none
     */
    public String getErrorMessage() {
        return coreResult.getErrorMessage();
    }
    
    /**
     * Get the core result
     * @return the core result
     */
    public com.rbatllet.blockchain.util.validation.BlockValidationResult getCoreResult() {
        return coreResult;
    }
}
