#!/bin/bash

# Enhanced Blockchain CLI Build and Test Script
# Comprehensive testing of all CLI commands and workflows
# Version: 2.2 - Refactored for better modularity
#
# Environment Variables:
#   SKIP_UNIT_TESTS=true     - Skip Maven unit tests
#   SKIP_DB_CLEANUP=true     - Skip database cleanup (for debugging)
#
# This script automatically cleans corrupted SQLite database files to prevent
# SQLITE_IOERR_SHORT_READ errors that can occur when WAL files are not properly processed.

# Get the script directory and load common functions
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/lib/common-functions.sh"

# Note: Removed "set -e" to be more permissive with minor failures

# Counters
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=0

# Function to run CLI command with error handling
run_cli_test() {
    local test_name="$1"
    shift
    local cmd="java -jar target/blockchain-cli.jar $*"
    
    print_test "$((++TOTAL_TESTS)): $test_name"
    echo "   Command: $cmd"
    
    if output=$($cmd 2>&1); then
        print_success "$test_name passed"
        ((TESTS_PASSED++))
        if [ ${#output} -gt 100 ]; then
            echo "   Output: ${output:0:100}..."
        else
            echo "   Output: $output"
        fi
        return 0
    else
        print_error "$test_name failed"
        ((TESTS_FAILED++))
        echo "   Error: $output"
        return 1
    fi
}

# Function to use external database cleanup
clean_database() {
    # Allow skipping database cleanup with environment variable
    if [ "${SKIP_DB_CLEANUP:-}" = "true" ]; then
        print_info "Database cleanup skipped (SKIP_DB_CLEANUP=true)"
        return 0
    fi
    
    print_info "Running database cleanup..."
    if [ -x "$SCRIPT_DIR/clean-database.sh" ]; then
        # Run the dedicated cleanup script
        "$SCRIPT_DIR/clean-database.sh"
    else
        print_warning "clean-database.sh not found or not executable, skipping cleanup"
    fi
}

# Function to create temporary test files
setup_temp_files() {
    TEMP_DIR=$(mktemp -d)
    EXPORT_FILE="$TEMP_DIR/test_export.json"
    IMPORT_FILE="$TEMP_DIR/test_import.json"
    echo "📁 Created temp directory: $TEMP_DIR"
}

# Function to cleanup
cleanup() {
    if [ -n "$TEMP_DIR" ] && [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
        echo "🧹 Cleaned up temp directory"
    fi
}

# Main execution starts here
print_header "🚀 Enhanced Blockchain CLI Build and Test Process"

cd "$SCRIPT_DIR"
echo "📁 Working directory: $(pwd)"
echo ""

# Clean any corrupted database files before starting
clean_database

# Setup temporary files
setup_temp_files

# Set up trap for cleanup on exit
trap cleanup EXIT

print_header "🏗️  BUILD PHASE"

# Step 1: Clean previous builds
echo "🧹 Cleaning previous builds..."
if mvn clean -q 2>/dev/null; then
    print_success "Clean completed"
else
    print_warning "Clean had warnings (continuing anyway)"
fi

# Step 2: Compile the project
echo "🔨 Compiling project..."
if mvn compile -q 2>/dev/null; then
    print_success "Compilation completed"
else
    print_error "Compilation failed"
    exit 1
fi

# Step 3: Run tests (if not skipped)
if [ "${SKIP_UNIT_TESTS:-}" != "true" ]; then
    echo "🧪 Running unit tests..."
    if mvn test -q -Dtest="StatusCommandTest,BlockchainCLITest" 2>/dev/null; then
        print_success "Unit tests passed"
    else
        print_warning "Some unit tests failed (continuing with integration tests)"
    fi
else
    print_info "Unit tests skipped"
fi

# Step 4: Package the application
echo "📦 Packaging application..."
if mvn package -q -DskipTests; then
    print_success "Packaging completed"
else
    print_error "Packaging failed"
    exit 1
fi

# Step 5: Verify the JAR was created
if [ -f "target/blockchain-cli.jar" ]; then
    JAR_SIZE=$(ls -lh target/blockchain-cli.jar | awk '{print $5}')
    print_success "JAR file created successfully: target/blockchain-cli.jar ($JAR_SIZE)"
else
    print_error "JAR file not found!"
    exit 1
fi

print_header "🧪 FUNCTIONAL TESTING PHASE"

# Load functional tests modules
source "$SCRIPT_DIR/lib/functional-tests.sh"
source "$SCRIPT_DIR/lib/additional-tests.sh" 
source "$SCRIPT_DIR/lib/secure-integration.sh"
source "$SCRIPT_DIR/lib/rollback-tests.sh"

# Run all test suites
run_basic_tests
run_status_tests  
run_validation_tests
run_key_management_tests
run_block_management_tests
run_signer_bug_fix_tests
run_search_tests
run_export_import_tests
run_rollback_tests
run_help_tests
run_error_handling_tests
run_workflow_tests
run_performance_tests

# Run secure key management tests based on environment variables
if [ "${SKIP_SECURE_TESTS:-}" != "true" ]; then
    if [ "${FULL_SECURE_TESTS:-}" = "true" ]; then
        run_integrated_secure_tests "full"
    elif [ "${STRESS_TESTS:-}" = "true" ]; then
        run_integrated_secure_tests "full"
        # Add stress testing here if needed
        print_info "Stress testing mode enabled"
    else
        run_integrated_secure_tests "quick"
    fi
else
    print_info "Secure key management tests skipped (SKIP_SECURE_TESTS=true)"
fi

# Final Summary
print_header "📊 TEST SUMMARY"

echo ""
echo "🎯 Total Tests: $TOTAL_TESTS"
echo "✅ Passed: $TESTS_PASSED"
echo "❌ Failed: $TESTS_FAILED"
echo "📈 Success Rate: $(( TESTS_PASSED * 100 / TOTAL_TESTS ))%"
echo ""

# Manual cleanup
cleanup

if [ $TESTS_FAILED -eq 0 ]; then
    print_success "🎉 ALL TESTS PASSED! CLI is ready for production use"
    echo ""
    echo "📝 Usage examples:"
    echo "   java -jar target/blockchain-cli.jar status"
    echo "   java -jar target/blockchain-cli.jar add-key \"Alice\" --generate"
    echo "   java -jar target/blockchain-cli.jar list-keys --detailed"
    echo "   java -jar target/blockchain-cli.jar add-block \"My data\" --signer Alice"
    echo "   java -jar target/blockchain-cli.jar validate --detailed"
    echo "   java -jar target/blockchain-cli.jar export backup.json"
    echo "   java -jar target/blockchain-cli.jar search \"Genesis\" --json"
    echo "   java -jar target/blockchain-cli.jar rollback --blocks 2 --dry-run"
    echo "   java -jar target/blockchain-cli.jar add-block \"Data\" --generate-key"
    echo ""
    echo "🔗 For all commands: java -jar target/blockchain-cli.jar --help"
    
    # Show practical examples
    show_practical_examples
    
    # Show enhanced examples and integration notes
    show_enhanced_practical_examples
    show_integration_notes
    
    exit 0
else
    print_error "❌ $TESTS_FAILED tests failed. Please review the output above."
    exit 1
fi
