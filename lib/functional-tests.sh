#!/usr/bin/env zsh

# Functional Tests Module for Blockchain CLI
# Contains all CLI functional tests
# Version: 1.0.0
# ZSH adaptation

# This file should be sourced from test-cli.sh
# It assumes common-functions.sh is already loaded and variables are set

# Function to run basic CLI tests
function run_basic_tests() {
    print_header "📋 Basic CLI Commands"
    
    run_cli_test "Version check" --version
    run_cli_test "Help flag" --help
    run_cli_test "Short help flag" -h
    run_cli_test "Short version flag" -V
}

# Function to run status tests
function run_status_tests() {
    print_header "📊 Status Commands"
    
    run_cli_test "Basic status" status
    run_cli_test "Status with JSON" status --json
    run_cli_test "Status with detailed" status --detailed
    run_cli_test "Status with short JSON flag" status -j
    run_cli_test "Status with short detailed flag" status -d
}

# Function to run validation tests
function run_validation_tests() {
    print_header "🔍 Validation Commands"
    
    run_cli_test "Basic validate" validate
    run_cli_test "Validate with detailed" validate --detailed
    run_cli_test "Validate with JSON" validate --json
    run_cli_test "Validate with quick" validate --quick
}

# Function to run key management tests
function run_key_management_tests() {
    print_header "🔑 Key Management Commands"
    
    # Clean up any existing TestUser key first
    print_info "Cleaning up any existing test keys..."
    rm -f blockchain.db 2>/dev/null || true
    
    run_cli_test "Add key with generation" add-key "TestUser" --generate
    run_cli_test "List keys basic" list-keys
    run_cli_test "List keys detailed" list-keys --detailed
    run_cli_test "List keys JSON" list-keys --json
    run_cli_test "Add another key" add-key "SecondUser" --generate
    
    # Additional Key Management Tests (Bug Fix Verification)
    print_test "Key management bug fix verification"
    KEY_OWNER_TEST="BugFixTestUser"
    KEY_OUTPUT=$(java -jar target/blockchain-cli.jar add-key "$KEY_OWNER_TEST" --generate 2>/dev/null | grep "Owner:" | cut -d: -f2 | xargs)
    if [[ "$KEY_OUTPUT" = "$KEY_OWNER_TEST" ]]; then
        print_success "Key owner name stored correctly (bug fix verified)"
        ((TESTS_PASSED++))
    else
        print_error "Key owner name bug not fixed (got: '$KEY_OUTPUT', expected: '$KEY_OWNER_TEST')"
        ((TESTS_FAILED++))
    fi
    
    print_test "Key-owner separation verification"
    JSON_OUTPUT=$(java -jar target/blockchain-cli.jar add-key "SeparationTestUser" --generate --json 2>/dev/null)
    JSON_OWNER=$(echo "$JSON_OUTPUT" | grep '"owner"' | cut -d'"' -f4)
    JSON_PUBLIC_KEY=$(echo "$JSON_OUTPUT" | grep '"publicKey"' | cut -d'"' -f4)
    if [[ -n "$JSON_OWNER" && -n "$JSON_PUBLIC_KEY" && "$JSON_OWNER" != "$JSON_PUBLIC_KEY" ]]; then
        print_success "Public key and owner name properly separated"
        ((TESTS_PASSED++))
    else
        print_error "Public key and owner name not properly separated"
        ((TESTS_FAILED++))
    fi
    
    print_test "Multiple user key management"
    MULTI_USER_SUCCESS=true
    for test_user in "Alice" "Bob" "Charlie"; do
        USER_RESULT=$(java -jar target/blockchain-cli.jar add-key "$test_user" --generate 2>/dev/null | grep "Owner:" | cut -d: -f2 | xargs)
        if [[ "$USER_RESULT" != "$test_user" ]]; then
            MULTI_USER_SUCCESS=false
            break
        fi
    done
    if [[ "$MULTI_USER_SUCCESS" = true ]]; then
        print_success "Multiple users with correct owner names"
        ((TESTS_PASSED++))
    else
        print_error "Multiple user key management failed"
        ((TESTS_FAILED++))
    fi
}

# Function to run block management tests
function run_block_management_tests() {
    print_header "🧱 Block Management Commands"
    
    print_info "Testing basic block management functionality"
    run_cli_test "Add block with generated key" add-block "Test_transaction_data" --generate-key
}

