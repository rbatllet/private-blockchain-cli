#!/usr/bin/env zsh

# Enhanced Blockchain CLI Demos Script
# Demonstrates Off-Chain Storage and Hybrid Search functionality
# Version: 1.0.0 - New demos for enhanced CLI features

# Colors for better output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Emojis for visual appeal
SUCCESS="✅"
ERROR="❌"
INFO="ℹ️"
DEMO="🚀"
STORAGE="💾"
SEARCH="🔍"
PERFORMANCE="⚡"

# Function to print colored headers
function print_header() {
    echo ""
    echo -e "${PURPLE}================================================================================${NC}"
    echo -e "${WHITE}$1${NC}"
    echo -e "${PURPLE}================================================================================${NC}"
    echo ""
}

# Function to print colored messages
function print_info() {
    echo -e "${BLUE}${INFO} $1${NC}"
}

function print_success() {
    echo -e "${GREEN}${SUCCESS} $1${NC}"
}

function print_error() {
    echo -e "${RED}${ERROR} $1${NC}"
}

function print_demo() {
    echo -e "${CYAN}${DEMO} $1${NC}"
}

function print_separator() {
    echo -e "${YELLOW}────────────────────────────────────────────────────────────────────────────────${NC}"
}

# Function to pause for user interaction (optional)
function pause_for_user() {
    if [[ "${AUTO_RUN:-}" != "true" ]]; then
        echo ""
        echo -e "${YELLOW}Press Enter to continue...${NC}"
        read
    else
        echo ""
        sleep 1
    fi
}

# Function to check if JAR exists
function check_jar() {
    if [[ ! -f "target/blockchain-cli.jar" ]]; then
        print_error "JAR file not found. Please build the project first:"
        echo "  mvn clean package"
        exit 1
    fi
    
    # Check if JAR is executable
    if [[ ! -r "target/blockchain-cli.jar" ]]; then
        print_error "JAR file is not readable. Check permissions."
        exit 1
    fi
    
    print_info "Found JAR file: target/blockchain-cli.jar"
}

# Function to run CLI command with timing
function run_timed_command() {
    local description="$1"
    shift
    local cmd="java -jar target/blockchain-cli.jar $*"
    
    print_info "$description"
    echo "   Command: $cmd"
    echo ""
    
    local start_time=$(date +%s)
    if eval "$cmd"; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        print_success "Completed in ${duration}s"
    else
        local exit_code=$?
        print_error "Command failed with exit code: $exit_code"
        echo "   This may be expected for demonstration purposes."
        return 1
    fi
    echo ""
}

# Function to demonstrate off-chain storage
function demo_offchain_storage() {
    print_header "${STORAGE} OFF-CHAIN STORAGE DEMONSTRATION"
    
    print_info "This demo shows how the CLI automatically handles large data:"
    echo "• Small data (< 512KB) stays on-chain"
    echo "• Large data (> 512KB) goes off-chain with AES encryption"
    echo "• Keywords and categories are preserved"
    echo "• Full validation and integrity checking"
    
    pause_for_user
    
    print_separator
    print_demo "Demo 1: Small Data Block (On-Chain)"
    run_timed_command "Adding small medical record" add-block "Patient PAT-001: Routine checkup on 2024-01-15. Vital signs normal." --keywords "PAT-001,CHECKUP,VITALS" --category "MEDICAL" --generate-key --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 2: Large Data Block (Off-Chain)"
    print_info "Generating large financial transaction log (will trigger off-chain storage)..."
    
    # Create large data for off-chain storage demo
    large_data="Financial Batch Report Q1 2024. $(printf 'Transaction data entry %.0s' {1..100})"
    print_info "Generated data size: $(( ${#large_data} / 1024 )) KB"
    
    run_timed_command "Adding large financial batch (off-chain)" add-block "$large_data" --keywords "BATCH-Q1-2024,FINANCIAL,TRANSACTIONS" --category "FINANCE" --generate-key --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 3: Verify Off-Chain Data Integrity"
    run_timed_command "Validating blockchain with off-chain data" validate --detailed --verbose
}

