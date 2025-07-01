# 💡 Blockchain CLI Examples

Comprehensive examples and use cases for the Private Blockchain CLI, featuring the new enhanced `--signer` functionality and secure private key management.

## 📋 Table of Contents

- [Quick Start Examples](#-quick-start-examples)
- [Off-Chain Storage Examples](#-off-chain-storage-examples)
- [Hybrid Search Examples](#-hybrid-search-examples)
- [Enhanced CLI Features](#-enhanced-cli-features)
- [Secure Key Management Examples](#-secure-key-management-examples)
- [Advanced Signer Workflows](#-advanced-signer-workflows)
- [Real-World Use Cases](#-real-world-use-cases)
- [Advanced Scenarios](#-advanced-scenarios)
- [Docker Examples](#-docker-examples)
- [Automation Scripts](#-automation-scripts)
- [Demo Scripts](#-demo-scripts)

## 🚀 Quick Start Examples

### Example 1: First-Time Setup with Secure Keys
```zsh
# Step 1: Check if CLI is working
java -jar blockchain-cli.jar --version
# Expected: 1.0.4

# Step 2: Initialize and check blockchain
java -jar blockchain-cli.jar status
# Creates genesis block automatically

# Step 3: Set up users with different security levels
# Production user with stored private key
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
🔐 Enter password to protect private key: [hidden]
Confirm password: [hidden]
🔒 Private key stored securely for: Alice

# Demo users without stored private keys
java -jar blockchain-cli.jar add-key "Bob" --generate
java -jar blockchain-cli.jar add-key "Charlie" --generate

# Step 4: Verify users and check stored keys
java -jar blockchain-cli.jar list-keys --detailed
java -jar blockchain-cli.jar manage-keys --list

# Step 5: Add blocks using different security modes
# Production mode (requires password)
java -jar blockchain-cli.jar add-block "Secure production data" --signer Alice
🔐 Enter password for Alice: [hidden]
✅ Using stored private key for signer: Alice

# Demo mode (temporary keys)
java -jar blockchain-cli.jar add-block "Demo data from Bob" --signer Bob
⚠️  DEMO MODE: No stored private key found for signer: Bob

# External key file mode (for enterprise use)
java -jar blockchain-cli.jar add-block "Enterprise data" --key-file /path/to/keys/private_key.pem
✅ Successfully loaded private key from file
✅ Block added successfully!

# With verbose output for detailed information
java -jar blockchain-cli.jar add-block "Detailed logging" --key-file /path/to/keys/private_key.pem --verbose
📡 Verbose: Attempting to load private key from file: /path/to/keys/private_key.pem
📡 Verbose: Detected key format: PEM PKCS#8
📡 Verbose: Successfully loaded RSA private key
📡 Verbose: Derived public key from private key
📡 Verbose: Auto-authorizing key with name: KeyFile-private_key.pem-1686841234567
✅ Successfully loaded private key from file
✅ Block added successfully!
🔑 DEMO: Created temporary key for existing signer: Bob

# Step 6: Verify everything worked
java -jar blockchain-cli.jar validate --detailed

# With verbose output for detailed validation information
java -jar blockchain-cli.jar validate --detailed --verbose
🔍 Starting comprehensive blockchain validation...
🔍 Validating block #1: Genesis block
🔍 Validating block #2: Production data
🔍 Validation completed successfully

# Check system status with verbose output
java -jar blockchain-cli.jar status --detailed --verbose
🔍 Initializing blockchain status check...
🔍 Loading blockchain database...
🔍 Analyzing chain integrity...
✅ Status check completed
```

## 📄 File Input Examples

The CLI supports reading block content from external files using the `--file` option, providing better flexibility and automation capabilities.

### Example 1: Basic File Input
```zsh
# Create a text file with content
echo "Patient medical record for John Doe - Regular checkup completed successfully." > patient-record.txt

# Add block content from file
java -jar blockchain-cli.jar add-block --file patient-record.txt --generate-key --verbose
🔍 Reading block content from file: patient-record.txt
🔍 Successfully read 78 bytes from file
✅ Block added successfully!

# With keywords and category
java -jar blockchain-cli.jar add-block --file patient-record.txt \
    --keywords "PATIENT,CHECKUP,MEDICAL" \
    --category "MEDICAL" \
    --generate-key
```

### Example 2: Large File Processing
```zsh
# Create a large financial report
cat > quarterly-report-q1.txt << EOF
FINANCIAL QUARTERLY REPORT Q1 2024
==================================
$(for i in {1..5000}; do
    echo "Transaction $i: Amount \$$(( RANDOM % 10000 + 100 )), Account ACC-$(( RANDOM % 100000 + 10000 )), Date: 2024-01-$(( RANDOM % 28 + 1 ))"
done)
EOF

# Add large file (will automatically use off-chain storage)
java -jar blockchain-cli.jar add-block --file quarterly-report-q1.txt \
    --keywords "Q1-2024,FINANCIAL,QUARTERLY-REPORT" \
    --category "FINANCE" \
    --generate-key \
    --verbose
📝 Data size: 245.3 KB
💾 Large data detected - will be stored off-chain with encryption
✅ Block added successfully!
```

### Example 3: Different File Types
```zsh
# Technical documentation
java -jar blockchain-cli.jar add-block --file api-documentation.md \
    --keywords "API,DOCS,TECHNICAL" \
    --category "TECHNICAL" \
    --generate-key

# Legal contracts (text converted from PDF)
java -jar blockchain-cli.jar add-block --file contract-partnership-2024.txt \
    --keywords "CONTRACT,PARTNERSHIP,LEGAL" \
    --category "LEGAL" \
    --signer Legal-Counsel

# Medical records in structured format
java -jar blockchain-cli.jar add-block --file patient-mri-results.json \
    --keywords "MRI,PATIENT-007,RADIOLOGY" \
    --category "MEDICAL" \
    --generate-key
```

### Example 4: Error Handling
```zsh
# File doesn't exist
java -jar blockchain-cli.jar add-block --file non-existent.txt --generate-key
❌ Failed to read input file: Input file does not exist: non-existent.txt

# Cannot specify both file and direct data
java -jar blockchain-cli.jar add-block "direct data" --file some-file.txt --generate-key
❌ Failed to add block: Runtime error - Cannot specify both file input (-f/--file) and direct data input. Please use only one method.

# Missing input entirely
java -jar blockchain-cli.jar add-block --generate-key
❌ Failed to add block: Runtime error - Must specify either block data directly or use --file option to read from file.
```

### Example 5: Automation with File Input
```zsh
# Process multiple files in a loop
for file in reports/*.txt; do
    echo "Processing $file..."
    java -jar blockchain-cli.jar add-block --file "$file" \
        --keywords "BATCH-PROCESS,$(basename "$file" .txt)" \
        --category "REPORTS" \
        --generate-key
done

# Use file input in automated scripts
#!/bin/bash
DATA_FILE="/tmp/daily-report-$(date +%Y%m%d).txt"
generate_daily_report > "$DATA_FILE"
java -jar blockchain-cli.jar add-block --file "$DATA_FILE" \
    --keywords "DAILY-REPORT,$(date +%Y-%m-%d)" \
    --category "OPERATIONS" \
    --signer Operations-Bot
```

## 💾 Off-Chain Storage Examples

The CLI now automatically handles large data with off-chain storage and AES encryption.

### Example 1: Automatic Off-Chain Storage
```zsh
# Small data stays on-chain
java -jar blockchain-cli.jar add-block "Small patient record for PAT-001 with normal vital signs." \
    --keywords "PAT-001,MEDICAL,VITALS" \
    --category "MEDICAL" \
    --generate-key \
    --verbose

# Large data automatically goes off-chain (>512KB)
java -jar blockchain-cli.jar add-block "$(cat large_medical_report.txt)" \
    --keywords "PATIENT-001,MRI,COMPREHENSIVE" \
    --category "MEDICAL" \
    --generate-key \
    --verbose
📊 Large data detected (1.2 MB). Will store off-chain.
🔐 Encrypting data with AES-256-CBC...
💾 Data stored off-chain. Block contains reference: OFF_CHAIN_REF:abc123...
```

### Example 2: Large Financial Batch Processing
```zsh
# Create large financial dataset
cat > financial_batch_q1.txt << EOF
Financial Batch Processing Report Q1 2024
==========================================
$(for i in {1..10000}; do
    echo "TXN-$(printf '%06d' $i): Amount \$$(( RANDOM % 10000 + 100 )).$(( RANDOM % 100 )), Account ACC-$(( RANDOM % 1000000 + 100000 )), Status: SUCCESS"
done)
EOF

# Add with automatic off-chain storage
java -jar blockchain-cli.jar add-block "$(cat financial_batch_q1.txt)" \
    --keywords "BATCH-Q1-2024,FINANCIAL,TRANSACTIONS" \
    --category "FINANCE" \
    --generate-key \
    --verbose
```

### Example 3: Legal Document Storage
```zsh
# Large contract document
java -jar blockchain-cli.jar add-block "$(cat contract_2024_partnership.pdf.txt)" \
    --keywords "CONTRACT,PARTNERSHIP,LEGAL,IP-RIGHTS" \
    --category "LEGAL" \
    --signer Legal-Counsel \
    --verbose
```

### Example 4: Off-Chain Data Validation
```zsh
# Validate blockchain including off-chain data integrity
java -jar blockchain-cli.jar validate --detailed --verbose
🔍 Starting comprehensive validation with off-chain data...
🔍 Validating block #15: Large medical report
✅ Block #15 validation passed
📁 Off-chain file: offchain_1234567890_5678.dat
📦 Size: 1.2 MB, 🔐 Encrypted: Yes, ✅ Integrity: Verified
🔍 Off-chain data validation completed

# Quick validation without verbose output
java -jar blockchain-cli.jar validate --detailed

# Basic validation with verbose logging
java -jar blockchain-cli.jar validate --verbose
🔍 Starting basic blockchain validation...
🔍 Chain integrity check completed
```

## 🔍 Hybrid Search Examples

The CLI provides multi-level search capabilities for different performance needs.

### Example 1: Fast Search (Keywords Only)
```zsh
# Fastest search - only searches in manual and auto keywords
java -jar blockchain-cli.jar search "PATIENT-001" --fast --verbose
⚡ FAST_ONLY search completed in 15ms
📦 Found 3 blocks: #1, #5, #12

# Search by transaction ID
java -jar blockchain-cli.jar search "TXN-2024-001" --fast
```

### Example 2: Balanced Search (Include Data)
```zsh
# Default search level - searches keywords + block data
java -jar blockchain-cli.jar search "cardiology" --level INCLUDE_DATA --verbose
⚖️ INCLUDE_DATA search completed in 45ms
📦 Found 4 blocks with cardiology information

# Search with detailed output
java -jar blockchain-cli.jar search "financial" --level INCLUDE_DATA --detailed
```

### Example 3: Exhaustive Search (Including Off-Chain)
```zsh
# Most comprehensive - searches everything including off-chain files
java -jar blockchain-cli.jar search "partnership" --complete --verbose --detailed
🔍 EXHAUSTIVE_OFFCHAIN search completed in 340ms
  - Fast results: 1 block
  - Off-chain matches: 2 blocks
📦 Found 3 blocks total including off-chain content
```

### Example 4: Category-Based Search
```zsh
# Search by content category
java -jar blockchain-cli.jar search --category MEDICAL --limit 10 --detailed
📂 Found 8 blocks in category: MEDICAL

java -jar blockchain-cli.jar search --category FINANCE --json
```

### Example 5: Advanced Search Options
```zsh
# Date range search
java -jar blockchain-cli.jar search --date-from 2024-01-01 --date-to 2024-12-31 --verbose

# Block number search
java -jar blockchain-cli.jar search --block-number 5 --detailed

# Search with JSON output
java -jar blockchain-cli.jar search "API" --json --limit 5

# Search with performance reporting
java -jar blockchain-cli.jar search "data" --verbose
🔍 Search performance: 23ms (INCLUDE_DATA level)
```

### Example 6: Search Performance Comparison
```zsh
# Compare performance across search levels
echo "Testing search performance for 'transaction'..."

time java -jar blockchain-cli.jar search "transaction" --fast
# Fast: ~10-20ms

time java -jar blockchain-cli.jar search "transaction" --level INCLUDE_DATA  
# Balanced: ~30-60ms

time java -jar blockchain-cli.jar search "transaction" --complete
# Exhaustive: ~200-500ms (depending on off-chain data size)
```

## 🚀 Enhanced CLI Features

New features that extend the CLI's capabilities while maintaining backward compatibility.

### Example 1: Keywords and Categories
```zsh
# Manual keywords with category
java -jar blockchain-cli.jar add-block "Project meeting on 2024-01-15 with stakeholders." \
    --keywords "MEETING,PROJECT,STAKEHOLDERS" \
    --category "BUSINESS" \
    --generate-key

# Automatic keyword extraction (universal elements)
java -jar blockchain-cli.jar add-block "Contact admin@company.com for API access. Budget: 50000 EUR. Document ref: DOC-2024-001." \
    --category "TECHNICAL" \
    --generate-key \
    --verbose
🤖 Auto Keywords: admin@company.com, 50000, EUR, DOC-2024-001, 2024, API
```

### Example 2: Keyword Processing
```zsh
# Keywords with spaces (automatically trimmed)
java -jar blockchain-cli.jar add-block "Test data for keyword processing" \
    --keywords " KEYWORD1 , KEYWORD2, KEYWORD3 " \
    --generate-key \
    --verbose
🔍 Using manual keywords: KEYWORD1, KEYWORD2, KEYWORD3
🏷️ Manual Keywords: keyword1 keyword2 keyword3
```

### Example 3: Enhanced JSON Output
```zsh
# JSON output includes new fields
java -jar blockchain-cli.jar add-block "Enhanced JSON test data" \
    --keywords "JSON,TEST,ENHANCED" \
    --category "TECHNICAL" \
    --generate-key \
    --json
{
  "success": true,
  "blockNumber": 42,
  "manualKeywords": ["json", "test", "enhanced"],
  "autoKeywords": ["enhanced", "json", "test", "data"],
  "category": "TECHNICAL",
  "offChainStorage": false,
  "dataSize": 25
}
```

### Example 4: Backward Compatibility
```zsh
# Legacy commands work unchanged
java -jar blockchain-cli.jar add-block "Legacy data without keywords" --generate-key
java -jar blockchain-cli.jar search "Genesis"
java -jar blockchain-cli.jar status
java -jar blockchain-cli.jar validate
```

### Example 5: Enhanced Verbose Output
```zsh
java -jar blockchain-cli.jar add-block "Verbose example with all features" \
    --keywords "VERBOSE,EXAMPLE,FEATURES" \
    --category "TEST" \
    --generate-key \
    --verbose
🔍 Adding new block to blockchain...
🔍 Data will be stored on-chain
🔍 Generating new key pair...
🔍 Using manual keywords: VERBOSE, EXAMPLE, FEATURES
🔍 Using content category: TEST
🔍 Attempting to add block with derived public key...
✅ Block added successfully!
```

## 🔐 Secure Key Management Examples

### Example 1: Setting Up Production Environment
```zsh
# Create secure production users
java -jar blockchain-cli.jar add-key "CEO" --generate --store-private
java -jar blockchain-cli.jar add-key "CFO" --generate --store-private  
java -jar blockchain-cli.jar add-key "CTO" --generate --store-private

# Verify all keys are stored securely
java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
🔑 CEO
🔑 CFO
🔑 CTO
📊 Total: 3 stored private key(s)

# Test access to stored keys
java -jar blockchain-cli.jar manage-keys --test CEO
🔐 Enter password for CEO: [hidden]
✅ Password is correct for: CEO

# Use secure keys for critical operations
java -jar blockchain-cli.jar add-block "Q4 Financial Results Approved" --signer CEO
java -jar blockchain-cli.jar add-block "Budget for 2026 Allocated" --signer CFO
java -jar blockchain-cli.jar add-block "Security Audit Completed" --signer CTO
```

### Example 2: Mixed Environment (Production + Development)
```zsh
# Production users with stored keys
java -jar blockchain-cli.jar add-key "ProductionUser" --generate --store-private
java -jar blockchain-cli.jar add-key "AuditUser" --generate --store-private

# Development users without stored keys
java -jar blockchain-cli.jar add-key "DevUser1" --generate
java -jar blockchain-cli.jar add-key "TestUser" --generate

# Check security setup
java -jar blockchain-cli.jar manage-keys --list
java -jar blockchain-cli.jar manage-keys --check ProductionUser
java -jar blockchain-cli.jar manage-keys --check DevUser1

# Production operations (secure)
java -jar blockchain-cli.jar add-block "Customer data processed" --signer ProductionUser
🔐 Enter password for ProductionUser: [hidden]
✅ Using stored private key for signer: ProductionUser

# Development operations (demo mode)  
java -jar blockchain-cli.jar add-block "Test data for debugging" --signer DevUser1
⚠️  DEMO MODE: No stored private key found for signer: DevUser1
🔑 DEMO: Created temporary key for existing signer: DevUser1
```

### Example 3: Key Lifecycle Management
```zsh
# Create user with stored key
java -jar blockchain-cli.jar add-key "TempUser" --generate --store-private

# Use the key for some operations
java -jar blockchain-cli.jar add-block "Temporary operation" --signer TempUser

# When user leaves or key compromised, remove stored key
java -jar blockchain-cli.jar manage-keys --delete TempUser
⚠️  Are you sure you want to delete the private key for 'TempUser'? (yes/no): yes
🗑️  Private key deleted for: TempUser

# User can still be used but will fall back to demo mode
java -jar blockchain-cli.jar add-block "Post-deletion operation" --signer TempUser
⚠️  DEMO MODE: No stored private key found for signer: TempUser
```

### Example 2: Daily Operations with Role-Based Signing
```zsh
# Morning: Check blockchain health
java -jar blockchain-cli.jar status --detailed

# Morning: Check blockchain health with verbose output
java -jar blockchain-cli.jar status --detailed --verbose
🔍 Initializing comprehensive status check...
🔍 Loading blockchain database...
🔍 Analyzing system configuration...
🔍 Checking off-chain storage integrity...
✅ Comprehensive status check completed

# Team lead adds daily standup notes
java -jar blockchain-cli.jar add-block "Daily Standup 2025-06-12: Team velocity on track, 3 stories completed" --signer TeamLead

# Developer adds technical updates
java -jar blockchain-cli.jar add-block "Feature: User authentication implemented and tested" --signer Developer

# Manager adds business decisions
java -jar blockchain-cli.jar add-block "Decision: Approved budget for additional QA resources" --signer Manager

# End of day: Create backup
java -jar blockchain-cli.jar export backups/daily_$(date +%Y%m%d).json

# Verify backup integrity
java -jar blockchain-cli.jar validate --json
```

## 🎯 Advanced Signer Workflows

### Workflow 1: Multi-Department Corporate Environment
```zsh
# Setup: Create department-specific signers
java -jar blockchain-cli.jar add-key "HR-Director" --generate
java -jar blockchain-cli.jar add-key "Finance-Manager" --generate
java -jar blockchain-cli.jar add-key "IT-Administrator" --generate
java -jar blockchain-cli.jar add-key "Legal-Counsel" --generate

# Verify all signers are created
java -jar blockchain-cli.jar list-keys

# Department-specific transactions
java -jar blockchain-cli.jar add-block "New employee onboarded: John Doe | Start date: 2025-06-15 | Department: Engineering" --signer HR-Director
java -jar blockchain-cli.jar add-block "Budget allocation approved: Q3 Marketing $50K | Reference: FIN-2025-Q3-001" --signer Finance-Manager
java -jar blockchain-cli.jar add-block "Security update deployed: CVE-2025-1234 patched across all systems" --signer IT-Administrator
java -jar blockchain-cli.jar add-block "Contract review completed: Vendor Agreement ABC Corp | Status: Approved with amendments" --signer Legal-Counsel

# Generate department activity report
java -jar blockchain-cli.jar search "HR-Director\|Finance-Manager\|IT-Administrator\|Legal-Counsel" --detailed
```

### Workflow 2: Project Management with Milestone Tracking
```zsh
# Setup: Create project team signers
java -jar blockchain-cli.jar add-key "Project-Manager" --generate  
java -jar blockchain-cli.jar add-key "Lead-Developer" --generate
java -jar blockchain-cli.jar add-key "QA-Lead" --generate
java -jar blockchain-cli.jar add-key "Product-Owner" --generate

# Project phases with different signers
java -jar blockchain-cli.jar add-block "PROJECT INITIATION: Project Alpha kickoff meeting completed | Stakeholders: 8 | Duration: 6 months" --signer Project-Manager
java -jar blockchain-cli.jar add-block "DEVELOPMENT: Sprint 1 completed | User stories: 12/12 | Velocity: 45 points" --signer Lead-Developer  
java -jar blockchain-cli.jar add-block "TESTING: Integration tests completed | Test cases: 156 passed, 0 failed | Coverage: 94%" --signer QA-Lead
java -jar blockchain-cli.jar add-block "REVIEW: Sprint 1 demo approved by stakeholders | Feedback: Positive | Next sprint approved" --signer Product-Owner

# Track project timeline
java -jar blockchain-cli.jar search "PROJECT\|DEVELOPMENT\|TESTING\|REVIEW" --json > project_alpha_timeline.json
```

### Workflow 3: Error Handling and Troubleshooting
```zsh
# Common error scenarios and solutions

# Error 1: Trying to use non-existent signer
$ java -jar blockchain-cli.jar add-block "Test data" --signer NonExistentUser
❌ Error: Signer 'NonExistentUser' not found in authorized keys
❌ Error: Use 'blockchain list-keys' to see available signers

# Solution: Check available signers
java -jar blockchain-cli.jar list-keys

# Error 2: No signing method specified
$ java -jar blockchain-cli.jar add-block "Test data"
❌ Error: No signing method specified

# Solution: Use one of the available methods
java -jar blockchain-cli.jar add-block "Test data" --signer Alice
# OR
java -jar blockchain-cli.jar add-block "Test data" --generate-key

# Error 3: Dealing with special characters in data
java -jar blockchain-cli.jar add-block "Transaction with \"quotes\" and $symbols" --signer Alice
java -jar blockchain-cli.jar add-block 'Data with single quotes and $variables' --signer Alice

# Advanced Scenarios

# Using key file with different formats
java -jar blockchain-cli.jar add-block "PEM format example" --key-file keys/private_key.pem
java -jar blockchain-cli.jar add-block "DER format example" --key-file keys/private_key.der
java -jar blockchain-cli.jar add-block "Base64 format example" --key-file keys/private_key.b64

# Using verbose mode for troubleshooting
java -jar blockchain-cli.jar add-block "Verbose example" --key-file keys/private_key.pem --verbose
```

### Example 3: Multi-User Setup
```zsh
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

## 🎯 Real-World Use Cases

### Use Case 1: Document Audit Trail with Role-Based Approval
```zsh
# Setup for document management system with different roles
java -jar blockchain-cli.jar add-key "Document-Author" --generate
java -jar blockchain-cli.jar add-key "Legal-Reviewer" --generate
java -jar blockchain-cli.jar add-key "Final-Approver" --generate

# Complete document lifecycle with proper signers
java -jar blockchain-cli.jar add-block "CREATED: Contract_2025-ABC.pdf | Author: Alice Smith | Size: 245KB | Version: 1.0" --signer Document-Author
java -jar blockchain-cli.jar add-block "REVIEWED: Contract_2025-ABC.pdf | Reviewer: Legal Team | Status: Minor changes required | Comments: 3" --signer Legal-Reviewer
java -jar blockchain-cli.jar add-block "REVISED: Contract_2025-ABC.pdf | Author: Alice Smith | Version: 1.1 | Changes: Addressed legal comments" --signer Document-Author
java -jar blockchain-cli.jar add-block "APPROVED: Contract_2025-ABC.pdf | Approver: John Manager | Final Status: Approved for signature" --signer Final-Approver
java -jar blockchain-cli.jar add-block "SIGNED: Contract_2025-ABC.pdf | Signatory: Bob Client | Digital signature verified | Timestamp: $(date)" --signer Final-Approver

# Generate comprehensive audit report
java -jar blockchain-cli.jar search "Contract_2025-ABC.pdf" --detailed > audit_reports/contract_abc_audit.txt
java -jar blockchain-cli.jar export audit_reports/contract_abc_full_$(date +%Y%m%d).json
```

### Use Case 2: Supply Chain with Multi-Party Verification
```zsh
# Setup supply chain participants with specific roles
java -jar blockchain-cli.jar add-key "Manufacturer-QC" --generate
java -jar blockchain-cli.jar add-key "Logistics-Coordinator" --generate  
java -jar blockchain-cli.jar add-key "Warehouse-Supervisor" --generate
java -jar blockchain-cli.jar add-key "Retail-Manager" --generate
java -jar blockchain-cli.jar add-key "Quality-Inspector" --generate

# Complete product journey with verification at each step
java -jar blockchain-cli.jar add-block "MANUFACTURED: Product SKU-12345 | Batch: B2025-06-001 | QC Inspector: Mike | Quality Grade: A" --signer Manufacturer-QC
java -jar blockchain-cli.jar add-block "DISPATCHED: Product SKU-12345 | Carrier: FastShip Express | Tracking: FS123456789 | Expected delivery: 2025-06-14" --signer Logistics-Coordinator
java -jar blockchain-cli.jar add-block "IN-TRANSIT: Product SKU-12345 | Checkpoint: City Hub | Temperature: 22°C | Condition: Good" --signer Logistics-Coordinator
java -jar blockchain-cli.jar add-block "RECEIVED: Product SKU-12345 | Warehouse: Central-001 | Condition: Excellent | Inspector: Sarah | Storage location: A-15-C" --signer Warehouse-Supervisor
java -jar blockchain-cli.jar add-block "QUALITY-CHECK: Product SKU-12345 | Inspector: QC Team | Random sample test: Passed | Compliance: FDA approved" --signer Quality-Inspector
java -jar blockchain-cli.jar add-block "SOLD: Product SKU-12345 | Store: TechMart Downtown | Customer: [PRIVACY-PROTECTED] | Price: $299.99 | Warranty: 2 years" --signer Retail-Manager

# Track complete product lifecycle
java -jar blockchain-cli.jar search "SKU-12345" --json > supply_chain/product_12345_complete_history.json

# Generate compliance report for auditors
java -jar blockchain-cli.jar search "QUALITY-CHECK\|QC Inspector" --detailed > compliance_reports/qc_audit_$(date +%Y%m%d).txt
```

### Use Case 3: Corporate Governance with Board Meeting Records
```zsh
# Setup corporate governance participants
java -jar blockchain-cli.jar add-key "Board-Secretary" --generate
java -jar blockchain-cli.jar add-key "Board-Chairman" --generate
java -jar blockchain-cli.jar add-key "Chief-Executive" --generate
java -jar blockchain-cli.jar add-key "Compliance-Officer" --generate

# Complete board meeting lifecycle
java -jar blockchain-cli.jar add-block "MEETING-NOTICE: Board Meeting scheduled for 2025-06-15 14:00 UTC | Agenda items: 5 | Notice period: 14 days" --signer Board-Secretary
java -jar blockchain-cli.jar add-block "MEETING-START: Board Meeting commenced | Attendees: 8/10 directors present | Quorum: Achieved | Chairman: John Smith" --signer Board-Chairman
java -jar blockchain-cli.jar add-block "MOTION-01: Approve Q2 financial results | Proposer: CFO | Seconder: COO | Discussion: 15 minutes" --signer Board-Secretary
java -jar blockchain-cli.jar add-block "VOTE-01: Q2 financial results approved | Vote: 7 in favor, 1 abstention | Motion: PASSED" --signer Board-Chairman
java -jar blockchain-cli.jar add-block "MOTION-02: CEO compensation adjustment | Proposer: Chairman | Seconder: Independent Director | Discussion: 25 minutes" --signer Board-Secretary
java -jar blockchain-cli.jar add-block "VOTE-02: CEO compensation approved | Vote: 6 in favor, 2 against | Motion: PASSED | Effective: Q3 2025" --signer Board-Chairman
java -jar blockchain-cli.jar add-block "COMPLIANCE-NOTE: All decisions comply with corporate governance guidelines | Reviewed by: Legal Counsel | Filed: SEC Form 8-K" --signer Compliance-Officer
java -jar blockchain-cli.jar add-block "MEETING-END: Board Meeting concluded | Duration: 2.5 hours | Next meeting: 2025-09-15 | Minutes: Filed" --signer Board-Secretary

# Generate meeting minutes and governance reports
java -jar blockchain-cli.jar search --date-from $(date +%Y-%m-%d) --detailed > governance/board_meeting_$(date +%Y%m%d)_minutes.txt
java -jar blockchain-cli.jar export governance/board_meeting_$(date +%Y%m%d)_complete.json
```

### Use Case 4: Software Development Lifecycle with DevOps Pipeline
```zsh
# Setup development team with specialized roles
java -jar blockchain-cli.jar add-key "Feature-Developer" --generate
java -jar blockchain-cli.jar add-key "Code-Reviewer" --generate
java -jar blockchain-cli.jar add-key "QA-Automation" --generate
java -jar blockchain-cli.jar add-key "Security-Tester" --generate
java -jar blockchain-cli.jar add-key "Release-Manager" --generate
java -jar blockchain-cli.jar add-key "Production-Ops" --generate

# Complete feature development and deployment cycle
java -jar blockchain-cli.jar add-block "FEATURE-START: User authentication with 2FA | Developer: Alice | Branch: feature/2fa-auth | Estimated effort: 3 days" --signer Feature-Developer
java -jar blockchain-cli.jar add-block "CODE-COMPLETE: Feature implementation finished | Files changed: 12 | Lines added: 847 | Unit tests: 23 added" --signer Feature-Developer
java -jar blockchain-cli.jar add-block "CODE-REVIEW: Pull request reviewed | Reviewer: Bob | Comments: 5 | Status: Approved with minor changes" --signer Code-Reviewer
java -jar blockchain-cli.jar add-block "AUTOMATED-TESTS: CI pipeline completed | Unit tests: 156/156 passed | Integration tests: 89/89 passed | Coverage: 94.2%" --signer QA-Automation
java -jar blockchain-cli.jar add-block "SECURITY-SCAN: Security tests completed | Vulnerabilities: 0 critical, 1 medium (fixed) | OWASP compliance: Verified" --signer Security-Tester
java -jar blockchain-cli.jar add-block "STAGING-DEPLOY: Feature deployed to staging | Environment: staging-v2.1.0 | Smoke tests: Passed | Performance: Baseline" --signer Release-Manager
java -jar blockchain-cli.jar add-block "PRODUCTION-DEPLOY: Feature deployed to production | Version: v2.1.0 | Rollout: Gradual (10% traffic) | Monitoring: Active" --signer Production-Ops
java -jar blockchain-cli.jar add-block "RELEASE-COMPLETE: 2FA authentication feature live | User adoption: 15% in first 24h | Issues: 0 | Performance impact: <2ms" --signer Production-Ops

# Generate release audit trail and performance reports
java -jar blockchain-cli.jar search "2FA\|authentication" --detailed > releases/feature_2fa_complete_audit.txt
java -jar blockchain-cli.jar search "PRODUCTION-DEPLOY\|RELEASE-COMPLETE" --json > releases/production_deployments.json
```

## 🔧 Advanced Scenarios

### Scenario 1: Backup & Recovery Workflow
```zsh
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

### Scenario 2: Security Incident Response
```zsh
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

### Scenario 3: Data Migration
```zsh
# Export from old system
java -jar blockchain-cli.jar export migration/old_system_$(date +%Y%m%d).json

# Import to new system (with backup)
java -jar blockchain-cli.jar import migration/old_system_*.json --backup --validate-after

# Verify migration integrity
java -jar blockchain-cli.jar validate --detailed
java -jar blockchain-cli.jar search --date-from 2025-01-01 --limit 100 > migration_verification.txt
```

### Scenario 4: Compliance Reporting
```zsh
# Generate monthly compliance report
java -jar blockchain-cli.jar search --date-from $(date -d "1 month ago" +%Y-%m-%d) --date-to $(date +%Y-%m-%d) --json > compliance/monthly_$(date +%Y_%m).json

# Validate chain integrity for auditors
java -jar blockchain-cli.jar validate --detailed > compliance/chain_integrity_$(date +%Y%m%d).txt

# Export complete blockchain for regulatory submission
java -jar blockchain-cli.jar export compliance/full_blockchain_$(date +%Y-%m-%d).json
```

## 🛠 Docker Examples with --signer

### Docker Quick Start with Multi-User Setup
```zsh
# Build once, use everywhere
docker build -t blockchain-cli .

# Setup persistent data volume
mkdir -p blockchain-data backups

# Initialize blockchain and create signers
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli status
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-key "Docker-Admin" --generate
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-key "Docker-User" --generate
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-key "Docker-Service" --generate

# Verify signers were created
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli list-keys --detailed

# Add blocks using different signers
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "Admin: System initialized" --signer Docker-Admin
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "User: First user transaction" --signer Docker-User
docker run --rm -v $(pwd)/blockchain-data:/data blockchain-cli add-block "Service: Automated process completed" --signer Docker-Service

# Backup with dual volumes
docker run --rm -v $(pwd)/blockchain-data:/data -v $(pwd)/backups:/backups blockchain-cli export /backups/docker_backup_$(date +%Y%m%d).json
```

### Docker Compose with Role-Based Workflow
```yaml
# docker-compose.yml
version: '3.8'
services:
  blockchain-admin:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    command: ["add-block", "Admin operation: Daily system check", "--signer", "Docker-Admin"]
    profiles: ["admin"]

  blockchain-user:
    build: .
    volumes:
      - ./blockchain-data:/data
    command: ["add-block", "User transaction: Data processing completed", "--signer", "Docker-User"]
    profiles: ["user"]

  blockchain-backup:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    command: ["export", "/backups/automated_backup_$(date +%Y%m%d_%H%M%S).json"]
    profiles: ["backup"]

  blockchain-status:
    build: .
    volumes:
      - ./blockchain-data:/data
    command: ["status", "--detailed"]
    profiles: ["default", "status"]
```

**Usage:**
```zsh
# Run as admin
docker-compose --profile admin up

# Run as user
docker-compose --profile user up

# Automated daily backup
docker-compose --profile backup up

# Status monitoring
docker-compose --profile status up
```

### Docker Automation Script with Error Handling
```zsh
#!/usr/bin/env zsh
# save as: docker_blockchain_operations.sh

DOCKER_IMAGE="blockchain-cli"
DATA_VOLUME="$(pwd)/blockchain-data"
BACKUP_VOLUME="$(pwd)/backups"

# Function to run blockchain command with proper volumes
run_blockchain() {
    docker run --rm \
        -v "$DATA_VOLUME:/data" \
        -v "$BACKUP_VOLUME:/backups" \
        "$DOCKER_IMAGE" "$@"
}

# Function to add block with signer, creating signer if needed
add_block_with_signer() {
    local data="$1"
    local signer="$2"
    
    echo "📝 Adding block with signer: $signer"
    
    # Check if signer exists
    if run_blockchain list-keys | grep -q "$signer"; then
        echo "✅ Using existing signer: $signer"
    else
        echo "🔑 Creating new signer: $signer"
        run_blockchain add-key "$signer" --generate
    fi
    
    # Add the block
    run_blockchain add-block "$data" --signer "$signer"
}

# Setup function
setup_blockchain() {
    echo "🚀 Setting up blockchain with Docker..."
    
    # Create directories
    mkdir -p "$DATA_VOLUME" "$BACKUP_VOLUME"
    
    # Initialize blockchain
    run_blockchain status
    
    # Create initial signers
    add_block_with_signer "System initialization completed" "System-Admin"
    add_block_with_signer "Docker environment ready" "Docker-Service"
    
    echo "✅ Blockchain setup complete!"
}

# Daily operations function
daily_operations() {
    echo "📅 Running daily operations..."
    
    DATE=$(date +%Y-%m-%d)
    
    # Add daily entries
    add_block_with_signer "Daily health check: All systems operational | Date: $DATE" "System-Monitor"
    add_block_with_signer "Backup process initiated | Date: $DATE" "Backup-Service"
    
    # Create backup
    run_blockchain export "/backups/daily_backup_$(date +%Y%m%d).json"
    
    # Validate
    run_blockchain validate --detailed
    
    echo "✅ Daily operations complete!"
}

# Usage
case "${1:-help}" in
    setup)
        setup_blockchain
        ;;
    daily)
        daily_operations
        ;;
    status)
        run_blockchain status --detailed
        ;;
    backup)
        run_blockchain export "/backups/manual_backup_$(date +%Y%m%d_%H%M%S).json"
        ;;
    help|*)
        echo "Usage: $0 {setup|daily|status|backup}"
        echo "  setup  - Initialize blockchain with signers"
        echo "  daily  - Run daily operations and backup"
        echo "  status - Show blockchain status"
        echo "  backup - Create manual backup"
        ;;
esac
```

**Usage:**
```zsh
chmod +x docker_blockchain_operations.sh

# Initial setup
./docker_blockchain_operations.sh setup

# Daily operations
./docker_blockchain_operations.sh daily

# Check status
./docker_blockchain_operations.sh status

# Manual backup
./docker_blockchain_operations.sh backup
```

For comprehensive Docker usage, see [DOCKER_GUIDE.md](DOCKER_GUIDE.md).

## 📊 Automation Scripts

### Daily Backup Script
```zsh
#!/usr/bin/env zsh
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

### Health Check Script
```zsh
#!/usr/bin/env zsh
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

For more automation scripts, see [AUTOMATION_SCRIPTS.md](AUTOMATION_SCRIPTS.md).

## 🎬 Demo Scripts

The CLI includes several demo scripts to showcase the enhanced functionality.

### Quick Demo Script
```zsh
# Run simple enhanced CLI demos (recommended for quick testing)
./test-simple-demos.zsh

# Features demonstrated:
# - Basic blockchain operations
# - Enhanced search functionality
# - Keywords and categories
# - Simple off-chain data handling
```

### Interactive CLI Demos
```zsh
# Run comprehensive interactive demonstrations (full feature showcase)
./run-enhanced-demos.zsh

# Features demonstrated:
# - Off-chain storage with encryption
# - Hybrid search with performance comparison
# - CLI integration examples
# - Real-time keyword processing
# - Large data handling
```

### Java Demo Classes
```zsh
# Run the official Java demo classes
./run-java-demos.zsh

# Executes:
# - OffChainStorageDemo: Complete off-chain storage demonstration
# - HybridSearchDemo: Comprehensive search functionality showcase
```

### Enhanced Test Suite
```zsh
# Run complete test suite including new features
./test-cli.sh

# Skip unit tests for faster integration testing
SKIP_UNIT_TESTS=true ./test-cli.sh

# Run with enhanced security tests
FULL_SECURE_TESTS=true ./test-cli.sh
```

### Demo Script Features
- 🎨 **Colorful Output**: Visual feedback with emojis and colors
- ⏸️ **Interactive Pauses**: Follow along at your own pace
- ⏱️ **Performance Timing**: See real execution times
- 📊 **Comprehensive Coverage**: All features demonstrated
- 🔄 **Automated Setup**: No manual configuration required

### Demo Data Examples
The demos create realistic test data:

```zsh
# Medical records with off-chain storage
"Comprehensive medical report for PATIENT-001: ECG analysis shows normal sinus rhythm..."

# Financial batch processing
"Financial Batch Processing Report Q1 2024: 5000 transactions totaling $12.5M..."

# Legal contracts with keyword extraction
"Partnership Agreement between Company A and Company B regarding IP rights..."
```

### Script Documentation
See [DEMO_SCRIPTS.md](../DEMO_SCRIPTS.md) for detailed information about:
- Script descriptions and usage
- When to use each script
- Configuration options
- Troubleshooting guides

## 🔗 Related Documents

- [Main README](README.md) - Getting started and basic usage
- [Docker Guide](DOCKER_GUIDE.md) - Complete Docker integration
- [Enterprise Guide](ENTERPRISE_GUIDE.md) - Best practices for business use
- [Troubleshooting](TROUBLESHOOTING.md) - Problem resolution
- [Integration Patterns](INTEGRATION_PATTERNS.md) - External system integrations
- [Automation Scripts](AUTOMATION_SCRIPTS.md) - Ready-to-use automation

---

**Need help?** Check the [Troubleshooting Guide](TROUBLESHOOTING.md) or return to the [main README](README.md).
