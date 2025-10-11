# 🏢 Enterprise Guide

Best practices and guidelines for using the Private Blockchain CLI in enterprise environments.

## 📋 Table of Contents

- [Security Best Practices](#-security-best-practices)
- [Performance Best Practices](#-performance-best-practices)
- [Operational Best Practices](#-operational-best-practices)
- [Monitoring & Alerting](#-monitoring-alerting)
- [Compliance & Auditing](#-compliance-auditing)
- [Production Deployment](#-production-deployment)

## 🔐 Security Best Practices

### Key Management

#### Secure Key Generation
```zsh
# 1. Generate keys with proper hierarchy and security levels
# Root key (long-term, highly secure)
java -jar blockchain-cli.jar add-key "Root-Key-2025" --generate --store-private --key-type root

# Department key (intermediate, 6-12 months)
java -jar blockchain-cli.jar add-key "Finance-Dept-Key" --generate --store-private --key-type intermediate --parent-key Root-Key-2025

# Service key (short-term, 30-90 days)
java -jar blockchain-cli.jar add-key "Payment-Service-Key" --generate --store-private --key-type operational --parent-key Finance-Dept-Key --validity-days 30

# 2. Use descriptive names following naming convention
# Format: {Purpose}-{Team/Dept}-{Environment}-{CreationDate}
java -jar blockchain-cli.jar add-key "Signing-PaymentService-Prod-$(date +%Y%m%d)" --generate

# 3. Document key metadata in secure registry
echo "KeyID: 1a2b3c4d-..." > key_registry.txt
echo "Type: Operational" >> key_registry.txt
echo "Purpose: Payment Service Signing" >> key_registry.txt
echo "Owner: Payment Team" >> key_registry.txt
echo "Expiration: 2025-07-22" >> key_registry.txt
```

#### Key Storage and Protection
```zsh
# 1. Create secure key storage with restricted access
KEYSTORE_DIR="/etc/blockchain/keystore"
mkdir -p "$KEYSTORE_DIR"
chown -R blockchain:blockchain "$KEYSTORE_DIR"
chmod 750 "$KEYSTORE_DIR"

# 2. Store keys with proper permissions and encryption
# Generate a secure passphrase
PASSPHRASE=$(openssl rand -base64 32)

# Store encrypted private key
java -jar blockchain-cli.jar manage-keys --export "Payment-Service-Key" --output "$KEYSTORE_DIR/payment_service.p12" --password "$PASSPHRASE"

# 3. Secure backup strategy with encryption
BACKUP_DIR="/backup/blockchain/keys/$(date +%Y%m%d)"
mkdir -p "$BACKUP_DIR"

# Create encrypted backup
java -jar blockchain-cli.jar manage-keys --backup --output "$BACKUP_DIR/keystore_backup_$(date +%s).p12" --password "$PASSPHRASE"

# 4. Store passphrase in enterprise secret management
# Example using AWS Secrets Manager
aws secretsmanager create-secret \
    --name "blockchain/keystore/payment-service" \
    --secret-string "$PASSPHRASE"

# 5. Set up key rotation automation
# Add to crontab for monthly rotation
0 2 1 * * /opt/blockchain/scripts/rotate-keys.sh
```

### Data Protection and Cryptography

#### Secure Data Handling
```zsh
# 1. Regular encrypted backups with integrity checks
BACKUP_FILE="backups/blockchain_$(date +%Y%m%d_%H%M%S).enc"
java -jar blockchain-cli.jar export - | \
    openssl enc -aes-256-gcm -salt -out "$BACKUP_FILE" -kfile /etc/blockchain/backup.key

# 2. Data validation with cryptographic verification
BACKUP_HASH=$(sha3sum -a 256 "$BACKUP_FILE" | cut -d' ' -f1)
java -jar blockchain-cli.jar add-block "Backup created: $BACKUP_FILE | SHA3-256: $BACKUP_HASH" --signer Backup-Service

# 3. Secure data handling with external encryption
# Encrypt sensitive data before adding to blockchain
SENSITIVE_DATA="credit_card=4111111111111111"
ENCRYPTED_DATA=$(echo "$SENSITIVE_DATA" | openssl pkeyutl -encrypt -pubin -inkey /etc/keys/encryption_key.pub -outform base64)
REFERENCE_ID=$(uuidgen)

# Store encrypted data in secure storage
echo "$ENCRYPTED_DATA" | aws s3 cp - "s3://secure-data-bucket/$REFERENCE_ID.enc" --sse aws:kms

# Add reference to blockchain
java -jar blockchain-cli.jar add-block "type=payment_reference | ref=$REFERENCE_ID | storage=s3://secure-data-bucket" --signer Payment-Processor

# 4. Data integrity with cryptographic proofs
DATA_TO_SIGN="transaction_1234"
SIGNATURE=$(echo -n "$DATA_TO_SIGN" | openssl dgst -sha3-256 -sign /etc/keys/signing_key.pem | base64 -w 0)
java -jar blockchain-cli.jar add-block "data=$DATA_TO_SIGN | signature=$SIGNATURE | key_id=signing_key_001" --signer Signing-Service
```

## 🚀 Performance and Scalability

### Efficient Operations

#### Batch and Parallel Processing
```zsh
# 1. JSON output for automation with jq processing
BLOCK_INFO=$(java -jar blockchain-cli.jar status --json)
BLOCK_COUNT=$(jq -r '.blockCount' <<< "$BLOCK_INFO")
LATEST_HASH=$(jq -r '.latestBlockHash' <<< "$BLOCK_INFO")

# 2. Parallel batch processing with GNU parallel
echo "order_{1..1000}.json" | parallel -j 8 '
    DATA=$(cat {}); \
    SIGNATURE=$(echo -n "$DATA" | openssl dgst -sha3-256 -sign /etc/keys/batch_signer.pem | base64); \
    java -jar blockchain-cli.jar add-block "batch_data=$DATA" --signer Batch-Processor --signature "$SIGNATURE"
'

# 3. Optimized validation strategies
# Quick validation for CI/CD pipelines
java -jar blockchain-cli.jar validate --quick --check-depth 100

# Full validation during off-peak hours
0 2 * * * java -jar blockchain-cli.jar validate --full --report /var/log/blockchain/validation_$(date +\%Y\%m\%d).log

# 4. Advanced search with pagination and filtering
# Search with pagination
SEARCH_RESULTS=$(java -jar blockchain-cli.jar search "payment" --limit 100 --offset 0 --json)

# Filter results by date range
START_DATE=$(date -d "30 days ago" +%s)
END_DATE=$(date +%s)
FILTERED_RESULTS=$(jq --arg start "$START_DATE" --arg end "$END_DATE" 
    '.items[] | select(.timestamp >= ($start|tonumber) and .timestamp <= ($end|tonumber))' 
    <<< "$SEARCH_RESULTS")
```

### Performance Monitoring

#### Metrics Collection
```zsh
# 1. Track block addition performance
START_TIME=$(date +%s.%N)
java -jar blockchain-cli.jar add-block "performance_test_$(date +%s)" --signer Perf-Test
END_TIME=$(date +%s.%N)
ELAPSED=$(echo "$END_TIME - $START_TIME" | bc)
echo "Block addition time: ${ELAPSED}s" | tee -a /var/log/blockchain/performance_metrics.log

# 2. Monitor memory usage
JAVA_OPTS="-Xmx2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
java $JAVA_OPTS -jar blockchain-cli.jar status --detailed

# 3. Export metrics for monitoring systems
METRICS=$(java -jar blockchain-cli.jar metrics --format prometheus)
curl -X POST -H "Content-Type: text/plain" --data "$METRICS" http://prometheus:9091/metrics/job/blockchain
```

## 📋 Operational Best Practices

### Daily Operations

#### Health Check Script
```zsh
#!/usr/bin/env zsh
# save as: daily_health_check.sh

echo "🌅 Daily Blockchain Health Check: $(date)"

# 1. System status
java -jar blockchain-cli.jar status --detailed

# 2. Chain integrity
if java -jar blockchain-cli.jar validate --json | jq -r '.valid' | grep -q true; then
    echo "✅ Chain integrity: GOOD"
else
    echo "❌ Chain integrity: FAILED - IMMEDIATE ATTENTION REQUIRED"
    exit 1
fi

# 3. Key management check
KEYS=$(java -jar blockchain-cli.jar list-keys --json | jq length)
echo "🔑 Active keys: $KEYS"

# 4. Recent activity
echo "📈 Recent activity:"
java -jar blockchain-cli.jar search --date-from $(date -d "1 day ago" +%Y-%m-%d) --limit 5

echo "✅ Daily check completed"
```

## 🔍 Monitoring & Alerting

### Health Monitoring

#### Comprehensive Monitoring Script
```zsh
#!/usr/bin/env zsh
# save as: enterprise_monitor.sh

ALERT_EMAIL="blockchain-ops@company.com"
LOG_FILE="/var/log/blockchain/monitor.log"

log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a $LOG_FILE
}

send_alert() {
    local message="$1"
    local severity="$2"
    echo "$message" | mail -s "[$severity] Blockchain Alert" $ALERT_EMAIL
    log_message "ALERT SENT: $message"
}

# Health checks
check_connectivity() {
    if ! java -jar blockchain-cli.jar --version >/dev/null 2>&1; then
        send_alert "Blockchain CLI not responding" "CRITICAL"
        return 1
    fi
    return 0
}

check_chain_integrity() {
    if ! java -jar blockchain-cli.jar validate --json | jq -r '.valid' | grep -q true; then
        send_alert "Blockchain validation failed" "CRITICAL"
        return 1
    fi
    return 0
}

# Main monitoring
log_message "Starting blockchain monitoring"

if check_connectivity && check_chain_integrity; then
    log_message "All health checks passed"
else
    log_message "Health check failures detected"
    exit 1
fi
```

## 📊 Compliance & Auditing

### Audit Trail Generation
```zsh
#!/usr/bin/env zsh
# save as: generate_audit_report.sh

REPORT_DIR="/audit/blockchain"
PERIOD="$1"  # daily, weekly, monthly

case "$PERIOD" in
    "daily")
        DATE_FILTER="--date-from $(date +%Y-%m-%d)"
        ;;
    "weekly")
        DATE_FILTER="--date-from $(date -d '7 days ago' +%Y-%m-%d)"
        ;;
    "monthly")
        DATE_FILTER="--date-from $(date -d '1 month ago' +%Y-%m-%d)"
        ;;
esac

mkdir -p $REPORT_DIR

echo "📊 Generating $PERIOD audit report..."

# Generate audit data
java -jar blockchain-cli.jar search $DATE_FILTER --json > $REPORT_DIR/audit_${PERIOD}_$(date +%Y%m%d).json
java -jar blockchain-cli.jar validate --detailed > $REPORT_DIR/validation_${PERIOD}_$(date +%Y%m%d).txt
java -jar blockchain-cli.jar list-keys --detailed > $REPORT_DIR/keys_${PERIOD}_$(date +%Y%m%d).txt

echo "✅ Audit report generated in $REPORT_DIR"
```

### Compliance Verification
```zsh
#!/usr/bin/env zsh
# save as: compliance_check.sh

echo "🔍 Compliance Verification: $(date)"

# 1. Data integrity check
if java -jar blockchain-cli.jar validate --json | jq -r '.valid' | grep -q true; then
    echo "✅ Data integrity: VERIFIED"
else
    echo "❌ Data integrity: FAILED"
    exit 1
fi

# 2. Backup verification
LATEST_BACKUP=$(ls -t backups/*.json 2>/dev/null | head -1)
if [ -n "$LATEST_BACKUP" ]; then
    if java -jar blockchain-cli.jar import "$LATEST_BACKUP" --dry-run >/dev/null 2>&1; then
        echo "✅ Backup integrity: VERIFIED"
    else
        echo "❌ Backup integrity: FAILED"
    fi
else
    echo "⚠️ No backups found"
fi

# 3. Access control verification
ACTIVE_KEYS=$(java -jar blockchain-cli.jar list-keys --json | jq length)
echo "📊 Active authorized keys: $ACTIVE_KEYS"

echo "✅ Compliance check completed"
```

## 🚀 Production Deployment

### Environment Setup

#### Production Configuration
```zsh
#!/usr/bin/env zsh
# save as: production_setup.sh

# Create production directory structure
sudo mkdir -p /opt/blockchain/{bin,data,logs,backups,config}
sudo chown -R blockchain:blockchain /opt/blockchain

# Set proper permissions
chmod 755 /opt/blockchain/{bin,data,logs,backups,config}
chmod 600 /opt/blockchain/config/*

# Copy application
cp blockchain-cli.jar /opt/blockchain/bin/

# Create systemd service
cat > /etc/systemd/system/blockchain-monitor.service << EOF
[Unit]
Description=Blockchain Monitor
After=network.target

[Service]
Type=simple
User=blockchain
Group=blockchain
WorkingDirectory=/opt/blockchain/data
ExecStart=/opt/blockchain/bin/monitor.sh
Restart=always
RestartSec=30

[Install]
WantedBy=multi-user.target
EOF

systemctl enable blockchain-monitor
systemctl start blockchain-monitor
```

#### Resource Allocation
```zsh
# JVM tuning for production
export JAVA_OPTS="-Xmx4096m -Xms2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Disk I/O optimization
# Use SSD storage for blockchain.db
# Mount with appropriate options
mount -o noatime,data=writeback /dev/ssd1 /opt/blockchain/data

# Network optimization (if applicable)
# Configure firewall rules
# Set up load balancing
```

### Disaster Recovery

#### Recovery Plan
```zsh
#!/usr/bin/env zsh
# save as: disaster_recovery.sh

BACKUP_LOCATION="/backup/blockchain"
RECOVERY_POINT="$1"

echo "🆘 Blockchain Disaster Recovery"
echo "Recovery point: $RECOVERY_POINT"

# 1. Stop current services
systemctl stop blockchain-monitor

# 2. Backup current state (if possible)
if [ -f blockchain.db ]; then
    cp blockchain.db blockchain.db.disaster_backup_$(date +%s)
fi

# 3. Restore from backup
if [ -f "$BACKUP_LOCATION/$RECOVERY_POINT" ]; then
    echo "📥 Restoring from: $RECOVERY_POINT"
    java -jar blockchain-cli.jar import "$BACKUP_LOCATION/$RECOVERY_POINT" --validate-after
    
    if [ $? -eq 0 ]; then
        echo "✅ Recovery successful"
        systemctl start blockchain-monitor
    else
        echo "❌ Recovery failed"
        exit 1
    fi
else
    echo "❌ Backup file not found: $RECOVERY_POINT"
    exit 1
fi
```

### Scaling Strategies

#### Horizontal Scaling
```zsh
# Load balancer configuration (example)
# For multiple blockchain instances

# Instance 1
java -jar blockchain-cli.jar status --json > /health/instance1.json

# Instance 2  
java -jar blockchain-cli.jar status --json > /health/instance2.json

# Health check endpoint
curl -f http://localhost:8080/health || exit 1
```

#### Performance Monitoring
```zsh
#!/usr/bin/env zsh
# save as: performance_monitor.sh

# Monitor key performance indicators
while true; do
    # Response time
    start_time=$(date +%s%N)
    java -jar blockchain-cli.jar status >/dev/null
    end_time=$(date +%s%N)
    response_time=$(( (end_time - start_time) / 1000000 ))
    
    # Memory usage
    memory_usage=$(ps -o pid,vsz,rss -p $(pgrep java) | tail -1 | awk '{print $3}')
    
    # Database size
    db_size=$(ls -l blockchain.db | awk '{print $5}')
    
    # Log metrics
    echo "$(date -Iseconds),response_time_ms:$response_time,memory_kb:$memory_usage,db_size:$db_size" >> /var/log/blockchain/performance.csv
    
    sleep 60
done
```

## 📋 Checklist Templates

### Deployment Checklist
- [ ] Java 21+ installed and configured
- [ ] Blockchain CLI tested and verified
- [ ] Backup strategy implemented
- [ ] Monitoring scripts deployed
- [ ] Alert notifications configured
- [ ] Security hardening completed
- [ ] Documentation updated
- [ ] Team training completed

### Security Checklist
- [ ] Private keys stored securely
- [ ] File permissions properly set
- [ ] Network access restricted
- [ ] Backup encryption enabled
- [ ] Audit logging configured
- [ ] Access controls implemented
- [ ] Key rotation schedule defined

### Compliance Checklist
- [ ] Data retention policy defined
- [ ] Audit trail generation automated
- [ ] Regulatory requirements mapped
- [ ] Data privacy controls implemented
- [ ] Incident response plan documented
- [ ] Regular compliance reviews scheduled

## 🔗 Related Documents

- [Main README](../README.md) - Getting started
- [Examples](EXAMPLES.md) - Practical use cases
- [Docker Guide](DOCKER_GUIDE.md) - Container deployment
- [Troubleshooting](TROUBLESHOOTING.md) - Problem resolution
- [Integration Patterns](INTEGRATION_PATTERNS.md) - System integrations
- [Automation Scripts](AUTOMATION_SCRIPTS.md) - Ready-to-use scripts

---

**Need enterprise support?** This guide provides the foundation for production deployment. Customize these practices to fit your specific organizational requirements.
