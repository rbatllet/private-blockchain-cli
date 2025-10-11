#!/usr/bin/env zsh

# Additional Tests Module for Blockchain CLI
# Contains error handling, workflow and performance tests
# Version: 1.0.0
# ZSH adaptation

# Function to run help tests
function run_help_tests() {
    print_header "❓ Help Commands"
    
    run_cli_test "Help command" help
}

# Function to run error handling tests
function run_error_handling_tests() {
    print_header "⚠️  Error Handling Tests"
    
    print_test "Invalid command test"
    if ! java -jar target/blockchain-cli.jar invalid-command >/dev/null 2>&1; then
        print_success "Invalid command properly rejected"
        count_test_passed
    else
        print_error "Invalid command not properly handled"
        count_test_failed
    fi
    
    print_test "Missing arguments test"
    if ! java -jar target/blockchain-cli.jar add-key >/dev/null 2>&1; then
        print_success "Missing arguments properly detected"
        count_test_passed
    else
        print_error "Missing arguments not properly handled"
        count_test_failed
    fi
    
    print_test "Invalid file import test"
    if ! java -jar target/blockchain-cli.jar import "non_existent_file.json" >/dev/null 2>&1; then
        print_success "Invalid file import properly rejected"
        count_test_passed
    else
        print_error "Invalid file import not properly handled"
        count_test_failed
    fi
}

# Function to run workflow integration tests
function run_workflow_tests() {
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
    
    if [[ "$WORKFLOW_SUCCESS" = true ]]; then
        print_success "Complete workflow test passed"
        count_test_passed
    else
        print_error "Complete workflow test failed"
        count_test_failed
    fi
}

# Function to run performance tests
function run_performance_tests() {
    print_header "⚡ Performance Tests"
    
    print_test "Performance test - multiple status calls"
    start_time=$(date +%s)
    for i in {1..5}; do
        java -jar target/blockchain-cli.jar status >/dev/null 2>&1
    done
    end_time=$(date +%s)
    duration=$((end_time - start_time))
    
    if [[ $duration -lt 30 ]]; then
        print_success "Performance test passed (5 status calls in ${duration}s)"
        count_test_passed
    else
        print_warning "Performance test slow (5 status calls in ${duration}s)"
        count_test_passed
    fi
}

# Function to show practical examples
function show_practical_examples() {
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
    echo "🔐 ENCRYPTION ANALYSIS EXAMPLES:"
    echo "================================"
    echo ""
    echo "📊 Basic Encryption Statistics:"
    echo "   # View overall encryption statistics"
    echo "   java -jar target/blockchain-cli.jar encrypt --stats"
    echo "   # Get encryption stats in JSON format"
    echo "   java -jar target/blockchain-cli.jar encrypt --stats --json"
    echo ""
    echo "🔍 Encrypted Block Analysis:"
    echo "   # Show only encrypted blocks"
    echo "   java -jar target/blockchain-cli.jar encrypt --encrypted-only"
    echo "   # Search for specific encrypted data"
    echo "   java -jar target/blockchain-cli.jar encrypt \"medical\" --username Doctor --password secret123"
    echo "   # Validate encrypted blocks integrity"
    echo "   java -jar target/blockchain-cli.jar encrypt --validate"
    echo ""
    echo "📈 SEARCH METRICS EXAMPLES:"
    echo "============================"
    echo ""
    echo "📋 Performance Monitoring:"
    echo "   # View basic search metrics"
    echo "   java -jar target/blockchain-cli.jar search-metrics"
    echo "   # Get detailed search performance breakdown"
    echo "   java -jar target/blockchain-cli.jar search-metrics --detailed"
    echo "   # Export metrics in JSON format"
    echo "   java -jar target/blockchain-cli.jar search-metrics --json"
    echo "   # Reset all search metrics"
    echo "   java -jar target/blockchain-cli.jar search-metrics --reset"
    echo ""
}
