#!/usr/bin/env zsh

# Simple Test Demos for Enhanced CLI
# This is a simplified version to test the CLI functionality

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

function print_info() {
    echo -e "${BLUE}ℹ️ $1${NC}"
}

function print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

function print_error() {
    echo -e "${RED}❌ $1${NC}"
}

function run_simple_command() {
    local description="$1"
    shift
    local cmd="java -jar target/blockchain-cli.jar $*"
    
    print_info "$description"
    echo "Command: $cmd"
    echo ""
    
    if eval "$cmd"; then
        print_success "Command succeeded"
    else
        print_error "Command failed"
    fi
    echo ""
}

echo "🚀 Simple Enhanced CLI Demos"
echo "================================"
echo ""

# Test 1: Basic functionality
run_simple_command "Testing basic status" status

# Test 2: Add a simple block
run_simple_command "Adding simple block" add-block "'Test medical record'" --keywords "'TEST,MEDICAL'" --category "'MEDICAL'" --generate-key

# Test 3: Search test
run_simple_command "Searching for medical blocks" search --category "'MEDICAL'"

# Test 4: Add larger block
run_simple_command "Adding larger block" add-block "'This is a larger financial report with more content to test the off-chain storage functionality when data exceeds the threshold.'" --keywords "'FINANCE,REPORT'" --category "'FINANCE'" --generate-key

# Test 5: Search in content
run_simple_command "Searching for financial" search "financial" --verbose

# Test 6: Validation
run_simple_command "Validating blockchain" validate --detailed

print_success "Simple demos completed!"