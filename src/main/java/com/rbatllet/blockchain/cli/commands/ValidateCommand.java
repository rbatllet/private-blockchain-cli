package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.validation.BlockValidationResult;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to validate the blockchain integrity
 */
@Command(name = "validate", 
         description = "Validate the entire blockchain integrity")
public class ValidateCommand implements Runnable {
    
    @Option(names = {"-d", "--detailed"}, description = "Show detailed validation results for each block")
    private boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, description = "Output result in JSON format")
    private boolean json = false;
    
    @Option(names = {"-q", "--quick"}, description = "Perform quick validation (chain integrity only)")
    private boolean quick = false;
    
    @Override
    public void run() {
        try {
            BlockchainCLI.verbose("Starting blockchain validation...");
            
            Blockchain blockchain = new Blockchain();
            
            // Get basic chain information
            long blockCount = blockchain.getBlockCount();
            boolean isValid;
            
            if (quick) {
                BlockchainCLI.verbose("Performing quick validation...");
                isValid = blockchain.validateChain();
            } else {
                BlockchainCLI.verbose("Performing detailed validation...");
                isValid = performDetailedValidation(blockchain);
            }
            
            if (json) {
                outputJson(isValid, blockCount, blockchain);
            } else {
                outputText(isValid, blockCount, blockchain);
            }
            
            // Exit with error code if validation failed
            if (!isValid) {
                ExitUtil.exit(1);
            }
            
        } catch (SecurityException e) {
            BlockchainCLI.error("❌ Validation failed: Security error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (RuntimeException e) {
            BlockchainCLI.error("❌ Validation failed: Runtime error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        } catch (Exception e) {
            BlockchainCLI.error("❌ Validation failed: Unexpected error - " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    /**
     * Using the BlockValidationResult implementation from the core project
     */
    
    private boolean performDetailedValidation(Blockchain blockchain) {
        try {
            List<Block> blocks = blockchain.getAllBlocks();
            
            BlockchainCLI.verbose("Validating " + blocks.size() + " blocks...");
            
            boolean isChainValid = true;
            
            // Verify the genesis block separately
            if (!blocks.isEmpty()) {
                Block genesisBlock = blocks.get(0);
                boolean isGenesisValid = validateGenesisBlock(genesisBlock);
                if (!isGenesisValid) {
                    isChainValid = false;
                }
            } else {
                BlockchainCLI.error("❌ No blocks found in blockchain");
                return false;
            }
            
            // Validate each block individually with detailed checks
            List<BlockValidationResult> validationResults = new ArrayList<>();
            
            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);
                Block previousBlock = blocks.get(i - 1);
                
                // Perform detailed validations and store results
                BlockValidationResult result = validateBlockDetailed(blockchain, currentBlock, previousBlock);
                validationResults.add(result);
                
                // If any block is invalid, the chain is invalid
                if (!result.isValid()) {
                    isChainValid = false;
                }
                
                // Show detailed results if requested
                if (detailed && !json) {
                    showDetailedBlockValidation(currentBlock, result);
                }
            }
            
            return isChainValid;
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Error during detailed validation: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            return false;
        }
    }
    
    /**
     * Validates an individual block with detailed validations
     * Uses the core project's BlockValidationResult for validation results
     * Made public to allow unit testing
     */
    public BlockValidationResult validateBlockDetailed(Blockchain blockchain, Block block, Block previousBlock) {
        BlockValidationResult result = new BlockValidationResult();
        
        try {
            // Validate previous hash
            boolean previousHashValid = block.getPreviousHash().equals(previousBlock.getHash());
            result.setPreviousHashValid(previousHashValid);
            if (!previousHashValid) {
                result.setErrorMessage("Previous hash mismatch: expected " + previousBlock.getHash() + ", got " + block.getPreviousHash());
            }
            
            // Validate block number
            long expectedNumber = previousBlock.getBlockNumber() + 1L;
            long actualNumber = block.getBlockNumber();
            boolean blockNumberValid = expectedNumber == actualNumber;
            result.setBlockNumberValid(blockNumberValid);
            if (!blockNumberValid) {
                result.setErrorMessage("Block number mismatch: expected " + expectedNumber + ", got " + actualNumber);
            }
            
            // Validate hash integrity
            String calculatedHash = CryptoUtil.calculateHash(blockchain.buildBlockContent(block));
            boolean hashValid = block.getHash().equals(calculatedHash);
            result.setHashIntegrityValid(hashValid);
            if (!hashValid) {
                result.setErrorMessage("Hash mismatch: expected " + calculatedHash + ", got " + block.getHash());
            }
            
            // Validate digital signature
            try {
                java.security.PublicKey signerPublicKey = CryptoUtil.stringToPublicKey(block.getSignerPublicKey());
                boolean signatureValid = CryptoUtil.verifySignature(blockchain.buildBlockContent(block), 
                                                                   block.getSignature(), 
                                                                   signerPublicKey);
                result.setSignatureValid(signatureValid);
                if (!signatureValid) {
                    result.setErrorMessage("Invalid digital signature");
                }
            } catch (Exception e) {
                result.setSignatureValid(false);
                result.setErrorMessage("Error verifying signature: " + e.getMessage());
            }
            
            // Validate key authorization using Blockchain's public method
            boolean keyAuthorized = blockchain.wasKeyAuthorizedAt(block.getSignerPublicKey(), block.getTimestamp());
            result.setAuthorizedKeyValid(keyAuthorized);
            if (!keyAuthorized) {
                result.setErrorMessage("Block signed by key that was not authorized at time of creation");
            }
            
            // The overall validity is automatically calculated by the isValid() method
            // based on all individual validation results
            
            return result;
        } catch (Exception e) {
            if (result.isPreviousHashValid() && result.isBlockNumberValid() && 
                result.isHashIntegrityValid() && result.isSignatureValid() && 
                result.isAuthorizedKeyValid()) {
                // Only if no specific error has been set
                result.setPreviousHashValid(false);
            }
            result.setErrorMessage("Error validating block: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Validate genesis block specifically
     */
    private boolean validateGenesisBlock(Block genesisBlock) {
        boolean isValid = true;
        final String GENESIS_PREVIOUS_HASH = "0";
        
        if (genesisBlock.getBlockNumber() != 0) {
            BlockchainCLI.error("❌ Genesis block has invalid block number: " + genesisBlock.getBlockNumber());
            isValid = false;
        }
        
        if (!GENESIS_PREVIOUS_HASH.equals(genesisBlock.getPreviousHash())) {
            BlockchainCLI.error("❌ Genesis block has invalid previous hash");
            isValid = false;
        }
        
        if (detailed && !json) {
            String status = isValid ? "✅ VALID" : "❌ INVALID";
            System.out.println("📦 Genesis Block - " + status);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("   📅 Timestamp: " + genesisBlock.getTimestamp().format(formatter));
            System.out.println("   🔗 Hash: " + truncateHash(genesisBlock.getHash()));
            System.out.println("   📝 Data: " + genesisBlock.getData());
            System.out.println();
        }
        
        return isValid;
    }
    
    /**
     * Show detailed validation results for a block
     */
    private void showDetailedBlockValidation(Block block, BlockValidationResult result) {
        String overallStatus = result.isValid() ? "✅ VALID" : "❌ INVALID";
        System.out.println("📦 Block #" + block.getBlockNumber() + " - " + overallStatus);
        
        // Show basic block information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("   📅 Timestamp: " + block.getTimestamp().format(formatter));
        System.out.println("   🔗 Hash: " + truncateHash(block.getHash()));
        System.out.println("   🔗 Previous Hash: " + truncateHash(block.getPreviousHash()));
        System.out.println("   📝 Data Length: " + block.getData().length() + " chars");
        
        // Show detailed validation results
        System.out.println("   🔍 Validation Details:");
        System.out.println("      - Previous Hash: " + (result.isPreviousHashValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Block Number: " + (result.isBlockNumberValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Hash Integrity: " + (result.isHashIntegrityValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Digital Signature: " + (result.isSignatureValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Key Authorization: " + (result.isAuthorizedKeyValid() ? "✅ Valid" : "❌ Invalid"));
        
        // Show error message if exists
        if (result.getErrorMessage() != null) {
            System.out.println("   ⚠️ Error: " + result.getErrorMessage());
        }
        
        System.out.println();
    }
    
    private String truncateHash(String hash) {
        if (hash == null) return "null";
        return hash.length() > 32 ? 
            hash.substring(0, 16) + "..." + hash.substring(hash.length() - 16) :
            hash;
    }
    
    private void outputText(boolean isValid, long blockCount, Blockchain blockchain) {
        System.out.println("🔍 Blockchain Validation Results");
        System.out.println("=" .repeat(50));
        
        String status = isValid ? "✅ VALID" : "❌ INVALID";
        System.out.println("🔗 Chain Status: " + status);
        System.out.println("📊 Total Blocks: " + blockCount);
        
        try {
            int authorizedKeys = blockchain.getAuthorizedKeys().size();
            System.out.println("🔑 Authorized Keys: " + authorizedKeys);
            
            if (blockCount > 0) {
                Block lastBlock = blockchain.getLastBlock();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.println("📅 Last Block: " + lastBlock.getTimestamp().format(formatter));
            }
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Could not retrieve additional chain information: " + e.getMessage());
        }
        
        System.out.println();
        
        if (isValid) {
            BlockchainCLI.success("Blockchain validation completed successfully!");
            System.out.println("💡 All blocks are properly linked and signatures are valid.");
        } else {
            BlockchainCLI.error("❌ Blockchain validation failed!");
            System.out.println("⚠️  The blockchain contains invalid blocks or broken links.");
            System.out.println("   Run with --detailed flag for more information.");
        }
    }
    
    private void outputJson(boolean isValid, long blockCount, Blockchain blockchain) {
        System.out.println("{");
        System.out.println("  \"valid\": " + isValid + ",");
        System.out.println("  \"totalBlocks\": " + blockCount + ",");
        
        try {
            int authorizedKeys = blockchain.getAuthorizedKeys().size();
            System.out.println("  \"authorizedKeys\": " + authorizedKeys + ",");
            
            if (blockCount > 0) {
                Block lastBlock = blockchain.getLastBlock();
                System.out.println("  \"lastBlockTimestamp\": \"" + lastBlock.getTimestamp() + "\",");
                System.out.println("  \"lastBlockHash\": \"" + lastBlock.getHash() + "\",");
            }
            
        } catch (Exception e) {
            BlockchainCLI.verbose("Could not retrieve additional chain information: " + e.getMessage());
        }
        
        System.out.println("  \"validationTimestamp\": \"" + java.time.Instant.now() + "\",");
        System.out.println("  \"validationType\": \"" + (quick ? "quick" : "detailed") + "\"");
        System.out.println("}");
    }
}
