# 🚀 Demo Scripts Guide

This guide explains the different demonstration scripts available for the Private Blockchain CLI with enhanced functionality.

## 📋 Quick Links to Documentation

- [Examples Guide](EXAMPLES.md) - Practical usage examples
- [Enhanced Features](ENHANCED_FEATURES.md) - New functionality overview
- [Docker Guide](DOCKER_GUIDE.md) - Container deployment
- [Script Reference](SCRIPT_REFERENCE.md) - Complete script documentation
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues and solutions
- [Practical Examples](PRACTICAL_EXAMPLES.md) - Real-world use cases

## 📁 Available Scripts

### Main Demo Scripts

#### 1. `test-cli.sh` - Main Test and Demo Script
**Description**: Primary script that runs comprehensive tests and interactive demonstrations.

**Features**:
- ✅ Tests all CLI functionalities
- 💾 Off-chain storage tests
- 🔍 Hybrid search tests
- 🔐 Secure key management tests
- 📊 Performance summary and practical examples

**Usage**:
```bash
# Full execution with all tests
./test-cli.sh

# Skip unit tests (integration tests only)
SKIP_UNIT_TESTS=true ./test-cli.sh

# Skip database cleanup (for debugging)
SKIP_DB_CLEANUP=true ./test-cli.sh

# Full security tests
FULL_SECURE_TESTS=true ./test-cli.sh

# Stress testing mode
STRESS_TESTS=true ./test-cli.sh
```

#### 2. `run-enhanced-demos.zsh` - Interactive CLI Demos
**Description**: Interactive demonstrations showcasing new features using real CLI commands.

**Demonstrations**:
- 💾 **Off-Chain Storage**: Automatic storage of large data
- 🔍 **Hybrid Search**: Multi-level search with optimization
- 🔗 **CLI Integration**: Complete integration of all functionalities

**Usage**:
```bash
# Interactive execution (with pauses)
./run-enhanced-demos.zsh

# Automatic execution (no pauses)
./run-enhanced-demos.zsh --auto
```

**Features**:
- 🎨 Colorful and visual output
- ⏸️ Interactive pauses to follow progress
- ⏱️ Execution time measurement
- 📋 Practical usage examples

#### 3. `run-java-demos.zsh` - Java Class Demos
**Description**: Directly executes Java demonstration classes via Maven.

**Demo Classes**:
- `OffChainStorageDemo`: Demonstrates off-chain storage
- `HybridSearchDemo`: Demonstrates hybrid search

**Usage**:
```bash
./run-java-demos.zsh
```

**Advantages**:
- 🚀 Direct execution of Java classes
- 📊 Detailed output from programmatic demos
- 🏗️ Automatic build before execution

### Testing Scripts

#### 4. `test-demo-script.zsh` - Demo Script Tester
**Description**: Tests and validates demo scripts functionality.

**Usage**:
```bash
./test-demo-script.zsh
```

#### 5. `test-simple-demos.zsh` - Simple Demo Tester
**Description**: Tests basic demo functionality with simplified scenarios.

**Usage**:
```bash
./test-simple-demos.zsh
```

### Utility Scripts

#### 6. `clean-database.sh` - Database Cleanup
**Description**: Cleans corrupted SQLite database files.

**Usage**:
```bash
./clean-database.sh
```

#### 7. `build-docker.sh` - Docker Build Script
**Description**: Builds Docker image for the blockchain CLI.

**Usage**:
```bash
./build-docker.sh
```

#### 8. `run-docker.sh` - Docker Run Script
**Description**: Runs the blockchain CLI in a Docker container.

**Usage**:
```bash
./run-docker.sh [CLI_COMMANDS]
```

### Test Suite Scripts

#### 9. `test-validate-detailed.sh` - Detailed Validation Tests
**Description**: Comprehensive validation testing with detailed output.

**Usage**:
```bash
./test-validate-detailed.sh
```

#### 10. `test-key-file-functionality.sh` - Key File Tests
**Description**: Tests key file functionality and formats.

**Usage**:
```bash
./test-key-file-functionality.sh
```

#### 11. `test-rollback-setup.sh` - Rollback Setup Tests
**Description**: Sets up and tests rollback functionality.

**Usage**:
```bash
./test-rollback-setup.sh
```

#### 12. `test-rollback-interactive.sh` - Interactive Rollback Tests
**Description**: Interactive rollback testing with user prompts.

**Usage**:
```bash
./test-rollback-interactive.sh
```

#### 13. `run-rollback-tests.sh` - Rollback Test Runner
**Description**: Runs comprehensive rollback tests.

**Usage**:
```bash
./run-rollback-tests.sh
```

#### 14. `test-build-config.sh` - Build Configuration Tests
**Description**: Tests build configuration and setup.

**Usage**:
```bash
./test-build-config.sh
```

#### 15. `generate-test-keys.sh` - Test Key Generator
**Description**: Generates test keys for development and testing.

**Usage**:
```bash
./generate-test-keys.sh
```

## 🎯 When to Use Each Script

### `test-cli.sh` - For Development and QA
- ✅ Verify everything works correctly
- 🧪 Run complete integration tests
- 📈 Get performance metrics
- 🔍 Debug issues

### `run-enhanced-demos.zsh` - For Presentations and Training
- 🎬 Show features live
- 📚 Learn how to use new functions
- 🎨 Visual and interactive experience
- 💡 Get practical usage examples

