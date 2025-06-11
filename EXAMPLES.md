# 💡 Blockchain CLI Examples

Comprehensive examples and use cases for the Private Blockchain CLI.

## 📋 Table of Contents

- [Quick Start Examples](#-quick-start-examples)
- [Real-World Use Cases](#-real-world-use-cases)
- [Advanced Scenarios](#-advanced-scenarios)
- [Docker Examples](#-docker-examples)
- [Automation Scripts](#-automation-scripts)

## 🚀 Quick Start Examples

### Example 1: First-Time Setup
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

### Example 2: Daily Operations
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

### Example 3: Multi-User Setup
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

## 🎯 Real-World Use Cases

### Use Case 1: Document Audit Trail
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

### Use Case 2: Supply Chain Tracking
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

### Use Case 3: Meeting Minutes & Decisions
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

### Use Case 4: Software Release Pipeline
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

## 🔧 Advanced Scenarios

### Scenario 1: Backup & Recovery Workflow
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

### Scenario 2: Security Incident Response
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

### Scenario 3: Data Migration
```bash
# Export from old system
java -jar blockchain-cli.jar export migration/old_system_$(date +%Y%m%d).json

# Import to new system (with backup)
java -jar blockchain-cli.jar import migration/old_system_*.json --backup --validate-after

# Verify migration integrity
java -jar blockchain-cli.jar validate --detailed
java -jar blockchain-cli.jar search --date-from 2025-01-01 --limit 100 > migration_verification.txt
```

### Scenario 4: Compliance Reporting
```bash
# Generate monthly compliance report
java -jar blockchain-cli.jar search --date-from $(date -d "1 month ago" +%Y-%m-%d) --date-to $(date +%Y-%m-%d) --json > compliance/monthly_$(date +%Y_%m).json

# Validate chain integrity for auditors
java -jar blockchain-cli.jar validate --detailed > compliance/chain_integrity_$(date +%Y%m%d).txt

# Export complete blockchain for regulatory submission
java -jar blockchain-cli.jar export compliance/full_blockchain_$(date +%Y-%m-%d).json
```

## 🛠 Docker Examples

### Docker Quick Start
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

### Docker Compose Automation
```bash
# Automated daily backup
docker-compose --profile backup up

# Chain validation
docker-compose --profile validate up

# Status monitoring
docker-compose --profile default up
```

For comprehensive Docker usage, see [DOCKER_GUIDE.md](DOCKER_GUIDE.md).

## 📊 Automation Scripts

### Daily Backup Script
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

### Health Check Script
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

For more automation scripts, see [AUTOMATION_SCRIPTS.md](AUTOMATION_SCRIPTS.md).

## 🔗 Related Documents

- [Main README](README.md) - Getting started and basic usage
- [Docker Guide](DOCKER_GUIDE.md) - Complete Docker integration
- [Enterprise Guide](ENTERPRISE_GUIDE.md) - Best practices for business use
- [Troubleshooting](TROUBLESHOOTING.md) - Problem resolution
- [Integration Patterns](INTEGRATION_PATTERNS.md) - External system integrations
- [Automation Scripts](AUTOMATION_SCRIPTS.md) - Ready-to-use automation

---

**Need help?** Check the [Troubleshooting Guide](TROUBLESHOOTING.md) or return to the [main README](README.md).
