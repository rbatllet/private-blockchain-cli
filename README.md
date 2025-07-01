# Private Blockchain CLI

A command-line interface for managing a private blockchain system built with Java 21, Maven, and PicoCLI.

## 📋 Table of Contents

- [Overview](#-overview)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Commands](#-commands)
- [Building from Source](#-building-from-source)
- [Basic Testing](#-basic-testing)
- [Technical Details](#-technical-details)
- [Documentation](#-documentation)
- [License](#-license)

## 🚀 Overview

This CLI application provides a secure interface for managing a private blockchain. It is built with enterprise-grade technologies and offers complete blockchain management capabilities.

### What You Can Do

- **Check blockchain status** and get detailed statistics
- **Validate blockchain integrity** with comprehensive checks including off-chain data
- **Manage authorized keys** for secure block signing
- **Add new blocks** with automatic off-chain storage for large data (>512KB)
- **Search through blockchain** with hybrid multi-level search capabilities
- **Export and import** blockchain data for backup and migration
- **Monitor chain health** with detailed validation reports
- **Process keywords** automatically from content or manually specified
- **Organize content** by categories for efficient filtering

### Key Features

✅ **Complete Implementation** - All core commands fully working  
✅ **Off-Chain Storage** - Automatic large data handling with AES-128-CBC encryption  
✅ **Hybrid Search** - Multi-level search: Fast, Balanced, and Exhaustive modes  
✅ **Modern Cryptography** - ECDSA with SHA3-256 and hierarchical key management  
✅ **Secure Architecture** - Cryptographic key management and validation  
✅ **Keywords & Categories** - Automatic and manual content organization  
✅ **Multiple Output Formats** - Text, JSON, and detailed views  
✅ **Robust Testing** - 295+ tests with comprehensive coverage  
✅ **Production Ready** - Enterprise-grade error handling and logging  
✅ **Easy to Use** - Clear help system and interactive demos  

### 🔐 Secure Key Management Features

✅ **Production-Grade Security** - AES-256 encrypted private key storage  
✅ **Modern Cryptography** - ECDSA with secp256r1 curve and SHA3-256 hashing  
✅ **Hierarchical Keys** - Root, intermediate, and operational key support  
✅ **Password Protection** - Strong password validation with PBKDF2 key derivation  
✅ **Dual Mode Operation** - Demo mode for testing, production mode for real use  
✅ **Key Lifecycle** - Complete key rotation and revocation support  
✅ **Audit Trail** - Track key usage and operations  

## 📦 Prerequisites

Before using this application, make sure you have:

- **Java 21** or higher installed
- **Maven 3.6+** (only needed for building from source)
- At least **50MB** of free disk space

### Checking Java Version

```zsh
java -version
```

You should see something like:
```
java version "21.0.1" or higher
```

## 💻 Installation

### Option 1: Docker (Recommended - No Java Required) 🐳

The fastest way to get started without installing Java locally:

```zsh
# Clone and build
git clone <repository-url>
cd privateBlockchain-cli
docker build -t blockchain-cli .

# Start using immediately
docker run --rm blockchain-cli --version
docker run --rm blockchain-cli status

# Add blocks from files using Docker
echo "My first Docker blockchain entry" > my-data.txt
docker run --rm \
  -v "$(pwd)/blockchain-data":/app/data \
  -v "$(pwd)":/app/files \
  --entrypoint /bin/zsh \
  blockchain-cli \
  -c "cd /app && ln -sf /app/data/blockchain.db blockchain.db && java -jar /app/blockchain-cli.jar add-block --file /app/files/my-data.txt --generate-key"
```

### Option 2: Using Pre-built JAR

1. Download the latest `blockchain-cli.jar` file (32MB)
2. Place it in your preferred directory
3. Make sure Java 21+ is installed
4. You're ready to go!

### Option 3: Building from Source

```zsh
# Clone the repository
git clone <repository-url>
cd privateBlockchain-cli

# Build the application
mvn clean package

# The executable JAR will be created at target/blockchain-cli.jar
```

## 🚀 Quick Start

### Using Docker (Easiest) 🐳

```zsh
# Build once
docker build -t blockchain-cli .

# Check version
docker run --rm blockchain-cli --version

# Get help
docker run --rm blockchain-cli --help

# Check blockchain status
docker run --rm blockchain-cli status
```

### Using JAR directly

### 1. Check if the CLI is working

```zsh
java -jar blockchain-cli.jar --version
```

Expected output:
```
1.0.4
```

### 2. View help information

```zsh
java -jar blockchain-cli.jar --help
```

### 3. Check blockchain status

```zsh
java -jar blockchain-cli.jar status
```

This will initialize the blockchain database (if it doesn't exist) and show current status:

```
🔗 Blockchain Status
==================================================
📊 Total blocks: 1
🔑 Authorized keys: 0
🔒 Cryptographic Suite: ECDSA-SHA3-256 (secp256r1)
✅ Chain integrity: VALID
⚠️  Run 'validate --detailed' for comprehensive validation
```

### 4. Add your first authorized user

```zsh
# Generate a new ECDSA key pair (secp256r1 curve)
java -jar blockchain-cli.jar add-key "Alice" --generate --show-private

# For production use with secure key storage:
# java -jar blockchain-cli.jar add-key "Admin" --generate --store-private
# You'll be prompted to enter a secure password
```

### 5. Add your first block

```zsh
# Add block with direct data input
java -jar blockchain-cli.jar add-block "My first blockchain entry" --signer Alice

# NEW: Add block content from external file
echo "This content comes from a file" > my-data.txt
java -jar blockchain-cli.jar add-block --file my-data.txt --generate-key

# For large files (automatic off-chain storage)
java -jar blockchain-cli.jar add-block --file large-report.pdf --category REPORT --generate-key
```

**✨ NEW: File Input Functionality**
The `--file` option allows you to read block content from external files instead of providing data directly on the command line. This is especially useful for:
- Large documents, reports, or datasets
- Files with special characters or formatting
- Automated processing of existing files
- Better separation of data and commands

**✨ NEW: Enhanced --signer functionality**
The `--signer` parameter now works seamlessly with existing authorized users. When you specify an existing signer, the CLI creates a demo mode signature that simulates real-world usage.

### 6. Verify everything worked with detailed validation

```zsh
# Basic validation
java -jar blockchain-cli.jar validate

# For comprehensive validation including key status and cryptographic integrity
java -jar blockchain-cli.jar validate --detailed

# Example output will show:
# - Block structure validation
# - Signature verification using ECDSA
# - Key revocation status
# - Cryptographic hash integrity (SHA3-256)
```

### Complete Workflow Example

Here's a complete workflow that demonstrates all major features including the new secure private key management:

```zsh
# 1. Check initial status
java -jar blockchain-cli.jar status

# 2. Set up users with different security levels
# Production user with stored private key
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
🔐 Enter password to protect private key: [hidden]
🔒 Private key stored securely for: Alice

# Demo user without stored private key
java -jar blockchain-cli.jar add-key "Bob" --generate

# 3. List all keys and check stored private keys
java -jar blockchain-cli.jar list-keys --detailed
java -jar blockchain-cli.jar manage-keys --list

# 4. Add blocks using different signing methods
# Production signing (requires password)
java -jar blockchain-cli.jar add-block "Production transaction" --signer Alice
🔐 Enter password for Alice: [hidden]
✅ Using stored private key for signer: Alice

# Demo mode signing (temporary key)
java -jar blockchain-cli.jar add-block "Demo transaction" --signer Bob
⚠️  DEMO MODE: No stored private key found for signer: Bob
🔑 DEMO: Created temporary key for existing signer: Bob

# Quick test with auto-generated key
java -jar blockchain-cli.jar add-block "Test transaction" --generate-key

# 5. Validate blockchain integrity
java -jar blockchain-cli.jar validate --detailed

# 6. Search for content
java -jar blockchain-cli.jar search "transaction"

# 7. Create a backup
java -jar blockchain-cli.jar export backup_$(date +%Y%m%d).json

# 8. Verify the backup was created correctly
java -jar blockchain-cli.jar validate --json
```

### Advanced Security Workflows

#### Enterprise Production Workflow
```zsh
# Setup secure keys for department heads
java -jar blockchain-cli.jar add-key "ChiefFinancialOfficer" --generate --store-private
java -jar blockchain-cli.jar add-key "TechnicalDirector" --generate --store-private
java -jar blockchain-cli.jar add-key "ComplianceOfficer" --generate --store-private

# Verify stored keys
java -jar blockchain-cli.jar manage-keys --list

# Sign important transactions with stored keys
java -jar blockchain-cli.jar add-block "Q4 Budget Approved: $2.5M" --signer ChiefFinancialOfficer
java -jar blockchain-cli.jar add-block "Security Audit Completed" --signer ComplianceOfficer
java -jar blockchain-cli.jar add-block "System Architecture Updated" --signer TechnicalDirector
```

#### Migration from Demo to Production
```zsh
# Current demo users
java -jar blockchain-cli.jar list-keys

# Upgrade existing user to production
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
# Note: This creates a new secure key for Alice while preserving her authorization

# Test the upgraded user
java -jar blockchain-cli.jar manage-keys --check Alice
java -jar blockchain-cli.jar add-block "First production transaction" --signer Alice
```

#### Mixed Environment (Demo + Production)
```zsh
# Production users with stored keys
java -jar blockchain-cli.jar add-key "ProductionManager" --generate --store-private

# Demo users for testing
java -jar blockchain-cli.jar add-key "TestUser1" --generate
java -jar blockchain-cli.jar add-key "TestUser2" --generate

# Production signing (requires password)
java -jar blockchain-cli.jar add-block "Production data" --signer ProductionManager

# Demo signing (temporary keys)
java -jar blockchain-cli.jar add-block "Test data" --signer TestUser1
```

## 📖 Commands

### Global Options

| Option | Short | Description |
|--------|-------|-------------|
| `--help` | `-h` | Show help message and exit |
| `--version` | `-V` | Display version information |
| `--verbose` | `-v` | Enable verbose output with detailed information |

> **Note:** The `--verbose` option is available globally and for specific commands. It provides detailed information about operations, key loading, format detection, and other internal processes. This is especially useful for troubleshooting and understanding the blockchain operations.

### Available Commands

#### `status` - Show Blockchain Status ✅

Display current blockchain statistics and health information.

```zsh
# Basic status
java -jar blockchain-cli.jar status

# JSON format output (perfect for scripts)
java -jar blockchain-cli.jar status --json

# Detailed information with configuration
java -jar blockchain-cli.jar status --detailed

# Verbose status (shows detailed operation information)
java -jar blockchain-cli.jar status --verbose

# Detailed status with verbose output
java -jar blockchain-cli.jar status --detailed --verbose
```

#### `validate` - Validate Blockchain ✅

Check the integrity of the entire blockchain with comprehensive validation.

```zsh
# Full validation
java -jar blockchain-cli.jar validate

# Quick validation (faster for large chains)
java -jar blockchain-cli.jar validate --quick

# JSON output for automation
java -jar blockchain-cli.jar validate --json

# Detailed validation (shows comprehensive block-by-block validation results)
java -jar blockchain-cli.jar validate --detailed

# Detailed validation with JSON output
java -jar blockchain-cli.jar validate --detailed --json

# Verbose validation (shows detailed operation information)
java -jar blockchain-cli.jar validate --verbose

# Detailed validation with verbose output
java -jar blockchain-cli.jar validate --detailed --verbose
```

> **Note:** The `--detailed` option provides comprehensive validation information including signature verification, timestamp validation, and key authorization checks for each block. The `--verbose` option shows detailed operation information during the validation process. Both options are particularly useful for debugging and auditing the blockchain.

#### `add-key` - Add Authorized Key ✅

Add authorized keys to the blockchain for signing blocks.

```zsh
# Generate new key pair automatically
java -jar blockchain-cli.jar add-key "Alice" --generate

# Generate and show private key (keep secure!)
java -jar blockchain-cli.jar add-key "Bob" --generate --show-private
```

#### `list-keys` - List Authorized Keys ✅

List all authorized keys in the blockchain.

```zsh
# Basic listing
java -jar blockchain-cli.jar list-keys

# Detailed information with creation dates
java -jar blockchain-cli.jar list-keys --detailed

# JSON output for processing
java -jar blockchain-cli.jar list-keys --json
```

#### `manage-keys` - Manage Private Keys ✅ **NEW**

Manage securely stored private keys for production signing.

```zsh
# List all stored private keys
java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
🔑 Alice
🔑 Manager
📊 Total: 2 stored private key(s)

# Check if a specific user has stored private key
java -jar blockchain-cli.jar manage-keys --check Alice
✅ Private key is stored for: Alice

# Test password for a stored private key
java -jar blockchain-cli.jar manage-keys --test Alice
🔐 Enter password for Alice: [hidden]
✅ Password is correct for: Alice

# Delete a stored private key (with confirmation)
java -jar blockchain-cli.jar manage-keys --delete Alice
⚠️  Are you sure you want to delete the private key for 'Alice'? (yes/no): yes
🗑️  Private key deleted for: Alice

# JSON output for automation
java -jar blockchain-cli.jar manage-keys --list --json
```

**🔐 Private Key Security Features:**
- ✅ **AES-128 encryption** protects stored keys
- ✅ **Password validation** ensures strong passwords
- ✅ **Secure input** hides passwords during entry
- ✅ **Confirmation prompts** prevent accidental deletion

#### `add-block` - Add New Block ✅

Add a new block to the blockchain. Requires an authorized key for signing.

```zsh
# Method 1: Use existing authorized signer (RECOMMENDED)
java -jar blockchain-cli.jar add-block "Transaction data" --signer Alice

# Method 2: Generate new key automatically
java -jar blockchain-cli.jar add-block "System update" --generate-key

# Method 3: Load from key file (fully implemented)
java -jar blockchain-cli.jar add-block "Secure data" --key-file alice.pem

# NEW Method 4: Read content from external file
echo "Patient medical record data" > record.txt
java -jar blockchain-cli.jar add-block --file record.txt --generate-key

# File input with keywords and categories
java -jar blockchain-cli.jar add-block --file large-report.txt \
    --keywords "REPORT,Q1-2024,FINANCE" \
    --category "FINANCE" \
    --signer Manager
```

**📄 Input Methods:**

| Option | Description | Example |
|--------|-------------|---------|
| Direct input | Provide data as command argument | `add-block "My data" --generate-key` |
| `--file <path>` | Read content from external file | `add-block --file report.txt --generate-key` |

**🔑 Signing Methods Explained:**

| Method | When to Use | Security Level | Best For |
|--------|-------------|----------------|----------|
| `--signer <name>` | User already exists | ⭐⭐⭐ Demo | Multi-user workflows |
| `--generate-key` | Quick testing | ⭐⭐ Medium | Development/testing |
| `--key-file <path>` | Production use | ⭐⭐⭐⭐ High | Enterprise deployments |

**📋 Additional Options:**

| Option | Short | Description | Example |
|--------|-------|-------------|---------|
| `--file` | `-f` | Read block content from file | `--file report.txt` |
| `--keywords` | `-w` | Manual keywords (comma-separated) | `--keywords "MEDICAL,PAT-001"` |
| `--category` | `-c` | Content category | `--category FINANCE` |
| `--json` | `-j` | Output result in JSON format | `--json` |
| `--verbose` | `-v` | Enable verbose output | `--verbose` |

**🔐 Key File Support Details:**

| Format | Description | Example File | Notes |
|--------|-------------|--------------|-------|
| PEM PKCS#8 | Text-based format (BEGIN/END PRIVATE KEY) | `key.pem` | Recommended format |
| DER Binary | Binary format | `key.der` | Compact binary format |
| Base64 | Raw base64-encoded key | `key.b64` | Simple text format |

> **Note:** The `--key-file` option automatically detects the format, derives the public key, and auto-authorizes the key if needed. Use `--verbose` for detailed information about key loading and format detection.

**Common Use Cases:**

```zsh
# Corporate environment - multiple signers
java -jar blockchain-cli.jar add-block "Monthly report submitted" --signer Manager
java -jar blockchain-cli.jar add-block "Budget approved" --signer CFO
java -jar blockchain-cli.jar add-block "Project milestone reached" --signer Developer

# Single user - quick operations
java -jar blockchain-cli.jar add-block "Quick note" --generate-key

# Batch operations with same signer
java -jar blockchain-cli.jar add-block "Transaction #001" --signer Alice
java -jar blockchain-cli.jar add-block "Transaction #002" --signer Alice
java -jar blockchain-cli.jar add-block "Transaction #003" --signer Alice

# Using key files with verbose output
java -jar blockchain-cli.jar add-block "Secure transaction" --key-file keys/private_key.pem --verbose

# Using different key file formats
java -jar blockchain-cli.jar add-block "PEM format" --key-file keys/key.pem
java -jar blockchain-cli.jar add-block "DER format" --key-file keys/key.der
java -jar blockchain-cli.jar add-block "Base64 format" --key-file keys/key.b64

# File input examples
echo "Daily report content" > daily-report.txt
java -jar blockchain-cli.jar add-block --file daily-report.txt --generate-key

# Large file with automatic off-chain storage
java -jar blockchain-cli.jar add-block --file quarterly-financials.json \
    --keywords "Q1-2024,FINANCE,QUARTERLY" \
    --category "FINANCE" \
    --signer CFO

# Batch processing files
for file in reports/*.txt; do
    java -jar blockchain-cli.jar add-block --file "$file" \
        --keywords "BATCH,$(basename "$file" .txt)" \
        --category "REPORTS" \
        --generate-key
done
```

**Error Handling:**

```zsh
# Error: Signer doesn't exist
$ java -jar blockchain-cli.jar add-block "Test" --signer UnknownUser
❌ Error: Signer 'UnknownUser' not found in authorized keys
❌ Error: Use 'blockchain list-keys' to see available signers

# Error: No signing method specified
$ java -jar blockchain-cli.jar add-block "Test data"
❌ Error: No signing method specified
❌ Error: Use one of the following options:
❌ Error:   --generate-key: Generate a new key pair
❌ Error:   --signer <name>: Use an existing authorized key
❌ Error:   --key-file <path>: Load private key from file

# Error: File doesn't exist
$ java -jar blockchain-cli.jar add-block --file missing.txt --generate-key
❌ Failed to read input file: Input file does not exist: missing.txt

# Error: Both file and direct data specified
$ java -jar blockchain-cli.jar add-block "data" --file report.txt --generate-key
❌ Failed to add block: Runtime error - Cannot specify both file input (-f/--file) and direct data input. Please use only one method.

# Error: No input specified
$ java -jar blockchain-cli.jar add-block --generate-key
❌ Failed to add block: Runtime error - Must specify either block data directly or use --file option to read from file.
```

#### `export` - Export Blockchain ✅

Export blockchain data to a file for backup or migration.

```zsh
# Basic export
java -jar blockchain-cli.jar export backup.json

# Export with automatic overwrite
java -jar blockchain-cli.jar export backup.json --overwrite
```

#### `import` - Import Blockchain ✅

Import blockchain data from a file.

```zsh
# Import with automatic backup
java -jar blockchain-cli.jar import backup.json --backup

# Dry run to check what would be imported
java -jar blockchain-cli.jar import data.json --dry-run
```

#### `search` - Search Blocks ✅

Search for blocks by various criteria with powerful filtering options.

```zsh
# Search by content (case-insensitive)
java -jar blockchain-cli.jar search "transaction"

# Search by date range
java -jar blockchain-cli.jar search --date-from 2025-01-01 --date-to 2025-01-31

# Search with result limit and JSON output
java -jar blockchain-cli.jar search "data" --limit 10 --json
```

#### `rollback` - Rollback Blockchain ✅

**⚠️ DANGEROUS OPERATION:** Remove recent blocks from the blockchain. This operation is irreversible!

```zsh
# Remove last 3 blocks (with confirmation prompt)
java -jar blockchain-cli.jar rollback --blocks 3

# Rollback to specific block (keep blocks 0-10)
java -jar blockchain-cli.jar rollback --to-block 10

# Skip confirmation prompt (USE WITH EXTREME CAUTION)
java -jar blockchain-cli.jar rollback --blocks 2 --yes

# Dry run - see what would be removed without doing it
java -jar blockchain-cli.jar rollback --blocks 5 --dry-run

# JSON output for automation
java -jar blockchain-cli.jar rollback --to-block 8 --json
```

**Safety Features:**
- Interactive confirmation prompt (unless `--yes` flag is used)
- Dry run mode to preview changes
- Cannot remove genesis block
- Detailed preview of what will be removed

#### `help` - Detailed Help ✅

Show comprehensive help information.

```zsh
java -jar blockchain-cli.jar help
```

### Understanding Output Formats

Most commands support multiple output formats:

**Text Format (Default):**
```
🔗 Blockchain Status
==================================================
📊 Total blocks: 1
👥 Authorized keys: 0
✅ Chain integrity: VALID
```

**JSON Format (`--json`):**
```json
{
  "blockCount": 1,
  "authorizedKeys": 0,
  "isValid": true,
  "timestamp": "2025-06-10T09:08:23.143417Z"
}
```

For detailed command usage, examples, and advanced scenarios, see [docs/EXAMPLES.md](docs/EXAMPLES.md).

## 🔄 Migration Guide

### Upgrading from Previous Versions

If you're upgrading from an earlier version without secure key management:

#### Step 1: Check Current State
```zsh
# See what you currently have
java -jar blockchain-cli.jar list-keys --detailed
java -jar blockchain-cli.jar status --detailed
```

#### Step 2: Backup Everything
```zsh
# Create a complete backup before upgrading workflows
java -jar blockchain-cli.jar export pre_upgrade_backup_$(date +%Y%m%d).json
```

#### Step 3: Upgrade Users to Secure Keys (Optional)
```zsh
# Existing users continue to work in demo mode
# Optionally upgrade important users to production mode:
java -jar blockchain-cli.jar add-key "ImportantUser" --generate --store-private
```

### Demo vs Production Mode Comparison

| Feature | Demo Mode | Production Mode |
|---------|-----------|-----------------|
| **Private Key Storage** | Temporary | AES-encrypted, password-protected |
| **Security Level** | ⭐⭐ Medium | ⭐⭐⭐⭐ High |
| **Best For** | Testing, Development | Production, Enterprise |
| **Password Required** | ❌ No | ✅ Yes |
| **Key Persistence** | ❌ No | ✅ Yes |
| **Audit Trail** | ⭐ Basic | ⭐⭐⭐ Complete |
| **Compliance Ready** | ❌ No | ✅ Yes |

### Quick Command Reference

| Task | Command |
|------|---------|
| **Check stored keys** | `manage-keys --list` |
| **Verify password** | `manage-keys --test <user>` |
| **Remove stored key** | `manage-keys --delete <user>` |
| **Production sign** | `add-block "data" --signer <user>` |
| **Demo sign** | `add-block "data" --signer <user>` (if no stored key) |
| **Quick test** | `add-block "data" --generate-key` |

## 🔨 Building from Source

### Prerequisites for Building

- Java 21+
- Maven 3.6+
- Git

### Build Steps

```zsh
# 1. Clone the repository
git clone <repository-url>
cd privateBlockchain-cli

# 2. Clean and compile
mvn clean compile

# 3. Run all tests
mvn test

# 4. Package the application
mvn package

# 5. Verify the build
ls -la target/blockchain-cli.jar
```

### Automated Build Script

Use the provided test script:

```zsh
chmod +x test-cli.sh
./test-cli.sh
```

This script will:
- Clean previous builds
- Compile the code
- Run all tests
- Package the JAR
- Test basic functionality

## 🧪 Basic Testing

### Comprehensive Test Suite

The project includes **enterprise-grade testing** with full coverage:

- **Total Tests**: 295+ individual tests across 15+ test suites
- **Command Coverage**: 100% (all commands fully tested including enhanced features)  
- **Pass Rate**: 100% (all tests passing consistently)
- **Test Categories**: Unit tests, integration tests, enhanced features, off-chain storage, hybrid search
- **Enhanced Coverage**: Off-chain storage validation, hybrid search performance, keyword processing

### Quick Test Execution

```zsh
# Run the comprehensive test script (recommended)
./test-cli.sh

# Run specific stable tests only
mvn test -Dtest="StatusCommandTest,BlockchainCLITest"

# Run tests for a specific command
mvn test -Dtest=ValidateCommandTest

# Run detailed validation tests
mvn test -Dtest=ValidateCommandDetailedTest

# Run integration tests
mvn test -Dtest=BlockchainCLIIntegrationTest

# Run shell script tests for detailed validation
./test-validate-detailed.sh

# Run enhanced features tests only
mvn test -Dtest="*Enhanced*"

# Run off-chain storage tests
mvn test -Dtest="AddBlockCommandEnhancedOffChainTest"

# Run hybrid search tests
mvn test -Dtest="SearchCommandEnhancedTest"

# Run demo classes
./run-java-demos.zsh

# Run interactive CLI demos
./run-enhanced-demos.zsh
```

### Verified Commands

All commands have been thoroughly tested and verified:

```zsh
# Core functionality ✅
java -jar blockchain-cli.jar --version              # Returns: 1.0.4
java -jar blockchain-cli.jar --help                 # Shows comprehensive help
java -jar blockchain-cli.jar status                 # Shows blockchain status
java -jar blockchain-cli.jar validate               # Full chain validation

# Key management ✅
java -jar blockchain-cli.jar add-key "Alice" --generate          
java -jar blockchain-cli.jar list-keys                          

# Block management ✅
java -jar blockchain-cli.jar add-block "Transaction data" --generate-key  

# Enhanced block management with keywords and categories ✅
java -jar blockchain-cli.jar add-block "Medical data" --keywords "PATIENT-001,ECG" --category "MEDICAL" --generate-key
java -jar blockchain-cli.jar add-block "$(cat large_file.txt)" --keywords "LARGE,DOCUMENT" --category "TECHNICAL" --generate-key

# Data operations ✅
java -jar blockchain-cli.jar export backup.json                 
java -jar blockchain-cli.jar import backup.json --backup        

# Hybrid search functionality ✅
java -jar blockchain-cli.jar search "Genesis"                   # Legacy search
java -jar blockchain-cli.jar search "PATIENT-001" --fast        # Fast keyword search
java -jar blockchain-cli.jar search "transaction" --level INCLUDE_DATA --detailed
java -jar blockchain-cli.jar search "partnership" --complete --verbose
java -jar blockchain-cli.jar search --category MEDICAL --limit 10
java -jar blockchain-cli.jar search --block-number 5 --json

# Rollback functionality ✅
java -jar blockchain-cli.jar rollback --blocks 1 --dry-run     # Test rollback
java -jar blockchain-cli.jar rollback --to-block 5 --json      # Rollback to block 5
```

### Test Performance Benchmarks

- **Startup Time**: < 3 seconds per command
- **Status Command**: < 2 seconds consistently
- **Multiple Operations**: 5 status calls in < 30 seconds
- **Memory Usage**: Stable ~50MB during testing

For comprehensive testing information, see [docs/ROLLBACK_TESTING.md](docs/ROLLBACK_TESTING.md) for detailed rollback testing procedures.

### Rollback Testing Suite

The rollback functionality includes comprehensive testing tools:

```zsh
# Run complete rollback test suite
./run-rollback-tests.sh

# Setup test data for rollback testing
./test-rollback-setup.sh

# Interactive testing menu
./test-rollback-interactive.sh

# Exhaustive automated testing
./test-rollback-exhaustive.sh

# Unit tests only
mvn test -Dtest=RollbackCommandTest
```

**Test Coverage:**
- ✅ Parameter validation (edge cases, invalid inputs)
- ✅ Dry run functionality (preview without changes)
- ✅ Actual rollback operations (blocks and target-block modes)
- ✅ Error handling (boundary conditions, database errors)
- ✅ Output formats (text and JSON)
- ✅ Blockchain integrity validation after rollback
- ✅ Performance benchmarks and stress testing

**Safety Features:**
- Comprehensive backup/restore procedures
- Confirmation prompts for destructive operations
- Dry-run mode for safe preview
- Full integrity validation after operations

## 🚀 Enhanced Features

### 💾 Off-Chain Storage

The CLI automatically handles large data with seamless off-chain storage:

- **Automatic Detection**: Data >512KB automatically stored off-chain
- **AES-128-CBC Encryption**: All off-chain data is encrypted
- **Transparent Access**: API remains consistent regardless of storage location
- **Integrity Validation**: Complete hash verification and signature validation
- **Performance**: Large files don't impact blockchain performance

```zsh
# Small data stays on-chain
java -jar blockchain-cli.jar add-block "Small record" --generate-key

# Large data automatically goes off-chain
java -jar blockchain-cli.jar add-block "$(cat large_document.txt)" \
    --keywords "DOCUMENT,LARGE" \
    --category "TECHNICAL" \
    --generate-key
📊 Large data detected (1.2 MB). Will store off-chain.
🔐 Encrypting data with AES-128-CBC...
💾 Data stored off-chain. Block contains reference: OFF_CHAIN_REF:abc123...
```

### 🔍 Hybrid Search System

Multi-level search capabilities optimized for different performance needs:

#### Search Levels
- **FAST_ONLY**: Keywords only (~10-20ms) - Best for quick lookups
- **INCLUDE_DATA**: Keywords + block data (~30-60ms) - Balanced approach
- **EXHAUSTIVE_OFFCHAIN**: All content including off-chain files (~200-500ms) - Complete search

```zsh
# Fast search for quick results
java -jar blockchain-cli.jar search "PATIENT-001" --fast

# Balanced search (default)
java -jar blockchain-cli.jar search "transaction" --level INCLUDE_DATA

# Complete search including off-chain content
java -jar blockchain-cli.jar search "partnership" --complete --detailed

# Category and advanced filtering
java -jar blockchain-cli.jar search --category MEDICAL --limit 10
java -jar blockchain-cli.jar search --date-from 2024-01-01 --json
```

### 🏷️ Keywords & Categories

Automatic and manual content organization:

#### Automatic Keyword Extraction
- **Universal Elements**: Dates, numbers, emails, URLs, codes
- **Language Independent**: Works across different languages
- **Smart Processing**: Filters common stop words

#### Manual Keywords & Categories
- **Comma-separated Keywords**: Easy specification with automatic trimming
- **Content Categories**: MEDICAL, FINANCE, TECHNICAL, LEGAL, etc.
- **Normalized Processing**: Automatic uppercase normalization for categories

```zsh
# Manual keywords and category
java -jar blockchain-cli.jar add-block "Meeting notes from 2024-01-15" \
    --keywords "MEETING,PROJECT,NOTES" \
    --category "BUSINESS" \
    --generate-key

# Automatic extraction from content
java -jar blockchain-cli.jar add-block "Contact admin@company.com for API access. Budget: 50000 EUR." \
    --category "TECHNICAL" \
    --generate-key
🤖 Auto Keywords: admin@company.com, 50000, EUR, API, 2024
```

### 🧪 Enhanced Testing & Demos

Comprehensive test suite and interactive demonstrations:

#### Test Statistics
- **Total Tests**: 295+ comprehensive tests
- **Test Suites**: 15+ specialized test suites
- **Coverage**: Enhanced features, off-chain storage, hybrid search
- **Success Rate**: 100% passing tests

#### Demo Scripts
```zsh
# Interactive CLI demonstrations
./run-enhanced-demos.zsh

# Java demo classes execution
./run-java-demos.zsh

# Complete test suite with enhanced features
./test-cli.sh
```

### 🔄 Backward Compatibility

All new features maintain complete backward compatibility:

- **Legacy Commands**: All existing commands work unchanged
- **Default Behavior**: Smart defaults for new features
- **Gradual Adoption**: Optional use of new features
- **Migration Path**: Easy upgrade from basic to enhanced usage

```zsh
# These legacy commands continue to work exactly as before
java -jar blockchain-cli.jar add-block "Data" --generate-key
java -jar blockchain-cli.jar search "Genesis"
java -jar blockchain-cli.jar status
java -jar blockchain-cli.jar validate
```

## 🔧 Technical Details

### Architecture

- **Framework**: PicoCLI 4.7.5 for command-line interface
- **Database**: SQLite with Hibernate ORM 6.2.7.Final
- **Java Version**: 21 (using modern features)
- **Build Tool**: Maven 3.9.10
- **Testing**: JUnit 5.10.1

### Dependencies

#### Core Dependencies
- **Private Blockchain Core**: Custom blockchain implementation
- **Hibernate ORM**: Database persistence layer
- **SQLite JDBC**: Database driver
- **Jackson**: JSON processing
- **SLF4J**: Logging framework

#### Build Configuration
- **Maven Shade Plugin**: Creates self-contained JAR
- **Maven Compiler Plugin**: Java 21 compilation
- **Maven Surefire Plugin**: Test execution

### Performance Characteristics

- **JAR Size**: 32MB (includes all dependencies)
- **Memory Usage**: ~50MB typical, scales with blockchain size
- **Startup Time**: ~2-3 seconds for most commands
- **Database**: SQLite file-based storage
- **Concurrency**: Single-user design (file locking)

### Security Features

- **Cryptographic Keys**: RSA key generation and management
- **Block Signing**: Digital signatures for integrity
- **Validation**: Comprehensive chain validation
- **Input Validation**: All user inputs are validated
- **Error Handling**: Secure error messages (no sensitive data leaks)

### New Security Architecture

#### Secure Key Storage
- **Encryption**: AES-128 with password-derived keys
- **Storage Location**: `private-keys/` directory (created automatically)
- **Password Validation**: Enforced strong password requirements
- **Key Format**: PKCS#8 encrypted private keys

#### Dual Operation Modes
- **Demo Mode**: Temporary keys for testing and development
- **Production Mode**: Encrypted stored keys for enterprise use
- **Automatic Detection**: CLI automatically detects available modes
- **Seamless Migration**: Easy upgrade path from demo to production

#### Enhanced Security Features
- **Secure Input**: Password masking during entry
- **Key Verification**: Built-in key integrity checks
- **Audit Logging**: Track key operations and usage
- **Safe Defaults**: Secure-by-default configuration

## 📚 Documentation

This project includes comprehensive documentation for different use cases:

### 📖 User Guides
- **[docs/EXAMPLES.md](docs/EXAMPLES.md)** - Comprehensive examples including enhanced features **UPDATED**
- **[docs/DEMO_SCRIPTS.md](docs/DEMO_SCRIPTS.md)** - Interactive demo scripts guide **NEW**
- **[docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Complete troubleshooting guide
- **[docs/DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md)** - Docker deployment and usage
- **[docs/TEST-SCRIPTS.md](docs/TEST-SCRIPTS.md)** - Test scripts documentation including enhanced tests **UPDATED**

### 🔐 Security & Key Management
- **[docs/SECURE_KEY_MANAGEMENT.md](docs/SECURE_KEY_MANAGEMENT.md)** - Complete secure key management guide **NEW**
- **[docs/SIGNER_TROUBLESHOOTING.md](docs/SIGNER_TROUBLESHOOTING.md)** - Troubleshooting --signer issues **NEW**
- **[docs/KEY_FILE_IMPLEMENTATION.md](docs/KEY_FILE_IMPLEMENTATION.md)** - External key file support guide **NEW**
- **[docs/VERBOSE_OPTION.md](docs/VERBOSE_OPTION.md)** - Detailed verbose logging guide **NEW**
- **[docs/PRACTICAL_EXAMPLES.md](docs/PRACTICAL_EXAMPLES.md)** - Real-world usage examples **NEW**

### 🧪 Testing & Validation
- **[../privateBlockchain/docs/SECURITY_CLASSES_GUIDE.md](../privateBlockchain/docs/SECURITY_CLASSES_GUIDE.md)** - Guía de clases de seguridad (migradas al core) **UPDATED**
- **[docs/VALIDATION_SUMMARY.md](docs/VALIDATION_SUMMARY.md)** - Validation procedures summary **NEW**
- **[docs/ROLLBACK_TESTING.md](docs/ROLLBACK_TESTING.md)** - Rollback functionality testing guide **NEW**
- **[docs/SCRIPT_REFERENCE.md](docs/SCRIPT_REFERENCE.md)** - Comprehensive script reference **NEW**

### 🏢 Enterprise & Advanced Usage
- **[docs/ENTERPRISE_GUIDE.md](docs/ENTERPRISE_GUIDE.md)** - Enterprise deployment and best practices
- **[docs/AUTOMATION_SCRIPTS.md](docs/AUTOMATION_SCRIPTS.md)** - Production-ready automation scripts
- **[docs/INTEGRATION_PATTERNS.md](docs/INTEGRATION_PATTERNS.md)** - Integration with external systems

### 🚀 Quick Access Links

| What you want to do | Go to |
|---------------------|-------|
| See examples including enhanced features | [docs/EXAMPLES.md](docs/EXAMPLES.md) |
| Run interactive demos | [docs/DEMO_SCRIPTS.md](docs/DEMO_SCRIPTS.md) |
| Use off-chain storage and hybrid search | [docs/EXAMPLES.md](docs/EXAMPLES.md#-off-chain-storage-examples) |
| Deploy with Docker | [docs/DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md) |
| Set up for enterprise use | [docs/ENTERPRISE_GUIDE.md](docs/ENTERPRISE_GUIDE.md) |
| Automate operations | [docs/AUTOMATION_SCRIPTS.md](docs/AUTOMATION_SCRIPTS.md) |
| Fix issues or errors | [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) |
| Integrate with other systems | [docs/INTEGRATION_PATTERNS.md](docs/INTEGRATION_PATTERNS.md) |
| Test rollback functionality | [docs/ROLLBACK_TESTING.md](docs/ROLLBACK_TESTING.md) |
| Use external key files | [docs/KEY_FILE_IMPLEMENTATION.md](docs/KEY_FILE_IMPLEMENTATION.md) |
| Use verbose logging | [docs/VERBOSE_OPTION.md](docs/VERBOSE_OPTION.md) |
| Find script documentation | [docs/SCRIPT_REFERENCE.md](docs/SCRIPT_REFERENCE.md) |

---

### 🔗 Project Structure

```
privateBlockchain-cli/
├── README.md                    # This file - main overview
├── docs/
│   ├── DEMO_SCRIPTS.md           # Demo scripts documentation (NEW)
│   ├── EXAMPLES.md               # Detailed examples and use cases (UPDATED)
│   ├── TROUBLESHOOTING.md        # Complete troubleshooting guide
│   ├── DOCKER_GUIDE.md           # Docker deployment guide
│   ├── ENTERPRISE_GUIDE.md       # Enterprise usage guide
│   ├── AUTOMATION_SCRIPTS.md     # Production automation scripts
│   ├── INTEGRATION_PATTERNS.md   # External system integration
│   ├── ROLLBACK_TESTING.md       # Rollback functionality testing guide
│   ├── KEY_FILE_IMPLEMENTATION.md # External key file support guide
│   ├── SECURE_KEY_MANAGEMENT.md  # Secure key management guide
│   ├── SIGNER_TROUBLESHOOTING.md # Signer troubleshooting guide
│   ├── VERBOSE_OPTION.md         # Verbose logging guide
│   ├── PRACTICAL_EXAMPLES.md     # Real-world usage examples
│   ├── VALIDATION_SUMMARY.md     # Validation procedures summary
│   ├── SCRIPT_REFERENCE.md       # Comprehensive script reference
│   ├── TEST-SCRIPTS.md           # Test scripts documentation
│   └── ENHANCED_FEATURES.md      # Enhanced features overview
├── src/
│   ├── main/java/.../demos/     # Demo classes (NEW)
│   │   ├── OffChainStorageDemo.java
│   │   └── HybridSearchDemo.java
│   └── test/                    # Enhanced test suites (UPDATED)
├── lib/
│   ├── enhanced-features-tests.sh # Enhanced features test module (NEW)
│   ├── functional-tests.sh       # Functional tests module
│   ├── additional-tests.sh       # Additional tests module
│   ├── secure-integration.sh     # Security tests module
│   ├── rollback-tests.sh         # Rollback tests module
│   └── common-functions.sh       # Shared functions library
├── target/                      # Build output
├── blockchain.db                # SQLite database (created automatically)
├── off-chain-data/              # Off-chain encrypted data files (NEW)
├── pom.xml                      # Maven configuration
├── Dockerfile                   # Docker build configuration
├── docker-compose.yml           # Docker Compose setup
├── build-docker.sh              # Docker build script
├── run-docker.sh                # Docker run script
├── clean-database.sh            # Database cleanup utility
├── generate-test-keys.sh        # Test key generator
├── test-cli.sh                  # Comprehensive test script (UPDATED)
├── test-demo-script.zsh         # Demo script tester (NEW)
├── test-simple-demos.zsh        # Simple demo tester (NEW)
├── test-validate-detailed.sh    # Detailed validation tests
├── test-key-file-functionality.sh # Key file functionality tests
├── test-rollback-setup.sh       # Rollback setup tests
├── test-rollback-interactive.sh # Interactive rollback tests
├── run-rollback-tests.sh        # Rollback test runner
├── test-build-config.sh         # Build configuration tests
├── run-enhanced-demos.zsh       # Interactive CLI demos (NEW)
└── run-java-demos.zsh           # Java demo classes runner (NEW)
```

For the most up-to-date information and detailed documentation, please refer to the specific guide files listed above.

## 💡 Quick Tips & Best Practices

### Essential Tips for New Users
- **Always validate** after major operations: `java -jar blockchain-cli.jar validate`
- **Use JSON output** for scripts and automation: `--json` flag
- **Create regular backups** before important operations: `export backup.json`
- **Check status first** when troubleshooting: `status --detailed`

### Performance Tips
- Use `--quick` flag for faster validation on large chains
- Enable `--verbose` only when debugging (impacts performance)
- Consider using Docker for consistent environments across systems

### Security Best Practices
- **Keep private keys secure** - never share them
- **Use meaningful signer names** for audit trails
- **Validate regularly** to ensure chain integrity
- **Test imports** with `--dry-run` before applying

### Production Recommendations
- Set up automated daily backups (see [docs/AUTOMATION_SCRIPTS.md](docs/AUTOMATION_SCRIPTS.md))
- Monitor chain health with regular validation
- Use dedicated service accounts for automated operations
- Keep comprehensive logs for compliance and debugging

---

## 🤝 Support & Contributing

### Getting Help
- 📖 Check [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) for common issues
- 💬 Review [docs/EXAMPLES.md](docs/EXAMPLES.md) for usage patterns
- 🔐 See [docs/SECURE_KEY_MANAGEMENT.md](docs/SECURE_KEY_MANAGEMENT.md) for production security **NEW**
- 🔧 Check [docs/SIGNER_TROUBLESHOOTING.md](docs/SIGNER_TROUBLESHOOTING.md) for --signer issues
- 🏢 See [docs/ENTERPRISE_GUIDE.md](docs/ENTERPRISE_GUIDE.md) for advanced setups

### Project Information
- **Version**: 1.0.4
- **Java Compatibility**: 21+
- **License**: MIT License - see [LICENSE](LICENSE) file for details
- **Build Status**: All tests passing ✅

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### What this means:
- ✅ **Commercial use** - Use in commercial projects
- ✅ **Modification** - Modify and create derivative works
- ✅ **Distribution** - Distribute original or modified versions
- ✅ **Private use** - Use privately without sharing source
- ✅ **No warranty** - Software provided "as is"

---

*For enterprise support, custom integrations, or advanced use cases, refer to the comprehensive guides in this documentation suite.*
