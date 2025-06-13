#!/bin/bash

# Common Functions Library
# Shared functions for blockchain CLI scripts
# Version: 1.0

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Function to print colored messages
print_header() {
    echo -e "${BLUE}$1${NC}"
    echo "=============================================="
}

print_test() {
    echo -e "${PURPLE}📋 Test $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Function to get script directory
get_script_dir() {
    echo "$(cd "$(dirname "${BASH_SOURCE[1]}")" && pwd)"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}
