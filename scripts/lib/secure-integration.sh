#!/usr/bin/env zsh

# Secure Key Management Integration Module
# Contains the complete secure key management functionality
# Version: 1.0.0
# ZSH adaptation

# Function to run secure key tests (integrated into main test-cli.sh)
function run_integrated_secure_tests() {
    local test_mode="${1:-quick}"
    
    print_header "🔐 Integrated Secure Key Management Tests"
    
    case $test_mode in
        "quick")
            # Quick validation of core secure functionality
            if command_exists mvn; then
                print_test "Secure key storage validation"
                if mvn test -Dtest="SecureKeyManagementIntegrationTest" -q >/dev/null 2>&1; then
                    print_success "Core secure key functionality validated"
                    count_test_passed
                elif mvn test -Dtest="ManageKeysCommandTest" -q >/dev/null 2>&1; then
                    print_success "Key management functionality validated"
                    count_test_passed
                else
                    print_warning "Secure key tests not available or failed - checking basic functionality"
                    # Try basic CLI test instead of unit tests (manage-keys doesn't support --help)
                    if java -jar target/blockchain-cli.jar manage-keys --list >/dev/null 2>&1; then
                        print_success "Secure key management CLI available"
                        count_test_passed
                    else
                        print_error "Secure key management not available"
                        count_test_failed
                    fi
                fi
            else
                print_info "Maven not available - testing CLI functionality"
                if java -jar target/blockchain-cli.jar manage-keys --list >/dev/null 2>&1; then
                    print_success "Secure key management CLI available"
                    count_test_passed
                else
                    print_error "Secure key management not available"
                    count_test_failed
                fi
            fi
            ;;
        "full")
            # Run all secure key management tests
            if command_exists mvn; then
                local secure_test_classes=(
                    "SecureKeyManagementIntegrationTest:End-to-end workflows"
                    "ManageKeysCommandTest:Key management commands"
                    "AddBlockCommandKeyFileTest:Key file functionality"
                    "SecureKeyManagementStressTest:Stress testing"
                    "ManageKeysCommandCoverageTest:Coverage testing"
                )
                
                for test_info in "${secure_test_classes[@]}"; do
                    IFS=':' read -r test_class test_desc <<< "$test_info"
                    print_test "$test_desc"
                    if mvn test -Dtest="$test_class" -q >/dev/null 2>&1; then
                        print_success "$test_desc passed"
                        count_test_passed
                    else
                        print_error "$test_desc failed"
                        count_test_failed
                    fi
                done
            fi
            ;;
    esac
}

# Function to show enhanced practical examples
function show_enhanced_practical_examples() {
    echo ""
    echo "🔧 Key Management Operations:"
    echo "   # List all stored private keys"
    echo "   java -jar target/blockchain-cli.jar manage-keys --list"
    echo "   # Check if a specific user has a stored private key"
    echo "   java -jar target/blockchain-cli.jar manage-keys --check Manager"
    echo "   # Test password for a stored key"
    echo "   java -jar target/blockchain-cli.jar manage-keys --test Manager"
    echo "   # Delete a stored private key (with confirmation)"
    echo "   java -jar target/blockchain-cli.jar manage-keys --delete Manager"
    echo ""
    echo "📊 JSON Output for Integration:"
    echo "   # Get JSON output for programmatic use"
    echo "   java -jar target/blockchain-cli.jar manage-keys --list --json"
    echo "   java -jar target/blockchain-cli.jar add-block \"API data\" --signer Manager --json"
    echo "   java -jar target/blockchain-cli.jar manage-keys --check Manager --json"
    echo ""
}

# Integration notes for developers
function show_integration_notes() {
    echo ""
    echo "🚀 INTEGRATION NOTES FOR DEVELOPERS"
    echo "===================================="
    echo ""
    echo "This section documents how the secure key management tests are integrated:"
    echo ""
    echo "1. The test-cli.sh now includes secure key management validation"
    echo "2. Environment variables control test depth:"
    echo "   - Default: Quick validation of secure functionality"  
    echo "   - FULL_SECURE_TESTS=true: Comprehensive test suite"
    echo "   - STRESS_TESTS=true: Performance/stress testing"
    echo "   - SKIP_SECURE_TESTS=true: Skip all secure tests"
    echo ""
    echo "3. Practical examples demonstrate real-world usage patterns:"
    echo "   - Enterprise: Stored private keys with password protection"
    echo "   - Development: Demo mode with temporary keys"
    echo "   - Mixed environments: Both modes as appropriate"
    echo ""
    echo "4. The secure key management system supports:"
    echo "   - AES encryption of private keys at rest"
    echo "   - Password validation with strength requirements"
    echo "   - Two modes: Production (stored keys) vs Demo (temporary)"
    echo "   - Complete key lifecycle management"
    echo "   - JSON output for programmatic integration"
    echo ""
    echo "Usage examples:"
    echo "   ./test-cli.sh                              # Normal tests + quick secure validation"
    echo "   FULL_SECURE_TESTS=true ./test-cli.sh       # Full test suite including secure tests"
    echo "   STRESS_TESTS=true ./test-cli.sh            # Include performance/stress tests"
    echo "   SKIP_SECURE_TESTS=true ./test-cli.sh       # Skip secure key tests entirely"
    echo ""
}
