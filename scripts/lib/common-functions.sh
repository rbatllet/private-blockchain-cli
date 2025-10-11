#!/usr/bin/env zsh

# Common Functions Library for Private Blockchain CLI Scripts
# Adapted from CORE version for CLI usage
# Version: 2.0.0 (CLI adapted)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# CLI-specific emojis
CLI_EMOJI="🚀"
TEST_EMOJI="📋"
BUILD_EMOJI="🏗️"
SUCCESS_EMOJI="✅"
ERROR_EMOJI="❌"
INFO_EMOJI="ℹ️"
WARNING_EMOJI="⚠️"
CLEAN_EMOJI="🧹"

# Utility functions for colored output
print_header() {
    echo -e "${BLUE}📊 $1${NC}"
    echo "==============================================="
}

print_info() {
    echo -e "${BLUE}${INFO_EMOJI} $1${NC}"
}

print_success() {
    echo -e "${GREEN}${SUCCESS_EMOJI} $1${NC}"
}

print_error() {
    echo -e "${RED}${ERROR_EMOJI} $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}${WARNING_EMOJI} $1${NC}"
}

print_step() {
    echo -e "${PURPLE}📋 $1${NC}"
}

print_cli() {
    echo -e "${CYAN}${CLI_EMOJI} $1${NC}"
}

# Enhanced function to exit with error message
error_exit() {
    print_error "$1"
    exit 1
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if we're in the correct project directory
check_project_directory() {
    # If we detect we're in the scripts directory, navigate to parent
    if [[ "$(basename "$PWD")" == "scripts" ]]; then
        cd ..
    fi
    
    if [ ! -f "pom.xml" ]; then
        error_exit "pom.xml not found. Please run this script from the project root directory."
    fi
}

# Enhanced database cleanup function with corruption handling (from CORE)
cleanup_database() {
    # Allow skipping database cleanup with environment variable
    if [ "${SKIP_DB_CLEANUP:-}" = "true" ]; then
        print_info "Database cleanup skipped (SKIP_DB_CLEANUP=true)"
        return 0
    fi
    
    echo "🧹 Cleaning up database and temporary files..."
    
    # Clean SQLite database files in project root
    if [ -f "blockchain.db" ] || [ -f "blockchain.db-shm" ] || [ -f "blockchain.db-wal" ]; then
        print_info "Found existing database files, cleaning them..."
        rm -f blockchain.db blockchain.db-shm blockchain.db-wal 2>/dev/null || true
    fi
    
    # Clean database files using NULL_GLOB for safe wildcard handling
    setopt NULL_GLOB
    rm -f *.db *.sqlite *.sqlite3 2>/dev/null
    rm -f blockchain.db-shm blockchain.db-wal 2>/dev/null
    unsetopt NULL_GLOB
    
    # Clean SQLite database files in blockchain-data directory if it exists
    if [ -d "blockchain-data" ]; then
        if [ -f "blockchain-data/blockchain.db" ] || [ -f "blockchain-data/blockchain.db-shm" ] || [ -f "blockchain-data/blockchain.db-wal" ]; then
            print_info "Found existing database files in blockchain-data/, cleaning them..."
            rm -f blockchain-data/blockchain.db blockchain-data/blockchain.db-shm blockchain-data/blockchain.db-wal 2>/dev/null || true
        fi
    fi
    
    # Remove off-chain directories
    rm -rf off-chain-data off-chain-backup 2>/dev/null
    
    # Remove test export files if they exist
    setopt NULL_GLOB
    rm -f export_test_*.json 2>/dev/null
    rm -f export-test.json import-test.json 2>/dev/null
    rm -f corrupted_chain_recovery_*.json 2>/dev/null
    unsetopt NULL_GLOB
    
    # Remove temporary key files if they exist (preserve permanent keys in private-keys/)
    setopt NULL_GLOB
    rm -f temp_*.key test_*.key demo_*.key 2>/dev/null
    unsetopt NULL_GLOB
    
    # Remove class files if they exist
    find . -maxdepth 1 -name "*.class" -delete 2>/dev/null || true
    
    # Remove CLI-specific temporary log files (preserve logs/ directory)
    find . -maxdepth 1 -name "*.log" -delete 2>/dev/null || true
    rm -rf target/test-logs 2>/dev/null || true
    
    # CLI-specific: Clean target/blockchain-cli.jar if requested
    if [ "${CLEAN_JAR:-}" = "true" ]; then
        rm -f target/blockchain-cli*.jar 2>/dev/null || true
        print_info "Removed CLI JAR files"
    fi
    
    # If database exists but appears corrupted, try to repair it
    if [ -f "blockchain.db" ]; then
        print_info "Attempting to repair existing database..."
        if command_exists sqlite3; then
            sqlite3 blockchain.db "PRAGMA wal_checkpoint(TRUNCATE);" 2>/dev/null || true
            sqlite3 blockchain.db "PRAGMA integrity_check;" > /dev/null 2>&1 || {
                print_warning "Database appears corrupted, removing it..."
                rm -f blockchain.db blockchain.db-shm blockchain.db-wal 2>/dev/null || true
            }
        else
            print_warning "sqlite3 command not found - skipping database repair"
        fi
    fi
    
    echo "✅ Cleanup complete"
}

# Function to check Java availability
check_java() {
    if ! command -v java &> /dev/null; then
        echo "❌ Java is not installed or not in PATH"
        echo "ℹ️  Please install Java 11 or higher"
        return 1
    fi
    
    # Check Java version
    local java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [[ "$java_version" -lt 11 ]]; then
        echo "❌ Java version $java_version detected. Java 11 or higher is required."
        return 1
    fi
    
    return 0
}

# Function to check Maven availability
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven is not installed or not in PATH"
        echo "ℹ️  Please install Maven 3.6 or higher"
        return 1
    fi
    return 0
}

