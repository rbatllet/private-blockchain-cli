# 🛠️ Troubleshooting Guide

Comprehensive troubleshooting guide for the Private Blockchain CLI.

## 📋 Table of Contents

- [Diagnostic Commands](#-diagnostic-commands)
- [Common Scenarios](#-common-scenarios)
- [Step-by-Step Guide](#-step-by-step-guide)
- [Environment Issues](#-environment-issues)
- [Performance Problems](#-performance-problems)
- [Docker Issues](#-docker-issues)
- [Debug Mode](#-debug-mode)

## 🩺 Diagnostic Commands

Before diving into solutions, use these diagnostic commands to identify issues:

```zsh
# Quick health check
java -jar blockchain-cli.jar status --json | jq

# Detailed system information
java -jar blockchain-cli.jar status --detailed

# Detailed system information with verbose output
java -jar blockchain-cli.jar status --detailed --verbose
🔍 Initializing comprehensive system diagnostics...
🔍 Analyzing blockchain configuration...
🔍 Checking off-chain storage integrity...
✅ System diagnostics completed

# Chain integrity verification
java -jar blockchain-cli.jar validate --detailed

# Chain integrity verification with verbose output
java -jar blockchain-cli.jar validate --detailed --verbose
🔍 Starting comprehensive blockchain validation...
🔍 Validating all blocks and signatures...
🔍 Checking off-chain data integrity...
✅ Validation completed successfully

# Check authorized keys
java -jar blockchain-cli.jar list-keys --detailed

# Recent blockchain activity
java -jar blockchain-cli.jar search --date-from $(date -d "7 days ago" +%Y-%m-%d) --limit 10
```

## 🔧 Common Scenarios

### Scenario 1: "My blockchain validation is failing"

**Symptoms**: `validate` command returns `"valid": false`

**Diagnosis**:
```zsh
# Get detailed validation report
java -jar blockchain-cli.jar validate --detailed

# Get detailed validation report with verbose output
java -jar blockchain-cli.jar validate --detailed --verbose
🔍 Generating comprehensive validation report...
🔍 Analyzing blockchain structure and integrity...
🔍 Validation report generation completed

# Check recent blocks for corruption
java -jar blockchain-cli.jar search --date-from $(date -d "1 day ago" +%Y-%m-%d)
```

**Solutions**:
```zsh
# Option 1: Restore from recent backup
java -jar blockchain-cli.jar import backups/latest_backup.json --validate-after

# Option 2: Export current data for analysis
java -jar blockchain-cli.jar export investigation/corrupted_chain_$(date +%Y%m%d).json

# Option 3: Start fresh if data is not critical
rm blockchain.db* && java -jar blockchain-cli.jar status
```

### Scenario 2: "I can't add blocks - unauthorized key error"

**Symptoms**: `Unauthorized key attempting to add block`

**Diagnosis**:
```zsh
# Check available authorized keys
java -jar blockchain-cli.jar list-keys --active-only

# Verify specific key status
java -jar blockchain-cli.jar list-keys --detailed | grep "Alice"
```

**Solutions**:
```zsh
# Option 1: Add a new authorized key
java -jar blockchain-cli.jar add-key "NewUser" --generate --show-private

# Option 2: Use existing authorized key
java -jar blockchain-cli.jar add-block "Your data" --signer ExistingUser

# Option 3: Generate key automatically
java -jar blockchain-cli.jar add-block "Your data" --generate-key
```

### Scenario 3: "My export/import is failing"

**Symptoms**: Export creates empty files or import fails

**Diagnosis**:
```zsh
# Check current blockchain state
java -jar blockchain-cli.jar status --detailed

# Check current blockchain state with verbose output
java -jar blockchain-cli.jar status --detailed --verbose
🔍 Initializing blockchain state analysis...
🔍 Checking database integrity...
🔍 State analysis completed

# Verify file permissions
ls -la backups/
ls -la blockchain.db*

# Test with minimal export
java -jar blockchain-cli.jar export test_export.json && ls -la test_export.json
```

**Solutions**:
```zsh
# Create directory if missing
mkdir -p backups && chmod 755 backups

# Export with full path
java -jar blockchain-cli.jar export $(pwd)/backups/manual_backup.json

# Test import with validation
java -jar blockchain-cli.jar import backup_file.json --dry-run
```

### Scenario 4: "Performance is very slow"

**Symptoms**: Commands take >30 seconds to complete

**Diagnosis**:
```zsh
# Check database size
ls -lh blockchain.db*
du -sh .

# Count total blocks
java -jar blockchain-cli.jar status --json | jq '.blockCount'

# Check available disk space
df -h .
```

**Solutions**:
```zsh
# Option 1: Increase JVM memory
java -Xmx1024m -jar blockchain-cli.jar status

# Option 2: Clean up old data
java -jar blockchain-cli.jar export archive/full_backup.json
# Then start fresh if appropriate

# Option 3: Use quick validation
java -jar blockchain-cli.jar validate --quick
```

### Scenario 5: "Docker containers won't start"

**Symptoms**: Docker build fails or containers exit immediately

**Diagnosis**:
```zsh
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
```zsh
# Rebuild image cleanly
docker rmi blockchain-cli
docker build --no-cache -t blockchain-cli .

# Check JAR file exists
ls -la target/blockchain-cli.jar

# Test with simple command
docker run --rm blockchain-cli --version
```

## 📋 Step-by-Step Guide

### Step 1: Environment Check
```zsh
# Verify Java version
java -version | head -1

# Check JAR integrity
ls -la blockchain-cli.jar
java -jar blockchain-cli.jar --version

# Test basic functionality
java -jar blockchain-cli.jar status
```

### Step 2: Database Issues
```zsh
# Check database files
ls -la blockchain.db*

# Verify permissions
chmod 644 blockchain.db* 2>/dev/null || echo "No database files found"

# Test database connection
java -jar blockchain-cli.jar status --json | jq '.isValid'
```

### Step 3: Key Management Issues
```zsh
# List all keys
java -jar blockchain-cli.jar list-keys --detailed

# Test key generation
java -jar blockchain-cli.jar add-key "TestKey-$(date +%s)" --generate

# Test block addition
java -jar blockchain-cli.jar add-block "Test block $(date)" --generate-key
```

### Step 4: Data Integrity Check
```zsh
# Full validation
java -jar blockchain-cli.jar validate --detailed

# Full validation with verbose output
java -jar blockchain-cli.jar validate --detailed --verbose
🔍 Starting comprehensive blockchain validation...
🔍 Validating all blocks, signatures, and off-chain data...
🔍 Full validation completed

# Export test
java -jar blockchain-cli.jar export test_backup_$(date +%s).json

# Import test
java -jar blockchain-cli.jar import test_backup_*.json --dry-run
```

## 🔧 Environment Issues

### "Command not found" or "java: command not found"

**Problem**: Java is not installed or not in PATH.

**Solution**:
```zsh
# Check if Java is installed
java -version

# Install Java 21+ if needed:
# macOS with Homebrew:
brew install openjdk@21

# Ubuntu/Debian:
sudo apt update && sudo apt install openjdk-21-jdk

# Windows: Download from Oracle or OpenJDK website
```

### "UnsupportedClassVersionError"

**Problem**: You're using an older Java version.

**Solution**: This application requires Java 21+. Update your Java installation.

```zsh
# Check current version
java -version

# Set JAVA_HOME if multiple versions installed
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### "No such file or directory: blockchain-cli.jar"

**Problem**: JAR file path is incorrect.

**Solution**:
```zsh
# Make sure you're in the correct directory
ls -la blockchain-cli.jar

# Use full path if needed
java -jar /full/path/to/blockchain-cli.jar status

# Check if JAR was built
ls -la target/blockchain-cli.jar
```

## ⚡ Performance Problems

### Memory Issues

**Problem**: Application runs out of memory with large blockchains.

**Solution**:
```zsh
# Increase JVM memory
java -Xmx1024m -jar blockchain-cli.jar status

# For very large blockchains
java -Xmx2048m -jar blockchain-cli.jar status

# Check memory usage
java -Xmx512m -XX:+PrintGCDetails -jar blockchain-cli.jar status
```

### Slow Database Operations

**Problem**: Database operations are slow.

**Diagnosis**:
```zsh
# Check database size
ls -lh blockchain.db*

# Check disk space
df -h .

# Monitor disk I/O
iostat -x 1 5  # Linux
```

**Solutions**:
```zsh
# Optimize database
sqlite3 blockchain.db "VACUUM;"

# Use faster storage (SSD)
# Move database to faster disk

# Reduce validation frequency
java -jar blockchain-cli.jar validate --quick
```

### Network/Connectivity Issues

**Problem**: Slow network operations.

**Solutions**:
```zsh
# Use local operations when possible
java -jar blockchain-cli.jar status --json > status.json

# Batch operations
# Combine multiple commands in scripts

# Use compression for large exports
gzip -c backup.json > backup.json.gz
```

## 🐳 Docker Issues

### Build Problems

**Problem**: Docker build fails.

**Diagnosis**:
```zsh
# Check Docker installation
docker --version
docker info

# Check disk space
docker system df
df -h

# Check JAR file exists
ls -la target/blockchain-cli.jar
```

**Solutions**:
```zsh
# Clean Docker cache
docker system prune -a

# Build with no cache
docker build --no-cache -t blockchain-cli .

# Check Dockerfile syntax
docker build --dry-run -t blockchain-cli .
```

### Container Runtime Issues

**Problem**: Container exits immediately.

**Diagnosis**:
```zsh
# Check container logs
docker logs container_name

# Run interactively
docker run -it blockchain-cli /bin/zsh

# Check entrypoint
docker inspect blockchain-cli | jq '.[0].Config.Entrypoint'
```

**Solutions**:
```zsh
# Override entrypoint for debugging
docker run -it --entrypoint=/bin/zsh blockchain-cli

# Check file permissions
docker run --rm blockchain-cli ls -la /app

# Test with simple command
docker run --rm blockchain-cli echo "Hello World"
```

### Volume Issues

**Problem**: Volume mounting fails or permissions denied.

**Diagnosis**:
```zsh
# Check host directory permissions
ls -la blockchain-data/

# Check container user
docker run --rm blockchain-cli id

# Test volume mounting
docker run --rm -v $(pwd):/test alpine ls -la /test
```

**Solutions**:
```zsh
# Fix permissions
sudo chown -R $USER:$USER blockchain-data

# Run as specific user
docker run --rm --user $(id -u):$(id -g) \
  -v $(pwd)/blockchain-data:/data \
  blockchain-cli status

# Use named volumes
docker volume create blockchain_data
docker run --rm -v blockchain_data:/data blockchain-cli status
```

## 🔍 Debug Mode

### Enable Verbose Output

For detailed debugging information, use the `--verbose` flag globally or with specific commands:

```zsh
# Global verbose mode
java -jar blockchain-cli.jar --verbose status

# Command-specific verbose mode
java -jar blockchain-cli.jar add-block "Test data" --key-file keys/private.pem --verbose
```

Verbose mode shows:
- Database connection details
- Step-by-step operation progress  
- Internal validation results
- Timing information
- SQL queries (if enabled)
- Key loading and format detection details
- Authorization processes

### Troubleshooting Key File Issues

If you're having problems with the `--key-file` option:

```zsh
# Enable verbose mode to see detailed key loading information
java -jar blockchain-cli.jar add-block "Test data" --key-file keys/private.pem --verbose

# Check if the key file is readable
cat keys/private.pem | head -3

# Verify key format (should show BEGIN PRIVATE KEY for PKCS#8)
head -1 keys/private.pem
```

#### Common Key File Errors and Solutions

| Error | Possible Cause | Solution |
|-------|---------------|----------|
| "Invalid key format" | Not in PEM PKCS#8, DER, or Base64 format | Convert to PKCS#8: `openssl pkcs8 -topk8 -in key.pem -out key_pkcs8.pem -nocrypt` |
| "File not found" | Incorrect path or permissions | Check path and ensure read permissions: `chmod 600 keys/private.pem` |
| "Unable to load private key" | Corrupted or encrypted key file | Ensure key is not password-protected or use proper format |
| "Path validation failed" | Trying to access system directories | Use a path in a non-system directory |

#### Converting Key Formats

```zsh
# Convert RSA key to PKCS#8 format (recommended)
openssl pkcs8 -topk8 -in original_key.pem -out key_pkcs8.pem -nocrypt

# Convert PEM to DER format
openssl pkcs8 -topk8 -in key.pem -outform DER -out key.der -nocrypt

# Convert PEM to Base64 (raw)
openssl pkcs8 -topk8 -in key.pem -outform DER -out - -nocrypt | base64 > key.b64
```

### Logging Configuration

```zsh
# Enable SQL logging (if available)
java -Dhibernate.show_sql=true -jar blockchain-cli.jar status

# Enable debug logging
java -Dlogging.level.root=DEBUG -jar blockchain-cli.jar status

# Save output to file
java -jar blockchain-cli.jar --verbose status > debug.log 2>&1
```

## 🧪 Testing Your Setup

### Quick System Test

```zsh
#!/usr/bin/env zsh
# save as: system_test.sh

echo "🧪 Blockchain CLI System Test"
echo "=============================="

# Test 1: Version
echo "📋 Test 1: Version check"
if java -jar blockchain-cli.jar --version; then
    echo "✅ Version test passed"
else
    echo "❌ Version test failed"
    exit 1
fi

# Test 2: Status
echo "📋 Test 2: Status check"
if java -jar blockchain-cli.jar status >/dev/null; then
    echo "✅ Status test passed"
else
    echo "❌ Status test failed"
    exit 1
fi

# Test 3: Key generation
echo "📋 Test 3: Key generation"
if java -jar blockchain-cli.jar add-key "SystemTest" --generate >/dev/null; then
    echo "✅ Key generation test passed"
else
    echo "❌ Key generation test failed"
    exit 1
fi

# Test 4: Block addition
echo "📋 Test 4: Block addition"
if java -jar blockchain-cli.jar add-block "System test block" --generate-key >/dev/null; then
    echo "✅ Block addition test passed"
else
    echo "❌ Block addition test failed"
    exit 1
fi

# Test 5: Validation
echo "📋 Test 5: Chain validation"
if java -jar blockchain-cli.jar validate >/dev/null; then
    echo "✅ Validation test passed"
else
    echo "❌ Validation test failed"
    exit 1
fi

# Test 6: Key file support (if test keys exist)
echo "📋 Test 6: Key file support"
if [ -f "test-keys/test_key_pkcs8.pem" ]; then
    if java -jar blockchain-cli.jar add-block "Key file test" --key-file test-keys/test_key_pkcs8.pem >/dev/null; then
        echo "✅ Key file test passed"
    else
        echo "❌ Key file test failed"
        exit 1
    fi
else
    echo "ℹ️ Skipping key file test (test-keys/test_key_pkcs8.pem not found)"
    echo "  Create test keys with: mkdir -p test-keys && openssl genpkey -algorithm RSA -out test-keys/test_key.pem && openssl pkcs8 -topk8 -in test-keys/test_key.pem -out test-keys/test_key_pkcs8.pem -nocrypt"
fi

# Test 7: Verbose mode
echo "📋 Test 7: Verbose mode"
if java -jar blockchain-cli.jar --verbose status | grep -q "Verbose"; then
    echo "✅ Verbose mode test passed"
else
    echo "❌ Verbose mode test failed"
    exit 1
fi

echo "🎉 All system tests passed!"
```

### Performance Benchmark

```zsh
#!/usr/bin/env zsh
# save as: performance_test.sh

echo "⚡ Performance Benchmark"
echo "======================="

# Startup time
echo "🚀 Testing startup time..."
time java -jar blockchain-cli.jar --version >/dev/null

# Status performance
echo "📊 Testing status performance..."
time java -jar blockchain-cli.jar status >/dev/null

# Block addition performance
echo "📦 Testing block addition performance..."
start_time=$(date +%s)
for i in {1..10}; do
    java -jar blockchain-cli.jar add-block "Perf test $i" --generate-key >/dev/null
done
end_time=$(date +%s)
duration=$((end_time - start_time))
echo "Added 10 blocks in ${duration}s ($(echo "scale=2; 10/$duration" | bc) blocks/sec)"

# Validation performance
echo "🔍 Testing validation performance..."
time java -jar blockchain-cli.jar validate >/dev/null

echo "✅ Performance benchmark completed"
```

## 🔗 Related Documents

- [Main README](README.md) - Getting started
- [Examples](EXAMPLES.md) - Practical use cases  
- [Docker Guide](DOCKER_GUIDE.md) - Docker-specific troubleshooting
- [Enterprise Guide](ENTERPRISE_GUIDE.md) - Production troubleshooting

---

**Still having issues?** Return to the [main README](README.md) or check [Examples](EXAMPLES.md) for working configurations.