### `run-java-demos.zsh` - For Technical Deep Dive
- 🔬 See detailed programmatic behavior
- 📊 Technical analysis of functionalities
- 🚀 Execute official demos
- 📋 Complete output without interruptions

### Testing Scripts - For Quality Assurance
- 🧪 Focused testing of specific components
- 🔍 Isolated testing scenarios
- 📊 Detailed test reporting
- 🚨 Early detection of issues

## 📊 Demonstrated Features

### 💾 Off-Chain Storage
- **Automatic Detection**: Data > 512KB goes off-chain
- **Encryption**: AES-256-GCM for off-chain data
- **Integrity**: Complete validation with hash and signature
- **Transparency**: Consistent API regardless of storage

### 🔍 Hybrid Search
- **FAST_ONLY**: Search only in keywords (fastest)
- **INCLUDE_DATA**: Search in keywords + block data (balanced)
- **EXHAUSTIVE_OFFCHAIN**: Complete search including off-chain files
- **Filters**: By category, date range, block number
- **Output**: JSON or detailed format

### 🚀 Enhanced CLI
- **Keywords**: Support for manual and automatic keywords
- **Categories**: Organization by content type
- **Backward Compatibility**: All existing functions maintained
- **Performance**: Optimization for different use cases

## 🛠️ Environment Setup

### Prerequisites
```bash
# Java 21+
java -version

# Maven 3.8+
mvn -version

# ZSH (for interactive scripts)
echo $SHELL

# bc (for timing calculations)
which bc
```

### Useful Environment Variables
```bash
# Skip various test phases
export SKIP_UNIT_TESTS=true
export SKIP_DB_CLEANUP=true
export SKIP_SECURE_TESTS=true

# Enable extended testing
export FULL_SECURE_TESTS=true
export STRESS_TESTS=true
```

## 📁 File Structure

```
privateBlockchain-cli/
├── test-cli.sh                 # Main script
├── run-enhanced-demos.zsh      # Interactive CLI demos
├── run-java-demos.zsh          # Direct Java demos
├── test-demo-script.zsh        # Demo script tester
├── test-simple-demos.zsh       # Simple demo tester
├── clean-database.sh           # Database cleanup
├── build-docker.sh             # Docker build
├── run-docker.sh               # Docker run
├── test-validate-detailed.sh   # Detailed validation
├── test-key-file-functionality.sh # Key file tests
├── test-rollback-setup.sh      # Rollback setup
├── test-rollback-interactive.sh # Interactive rollback
├── run-rollback-tests.sh       # Rollback test runner
├── test-build-config.sh        # Build config tests
├── generate-test-keys.sh       # Test key generator
├── lib/
│   ├── enhanced-features-tests.sh  # New features tests
│   ├── functional-tests.sh         # Functional tests
│   ├── additional-tests.sh         # Additional tests
│   ├── secure-integration.sh       # Security tests
│   ├── rollback-tests.sh           # Rollback tests
│   └── common-functions.sh         # Shared functions
├── src/main/java/.../demos/
│   ├── OffChainStorageDemo.java    # Storage demo
│   └── HybridSearchDemo.java       # Search demo
└── off-chain-data/            # Generated off-chain data
```

## 🎬 Recommended Sequence

### For Developers
1. `./test-cli.sh` - Verify everything works
2. `./run-enhanced-demos.zsh` - See features in action
3. `./run-java-demos.zsh` - Analyze detailed behavior

### For Presentations
1. `./run-enhanced-demos.zsh` - Complete interactive demo
2. `./test-cli.sh` - Show validation and tests (optional)

### For Training
1. `./run-java-demos.zsh` - See programmatic output
2. `./run-enhanced-demos.zsh` - Practice with CLI
3. `./test-cli.sh` - Understand testing process

### For Quality Assurance
1. `./test-cli.sh` - Full integration testing
2. `./test-validate-detailed.sh` - Detailed validation
3. `./test-key-file-functionality.sh` - Key management tests
4. `./run-rollback-tests.sh` - Rollback functionality

## 🚨 Troubleshooting

### If scripts don't execute
```bash
# Make executable
chmod +x *.zsh *.sh

# Verify ZSH
which zsh
```

### If Maven fails
```bash
# Clean and rebuild
mvn clean compile
```

### If database has issues
```bash
# Manual cleanup
rm -f blockchain.db*
./clean-database.sh
```

### For Docker issues
```bash
# Rebuild Docker image
./build-docker.sh

# Check Docker status
docker ps -a
```

## 📞 Support

For script issues:
1. Check logs in `./logs/`
2. Run with `--verbose` if available
3. Verify prerequisites (Java, Maven, ZSH)
4. Consult documentation in `./docs/`

## 🔗 Related Documentation

- [Enhanced Features Guide](ENHANCED_FEATURES.md) - Detailed feature documentation
- [Docker Guide](DOCKER_GUIDE.md) - Container deployment guide
- [Examples](EXAMPLES.md) - Usage examples and tutorials
- [Troubleshooting Guide](TROUBLESHOOTING.md) - Common issues and solutions
- [Script Reference](SCRIPT_REFERENCE.md) - Complete script documentation

---

🎉 **All scripts are optimized for ZSH and provide a rich interactive experience to demonstrate the new features of the Private Blockchain CLI.**