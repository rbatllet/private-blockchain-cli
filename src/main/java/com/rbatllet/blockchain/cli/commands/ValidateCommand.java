package com.rbatllet.blockchain.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.entity.AuthorizedKey;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.util.ExitUtil;
import com.rbatllet.blockchain.util.CryptoUtil;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to validate the blockchain integrity
 */
@Command(name = "validate", 
         description = "Validate the entire blockchain integrity")
public class ValidateCommand implements Runnable {
    
    @Option(names = {"-d", "--detailed"}, 
            description = "Show detailed validation results for each block")
    boolean detailed = false;
    
    @Option(names = {"-j", "--json"}, 
            description = "Output result in JSON format")
    boolean json = false;
    
    @Option(names = {"-q", "--quick"}, 
            description = "Perform quick validation (chain integrity only)")
    boolean quick = false;
    
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
            
        } catch (Exception e) {
            BlockchainCLI.error("Validation failed: " + e.getMessage());
            if (BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }
    
    /**
     * Class to store detailed validation results for a block
     */
    /**
     * Clase que encapsula los resultados de la validación de un bloque
     * Se hace pública para permitir pruebas unitarias
     */
    public class BlockValidationResult {
        private boolean valid = true;
        private boolean previousHashValid = true;
        private boolean blockNumberValid = true;
        private boolean hashIntegrityValid = true;
        private boolean signatureValid = true;
        private boolean authorizedKeyValid = true;
        private String errorMessage = null;
        
        public boolean isValid() {
            return valid && previousHashValid && blockNumberValid && 
                   hashIntegrityValid && signatureValid && authorizedKeyValid;
        }
        
        public boolean isPreviousHashValid() { return previousHashValid; }
        public void setPreviousHashValid(boolean valid) { 
            this.previousHashValid = valid; 
            if (!valid) this.valid = false;
        }
        
        public boolean isBlockNumberValid() { return blockNumberValid; }
        public void setBlockNumberValid(boolean valid) { 
            this.blockNumberValid = valid; 
            if (!valid) this.valid = false;
        }
        
        public boolean isHashIntegrityValid() { return hashIntegrityValid; }
        public void setHashIntegrityValid(boolean valid) { 
            this.hashIntegrityValid = valid; 
            if (!valid) this.valid = false;
        }
        
        public boolean isSignatureValid() { return signatureValid; }
        public void setSignatureValid(boolean valid) { 
            this.signatureValid = valid; 
            if (!valid) this.valid = false;
        }
        
        public boolean isAuthorizedKeyValid() { return authorizedKeyValid; }
        public void setAuthorizedKeyValid(boolean valid) { 
            this.authorizedKeyValid = valid; 
            if (!valid) this.valid = false;
        }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String message) { this.errorMessage = message; }
    }
    
    private boolean performDetailedValidation(Blockchain blockchain) {
        try {
            List<Block> blocks = blockchain.getAllBlocks();
            
            BlockchainCLI.verbose("Validating " + blocks.size() + " blocks...");
            
            boolean isChainValid = true;
            
            // Verificar el bloque génesis por separado
            if (!blocks.isEmpty()) {
                Block genesisBlock = blocks.get(0);
                boolean isGenesisValid = validateGenesisBlock(genesisBlock);
                if (!isGenesisValid) {
                    isChainValid = false;
                }
            } else {
                BlockchainCLI.error("No blocks found in blockchain");
                return false;
            }
            
            // Validar cada bloque individualmente con comprobaciones detalladas
            List<BlockValidationResult> validationResults = new ArrayList<>();
            
            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);
                Block previousBlock = blocks.get(i - 1);
                
                // Realizar validaciones detalladas y almacenar resultados
                BlockValidationResult result = validateBlockDetailed(blockchain, currentBlock, previousBlock);
                validationResults.add(result);
                
                // Si algún bloque es inválido, la cadena es inválida
                if (!result.isValid()) {
                    isChainValid = false;
                }
                
                // Mostrar resultados detallados si se solicita
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
     * Valida un bloque individual con validaciones detalladas
     * Utiliza los métodos públicos de la clase Blockchain para realizar las validaciones
     * Se hace público para permitir pruebas unitarias
     */
    public BlockValidationResult validateBlockDetailed(Blockchain blockchain, Block block, Block previousBlock) {
        BlockValidationResult result = new BlockValidationResult();
        
        try {
            // Validar hash previo
            boolean previousHashValid = block.getPreviousHash().equals(previousBlock.getHash());
            result.setPreviousHashValid(previousHashValid);
            if (!previousHashValid) {
                result.setErrorMessage("Previous hash mismatch: expected " + previousBlock.getHash() + ", got " + block.getPreviousHash());
            }
            
            // Validar número de bloque
            boolean blockNumberValid = block.getBlockNumber() == previousBlock.getBlockNumber() + 1;
            result.setBlockNumberValid(blockNumberValid);
            if (!blockNumberValid) {
                result.setErrorMessage("Block number mismatch: expected " + (previousBlock.getBlockNumber() + 1) + ", got " + block.getBlockNumber());
            }
            
            // Validar integridad del hash
            String calculatedHash = CryptoUtil.calculateHash(blockchain.buildBlockContent(block));
            boolean hashValid = block.getHash().equals(calculatedHash);
            result.setHashIntegrityValid(hashValid);
            if (!hashValid) {
                result.setErrorMessage("Hash mismatch: expected " + calculatedHash + ", got " + block.getHash());
            }
            
            // Validar firma digital
            try {
                PublicKey signerPublicKey = CryptoUtil.stringToPublicKey(block.getSignerPublicKey());
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
            
            // Validar autorización de la clave usando el método público de Blockchain
            boolean keyAuthorized = blockchain.wasKeyAuthorizedAt(block.getSignerPublicKey(), block.getTimestamp());
            result.setAuthorizedKeyValid(keyAuthorized);
            if (!keyAuthorized) {
                result.setErrorMessage("Block signed by key that was not authorized at time of creation");
            }
            
            return result;
        } catch (Exception e) {
            // No necesitamos setValid(false) porque los setters individuales ya actualizan valid
            // cuando se establece un valor falso
            if (result.isPreviousHashValid() && result.isBlockNumberValid() && 
                result.isHashIntegrityValid() && result.isSignatureValid() && 
                result.isAuthorizedKeyValid()) {
                // Solo si no se ha establecido ningún error específico
                result.setPreviousHashValid(false); // Esto establecerá valid = false
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
            BlockchainCLI.error("Genesis block has invalid block number: " + genesisBlock.getBlockNumber());
            isValid = false;
        }
        
        if (!GENESIS_PREVIOUS_HASH.equals(genesisBlock.getPreviousHash())) {
            BlockchainCLI.error("Genesis block has invalid previous hash");
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
     * Helper method que utiliza el método público wasKeyAuthorizedAt de la clase Blockchain
     */
    private boolean wasKeyAuthorizedAt(Blockchain blockchain, String publicKeyString, LocalDateTime timestamp) {
        try {
            // Usar el método público de la clase Blockchain
            return blockchain.wasKeyAuthorizedAt(publicKeyString, timestamp);
        } catch (Exception e) {
            BlockchainCLI.verbose("Error checking key authorization: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Show detailed validation results for a block
     */
    private void showDetailedBlockValidation(Block block, BlockValidationResult result) {
        String overallStatus = result.isValid() ? "✅ VALID" : "❌ INVALID";
        System.out.println("📦 Block #" + block.getBlockNumber() + " - " + overallStatus);
        
        // Mostrar información básica del bloque
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("   📅 Timestamp: " + block.getTimestamp().format(formatter));
        System.out.println("   🔗 Hash: " + truncateHash(block.getHash()));
        System.out.println("   🔗 Previous Hash: " + truncateHash(block.getPreviousHash()));
        System.out.println("   📝 Data Length: " + block.getData().length() + " chars");
        
        // Mostrar resultados detallados de las validaciones
        System.out.println("   🔍 Validation Details:");
        System.out.println("      - Previous Hash: " + (result.isPreviousHashValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Block Number: " + (result.isBlockNumberValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Hash Integrity: " + (result.isHashIntegrityValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Digital Signature: " + (result.isSignatureValid() ? "✅ Valid" : "❌ Invalid"));
        System.out.println("      - Key Authorization: " + (result.isAuthorizedKeyValid() ? "✅ Valid" : "❌ Invalid"));
        
        // Mostrar mensaje de error si existe
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
            BlockchainCLI.error("Blockchain validation failed!");
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
