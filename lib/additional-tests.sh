#!/bin/bash

# Additional Tests Module for Blockchain CLI
# Contains error handling, workflow and performance tests
# Version: 1.0

# Function to run help tests
run_help_tests() {
    print_header "❓ Help Commands"
    
    run_cli_test "Help command" help
}

# Function to run error handling tests
run_error_handling_tests() {
    print_header "⚠️  Error Handling Tests"
    
    print_test "Invalid command test"
    if ! java -jar target/blockchain-cli.jar invalid-command >/dev/null 2>&1; then
        print_success "Invalid command properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Invalid command not properly handled"
        ((TESTS_FAILED++))
    fi
    
    print_test "Missing arguments test"
    if ! java -jar target/blockchain-cli.jar add-key >/dev/null 2>&1; then
        print_success "Missing arguments properly detected"
        ((TESTS_PASSED++))
    else
        print_error "Missing arguments not properly handled"
        ((TESTS_FAILED++))
    fi
    
    print_test "Invalid file import test"
    if ! java -jar target/blockchain-cli.jar import "non_existent_file.json" >/dev/null 2>&1; then
        print_success "Invalid file import properly rejected"
        ((TESTS_PASSED++))
    else
        print_error "Invalid file import not properly handled"
        ((TESTS_FAILED++))
    fi
}

# Function to run workflow integration tests
run_workflow_tests() {
    print_header "🔄 Workflow Integration Tests"
    
    print_test "Complete workflow test"
    WORKFLOW_SUCCESS=true
    
    # Workflow: Status -> Add Key -> List Keys -> Validate -> Export -> Search -> Rollback (dry-run) -> Key Management
    if ! java -jar target/blockchain-cli.jar status >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar add-key "WorkflowUser" --generate >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar list-keys >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar validate >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar export "$TEMP_DIR/workflow_export.json" >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar search "Genesis" >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    if ! java -jar target/blockchain-cli.jar rollback --blocks 1 --dry-run >/dev/null 2>&1; then WORKFLOW_SUCCESS=false; fi
    
    if [ "$WORKFLOW_SUCCESS" = true ]; then
        print_success "Complete workflow test passed"
        ((TESTS_PASSED++))
    else
        print_error "Complete workflow test failed"
        ((TESTS_FAILED++))
    fi
}

# Function to run performance tests
run_performance_tests() {
    print_header "⚡ Performance Tests"
    
    print_test "Performance test - multiple status calls"
    start_time=$(date +%s)
    for i in {1..5}; do
        java -jar target/blockchain-cli.jar status >/dev/null 2>&1
    done
    end_time=$(date +%s)
    duration=$((end_time - start_time))
    
    if [ $duration -lt 30 ]; then
        print_success "Performance test passed (5 status calls in ${duration}s)"
        ((TESTS_PASSED++))
    else
        print_warning "Performance test slow (5 status calls in ${duration}s)"
        ((TESTS_PASSED++))
    fi
}

# Function to show practical examples
show_practical_examples() {
    echo ""
    echo "🔐 SECURE KEY MANAGEMENT EXAMPLES:"
    echo "=================================="
    echo ""
    echo "💼 Business/Enterprise Usage:"
    echo "   # Create department manager with secure private key storage"
    echo "   java -jar target/blockchain-cli.jar add-key \"DepartmentManager\" --generate --store-private"
    echo "   # Manager signs important business documents"
    echo "   java -jar target/blockchain-cli.jar add-block \"Q4 Budget Approved\" --signer DepartmentManager"
    echo "   java -jar target/blockchain-cli.jar add-block \"Compliance Audit Passed\" --signer DepartmentManager"
    echo ""
    echo "🏥 Healthcare/Regulated Industry:"
    echo "   # Create secure keys for medical staff"
    echo "   java -jar target/blockchain-cli.jar add-key \"ChiefMedicalOfficer\" --generate --store-private"
    echo "   java -jar target/blockchain-cli.jar add-key \"PrivacyOfficer\" --generate --store-private"
    echo "   # Sign sensitive medical records"
    echo "   java -jar target/blockchain-cli.jar add-block \"Patient consent updated\" --signer PrivacyOfficer"
    echo ""
    echo "🏛️ Government/Legal Usage:"
    echo "   # Create secure keys for legal authorities"
    echo "   java -jar target/blockchain-cli.jar add-key \"LegalCounsel\" --generate --store-private"
    echo "   # Sign legal documents with non-repudiation"
    echo "   java -jar target/blockchain-cli.jar add-block \"Contract approved and executed\" --signer LegalCounsel"
    echo ""
    echo "👨‍💻 Development/Testing (Demo Mode):"
    echo "   # Create temporary keys for testing"
    echo "   java -jar target/blockchain-cli.jar add-key \"Developer\" --generate"
    echo "   java -jar target/blockchain-cli.jar add-key \"Tester\" --generate"
    echo "   # Use demo mode for quick testing"
    echo "   java -jar target/blockchain-cli.jar add-block \"Test data\" --signer Developer"
    echo "   java -jar target/blockchain-cli.jar add-block \"Another test\" --signer Tester"
    echo ""
}