# Enhanced function to compile the project
compile_project() {
    echo "📦 Compiling project..."
    if mvn compile -q; then
        print_success "Project compiled successfully"
        return 0
    else
        print_error "Compilation failed"
        return 1
    fi
}

# CLI-specific function to build JAR
build_cli_jar() {
    print_info "Building CLI JAR..."
    if mvn package -q -DskipTests; then
        print_success "CLI JAR built successfully"
        return 0
    else
        print_error "JAR build failed"
        return 1
    fi
}

# CLI-specific function to find JAR file
find_cli_jar() {
    # First try the standard name
    if [[ -f "target/blockchain-cli.jar" ]]; then
        echo "target/blockchain-cli.jar"
        return 0
    fi
    
    # Try pattern matching for versioned JARs
    local jar_file=$(find target -name "blockchain-cli*.jar" -not -name "*sources*" -not -name "*javadoc*" 2>/dev/null | head -1)
    if [[ -n "$jar_file" ]]; then
        echo "$jar_file"
        return 0
    fi
    
    return 1
}

# Function to run CLI command with proper JAR handling
run_cli_command() {
    local jar_file=$(find_cli_jar)
    if [[ -z "$jar_file" ]]; then
        print_error "CLI JAR not found. Please build the project first."
        return 1
    fi
    
    java -jar "$jar_file" "$@"
}

# Function to compile project and tests (from CORE)
compile_project_with_tests() {
    print_step "Compiling project and tests..."
    mvn clean compile test-compile -q
    
    if [ $? -ne 0 ]; then
        error_exit "Project compilation failed. Please check for syntax errors."
    fi
    
    print_success "Project and tests compiled successfully"
}

# Function to create a colored separator (from CORE)
print_separator() {
    echo
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo
}

# Function to print a section header (from CORE)
print_section() {
    local title="$1"
    echo
    echo "📊 $title"
    echo "────────────────────────────────────────────────────"
}

