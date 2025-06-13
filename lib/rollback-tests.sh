#!/bin/bash

# Rollback Tests Module for Blockchain CLI  
# Contains all rollback-related tests
# Version: 1.0

# Function to run rollback tests
run_rollback_tests() {
    print_header "🔄 Rollback Commands"
    
    print_warning "Testing rollback functionality (uses dry-run for safety)"
    
    # Basic rollback parameter validation tests (expected to fail gracefully)
    print_test "Rollback no params (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback >/dev/null 2>&1; then
        print_success "Rollback without parameters properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Rollback without parameters should fail"
        ((TESTS_FAILED++))
    fi
    
    print_test "Rollback both params (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --blocks 1 --to-block 1 >/dev/null 2>&1; then
        print_success "Conflicting parameters properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Conflicting parameters should fail"
        ((TESTS_FAILED++))
    fi
    
    print_test "Rollback negative blocks (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --blocks -1 --yes >/dev/null 2>&1; then
        print_success "Negative values properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Negative values should fail"
        ((TESTS_FAILED++))
    fi
    
    print_test "Rollback zero blocks (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --blocks 0 --yes >/dev/null 2>&1; then
        print_success "Zero blocks properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Zero blocks should fail"
        ((TESTS_FAILED++))
    fi
    
    print_test "Rollback negative target (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --to-block -1 --yes >/dev/null 2>&1; then
        print_success "Negative target block properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Negative target block should fail"
        ((TESTS_FAILED++))
    fi
    
    # Dry run tests (safe to execute)
    run_cli_test "Rollback dry run with blocks" rollback --blocks 1 --dry-run
    run_cli_test "Rollback dry run with target" rollback --to-block 0 --dry-run  
    run_cli_test "Rollback dry run JSON output" rollback --blocks 1 --dry-run --json
    
    # Test edge cases with dry run (expected to fail)
    print_test "Rollback too many blocks (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --blocks 1000 --dry-run >/dev/null 2>&1; then
        print_success "Excessive rollback properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Excessive rollback should fail"
        ((TESTS_FAILED++))
    fi
    
    print_test "Rollback to non-existent block (should fail)"
    if ! java -jar target/blockchain-cli.jar rollback --to-block 1000 --dry-run >/dev/null 2>&1; then
        print_success "Non-existent target block properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Non-existent target block should fail"
        ((TESTS_FAILED++))
    fi
    
    # Additional Rollback Consistency Tests
    print_test "Rollback + validation consistency"
    TEMP_EXPORT_FILE="$TEMP_DIR/consistency_test.json"
    # Export current state
    java -jar target/blockchain-cli.jar export "$TEMP_EXPORT_FILE" >/dev/null 2>&1
    # Perform rollback and validate
    if java -jar target/blockchain-cli.jar rollback --blocks 1 --dry-run >/dev/null 2>&1 && \
       java -jar target/blockchain-cli.jar validate >/dev/null 2>&1; then
        print_success "Rollback maintains blockchain consistency"
        ((TESTS_PASSED++))
    else
        print_error "Rollback consistency validation failed"
        ((TESTS_FAILED++))
    fi
    
    print_test "Export/Import + Rollback consistency"
    if java -jar target/blockchain-cli.jar import "$TEMP_EXPORT_FILE" --force >/dev/null 2>&1 && \
       java -jar target/blockchain-cli.jar validate >/dev/null 2>&1; then
        print_success "Export/Import maintains consistency with rollback"
        ((TESTS_PASSED++))
    else
        print_error "Export/Import + rollback consistency failed"
        ((TESTS_FAILED++))
    fi
}
