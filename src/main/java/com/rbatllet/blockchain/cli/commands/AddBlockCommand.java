package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.AuthorizedKey;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.security.ECKeyDerivation;
import com.rbatllet.blockchain.security.KeyFileLoader;
import com.rbatllet.blockchain.service.UserFriendlyEncryptionAPI;
import com.rbatllet.blockchain.util.CryptoUtil;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.util.LoggingUtil;
import com.rbatllet.blockchain.util.format.FormatUtil;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Modern add block command using UserFriendlyEncryptionAPI
 */
@Command(name = "add-block", description = "Add a new block to the blockchain")
public class AddBlockCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(
        AddBlockCommand.class
    );

    @Parameters(index = "0", arity = "0..1", description = "Block data content")
    String blockContent;

    @Option(
        names = { "-f", "--file" },
        description = "Read block content from file instead of command line parameter"
    )
    String inputFile;

    @Option(
        names = { "-u", "--username" },
        description = "Username for block creation"
    )
    String username;

    @Option(
        names = { "--password" },
        description = "Password for encryption (enables encryption)"
    )
    String password;

    @Option(
        names = { "-i", "--identifier" },
        description = "Identifier for the block (helps with searches)"
    )
    String identifier;

    @Option(
        names = { "-j", "--json" },
        description = "Output result in JSON format"
    )
    boolean json = false;

    @Option(
        names = { "-d", "--detailed" },
        description = "Show detailed block information"
    )
    boolean detailed = false;

    @Option(
        names = { "-v", "--verbose" },
        description = "Enable verbose output"
    )
    boolean verbose = false;

    @Option(
        names = { "-r", "--recipient" },
        description = "Recipient username for encrypted blocks (uses public key encryption)"
    )
    String recipientUsername;

    @Option(
        names = { "-m", "--metadata" },
        description = "Custom metadata in format key=value (can be used multiple times)"
    )
    String[] metadata;

    @Option(
        names = { "-c", "--category" },
        description = "Content category for the block"
    )
    String category;

    @Option(
        names = { "-w", "--keywords" },
        description = "Keywords for the block (comma-separated)"
    )
    String keywords;

    @Option(names = { "--off-chain" }, description = "Store data off-chain")
    boolean offChain = false;

    @Option(
        names = { "--off-chain-file" },
        description = "Path to file for off-chain storage"
    )
    String offChainFilePath;

    @Option(
        names = { "-g", "--generate-key" },
        description = "Generate a new key pair for signing"
    )
    boolean generateKey = false;

    @Option(
        names = { "-k", "--key-file" },
        description = "Path to private key file (PEM, DER, or Base64 format)"
    )
    String keyFilePath;

    @Option(
        names = { "-s", "--signer" },
        description = "Username of the signer (creates demo key if needed)"
    )
    String signer;

    private UserFriendlyEncryptionAPI encryptionAPI;

    @Override
    public void run() {
        try {
            LoggingUtil.logOperationStart(
                logger,
                "add block operation",
                verbose || BlockchainCLI.verbose
            );

            // Handle file input if specified
            if (inputFile != null && !inputFile.trim().isEmpty()) {
                LoggingUtil.verboseLog(
                    logger,
                    "📁 Reading block content from file: " + inputFile,
                    verbose || BlockchainCLI.verbose
                );

                try {
                    java.nio.file.Path filePath = java.nio.file.Paths.get(inputFile);
                    blockContent = java.nio.file.Files.readString(filePath);
                    LoggingUtil.verboseLog(
                        logger,
                        "✅ Successfully read " + blockContent.length() + " characters from file",
                        verbose || BlockchainCLI.verbose
                    );
                } catch (java.io.IOException e) {
                    BlockchainCLI.error("❌ Failed to read file: " + e.getMessage());
                    if (verbose || BlockchainCLI.verbose) {
                        e.printStackTrace();
                    }
                    ExitUtil.exit(1);
                }
            }

            // Validate block content
            if (blockContent == null || blockContent.trim().isEmpty()) {
                BlockchainCLI.error("❌ Block content is required");
                BlockchainCLI.error("   Provide content directly or use -f/--file to read from file");
                ExitUtil.exit(1);
            }

            // Initialize blockchain and encryption API
            Blockchain blockchain = new Blockchain();
            encryptionAPI = new UserFriendlyEncryptionAPI(blockchain);

            LoggingUtil.logDataSize(
                logger,
                "Adding block",
                blockContent.length(),
                verbose || BlockchainCLI.verbose
            );

            Block createdBlock;

            // Handle key file option
            if (keyFilePath != null && !keyFilePath.trim().isEmpty()) {
                LoggingUtil.verboseLog(
                    logger,
                    "🔑 Loading private key from file: " + keyFilePath,
                    verbose || BlockchainCLI.verbose
                );

                try {
                    PrivateKey privateKey =
                        KeyFileLoader.loadPrivateKeyFromFile(keyFilePath);
                    if (privateKey == null) {
                        BlockchainCLI.error(
                            "❌ Failed to load private key from file: " +
                                keyFilePath
                        );
                        ExitUtil.exit(1);
                    }

                    // Create key pair with loaded private key (derive public key from private)
                    ECKeyDerivation keyDerivation = new ECKeyDerivation();
                    PublicKey publicKey =
                        keyDerivation.derivePublicKeyFromPrivate(privateKey);
                    KeyPair keyPair = new KeyPair(publicKey, privateKey);
                    String keyUsername = username != null
                        ? username
                        : "key_file_user";
                    encryptionAPI.setDefaultCredentials(keyUsername, keyPair);
                    LoggingUtil.verboseLog(
                        logger,
                        "✅ Successfully loaded and set private key from file",
                        verbose || BlockchainCLI.verbose
                    );

                    // Set username if not provided
                    if (username == null) {
                        username = keyUsername;
                    }
                } catch (Exception e) {
                    BlockchainCLI.error(
                        "❌ Failed to load key file: " + e.getMessage()
                    );
                    if (verbose || BlockchainCLI.verbose) {
                        e.printStackTrace();
                    }
                    ExitUtil.exit(1);
                }
            }

            // Handle generate key option by generating actual key pair
            String effectivePassword = password;
            if (generateKey) {
                LoggingUtil.verboseLog(
                    logger,
                    "🔑 Generating new key pair for signing",
                    verbose || BlockchainCLI.verbose
                );

                // Generate a new key pair using CryptoUtil
                try {
                    KeyPair keyPair = CryptoUtil.generateKeyPair();
                    encryptionAPI.setDefaultCredentials("demo_user", keyPair);
                    LoggingUtil.verboseLog(
                        logger,
                        "✅ Generated and set default key pair",
                        verbose || BlockchainCLI.verbose
                    );
                } catch (Exception e) {
                    BlockchainCLI.error(
                        "❌ Failed to generate key pair: " + e.getMessage()
                    );
                    ExitUtil.exit(1);
                }

                // Don't use password encryption when generating keys
                effectivePassword = null;
            }

            // Handle signer option
            if (signer != null && !signer.trim().isEmpty()) {
                LoggingUtil.verboseLog(
                    logger,
                    "👤 Using signer: " + signer,
                    verbose || BlockchainCLI.verbose
                );

                // Check if signer exists in authorized keys
                try {
                    AuthorizedKey authorizedKey =
                        blockchain.getAuthorizedKeyByOwner(signer);
                    if (authorizedKey == null) {
                        BlockchainCLI.error("❌ Signer not found: " + signer);
                        BlockchainCLI.error(
                            "   Use 'add-key \"" +
                                signer +
                                "\" --generate' to create the user first"
                        );
                        ExitUtil.exit(1);
                    }

                    LoggingUtil.verboseLog(
                        logger,
                        "✅ Signer found in authorized keys",
                        verbose || BlockchainCLI.verbose
                    );
                    LoggingUtil.verboseLog(
                        logger,
                        "🔑 Creating demo key for signer: " + signer,
                        verbose || BlockchainCLI.verbose
                    );
                    KeyPair keyPair = CryptoUtil.generateKeyPair();
                    encryptionAPI.setDefaultCredentials(signer, keyPair);
                    LoggingUtil.verboseLog(
                        logger,
                        "✅ Demo key created for signer",
                        verbose || BlockchainCLI.verbose
                    );
                } catch (Exception e) {
                    BlockchainCLI.error(
                        "❌ Failed to verify signer: " + e.getMessage()
                    );
                    ExitUtil.exit(1);
                }

                // Set the username to the signer
                username = signer;
            }

            // Use enhanced block creation with options
            UserFriendlyEncryptionAPI.BlockCreationOptions options =
                new UserFriendlyEncryptionAPI.BlockCreationOptions()
                    .withUsername(username)
                    .withPassword(effectivePassword)
                    .withIdentifier(identifier)
                    .withCategory(category)
                    .withOffChain(offChain)
                    .withOffChainFilePath(offChainFilePath)
                    .withRecipient(recipientUsername)
                    .withEncryption(
                        (effectivePassword != null &&
                                !effectivePassword.trim().isEmpty()) ||
                            (recipientUsername != null &&
                                !recipientUsername.trim().isEmpty())
                    );

            // Process keywords if provided
            if (keywords != null && !keywords.trim().isEmpty()) {
                String[] keywordArray = keywords.split(",");
                for (int i = 0; i < keywordArray.length; i++) {
                    keywordArray[i] = keywordArray[i].trim();
                }
                options.withKeywords(keywordArray);
            }

            // Process custom metadata if provided
            if (metadata != null && metadata.length > 0) {
                for (String metadataEntry : metadata) {
                    String[] parts = metadataEntry.split("=", 2);
                    if (parts.length == 2) {
                        options.withMetadata(parts[0].trim(), parts[1].trim());
                        LoggingUtil.verboseLog(
                            logger,
                            "Adding metadata: " + parts[0] + " = " + parts[1],
                            verbose || BlockchainCLI.verbose
                        );
                    } else {
                        BlockchainCLI.error(
                            "⚠️  Invalid metadata format: " +
                                metadataEntry +
                                " (expected key=value)"
                        );
                    }
                }
            }

            LoggingUtil.verboseLog(
                logger,
                "Creating block with options: encrypted=" +
                    options.isEncrypt() +
                    ", username=" +
                    options.getUsername() +
                    ", recipient=" +
                    options.getRecipientUsername() +
                    ", category=" +
                    options.getCategory() +
                    ", off-chain=" +
                    options.isOffChain(),
                verbose || BlockchainCLI.verbose
            );

            createdBlock = encryptionAPI.createBlockWithOptions(
                blockContent,
                options
            );

            if (createdBlock == null) {
                BlockchainCLI.error("❌ Failed to create block");
                ExitUtil.exit(1);
            }

            // Output results
            if (json) {
                outputJson(createdBlock);
            } else {
                outputText(createdBlock);
            }

            LoggingUtil.logOperationComplete(
                logger,
                "Block creation",
                verbose || BlockchainCLI.verbose
            );
        } catch (Exception e) {
            BlockchainCLI.error("❌ Add block failed: " + e.getMessage());
            logger.error("Add block command failed", e);
            if (verbose || BlockchainCLI.verbose) {
                e.printStackTrace();
            }
            ExitUtil.exit(1);
        }
    }

    private void outputText(Block block) {
        System.out.println("✅ Block Added Successfully");
        System.out.println("=".repeat(60));

        System.out.println("📦 Block #" + block.getBlockNumber());
        System.out.println(
            "📅 Timestamp: " + FormatUtil.formatTimestamp(block.getTimestamp())
        );
        System.out.println(
            "🔗 Hash: " + FormatUtil.truncateHash(block.getHash())
        );

        if (detailed) {
            System.out.println(
                "🔗 Previous Hash: " +
                    FormatUtil.truncateHash(block.getPreviousHash())
            );
            System.out.println(
                "🔑 Signer Key: " +
                    FormatUtil.truncateKey(block.getSignerPublicKey())
            );
            System.out.println(
                "✍️  Signature: " +
                    FormatUtil.truncateHash(block.getSignature())
            );
        }

        // Show metadata if available
        if (block.getContentCategory() != null) {
            System.out.println("📂 Category: " + block.getContentCategory());
        }
        if (detailed) {
            if (
                block.getManualKeywords() != null &&
                !block.getManualKeywords().trim().isEmpty()
            ) {
                System.out.println(
                    "🏷️  Keywords: " + block.getManualKeywords()
                );
            }
            if (
                block.getAutoKeywords() != null &&
                !block.getAutoKeywords().trim().isEmpty()
            ) {
                System.out.println(
                    "🤖 Auto Keywords: " + block.getAutoKeywords()
                );
            }
        }

        // Show encryption status
        if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
            System.out.println("🔐 Encrypted: Yes");
            // Check if recipient encrypted - info is in encryptionMetadata
            if (
                block.getEncryptionMetadata() != null &&
                block.getEncryptionMetadata().contains("RECIPIENT_ENCRYPTED")
            ) {
                // Extract recipient from JSON: {"type":"RECIPIENT_ENCRYPTED","recipient":"username"}
                try {
                    int recipientStart = block.getEncryptionMetadata().indexOf("\"recipient\":\"") + 13;
                    int recipientEnd = block.getEncryptionMetadata().indexOf("\"", recipientStart);
                    if (recipientStart > 12 && recipientEnd > recipientStart) {
                        String recipient = block.getEncryptionMetadata().substring(recipientStart, recipientEnd);
                        System.out.println("👤 Recipient: " + recipient);
                    }
                } catch (Exception e) {
                    // If we can't parse, skip showing recipient
                }
            }
        }

        // Show custom metadata if available
        if (
            block.getCustomMetadata() != null &&
            !block.getCustomMetadata().trim().isEmpty()
        ) {
            System.out.println(
                "📊 Custom Metadata: " + block.getCustomMetadata()
            );
        }

        // Show off-chain information
        if (block.hasOffChainData()) {
            var offChainData = block.getOffChainData();
            System.out.println(
                "💾 Off-chain: " +
                    FormatUtil.formatBytes(offChainData.getFileSize()) +
                    " (encrypted)"
            );
            if (detailed) {
                System.out.println(
                    "🔐 Content Type: " + offChainData.getContentType()
                );
            }
        }

        // Show data content
        String data = block.getData();
        if (block.getIsEncrypted() != null && block.getIsEncrypted()) {
            System.out.println(
                "📝 Data: [Encrypted content - " + data.length() + " chars]"
            );
        } else if (block.hasOffChainData()) {
            System.out.println("📝 Data: [Stored off-chain]");
        } else if (data.length() > 100) {
            System.out.println("📝 Data: " + data.substring(0, 97) + "...");
        } else {
            System.out.println("📝 Data: " + data);
        }
    }

    private void outputJson(Block block) {
        System.out.println("{");
        System.out.println("  \"success\": true,");
        System.out.println(
            "  \"blockNumber\": " + block.getBlockNumber() + ","
        );
        System.out.println(
            "  \"timestamp\": \"" + block.getTimestamp() + "\","
        );
        System.out.println("  \"hash\": \"" + block.getHash() + "\",");
        System.out.println(
            "  \"previousHash\": \"" + block.getPreviousHash() + "\","
        );
        System.out.println(
            "  \"data\": \"" + FormatUtil.escapeJson(block.getData()) + "\","
        );
        System.out.println(
            "  \"signerPublicKey\": \"" + block.getSignerPublicKey() + "\","
        );
        System.out.println(
            "  \"signature\": \"" + block.getSignature() + "\","
        );

        // Add available metadata
        System.out.println(
            "  \"category\": \"" +
                (block.getContentCategory() != null
                        ? block.getContentCategory()
                        : "") +
                "\","
        );
        System.out.println(
            "  \"manualKeywords\": \"" +
                (block.getManualKeywords() != null
                        ? FormatUtil.escapeJson(block.getManualKeywords())
                        : "") +
                "\","
        );
        System.out.println(
            "  \"autoKeywords\": \"" +
                (block.getAutoKeywords() != null
                        ? FormatUtil.escapeJson(block.getAutoKeywords())
                        : "") +
                "\","
        );
        System.out.println(
            "  \"encrypted\": " +
                (block.getIsEncrypted() != null
                        ? block.getIsEncrypted()
                        : false) +
                ","
        );

        // Add recipient info if available - extract from encryptionMetadata
        if (
            block.getIsEncrypted() != null &&
            block.getIsEncrypted() &&
            block.getEncryptionMetadata() != null &&
            block.getEncryptionMetadata().contains("RECIPIENT_ENCRYPTED")
        ) {
            // Extract recipient from JSON: {"type":"RECIPIENT_ENCRYPTED","recipient":"username"}
            try {
                int recipientStart = block.getEncryptionMetadata().indexOf("\"recipient\":\"") + 13;
                int recipientEnd = block.getEncryptionMetadata().indexOf("\"", recipientStart);
                if (recipientStart > 12 && recipientEnd > recipientStart) {
                    String recipient = block.getEncryptionMetadata().substring(recipientStart, recipientEnd);
                    System.out.println("  \"recipient\": \"" + recipient + "\",");
                }
            } catch (Exception e) {
                // If we can't parse, skip showing recipient
            }
        }

        // Add custom metadata if available
        if (
            block.getCustomMetadata() != null &&
            !block.getCustomMetadata().trim().isEmpty()
        ) {
            System.out.println(
                "  \"customMetadata\": \"" +
                    FormatUtil.escapeJson(block.getCustomMetadata()) +
                    "\","
            );
        }

        System.out.println(
            "  \"hasOffChainData\": " + block.hasOffChainData() + ","
        );
        System.out.println(
            "  \"timestamp\": \"" + java.time.Instant.now() + "\""
        );
        System.out.println("}");
    }

    // Utility methods removed - now using BlockchainDisplayUtils

    // Verbose logging now handled by LoggingUtil
}
