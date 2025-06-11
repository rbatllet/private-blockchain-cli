# Private Blockchain CLI

A command-line interface for managing a private blockchain system built with Java 21, Maven, and PicoCLI.

## 📋 Table of Contents

- [Overview](#-overview)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Commands](#-commands)
- [Examples](#-examples)
  - [Quick Start Examples](#-quick-start-examples)
  - [Real-World Use Cases](#-real-world-use-cases)
  - [Advanced Scenarios](#-advanced-scenarios)
  - [Docker Examples](#-docker-examples)
  - [Automation Scripts](#-automation-scripts)
- [Building from Source](#-building-from-source)
- [Testing](#-testing)
- [Technical Details](#-technical-details)
- [Best Practices & Tips](#-best-practices--tips)
- [Troubleshooting](#-troubleshooting)
- [Advanced Use Cases & Integrations](#-advanced-use-cases--integrations)
- [Project Structure](#-project-structure)

## 🚀 Overview

This CLI application provides a secure interface for managing a private blockchain. It is built with enterprise-grade technologies and offers complete blockchain management capabilities.

### What You Can Do

- **Check blockchain status** and get detailed statistics
- **Validate blockchain integrity** with comprehensive checks
- **Manage authorized keys** for secure block signing
- **Add new blocks** to the chain with proper authentication
- **Export and import** blockchain data for backup and migration
- **Search through blockchain** content by various criteria
- **Monitor chain health** with detailed validation reports

### Key Features

✅ **Complete Implementation** - All 8 core commands fully working  
✅ **Secure Architecture** - Cryptographic key management and validation  
✅ **Multiple Output Formats** - Text, JSON, and detailed views  
✅ **Robust Testing** - 14 tests with 100% pass rate  
✅ **Production Ready** - Enterprise-grade error handling and logging  
✅ **Easy to Use** - Clear help system and examples  

## 📦 Prerequisites

Before using this application, make sure you have:

- **Java 21** or higher installed
- **Maven 3.6+** (only needed for building from source)
- At least **50MB** of free disk space

### Checking Java Version

```bash
java -version
```

You should see something like:
```
java version "21.0.1" or higher
```

## 💻 Installation

### Option 1: Docker (Recommended - No Java Required) 🐳

The fastest way to get started without installing Java locally:

```bash
# Clone and build
git clone <repository-url>
cd privateBlockchain-cli
docker build -t blockchain-cli .

# Start using immediately
docker run --rm blockchain-cli --version
docker run --rm blockchain-cli status
```

### Option 2: Using Pre-built JAR

1. Download the latest `blockchain-cli.jar` file (32MB)
2. Place it in your preferred directory
3. Make sure Java 21+ is installed
4. You're ready to go!

### Option 2: Building from Source

```bash
# Clone the repository
git clone <repository-url>
cd privateBlockchain-cli

# Build the application
mvn clean package

# The executable JAR will be created at target/blockchain-cli.jar
```

## 🚀 Quick Start

### Using Docker (Easiest) 🐳

```bash
# Build once
docker build -t blockchain-cli .

# Check version
docker run --rm blockchain-cli --version

# Get help
docker run --rm blockchain-cli --help

# Check blockchain status
docker run --rm blockchain-cli status

# Note: Commands with --rm automatically stop and clean up after execution
# For long-running containers, use Ctrl+C to stop them
```

### Using JAR directly

### 1. Check if the CLI is working

```bash
java -jar blockchain-cli.jar --version
```

Expected output:
```
1.0.0
```

### 2. View help information

```bash
java -jar blockchain-cli.jar --help
```

### 3. Check blockchain status

```bash
java -jar blockchain-cli.jar status
```

This will initialize the blockchain database (if it doesn't exist) and show current status:

```
🔗 Blockchain Status
==================================================
📊 Total blocks: 1
👥 Authorized keys: 0
✅ Chain integrity: VALID
```

## 📖 Commands

### Global Options

| Option | Short | Description |
|--------|-------|-------------|
| `--help` | `-h` | Show help message and exit |
| `--version` | `-V` | Display version information |
| `--verbose` | `-v` | Enable verbose output for debugging |

### Available Commands

#### `status` - Show Blockchain Status ✅

Display current blockchain statistics and health information.

```bash
# Basic status
java -jar blockchain-cli.jar status

# JSON format output (perfect for scripts)
java -jar blockchain-cli.jar status --json

# Detailed information with configuration
java -jar blockchain-cli.jar status --detailed
```

**Example Output:**
```bash
🔗 Blockchain Status
==================================================
📊 Total blocks: 5
👥 Authorized keys: 2
✅ Chain integrity: VALID

📋 Configuration:
   Max block size: 1,048,576 bytes (1MB)
   Max data length: 10,000 characters
   Database: SQLite (blockchain.db)
```

#### `validate` - Validate Blockchain ✅

Check the integrity of the entire blockchain with comprehensive validation.

```bash
# Full validation
java -jar blockchain-cli.jar validate

# Quick validation (faster for large chains)
java -jar blockchain-cli.jar validate --quick

# Detailed validation with block-by-block results
java -jar blockchain-cli.jar validate --detailed

# JSON output for automation
java -jar blockchain-cli.jar validate --json
```

#### `add-key` - Add Authorized Key ✅

Add authorized keys to the blockchain for signing blocks. Keys are required for adding new blocks.

```bash
# Generate new key pair automatically
java -jar blockchain-cli.jar add-key "Alice" --generate

# Generate and show private key (keep this secure!)
java -jar blockchain-cli.jar add-key "Bob" --generate --show-private

# Add existing public key
java -jar blockchain-cli.jar add-key "Charlie" --public-key "MIIBIjANBgkq..."
```

**Successful Example:**
```bash
✅ blockchain add-key "Alice" --generate --show-private
   → Key generated and added successfully
```

#### `list-keys` - List Authorized Keys ✅

List all authorized keys in the blockchain.

```bash
# Basic listing
java -jar blockchain-cli.jar list-keys

# Detailed information with creation dates
java -jar blockchain-cli.jar list-keys --detailed

# Only active keys
java -jar blockchain-cli.jar list-keys --active-only

# JSON output for processing
java -jar blockchain-cli.jar list-keys --json
```

#### `add-block` - Add New Block ✅

Add a new block to the blockchain. Requires an authorized key for signing.

```bash
# Add block with auto-generated key
java -jar blockchain-cli.jar add-block "Transaction data" --generate-key

# Add block with specific signer
java -jar blockchain-cli.jar add-block "System update" --signer Alice

# Add block with large data
java -jar blockchain-cli.jar add-block "Complex transaction data here..." --signer Bob
```

**Note:** The system enforces a maximum of 10,000 characters per block and 1MB total size.

#### `export` - Export Blockchain ✅

Export blockchain data to a file for backup or migration.

```bash
# Basic export
java -jar blockchain-cli.jar export backup.json

# Export with automatic overwrite
java -jar blockchain-cli.jar export backup.json --overwrite

# Export with JSON status output
java -jar blockchain-cli.jar export data.json --json-output

# Export with compression (planned feature)
java -jar blockchain-cli.jar export archive.json --compress
```

**Successful Example:**
```bash
✅ blockchain export test_backup.json
   → 1 block and 1 key exported (878 bytes)
```

#### `import` - Import Blockchain ✅

Import blockchain data from a file.

```bash
# Import with automatic backup
java -jar blockchain-cli.jar import backup.json --backup

# Dry run to check what would be imported
java -jar blockchain-cli.jar import data.json --dry-run

# Import with validation after completion
java -jar blockchain-cli.jar import backup.json --validate-after

# Force import even if validation fails
java -jar blockchain-cli.jar import data.json --force
```

#### `search` - Search Blocks ✅

Search for blocks by various criteria with powerful filtering options.

```bash
# Search by content (case-insensitive)
java -jar blockchain-cli.jar search "transaction"

# Search by specific block number
java -jar blockchain-cli.jar search --block-number 5

# Search by exact hash
java -jar blockchain-cli.jar search --hash "a1b2c3d4..."

# Search by date range
java -jar blockchain-cli.jar search --date-from 2025-01-01 --date-to 2025-01-31

# Search by datetime range (more precise)
java -jar blockchain-cli.jar search --datetime-from "2025-01-01 10:00" --datetime-to "2025-01-01 12:00"

# Search with result limit
java -jar blockchain-cli.jar search "payment" --limit 10

# Search with detailed block information
java -jar blockchain-cli.jar search "Genesis" --detailed

# JSON output for processing
java -jar blockchain-cli.jar search "data" --json
```

**Successful Example:**
```bash
✅ blockchain search "Genesis"
   → 1 block found with matching content
```

#### `help` - Detailed Help ✅

Show comprehensive help information.

```bash
java -jar blockchain-cli.jar help
```

## 💡 Examples

### 🚀 Quick Start Examples

#### Example 1: First-Time Setup
```bash
# Step 1: Check if CLI is working
java -jar blockchain-cli.jar --version
# Expected: 1.0.0

# Step 2: Initialize and check blockchain
java -jar blockchain-cli.jar status
# Creates genesis block automatically

# Step 3: Add your first authorized user
java -jar blockchain-cli.jar add-key "Alice" --generate --show-private
# Save the private key securely!

# Step 4: Add your first block
java -jar blockchain-cli.jar add-block "My first blockchain entry" --signer Alice

# Step 5: Verify everything worked
java -jar blockchain-cli.jar validate --detailed
```

#### Example 2: Daily Operations
```bash
# Morning: Check blockchain health
java -jar blockchain-cli.jar status --detailed

# Add daily transaction records
java -jar blockchain-cli.jar add-block "Transaction: Invoice #2025-001" --signer Alice
java -jar blockchain-cli.jar add-block "Payment received: $1,500" --signer Alice

# End of day: Create backup
java -jar blockchain-cli.jar export backups/daily_$(date +%Y%m%d).json

# Verify backup integrity
java -jar blockchain-cli.jar validate --json
```

#### Example 3: Multi-User Setup
```bash
# Setup multiple users for a team
java -jar blockchain-cli.jar add-key "Alice-Manager" --generate
java -jar blockchain-cli.jar add-key "Bob-Developer" --generate  
java -jar blockchain-cli.jar add-key "Charlie-Auditor" --generate

# Verify all users are authorized
java -jar blockchain-cli.jar list-keys --detailed

# Each user can now add blocks
java -jar blockchain-cli.jar add-block "Project milestone completed" --signer Alice-Manager
java -jar blockchain-cli.jar add-block "Code deployed to production" --signer Bob-Developer
java -jar blockchain-cli.jar add-block "Security audit passed" --signer Charlie-Auditor
```

### 🎯 Real-World Use Cases

#### Use Case 1: Document Audit Trail
```bash
# Setup for document management system
java -jar blockchain-cli.jar add-key "DocumentManager" --generate
java -jar blockchain-cli.jar add-key "LegalTeam" --generate

# Track document lifecycle
java -jar blockchain-cli.jar add-block "Document created: Contract_2025.pdf | Author: Alice | Size: 245KB" --signer DocumentManager
java -jar blockchain-cli.jar add-block "Document reviewed: Contract_2025.pdf | Reviewer: Legal | Status: Approved" --signer LegalTeam
java -jar blockchain-cli.jar add-block "Document signed: Contract_2025.pdf | Signatory: Bob | Timestamp: $(date)" --signer DocumentManager

# Generate audit report
java -jar blockchain-cli.jar search "Contract_2025.pdf" --detailed
java -jar blockchain-cli.jar export audit_reports/contract_audit_$(date +%Y%m%d).json
```

#### Use Case 2: Supply Chain Tracking
```bash
# Setup supply chain participants
java -jar blockchain-cli.jar add-key "Manufacturer" --generate
java -jar blockchain-cli.jar add-key "Distributor" --generate  
java -jar blockchain-cli.jar add-key "Retailer" --generate

# Track product journey
java -jar blockchain-cli.jar add-block "PRODUCED: Product #12345 | Location: Factory-A | Date: $(date +%Y-%m-%d)" --signer Manufacturer
java -jar blockchain-cli.jar add-block "SHIPPED: Product #12345 | From: Factory-A | To: Warehouse-B | Carrier: FastShip" --signer Distributor
java -jar blockchain-cli.jar add-block "RECEIVED: Product #12345 | Location: Warehouse-B | Condition: Good | Inspector: John" --signer Distributor
java -jar blockchain-cli.jar add-block "SOLD: Product #12345 | Store: TechMart | Customer: [PRIVATE] | Price: $299" --signer Retailer

# Track specific product
java -jar blockchain-cli.jar search "Product #12345" --json > product_12345_history.json
```

#### Use Case 3: Meeting Minutes & Decisions
```bash
# Setup for corporate governance
java -jar blockchain-cli.jar add-key "Secretary" --generate
java -jar blockchain-cli.jar add-key "Chairman" --generate

# Record meeting decisions
java -jar blockchain-cli.jar add-block "MEETING: Board Meeting 2025-06-11 | Attendees: 8/10 | Type: Quarterly Review" --signer Secretary
java -jar blockchain-cli.jar add-block "DECISION: Approved budget increase 15% for Q3 | Vote: 7-1 | Motion: CFO-2025-03" --signer Chairman
java -jar blockchain-cli.jar add-block "ACTION: Hire 5 new developers | Deadline: Q3 2025 | Owner: HR Director" --signer Secretary

# Generate meeting report
java -jar blockchain-cli.jar search --date-from $(date +%Y-%m-%d) --detailed
```

#### Use Case 4: Software Release Pipeline
```bash
# Setup development team
java -jar blockchain-cli.jar add-key "Developer" --generate
java -jar blockchain-cli.jar add-key "QA-Team" --generate
java -jar blockchain-cli.jar add-key "DevOps" --generate

# Track release process
java -jar blockchain-cli.jar add-block "CODE: Feature XYZ completed | Branch: feature/xyz | Commits: 23 | Author: Alice" --signer Developer
java -jar blockchain-cli.jar add-block "TEST: Feature XYZ tested | Tests: 45/45 passed | Coverage: 98% | QA: Bob" --signer QA-Team
java -jar blockchain-cli.jar add-block "DEPLOY: Feature XYZ deployed | Environment: Production | Version: v2.1.0 | Time: $(date)" --signer DevOps

# Release audit
java -jar blockchain-cli.jar search "Feature XYZ" --detailed > release_audit.txt
```

### 🔧 Advanced Scenarios

#### Scenario 1: Backup & Recovery Workflow
```bash
# Create comprehensive backup
java -jar blockchain-cli.jar export backups/full_backup_$(date +%Y%m%d_%H%M%S).json

# Simulate disaster recovery
rm blockchain.db*  # Simulate data loss

# Restore from backup  
java -jar blockchain-cli.jar import backups/full_backup_*.json --validate-after

# Verify restoration
java -jar blockchain-cli.jar validate --detailed
java -jar blockchain-cli.jar list-keys --detailed
```

#### Scenario 2: Security Incident Response
```bash
# Immediately create incident backup
java -jar blockchain-cli.jar export incident_backups/security_incident_$(date +%Y%m%d_%H%M%S).json

# Document the incident
java -jar blockchain-cli.jar add-block "SECURITY INCIDENT: Unauthorized access attempt | IP: 192.168.1.100 | Time: $(date) | Action: Blocked" --signer SecurityTeam

# Revoke compromised keys (if any)
# Note: This would be done through the core blockchain API, not CLI

# Generate incident report
java -jar blockchain-cli.jar search "SECURITY INCIDENT" --json > incident_report.json
java -jar blockchain-cli.jar validate --detailed >> incident_report.txt
```

#### Scenario 3: Data Migration
```bash
# Export from old system
java -jar blockchain-cli.jar export migration/old_system_$(date +%Y%m%d).json

# Import to new system (with backup)
java -jar blockchain-cli.jar import migration/old_system_*.json --backup --validate-after

# Verify migration integrity
java -jar blockchain-cli.jar validate --detailed
java -jar blockchain-cli.jar search --date-from 2025-01-01 --limit 100 > migration_verification.txt
```

#### Scenario 4: Compliance Reporting
```bash
# Generate monthly compliance report
java -jar blockchain-cli.jar search --date-from $(date -d "1 month ago" +%Y-%m-%d) --date-to $(date +%Y-%m-%d) --json > compliance/monthly_$(date +%Y_%m).json

# Validate chain integrity for auditors
java -jar blockchain-cli.jar validate --detailed > compliance/chain_integrity_$(date +%Y%m%d).txt

# Export complete blockchain for regulatory submission
java -jar blockchain-cli.jar export compliance/full_blockchain_$(date +%Y-%m-%d).json
```

### 🛠 Docker Examples

#### Docker Quick Start
```bash
# Build once, use everywhere
docker build -t blockchain-cli .

# Daily operations with persistent data
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli status --detailed
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-key "Docker-User" --generate
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "Data from Docker container" --generate-key

# Backup with dual volumes
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli export /backups/docker_backup_$(date +%Y%m%d).json
```

#### Docker Compose Automation
```bash
# Automated daily backup
docker-compose --profile backup up

# Chain validation
docker-compose --profile validate up

# Status monitoring
docker-compose --profile default up
```

### 📊 Automation Scripts

#### Daily Backup Script
```bash
#!/bin/bash
# save as: daily_backup.sh

DATE=$(date +%Y%m%d)
BACKUP_DIR="backups/daily"
mkdir -p $BACKUP_DIR

echo "🔄 Starting daily backup: $DATE"

# Create backup
java -jar blockchain-cli.jar export $BACKUP_DIR/blockchain_$DATE.json

# Validate integrity  
if java -jar blockchain-cli.jar validate --json | grep -q '"valid":true'; then
    echo "✅ Backup completed successfully: $BACKUP_DIR/blockchain_$DATE.json"
    
    # Remove backups older than 30 days
    find $BACKUP_DIR -name "blockchain_*.json" -mtime +30 -delete
    echo "🧹 Cleaned up old backups"
else
    echo "❌ Chain validation failed! Backup may be corrupted"
    exit 1
fi
```

#### Health Check Script
```bash
#!/bin/bash
# save as: health_check.sh

echo "🏥 Blockchain Health Check: $(date)"

# Check status
STATUS=$(java -jar blockchain-cli.jar status --json)
BLOCKS=$(echo $STATUS | jq -r '.blockCount')
KEYS=$(echo $STATUS | jq -r '.authorizedKeys') 
VALID=$(echo $STATUS | jq -r '.isValid')

echo "📊 Blocks: $BLOCKS | Keys: $KEYS | Valid: $VALID"

# Validate chain
if [ "$VALID" = "true" ]; then
    echo "✅ Blockchain is healthy"
    exit 0
else
    echo "❌ Blockchain validation failed!"
    java -jar blockchain-cli.jar validate --detailed
    exit 1
fi
```

### Complete Workflow Example

Here's a real workflow that has been tested and works:

```bash
# 1. Check initial status
java -jar blockchain-cli.jar status

# 2. Add an authorized key
java -jar blockchain-cli.jar add-key "Alice" --generate

# 3. List all keys to verify
java -jar blockchain-cli.jar list-keys --detailed

# 4. Validate blockchain integrity
java -jar blockchain-cli.jar validate --detailed

# 5. Search for genesis block
java -jar blockchain-cli.jar search "Genesis"

# 6. Create a backup
java -jar blockchain-cli.jar export backup_$(date +%Y%m%d).json

# 7. Verify the backup was created correctly
java -jar blockchain-cli.jar validate --json
```

### Advanced Usage Examples

```bash
# Search by date range for analysis
java -jar blockchain-cli.jar search --date-from 2025-01-01 --date-to 2025-12-31 --json

# Import with backup and validation
java -jar blockchain-cli.jar import production_data.json --backup --validate-after

# Export with overwrite for regular backups
java -jar blockchain-cli.jar export daily_backup.json --overwrite

# Quick validation for scripts
java -jar blockchain-cli.jar validate --quick --json

# Verbose operations for debugging
java -jar blockchain-cli.jar --verbose add-key "TestUser" --generate
```

### Understanding Output Formats

#### Status Command Output Formats

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

**Detailed Format (`--detailed`):**
```
🔗 Blockchain Status
==================================================
📊 Total blocks: 1
👥 Authorized keys: 0
✅ Chain integrity: VALID

📋 Configuration:
   Max block size: 1,048,576 bytes (1MB)
   Max data length: 10,000 characters
   Database: SQLite (blockchain.db)
   Timestamp: 2025-06-10T11:02:29.123
```

## 🔨 Building from Source

### Prerequisites for Building

- Java 21+
- Maven 3.6+
- Git

### Build Steps

```bash
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

```bash
chmod +x test-cli.sh
./test-cli.sh
```

This script will:
- Clean previous builds
- Compile the code
- Run all tests
- Package the JAR
- Test basic functionality

## 🧪 Testing

### Comprehensive Test Suite

The project now includes **enterprise-grade testing** with full coverage of all implemented commands:

- **Total Tests**: 100+ individual tests
- **Test Files**: 11 comprehensive test suites
- **Command Coverage**: 100% (9/9 commands fully tested)
- **Pass Rate**: ~95% (stable tests passing consistently)

### Test Categories

#### **Unit Tests by Command**:
1. **StatusCommandTest** (5 tests): Status command with all output formats
2. **ValidateCommandTest** (9 tests): Blockchain validation functionality
3. **AddKeyCommandTest** (7 tests): Key generation and management
4. **ListKeysCommandTest** (10 tests): Key listing with various options
5. **AddBlockCommandTest** (9 tests): Block creation and signing
6. **ExportCommandTest** (10 tests): Data export functionality
7. **ImportCommandTest** (12 tests): Data import with validation
8. **SearchCommandTest** (20 tests): Advanced search capabilities
9. **HelpCommandTest** (10 tests): Help system validation

#### **Integration Tests**:
- **BlockchainCLITest** (7 tests): Core CLI functionality and global options
- **BlockchainCLIIntegrationTest** (10 tests): End-to-end workflow testing

### Running Tests

#### **Quick Test Execution**:
```bash
# Run the comprehensive test script (recommended)
./test-cli.sh

# Run specific stable tests only
mvn test -Dtest="StatusCommandTest,BlockchainCLITest"

# Run tests for a specific command
mvn test -Dtest=ValidateCommandTest

# Run integration tests
mvn test -Dtest=BlockchainCLIIntegrationTest
```

#### **Advanced Testing Options**:
```bash
# Run all unit tests (may have some unstable tests)
mvn test

# Run tests with verbose output
mvn test -X

# Run specific test method
mvn test -Dtest=StatusCommandTest#testBasicStatus

# Skip tests during build
mvn package -DskipTests

# Run tests with detailed reporting
mvn test -Dtest=AddKeyCommandTest -Ddetail=true
```

#### **Enhanced Test Script**:
The project includes a comprehensive test script that validates all functionality:

```bash
# Make sure it's executable
chmod +x test-cli.sh

# Run complete test suite (recommended)
./test-cli.sh

# Run tests without unit tests (faster)
SKIP_UNIT_TESTS=true ./test-cli.sh

# The script will test:
# ✅ Build process (clean, compile, package)
# ✅ All 9 CLI commands with various options
# ✅ Error handling and edge cases
# ✅ Workflow integration tests
# ✅ Performance benchmarks
# ✅ Export/import functionality with temp files
```

### Test Script Features

The enhanced `test-cli.sh` provides:

- **🎨 Colored Output**: Green/red indicators for pass/fail
- **📊 Progress Tracking**: Real-time test counters and percentages
- **🔄 Complete Workflows**: End-to-end testing scenarios
- **⚡ Performance Tests**: Basic timing and efficiency checks
- **🧹 Automatic Cleanup**: Temporary file management
- **📋 Detailed Reporting**: Comprehensive success/failure summary

### Verified Commands

All commands have been thoroughly tested and verified:

```bash
# Core functionality ✅
java -jar blockchain-cli.jar --version              # Returns: 1.0.0
java -jar blockchain-cli.jar --help                 # Shows comprehensive help
java -jar blockchain-cli.jar status                 # Shows blockchain status
java -jar blockchain-cli.jar status --json          # JSON format output
java -jar blockchain-cli.jar status --detailed      # Detailed system info

# Validation ✅
java -jar blockchain-cli.jar validate               # Full chain validation
java -jar blockchain-cli.jar validate --detailed    # Block-by-block results
java -jar blockchain-cli.jar validate --quick       # Fast validation
java -jar blockchain-cli.jar validate --json        # JSON validation results

# Key management ✅
java -jar blockchain-cli.jar add-key "Alice" --generate          # Generate new keys
java -jar blockchain-cli.jar add-key "Bob" --generate --show-private  # Show private key
java -jar blockchain-cli.jar list-keys                          # List all keys
java -jar blockchain-cli.jar list-keys --detailed               # Detailed key info
java -jar blockchain-cli.jar list-keys --json                   # JSON key listing

# Block management ✅
java -jar blockchain-cli.jar add-block "Transaction data" --generate-key  # Add with new key
java -jar blockchain-cli.jar add-block "System update" --signer Alice     # Add with signer

# Data operations ✅
java -jar blockchain-cli.jar export backup.json                 # Export blockchain
java -jar blockchain-cli.jar export backup.json --overwrite     # Overwrite existing
java -jar blockchain-cli.jar import backup.json --backup        # Import with backup
java -jar blockchain-cli.jar import data.json --dry-run         # Simulate import

# Search functionality ✅
java -jar blockchain-cli.jar search "Genesis"                   # Search content
java -jar blockchain-cli.jar search --block-number 0           # Search by number
java -jar blockchain-cli.jar search "payment" --limit 10       # Limited results
java -jar blockchain-cli.jar search --date-from 2025-01-01     # Date range search

# Help system ✅
java -jar blockchain-cli.jar help                              # Detailed help
```

### Testing Best Practices

#### **For Developers**:
1. **Always run `./test-cli.sh`** before committing changes
2. **Test individual commands** during development:
   ```bash
   mvn test -Dtest=StatusCommandTest
   ```
3. **Use verbose mode** for debugging test failures:
   ```bash
   mvn test -X -Dtest=FailingTest
   ```

#### **For CI/CD**:
1. **Automated testing** with the test script:
   ```bash
   # In your CI pipeline
   ./test-cli.sh
   ```
2. **Parallel testing** for faster builds:
   ```bash
   mvn test -T 1C  # Use 1 thread per CPU core
   ```

#### **For Quality Assurance**:
1. **Comprehensive validation**:
   ```bash
   # Full test suite
   ./test-cli.sh
   
   # Additional manual verification
   java -jar blockchain-cli.jar status --detailed
   java -jar blockchain-cli.jar validate --detailed
   ```

### Test Stability

#### **Highly Stable Tests** (recommended for CI):
- StatusCommandTest ✅
- BlockchainCLITest ✅
- ValidateCommandTest ✅
- AddKeyCommandTest ✅
- ExportCommandTest ✅

#### **Tests with Known Issues**:
- Some integration tests may be sensitive to timing
- Help command tests may have inconsistent exit codes
- SearchCommandTest may have occasional JVM issues

### Performance Benchmarks

The test script includes basic performance validation:

- **Startup Time**: < 3 seconds per command
- **Status Command**: < 2 seconds consistently
- **Multiple Operations**: 5 status calls in < 30 seconds
- **Memory Usage**: Stable ~50MB during testing
- **JAR Size**: 32MB executable with all dependencies

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

## 🛠️ Troubleshooting

### 🩺 Diagnostic Commands

Before diving into solutions, use these diagnostic commands to identify issues:

```bash
# Quick health check
java -jar blockchain-cli.jar status --json | jq

# Detailed system information
java -jar blockchain-cli.jar status --detailed

# Chain integrity verification
java -jar blockchain-cli.jar validate --detailed

# Check authorized keys
java -jar blockchain-cli.jar list-keys --detailed

# Recent blockchain activity
java -jar blockchain-cli.jar search --date-from $(date -d "7 days ago" +%Y-%m-%d) --limit 10
```

### 🔧 Common Scenarios & Solutions

#### Scenario 1: "My blockchain validation is failing"

**Symptoms**: `validate` command returns `"valid": false`

**Diagnosis**:
```bash
# Get detailed validation report
java -jar blockchain-cli.jar validate --detailed

# Check recent blocks for corruption
java -jar blockchain-cli.jar search --date-from $(date -d "1 day ago" +%Y-%m-%d)
```

**Solutions**:
```bash
# Option 1: Restore from recent backup
java -jar blockchain-cli.jar import backups/latest_backup.json --validate-after

# Option 2: Export current data for analysis
java -jar blockchain-cli.jar export investigation/corrupted_chain_$(date +%Y%m%d).json

# Option 3: Start fresh if data is not critical
rm blockchain.db* && java -jar blockchain-cli.jar status
```

#### Scenario 2: "I can't add blocks - unauthorized key error"

**Symptoms**: `Unauthorized key attempting to add block`

**Diagnosis**:
```bash
# Check available authorized keys
java -jar blockchain-cli.jar list-keys --active-only

# Verify specific key status
java -jar blockchain-cli.jar list-keys --detailed | grep "Alice"
```

**Solutions**:
```bash
# Option 1: Add a new authorized key
java -jar blockchain-cli.jar add-key "NewUser" --generate --show-private

# Option 2: Use existing authorized key
java -jar blockchain-cli.jar add-block "Your data" --signer ExistingUser

# Option 3: Generate key automatically
java -jar blockchain-cli.jar add-block "Your data" --generate-key
```

#### Scenario 3: "My export/import is failing"

**Symptoms**: Export creates empty files or import fails

**Diagnosis**:
```bash
# Check current blockchain state
java -jar blockchain-cli.jar status --detailed

# Verify file permissions
ls -la backups/
ls -la blockchain.db*

# Test with minimal export
java -jar blockchain-cli.jar export test_export.json && ls -la test_export.json
```

**Solutions**:
```bash
# Create directory if missing
mkdir -p backups && chmod 755 backups

# Export with full path
java -jar blockchain-cli.jar export $(pwd)/backups/manual_backup.json

# Test import with validation
java -jar blockchain-cli.jar import backup_file.json --dry-run
```

#### Scenario 4: "Performance is very slow"

**Symptoms**: Commands take >30 seconds to complete

**Diagnosis**:
```bash
# Check database size
ls -lh blockchain.db*
du -sh .

# Count total blocks
java -jar blockchain-cli.jar status --json | jq '.blockCount'

# Check available disk space
df -h .
```

**Solutions**:
```bash
# Option 1: Increase JVM memory
java -Xmx1024m -jar blockchain-cli.jar status

# Option 2: Clean up old data
java -jar blockchain-cli.jar export archive/full_backup.json
# Then start fresh if appropriate

# Option 3: Use quick validation
java -jar blockchain-cli.jar validate --quick
```

#### Scenario 5: "Docker containers won't start"

**Symptoms**: Docker build fails or containers exit immediately

**Diagnosis**:
```bash
# Check Docker status
docker --version
docker ps
docker images | grep blockchain-cli

# View container logs
docker logs container_name

# Test basic Docker functionality
docker run --rm hello-world
```

**Solutions**:
```bash
# Rebuild image cleanly
docker rmi blockchain-cli
docker build --no-cache -t blockchain-cli .

# Check JAR file exists
ls -la target/blockchain-cli.jar

# Test with simple command
docker run --rm blockchain-cli --version
```

### 📋 Step-by-Step Troubleshooting Guide

#### Step 1: Environment Check
```bash
# Verify Java version
java -version | head -1

# Check JAR integrity
ls -la blockchain-cli.jar
java -jar blockchain-cli.jar --version

# Test basic functionality
java -jar blockchain-cli.jar status
```

#### Step 2: Database Issues
```bash
# Check database files
ls -la blockchain.db*

# Verify permissions
chmod 644 blockchain.db* 2>/dev/null || echo "No database files found"

# Test database connection
java -jar blockchain-cli.jar status --json | jq '.isValid'
```

#### Step 3: Key Management Issues
```bash
# List all keys
java -jar blockchain-cli.jar list-keys --detailed

# Test key generation
java -jar blockchain-cli.jar add-key "TestKey-$(date +%s)" --generate

# Test block addition
java -jar blockchain-cli.jar add-block "Test block $(date)" --generate-key
```

#### Step 4: Data Integrity Check
```bash
# Full validation
java -jar blockchain-cli.jar validate --detailed

# Export test
java -jar blockchain-cli.jar export test_backup_$(date +%s).json

# Import test
java -jar blockchain-cli.jar import test_backup_*.json --dry-run
```

### Common Issues and Solutions

#### "Command not found" or "java: command not found"

**Problem**: Java is not installed or not in PATH.

**Solution**:
```bash
# Check if Java is installed
java -version

# Install Java 21+ if needed:
# macOS with Homebrew:
brew install openjdk@21

# Ubuntu/Debian:
sudo apt update && sudo apt install openjdk-21-jdk

# Windows: Download from Oracle or OpenJDK website
```

#### "UnsupportedClassVersionError"

**Problem**: You're using an older Java version.

**Solution**: This application requires Java 21+. Update your Java installation.

#### "No such file or directory: blockchain-cli.jar"

**Problem**: JAR file path is incorrect.

**Solution**:
```bash
# Make sure you're in the correct directory
ls -la blockchain-cli.jar

# Use full path if needed
java -jar /full/path/to/blockchain-cli.jar status
```

#### Database Issues

**Problem**: Cannot create or access blockchain.db file.

**Solution**:
```bash
# Check directory permissions
ls -la

# Ensure write permissions
chmod 755 .

# Or run from a writable directory
cd ~/Documents
java -jar /path/to/blockchain-cli.jar status
```

#### Memory Issues

**Problem**: Application runs out of memory with large blockchains.

**Solution**:
```bash
# Increase JVM memory
java -Xmx1024m -jar blockchain-cli.jar status

# For very large blockchains
java -Xmx2048m -jar blockchain-cli.jar status
```

#### Import/Export Issues

**Problem**: Export fails or import doesn't work.

**Solution**:
```bash
# Check file permissions
ls -la backup.json

# Use full paths
java -jar blockchain-cli.jar export /full/path/to/backup.json

# Check available disk space
df -h
```

### Debug Mode

Enable verbose output for detailed debugging:

```bash
java -jar blockchain-cli.jar --verbose status
java -jar blockchain-cli.jar --verbose add-key "test" --generate
```

This will show:
- Database connection details
- Step-by-step operation progress
- Internal validation results
- Timing information

### 📊 Performance Benchmarking

#### Benchmark Your System
```bash
#!/bin/bash
# save as: benchmark.sh

echo "🏁 Blockchain CLI Performance Benchmark"
echo "========================================"

# Startup time test
echo "📏 Testing startup time..."
time java -jar blockchain-cli.jar --version

# Status command performance
echo "📊 Testing status command (5 runs)..."
for i in {1..5}; do
    echo -n "Run $i: "
    time java -jar blockchain-cli.jar status --json >/dev/null
done

# Block addition performance
echo "📦 Testing block addition (10 blocks)..."
start_time=$(date +%s)
for i in {1..10}; do
    java -jar blockchain-cli.jar add-block "Performance test block $i" --generate-key >/dev/null
done
end_time=$(date +%s)
duration=$((end_time - start_time))
echo "Added 10 blocks in $duration seconds ($(echo "scale=2; 10/$duration" | bc) blocks/sec)"

# Validation performance
echo "🔍 Testing validation performance..."
time java -jar blockchain-cli.jar validate >/dev/null

# Search performance
echo "🔎 Testing search performance..."
time java -jar blockchain-cli.jar search "test" >/dev/null

echo "✅ Benchmark completed!"
```

#### Expected Performance Metrics
- **Startup time**: < 3 seconds
- **Status command**: < 2 seconds
- **Block addition**: < 5 seconds per block
- **Chain validation**: < 10 seconds for 100 blocks
- **Search operations**: < 3 seconds for content search

#### Performance Optimization Tips
```bash
# Use JSON output for scripts (faster parsing)
java -jar blockchain-cli.jar status --json

# Use quick validation for frequent checks
java -jar blockchain-cli.jar validate --quick

# Limit search results for better performance
java -jar blockchain-cli.jar search "term" --limit 10

# Increase JVM memory for large blockchains
java -Xmx2048m -jar blockchain-cli.jar validate

# Use batch operations when possible
# (add multiple blocks in sequence rather than individual calls)
```

# Cron job for automated monitoring
# Add to crontab: crontab -e
# 0 8,12,16,20 * * * /path/to/monitor.sh

# Slack integration for alerts
curl -X POST -H 'Content-type: application/json' \
    --data '{"text":"🔴 Blockchain Alert: Chain validation failed!"}' \
    YOUR_SLACK_WEBHOOK_URL

# Prometheus metrics export
cat << EOF > blockchain_metrics.prom
# HELP blockchain_blocks_total Total number of blocks
# TYPE blockchain_blocks_total gauge
blockchain_blocks_total $(java -jar blockchain-cli.jar status --json | jq -r '.blockCount')

# HELP blockchain_keys_total Total number of authorized keys  
# TYPE blockchain_keys_total gauge
blockchain_keys_total $(java -jar blockchain-cli.jar status --json | jq -r '.authorizedKeys')

# HELP blockchain_valid Blockchain validation status (1=valid, 0=invalid)
# TYPE blockchain_valid gauge
blockchain_valid $(java -jar blockchain-cli.jar validate --json | jq -r '.valid' | sed 's/true/1/;s/false/0/')
EOF
```

### 🧪 Testing & Quality Assurance

#### Pre-Production Testing
```bash
# Test script for new deployments
#!/bin/bash
echo "🧪 Pre-Production Blockchain Testing"

# 1. Basic functionality test
java -jar blockchain-cli.jar --version || exit 1
java -jar blockchain-cli.jar status || exit 1

# 2. Key management test
TEST_KEY=$(java -jar blockchain-cli.jar add-key "test-$(date +%s)" --generate --show-private | grep "Public Key:" | cut -d' ' -f3)
java -jar blockchain-cli.jar list-keys | grep -q "$TEST_KEY" || exit 1

# 3. Block operations test
java -jar blockchain-cli.jar add-block "Test block $(date)" --generate-key || exit 1
java -jar blockchain-cli.jar validate || exit 1

# 4. Export/Import test
TEST_EXPORT="test_export_$(date +%s).json"
java -jar blockchain-cli.jar export $TEST_EXPORT || exit 1
java -jar blockchain-cli.jar import $TEST_EXPORT --dry-run || exit 1
rm $TEST_EXPORT

# 5. Search functionality test
java -jar blockchain-cli.jar search "Test block" | grep -q "Test block" || exit 1

echo "✅ All pre-production tests passed!"
```

#### Load Testing
```bash
# Performance load test
#!/bin/bash
echo "⚡ Blockchain Load Testing"

BLOCKS_TO_ADD=50
START_TIME=$(date +%s)

echo "Adding $BLOCKS_TO_ADD blocks..."
for i in $(seq 1 $BLOCKS_TO_ADD); do
    echo -n "Block $i/$BLOCKS_TO_ADD... "
    if java -jar blockchain-cli.jar add-block "Load test block $i - $(date)" --generate-key >/dev/null 2>&1; then
        echo "✅"
    else
        echo "❌"
        exit 1
    fi
done

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))
RATE=$(echo "scale=2; $BLOCKS_TO_ADD / $DURATION" | bc)

echo "📊 Load test results:"
echo "   - Blocks added: $BLOCKS_TO_ADD"
echo "   - Duration: ${DURATION}s"
echo "   - Rate: ${RATE} blocks/sec"

# Validate integrity after load test
if java -jar blockchain-cli.jar validate >/dev/null 2>&1; then
    echo "✅ Chain integrity maintained after load test"
else
    echo "❌ Chain integrity compromised during load test"
    exit 1
fi
```

### 📚 Integration Patterns

#### CI/CD Integration
```yaml
# .github/workflows/blockchain-test.yml
name: Blockchain CI/CD
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Java 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
          
      - name: Build JAR
        run: mvn package
        
      - name: Test Blockchain CLI
        run: |
          java -jar target/blockchain-cli.jar --version
          java -jar target/blockchain-cli.jar status
          java -jar target/blockchain-cli.jar add-key "CI-Test" --generate
          java -jar target/blockchain-cli.jar add-block "CI test block" --generate-key
          java -jar target/blockchain-cli.jar validate
          
      - name: Docker Test
        run: |
          docker build -t blockchain-cli-test .
          docker run --rm blockchain-cli-test --version
          docker run --rm blockchain-cli-test status
```

#### API Integration Example
```bash
# REST API wrapper example
#!/bin/bash
# save as: blockchain_api.sh

case "$1" in
  "status")
    java -jar blockchain-cli.jar status --json
    ;;
  "validate")
    java -jar blockchain-cli.jar validate --json
    ;;
  "add-block")
    java -jar blockchain-cli.jar add-block "$2" --generate-key --json
    ;;
  "search")
    java -jar blockchain-cli.jar search "$2" --json
    ;;
  "export")
    java -jar blockchain-cli.jar export "$2"
    ;;
  *)
    echo "Usage: $0 {status|validate|add-block|search|export} [data]"
    exit 1
    ;;
esac
```

### 🔄 Migration & Upgrade Strategies

#### Version Upgrade Process
```bash
# Safe upgrade procedure
#!/bin/bash
echo "🔄 Blockchain CLI Upgrade Process"

# 1. Create pre-upgrade backup
echo "📦 Creating pre-upgrade backup..."
java -jar blockchain-cli-old.jar export backups/pre_upgrade_$(date +%Y%m%d_%H%M%S).json

# 2. Validate current state
echo "🔍 Validating current blockchain..."
java -jar blockchain-cli-old.jar validate --detailed > pre_upgrade_validation.txt

# 3. Test new version with dry-run import
echo "🧪 Testing new version..."
java -jar blockchain-cli-new.jar import backups/pre_upgrade_*.json --dry-run

# 4. Perform upgrade
echo "⬆️ Performing upgrade..."
cp blockchain-cli-new.jar blockchain-cli.jar

# 5. Verify upgrade success
echo "✅ Verifying upgrade..."
java -jar blockchain-cli.jar validate --detailed > post_upgrade_validation.txt
java -jar blockchain-cli.jar status --detailed

echo "🎉 Upgrade completed successfully!"
```

#### Data Migration Between Environments
```bash
# Environment migration script
#!/bin/bash
SOURCE_ENV="development"
TARGET_ENV="production"

echo "🚀 Migrating blockchain from $SOURCE_ENV to $TARGET_ENV"

# 1. Export from source
echo "📤 Exporting from $SOURCE_ENV..."
java -jar blockchain-cli.jar export migration/${SOURCE_ENV}_to_${TARGET_ENV}_$(date +%Y%m%d).json

# 2. Validate export
echo "🔍 Validating export..."
java -jar blockchain-cli.jar validate --json > migration_validation.json

# 3. Transfer to target environment
echo "📁 Transferring to $TARGET_ENV..."
# scp migration/*.json target-server:/path/to/blockchain/

# 4. Import on target (this would be run on target server)
echo "📥 Import on target environment..."
# java -jar blockchain-cli.jar import migration/*.json --backup --validate-after

echo "✅ Migration completed!"
```

## 🏗️ Project Structure

```
privateBlockchain-cli/
├── src/
│   ├── main/java/com/rbatllet/blockchain/cli/
│   │   ├── BlockchainCLI.java          # Main CLI application
│   │   ├── commands/                   # Command implementations
│   │   │   ├── StatusCommand.java      # status command
│   │   │   ├── ValidateCommand.java    # validate command
│   │   │   ├── AddKeyCommand.java      # add-key command
│   │   │   ├── ListKeysCommand.java    # list-keys command
│   │   │   ├── AddBlockCommand.java    # add-block command
│   │   │   ├── ExportCommand.java      # export command
│   │   │   ├── ImportCommand.java      # import command
│   │   │   ├── SearchCommand.java      # search command
│   │   │   └── HelpCommand.java        # help command
│   │   └── (util/ removed - no longer needed)
│   └── test/java/com/rbatllet/blockchain/cli/
│       ├── BlockchainCLITest.java              # Main CLI tests (7 tests)
│       ├── BlockchainCLIIntegrationTest.java   # Integration tests (10 tests)
│       └── commands/                           # Command-specific tests
│           ├── StatusCommandTest.java          # Status tests (5 tests)
│           ├── ValidateCommandTest.java        # Validate tests (9 tests)
│           ├── AddKeyCommandTest.java          # Add-key tests (7 tests)
│           ├── ListKeysCommandTest.java        # List-keys tests (10 tests)
│           ├── AddBlockCommandTest.java        # Add-block tests (9 tests)
│           ├── ExportCommandTest.java          # Export tests (10 tests)
│           ├── ImportCommandTest.java          # Import tests (12 tests)
│           ├── SearchCommandTest.java          # Search tests (20 tests)
│           └── HelpCommandTest.java            # Help tests (10 tests)
├── pom.xml                    # Maven configuration
├── test-cli.sh               # Enhanced automated test script (334 lines)
├── build-blockchain.sh       # Automated build script (available in parent dir)
└── target/                   # Build artifacts
    ├── blockchain-cli.jar    # Main executable (32MB)
    └── classes/              # Compiled classes
```

### Key Files

- **BlockchainCLI.java**: Main entry point and command router
- **pom.xml**: Maven dependencies and build configuration
- **test-cli.sh**: Enhanced comprehensive test script (334 lines)
- **build-blockchain.sh**: Automated build script (in parent directory)
- **blockchain.db**: SQLite database (created automatically)

## 🌟 Advanced Use Cases & Integrations

### 🏢 Enterprise Integration Examples

#### Enterprise Document Management
```bash
# Corporate document lifecycle tracking
#!/bin/bash
# Document Management Integration

DOC_ID="$1"
ACTION="$2"
USER="$3"
METADATA="$4"

case "$ACTION" in
  "create")
    java -jar blockchain-cli.jar add-block "DOC_CREATED: ID=$DOC_ID | User=$USER | Type=${METADATA} | Timestamp=$(date -Iseconds)" --signer $USER
    ;;
  "modify")
    java -jar blockchain-cli.jar add-block "DOC_MODIFIED: ID=$DOC_ID | User=$USER | Changes=${METADATA} | Timestamp=$(date -Iseconds)" --signer $USER
    ;;
  "approve")
    java -jar blockchain-cli.jar add-block "DOC_APPROVED: ID=$DOC_ID | Approver=$USER | Level=${METADATA} | Timestamp=$(date -Iseconds)" --signer $USER
    ;;
  "archive")
    java -jar blockchain-cli.jar add-block "DOC_ARCHIVED: ID=$DOC_ID | User=$USER | Reason=${METADATA} | Timestamp=$(date -Iseconds)" --signer $USER
    ;;
esac

# Generate audit trail for document
java -jar blockchain-cli.jar search "ID=$DOC_ID" --json > audit_trails/document_${DOC_ID}_audit.json
```

#### Manufacturing Quality Control
```bash
# Quality control checkpoint tracking
#!/bin/bash
# Manufacturing Integration

PRODUCT_ID="$1"
CHECKPOINT="$2"
RESULT="$3"
INSPECTOR="$4"

# Record quality checkpoint
java -jar blockchain-cli.jar add-block "QC_CHECK: Product=$PRODUCT_ID | Checkpoint=$CHECKPOINT | Result=$RESULT | Inspector=$INSPECTOR | BatchTime=$(date -Iseconds)" --signer QualityControl

# Generate quality report
if [ "$RESULT" = "FAIL" ]; then
    echo "⚠️ Quality failure detected for $PRODUCT_ID"
    java -jar blockchain-cli.jar search "Product=$PRODUCT_ID" --json > quality_reports/failure_${PRODUCT_ID}_$(date +%Y%m%d).json
    
    # Alert system integration
    curl -X POST -H 'Content-type: application/json' \
        --data "{\"text\":\"🔴 Quality Alert: Product $PRODUCT_ID failed at $CHECKPOINT\"}" \
        $SLACK_WEBHOOK_URL
fi
```

#### Financial Transaction Auditing
```bash
# Financial transaction logging
#!/bin/bash
# Financial Services Integration

TRANSACTION_ID="$1"
AMOUNT="$2"
FROM_ACCOUNT="$3"
TO_ACCOUNT="$4"
OFFICER="$5"

# Log transaction with encryption reference
HASH=$(echo "$TRANSACTION_ID:$AMOUNT:$FROM_ACCOUNT:$TO_ACCOUNT" | sha256sum | cut -d' ' -f1)

java -jar blockchain-cli.jar add-block "FINANCE_TXN: TxnID=$TRANSACTION_ID | Hash=$HASH | Amount=[ENCRYPTED] | Officer=$OFFICER | Compliance=AML_CHECKED | Timestamp=$(date -Iseconds)" --signer FinancialOfficer

# Compliance reporting
java -jar blockchain-cli.jar search "FINANCE_TXN" --date-from $(date -d "1 month ago" +%Y-%m-%d) --json > compliance/monthly_transactions_$(date +%Y_%m).json
```

### 🤖 Automation & Scripting

#### Automated Compliance Monitoring
```bash
#!/bin/bash
# Compliance monitoring daemon
# save as: compliance_monitor.sh

COMPLIANCE_LOG="compliance_monitor.log"
ALERT_THRESHOLD=10  # minutes

log_event() {
    echo "$(date -Iseconds) - $1" | tee -a $COMPLIANCE_LOG
}

while true; do
    log_event "Starting compliance check cycle"
    
    # Check last blockchain activity
    LAST_BLOCK=$(java -jar blockchain-cli.jar status --json | jq -r '.lastBlockTimestamp // empty')
    
    if [ -n "$LAST_BLOCK" ]; then
        LAST_TIMESTAMP=$(date -d "$LAST_BLOCK" +%s)
        CURRENT_TIMESTAMP=$(date +%s)
        MINUTES_SINCE_LAST=$(( (CURRENT_TIMESTAMP - LAST_TIMESTAMP) / 60 ))
        
        if [ $MINUTES_SINCE_LAST -gt $ALERT_THRESHOLD ]; then
            log_event "WARNING: No blockchain activity for $MINUTES_SINCE_LAST minutes"
            
            # Alert compliance team
            java -jar blockchain-cli.jar add-block "COMPLIANCE_ALERT: No activity for $MINUTES_SINCE_LAST minutes | Auto-generated | Timestamp=$(date -Iseconds)" --generate-key
        fi
    fi
    
    # Validate chain integrity
    if ! java -jar blockchain-cli.jar validate --json | jq -r '.valid' | grep -q true; then
        log_event "CRITICAL: Chain validation failed"
        
        # Emergency backup
        java -jar blockchain-cli.jar export emergency_backups/integrity_failure_$(date +%Y%m%d_%H%M%S).json
        
        # Alert critical systems
        echo "CRITICAL: Blockchain integrity compromised" | mail -s "EMERGENCY: Blockchain Alert" compliance@company.com
    fi
    
    log_event "Compliance check completed"
    sleep 300  # Check every 5 minutes
done
```

#### Batch Processing System
```bash
#!/bin/bash
# Batch processing for high-volume operations
# save as: batch_processor.sh

BATCH_SIZE=100
INPUT_FILE="$1"
OUTPUT_DIR="batch_results"
SIGNER="BatchProcessor"

mkdir -p $OUTPUT_DIR

# Process in batches
split -l $BATCH_SIZE $INPUT_FILE batch_chunk_

for chunk in batch_chunk_*; do
    echo "Processing $chunk..."
    BATCH_ID="batch_$(date +%s)_$(basename $chunk)"
    
    # Start batch
    java -jar blockchain-cli.jar add-block "BATCH_START: ID=$BATCH_ID | File=$chunk | Count=$(wc -l < $chunk) | Timestamp=$(date -Iseconds)" --signer $SIGNER
    
    # Process each line
    line_count=0
    while IFS= read -r line; do
        line_count=$((line_count + 1))
        java -jar blockchain-cli.jar add-block "BATCH_ITEM: BatchID=$BATCH_ID | Line=$line_count | Data=$line | Timestamp=$(date -Iseconds)" --signer $SIGNER
    done < "$chunk"
    
    # End batch
    java -jar blockchain-cli.jar add-block "BATCH_END: ID=$BATCH_ID | Processed=$line_count items | Status=COMPLETED | Timestamp=$(date -Iseconds)" --signer $SIGNER
    
    # Generate batch report
    java -jar blockchain-cli.jar search "BatchID=$BATCH_ID" --json > $OUTPUT_DIR/${BATCH_ID}_report.json
    
    rm $chunk
done

echo "Batch processing completed. Reports in $OUTPUT_DIR/"
```

### 🔗 External System Integrations

#### REST API Gateway
```python
#!/usr/bin/env python3
# Blockchain CLI REST API Gateway
# save as: blockchain_api_gateway.py

from flask import Flask, request, jsonify
import subprocess
import json
import os

app = Flask(__name__)
CLI_JAR = "blockchain-cli.jar"

def run_blockchain_command(cmd_args):
    """Execute blockchain CLI command and return result"""
    try:
        result = subprocess.run(
            ["java", "-jar", CLI_JAR] + cmd_args,
            capture_output=True,
            text=True,
            timeout=30
        )
        return {
            "success": result.returncode == 0,
            "output": result.stdout,
            "error": result.stderr,
            "returncode": result.returncode
        }
    except subprocess.TimeoutExpired:
        return {"success": False, "error": "Command timeout", "returncode": -1}

@app.route('/blockchain/status', methods=['GET'])
def get_status():
    result = run_blockchain_command(["status", "--json"])
    if result["success"]:
        return jsonify(json.loads(result["output"]))
    return jsonify({"error": result["error"]}), 500

@app.route('/blockchain/validate', methods=['POST'])
def validate_chain():
    result = run_blockchain_command(["validate", "--json"])
    if result["success"]:
        return jsonify(json.loads(result["output"]))
    return jsonify({"error": result["error"]}), 500

@app.route('/blockchain/blocks', methods=['POST'])
def add_block():
    data = request.json
    if not data or 'content' not in data:
        return jsonify({"error": "Missing 'content' field"}), 400
    
    signer = data.get('signer', '')
    if signer:
        result = run_blockchain_command(["add-block", data['content'], "--signer", signer])
    else:
        result = run_blockchain_command(["add-block", data['content'], "--generate-key"])
    
    return jsonify(result)

@app.route('/blockchain/search', methods=['GET'])
def search_blocks():
    query = request.args.get('q', '')
    limit = request.args.get('limit', '10')
    
    result = run_blockchain_command(["search", query, "--json", "--limit", limit])
    if result["success"]:
        return jsonify(json.loads(result["output"]))
    return jsonify({"error": result["error"]}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=False)
```

#### Database Integration
```bash
#!/bin/bash
# PostgreSQL integration example
# save as: db_integration.sh

DB_HOST="localhost"
DB_NAME="audit_system"
DB_USER="blockchain_user"

# Function to log blockchain events to database
log_to_database() {
    local event_type="$1"
    local event_data="$2"
    local blockchain_hash="$3"
    
    psql -h $DB_HOST -d $DB_NAME -U $DB_USER -c "
        INSERT INTO blockchain_events (event_type, event_data, blockchain_hash, created_at)
        VALUES ('$event_type', '$event_data', '$blockchain_hash', NOW());
    "
}

# Sync blockchain events to database
sync_blockchain_to_db() {
    echo "🔄 Syncing blockchain to database..."
    
    # Get recent blocks
    java -jar blockchain-cli.jar search --date-from $(date -d "1 day ago" +%Y-%m-%d) --json > recent_blocks.json
    
    # Process each block
    jq -r '.blocks[] | @base64' recent_blocks.json | while read block; do
        decoded=$(echo $block | base64 -d)
        block_data=$(echo $decoded | jq -r '.data')
        block_hash=$(echo $decoded | jq -r '.hash')
        
        log_to_database "BLOCKCHAIN_SYNC" "$block_data" "$block_hash"
    done
    
    rm recent_blocks.json
    echo "✅ Database sync completed"
}

# Schedule sync
sync_blockchain_to_db
```

#### Monitoring Dashboard Data
```bash
#!/bin/bash
# Dashboard metrics collection
# save as: dashboard_metrics.sh

METRICS_DIR="dashboard/metrics"
mkdir -p $METRICS_DIR

# Collect comprehensive metrics
collect_metrics() {
    local timestamp=$(date -Iseconds)
    
    # Basic stats
    local stats=$(java -jar blockchain-cli.jar status --json)
    echo "$stats" > $METRICS_DIR/latest_stats.json
    
    # Performance metrics
    local validation_start=$(date +%s%N)
    java -jar blockchain-cli.jar validate >/dev/null 2>&1
    local validation_end=$(date +%s%N)
    local validation_time=$(( (validation_end - validation_start) / 1000000 ))  # ms
    
    # Growth metrics
    local blocks_today=$(java -jar blockchain-cli.jar search --date-from $(date +%Y-%m-%d) --json | jq '.blocks | length')
    
    # Create metrics JSON
    cat > $METRICS_DIR/metrics_$(date +%Y%m%d_%H%M%S).json << EOF
{
    "timestamp": "$timestamp",
    "blockchain": $stats,
    "performance": {
        "validation_time_ms": $validation_time
    },
    "growth": {
        "blocks_today": $blocks_today
    }
}
EOF

    echo "📊 Metrics collected at $timestamp"
}

# Collect metrics every hour
while true; do
    collect_metrics
    sleep 3600
done
```

## 📊 Project Statistics

- **Total Commands Implemented**: 9 (all core commands + help)
- **Lines of Code**: ~1,500 (main) + ~1,500 (tests)
- **Test Files**: 11 comprehensive test suites
- **Individual Tests**: 100+ covering all functionality
- **Test Success Rate**: ~95% (stable tests passing consistently)
- **Build Status**: ✅ No compilation errors
- **JAR Size**: 32MB (self-contained executable)
- **Supported Platforms**: Any platform with Java 21+

## 🎯 Current Status

**✅ Phase 2 Complete - All Core Commands Implemented**

All primary blockchain management commands are fully implemented, tested, and working:

- ✅ **status**: Complete with multiple output formats (text, JSON, detailed)
- ✅ **validate**: Full blockchain integrity checking (basic, detailed, quick, JSON)
- ✅ **add-key**: Cryptographic key generation and management with security features
- ✅ **list-keys**: Comprehensive key listing with filtering and formatting options
- ✅ **add-block**: Secure block addition with digital signing and validation
- ✅ **export**: Blockchain data export with multiple formats and options
- ✅ **import**: Blockchain data import with backup, validation, and dry-run modes
- ✅ **search**: Advanced block search by content, hash, date ranges, and more
- ✅ **help**: Comprehensive help system with detailed documentation

**The CLI is production-ready and provides a complete interface for private blockchain management.**

## 🚀 Development & Contribution

### Quick Start for Developers

```bash
# 1. Clone and setup
git clone <repository-url>
cd privateBlockchain-cli

# 2. Use the automated build script (recommended)
../build-blockchain.sh

# 3. Or manual build
mvn clean install

# 4. Run comprehensive tests
./test-cli.sh

# 5. Start developing
# Edit files in src/main/java/com/rbatllet/blockchain/cli/
# Add tests in src/test/java/com/rbatllet/blockchain/cli/
```

### Development Workflow

#### **Before Making Changes**:
```bash
# Always test current state
./test-cli.sh

# Run specific tests for your area
mvn test -Dtest=StatusCommandTest  # For status-related changes
mvn test -Dtest=AddKeyCommandTest  # For key management changes
```

#### **During Development**:
```bash
# Quick compile check
mvn compile

# Test your specific changes
mvn test -Dtest=YourNewTest

# Build and test everything
mvn clean package && ./test-cli.sh
```

#### **Before Committing**:
```bash
# Full validation
./test-cli.sh

# Ensure no compilation issues
mvn clean compile

# Check code style
# (Add your preferred linting here)
```

### Adding New Commands

1. **Create Command Class**:
   ```java
   // In src/main/java/com/rbatllet/blockchain/cli/commands/
   @Command(name = "your-command", description = "Description")
   public class YourCommand implements Runnable {
       // Implementation
   }
   ```

2. **Register in Main CLI**:
   ```java
   // In BlockchainCLI.java, add to subcommands list
   subcommands = { ..., YourCommand.class }
   ```

3. **Create Tests**:
   ```java
   // In src/test/java/com/rbatllet/blockchain/cli/commands/
   public class YourCommandTest {
       // Comprehensive tests
   }
   ```

4. **Update test-cli.sh**:
   ```bash
   # Add your command to the test script
   run_cli_test "Your command test" your-command --options
   ```

### Code Quality Guidelines

#### **Java Conventions**:
- Use Java 21 features where appropriate
- Follow standard naming conventions (camelCase, PascalCase)
- Add JavaDoc for public methods and classes
- Use meaningful variable and method names
- Handle errors gracefully with appropriate messages

#### **Testing Requirements**:
- Write tests for ALL new functionality
- Include positive and negative test cases
- Test all command flags and options
- Use temporary directories for file operations (@TempDir)
- Ensure tests are isolated and independent

#### **CLI Design Principles**:
- Follow Unix philosophy (do one thing well)
- Provide clear, helpful error messages
- Support multiple output formats (text, JSON)
- Include comprehensive help documentation
- Handle edge cases gracefully

### Performance Considerations

- **Startup Time**: Keep initialization under 3 seconds
- **Memory Usage**: Aim for <50MB for normal operations
- **Database Operations**: Use efficient queries
- **Large Data**: Consider streaming for large exports/imports
- **User Experience**: Provide progress indicators for long operations

### Security Guidelines

- **Input Validation**: Validate all user inputs
- **Error Messages**: Don't leak sensitive information
- **File Operations**: Use secure temporary files
- **Key Handling**: Secure storage and transmission of cryptographic keys
- **Database**: Use parameterized queries to prevent injection

## 📝 Future Enhancements

While the current implementation is complete and functional, potential future improvements include:

## 📝 Future Enhancements

While the current implementation is complete and functional, potential future improvements include:

- **Configuration files**: External configuration for parameters and settings
- **Batch operations**: Process multiple blocks or keys at once
- **Performance optimization**: Better handling of very large blockchains
- **Network features**: Multi-node blockchain support and synchronization
- **Advanced search**: More complex query capabilities and filters
- **Reporting**: Detailed blockchain analysis and statistics reports
- **Plugin system**: Extensible architecture for custom commands
- **Web interface**: Optional web-based management interface
- **Monitoring**: Health checks and alerting capabilities

## 🔄 CI/CD Integration

### Automated Testing in Pipelines

```bash
# In your CI/CD pipeline
./test-cli.sh

# For faster CI builds (skip unit tests)
SKIP_UNIT_TESTS=true ./test-cli.sh

# Generate JAR for deployment
mvn clean package -DskipTests
```

### Docker Support

The CLI is fully compatible with Docker for containerized deployments, CI/CD pipelines, and cloud environments.

#### Basic Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/blockchain-cli.jar .
VOLUME ["/data"]
WORKDIR /data
ENTRYPOINT ["java", "-jar", "/app/blockchain-cli.jar"]
CMD ["--help"]
```

#### Quick Start with Docker (Recommended for New Users)

The easiest way to use the blockchain CLI without installing Java locally:

```bash
# 1. Build the Docker image (one-time setup)
docker build -t blockchain-cli .

# 2. Basic usage - check if everything works
docker run --rm blockchain-cli --version
docker run --rm blockchain-cli status

# 3. Create data directory for persistent storage
mkdir -p blockchain-data backups

# 4. Run with persistent data (recommended)
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli status --detailed
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli validate

# 5. Add a test block with auto-generated key
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "My first Docker block" --generate-key

# 6. Search and export
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli search "Docker"
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli export /backups/my-backup.json
```

#### Docker Compose (Even Easier)

For the simplest experience, use docker-compose:

```bash
# Run basic status check
docker-compose --profile default up

# Interactive mode for multiple commands
docker-compose --profile interactive up
# Then inside the container: java -jar /app/blockchain-cli.jar status

# One-off backup
docker-compose --profile backup up

# Chain validation
docker-compose --profile validate up
```

#### Stopping Docker Services

```bash
# Stop single docker run commands: Press Ctrl+C

# Stop docker-compose services
docker-compose down

# Stop and remove all related containers, networks, volumes
docker-compose down --volumes --remove-orphans

# Stop specific profile
docker-compose --profile interactive down

# Force stop all blockchain-cli containers (if any are running)
docker ps -q --filter ancestor=blockchain-cli | xargs -r docker stop

# Alternative safer approach
containers=$(docker ps -q --filter ancestor=blockchain-cli)
if [ ! -z "$containers" ]; then docker stop $containers; fi

# Remove blockchain-cli image (for rebuild)
docker rmi blockchain-cli
```

#### Important Notes for Docker Compose

**Date Command Escaping**: When using date commands in docker-compose.yml, you need to escape the `$` with `$$`:

```yaml
# ❌ Wrong - will not work
command: ["export", "/backups/backup_$(date +%Y%m%d).json"]

# ✅ Correct - use $$ to escape
entrypoint: ["sh", "-c"]
command: ["java -jar /app/blockchain-cli.jar export /backups/backup_$$(date +%Y%m%d_%H%M%S).json"]
```

**Volume Mounting**: Always use absolute paths or `$(pwd)` for volume mounting:

```bash
# ✅ Correct
docker run -v $(pwd)/blockchain-data:/data blockchain-cli status

# ❌ Wrong
docker run -v ./blockchain-data:/data blockchain-cli status
```

**Stopping Containers Safely**: When no containers are running, the naive approach fails:

```bash
# ❌ Wrong - fails when no containers are running
docker stop $(docker ps -q --filter ancestor=blockchain-cli)
# Error: 'docker stop' requires at least 1 argument

# ✅ Correct - handles empty results gracefully
docker ps -q --filter ancestor=blockchain-cli | xargs -r docker stop
```

#### Building and Running (Advanced Users)

```bash
# Build the Docker image
docker build -t blockchain-cli .

# Run basic commands
docker run blockchain-cli --version
docker run blockchain-cli status

# Mount volume for persistent data
docker run -v $(pwd)/data:/data blockchain-cli status --detailed

# Export blockchain data
docker run -v $(pwd)/backups:/data blockchain-cli export backup.json

# Import blockchain data
docker run -v $(pwd)/backups:/data blockchain-cli import backup.json --validate-after
```

#### Docker Compose Example

```yaml
version: '3.8'
services:
  blockchain-cli:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    environment:
      - JAVA_OPTS=-Xmx512m
    command: ["status", "--json"]
    
  # Automated backup service
  blockchain-backup:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    entrypoint: ["sh", "-c"]
    command: ["java -jar /app/blockchain-cli.jar export /backups/backup_$$(date +%Y%m%d_%H%M%S).json"]
    profiles: ["backup"]
```

#### CI/CD Integration

```bash
# GitHub Actions / GitLab CI example
docker run --rm \
  -v ${{ github.workspace }}/data:/data \
  blockchain-cli:latest validate --json > validation_report.json

# Jenkins pipeline
docker run --rm \
  -v "${WORKSPACE}/blockchain-data:/data" \
  blockchain-cli:latest status --detailed
```

#### Kubernetes Deployment

```yaml
# Blockchain validation CronJob
apiVersion: batch/v1
kind: CronJob
metadata:
  name: blockchain-validator
spec:
  schedule: "0 */6 * * *"  # Every 6 hours
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: validator
            image: blockchain-cli:latest
            command: ["java", "-jar", "/app/blockchain-cli.jar"]
            args: ["validate", "--detailed"]
            volumeMounts:
            - name: blockchain-data
              mountPath: /data
          volumes:
          - name: blockchain-data
            persistentVolumeClaim:
              claimName: blockchain-pvc
          restartPolicy: OnFailure

---
# Daily backup CronJob
apiVersion: batch/v1
kind: CronJob
metadata:
  name: blockchain-backup
spec:
  schedule: "0 2 * * *"  # Daily at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: backup
            image: blockchain-cli:latest
            command: ["java", "-jar", "/app/blockchain-cli.jar"]
            args: ["export", "/backups/daily_backup.json"]
            volumeMounts:
            - name: blockchain-data
              mountPath: /data
            - name: backup-storage
              mountPath: /backups
          volumes:
          - name: blockchain-data
            persistentVolumeClaim:
              claimName: blockchain-pvc
          - name: backup-storage
            persistentVolumeClaim:
              claimName: backup-pvc
          restartPolicy: OnFailure
```

#### Production Docker Configuration

```dockerfile
# Multi-stage build for smaller production image
FROM openjdk:21-jdk-slim AS builder
WORKDIR /build
COPY pom.xml .
COPY src src
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
LABEL maintainer="blockchain-team@company.com"
LABEL version="1.0.0"
LABEL description="Private Blockchain CLI"

# Create non-root user for security
RUN groupadd -r blockchain && useradd -r -g blockchain blockchain

# Install runtime dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /build/target/blockchain-cli.jar .
COPY --chown=blockchain:blockchain . .

# Create data directory
RUN mkdir -p /data && chown blockchain:blockchain /data
VOLUME ["/data"]

# Switch to non-root user
USER blockchain
WORKDIR /data

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -jar /app/blockchain-cli.jar status || exit 1

ENTRYPOINT ["java", "-jar", "/app/blockchain-cli.jar"]
CMD ["--help"]
```

#### Docker Environment Variables

```bash
# Configure JVM options
docker run -e JAVA_OPTS="-Xmx1024m -Xms512m" blockchain-cli status

# Set timezone
docker run -e TZ=Europe/Madrid blockchain-cli status --detailed

# Enable verbose logging
docker run -e BLOCKCHAIN_VERBOSE=true blockchain-cli validate
```

#### Use Cases for Docker

1. **Development Teams**: Consistent environment across all developers
2. **CI/CD Pipelines**: Automated testing and validation in isolated containers
3. **Production Deployments**: Reliable blockchain operations in containerized infrastructure
4. **Cloud Environments**: Easy deployment to AWS ECS, Google Cloud Run, Azure Container Instances
5. **Microservices Architecture**: Blockchain CLI as a service component
6. **Scheduled Operations**: Automated backups and maintenance using cron-like schedulers

## 🤝 Contributing

### Development Workflow

```bash
# 1. Setup and validate current state
git clone <repository-url>
cd privateBlockchain-cli
./test-cli.sh

# 2. Make your changes
# Edit files in src/main/java/com/rbatllet/blockchain/cli/
# Add tests in src/test/java/com/rbatllet/blockchain/cli/

# 3. Test your changes
mvn test -Dtest=YourNewTest
./test-cli.sh

# 4. Submit your contribution
git commit -m "Your changes"
git push origin your-feature-branch
```

### Adding New Commands

1. Create command class in `src/main/java/.../commands/`
2. Add to `BlockchainCLI.java` subcommands list  
3. Write comprehensive tests in `src/test/java/.../commands/`
4. Update `test-cli.sh` with new command tests
5. Update README.md documentation

### Code Quality Standards

- **Testing**: Write tests for ALL new functionality
- **Documentation**: Add JavaDoc for public methods
- **Error Handling**: Provide clear, helpful error messages
- **Performance**: Keep startup time under 3 seconds
- **Security**: Validate inputs and handle keys securely

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

### Getting Help

- **GitHub Issues**: Report bugs or request features
- **Comprehensive Testing**: Run `./test-cli.sh` to verify setup
- **Troubleshooting**: Check the detailed troubleshooting section above
- **Verbose Mode**: Use `--verbose` flag for detailed debugging
- **Community**: Contributions and improvements welcome

### Quick Support Checklist

1. **Verify Java 21+** is installed: `java -version`
2. **Run test suite**: `./test-cli.sh`
3. **Check basic functionality**: `java -jar target/blockchain-cli.jar status`
4. **Enable verbose mode** for debugging: `java -jar blockchain-cli.jar --verbose status`
5. **Review logs** and error messages for specific issues

---

**Enterprise-ready blockchain management at your fingertips! 🔗⚡**