# Function to run signer bug fix verification tests
function run_signer_bug_fix_tests() {
    print_header "🔧 --signer Bug Fix Verification"
    
    print_info "Testing the --signer parameter bug fix"
    print_info "Previous bug: --signer parameter existed but was not used in code"
    
    # First create a test user for signer tests
    SIGNER_TEST_USER="SignerTestUser"
    print_test "Creating test user for --signer tests"
    if java -jar target/blockchain-cli.jar add-key "$SIGNER_TEST_USER" --generate >/dev/null 2>&1; then
        print_success "Test user '$SIGNER_TEST_USER' created successfully"
        ((TESTS_PASSED++))
        
        # Test 1: Verify --signer parameter is recognized (not "unknown option")
        print_test "--signer parameter recognition"
        SIGNER_OUTPUT=$(java -jar target/blockchain-cli.jar add-block "Test with existing signer" --signer "$SIGNER_TEST_USER" 2>&1)
        if echo "$SIGNER_OUTPUT" | grep -qi "unknown option"; then
            print_error "--signer parameter not recognized (bug not fixed)"
            ((TESTS_FAILED++))
        else
            print_success "--signer parameter is recognized (bug fix verified)"
            ((TESTS_PASSED++))
        fi
        
        # Test 2: Verify --signer with existing user works (creates demo key)
        print_test "--signer with existing user functionality"
        if java -jar target/blockchain-cli.jar add-block "Block with existing signer" --signer "$SIGNER_TEST_USER" >/dev/null 2>&1; then
            print_success "--signer with existing user works (creates demo key)"
            ((TESTS_PASSED++))
        else
            print_error "--signer with existing user failed"
            ((TESTS_FAILED++))
        fi
        
        # Test 3: Verify --signer shows demo mode message
        print_test "--signer demo mode verification"
        DEMO_OUTPUT=$(java -jar target/blockchain-cli.jar add-block "Demo mode test" --signer "$SIGNER_TEST_USER" 2>&1)
        if echo "$DEMO_OUTPUT" | grep -qi "demo"; then
            print_success "--signer shows demo mode message correctly"
            ((TESTS_PASSED++))
        else
            print_error "--signer demo mode message not found"
            ((TESTS_FAILED++))
        fi
    else
        print_error "Failed to create test user for --signer tests"
        ((TESTS_FAILED++))
    fi
    
    # Test 4: Verify --signer with non-existent user fails gracefully
    print_test "--signer with non-existent user"
    NON_EXISTENT_OUTPUT=$(java -jar target/blockchain-cli.jar add-block "Test with fake signer" --signer "NonExistentUser123" 2>&1)
    if echo "$NON_EXISTENT_OUTPUT" | grep -qi "not found"; then
        print_success "--signer with non-existent user fails gracefully"
        ((TESTS_PASSED++))
    else
        print_error "--signer with non-existent user should show 'not found' error"
        ((TESTS_FAILED++))
    fi
    
    # Test 5: Verify proper error when no signing method specified
    print_test "No signing method specified error"
    NO_METHOD_OUTPUT=$(java -jar target/blockchain-cli.jar add-block "Test no method" 2>&1)
    if echo "$NO_METHOD_OUTPUT" | grep -qi "signing method"; then
        print_success "Proper error shown when no signing method specified"
        ((TESTS_PASSED++))
    else
        print_error "Should show 'signing method' error when no method specified"
        ((TESTS_FAILED++))
    fi
    
    print_info "✅ --signer bug fix verification completed"
}

# Function to run search tests
function run_search_tests() {
    print_header "🔍 Search Commands"
    
    run_cli_test "Search for Genesis" search "Genesis"
    run_cli_test "Search with JSON" search "Genesis" --json
    run_cli_test "Search with limit" search "Genesis" --limit 5
    run_cli_test "Search by block number" search --block-number 0
    run_cli_test "Search non-existent term" search "NonExistentTerm12345"
}

# Function to run export/import tests
function run_export_import_tests() {
    print_header "📤📥 Export/Import Commands"
    
    if run_cli_test "Export blockchain" export "$EXPORT_FILE"; then
        if [[ -f "$EXPORT_FILE" ]]; then
            FILE_SIZE=$(ls -lh "$EXPORT_FILE" | awk '{print $5}')
            print_success "Export file created: $EXPORT_FILE ($FILE_SIZE)"
            
            # Test import with dry run
            run_cli_test "Import dry run" import "$EXPORT_FILE" --dry-run
            
            # Test import with backup
            run_cli_test "Import with backup" import "$EXPORT_FILE" --backup
        else
            print_error "Export file not created"
        fi
    fi
}
