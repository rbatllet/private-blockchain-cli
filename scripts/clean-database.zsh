#!/usr/bin/env zsh

# Clean Database Script
# Quick script to clean corrupted SQLite database files
# Version: 1.0.0
# ZSH adaptation

# Get the script directory and load common functions
SCRIPT_DIR="${0:a:h}"
source "$SCRIPT_DIR/lib/common-functions.sh"

echo -e "${BLUE}🧹 Database Cleanup Script${NC}"
echo "=================================="

cd "$SCRIPT_DIR/.."
print_info "Working directory: $(pwd)"

# Use enhanced cleanup function from common library
# (function is now defined in common-functions.sh)

# Check if sqlite3 is available
if ! command_exists sqlite3; then
    print_warning "sqlite3 command not found - some repair operations will be skipped"
fi

# Run the cleanup using the enhanced function from common library
cleanup_database

echo ""
print_info "Cleanup complete. You can now run CLI tests safely:"
print_info "  ./scripts/test-cli.zsh"
print_info "  ./scripts/run-java-demos.zsh"