# Function to handle errors (from CORE)
handle_error() {
    local error_msg="$1"
    echo
    echo "❌ ERROR: $error_msg"
    echo "ℹ️  Check the logs for more details"
    exit 1
}

# Function to clean logs directory (adapted for CLI)
cleanup_logs() {
    if [[ -d "logs" ]]; then
        echo "🗑️  Cleaning up log files..."
        rm -f logs/*.log 2>/dev/null || true
        echo "✅ Log files cleaned"
    fi
    
    # CLI-specific: also clean test logs
    if [[ -d "target/test-logs" ]]; then
        echo "🗑️  Cleaning up test log files..."
        rm -rf target/test-logs 2>/dev/null || true
        echo "✅ Test log files cleaned"
    fi
}

# Function to ensure logs directory exists (from CORE)
ensure_logs_dir() {
    if [[ ! -d "logs" ]]; then
        mkdir -p logs
    fi
}

# Function to check if running as admin/root (from CORE)
check_not_root() {
    if [ "$EUID" -eq 0 ]; then
        print_warning "Running as root is not recommended for this application"
    fi
}

# Function to check disk space (from CORE)
check_disk_space() {
    local required_mb=${1:-100}  # Default 100MB
    local available_mb=$(df . | tail -1 | awk '{print $4}')
    available_mb=$((available_mb / 1024))  # Convert to MB
    
    if [ "$available_mb" -lt "$required_mb" ]; then
        print_warning "Low disk space: ${available_mb}MB available, ${required_mb}MB recommended"
        return 1
    fi
    return 0
}

# Function to wait for user confirmation (from CORE)
confirm() {
    local message=${1:-"Do you want to continue?"}
    echo -n "${message} [y/N]: "
    read -r response
    case "$response" in
        [yY]|[yY][eE][sS]) return 0 ;;
        *) return 1 ;;
    esac
}

# Function to show CLI status (CLI-specific)
show_cli_status() {
    print_header "CLI Status"
    
    if check_java; then
        print_success "Java is available"
    else
        print_error "Java is not available"
    fi
    
    if check_maven; then
        print_success "Maven is available"
    else
        print_error "Maven is not available"
    fi
    
    local jar_file=$(find_cli_jar)
    if [[ -n "$jar_file" ]]; then
        print_success "CLI JAR found: $jar_file"
    else
        print_warning "CLI JAR not found - build required"
    fi
    
    if [[ -f "blockchain.db" ]]; then
        print_info "Database exists"
    else
        print_info "No database found (will be created on first use)"
    fi
    
    # Additional CLI-specific checks
    check_not_root
    check_disk_space 500  # CLI needs more space for JARs and tests
}

# Enhanced CLI diagnostic function
diagnose_cli_environment() {
    print_header "CLI Environment Diagnosis"
    
    print_section "System Requirements"
    check_java && print_success "Java requirement met" || print_error "Java requirement NOT met"
    check_maven && print_success "Maven requirement met" || print_error "Maven requirement NOT met"
    
    print_section "Project Structure"
    check_project_directory && print_success "Project structure valid" || print_error "Invalid project structure"
    
    print_section "Build Status"
    local jar_file=$(find_cli_jar)
    if [[ -n "$jar_file" ]]; then
        print_success "CLI JAR ready: $jar_file"
        local jar_size=$(du -h "$jar_file" 2>/dev/null | cut -f1)
        print_info "JAR size: $jar_size"
    else
        print_warning "CLI JAR not found - build required"
    fi
    
    print_section "Database Status"
    if [[ -f "blockchain.db" ]]; then
        local db_size=$(du -h blockchain.db 2>/dev/null | cut -f1)
        print_info "Database exists (size: $db_size)"
    else
        print_info "No database found (will be created on first use)"
    fi
    
    print_section "Resources"
    check_disk_space 500 && print_success "Sufficient disk space" || print_warning "Low disk space"
    
    print_section "Permissions"
    check_not_root
    
    print_separator
    print_info "Diagnosis complete. Use 'show_cli_status' for quick status check."
}