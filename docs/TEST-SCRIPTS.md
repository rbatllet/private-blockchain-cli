# Test Scripts for Blockchain CLI

This document describes the test scripts available to validate the functionality of the blockchain CLI.

## Available Scripts

### 1. `test-cli.sh`

General script to test the basic CLI commands.

```bash
./test-cli.sh
```

### 2. `test-validate-detailed.sh`

Specialized script to test the detailed validation functionality of the blockchain.

```bash
./test-validate-detailed.sh
```

## Description of `test-validate-detailed.sh`

This script specifically tests the detailed validation functionality of the blockchain, including:

- **Automatic JAR detection**: Automatically finds the correct CLI JAR regardless of version.
- **Test setup**: Creates a test blockchain with multiple blocks and authorized keys.
- **Validation tests**: Runs basic, quick, detailed validation tests and with JSON output.
- **Corruption simulation**: Simulates a corrupted blockchain to test problem detection.
- **Post-corruption validation**: Verifies that detailed validation correctly detects issues.

### Tested Features

1. **Basic Validation**: `validate`
2. **Quick Validation**: `validate --quick`
3. **Detailed Validation**: `validate --detailed`
4. **JSON Output**: `validate --json`
5. **Detailed Validation with JSON**: `validate --detailed --json`
6. **Validation after corruption**: Tests all modes after simulating corruption

### How to Run

```bash
# Make sure the script has execution permissions
chmod +x test-validate-detailed.sh

# Run the script
./test-validate-detailed.sh
```

### Expected Results

The script will display the result of each test, indicating whether it passed or failed. At the end, it will show a summary with the total number of tests, how many passed, and how many failed.

## Integration with Development Process

These test scripts are valuable tools for:

1. **Quick verification**: Check that code changes haven't broken existing functionality.
2. **Demonstration**: Show how the CLI works to new users or contributors.
3. **Living documentation**: Provide practical examples of how the CLI is used.
4. **CI/CD**: Integration into continuous integration pipelines for automated testing.

## Maintenance

When modifying the CLI, make sure to also update the test scripts to reflect any changes in command syntax or functionality.
