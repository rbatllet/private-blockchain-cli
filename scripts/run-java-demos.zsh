#!/usr/bin/env zsh

# Java Demos Runner Script
# Executes the Java demo classes directly via Maven
# Version: 1.0.0 - Runs OffChainStorageDemo and HybridSearchDemo

# Colors for better output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Emojis
SUCCESS="✅"
ERROR="❌"
INFO="ℹ️"
DEMO="🚀"
STORAGE="💾"
SEARCH="🔍"

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

# Function to pause for user interaction (optional)
function pause_for_user() {
    if [[ "${AUTO_RUN:-}" != "true" ]]; then
        echo ""
        echo -e "${YELLOW}Press Enter to continue or Ctrl+C to exit...${NC}"
        read
    else
        echo ""
        sleep 1
    fi
}

# Function to run demo with timing
function run_demo() {
    local demo_name="$1"
    local demo_class="$2"
    local description="$3"
    
    print_demo "$demo_name"
    print_info "$description"
    
    pause_for_user
    
    echo -e "${CYAN}Starting $demo_name...${NC}"
    echo ""
    
    local start_time=$(date +%s)
    
    if mvn -q exec:java -Dexec.mainClass="$demo_class"; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        print_success "$demo_name completed successfully in ${duration}s"
    else
        print_error "$demo_name failed"
        return 1
    fi
    
    pause_for_user
}

# Main execution
function main() {
    # Check for help flag
    if [[ "$1" = "--help" || "$1" = "-h" ]]; then
        echo "Java Demo Classes Runner"
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
    
    print_header "🚀 BLOCKCHAIN CLI - JAVA DEMOS RUNNER"
    
    # Get script directory and navigate to project root
    SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
    cd "$SCRIPT_DIR/.."
    
    # Check if we're in the right directory
    if [[ ! -f "pom.xml" ]]; then
        print_error "Please run this script from the project root directory"
        exit 1
    fi
    
    print_info "This script runs the Java demo classes that showcase:"
    echo "• ${STORAGE} Off-Chain Storage: Automatic large data handling with encryption"
    echo "• ${SEARCH} Hybrid Search: Multi-level search capabilities with performance optimization"
    echo ""
    print_info "The demos will create test data and demonstrate real functionality."
    
    if [[ "${AUTO_RUN:-}" != "true" ]]; then
        echo ""
        echo "💡 Tip: Use --auto flag to run without pauses: ./run-java-demos.zsh --auto"
    fi
    
    pause_for_user
    
    # Build the project first
    print_header "🏗️ BUILD PHASE"
    print_info "Building project to ensure demos are up to date..."
    
    if mvn compile -q; then
        print_success "Project compiled successfully"
    else
        print_error "Build failed. Please check for compilation errors."
        exit 1
    fi
    
    # Run Off-Chain Storage Demo
    run_demo \
        "Off-Chain Storage Demo" \
        "demo.OffChainStorageDemo" \
        "Demonstrates automatic storage decisions, AES encryption, and large data handling"
    
    # Run Hybrid Search Demo
    run_demo \
        "Hybrid Search Demo" \
        "demo.HybridSearchDemo" \
        "Demonstrates multi-level search, performance comparison, and comprehensive search features"
    
    print_header "${SUCCESS} ALL DEMOS COMPLETED"
    
    print_success "Both Java demos executed successfully!"
    echo ""
    print_info "What was demonstrated:"
    echo "• Automatic off-chain storage for large data (>512KB)"
    echo "• AES-256-CBC encryption for off-chain files"
    echo "• Three search levels: FAST_ONLY, INCLUDE_DATA, EXHAUSTIVE_OFFCHAIN"
    echo "• Keyword extraction and management"
    echo "• Category-based organization"
    echo "• Performance optimization and timing"
    echo "• Thread-safe blockchain operations"
    echo "• Complete validation including off-chain data"
    echo ""
    print_info "For CLI usage examples, run:"
    echo "• ./run-enhanced-demos.zsh - Interactive CLI demonstrations"
    echo "• ./test-cli.sh - Complete test suite including enhanced features"
    echo "• java -jar target/blockchain-cli.jar --help - Command reference"
    
    echo ""
    print_info "Demo completed at $(date)"
}

# Check for Maven
if ! command -v mvn >/dev/null 2>&1; then
    print_error "Maven is required to run the demos. Please install Maven first."
    exit 1
fi

# Check for bc (for timing calculations)
if ! command -v bc >/dev/null 2>&1; then
    print_info "Note: 'bc' not found. Timing calculations may not work properly."
fi

# Run main function
main "$@"