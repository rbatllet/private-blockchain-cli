#!/bin/bash

# Clean Database Script
# Quick script to clean corrupted SQLite database files
# Version: 1.1 - Refactored to use common functions

# Get the script directory and load common functions
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/lib/common-functions.sh"

echo -e "${BLUE}🧹 Database Cleanup Script${NC}"
echo "=================================="

cd "$SCRIPT_DIR"
print_info "Working directory: $(pwd)"

# Function to clean corrupted database files
clean_database() {
    print_info "Cleaning any corrupted database files..."
    
    # Clean SQLite database files in project root
    if [ -f "blockchain.db" ] || [ -f "blockchain.db-shm" ] || [ -f "blockchain.db-wal" ]; then
        print_info "Found existing database files, cleaning them..."
        rm -f blockchain.db blockchain.db-shm blockchain.db-wal 2>/dev/null || true
        print_success "Removed database files from project root"
    fi
    
    # Clean SQLite database files in blockchain-data directory  
    if [ -d "blockchain-data" ]; then
        if [ -f "blockchain-data/blockchain.db" ] || [ -f "blockchain-data/blockchain.db-shm" ] || [ -f "blockchain-data/blockchain.db-wal" ]; then
            print_info "Found existing database files in blockchain-data/, cleaning them..."
            rm -f blockchain-data/blockchain.db blockchain-data/blockchain.db-shm blockchain-data/blockchain.db-wal 2>/dev/null || true
            print_success "Removed database files from blockchain-data/"
        fi
    fi
    
    # If database exists but appears corrupted, try to repair it
    if [ -f "blockchain.db" ]; then
        print_info "Attempting to repair existing database..."
        if command_exists sqlite3; then
            if sqlite3 blockchain.db "PRAGMA wal_checkpoint(TRUNCATE);" 2>/dev/null; then
                print_success "WAL checkpoint completed"
            fi
            
            if sqlite3 blockchain.db "PRAGMA integrity_check;" > /dev/null 2>&1; then
                print_success "Database integrity check passed"
            else
                print_warning "Database appears corrupted, removing it..."
                rm -f blockchain.db blockchain.db-shm blockchain.db-wal 2>/dev/null || true
                print_success "Corrupted database removed"
            fi
        else
            print_warning "sqlite3 command not found - skipping database repair"
        fi
    fi
    
    print_success "Database cleanup completed!"
}

# Check if sqlite3 is available
if ! command_exists sqlite3; then
    print_warning "sqlite3 command not found - some repair operations will be skipped"
fi

# Run the cleanup
clean_database

echo ""
print_info "Cleanup complete. You can now run ./test-cli.sh safely."
