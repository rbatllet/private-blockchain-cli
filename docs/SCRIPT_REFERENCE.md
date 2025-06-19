# 📜 Script Reference Guide

This document provides a comprehensive reference of all scripts available in the Private Blockchain CLI project.

## 📋 Table of Contents

- [Main Scripts](#main-scripts)
- [Library Scripts](#library-scripts)
- [Testing Scripts](#testing-scripts)
- [Utility Scripts](#utility-scripts)

## Main Scripts

### test-cli.sh

The primary test script that runs the complete test suite for the Private Blockchain CLI.

**Usage:**
```zsh
#!/usr/bin/env zsh

# Run all tests
./test-cli.sh

# Run only rollback tests
./test-cli.sh rollback

# Run with full secure tests enabled
FULL_SECURE_TESTS=true ./test-cli.sh

# Run with stress tests enabled
FULL_SECURE_TESTS=true STRESS_TESTS=true ./test-cli.sh

# Skip secure tests
SKIP_SECURE_TESTS=true ./test-cli.sh
```

**Features:**
- Comprehensive test suite for all CLI functionality
- Modular design with test components loaded from the lib directory
- Environment variable configuration for test behavior
- Color-coded output for better readability

### test-build-config.sh

Tests the build configuration and environment setup for the Private Blockchain CLI.

**Usage:**
```zsh
#!/usr/bin/env zsh

./test-build-config.sh
```

**Features:**
- Validates Maven configuration
- Checks for required dependencies
- Verifies environment variables
- Tests build process

## Testing Scripts

### run-rollback-tests.sh

Dedicated script to run all rollback tests in sequence.

**Usage:**
```zsh
#!/usr/bin/env zsh

./run-rollback-tests.sh
```

**Features:**
- Focused on rollback functionality testing
- Runs all rollback test scenarios
- Provides detailed test results

### test-rollback-interactive.sh

Interactive script for manual rollback testing with menu options.

**Usage:**
```zsh
#!/usr/bin/env zsh

./test-rollback-interactive.sh
```

**Features:**
- Interactive menu for different rollback test scenarios
- Step-by-step guided testing
- Detailed feedback on test results

### test-rollback-setup.sh

Script to set up test data for rollback testing.

**Usage:**
```zsh
#!/usr/bin/env zsh

./test-rollback-setup.sh
```

**Features:**
- Creates test blockchain data
- Sets up specific block configurations for testing
- Prepares environment for rollback tests

### test_key_file_functionality.sh

Functional test script for the key file implementation.

**Usage:**
```zsh
#!/usr/bin/env zsh

./test_key_file_functionality.sh

# Skip key generation
./test_key_file_functionality.sh --skip-keygen

# Show debug information
./test_key_file_functionality.sh --debug
```

**Features:**
- Tests the complete `--key-file` functionality
- Validates different key formats (PEM, DER)
- Tests error handling and edge cases
- Includes verbose mode testing

## Utility Scripts

### clean-database.sh

Utility script to clean corrupted SQLite database files.

**Usage:**
```zsh
#!/usr/bin/env zsh

./clean-database.sh
```

**Features:**
- Removes database files from project root and blockchain-data directory
- Attempts to repair corrupted databases if possible
- Provides detailed output of actions taken

### generate_test_keys.sh

Utility script for creating test keys for development and testing.

**Usage:**
```zsh
#!/usr/bin/env zsh

./generate_test_keys.sh
```

**Features:**
- Generates test keys in various formats
- Creates both valid and invalid test keys
- Includes warning about not using for production

## Library Scripts

These scripts are not meant to be run directly but are sourced by other scripts.

### lib/common-functions.sh

Common utility functions used across multiple scripts.

**Features:**
- Color output functions
- Utility functions for file and command checking
- Error handling functions

### lib/functional-tests.sh

Core functional tests for the blockchain CLI.

**Features:**
- Basic blockchain operations tests
- Transaction validation tests
- Block creation and verification tests

### lib/additional-tests.sh

Additional test cases for extended functionality.

**Features:**
- Edge case testing
- Performance testing functions
- Special feature tests

### lib/rollback-tests.sh

Core library with rollback test functions.

**Features:**
- Rollback validation tests
- Data consistency checks
- Error handling tests for rollback

### lib/secure-integration.sh

Secure key management integration tests.

**Features:**
- Key generation and storage tests
- Signing and verification tests
- Security validation tests

## Version Information

All scripts follow semantic versioning (X.Y.Z) and include version information in their headers.