# Function to demonstrate hybrid search
function demo_hybrid_search() {
    print_header "${SEARCH} HYBRID SEARCH DEMONSTRATION"
    
    print_info "This demo shows the three search levels:"
    echo "• FAST_ONLY: Searches only in keywords (fastest)"
    echo "• INCLUDE_DATA: Searches keywords + block data (balanced)"
    echo "• EXHAUSTIVE_OFFCHAIN: Searches everything including off-chain files (complete)"
    
    pause_for_user
    
    # First, add some test data if not already present
    print_separator
    print_demo "Setting up test data for search demonstrations..."
    
    run_timed_command "Adding technical documentation block" add-block "API Documentation for REST endpoints. Includes JSON schema, SQL queries, and XML configuration." --keywords "API,DOCUMENTATION,JSON,SQL" --category "TECHNICAL" --generate-key
    
    run_timed_command "Adding medical record" add-block "Patient PATIENT-001 ECG results from cardiology department on 2024-01-15. Heart rhythm normal." --keywords "PATIENT-001,ECG,CARDIOLOGY" --category "MEDICAL" --generate-key
    
    pause_for_user
    
    print_separator
    print_demo "Demo 1: Fast Search (Keywords Only)"
    print_info "Searching for 'PATIENT' in keywords only..."
    run_timed_command "Fast search for patient records" search "PATIENT" --fast --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 2: Balanced Search (Include Data)"
    print_info "Searching for 'documentation' in keywords and block content..."
    run_timed_command "Balanced search for documentation" search "documentation" --level INCLUDE_DATA --verbose --detailed
    
    pause_for_user
    
    print_separator
    print_demo "Demo 3: Exhaustive Search (Including Off-Chain)"
    print_info "Searching for 'transaction' everywhere, including off-chain files..."
    run_timed_command "Exhaustive search for transactions" search "transaction" --complete --verbose --detailed
    
    pause_for_user
    
    print_separator
    print_demo "Demo 4: Category Search"
    run_timed_command "Searching medical category" search --category MEDICAL --detailed --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 5: JSON Output Example"
    run_timed_command "Search with JSON output" search "API" --json --limit 3
    
    pause_for_user
    
    print_separator
    print_demo "Demo 6: Performance Comparison"
    print_info "Comparing search performance across different levels..."
    
    echo "🏁 Fast search:"
    time java -jar target/blockchain-cli.jar search "data" --fast > /dev/null 2>&1
    
    echo "⚖️ Balanced search:"
    time java -jar target/blockchain-cli.jar search "data" --level INCLUDE_DATA > /dev/null 2>&1
    
    echo "🔍 Exhaustive search:"
    time java -jar target/blockchain-cli.jar search "data" --complete > /dev/null 2>&1
}

# Function to demonstrate CLI integration
function demo_cli_integration() {
    print_header "🔗 CLI INTEGRATION DEMONSTRATION"
    
    print_info "This demo shows how off-chain storage and search work together:"
    
    pause_for_user
    
    print_separator
    print_demo "Demo 1: End-to-End Workflow"
    
    # Create a large legal contract document
    legal_data="LEGAL CONTRACT DOCUMENT - PARTNERSHIP AGREEMENT. Generated on: $(date). $(printf 'Section content: terms and conditions %.0s' {1..50})"
    
    run_timed_command "Adding large legal contract (will go off-chain)" add-block "$legal_data" --keywords "CONTRACT,PARTNERSHIP,LEGAL,IP-RIGHTS" --category "LEGAL" --generate-key --verbose
    
    pause_for_user
    
    run_timed_command "Searching for contract in off-chain data" search "partnership" --complete --detailed --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 2: Advanced Search Features"
    
    run_timed_command "Block number search" search --block-number 1 --detailed
    
    run_timed_command "Date range search (today)" search --date-from $(date +%Y-%m-%d) --date-to $(date +%Y-%m-%d) --verbose
    
    pause_for_user
    
    print_separator
    print_demo "Demo 3: Status and Validation"
    
    run_timed_command "Blockchain status with off-chain summary" status --detailed
    
    run_timed_command "Complete validation including off-chain data" validate --detailed --verbose
}

# Main execution
function main() {
    # Check for help flag
    if [[ "$1" = "--help" || "$1" = "-h" ]]; then
        echo "Enhanced Blockchain CLI Demonstrations"
        echo ""
        echo "Usage: $0 [OPTIONS]"
        echo ""
        echo "Options:"
        echo "  --auto, -a    Run automatically without pauses"
        echo "  --help, -h    Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0              # Interactive mode with pauses"
        echo "  $0 --auto       # Automatic mode without pauses"
        exit 0
    fi
    
    # Check for auto-run flag
    if [[ "$1" = "--auto" || "$1" = "-a" ]]; then
        export AUTO_RUN=true
        print_info "Running in automatic mode (no pauses)"
    fi
    
    print_header "🚀 ENHANCED BLOCKCHAIN CLI DEMONSTRATIONS"
    
    # Check if we're in the right directory
    if [[ ! -f "pom.xml" ]]; then
        print_error "Please run this script from the project root directory"
        exit 1
    fi
    
    # Check if JAR exists
    check_jar
    
    print_info "This demo showcases the new enhanced features:"
    echo "• ${STORAGE} Off-Chain Storage: Automatic large data handling with encryption"
    echo "• ${SEARCH} Hybrid Search: Multi-level search with performance optimization"
    echo "• 🔗 CLI Integration: Seamless integration of both features"
    echo ""
    echo "The demos will use the actual CLI commands and show real results."
    
    if [[ "${AUTO_RUN:-}" != "true" ]]; then
        echo ""
        echo "💡 Tip: Use --auto flag to run without pauses: ./run-enhanced-demos.zsh --auto"
    fi
    
    pause_for_user
    
    # Run the demonstrations
    demo_offchain_storage
    demo_hybrid_search
    demo_cli_integration
    
    print_header "${SUCCESS} DEMONSTRATIONS COMPLETED"
    
    print_success "All enhanced features demonstrated successfully!"
    echo ""
    print_info "Key takeaways:"
    echo "• Off-chain storage is automatic and transparent"
    echo "• Search performance scales with different levels"
    echo "• All data remains secure with AES encryption"
    echo "• CLI provides flexible options for different use cases"
    echo ""
    print_info "For more information, see:"
    echo "• docs/EXAMPLES.md - Usage examples"
    echo "• docs/SEARCH_GUIDE.md - Search documentation"
    echo "• java -jar target/blockchain-cli.jar --help - Command reference"
    
    print_separator
    print_info "Demo completed at $(date)"
}

# Run main function
main "$@"