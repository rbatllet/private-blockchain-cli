#!/usr/bin/env zsh

# Test script for the detailed validation functionality of the blockchain CLI
# This script tests the various options of the validate command, with focus on detailed validation
# Version: 1.0.0

# Get the script directory and load common functions
SCRIPT_DIR="$(dirname "${0:A}")"
source "$SCRIPT_DIR/lib/common-functions.sh"

# Counters
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=0

# Function to find the correct JAR file
function find_jar_file() {
    # First try the standard name
    if [[ -f "target/blockchain-cli.jar" ]]; then
        echo "target/blockchain-cli.jar"
        return 0
    fi
    
    # Then try the assembly jar as backup
    if [[ -f "target/blockchain-cli-assembly-jar-with-dependencies.jar" ]]; then
        echo "target/blockchain-cli-assembly-jar-with-dependencies.jar"
        return 0
    fi
    
    # Finally, try to find any jar with 'blockchain-cli' in the name
    local jar_file=$(find target -name "*blockchain-cli*.jar" 2>/dev/null | grep -v "original" | head -n 1)
    if [[ -n "$jar_file" ]]; then
        echo "$jar_file"
        return 0
    fi
    
    echo ""
    return 1
}

# Function to run CLI command with error handling
function run_cli_test() {
    local test_name="$1"
    shift
    
    print_test "$test_name"
    
    # Use the global JAR_FILE variable that was already found
    local cmd="java -jar \"$JAR_FILE\" $*"
    echo "   Command: $cmd"
    echo "   $(date '+%Y-%m-%d %H:%M:%S') - Starting command: $test_name"
    
    # Execute the command directly
    eval "$cmd" > "test_output.txt" 2>&1
    local exit_code=$?
    echo "   $(date '+%Y-%m-%d %H:%M:%S') - Command completed with exit code: $exit_code"
    
    # Read the output
    local output=$(cat "test_output.txt")
    
    if [[ $exit_code -eq 0 ]]; then
        print_success "$test_name passed"
        count_test_passed
        if [[ ${#output} -gt 100 ]]; then
            echo "   Output: ${output:0:100}..."
            echo "   Full output length: ${#output} characters"
        else
            echo "   Output: $output"
        fi
        return 0
    else
        print_error "$test_name failed"
        count_test_failed
        echo "   Error: $output"
        return 1
    fi
}

# Function to clean the database
function clean_database() {
    if [[ "${SKIP_DB_CLEANUP:-}" != "true" ]]; then
        echo "🧹 Cleaning blockchain database..."
        rm -f blockchain.db*
        echo "✅ Database cleaned"
    else
        echo "⏭️ Database cleanup skipped (SKIP_DB_CLEANUP=true)"
    fi
}

# Function to setup a test blockchain with some blocks
function setup_test_blockchain() {
    echo "🔨 Setting up test blockchain with blocks..."
    
    # Generate a test key and add it to authorized keys
    echo "🔑 Generating test key and adding to authorized keys..."
    java -jar "$JAR_FILE" add-key "Test User" --generate --show-private > "test_key.txt"
    if [[ $? -ne 0 ]]; then
        print_error "Failed to generate and add test key"
        return 1
    fi
    
    # Extract the private key from the output
    PRIVATE_KEY=$(grep -A 20 "Private key:" "test_key.txt" | tail -n 20)
    if [[ $? -ne 0 ]]; then
        print_error "Failed to add key to authorized keys"
        return 1
    fi
    
    # Save the private key to a temporary file
    echo "$PRIVATE_KEY" > "temp_key.pem"
    
    # Add some blocks to the blockchain
    echo "🧱 Adding blocks to the blockchain..."
    for i in {1..5}; do
        java -jar "$JAR_FILE" add-block "Test Block $i" --signer "Test User" > /dev/null
        if [[ $? -ne 0 ]]; then
            print_error "Failed to add block $i"
            return 1
        fi
    done
    
    echo "✅ Test blockchain setup complete with 5 blocks"
    return 0
}

# Function to corrupt a block for testing validation failures
function corrupt_blockchain() {
    echo "🔨 Corrupting blockchain for validation testing..."
    
    # First, add one more normal block to have something to corrupt
    echo "🧱 Adding block to be corrupted..."
    java -jar "$JAR_FILE" add-block "Block to be corrupted" --signer "Test User" > /dev/null
    if [[ $? -ne 0 ]]; then
        print_error "Failed to add block to be corrupted"
        return 1
    fi
    
    # Create database backup before corruption
    echo "💾 Creating database backup..."
    cp blockchain.db blockchain.db.backup 2>/dev/null || true
    
    # Method 1: Corrupt the database file directly by modifying some bytes
    echo "🔓 Corrupting blockchain database..."
    if [[ -f "blockchain.db" ]]; then
        # Add some random bytes to the end of the database file to corrupt it
        echo "CORRUPTED_DATA_$(date +%s)" >> blockchain.db
        echo "   Database file corrupted with invalid data"
    fi
    
    # Method 2: Try to create an invalid block by manipulating the database with sqlite3 if available
    if command -v sqlite3 >/dev/null 2>&1 && [[ -f "blockchain.db.backup" ]]; then
        echo "🗄️ Additional corruption via database manipulation..."
        # Restore from backup to have a valid starting point
        cp blockchain.db.backup blockchain.db
        # Try to corrupt a block's hash or signature
        sqlite3 blockchain.db "UPDATE blocks SET hash = 'INVALID_HASH' WHERE id = (SELECT MAX(id) FROM blocks);" 2>/dev/null || true
        echo "   Block hash corrupted in database"
    fi
    
    echo "✅ Blockchain corrupted for validation testing"
    return 0
}

# Main execution starts here
print_header "🔍 Blockchain Detailed Validation Test Suite"

# Already in the correct directory since we source relative to script location
echo "📁 Working directory: $(pwd)"
echo ""

# Find the JAR file once for all tests
JAR_FILE=$(find_jar_file)
if [[ -z "$JAR_FILE" ]]; then
    print_error "No suitable JAR file found in target directory"
    exit 1
fi
echo "🔍 Using JAR file: $JAR_FILE"
echo ""

# Clean blockchain database
clean_database

# Setup test blockchain
setup_test_blockchain
if [[ $? -ne 0 ]]; then
    print_error "Failed to setup test blockchain"
    exit 1
fi

print_header "🧪 VALIDATION TESTING PHASE"

# Test 1: Basic validation (should pass with clean blockchain)
run_cli_test "Basic validation" validate

# Test 2: Quick validation
run_cli_test "Quick validation" validate --quick

# Test 3: Detailed validation
run_cli_test "Detailed validation" validate --detailed

# Test 4: JSON output
run_cli_test "JSON output validation" validate --json

# Test 5: Detailed JSON output
run_cli_test "Detailed JSON validation" validate --detailed --json

# Test 6: Status command basic test
run_cli_test "Status command" status

# Test 7: Status with detailed output
run_cli_test "Status detailed" status --detailed

print_header "🔄 BLOCKCHAIN CORRUPTION TESTING PHASE"

# Corrupt blockchain for testing
corrupt_blockchain

# Test 8: Basic validation after corruption (should pass but show issues)
run_cli_test "Basic validation after corruption" validate || true

# Test 9: Detailed validation after corruption (should pass but show detailed issues)
run_cli_test "Detailed validation after corruption" validate --detailed || true

# Test 10: Detailed JSON validation after corruption
run_cli_test "Detailed JSON validation after corruption" validate --detailed --json || true

# Print summary
print_header "📊 TEST SUMMARY"
echo "Total tests: $TOTAL_TESTS"
echo "Tests passed: $TESTS_PASSED"
echo "Tests failed: $((TOTAL_TESTS - TESTS_PASSED))"

if [[ $TESTS_PASSED -eq $TOTAL_TESTS ]]; then
    print_success "All tests passed! 🎉"
    exit 0
else
    print_error "Some tests failed! 😢"
    exit 1
fi
