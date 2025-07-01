#!/usr/bin/env zsh

# Common Functions Library
# Shared functions for blockchain CLI scripts
# Version: 1.0.0
# ZSH adaptation

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Function to print colored messages
function print_header() {
    echo -e "${BLUE}📊 $1${NC}"
    echo "==============================================="
}

function print_test() {
    echo -e "${PURPLE}📋 Test $1${NC}"
}

function print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

function print_error() {
    echo -e "${RED}❌ $1${NC}"
}

function print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

function print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Function to get script directory
function get_script_dir() {
    echo "${${(%):-%x}:a:h}"
}

# Function to check if command exists
function command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Standardized test counting functions
function count_test_passed() {
    ((TOTAL_TESTS++))
    ((TESTS_PASSED++))
}

function count_test_failed() {
    ((TOTAL_TESTS++))
    ((TESTS_FAILED++))
}

# Helper function for module tests (already counting total)
function record_test_result() {
    local success="$1"
    if [[ "$success" == "true" ]]; then
        ((TESTS_PASSED++))
    else
        ((TESTS_FAILED++))
    fi
}
