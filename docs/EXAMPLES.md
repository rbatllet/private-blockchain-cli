# 💡 Blockchain CLI Examples

Comprehensive examples and use cases for the Private Blockchain CLI, featuring the new enhanced `--signer` functionality and secure private key management.

## 📋 Table of Contents

- [Quick Start Examples](#-quick-start-examples)
- [Configuration Management Examples](#-configuration-management-examples)
- [Encryption Analysis Examples](#-encryption-analysis-examples)
- [Search Metrics Examples](#-search-metrics-examples)
- [Performance Monitoring Examples](#-performance-monitoring-examples)
- [Enhanced Search Examples](#-enhanced-search-examples)
- [File Input Examples](#-file-input-examples)
- [Advanced Block Creation Options](#-advanced-block-creation-options)
- [Off-Chain Storage Examples](#-off-chain-storage-examples)
- [Off-Chain Command Examples](#-off-chain-command-examples)
- [Hybrid Search Examples](#-hybrid-search-examples)
- [Enhanced CLI Features](#-enhanced-cli-features)
- [Secure Key Management Examples](#-secure-key-management-examples)
- [Advanced Signer Workflows](#-advanced-signer-workflows)
- [Real-World Use Cases](#-real-world-use-cases)
- [Advanced Scenarios](#-advanced-scenarios)
- [Docker Examples](#-docker-examples)
- [Automation Scripts](#-automation-scripts)
- [Demo Scripts](#-demo-scripts)

## 🔐 Encryption Analysis Examples

The `encrypt` command provides comprehensive analysis of blockchain encryption, encrypted blocks, and security metrics.

### Example 1: Basic Encryption Statistics

```zsh
# Show overall encryption statistics
java -jar blockchain-cli.jar encrypt --stats
🔐 Blockchain Encryption Statistics
============================================================
📊 Block Statistics:
   📦 Total blocks: 15
   🔐 Encrypted blocks: 8
   📝 Unencrypted blocks: 7
   💾 Off-chain blocks: 3
   📈 Encryption rate: 53.3%

📂 Category Breakdown:
   MEDICAL: 5 blocks
   FINANCE: 3 blocks
   TECHNICAL: 2 blocks

🔍 Analysis completed: 2025-07-21T10:30:45.123Z
```

### Example 2: Search and Analyze Encrypted Data

```zsh
# Search for encrypted blocks containing specific terms
java -jar blockchain-cli.jar encrypt "patient" --encrypted-only --verbose
🔍 Initializing encryption analysis...
🔍 Searching for blocks with term: patient
🔍 Found 3 blocks to analyze

🔐 Encryption Analysis Results
============================================================
📊 Analysis Summary:
   📦 Total blocks analyzed: 3
   🔐 Encrypted: 3
   📝 Unencrypted: 0
   📈 Encryption rate: 100.0%

📦 Block #5
   📅 2025-07-21T09:15:30.456Z
   🔐 Encrypted: Yes
   📝 Data: [Encrypted - 256 chars]

📦 Block #12
   📅 2025-07-21T09:45:22.789Z
   🔐 Encrypted: Yes
   💾 Off-chain: 1.2 MB
   📝 Data: [Encrypted - 128 chars]

📦 Block #14
   📅 2025-07-21T10:12:15.321Z
   🔐 Encrypted: Yes
   📝 Data: [Encrypted - 512 chars]
```

### Example 3: Decrypt and Analyze Content

```zsh
# Analyze encrypted blocks for specific username with decryption
java -jar blockchain-cli.jar encrypt "medical" --username Alice --password mypassword --verbose
🔍 Initializing encryption analysis...
🔍 Searching for blocks with term: medical
🔍 Found 4 blocks to analyze

🔐 Encryption Analysis Results
============================================================
📊 Analysis Summary:
   📦 Total blocks analyzed: 4
   🔐 Encrypted: 2
   📝 Unencrypted: 2
   📈 Encryption rate: 50.0%

📦 Block #8
   📅 2025-07-21T08:30:15.123Z
   🔐 Encrypted: Yes
   👤 User: Alice
   📝 Data: Patient consultation notes - cardiac assessment normal

📦 Block #11
   📅 2025-07-21T09:20:45.456Z
   🔐 Encrypted: No
   📝 Data: Public medical guidelines update
```

### Example 4: Validate Encrypted Blocks Integrity

```zsh
# Validate all encrypted blocks for integrity
java -jar blockchain-cli.jar encrypt --validate --verbose
🔍 Validating encrypted blocks integrity...

✅ Validating Encrypted Blocks
============================================================
🔍 Checking block #5: Medical consultation data
✅ Encryption integrity verified
🔍 Checking block #8: Financial audit report
✅ Encryption integrity verified
🔍 Checking block #12: Legal contract details
✅ Off-chain encryption verified

✅ All encrypted blocks are valid

📋 Validation Report:
• Total encrypted blocks: 8
• Validation passed: 8
• Validation failed: 0
• Off-chain encrypted files: 3
• Integrity check status: PASSED
```

### Example 5: JSON Output for Automation

```zsh
# Get encryption statistics in JSON format
java -jar blockchain-cli.jar encrypt --stats --json
{
  "operation": "encrypt-analysis",
  "totalBlocks": 15,
  "encryptedBlocks": 8,
  "unencryptedBlocks": 7,
  "encryptionRate": 53.3,
  "blocks": [
    {
      "blockNumber": 5,
      "timestamp": "2025-07-21T09:15:30.456Z",
      "encrypted": true,
      "hasOffChainData": false,
      "dataLength": 256
    },
    {
      "blockNumber": 8,
      "timestamp": "2025-07-21T09:30:22.789Z",
      "encrypted": true,
      "hasOffChainData": true,
      "dataLength": 128
    }
  ],
  "timestamp": "2025-07-21T10:30:45.123Z"
}

# Search encrypted blocks with JSON output
java -jar blockchain-cli.jar encrypt "confidential" --encrypted-only --json
```

### Example 6: Advanced Encryption Analysis

```zsh
# Complete encryption audit workflow
echo "📋 Starting comprehensive encryption audit..."

# 1. Get overall statistics
java -jar blockchain-cli.jar encrypt --stats

# 2. Validate all encrypted blocks
java -jar blockchain-cli.jar encrypt --validate

# 3. Analyze specific encrypted content
java -jar blockchain-cli.jar encrypt "audit" --username Auditor --password audit123

# 4. Check encryption by category
java -jar blockchain-cli.jar encrypt --encrypted-only | grep "MEDICAL"
java -jar blockchain-cli.jar encrypt --encrypted-only | grep "FINANCE"

# 5. Export results for compliance
java -jar blockchain-cli.jar encrypt --stats --json > encryption_audit_$(date +%Y%m%d).json
```

### Example 7: Troubleshooting Encryption Issues

```zsh
# Debug encryption problems
java -jar blockchain-cli.jar encrypt --validate --verbose
🔍 Validating encrypted blocks integrity...
🔍 Checking block #5: Medical consultation data
🔍 Decryption test passed
🔍 Hash verification passed
✅ Encryption integrity verified

# If validation fails, check specific blocks
java -jar blockchain-cli.jar encrypt "problem-block-content" --verbose
🔍 Searching for blocks with term: problem-block-content
⚠️  Found potential encryption issues in block #12
❌ Decryption failed - invalid password or corrupted data

# Check category-specific encryption status
java -jar blockchain-cli.jar encrypt --encrypted-only --verbose | grep "MEDICAL"
```

## 📊 Search Metrics Examples

The `search-metrics` command provides detailed performance analysis of search operations, helping optimize search strategies and monitor system performance.

### Example 1: Basic Search Metrics

```zsh
# Display current search performance metrics
java -jar blockchain-cli.jar search-metrics
📊 Search Performance Metrics
============================================================

Overall Performance Summary:
• Total Searches: 45
• Average Response Time: 68.5 ms
• Cache Hit Rate: 76.2%
• Most Common Search Type: SIMPLE (62%)

Search Type Performance:
🔹 SIMPLE:
   - Searches: 28
   - Avg Time: 15.2 ms
   - Cache Hits: 89.3%
   
🔹 SECURE:
   - Searches: 12
   - Avg Time: 85.4 ms
   - Cache Hits: 45.8%
   
🔹 INTELLIGENT:
   - Searches: 5
   - Avg Time: 124.7 ms
   - Cache Hits: 80.0%

💡 Tips:
  • Use --reset to clear all metrics
  • Use --json for machine-readable output
  • Run searches to populate metrics
```

### Example 2: Detailed Metrics Analysis

```zsh
# Get comprehensive metrics breakdown
java -jar blockchain-cli.jar search-metrics --detailed --verbose
🔍 Retrieving search metrics...

📊 Search Performance Metrics
============================================================

Overall Performance Summary:
• Total Searches: 45
• Average Response Time: 68.5 ms
• Cache Hit Rate: 76.2%

🔍 Detailed Metrics:
────────────────────────────────────────

📋 Search Type Breakdown:
  🔹 SIMPLE:
     Searches: 28
     Avg Time: 15.20 ms
     Total Time: 425.60 ms
     Cache Hits: 89.3%
     Avg Results: 3.2

  🔹 SECURE:
     Searches: 12
     Avg Time: 85.40 ms
     Total Time: 1024.80 ms
     Cache Hits: 45.8%
     Avg Results: 2.1

  🔹 INTELLIGENT:
     Searches: 5
     Avg Time: 124.70 ms
     Total Time: 623.50 ms
     Cache Hits: 80.0%
     Avg Results: 4.6

⚡ Performance Insights:
  🚀 Fastest search type: SIMPLE
  🐌 Slowest search type: INTELLIGENT
  📈 Overall cache hit rate: 76.2%
```

### Example 3: Performance Monitoring Workflow

```zsh
# Reset metrics for fresh analysis
java -jar blockchain-cli.jar search-metrics --reset
Search metrics have been reset

# Perform various searches to populate metrics
java -jar blockchain-cli.jar search "medical" --type SIMPLE
java -jar blockchain-cli.jar search "confidential" --type SECURE --password secret
java -jar blockchain-cli.jar search "financial" --type INTELLIGENT

# Check updated metrics
java -jar blockchain-cli.jar search-metrics --detailed
```

### Example 4: JSON Output for Monitoring Systems

```zsh
# Get metrics in JSON format for external monitoring
java -jar blockchain-cli.jar search-metrics --json
{
  "searchMetrics": {
    "totalSearches": 45,
    "totalTimeMs": 3073.90,
    "averageTimeMs": 68.31,
    "overallCacheHitRate": 0.762,
    "searchTypes": {
      "SIMPLE": {
        "searchCount": 28,
        "totalTimeMs": 425.60,
        "averageTimeMs": 15.20,
        "cacheHitRate": 0.893,
        "averageResults": 3.20,
        "minTimeMs": 8.50,
        "maxTimeMs": 25.10
      },
      "SECURE": {
        "searchCount": 12,
        "totalTimeMs": 1024.80,
        "averageTimeMs": 85.40,
        "cacheHitRate": 0.458,
        "averageResults": 2.10,
        "minTimeMs": 45.20,
        "maxTimeMs": 156.80
      }
    }
  },
  "timestamp": "2025-07-21T10:30:45.123Z"
}

# Pipe to monitoring systems
java -jar blockchain-cli.jar search-metrics --json | curl -X POST -H "Content-Type: application/json" -d @- http://monitoring-system/api/metrics
```

### Example 5: Performance Optimization Analysis

```zsh
# Analyze performance trends over time
echo "📈 Search Performance Analysis Report"
echo "===================================="

# Get current metrics
CURRENT_METRICS=$(java -jar blockchain-cli.jar search-metrics --json)

# Extract key metrics for analysis
echo "Current Performance Summary:"
echo "$CURRENT_METRICS" | grep -E "(totalSearches|averageTimeMs|overallCacheHitRate)"

# Performance recommendations based on metrics
java -jar blockchain-cli.jar search-metrics --detailed | grep "Performance Insights" -A 5

# Reset and test specific search patterns
java -jar blockchain-cli.jar search-metrics --reset
echo "Testing search performance patterns..."

# Test SIMPLE searches (should be fastest)
for term in "medical" "finance" "legal" "technical"; do
    time java -jar blockchain-cli.jar search "$term" --type SIMPLE >/dev/null
done

# Test SECURE searches (with encryption overhead)
for term in "confidential" "private" "secret"; do
    time java -jar blockchain-cli.jar search "$term" --type SECURE --password test123 >/dev/null
done

# Get performance comparison
java -jar blockchain-cli.jar search-metrics --detailed
```

## ⚡ Performance Monitoring Examples

The `performance` command provides comprehensive system performance metrics, monitoring, and health analysis capabilities.

### Example 1: Overall Performance Overview

```zsh
# Display complete performance overview (no specific flags)
java -jar blockchain-cli.jar performance
📊 System Performance Overview
================================================================================
📅 Timestamp: 2025-07-21 10:30:45

⚡ Performance Summary
──────────────────────────────────────────────────
Average Response Time: 45.2 ms
Peak Response Time: 234.5 ms
Throughput: 125 operations/second
Active Threads: 8
System Uptime: 2 hours 15 minutes

💚 System Health
──────────────────────────────────────────────────
✅ System Status: Healthy
CPU Usage: 15.3%
Memory Usage: 42.8%
Thread Count: 8 active
Blockchain Integrity: Valid

🔍 Search Performance
──────────────────────────────────────────────────
Average Search Time: 68.50 ms
Cache Hit Rate: 76.2%
Total Searches: 45

💾 Memory Status
──────────────────────────────────────────────────
Used Memory: 445.2 MB (42.8%)
Free Memory: 595.8 MB
Max Memory: 1041.0 MB

🚨 Alert Status
──────────────────────────────────────────────────
✅ No active alerts

# With detailed tips
java -jar blockchain-cli.jar performance --detailed
[... all above output ...]

📋 Tips for Performance Analysis:
  • Use --system for detailed system metrics
  • Use --search for comprehensive search analysis
  • Use --memory for memory management details
  • Use --alerts for alert statistics
  • Use --json for machine-readable output
  • Use --reset to clear all metrics
```

### Example 2: System Metrics Analysis

```zsh
# Show detailed system performance metrics
java -jar blockchain-cli.jar performance --system
🖥️  System Performance Metrics
============================================================

⚡ Performance Report:
──────────────────────────────────────
Average Response Time: 45.2 ms
Peak Response Time: 234.5 ms
Minimum Response Time: 8.3 ms
Throughput: 125 ops/sec
Total Operations: 1,234
Active Threads: 8
System Uptime: 2 hours 15 minutes

💾 Memory Management Details
────────────────────────────────────────
Used Memory: 445.2 MB
Free Memory: 595.8 MB
Total Memory: 1041.0 MB
Max Memory: 1041.0 MB
Memory Usage: 42.8%
Service Running: ✅ Yes
Last Cleanup: Mon Jul 21 09:15:22 UTC 2025
Last GC: Mon Jul 21 10:20:45 UTC 2025

# System metrics with verbose output
java -jar blockchain-cli.jar performance --system --verbose
🔍 Collecting system performance metrics...
🖥️  System Performance Metrics
[... detailed output ...]
```

### Example 3: Search Performance Metrics

```zsh
# Show detailed search performance analysis
java -jar blockchain-cli.jar performance --search --detailed
🔍 Search Performance Metrics
============================================================

📊 Overall Search Performance:
──────────────────────────────────────
Total Searches: 45
Average Response Time: 68.31 ms
Total Time: 3073.90 ms
Overall Cache Hit Rate: 76.2%

📋 Search Type Breakdown:
  🔹 SIMPLE:
     Searches: 28
     Avg Time: 15.20 ms
     Total Time: 425.60 ms
     Cache Hits: 89.3%
     Avg Results: 3.2
     Min Time: 8.50 ms
     Max Time: 25.10 ms

  🔹 SECURE:
     Searches: 12
     Avg Time: 85.40 ms
     Total Time: 1024.80 ms
     Cache Hits: 45.8%
     Avg Results: 2.1
     Min Time: 45.20 ms
     Max Time: 156.80 ms

  🔹 INTELLIGENT:
     Searches: 5
     Avg Time: 124.70 ms
     Total Time: 623.50 ms
     Cache Hits: 80.0%
     Avg Results: 4.6
     Min Time: 98.30 ms
     Max Time: 178.20 ms

💡 Performance Insights
────────────────────────────────────────
  🚀 Fastest search type: SIMPLE
  🐌 Slowest search type: INTELLIGENT
  📈 Overall cache hit rate: 76.2%
  💡 SIMPLE searches are 5.6x faster than SECURE searches
  💡 Cache is working effectively for SIMPLE searches
  ⚠️  Consider cache optimization for SECURE searches

# Brief search metrics without details
java -jar blockchain-cli.jar performance --search
🔍 Search Performance Metrics
============================================================
Average Search Time: 68.31 ms
Cache Hit Rate: 76.2%
Total Searches: 45
```

### Example 4: Cache Performance Analysis

```zsh
# Analyze cache efficiency and performance
java -jar blockchain-cli.jar performance --cache
🗄️  Cache Performance Metrics
============================================================
📊 Cache Hit Rate: 76.2%
🔍 Total Searches: 45
⏱️  Average Response: 68.31 ms

📋 Cache Performance by Search Type:
────────────────────────────────────────
SIMPLE         : 89.3% hit rate (28 searches)
SECURE         : 45.8% hit rate (12 searches)
INTELLIGENT    : 80.0% hit rate (5 searches)

# With detailed optimization tips
java -jar blockchain-cli.jar performance --cache --detailed
[... all above output ...]

💡 Cache Optimization Tips:
  • High hit rates (>80%) indicate good cache utilization
  • Low hit rates may suggest need for cache tuning
  • Monitor search patterns for cache sizing

# Cache metrics in JSON format
java -jar blockchain-cli.jar performance --cache --json
{
  "cacheMetrics": {
    "hitRate": 0.762,
    "totalSearches": 45,
    "averageSearchTimeMs": 68.31
  }
}
```

### Example 5: Alert Monitoring

```zsh
# Show active alerts and statistics
java -jar blockchain-cli.jar performance --alerts
🚨 Alert Statistics
============================================================
Alert Statistics Summary:
{
  "totalAlerts": 3,
  "criticalAlerts": 0,
  "warningAlerts": 2,
  "infoAlerts": 1,
  "activeAlerts": 1,
  "resolvedAlerts": 2
}

# With detailed alert management tips
java -jar blockchain-cli.jar performance --alerts --detailed
[... all above output ...]

💡 Alert Management Tips:
  • Monitor CRITICAL alerts immediately
  • Review WARNING alerts regularly
  • Use --reset-alerts to clear statistics
  • Check logs for detailed alert information

# Alerts in JSON format for automation
java -jar blockchain-cli.jar performance --alerts --json
{
  "totalAlerts": 3,
  "criticalAlerts": 0,
  "warningAlerts": 2,
  "infoAlerts": 1,
  "activeAlerts": 1,
  "resolvedAlerts": 2
}
```

### Example 6: Memory Management Details

```zsh
# Show detailed memory management metrics with visual bars
java -jar blockchain-cli.jar performance --memory
💾 Memory Management Metrics
============================================================
Current Memory Status:
────────────────────────────────────────
Used Memory    : ████████████░░░░░░░░░░░░░░░░░░ 445.2 MB
Memory Usage   : ████████████░░░░░░░░░░░░░░░░░░ 42.8 %

Detailed Memory Statistics:
────────────────────────────────────────
Used: 445.2 MB
Free: 595.8 MB
Total: 1041.0 MB
Max: 1041.0 MB
Usage: 42.8%
Service: ✅ Running
Last Cleanup: Mon Jul 21 09:15:22 UTC 2025
Last GC: Mon Jul 21 10:20:45 UTC 2025

# With memory management tips and health assessment
java -jar blockchain-cli.jar performance --memory --detailed
[... all above output ...]

🧹 Memory Management Options:
  • Force cleanup: Use memoryService.forceCleanup()
  • Monitor usage: Check percentage regularly
  • Automatic cleanup: Service handles routine maintenance

✅ HEALTHY MEMORY USAGE

# Memory metrics in JSON for monitoring systems
java -jar blockchain-cli.jar performance --memory --json
{
  "memoryMetrics": {
    "usedMemoryMB": 445.2,
    "freeMemoryMB": 595.8,
    "totalMemoryMB": 1041.0,
    "maxMemoryMB": 1041.0,
    "memoryUsagePercentage": 42.8,
    "serviceRunning": true,
    "lastCleanupTime": "Mon Jul 21 09:15:22 UTC 2025",
    "lastGCTime": "Mon Jul 21 10:20:45 UTC 2025"
  }
}
```

### Example 7: JSON Output for Monitoring Systems

```zsh
# Get complete performance overview in JSON
java -jar blockchain-cli.jar performance --json
{
  "performanceOverview": {
    "timestamp": "2025-07-21T10:30:45.123",
    "systemHealth": {
      "memoryUsagePercent": 42.8,
      "usedMemoryMB": 445.2,
      "freeMemoryMB": 595.8,
      "maxMemoryMB": 1041.0
    },
    "searchPerformance": {
      "averageSearchTimeMs": 68.31,
      "cacheHitRate": 0.762,
      "totalSearches": 45
    },
    "alerts": {
      "totalAlerts": 3,
      "criticalAlerts": 0,
      "warningAlerts": 2,
      "infoAlerts": 1,
      "activeAlerts": 1,
      "resolvedAlerts": 2
    }
  }
}

# Combine multiple metrics in JSON
java -jar blockchain-cli.jar performance --system --search --memory --json

# Pipe to monitoring system
java -jar blockchain-cli.jar performance --json | curl -X POST \
    -H "Content-Type: application/json" \
    -d @- \
    https://monitoring.example.com/api/metrics
```

### Example 8: Reset Operations

```zsh
# Reset all performance metrics
java -jar blockchain-cli.jar performance --reset
📊 All performance metrics have been reset

# Reset only search metrics
java -jar blockchain-cli.jar performance --reset-search
🔍 Search metrics have been reset

# Reset only alert statistics
java -jar blockchain-cli.jar performance --reset-alerts
🚨 Alert statistics have been reset

# Common workflow: reset and start fresh monitoring
java -jar blockchain-cli.jar performance --reset
java -jar blockchain-cli.jar search "test" --type SIMPLE
java -jar blockchain-cli.jar search "data" --type SECURE --password test123
java -jar blockchain-cli.jar performance --search --detailed
```

### Example 9: Performance Monitoring Workflow

```zsh
# Complete performance monitoring workflow

# Step 1: Reset metrics for clean baseline
echo "📊 Resetting all performance metrics..."
java -jar blockchain-cli.jar performance --reset

# Step 2: Perform operations to generate metrics
echo "🔄 Performing blockchain operations..."
java -jar blockchain-cli.jar add-block "Test data 1" --generate-key
java -jar blockchain-cli.jar add-block "Test data 2" --generate-key
java -jar blockchain-cli.jar search "test" --type SIMPLE
java -jar blockchain-cli.jar search "test" --type SECURE --password test123
java -jar blockchain-cli.jar validate

# Step 3: Collect comprehensive metrics
echo "📈 Collecting performance metrics..."
java -jar blockchain-cli.jar performance --detailed > performance-report.txt

# Step 4: Analyze specific areas
echo "🔍 Analyzing search performance..."
java -jar blockchain-cli.jar performance --search --detailed

echo "💾 Analyzing memory usage..."
java -jar blockchain-cli.jar performance --memory --detailed

echo "🚨 Checking for alerts..."
java -jar blockchain-cli.jar performance --alerts

# Step 5: Export metrics for analysis
echo "💾 Exporting metrics to JSON..."
java -jar blockchain-cli.jar performance --json > performance-metrics-$(date +%Y%m%d).json

echo "✅ Performance monitoring workflow completed!"
```

### Example 10: Integration with External Monitoring

```zsh
#!/usr/bin/env zsh
# Performance monitoring script for external systems

MONITORING_ENDPOINT="https://monitoring.example.com/api/metrics"
ALERT_WEBHOOK="https://alerts.example.com/webhook"

# Function to send metrics to monitoring system
send_metrics() {
    echo "📤 Sending performance metrics..."
    METRICS=$(java -jar blockchain-cli.jar performance --json)

    curl -X POST \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $MONITORING_TOKEN" \
        -d "$METRICS" \
        "$MONITORING_ENDPOINT"
}

# Function to check for critical conditions
check_alerts() {
    echo "🚨 Checking for critical alerts..."

    # Get memory usage
    MEMORY_USAGE=$(java -jar blockchain-cli.jar performance --memory --json | \
        jq -r '.memoryMetrics.memoryUsagePercentage')

    # Alert if memory usage is high
    if (( $(echo "$MEMORY_USAGE > 85.0" | bc -l) )); then
        echo "⚠️  HIGH MEMORY USAGE: ${MEMORY_USAGE}%"

        ALERT_DATA=$(cat <<EOF
{
  "severity": "warning",
  "service": "blockchain-cli",
  "metric": "memory_usage",
  "value": $MEMORY_USAGE,
  "threshold": 85.0,
  "message": "Memory usage is above threshold",
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
}
EOF
)

        curl -X POST \
            -H "Content-Type: application/json" \
            -d "$ALERT_DATA" \
            "$ALERT_WEBHOOK"
    fi

    # Check search performance
    AVG_SEARCH_TIME=$(java -jar blockchain-cli.jar performance --search --json | \
        jq -r '.searchPerformance.averageSearchTimeMs')

    if (( $(echo "$AVG_SEARCH_TIME > 200.0" | bc -l) )); then
        echo "⚠️  SLOW SEARCH PERFORMANCE: ${AVG_SEARCH_TIME}ms"
    fi
}

# Function to generate performance report
generate_report() {
    echo "📋 Generating performance report..."
    REPORT_FILE="performance-report-$(date +%Y%m%d-%H%M%S).txt"

    {
        echo "Performance Report"
        echo "=================="
        echo "Generated: $(date)"
        echo ""
        echo "Overall Metrics:"
        java -jar blockchain-cli.jar performance --detailed
        echo ""
        echo "System Metrics:"
        java -jar blockchain-cli.jar performance --system
        echo ""
        echo "Search Performance:"
        java -jar blockchain-cli.jar performance --search --detailed
        echo ""
        echo "Memory Status:"
        java -jar blockchain-cli.jar performance --memory --detailed
    } > "$REPORT_FILE"

    echo "✅ Report saved: $REPORT_FILE"
}

# Main monitoring loop
echo "🚀 Starting performance monitoring..."

while true; do
    send_metrics
    check_alerts

    # Generate hourly report
    if [ $(date +%M) -eq 00 ]; then
        generate_report
    fi

    # Wait 5 minutes between checks
    sleep 300
done
```

### Example 11: Troubleshooting Performance Issues

```zsh
# Scenario 1: High memory usage detected
echo "⚠️  Investigating high memory usage..."

# Check detailed memory metrics
java -jar blockchain-cli.jar performance --memory --detailed
💾 Memory Management Metrics
============================================================
[... memory details ...]

⚠️  HIGH MEMORY USAGE - Consider forcing cleanup

# Solution: Force cleanup if needed
java -jar blockchain-cli.jar performance --reset
echo "✅ Metrics reset, memory cleanup initiated"

# Verify improvement
java -jar blockchain-cli.jar performance --memory
💾 Memory Status: 38.2% (Improved from 92.5%)

# Scenario 2: Slow search performance
echo "🔍 Investigating slow search performance..."

# Analyze search metrics
java -jar blockchain-cli.jar performance --search --detailed
🔍 Search Performance Metrics
============================================================
⚠️  SECURE searches averaging 256.80 ms
💡 Consider cache optimization for SECURE searches

# Check cache performance
java -jar blockchain-cli.jar performance --cache --detailed
🗄️  Cache Hit Rate: 23.5% (Low)
💡 Low hit rates may suggest need for cache tuning

# Solution: Reset and monitor
java -jar blockchain-cli.jar performance --reset-search
java -jar blockchain-cli.jar performance --search --detailed

# Scenario 3: System health check
echo "💚 Performing comprehensive system health check..."

# Get overall system status
java -jar blockchain-cli.jar performance --verbose
🔍 Collecting performance metrics...
📊 System Performance Overview
[... comprehensive output ...]

# Check for active alerts
java -jar blockchain-cli.jar performance --alerts --detailed
🚨 Alert Statistics
[... alert details ...]

# Verify blockchain integrity
java -jar blockchain-cli.jar validate --detailed
✅ Blockchain integrity confirmed

# Generate complete diagnostic report
{
    echo "=== Diagnostic Report ==="
    echo "Generated: $(date)"
    echo ""
    java -jar blockchain-cli.jar performance --detailed
    echo ""
    java -jar blockchain-cli.jar status --detailed
    echo ""
    java -jar blockchain-cli.jar validate --detailed
} > diagnostic-report-$(date +%Y%m%d).txt

echo "✅ Diagnostic report saved"

# Scenario 4: Performance comparison over time
echo "📊 Comparing performance metrics..."

# Baseline measurement
java -jar blockchain-cli.jar performance --reset
echo "📌 Baseline established"

# Perform operations
for i in {1..10}; do
    java -jar blockchain-cli.jar add-block "Performance test $i" --generate-key
    java -jar blockchain-cli.jar search "test" --type SIMPLE
done

# Measure after operations
java -jar blockchain-cli.jar performance --json > metrics-after.json
echo "📈 Metrics captured after operations"

# Compare (using jq for JSON parsing)
BEFORE_AVG=$(jq -r '.performanceOverview.searchPerformance.averageSearchTimeMs' metrics-before.json)
AFTER_AVG=$(jq -r '.performanceOverview.searchPerformance.averageSearchTimeMs' metrics-after.json)

echo "Search Performance Comparison:"
echo "  Before: ${BEFORE_AVG}ms"
echo "  After: ${AFTER_AVG}ms"
```

## 🔍 Enhanced Search Examples

The enhanced `search` command now supports multiple search types and advanced filtering options using modern search APIs.

### Example 1: Search Type Comparison

```zsh
# SIMPLE search - fast public metadata search
java -jar blockchain-cli.jar search "transaction" --type SIMPLE --verbose
🔍 Performing SIMPLE search for: 'transaction'
🔍 Search completed in 12ms, found 5 results

# SECURE search - encrypted content search (requires password)
java -jar blockchain-cli.jar search "confidential" --type SECURE --password mypassword --verbose
🔍 Performing SECURE search for: 'confidential'
🔍 Search completed in 78ms, found 3 results

# INTELLIGENT search - adaptive search strategy
java -jar blockchain-cli.jar search "medical data" --type INTELLIGENT --verbose
🔍 Performing INTELLIGENT search for: 'medical data'
🔍 Search completed in 45ms, found 7 results

# ADVANCED search - full-featured search
java -jar blockchain-cli.jar search "contract" --type ADVANCED --password secret --verbose
🔍 Performing ADVANCED search for: 'contract'
🔍 Search completed in 124ms, found 4 results
```

### Example 2: Special Search Operations

```zsh
# Search by exact block hash
java -jar blockchain-cli.jar search --hash "a1b2c3d4e5f6789..." --verbose
🔍 Searching by hash: a1b2c3d4e5f6789...
🔍 Search completed in 2ms, found 1 results

# Search by specific block number
java -jar blockchain-cli.jar search --block-number 42 --detailed
🔍 Search Results (block-number)
============================================================
Found 1 blocks matching search criteria.

📦 Block #42
   🕒 2025-07-21T09:30:15.456Z
   👤 Creator: Alice
   🔐 Encrypted: Yes
   📝 Data: [Encrypted - 128 chars]
   🏷️ Category: MEDICAL

# Find all encrypted blocks
java -jar blockchain-cli.jar search --encrypted-only --limit 10
🔍 Search Results (encrypted-blocks)
============================================================
Found 8 blocks matching search criteria.

# Search by category
java -jar blockchain-cli.jar search --category MEDICAL --detailed --verbose
🔍 Searching by category: MEDICAL
🔍 Search completed in 25ms, found 5 results

📦 Block #8 - Medical consultation
📦 Block #12 - Patient records
📦 Block #15 - Medical device data
📦 Block #18 - Lab results
📦 Block #21 - Treatment plan
```

### Example 3: Advanced Filtering and Options

```zsh
# Search with username filter and date range
java -jar blockchain-cli.jar search "report" \
    --username Alice \
    --date-from 2025-07-01 \
    --date-to 2025-07-21 \
    --limit 5 \
    --detailed

# Complex search with multiple filters
java -jar blockchain-cli.jar search --category FINANCE \
    --encrypted-only \
    --username CFO \
    --limit 10 \
    --json \
    --verbose

# Search with JSON output for automation
java -jar blockchain-cli.jar search "audit" --type SECURE --password audit123 --json
{
  "searchType": "secure",
  "searchTime": 67,
  "resultsLimited": false,
  "results": [
    {
      "blockNumber": 15,
      "hash": "abc123...",
      "timestamp": "2025-07-21T09:30:15.456Z",
      "encrypted": true,
      "category": "FINANCE",
      "dataLength": 256
    }
  ]
}
```

### Example 4: Error Handling and Troubleshooting

```zsh
# Missing password for secure search
java -jar blockchain-cli.jar search "confidential" --type SECURE
❌ Password required for secure search
   Use --password to provide password

# No search criteria specified
java -jar blockchain-cli.jar search
❌ No search criteria specified
   Available search types:
   • SIMPLE: Fast public metadata search (default)
   • SECURE: Encrypted content search (requires --password)
   • INTELLIGENT: Adaptive search strategy
   • ADVANCED: Full-featured search with level control
   
   Special searches:
   • --hash <block-hash>: Find specific block
   • --block-number <number>: Find by number
   • --category <category>: Search by category

# Search strategy when no results found
java -jar blockchain-cli.jar search "rare-term" --type SIMPLE --verbose
🔍 Performing SIMPLE search for: 'rare-term'
🔍 Search completed in 15ms, found 0 results
No blocks found matching search criteria.

# Try broader search
java -jar blockchain-cli.jar search "rare-term" --type INTELLIGENT --verbose
🔍 Performing INTELLIGENT search for: 'rare-term'
🔍 Search completed in 45ms, found 0 results

# Try exhaustive search including off-chain
java -jar blockchain-cli.jar search "rare-term" --type ADVANCED --verbose
🔍 Performing ADVANCED search for: 'rare-term'
🔍 Search completed in 180ms, found 1 results
```

### Example 5: Datetime Range Search

The `--datetime-from` and `--datetime-to` parameters provide more precise filtering than date-only searches by including time (HH:mm).

```zsh
# Search with precise datetime range
java -jar blockchain-cli.jar search "transaction" \
    --datetime-from "2025-07-21 09:00" \
    --datetime-to "2025-07-21 17:30" \
    --detailed
🔍 Search Results (datetime-range)
============================================================
Found 12 blocks matching search criteria.

📦 Block #45
   🕒 2025-07-21T09:15:23.456Z
   👤 Creator: Alice
   📝 Data: Transaction T-1001 processed

📦 Block #48
   🕒 2025-07-21T14:22:45.123Z
   👤 Creator: Bob
   📝 Data: Transaction T-1015 completed

# Combine datetime range with other filters
java -jar blockchain-cli.jar search "financial" \
    --datetime-from "2025-07-20 08:00" \
    --datetime-to "2025-07-21 18:00" \
    --category FINANCE \
    --username CFO \
    --detailed \
    --verbose
🔍 Searching in datetime range: 2025-07-20 08:00 to 2025-07-21 18:00
🔍 Category filter: FINANCE
🔍 Username filter: CFO
🔍 Search completed in 45ms, found 8 results

# Single datetime boundary (from specified time onwards)
java -jar blockchain-cli.jar search "audit" \
    --datetime-from "2025-07-21 12:00" \
    --limit 10

# Single datetime boundary (up to specified time)
java -jar blockchain-cli.jar search "report" \
    --datetime-to "2025-07-21 16:00" \
    --limit 10

# Combine with JSON output for automation
java -jar blockchain-cli.jar search "medical" \
    --datetime-from "2025-07-21 00:00" \
    --datetime-to "2025-07-21 23:59" \
    --category MEDICAL \
    --json
{
  "searchType": "simple",
  "datetimeFrom": "2025-07-21T00:00:00Z",
  "datetimeTo": "2025-07-21T23:59:00Z",
  "resultsFound": 15,
  "results": [
    {
      "blockNumber": 12,
      "timestamp": "2025-07-21T08:30:15.456Z",
      "category": "MEDICAL",
      "creator": "Dr-Smith"
    }
  ]
}
```

### Example 6: Search Term Validation

The `--validate-term` flag validates the search term before executing the search, helping catch common issues early.

```zsh
# Validate search term before searching
java -jar blockchain-cli.jar search "med*cal" --validate-term --verbose
🔍 Validating search term: 'med*cal'
⚠️  Search term contains wildcard characters that may not work as expected
⚠️  Special characters detected: *
💡 Consider using exact terms for better results
🔍 Proceeding with search...
🔍 Search completed in 25ms, found 3 results

# Validation catches empty or invalid terms
java -jar blockchain-cli.jar search "" --validate-term
❌ Invalid search term: empty or whitespace-only
💡 Please provide a valid search term

# Validation warns about very short terms
java -jar blockchain-cli.jar search "a" --validate-term --verbose
🔍 Validating search term: 'a'
⚠️  Search term is very short (1 character)
⚠️  This may return too many results or be inefficient
💡 Consider using more specific search terms
🔍 Proceeding with search...

# Validation with secure search and password
java -jar blockchain-cli.jar search "confidential" \
    --type SECURE \
    --password mypass \
    --validate-term \
    --verbose
🔍 Validating search term: 'confidential'
✅ Search term validation passed
🔍 Performing SECURE search...
🔍 Search completed in 67ms, found 5 results

# Combine validation with datetime range
java -jar blockchain-cli.jar search "transaction" \
    --validate-term \
    --datetime-from "2025-07-21 09:00" \
    --datetime-to "2025-07-21 17:00" \
    --detailed
🔍 Validating search term: 'transaction'
✅ Search term validation passed
🔍 Search completed in 38ms, found 12 results
```

### Example 7: User-Specific Block Filtering

The `--my-blocks` and `--received` flags filter results to show only blocks created by or sent to a specific user.

```zsh
# Show only blocks created by Alice
java -jar blockchain-cli.jar search --my-blocks \
    --username Alice \
    --detailed
🔍 Search Results (my-blocks for Alice)
============================================================
Found 8 blocks created by Alice.

📦 Block #12
   🕒 2025-07-21T09:15:23.456Z
   👤 Creator: Alice
   📝 Data: Patient consultation notes
   🏷️ Category: MEDICAL

📦 Block #15
   🕒 2025-07-21T10:30:45.123Z
   👤 Creator: Alice
   📝 Data: Lab results for PAT-123
   🏷️ Category: MEDICAL

# Show only blocks received by Bob (recipient-encrypted)
java -jar blockchain-cli.jar search --received \
    --username Bob \
    --detailed
🔍 Search Results (received by Bob)
============================================================
Found 5 blocks sent to Bob.

📦 Block #18
   🕒 2025-07-21T11:00:15.789Z
   👤 Creator: Alice
   🎯 Recipient: Bob
   🔐 Encrypted: Yes (recipient-based)
   📝 Data: [Encrypted - requires Bob's private key]

📦 Block #22
   🕒 2025-07-21T14:20:30.456Z
   👤 Creator: Charlie
   🎯 Recipient: Bob
   🔐 Encrypted: Yes (recipient-based)
   📝 Data: [Encrypted - requires Bob's private key]

# Combine with search term
java -jar blockchain-cli.jar search "contract" \
    --my-blocks \
    --username Legal-Counsel \
    --category LEGAL \
    --detailed
🔍 Search Results (my-blocks for Legal-Counsel, term: 'contract')
============================================================
Found 3 blocks matching criteria.

# Combine with datetime range
java -jar blockchain-cli.jar search --received \
    --username CFO \
    --datetime-from "2025-07-20 00:00" \
    --datetime-to "2025-07-21 23:59" \
    --category FINANCE \
    --detailed
🔍 Search Results (received by CFO, datetime range)
============================================================
Found 7 financial blocks sent to CFO in the specified period.

# JSON output for automation
java -jar blockchain-cli.jar search --my-blocks \
    --username Alice \
    --json
{
  "searchType": "simple",
  "filterType": "my-blocks",
  "username": "Alice",
  "resultsFound": 8,
  "results": [
    {
      "blockNumber": 12,
      "timestamp": "2025-07-21T09:15:23.456Z",
      "creator": "Alice",
      "category": "MEDICAL",
      "encrypted": false
    }
  ]
}

java -jar blockchain-cli.jar search --received \
    --username Bob \
    --json
{
  "searchType": "simple",
  "filterType": "received",
  "username": "Bob",
  "resultsFound": 5,
  "results": [
    {
      "blockNumber": 18,
      "timestamp": "2025-07-21T11:00:15.789Z",
      "creator": "Alice",
      "recipient": "Bob",
      "encrypted": true,
      "encryptionType": "recipient-based"
    }
  ]
}
```

### Example 8: Combined Advanced Filtering

Combine multiple new parameters for powerful, precise searches.

```zsh
# Find all blocks Alice created for Bob in a specific timeframe
java -jar blockchain-cli.jar search --my-blocks \
    --username Alice \
    --datetime-from "2025-07-21 08:00" \
    --datetime-to "2025-07-21 18:00" \
    --validate-term \
    --category MEDICAL \
    --detailed \
    --verbose
🔍 Validating search parameters...
✅ All parameters valid
🔍 Filtering: my-blocks for Alice
🔍 Datetime range: 2025-07-21 08:00 to 2025-07-21 18:00
🔍 Category: MEDICAL
🔍 Search completed in 42ms, found 6 results

# Find all encrypted messages received by CFO during business hours
java -jar blockchain-cli.jar search --received \
    --username CFO \
    --datetime-from "2025-07-21 09:00" \
    --datetime-to "2025-07-21 17:00" \
    --encrypted-only \
    --category FINANCE \
    --detailed
🔍 Search Results (received by CFO, business hours)
============================================================
Found 4 encrypted financial blocks sent to CFO.

# Search with validation and multiple filters
java -jar blockchain-cli.jar search "quarterly report" \
    --validate-term \
    --my-blocks \
    --username Accounting \
    --datetime-from "2025-07-01 00:00" \
    --datetime-to "2025-07-31 23:59" \
    --category FINANCE \
    --limit 20 \
    --detailed \
    --verbose
🔍 Validating search term: 'quarterly report'
✅ Search term validation passed
🔍 Filtering: my-blocks for Accounting
🔍 Datetime range: July 2025
🔍 Category: FINANCE
🔍 Search completed in 58ms, found 12 results

# Complex workflow: validate, filter by received, datetime, and output JSON
java -jar blockchain-cli.jar search "approval" \
    --validate-term \
    --received \
    --username Manager \
    --datetime-from "2025-07-21 09:00" \
    --datetime-to "2025-07-21 17:00" \
    --json \
    --verbose
🔍 Validating search term: 'approval'
✅ Search term validation passed
🔍 Filtering: received by Manager
🔍 Datetime range: 2025-07-21 09:00 to 2025-07-21 17:00
🔍 Search completed in 35ms, found 8 results
{
  "searchTerm": "approval",
  "validated": true,
  "filterType": "received",
  "username": "Manager",
  "datetimeFrom": "2025-07-21T09:00:00Z",
  "datetimeTo": "2025-07-21T17:00:00Z",
  "resultsFound": 8,
  "searchTimeMs": 35
}
```

### Example 9: Automated Reporting with New Parameters

Use the new search parameters for automated reporting and monitoring scripts.

```zsh
#!/usr/bin/env zsh
# daily-blocks-report.zsh - Generate daily report of user activities

DATE=$(date '+%Y-%m-%d')
REPORT_FILE="blockchain-report-${DATE}.json"

echo "📊 Generating blockchain activity report for ${DATE}..."

# Get all blocks created by each user today
for USER in Alice Bob Charlie Legal-Counsel CFO; do
    echo "  📋 Collecting blocks created by ${USER}..."
    java -jar blockchain-cli.jar search --my-blocks \
        --username "${USER}" \
        --datetime-from "${DATE} 00:00" \
        --datetime-to "${DATE} 23:59" \
        --json > "blocks-created-${USER}.json"

    echo "  📬 Collecting blocks received by ${USER}..."
    java -jar blockchain-cli.jar search --received \
        --username "${USER}" \
        --datetime-from "${DATE} 00:00" \
        --datetime-to "${DATE} 23:59" \
        --json > "blocks-received-${USER}.json"
done

# Combine all reports
echo "{"
echo "  \"reportDate\": \"${DATE}\","
echo "  \"generatedAt\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\","
echo "  \"userActivities\": ["
# ... combine JSON files ...
echo "  ]"
echo "}" > "${REPORT_FILE}"

echo "✅ Report generated: ${REPORT_FILE}"

# Example output structure:
# {
#   "reportDate": "2025-07-21",
#   "generatedAt": "2025-07-21T18:30:00Z",
#   "userActivities": [
#     {
#       "username": "Alice",
#       "blocksCreated": 8,
#       "blocksReceived": 5,
#       "categories": ["MEDICAL", "GENERAL"]
#     },
#     {
#       "username": "CFO",
#       "blocksCreated": 3,
#       "blocksReceived": 12,
#       "categories": ["FINANCE"]
#     }
#   ]
# }
```

### Example 10: Security Audit with User Filtering

Use user-specific filtering for security audits and compliance checks.

```zsh
# Audit all blocks created by a specific user in the last 30 days
AUDIT_USER="Legal-Counsel"
START_DATE=$(date -v-30d '+%Y-%m-%d 00:00')
END_DATE=$(date '+%Y-%m-%d 23:59')

echo "🔍 Security Audit for user: ${AUDIT_USER}"
echo "📅 Period: ${START_DATE} to ${END_DATE}"

# Get all blocks created by user
java -jar blockchain-cli.jar search --my-blocks \
    --username "${AUDIT_USER}" \
    --datetime-from "${START_DATE}" \
    --datetime-to "${END_DATE}" \
    --validate-term \
    --detailed \
    --verbose > "audit-created-${AUDIT_USER}.txt"

# Get all blocks received by user
java -jar blockchain-cli.jar search --received \
    --username "${AUDIT_USER}" \
    --datetime-from "${START_DATE}" \
    --datetime-to "${END_DATE}" \
    --detailed \
    --verbose > "audit-received-${AUDIT_USER}.txt"

# Check for encrypted communications
java -jar blockchain-cli.jar search --received \
    --username "${AUDIT_USER}" \
    --datetime-from "${START_DATE}" \
    --datetime-to "${END_DATE}" \
    --encrypted-only \
    --json | jq '.resultsFound'

echo "✅ Audit completed. Results saved to:"
echo "   - audit-created-${AUDIT_USER}.txt"
echo "   - audit-received-${AUDIT_USER}.txt"

# Example audit workflow for compliance
# 1. Verify all legal documents are properly signed
java -jar blockchain-cli.jar search --my-blocks \
    --username Legal-Counsel \
    --category LEGAL \
    --datetime-from "2025-07-01 00:00" \
    --datetime-to "2025-07-31 23:59" \
    --detailed

# 2. Check all financial transactions for CFO
java -jar blockchain-cli.jar search --received \
    --username CFO \
    --category FINANCE \
    --datetime-from "2025-07-01 00:00" \
    --datetime-to "2025-07-31 23:59" \
    --encrypted-only \
    --detailed

# 3. Verify medical records handling
java -jar blockchain-cli.jar search --my-blocks \
    --username Dr-Smith \
    --category MEDICAL \
    --datetime-from "2025-07-01 00:00" \
    --datetime-to "2025-07-31 23:59" \
    --encrypted-only \
    --detailed
```

### Example 11: Troubleshooting New Search Parameters

Common issues and solutions when using the new search parameters.

```zsh
# Issue 1: No results with --my-blocks
java -jar blockchain-cli.jar search --my-blocks --username Alice
# ❌ No blocks found

# Solution: Verify username exists in blockchain
java -jar blockchain-cli.jar list-keys
java -jar blockchain-cli.jar search --username Alice --detailed
# If no results, user may not have created any blocks

# Issue 2: Invalid datetime format
java -jar blockchain-cli.jar search --datetime-from "2025-07-21" --datetime-to "2025-07-22"
# ❌ Invalid datetime format. Expected: yyyy-MM-dd HH:mm

# Solution: Include time in format
java -jar blockchain-cli.jar search --datetime-from "2025-07-21 00:00" --datetime-to "2025-07-22 23:59"
# ✅ Search completed successfully

# Issue 3: --validate-term with empty term
java -jar blockchain-cli.jar search "" --validate-term
# ❌ Invalid search term: empty or whitespace-only

# Solution: Provide valid search term
java -jar blockchain-cli.jar search "transaction" --validate-term
# ✅ Search term validation passed

# Issue 4: --received without --username
java -jar blockchain-cli.jar search --received
# ❌ --received requires --username parameter

# Solution: Specify username
java -jar blockchain-cli.jar search --received --username Bob --detailed
# ✅ Search completed successfully

# Issue 5: Combining --my-blocks and --received
java -jar blockchain-cli.jar search --my-blocks --received --username Alice
# ❌ Cannot use both --my-blocks and --received simultaneously

# Solution: Use one filter at a time
java -jar blockchain-cli.jar search --my-blocks --username Alice
# OR
java -jar blockchain-cli.jar search --received --username Alice

# Issue 6: Datetime range returns no results
java -jar blockchain-cli.jar search --datetime-from "2025-07-21 09:00" --datetime-to "2025-07-21 10:00" --verbose
# 🔍 Search completed in 15ms, found 0 results

# Solution: Verify blocks exist in that timeframe
java -jar blockchain-cli.jar status --detailed
# Check timestamps of recent blocks
java -jar blockchain-cli.jar search --datetime-from "2025-07-21 00:00" --detailed
# Expand datetime range

# Issue 7: Validation warnings but need to proceed
java -jar blockchain-cli.jar search "a" --validate-term --verbose
# ⚠️  Search term is very short (1 character)
# 🔍 Proceeding with search...

# This is a warning, not an error - search will continue
# To suppress validation, omit --validate-term flag
java -jar blockchain-cli.jar search "a" --verbose
# 🔍 Search completed without validation

# Verify correct parameter usage
java -jar blockchain-cli.jar search --help
# Shows all available search parameters and their descriptions
```

## 🚀 Quick Start Examples

### Example 1: First-Time Setup with Secure Keys
```zsh
# Step 1: Check if CLI is working
java -jar blockchain-cli.jar --version
# Expected: 1.0.5

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

## ⚙️ Configuration Management Examples

The `config` command provides persistent configuration management for the CLI, allowing you to customize behavior and save preferences across sessions.

### Example 1: View Current Configuration

```zsh
# Display all current configuration settings
java -jar blockchain-cli.jar config show
⚙️ Current CLI Configuration
============================================================
📋 Configuration Settings:
   output.format = text
   search.limit = 50
   verbose.mode = false
   detailed.output = false
   search.type.default = SIMPLE
   search.level.default = INCLUDE_DATA
   offchain.threshold = 524288
   log.level = INFO
   command.timeout = 30000
   max.results = 100
   enable.metrics = true
   auto.cleanup = false
   require.confirmation = true
   enable.audit.log = false

💡 Configuration file: ~/.blockchain-cli/config.properties
💡 Use 'config set --key <key> --value <value>' to change settings
```

### Example 2: Set Configuration Values

```zsh
# Change output format preference
java -jar blockchain-cli.jar config set --key output.format --value json
✅ Configuration updated: output.format = json

# Increase search result limit
java -jar blockchain-cli.jar config set --key search.limit --value 100
✅ Configuration updated: search.limit = 100

# Enable verbose mode by default
java -jar blockchain-cli.jar config set --key verbose.mode --value true
✅ Configuration updated: verbose.mode = true

# Set default search type to SECURE
java -jar blockchain-cli.jar config set --key search.type.default --value SECURE
✅ Configuration updated: search.type.default = SECURE

# Adjust off-chain storage threshold (512KB = 524288 bytes)
java -jar blockchain-cli.jar config set --key offchain.threshold --value 1048576
✅ Configuration updated: offchain.threshold = 1048576
ℹ️  Large data over 1.0 MB will be stored off-chain

# Enable performance metrics tracking
java -jar blockchain-cli.jar config set --key enable.metrics --value true
✅ Configuration updated: enable.metrics = true
```

### Example 3: Configuration Profiles

```zsh
# View available configuration profiles
java -jar blockchain-cli.jar config profiles
📋 Available Configuration Profiles
============================================================

🔧 development
   Description: Development environment with verbose logging
   Settings:
   • verbose.mode = true
   • detailed.output = true
   • log.level = DEBUG
   • enable.metrics = true
   • require.confirmation = false

🚀 production
   Description: Production environment with minimal logging
   Settings:
   • verbose.mode = false
   • detailed.output = false
   • log.level = WARN
   • enable.metrics = true
   • require.confirmation = true
   • enable.audit.log = true

⚡ performance
   Description: Optimized for performance testing
   Settings:
   • output.format = json
   • search.type.default = SIMPLE
   • search.level.default = FAST_ONLY
   • enable.metrics = true
   • max.results = 1000

🧪 testing
   Description: Testing environment with detailed output
   Settings:
   • verbose.mode = true
   • detailed.output = true
   • log.level = TRACE
   • auto.cleanup = true
   • require.confirmation = false

💡 Use 'config apply-profile --profile <name>' to apply a profile
```

### Example 4: Apply Configuration Profiles

```zsh
# Apply production profile for secure operations
java -jar blockchain-cli.jar config apply-profile --profile production
✅ Applied configuration profile: production
📋 Updated settings:
   verbose.mode = false
   detailed.output = false
   log.level = WARN
   enable.metrics = true
   require.confirmation = true
   enable.audit.log = true

# Apply development profile for debugging
java -jar blockchain-cli.jar config apply-profile --profile development
✅ Applied configuration profile: development
📋 Updated settings:
   verbose.mode = true
   detailed.output = true
   log.level = DEBUG
   enable.metrics = true
   require.confirmation = false

# Apply performance profile for benchmarking
java -jar blockchain-cli.jar config apply-profile --profile performance
✅ Applied configuration profile: performance
📋 Updated settings:
   output.format = json
   search.type.default = SIMPLE
   search.level.default = FAST_ONLY
   enable.metrics = true
   max.results = 1000
```

### Example 5: Export and Import Configuration

```zsh
# Export current configuration to share with team
java -jar blockchain-cli.jar config export --file my-config.properties
✅ Configuration exported to: my-config.properties
ℹ️  Share this file with your team for consistent settings

# View exported configuration file
cat my-config.properties
# Blockchain CLI Configuration
# Exported: 2025-07-21T10:30:45.123Z
output.format=json
search.limit=100
verbose.mode=true
search.type.default=SECURE
offchain.threshold=1048576
enable.metrics=true

# Import configuration from file
java -jar blockchain-cli.jar config import --file team-config.properties
✅ Configuration imported from: team-config.properties
📋 Updated 12 configuration settings
⚠️  Restart CLI for some changes to take effect

# Import with backup of current settings
java -jar blockchain-cli.jar config export --file backup-config.properties
java -jar blockchain-cli.jar config import --file new-config.properties
✅ Configuration imported successfully
ℹ️  Previous configuration saved to: backup-config.properties
```

### Example 6: Reset Configuration

```zsh
# Reset all settings to defaults
java -jar blockchain-cli.jar config reset
⚠️  This will reset all configuration to default values.
⚠️  Continue? (yes/no): yes
✅ Configuration reset to defaults
📋 Default settings restored:
   output.format = text
   search.limit = 50
   verbose.mode = false
   search.type.default = SIMPLE
   offchain.threshold = 524288

# Reset with automatic yes (useful for scripts)
java -jar blockchain-cli.jar config reset --yes
✅ Configuration reset to defaults
```

### Example 7: JSON Output for Automation

```zsh
# Get configuration in JSON format
java -jar blockchain-cli.jar config show --json
{
  "configuration": {
    "output.format": "json",
    "search.limit": 100,
    "verbose.mode": true,
    "detailed.output": true,
    "search.type.default": "SECURE",
    "search.level.default": "INCLUDE_DATA",
    "offchain.threshold": 1048576,
    "log.level": "INFO",
    "command.timeout": 30000,
    "max.results": 100,
    "enable.metrics": true,
    "auto.cleanup": false,
    "require.confirmation": true,
    "enable.audit.log": false
  },
  "configFile": "/Users/user/.blockchain-cli/config.properties",
  "timestamp": "2025-07-21T10:30:45.123Z"
}

# Get available profiles in JSON format
java -jar blockchain-cli.jar config profiles --json
{
  "profiles": [
    {
      "name": "development",
      "description": "Development environment with verbose logging",
      "settings": {
        "verbose.mode": "true",
        "detailed.output": "true",
        "log.level": "DEBUG"
      }
    },
    {
      "name": "production",
      "description": "Production environment with minimal logging",
      "settings": {
        "verbose.mode": "false",
        "log.level": "WARN",
        "enable.audit.log": "true"
      }
    }
  ]
}
```

### Example 8: Custom Configuration Keys

```zsh
# Set custom application-specific configuration
java -jar blockchain-cli.jar config set --key custom.company.name --value "Acme Corp"
✅ Configuration updated: custom.company.name = Acme Corp

java -jar blockchain-cli.jar config set --key custom.department --value "IT Security"
✅ Configuration updated: custom.department = IT Security

java -jar blockchain-cli.jar config set --key custom.environment --value "staging"
✅ Configuration updated: custom.environment = staging

# View all custom settings
java -jar blockchain-cli.jar config show | grep "custom\."
   custom.company.name = Acme Corp
   custom.department = IT Security
   custom.environment = staging
```

### Example 9: Configuration Demo Script

```zsh
# Run interactive configuration demo
./scripts/config-demo.zsh

# The demo script demonstrates:
# - Viewing current configuration
# - Applying different profiles
# - Setting custom values
# - Exporting and importing configuration
# - Resetting to defaults
# - JSON output for automation
```

### Example 10: Configuration Workflow for Teams

```zsh
# Team Lead: Create standardized configuration
java -jar blockchain-cli.jar config apply-profile --profile production
java -jar blockchain-cli.jar config set --key custom.team --value "Platform Team"
java -jar blockchain-cli.jar config set --key custom.project --value "Blockchain Initiative"
java -jar blockchain-cli.jar config export --file team-standard-config.properties
echo "✅ Team configuration created: team-standard-config.properties"

# Team Members: Import team configuration
java -jar blockchain-cli.jar config import --file team-standard-config.properties
echo "✅ Imported team configuration"

# Developer: Add personal preferences while keeping team settings
java -jar blockchain-cli.jar config set --key verbose.mode --value true
java -jar blockchain-cli.jar config set --key log.level --value DEBUG
echo "✅ Personal development settings applied"

# QA Tester: Use testing profile with team custom values
java -jar blockchain-cli.jar config apply-profile --profile testing
java -jar blockchain-cli.jar config import --file team-standard-config.properties
echo "✅ Testing environment configured"
```

### Example 11: Troubleshooting Configuration Issues

```zsh
# Check if configuration file exists and is readable
java -jar blockchain-cli.jar config show --verbose
🔍 Loading configuration from: ~/.blockchain-cli/config.properties
🔍 Configuration file found: true
🔍 Configuration entries loaded: 14
✅ Configuration loaded successfully

# If configuration is corrupted, reset to defaults
java -jar blockchain-cli.jar config reset
✅ Configuration reset to defaults

# Verify configuration after reset
java -jar blockchain-cli.jar config show
⚙️ Current CLI Configuration (defaults)

# Test configuration with specific command
java -jar blockchain-cli.jar config set --key search.limit --value 10
java -jar blockchain-cli.jar search "test" --json | jq '.results | length'
# Should return max 10 results
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

## 🔧 Advanced Block Creation Options

The `add-block` command supports advanced encryption and metadata options for specialized use cases.

### Example 1: Recipient-Based Encryption (Public Key)

```zsh
# Encrypt for a specific recipient using their public key
# First, ensure the recipient exists
java -jar blockchain-cli.jar add-key "Dr-Smith" --generate

# Add encrypted block for Dr-Smith (no password needed)
java -jar blockchain-cli.jar add-block "Confidential patient diagnosis for PAT-123" \
    --recipient "Dr-Smith" \
    --keywords "PAT-123,DIAGNOSIS,CONFIDENTIAL" \
    --category "MEDICAL" \
    --generate-key \
    --verbose

✅ Block Added Successfully
============================================================
📦 Block #42
📅 Timestamp: 2025-07-21T10:30:45.123Z
🔗 Hash: abc123...def456
🔐 Encrypted: Yes
👤 Recipient: Dr-Smith
📝 Data: [Encrypted content - 256 chars]

# Only Dr-Smith can decrypt this block (requires their private key)
java -jar blockchain-cli.jar search "PAT-123" --username "Dr-Smith" --password "dr-smith-password"
```

### Example 2: Difference Between Password and Recipient Encryption

```zsh
# Password-based encryption (symmetric - anyone with password can decrypt)
java -jar blockchain-cli.jar add-block "Shared team data" \
    --password "team-secret-2024" \
    --keywords "TEAM,SHARED" \
    --category "INTERNAL" \
    --generate-key

🔐 Encrypted: Yes (Password-based)
ℹ️  Anyone with the password can decrypt this block

# Recipient-based encryption (asymmetric - only recipient can decrypt)
java -jar blockchain-cli.jar add-block "Personal medical data for Dr-Jones" \
    --recipient "Dr-Jones" \
    --keywords "PERSONAL,MEDICAL" \
    --category "CONFIDENTIAL" \
    --generate-key

🔐 Encrypted: Yes (Public Key)
👤 Recipient: Dr-Jones
ℹ️  Only Dr-Jones with their private key can decrypt this block

# Combined: Multiple recipients (create separate blocks)
for recipient in "Alice" "Bob" "Charlie"; do
    java -jar blockchain-cli.jar add-block "Encrypted message for team" \
        --recipient "$recipient" \
        --keywords "TEAM-MSG,ENCRYPTED" \
        --generate-key
done
```

### Example 3: Custom Metadata (Key-Value Pairs)

```zsh
# Add block with custom metadata
java -jar blockchain-cli.jar add-block "Contract execution details" \
    --metadata "contract_id=CTR-2024-001" \
    --metadata "amount=50000" \
    --metadata "currency=USD" \
    --metadata "department=Finance" \
    --metadata "approval_level=executive" \
    --keywords "CONTRACT,FINANCE,APPROVED" \
    --category "LEGAL" \
    --generate-key \
    --detailed

✅ Block Added Successfully
============================================================
📦 Block #43
📅 Timestamp: 2025-07-21T11:15:30.456Z
🔗 Hash: def456...ghi789
📂 Category: LEGAL
🏷️  Keywords: contract finance approved
📊 Custom Metadata: contract_id=CTR-2024-001, amount=50000, currency=USD, department=Finance, approval_level=executive
📝 Data: Contract execution details

# Multiple metadata entries for detailed tracking
java -jar blockchain-cli.jar add-block "Medical procedure record" \
    --metadata "patient_id=PAT-12345" \
    --metadata "procedure=cardiac_catheterization" \
    --metadata "doctor=Dr-Smith" \
    --metadata "hospital=General-Hospital" \
    --metadata "duration_minutes=120" \
    --metadata "success=true" \
    --metadata "complications=none" \
    --keywords "MEDICAL,PROCEDURE,CARDIAC" \
    --category "MEDICAL" \
    --generate-key
```

### Example 4: Custom Metadata with JSON Output

```zsh
# Create block with metadata and get JSON output
java -jar blockchain-cli.jar add-block "API transaction log" \
    --metadata "api_endpoint=/api/v1/users" \
    --metadata "http_method=POST" \
    --metadata "response_code=201" \
    --metadata "user_agent=PostmanRuntime/7.32" \
    --metadata "ip_address=192.168.1.100" \
    --keywords "API,TRANSACTION,LOG" \
    --category "TECHNICAL" \
    --generate-key \
    --json

{
  "success": true,
  "blockNumber": 44,
  "timestamp": "2025-07-21T11:20:15.789Z",
  "hash": "ghi789jkl012...",
  "category": "TECHNICAL",
  "manualKeywords": "api transaction log",
  "encrypted": false,
  "customMetadata": "api_endpoint=/api/v1/users, http_method=POST, response_code=201, user_agent=PostmanRuntime/7.32, ip_address=192.168.1.100",
  "hasOffChainData": false
}

# Use in automation scripts
METADATA_JSON=$(java -jar blockchain-cli.jar add-block "Automated entry" \
    --metadata "script=$(basename $0)" \
    --metadata "timestamp=$(date +%s)" \
    --metadata "hostname=$(hostname)" \
    --generate-key \
    --json)

BLOCK_NUMBER=$(echo "$METADATA_JSON" | jq -r '.blockNumber')
echo "Created block #$BLOCK_NUMBER with custom metadata"
```

### Example 5: Off-Chain File with Explicit Path

```zsh
# Specify exact off-chain file path (advanced usage)
java -jar blockchain-cli.jar add-block "Large medical imaging data" \
    --off-chain \
    --off-chain-file "/secure-storage/medical/pat-123-mri.dcm" \
    --keywords "PAT-123,MRI,IMAGING" \
    --category "MEDICAL" \
    --generate-key \
    --verbose

🔍 Using explicit off-chain file path: /secure-storage/medical/pat-123-mri.dcm
💾 Data stored off-chain with encryption
✅ Block added successfully!

# Automatic off-chain (uses default location)
java -jar blockchain-cli.jar add-block "$(cat large-file.txt)" \
    --keywords "LARGE,AUTO" \
    --generate-key

💾 Large data detected - automatic off-chain storage
📁 Stored at: off-chain-data/offchain_1721565000_45.dat

# Force off-chain storage even for small data
java -jar blockchain-cli.jar add-block "Small but sensitive file" \
    --off-chain \
    --keywords "SENSITIVE,OFFCHAIN" \
    --category "CONFIDENTIAL" \
    --generate-key

💾 Forced off-chain storage (--off-chain flag)
📁 File encrypted and stored off-chain
```

### Example 6: Combining Recipient Encryption with Metadata

```zsh
# Create encrypted block with rich metadata for recipient
java -jar blockchain-cli.jar add-block "Quarterly financial report Q1 2024" \
    --recipient "CFO" \
    --metadata "quarter=Q1" \
    --metadata "year=2024" \
    --metadata "revenue=5000000" \
    --metadata "profit_margin=18.5" \
    --metadata "report_type=executive_summary" \
    --metadata "prepared_by=Finance-Team" \
    --keywords "FINANCIAL,QUARTERLY,EXECUTIVE" \
    --category "FINANCE" \
    --generate-key \
    --detailed \
    --verbose

🔍 Creating block with recipient encryption...
🔐 Encrypting for recipient: CFO
📊 Adding custom metadata: 6 entries
✅ Block Added Successfully
============================================================
📦 Block #46
📅 Timestamp: 2025-07-21T11:45:22.456Z
🔗 Hash: jkl012...mno345
🔐 Encrypted: Yes
👤 Recipient: CFO
📂 Category: FINANCE
🏷️  Keywords: financial quarterly executive
📊 Custom Metadata: quarter=Q1, year=2024, revenue=5000000, profit_margin=18.5, report_type=executive_summary, prepared_by=Finance-Team
📝 Data: [Encrypted content - 512 chars]

# CFO can decrypt and view
java -jar blockchain-cli.jar search "Q1" --username "CFO" --password "cfo-password"
```

### Example 7: Real-World Use Case - Audit Trail with Metadata

```zsh
# Create comprehensive audit trail with metadata
java -jar blockchain-cli.jar add-block "User login successful" \
    --metadata "user_id=12345" \
    --metadata "username=alice@company.com" \
    --metadata "ip_address=192.168.1.50" \
    --metadata "device=laptop-alice-01" \
    --metadata "browser=Chrome/120.0" \
    --metadata "mfa_verified=true" \
    --metadata "location=Office-Building-A" \
    --metadata "timestamp=$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
    --keywords "AUDIT,LOGIN,SUCCESS" \
    --category "SECURITY" \
    --generate-key

# Failed login attempt with detailed metadata
java -jar blockchain-cli.jar add-block "Failed login attempt - invalid password" \
    --metadata "user_id=unknown" \
    --metadata "attempted_username=admin" \
    --metadata "ip_address=203.0.113.45" \
    --metadata "failure_reason=invalid_password" \
    --metadata "attempt_count=3" \
    --metadata "locked_account=true" \
    --metadata "alert_sent=true" \
    --keywords "AUDIT,LOGIN,FAILED,SECURITY-ALERT" \
    --category "SECURITY" \
    --generate-key

# System configuration change
java -jar blockchain-cli.jar add-block "Firewall rule updated" \
    --metadata "rule_id=FW-2024-001" \
    --metadata "action=ALLOW" \
    --metadata "protocol=TCP" \
    --metadata "port=8443" \
    --metadata "source_ip=10.0.0.0/8" \
    --metadata "changed_by=admin-john" \
    --metadata "approval_ticket=JIRA-SEC-1234" \
    --keywords "FIREWALL,CONFIG,SECURITY" \
    --category "INFRASTRUCTURE" \
    --generate-key
```

### Example 8: Recipient Encryption for Multi-User Workflow

```zsh
# Scenario: Document approval workflow with encrypted data

# Step 1: Author creates encrypted document for reviewer
java -jar blockchain-cli.jar add-block "Draft proposal: New product launch strategy" \
    --recipient "Reviewer-Manager" \
    --metadata "document_id=DOC-2024-042" \
    --metadata "status=draft" \
    --metadata "author=Marketing-Team" \
    --metadata "version=1.0" \
    --keywords "PROPOSAL,DRAFT,REVIEW-NEEDED" \
    --category "BUSINESS" \
    --generate-key

# Step 2: Reviewer approves and encrypts for final approver
java -jar blockchain-cli.jar add-block "Reviewed proposal: Approved with minor changes" \
    --recipient "Director-CEO" \
    --metadata "document_id=DOC-2024-042" \
    --metadata "status=reviewed" \
    --metadata "reviewer=Reviewer-Manager" \
    --metadata "version=1.1" \
    --metadata "comments=Minor budget adjustments needed" \
    --keywords "PROPOSAL,REVIEWED,APPROVAL-NEEDED" \
    --category "BUSINESS" \
    --generate-key

# Step 3: CEO gives final approval
java -jar blockchain-cli.jar add-block "Final approval: New product launch authorized" \
    --metadata "document_id=DOC-2024-042" \
    --metadata "status=approved" \
    --metadata "approver=Director-CEO" \
    --metadata "version=1.1-final" \
    --metadata "budget_allocated=500000" \
    --metadata "launch_date=2024-Q3" \
    --keywords "PROPOSAL,APPROVED,AUTHORIZED" \
    --category "BUSINESS" \
    --generate-key

# Track the complete workflow
java -jar blockchain-cli.jar search "DOC-2024-042" --detailed
```

### Example 9: Error Handling and Validation

```zsh
# Error: Recipient doesn't exist
java -jar blockchain-cli.jar add-block "Test data" --recipient "NonExistentUser" --generate-key
❌ Failed to add block: Recipient 'NonExistentUser' not found in authorized keys
💡 Use 'add-key "NonExistentUser" --generate' to create the recipient first

# Error: Invalid metadata format
java -jar blockchain-cli.jar add-block "Test data" --metadata "invalid-format-no-equals" --generate-key
⚠️  Invalid metadata format: invalid-format-no-equals (expected key=value)

# Error: Cannot combine password and recipient
java -jar blockchain-cli.jar add-block "Test data" --password "pass123" --recipient "Alice" --generate-key
❌ Cannot use both --password and --recipient encryption simultaneously
💡 Use either password-based OR recipient-based encryption, not both

# Correct usage
java -jar blockchain-cli.jar add-block "Test data" --recipient "Alice" --generate-key
✅ Block added with recipient encryption
```

### Example 10: Advanced Automation Script

```zsh
#!/usr/bin/env zsh
# Advanced block creation with metadata tracking

# Function to create audit block with metadata
create_audit_block() {
    local event_type="$1"
    local description="$2"
    shift 2

    # Build metadata array
    local metadata_args=()
    metadata_args+=(--metadata "event_type=$event_type")
    metadata_args+=(--metadata "timestamp=$(date -u +%Y-%m-%dT%H:%M:%SZ)")
    metadata_args+=(--metadata "script_name=$(basename $0)")
    metadata_args+=(--metadata "script_pid=$$")
    metadata_args+=(--metadata "user=$(whoami)")
    metadata_args+=(--metadata "hostname=$(hostname)")

    # Add any additional metadata passed as arguments
    for arg in "$@"; do
        metadata_args+=(--metadata "$arg")
    done

    # Create block
    java -jar blockchain-cli.jar add-block "$description" \
        "${metadata_args[@]}" \
        --keywords "AUDIT,$event_type,AUTOMATED" \
        --category "SYSTEM" \
        --generate-key \
        --json
}

# Usage examples
create_audit_block "BACKUP" "Daily backup completed successfully" \
    "backup_size=10GB" \
    "files_count=5000" \
    "duration_seconds=120"

create_audit_block "DEPLOYMENT" "Application deployed to production" \
    "version=2.5.0" \
    "environment=production" \
    "deployment_method=automated" \
    "rollback_available=true"

create_audit_block "SECURITY_SCAN" "Security vulnerability scan completed" \
    "vulnerabilities_found=0" \
    "scan_type=full" \
    "tool=SemGrep" \
    "scan_duration=45"
```

### Example 11: Troubleshooting Advanced Features

```zsh
# Debug recipient encryption
java -jar blockchain-cli.jar add-block "Test recipient encryption" \
    --recipient "TestUser" \
    --generate-key \
    --verbose

🔍 Creating block with recipient encryption...
🔍 Looking up recipient public key: TestUser
🔍 Found recipient in authorized keys
🔐 Encrypting data with recipient's public key
✅ Block added successfully!

# Debug custom metadata
java -jar blockchain-cli.jar add-block "Test metadata" \
    --metadata "key1=value1" \
    --metadata "key2=value2" \
    --metadata "key3=value3" \
    --generate-key \
    --verbose \
    --detailed

🔍 Adding metadata: key1 = value1
🔍 Adding metadata: key2 = value2
🔍 Adding metadata: key3 = value3
✅ Block Added Successfully
📊 Custom Metadata: key1=value1, key2=value2, key3=value3

# Verify metadata in JSON output
java -jar blockchain-cli.jar add-block "Verify metadata" \
    --metadata "test=verification" \
    --generate-key \
    --json | jq '.customMetadata'

"test=verification"
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
🔐 Encrypting data with AES-256-GCM...
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

## 🗂️ Off-Chain Command Examples

The `offchain` command provides explicit management of off-chain data storage, retrieval, and analysis operations.

### Example 1: Store File as Off-Chain Data

```zsh
# Store a large document as off-chain data
java -jar blockchain-cli.jar offchain store --file large-report.pdf --category DOCUMENT
✅ Off-Chain Data Stored Successfully
============================================================
📦 Block #42
📅 Timestamp: 2025-07-21T10:30:45.123Z
🔗 Hash: abc123...def456
📁 Original File: large-report.pdf
💾 Off-chain Size: 2.5 MB
🔐 Content Type: application/pdf
🔐 Encrypted: No

# Store with encryption
java -jar blockchain-cli.jar offchain store \
    --file confidential-contract.pdf \
    --category LEGAL \
    --password "SecurePass123" \
    --identifier "CONTRACT-2025-001"
✅ Off-Chain Data Stored Successfully
============================================================
📦 Block #43
📅 Timestamp: 2025-07-21T10:35:22.456Z
🔗 Hash: def789...ghi012
📁 Original File: confidential-contract.pdf
💾 Off-chain Size: 1.8 MB
🔐 Content Type: application/pdf
🔐 Encrypted: Yes

# Store with verbose output
java -jar blockchain-cli.jar offchain store \
    --file medical-records.json \
    --category MEDICAL \
    --identifier "PATIENT-001" \
    --verbose
🔍 Initializing off-chain command...
🔍 Storing off-chain data from file: medical-records.json
✅ Off-Chain Data Stored Successfully
```

### Example 2: Retrieve Off-Chain Data

```zsh
# Retrieve by block hash
java -jar blockchain-cli.jar offchain retrieve \
    --block-hash "abc123def456" \
    --output retrieved-report.pdf
📥 Off-Chain Data Retrieved
============================================================
📦 Block #42
📅 Timestamp: 2025-07-21T10:30:45.123Z
🔗 Hash: abc123...def456
💾 File Size: 2.5 MB
🔐 Content Type: application/pdf
📁 File Path: off-chain-data/offchain_1721560245_42.dat
💾 Saved to: retrieved-report.pdf

# Retrieve by block number
java -jar blockchain-cli.jar offchain retrieve \
    --block-number 43 \
    --password "SecurePass123" \
    --output decrypted-contract.pdf
📥 Off-Chain Data Retrieved
============================================================
📦 Block #43
📅 Timestamp: 2025-07-21T10:35:22.456Z
🔗 Hash: def789...ghi012
💾 File Size: 1.8 MB
🔐 Content Type: application/pdf
📁 File Path: off-chain-data/offchain_1721560522_43.dat
💾 Saved to: decrypted-contract.pdf

# Retrieve encrypted data requires password
java -jar blockchain-cli.jar offchain retrieve --block-number 43 --output temp.pdf
❌ Password required to retrieve encrypted off-chain data
   Use --password to provide decryption password
```

### Example 3: List All Off-Chain Blocks

```zsh
# List all blocks with off-chain data
java -jar blockchain-cli.jar offchain list
💾 Off-Chain Data Blocks
============================================================
Found 5 block(s) with off-chain data:

📦 Block #15
   📅 2025-07-20T14:22:10.123Z
   🔗 abc111...def222
   💾 Size: 1.2 MB
   🔐 Type: application/json
   🔒 Encrypted: Yes

📦 Block #28
   📅 2025-07-21T08:15:33.456Z
   🔗 ghi333...jkl444
   💾 Size: 3.5 MB
   🔐 Type: text/plain
   🔒 Encrypted: No

📦 Block #42
   📅 2025-07-21T10:30:45.123Z
   🔗 abc123...def456
   💾 Size: 2.5 MB
   🔐 Type: application/pdf
   🔒 Encrypted: No

📦 Block #43
   📅 2025-07-21T10:35:22.456Z
   🔗 def789...ghi012
   💾 Size: 1.8 MB
   🔐 Type: application/pdf
   🔒 Encrypted: Yes

📦 Block #50
   📅 2025-07-21T11:20:15.789Z
   🔗 mno555...pqr666
   💾 Size: 5.2 MB
   🔐 Type: application/zip
   🔒 Encrypted: No

# List with JSON output for automation
java -jar blockchain-cli.jar offchain list --json
{
  "operation": "list",
  "totalBlocks": 5,
  "blocks": [
    {
      "blockNumber": 15,
      "timestamp": "2025-07-20T14:22:10.123Z",
      "hash": "abc111def222...",
      "encrypted": true,
      "offChainData": {
        "fileSize": 1258291,
        "contentType": "application/json"
      }
    },
    {
      "blockNumber": 28,
      "timestamp": "2025-07-21T08:15:33.456Z",
      "hash": "ghi333jkl444...",
      "encrypted": false,
      "offChainData": {
        "fileSize": 3670016,
        "contentType": "text/plain"
      }
    }
  ],
  "timestamp": "2025-07-21T12:00:00.000Z"
}
```

### Example 4: Analyze Off-Chain Storage

```zsh
# Analyze off-chain data usage patterns
java -jar blockchain-cli.jar offchain analyze
🔍 Off-Chain Data Analysis
============================================================
📊 Analysis Summary:
   📦 Total blockchain blocks: 50
   💾 Off-chain blocks: 5
   🔐 Encrypted off-chain: 2
   💽 Total off-chain size: 14.2 MB
   📈 Off-chain usage: 10.0%
   🔒 Off-chain encryption: 40.0%
   📏 Average file size: 2.8 MB

💡 Recommendations:
   • Consider encrypting more off-chain data for security

# Analyze with JSON output
java -jar blockchain-cli.jar offchain analyze --json
{
  "operation": "analyze",
  "totalBlocks": 50,
  "offChainBlocks": 5,
  "encryptedOffChain": 2,
  "totalOffChainSize": 14884863,
  "offChainUsageRate": 0.100,
  "offChainEncryptionRate": 0.400,
  "averageFileSize": 2976973,
  "timestamp": "2025-07-21T12:00:00.000Z"
}

# Analyze with verbose output
java -jar blockchain-cli.jar offchain analyze --verbose
🔍 Initializing off-chain command...
🔍 Analyzing off-chain data usage...
🔍 Off-Chain Data Analysis
============================================================
[Analysis results...]
```

### Example 5: Show Off-Chain Statistics

```zsh
# Display comprehensive off-chain storage statistics
java -jar blockchain-cli.jar offchain stats
💾 Off-Chain Storage Statistics
============================================================
📊 Storage Statistics:
   📦 Total blocks: 50
   💾 Off-chain blocks: 5
   🔐 Encrypted off-chain: 2
   📝 Unencrypted off-chain: 3
   💽 Total off-chain size: 14.2 MB
   📈 Off-chain usage rate: 10.0%
   📏 Average off-chain size: 2.8 MB
   🔐 Off-chain encryption rate: 40.0%

# Statistics with verbose output
java -jar blockchain-cli.jar offchain stats --verbose
🔍 Initializing off-chain command...
🔍 Generating off-chain statistics...
💾 Off-Chain Storage Statistics
============================================================
[Statistics output...]
```

### Example 6: JSON Output for All Operations

```zsh
# Store operation with JSON output
java -jar blockchain-cli.jar offchain store \
    --file report.pdf \
    --category REPORT \
    --json
{
  "operation": "store",
  "success": true,
  "blockNumber": 51,
  "blockHash": "xyz789abc012...",
  "originalFile": "report.pdf",
  "encrypted": false,
  "offChainData": {
    "fileSize": 1048576,
    "contentType": "application/pdf"
  },
  "timestamp": "2025-07-21T12:10:00.000Z"
}

# Retrieve operation with JSON output
java -jar blockchain-cli.jar offchain retrieve \
    --block-number 51 \
    --output retrieved.pdf \
    --json
{
  "operation": "retrieve",
  "success": true,
  "blockNumber": 51,
  "blockHash": "xyz789abc012...",
  "offChainData": {
    "fileSize": 1048576,
    "contentType": "application/pdf",
    "filePath": "off-chain-data/offchain_1721565000_51.dat"
  },
  "outputPath": "retrieved.pdf",
  "timestamp": "2025-07-21T12:15:00.000Z"
}
```

### Example 7: Complete Off-Chain Workflow

```zsh
# Step 1: Store multiple large files
echo "📦 Storing large files as off-chain data..."
java -jar blockchain-cli.jar offchain store --file annual-report-2024.pdf --category FINANCE
java -jar blockchain-cli.jar offchain store --file medical-imaging.dcm --category MEDICAL --password "Med123"
java -jar blockchain-cli.jar offchain store --file legal-contracts.zip --category LEGAL --password "Legal456"

# Step 2: List all off-chain blocks
echo "📋 Listing all off-chain blocks..."
java -jar blockchain-cli.jar offchain list

# Step 3: Analyze storage usage
echo "🔍 Analyzing off-chain storage..."
java -jar blockchain-cli.jar offchain analyze

# Step 4: Show statistics
echo "📊 Off-chain storage statistics..."
java -jar blockchain-cli.jar offchain stats

# Step 5: Retrieve specific files
echo "📥 Retrieving stored files..."
java -jar blockchain-cli.jar offchain retrieve --block-number 52 --output restored-annual-report.pdf
java -jar blockchain-cli.jar offchain retrieve --block-number 53 --password "Med123" --output restored-imaging.dcm
java -jar blockchain-cli.jar offchain retrieve --block-number 54 --password "Legal456" --output restored-contracts.zip

echo "✅ Off-chain workflow completed!"
```

### Example 8: Automated Off-Chain Management

```zsh
#!/usr/bin/env zsh
# Automated off-chain data archival script

ARCHIVE_DIR="/data/archives"
CATEGORY="ARCHIVE"

# Process all files in archive directory
for file in "$ARCHIVE_DIR"/*.{pdf,zip,tar.gz}; do
    if [ -f "$file" ]; then
        echo "📦 Archiving: $(basename "$file")"

        # Store with automatic encryption for sensitive files
        if [[ "$file" =~ (confidential|private|secret) ]]; then
            java -jar blockchain-cli.jar offchain store \
                --file "$file" \
                --category "$CATEGORY" \
                --password "ArchivePass2025" \
                --identifier "$(basename "$file")"
        else
            java -jar blockchain-cli.jar offchain store \
                --file "$file" \
                --category "$CATEGORY" \
                --identifier "$(basename "$file")"
        fi

        # Move processed file to backup
        mv "$file" "$ARCHIVE_DIR/processed/"
    fi
done

# Generate archive report
java -jar blockchain-cli.jar offchain analyze --json > archive-report-$(date +%Y%m%d).json
echo "✅ Archival process completed"
```

### Example 9: Off-Chain Data Recovery

```zsh
# Scenario: Recover all off-chain files from blockchain

# Step 1: List all off-chain blocks
java -jar blockchain-cli.jar offchain list --json > offchain-blocks.json

# Step 2: Parse and retrieve each file
RECOVERY_DIR="recovery-$(date +%Y%m%d)"
mkdir -p "$RECOVERY_DIR"

# Extract block numbers (simplified - use jq for real implementation)
java -jar blockchain-cli.jar offchain list | grep "Block #" | while read -r line; do
    BLOCK_NUM=$(echo "$line" | grep -oP '(?<=Block #)\d+')

    echo "📥 Recovering block #$BLOCK_NUM..."
    java -jar blockchain-cli.jar offchain retrieve \
        --block-number "$BLOCK_NUM" \
        --output "$RECOVERY_DIR/block_${BLOCK_NUM}.dat" \
        2>/dev/null
done

echo "✅ Recovery completed: $RECOVERY_DIR"
java -jar blockchain-cli.jar offchain stats
```

### Example 10: Troubleshooting Off-Chain Operations

```zsh
# Common error scenarios and solutions

# Error 1: Missing file path
$ java -jar blockchain-cli.jar offchain store --category DOCUMENT
❌ File path is required for store operation
   Use --file to specify the file path

# Solution: Specify file path
java -jar blockchain-cli.jar offchain store --file document.pdf --category DOCUMENT

# Error 2: File doesn't exist
$ java -jar blockchain-cli.jar offchain store --file nonexistent.pdf
❌ File does not exist: nonexistent.pdf

# Solution: Verify file exists
ls -lh nonexistent.pdf
# Then use correct path

# Error 3: Block not found
$ java -jar blockchain-cli.jar offchain retrieve --block-number 999
❌ Block not found

# Solution: List available off-chain blocks
java -jar blockchain-cli.jar offchain list

# Error 4: Block has no off-chain data
$ java -jar blockchain-cli.jar offchain retrieve --block-number 5
❌ Block does not contain off-chain data

# Solution: Verify block has off-chain data
java -jar blockchain-cli.jar offchain list | grep "Block #5"

# Error 5: Missing password for encrypted data
$ java -jar blockchain-cli.jar offchain retrieve --block-number 43
❌ Password required to retrieve encrypted off-chain data
   Use --password to provide decryption password

# Solution: Provide password
java -jar blockchain-cli.jar offchain retrieve --block-number 43 --password "YourPassword"

# Debugging with verbose mode
java -jar blockchain-cli.jar offchain store --file test.pdf --verbose
java -jar blockchain-cli.jar offchain list --verbose
java -jar blockchain-cli.jar offchain analyze --verbose
```

### Example 11: Off-Chain Data Compliance and Audit

```zsh
# Generate compliance report for off-chain storage

echo "📋 Off-Chain Storage Compliance Report"
echo "======================================"
echo "Generated: $(date)"
echo ""

# Total storage metrics
echo "📊 Storage Metrics:"
java -jar blockchain-cli.jar offchain stats

# Detailed analysis
echo ""
echo "🔍 Detailed Analysis:"
java -jar blockchain-cli.jar offchain analyze

# List all off-chain blocks with details
echo ""
echo "📦 Off-Chain Block Inventory:"
java -jar blockchain-cli.jar offchain list --json | jq '.'

# Export to compliance report file
{
    echo "# Off-Chain Storage Compliance Report"
    echo "Generated: $(date)"
    echo ""
    echo "## Statistics"
    java -jar blockchain-cli.jar offchain stats
    echo ""
    echo "## Analysis"
    java -jar blockchain-cli.jar offchain analyze
    echo ""
    echo "## Block Inventory"
    java -jar blockchain-cli.jar offchain list
} > compliance-report-offchain-$(date +%Y%m%d).md

echo "✅ Compliance report generated"
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
See [DEMO_SCRIPTS.md](DEMO_SCRIPTS.md) for detailed information about:
- Script descriptions and usage
- When to use each script
- Configuration options
- Troubleshooting guides

## 🔗 Related Documents

- [Main README](../README.md) - Getting started and basic usage
- [Docker Guide](DOCKER_GUIDE.md) - Complete Docker integration
- [Enterprise Guide](ENTERPRISE_GUIDE.md) - Best practices for business use
- [Troubleshooting](TROUBLESHOOTING.md) - Problem resolution
- [Integration Patterns](INTEGRATION_PATTERNS.md) - External system integrations
- [Automation Scripts](AUTOMATION_SCRIPTS.md) - Ready-to-use automation

---

**Need help?** Check the [Troubleshooting Guide](TROUBLESHOOTING.md) or return to the [main README](../README.md).
