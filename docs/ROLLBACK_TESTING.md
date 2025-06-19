# 🔄 Rollback Testing Guide

A comprehensive guide to testing the rollback functionality in the Private Blockchain CLI.

## 📋 Table of Contents

- [Introduction](#introduction)
- [Rollback Command Overview](#rollback-command-overview)
- [Testing Procedures](#testing-procedures)
- [Automated Test Scripts](#automated-test-scripts)
- [Manual Testing Guide](#manual-testing-guide)
- [Common Test Scenarios](#common-test-scenarios)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

## Introduction

The rollback functionality allows you to remove recent blocks from the blockchain. This is a critical operation that should be thoroughly tested before use in production environments. This guide provides detailed procedures for testing the rollback functionality.

## Rollback Command Overview

The `rollback` command provides two main methods for removing blocks:

1. **Remove N most recent blocks**:
   ```zsh
   blockchain rollback --blocks <N>
   ```

2. **Rollback to a specific block number**:
   ```zsh
   blockchain rollback --to-block <N>
   ```
   Note: Block numbers are stored as `Long` values

### Important Options

| Option | Description |
|--------|-------------|
| `--dry-run` | Show what would be removed without making changes |
| `--yes` or `--confirm` | Skip confirmation prompt (use with caution) |
| `--json` | Output results in JSON format |

## Testing Procedures

### Prerequisites

Before running rollback tests, ensure:

1. You have a test blockchain database that can be modified or reset
2. You have at least 5-10 blocks in your test blockchain
3. You have backed up any important data

### Test Environment Setup

```zsh
# Create a test directory
mkdir -p blockchain-test
cd blockchain-test

# Copy blockchain database for testing
cp /path/to/original/blockchain.db .

# Verify initial state
java -jar blockchain-cli.jar status
```

## Automated Test Scripts

The Private Blockchain CLI includes several scripts for testing rollback functionality:

### Core Scripts

- **`lib/rollback-tests.sh`**: Core library with rollback test functions integrated into the main test suite
- **`run-rollback-tests.sh`**: Dedicated script to run all rollback tests in sequence
- **`test-rollback-setup.sh`**: Script to set up test data for rollback testing
- **`test-rollback-interactive.sh`**: Interactive script for manual rollback testing with menu options

### Running the Rollback Tests

```zsh
# Run all tests including rollback tests
./test-cli.sh

# Run only rollback tests
./test-cli.sh rollback

# Run dedicated rollback test suite
./run-rollback-tests.sh

# Set up test data for rollback testing
./test-rollback-setup.sh

# Run interactive rollback tests
./test-rollback-interactive.sh
```

### What the Tests Cover

The automated tests verify:

1. **Parameter validation**:
   - Rejection of invalid parameters (negative numbers, zero blocks)
   - Proper handling of conflicting parameters
   - Proper error messages

2. **Dry run functionality**:
   - Correct preview of changes without actual modification
   - JSON output format

3. **Edge cases**:
   - Attempting to remove too many blocks
   - Attempting to rollback to non-existent blocks

4. **Consistency validation**:
   - Blockchain remains valid after rollback
   - Export/import functionality works with rollback

## Manual Testing Guide

For thorough testing, follow these step-by-step procedures:

### Test 1: Basic Rollback Functionality

```zsh
# 1. Check current blockchain status
java -jar blockchain-cli.jar status

# 2. Add some test blocks
java -jar blockchain-cli.jar add-block "Test Block 1" --generate-key
java -jar blockchain-cli.jar add-block "Test Block 2" --generate-key
java -jar blockchain-cli.jar add-block "Test Block 3" --generate-key

# 3. Verify blocks were added
java -jar blockchain-cli.jar status

# 4. Test dry run rollback
java -jar blockchain-cli.jar rollback --blocks 2 --dry-run

# 5. Perform actual rollback
java -jar blockchain-cli.jar rollback --blocks 2 --yes

# 6. Verify rollback was successful
java -jar blockchain-cli.jar status
java -jar blockchain-cli.jar validate
```

### Test 2: Rollback to Specific Block

```zsh
# 1. Check current blockchain status and note block numbers
java -jar blockchain-cli.jar status

# 2. Add several test blocks
for i in {1..5}; do
  java -jar blockchain-cli.jar add-block "Test Block $i" --generate-key
done

# 3. Verify blocks were added
java -jar blockchain-cli.jar status

# 4. Test rollback to specific block
java -jar blockchain-cli.jar rollback --to-block 3 --dry-run

# 5. Perform actual rollback
java -jar blockchain-cli.jar rollback --to-block 3 --yes

# 6. Verify final state
java -jar blockchain-cli.jar status
java -jar blockchain-cli.jar validate
```

## Common Test Scenarios

### Scenario 1: Error Handling

Test how the system handles errors:

```zsh
# Invalid parameters
java -jar blockchain-cli.jar rollback --blocks -1
java -jar blockchain-cli.jar rollback --to-block -5
java -jar blockchain-cli.jar rollback --blocks 0

# Conflicting parameters
java -jar blockchain-cli.jar rollback --blocks 1 --to-block 2

# Excessive rollback
java -jar blockchain-cli.jar rollback --blocks 1000
```

### Scenario 2: JSON Output

Test the JSON output format:

```zsh
# JSON output with dry run
java -jar blockchain-cli.jar rollback --blocks 1 --dry-run --json

# JSON output with actual rollback
java -jar blockchain-cli.jar rollback --blocks 1 --yes --json
```

### Scenario 3: Data Consistency

Test data consistency after rollback:

```zsh
# 1. Export blockchain before rollback
java -jar blockchain-cli.jar export before-rollback.json

# 2. Perform rollback
java -jar blockchain-cli.jar rollback --blocks 2 --yes

# 3. Export blockchain after rollback
java -jar blockchain-cli.jar export after-rollback.json

# 4. Validate blockchain
java -jar blockchain-cli.jar validate

# 5. Compare exports (using external tool like jq)
jq '.blocks | length' before-rollback.json
jq '.blocks | length' after-rollback.json
```

## Troubleshooting

### Common Issues

1. **Validation fails after rollback**
   - Check if any authorized keys were affected
   - Verify blockchain integrity with `validate --detailed`

2. **Unexpected block count after rollback**
   - Verify the parameters used in rollback command
   - Check if genesis block is being counted correctly

3. **Error: "Cannot remove X blocks"**
   - You cannot remove the genesis block (block #0)
   - Verify your blockchain has enough blocks for the requested operation

### Recovery Options

If rollback testing causes issues:

```zsh
# Import from backup
java -jar blockchain-cli.jar import backup.json --force

# Validate after import
java -jar blockchain-cli.jar validate
```

## Best Practices

1. **Always use `--dry-run` first** to preview changes before actual rollback

2. **Create backups before testing**:
   ```zsh
   java -jar blockchain-cli.jar export backup-$(date +%Y%m%d-%H%M%S).json
   ```

3. **Test in isolated environments** before production

4. **Combine with validation**:
   ```zsh
   java -jar blockchain-cli.jar rollback --blocks 1 --yes && \
   java -jar blockchain-cli.jar validate
   ```

5. **Document test results** for audit purposes

6. **Test both rollback methods** (`--blocks` and `--to-block`) thoroughly

7. **Verify key authorization** is maintained after rollback

---

This documentation provides a comprehensive guide to testing the rollback functionality in the Private Blockchain CLI. For additional information on other commands, refer to the main README.md file.
