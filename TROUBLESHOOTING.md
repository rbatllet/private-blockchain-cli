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

## 🔧 Common Scenarios

### Scenario 1: "My blockchain validation is failing"

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

### Scenario 2: "I can't add blocks - unauthorized key error"

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

### Scenario 3: "My export/import is failing"

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

### Scenario 4: "Performance is very slow"

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

### Scenario 5: "Docker containers won't start"

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

## 📋 Step-by-Step Guide

### Step 1: Environment Check
```bash
# Verify Java version
java -version | head -1

# Check JAR integrity
ls -la blockchain-cli.jar
java -jar blockchain-cli.jar --version

# Test basic functionality
java -jar blockchain-cli.jar status
```

### Step 2: Database Issues
```bash
# Check database files
ls -la blockchain.db*

# Verify permissions
chmod 644 blockchain.db* 2>/dev/null || echo "No database files found"

# Test database connection
java -jar blockchain-cli.jar status --json | jq '.isValid'
```

### Step 3: Key Management Issues
```bash
# List all keys
java -jar blockchain-cli.jar list-keys --detailed

# Test key generation
java -jar blockchain-cli.jar add-key "TestKey-$(date +%s)" --generate

# Test block addition
java -jar blockchain-cli.jar add-block "Test block $(date)" --generate-key
```

### Step 4: Data Integrity Check
```bash
# Full validation
java -jar blockchain-cli.jar validate --detailed

# Export test
java -jar blockchain-cli.jar export test_backup_$(date +%s).json

# Import test
java -jar blockchain-cli.jar import test_backup_*.json --dry-run
```

## 🔧 Environment Issues

### "Command not found" or "java: command not found"

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

### "UnsupportedClassVersionError"

**Problem**: You're using an older Java version.

**Solution**: This application requires Java 21+. Update your Java installation.

```bash
# Check current version
java -version

# Set JAVA_HOME if multiple versions installed
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### "No such file or directory: blockchain-cli.jar"

**Problem**: JAR file path is incorrect.

**Solution**:
```bash
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
```bash
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
```bash
# Check database size
ls -lh blockchain.db*

# Check disk space
df -h .

# Monitor disk I/O
iostat -x 1 5  # Linux
```

**Solutions**:
```bash
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
```bash
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
```bash
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
```bash
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
```bash
# Check container logs
docker logs container_name

# Run interactively
docker run -it blockchain-cli /bin/bash

# Check entrypoint
docker inspect blockchain-cli | jq '.[0].Config.Entrypoint'
```

**Solutions**:
```bash
# Override entrypoint for debugging
docker run -it --entrypoint=/bin/bash blockchain-cli

# Check file permissions
docker run --rm blockchain-cli ls -la /app

# Test with simple command
docker run --rm blockchain-cli echo "Hello World"
```

### Volume Issues

**Problem**: Volume mounting fails or permissions denied.

**Diagnosis**:
```bash
# Check host directory permissions
ls -la blockchain-data/

# Check container user
docker run --rm blockchain-cli id

# Test volume mounting
docker run --rm -v $(pwd):/test alpine ls -la /test
```

**Solutions**:
```bash
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

```bash
# Basic verbose mode
java -jar blockchain-cli.jar --verbose status

# Very detailed output
java -jar blockchain-cli.jar --verbose add-key "test" --generate

# Verbose with timing
time java -jar blockchain-cli.jar --verbose validate
```

### Debug Information

Verbose mode shows:
- Database connection details
- Step-by-step operation progress  
- Internal validation results
- Timing information
- SQL queries (if enabled)

### Logging Configuration

```bash
# Enable SQL logging (if available)
java -Dhibernate.show_sql=true -jar blockchain-cli.jar status

# Enable debug logging
java -Dlogging.level.root=DEBUG -jar blockchain-cli.jar status

# Save output to file
java -jar blockchain-cli.jar --verbose status > debug.log 2>&1
```

## 🧪 Testing Your Setup

### Quick System Test

```bash
#!/bin/bash
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

echo "🎉 All system tests passed!"
```

### Performance Benchmark

```bash
#!/bin/bash
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
