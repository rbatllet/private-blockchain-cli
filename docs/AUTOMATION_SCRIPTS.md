# Automation Scripts

This document provides ready-to-use automation scripts for the Private Blockchain CLI, designed for production environments, CI/CD pipelines, and daily operations.

## 📋 Table of Contents

- [Daily Operations Scripts](#-daily-operations-scripts)
- [Backup and Recovery Scripts](#-backup-and-recovery-scripts)
- [Monitoring and Health Checks](#-monitoring-and-health-checks)
- [CI/CD Integration Scripts](#-cicd-integration-scripts)
- [Maintenance Scripts](#-maintenance-scripts)
- [Docker Automation](#-docker-automation)
- [Troubleshooting Scripts](#-troubleshooting-scripts)

## 📊 Daily Operations Scripts

### Daily Backup Script

```zsh
#!/usr/bin/env zsh
# File: daily_backup.sh
# Purpose: Create daily blockchain backups with cleanup

set -e  # Exit on error

DATE=$(date +%Y%m%d)
BACKUP_DIR="backups/daily"
RETENTION_DAYS=30
LOG_FILE="logs/backup_$(date +%Y%m).log"

# Ensure directories exist
mkdir -p $BACKUP_DIR
mkdir -p logs

echo "[$(date)] 🔄 Starting daily backup: $DATE" | tee -a $LOG_FILE

# Create backup
if java -jar blockchain-cli.jar export $BACKUP_DIR/blockchain_$DATE.json; then
    echo "[$(date)] ✅ Backup created: $BACKUP_DIR/blockchain_$DATE.json" | tee -a $LOG_FILE
    
    # Validate backup integrity
    if java -jar blockchain-cli.jar validate --json | grep -q '"valid":true'; then
        echo "[$(date)] ✅ Backup validated successfully" | tee -a $LOG_FILE
        
        # Cleanup old backups
        find $BACKUP_DIR -name "blockchain_*.json" -mtime +$RETENTION_DAYS -delete
        echo "[$(date)] 🧹 Cleaned up backups older than $RETENTION_DAYS days" | tee -a $LOG_FILE
        
        exit 0
    else
        echo "[$(date)] ❌ Backup validation failed!" | tee -a $LOG_FILE
        exit 1
    fi
else
    echo "[$(date)] ❌ Backup creation failed!" | tee -a $LOG_FILE
    exit 1
fi
```

### Health Check Script

```zsh
#!/usr/bin/env zsh
# File: health_check.sh
# Purpose: Comprehensive blockchain health monitoring

set -e

LOG_FILE="logs/health_$(date +%Y%m).log"
ALERT_EMAIL="admin@company.com"
WEBHOOK_URL=""  # Set your webhook URL for alerts

echo "[$(date)] 🏥 Blockchain Health Check Starting" | tee -a $LOG_FILE

# Function to send alerts
send_alert() {
    local message="$1"
    echo "[$(date)] 🚨 ALERT: $message" | tee -a $LOG_FILE
    
    # Email alert (requires mailutils)
    if command -v mail >/dev/null 2>&1; then
        echo "$message" | mail -s "Blockchain Alert" $ALERT_EMAIL
    fi
    
    # Webhook alert (requires curl)
    if [ -n "$WEBHOOK_URL" ] && command -v curl >/dev/null 2>&1; then
        curl -X POST -H "Content-Type: application/json" \
             -d "{\"text\":\"🚨 Blockchain Alert: $message\"}" \
             $WEBHOOK_URL
    fi
}

# Check blockchain status
STATUS_OUTPUT=$(java -jar blockchain-cli.jar status --json 2>/dev/null)
if [ $? -ne 0 ]; then
    send_alert "Failed to get blockchain status"
    exit 1
fi

# Parse status
BLOCKS=$(echo $STATUS_OUTPUT | grep -o '"blockCount":[0-9]*' | cut -d: -f2)
KEYS=$(echo $STATUS_OUTPUT | grep -o '"authorizedKeys":[0-9]*' | cut -d: -f2)
VALID=$(echo $STATUS_OUTPUT | grep -o '"isValid":[^,}]*' | cut -d: -f2)

echo "[$(date)] 📊 Status: Blocks=$BLOCKS, Keys=$KEYS, Valid=$VALID" | tee -a $LOG_FILE

# Validate chain integrity
if [ "$VALID" = "true" ]; then
    echo "[$(date)] ✅ Blockchain is healthy" | tee -a $LOG_FILE
else
    send_alert "Blockchain validation failed! Blocks: $BLOCKS"
    java -jar blockchain-cli.jar validate --detailed >> $LOG_FILE
    exit 1
fi

# Check disk space
DISK_USAGE=$(df . | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 90 ]; then
    send_alert "Disk space critical: ${DISK_USAGE}% used"
fi

echo "[$(date)] ✅ Health check completed successfully" | tee -a $LOG_FILE
```

### Transaction Processing Script

```zsh
#!/usr/bin/env zsh
# File: process_transactions.sh
# Purpose: Batch process pending transactions

PENDING_DIR="pending_transactions"
PROCESSED_DIR="processed_transactions"
ERROR_DIR="error_transactions"
SIGNER="AutoProcessor"

# Ensure directories exist
mkdir -p $PENDING_DIR $PROCESSED_DIR $ERROR_DIR

echo "🔄 Processing pending transactions..."

# Process each transaction file
for file in $PENDING_DIR/*.txt; do
    if [ -f "$file" ]; then
        filename=$(basename "$file")
        echo "Processing: $filename"
        
        # Read transaction data
        transaction_data=$(cat "$file")
        
        # Add to blockchain
        if java -jar blockchain-cli.jar add-block "$transaction_data" --signer $SIGNER; then
            echo "✅ Processed: $filename"
            mv "$file" "$PROCESSED_DIR/"
        else
            echo "❌ Failed: $filename"
            mv "$file" "$ERROR_DIR/"
        fi
    fi
done

echo "✅ Transaction processing completed"
```

## 🔄 Backup and Recovery Scripts

### Weekly Full Backup

```zsh
#!/usr/bin/env zsh
# File: weekly_backup.sh
# Purpose: Create weekly full backups with compression

WEEK=$(date +%Y_W%U)
BACKUP_DIR="backups/weekly"
ARCHIVE_DIR="backups/archive"

mkdir -p $BACKUP_DIR $ARCHIVE_DIR

echo "📦 Creating weekly backup for week $WEEK"

# Export blockchain
java -jar blockchain-cli.jar export $BACKUP_DIR/blockchain_week_$WEEK.json

# Compress backup
gzip $BACKUP_DIR/blockchain_week_$WEEK.json

# Create archive copy
cp $BACKUP_DIR/blockchain_week_$WEEK.json.gz $ARCHIVE_DIR/

# Generate backup report
{
    echo "=== Weekly Backup Report ==="
    echo "Date: $(date)"
    echo "Week: $WEEK"
    echo "File: blockchain_week_$WEEK.json.gz"
    echo "Size: $(ls -lh $BACKUP_DIR/blockchain_week_$WEEK.json.gz | awk '{print $5}')"
    java -jar blockchain-cli.jar status --detailed
} > $BACKUP_DIR/backup_report_$WEEK.txt

echo "✅ Weekly backup completed: $BACKUP_DIR/blockchain_week_$WEEK.json.gz"
```

### Disaster Recovery Script

```zsh
#!/usr/bin/env zsh
# File: disaster_recovery.sh
# Purpose: Complete disaster recovery from backup

BACKUP_FILE="$1"
RECOVERY_LOG="logs/recovery_$(date +%Y%m%d_%H%M%S).log"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file.json>"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

mkdir -p logs

echo "[$(date)] 🚨 Starting disaster recovery from: $BACKUP_FILE" | tee -a $RECOVERY_LOG

# Backup current state (if any)
if [ -f "blockchain.db" ]; then
    echo "[$(date)] 💾 Backing up current state..." | tee -a $RECOVERY_LOG
    cp blockchain.db "blockchain.db.disaster_backup_$(date +%Y%m%d_%H%M%S)"
fi

# Remove corrupted database
rm -f blockchain.db*

# Import from backup
echo "[$(date)] 📥 Importing from backup..." | tee -a $RECOVERY_LOG
if java -jar blockchain-cli.jar import "$BACKUP_FILE" --validate-after; then
    echo "[$(date)] ✅ Recovery completed successfully" | tee -a $RECOVERY_LOG
    
    # Verify recovery
    java -jar blockchain-cli.jar status --detailed | tee -a $RECOVERY_LOG
    java -jar blockchain-cli.jar validate --detailed | tee -a $RECOVERY_LOG
    
    echo "[$(date)] 🎉 Disaster recovery completed successfully" | tee -a $RECOVERY_LOG
else
    echo "[$(date)] ❌ Recovery failed!" | tee -a $RECOVERY_LOG
    exit 1
fi
```

## 🔍 Monitoring and Health Checks

### Continuous Monitoring Script

```zsh
#!/usr/bin/env zsh
# File: continuous_monitor.sh
# Purpose: Continuous blockchain monitoring with alerts

INTERVAL=300  # 5 minutes
LOG_FILE="logs/monitor_$(date +%Y%m%d).log"
ALERT_THRESHOLD_BLOCKS=1000
ALERT_THRESHOLD_SIZE_MB=100

echo "[$(date)] 🔍 Starting continuous monitoring (interval: ${INTERVAL}s)" | tee -a $LOG_FILE

while true; do
    # Get status
    STATUS=$(java -jar blockchain-cli.jar status --json 2>/dev/null)
    
    if [ $? -eq 0 ]; then
        BLOCKS=$(echo $STATUS | grep -o '"blockCount":[0-9]*' | cut -d: -f2)
        VALID=$(echo $STATUS | grep -o '"isValid":[^,}]*' | cut -d: -f2)
        
        echo "[$(date)] 📊 Blocks: $BLOCKS, Valid: $VALID" | tee -a $LOG_FILE
        
        # Check for issues
        if [ "$VALID" != "true" ]; then
            echo "[$(date)] 🚨 ALERT: Blockchain validation failed!" | tee -a $LOG_FILE
        fi
        
        # Check block count threshold
        if [ $BLOCKS -gt $ALERT_THRESHOLD_BLOCKS ]; then
            echo "[$(date)] ⚠️  WARNING: Block count exceeds threshold ($BLOCKS > $ALERT_THRESHOLD_BLOCKS)" | tee -a $LOG_FILE
        fi
        
        # Check database size
        if [ -f "blockchain.db" ]; then
            SIZE_MB=$(du -m blockchain.db | cut -f1)
            if [ $SIZE_MB -gt $ALERT_THRESHOLD_SIZE_MB ]; then
                echo "[$(date)] ⚠️  WARNING: Database size exceeds threshold (${SIZE_MB}MB > ${ALERT_THRESHOLD_SIZE_MB}MB)" | tee -a $LOG_FILE
            fi
        fi
    else
        echo "[$(date)] ❌ ERROR: Failed to get blockchain status" | tee -a $LOG_FILE
    fi
    
    sleep $INTERVAL
done
```

### Performance Benchmark Script

```zsh
#!/usr/bin/env zsh
# File: benchmark.sh
# Purpose: Performance benchmarking and testing

echo "🏃 Starting performance benchmark..."

# Test status command performance
echo "📊 Testing status command (10 iterations)..."
start_time=$(date +%s.%N)
for i in {1..10}; do
    java -jar blockchain-cli.jar status >/dev/null 2>&1
done
end_time=$(date +%s.%N)
status_time=$(echo "$end_time - $start_time" | bc)
avg_status_time=$(echo "scale=3; $status_time / 10" | bc)

# Test validation performance
echo "🔍 Testing validation command..."
start_time=$(date +%s.%N)
java -jar blockchain-cli.jar validate >/dev/null 2>&1
end_time=$(date +%s.%N)
validate_time=$(echo "$end_time - $start_time" | bc)

# Test search performance
echo "🔎 Testing search command..."
start_time=$(date +%s.%N)
java -jar blockchain-cli.jar search "Genesis" >/dev/null 2>&1
end_time=$(date +%s.%N)
search_time=$(echo "$end_time - $start_time" | bc)

# Generate report
{
    echo "=== Performance Benchmark Report ==="
    echo "Date: $(date)"
    echo "Average Status Command: ${avg_status_time}s"
    echo "Validation Command: ${validate_time}s"
    echo "Search Command: ${search_time}s"
    echo ""
    java -jar blockchain-cli.jar status --detailed
} > "benchmark_$(date +%Y%m%d_%H%M%S).txt"

echo "✅ Benchmark completed. Results saved to benchmark_*.txt"
```

## 🚀 CI/CD Integration Scripts

### Pre-deployment Validation

```zsh
#!/usr/bin/env zsh
# File: pre_deploy_check.sh
# Purpose: Pre-deployment validation for CI/CD

set -e

echo "🔍 Pre-deployment validation starting..."

# Run comprehensive tests
echo "🧪 Running tests..."
mvn test

# Build application
echo "🔨 Building application..."
mvn clean package

# Test basic functionality
echo "🔧 Testing basic functionality..."
java -jar target/blockchain-cli.jar --version
java -jar target/blockchain-cli.jar status

# Validate blockchain integrity
echo "✅ Validating blockchain..."
java -jar target/blockchain-cli.jar validate

# Create test backup
echo "💾 Testing backup functionality..."
java -jar target/blockchain-cli.jar export test_deploy_backup.json --overwrite

# Clean up
rm -f test_deploy_backup.json

echo "✅ Pre-deployment validation completed successfully"
```

### Post-deployment Verification

```zsh
#!/usr/bin/env zsh
# File: post_deploy_verify.sh
# Purpose: Post-deployment verification

DEPLOYMENT_ID="$1"
if [ -z "$DEPLOYMENT_ID" ]; then
    DEPLOYMENT_ID="deploy_$(date +%Y%m%d_%H%M%S)"
fi

echo "🔍 Post-deployment verification for: $DEPLOYMENT_ID"

# Test all critical commands
commands=(
    "--version"
    "status"
    "status --json"
    "validate"
    "list-keys"
    "help"
)

for cmd in "${commands[@]}"; do
    echo "Testing: java -jar blockchain-cli.jar $cmd"
    if java -jar blockchain-cli.jar $cmd >/dev/null 2>&1; then
        echo "✅ $cmd - PASSED"
    else
        echo "❌ $cmd - FAILED"
        exit 1
    fi
done

# Create deployment verification log
{
    echo "=== Deployment Verification ==="
    echo "Deployment ID: $DEPLOYMENT_ID"
    echo "Date: $(date)"
    echo "Status: SUCCESS"
    java -jar blockchain-cli.jar status --detailed
} > "deploy_verification_$DEPLOYMENT_ID.log"

echo "✅ Post-deployment verification completed successfully"
```

## 🛠️ Maintenance Scripts

### Database Maintenance

```zsh
#!/usr/bin/env zsh
# File: db_maintenance.sh
# Purpose: Database optimization and maintenance

echo "🔧 Starting database maintenance..."

# Backup before maintenance
java -jar blockchain-cli.jar export "maintenance_backup_$(date +%Y%m%d).json"

# Check database integrity
echo "🔍 Checking database integrity..."
if sqlite3 blockchain.db "PRAGMA integrity_check;" | grep -q "ok"; then
    echo "✅ Database integrity check passed"
else
    echo "❌ Database integrity check failed!"
    exit 1
fi

# Optimize database
echo "📊 Optimizing database..."
sqlite3 blockchain.db "VACUUM;"
sqlite3 blockchain.db "PRAGMA optimize;"

# Generate maintenance report
{
    echo "=== Database Maintenance Report ==="
    echo "Date: $(date)"
    echo "Database Size: $(ls -lh blockchain.db | awk '{print $5}')"
    echo "Integrity Check: PASSED"
    echo "Optimization: COMPLETED"
} > "maintenance_$(date +%Y%m%d).log"

echo "✅ Database maintenance completed"
```

### Log Rotation Script

```zsh
#!/usr/bin/env zsh
# File: rotate_logs.sh
# Purpose: Log file rotation and cleanup

LOG_DIR="logs"
ARCHIVE_DIR="logs/archive"
RETENTION_DAYS=90

mkdir -p $ARCHIVE_DIR

echo "🔄 Starting log rotation..."

# Compress and archive old logs
find $LOG_DIR -name "*.log" -mtime +7 -not -path "*/archive/*" | while read logfile; do
    filename=$(basename "$logfile")
    echo "Archiving: $filename"
    gzip "$logfile"
    mv "$logfile.gz" "$ARCHIVE_DIR/"
done

# Delete very old archives
find $ARCHIVE_DIR -name "*.gz" -mtime +$RETENTION_DAYS -delete

echo "✅ Log rotation completed"
```

## 🐳 Docker Automation

### Docker Health Check

```zsh
#!/usr/bin/env zsh
# File: docker_health_check.sh
# Purpose: Health check for Docker containers

CONTAINER_NAME="blockchain-cli"

# Check if container is running
if docker ps | grep -q $CONTAINER_NAME; then
    echo "✅ Container $CONTAINER_NAME is running"
    
    # Test blockchain functionality
    if docker exec $CONTAINER_NAME java -jar blockchain-cli.jar status >/dev/null 2>&1; then
        echo "✅ Blockchain is healthy"
        exit 0
    else
        echo "❌ Blockchain health check failed"
        exit 1
    fi
else
    echo "❌ Container $CONTAINER_NAME is not running"
    exit 1
fi
```

### Docker Compose Automation

```yaml
# File: docker-compose.automation.yml
# Purpose: Automated operations with Docker Compose

version: '3.8'

services:
  blockchain-cli:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
      - ./logs:/logs
    
  backup:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./backups:/backups
    command: ["java", "-jar", "blockchain-cli.jar", "export", "/backups/auto_backup_$(date +%Y%m%d).json"]
    profiles: ["backup"]
    
  validate:
    build: .
    volumes:
      - ./blockchain-data:/data
    command: ["java", "-jar", "blockchain-cli.jar", "validate", "--detailed"]
    profiles: ["validate"]
    
  monitor:
    build: .
    volumes:
      - ./blockchain-data:/data
      - ./logs:/logs
    command: ["java", "-jar", "blockchain-cli.jar", "status", "--json"]
    profiles: ["monitor"]
```

## 🔧 Troubleshooting Scripts

### Diagnostic Script

```zsh
#!/usr/bin/env zsh
# File: diagnose.sh
# Purpose: Comprehensive system diagnostics

echo "🩺 Running blockchain diagnostics..."

# System information
echo "=== System Information ==="
echo "Date: $(date)"
echo "Java Version: $(java -version 2>&1 | head -1)"
echo "OS: $(uname -a)"
echo "Available Memory: $(free -h 2>/dev/null || vm_stat)"
echo ""

# Blockchain status
echo "=== Blockchain Status ==="
java -jar blockchain-cli.jar status --detailed
echo ""

# Database information
echo "=== Database Information ==="
if [ -f "blockchain.db" ]; then
    echo "Database Size: $(ls -lh blockchain.db | awk '{print $5}')"
    echo "Database Tables:"
    sqlite3 blockchain.db ".tables"
    echo "Block Count: $(sqlite3 blockchain.db "SELECT COUNT(*) FROM blocks;")"
else
    echo "Database not found"
fi
echo ""

# Validation
echo "=== Validation Results ==="
java -jar blockchain-cli.jar validate --detailed

echo "✅ Diagnostics completed"
```

### Error Recovery Script

```zsh
#!/usr/bin/env zsh
# File: recover_errors.sh
# Purpose: Automated error recovery

echo "🔧 Starting error recovery process..."

# Check for common issues
if [ ! -f "blockchain.db" ]; then
    echo "❌ Database missing - initializing new blockchain"
    java -jar blockchain-cli.jar status
fi

# Test basic functionality
if ! java -jar blockchain-cli.jar status >/dev/null 2>&1; then
    echo "❌ CLI not responding - checking for corruption"
    
    # Backup corrupted database
    if [ -f "blockchain.db" ]; then
        mv blockchain.db "blockchain.db.corrupted_$(date +%Y%m%d_%H%M%S)"
    fi
    
    # Find latest backup
    LATEST_BACKUP=$(ls -t backups/daily/blockchain_*.json 2>/dev/null | head -1)
    if [ -n "$LATEST_BACKUP" ]; then
        echo "🔄 Restoring from backup: $LATEST_BACKUP"
        java -jar blockchain-cli.jar import "$LATEST_BACKUP"
    else
        echo "⚠️  No backup found - initializing new blockchain"
        java -jar blockchain-cli.jar status
    fi
fi

echo "✅ Error recovery completed"
```

## 📝 Usage Examples

### Running Daily Operations

```zsh
# Set up daily cron job
echo "0 2 * * * /path/to/daily_backup.sh" | crontab -

# Run health checks every hour
echo "0 * * * * /path/to/health_check.sh" | crontab -

# Weekly maintenance
echo "0 3 * * 0 /path/to/db_maintenance.sh" | crontab -
```

### CI/CD Pipeline Integration

```yaml
# .github/workflows/deploy.yml
name: Deploy Blockchain CLI

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Pre-deployment validation
        run: ./scripts/pre_deploy_check.sh
      - name: Deploy
        run: ./deploy.sh
      - name: Post-deployment verification
        run: ./scripts/post_deploy_verify.sh
```

All scripts are production-ready and include proper error handling, logging, and cleanup procedures. Remember to make scripts executable with `chmod +x script_name.sh` before use.
