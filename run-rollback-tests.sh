#!/usr/bin/env zsh

# run-rollback-tests.sh
# Complete test suite for rollback functionality in Private Blockchain CLI
# Version: 1.1.0
# ZSH adaptation

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Header
echo -e "${BLUE}🔄 PRIVATE BLOCKCHAIN CLI - ROLLBACK TEST SUITE${NC}"
echo -e "${BLUE}=================================================${NC}"
echo ""
echo -e "Running comprehensive rollback tests..."
echo ""

# Check if we're in the correct directory
if [[ ! -f "pom.xml" ]]; then
    echo -e "${RED}❌ Error: pom.xml not found. Please run this script from the project root directory.${NC}"
    exit 1
fi

# Check if blockchain-cli.jar exists
if [[ ! -f "target/blockchain-cli.jar" ]]; then
    echo -e "${YELLOW}⚠️ Warning: blockchain-cli.jar not found in target directory.${NC}"
    echo -e "${YELLOW}Building project...${NC}"
    mvn clean package -DskipTests
    
    if [[ ! -f "target/blockchain-cli.jar" ]]; then
        echo -e "${RED}❌ Error: Failed to build blockchain-cli.jar${NC}"
        exit 1
    fi
fi

# Create test directory
TEST_DIR="rollback-test-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$TEST_DIR"
cd "$TEST_DIR"

echo -e "${BLUE}🔍 Test environment: $PWD${NC}"
echo ""

# Function to run a test and check result
function run_test() {
    local test_name="$1"
    local command="$2"
    local expected_exit_code="$3"
    
    echo -e "${YELLOW}Running test: $test_name${NC}"
    echo -e "Command: $command"
    
    eval "$command"
    local actual_exit_code=$?
    
    if [[ "$actual_exit_code" -eq "$expected_exit_code" ]]; then
        echo -e "${GREEN}✅ Test passed: $test_name${NC}"
        return 0
    else
        echo -e "${RED}❌ Test failed: $test_name${NC}"
        echo -e "${RED}Expected exit code: $expected_exit_code, Actual: $actual_exit_code${NC}"
        return 1
    fi
}

# Initialize test blockchain
echo -e "${BLUE}Initializing test blockchain...${NC}"
java -jar ../target/blockchain-cli.jar status > /dev/null 2>&1

# Add test blocks
echo -e "${BLUE}Adding test blocks...${NC}"
for ((i=1; i<=10; i++)); do
    java -jar ../target/blockchain-cli.jar add-block "Test Block $i for rollback testing" --generate-key > /dev/null 2>&1
done

# Verify blocks were added
echo -e "${BLUE}Verifying initial blockchain state...${NC}"
java -jar ../target/blockchain-cli.jar status

echo -e "${BLUE}Running rollback tests...${NC}"
echo ""

# Test 1: Basic parameter validation
run_test "No parameters (should fail)" "java -jar ../target/blockchain-cli.jar rollback" 2
run_test "Negative blocks (should fail)" "java -jar ../target/blockchain-cli.jar rollback --blocks -1" 2
run_test "Zero blocks (should fail)" "java -jar ../target/blockchain-cli.jar rollback --blocks 0" 2
run_test "Conflicting parameters (should fail)" "java -jar ../target/blockchain-cli.jar rollback --blocks 1 --to-block 5" 2

# Test 2: Dry run functionality
run_test "Dry run with blocks" "java -jar ../target/blockchain-cli.jar rollback --blocks 2 --dry-run" 0
run_test "Dry run with target block" "java -jar ../target/blockchain-cli.jar rollback --to-block 5 --dry-run" 0
run_test "Dry run with JSON output" "java -jar ../target/blockchain-cli.jar rollback --blocks 2 --dry-run --json" 0

# Test 3: Edge cases
run_test "Too many blocks (should fail)" "java -jar ../target/blockchain-cli.jar rollback --blocks 100 --dry-run" 1
run_test "Non-existent target (should fail)" "java -jar ../target/blockchain-cli.jar rollback --to-block 100 --dry-run" 1

# Test 4: Actual rollback operations
echo -e "${BLUE}Testing actual rollback operations...${NC}"

# Export before rollback
java -jar ../target/blockchain-cli.jar export before-rollback.json

# Rollback 2 blocks
run_test "Rollback 2 blocks" "java -jar ../target/blockchain-cli.jar rollback --blocks 2 --yes" 0

# Verify blockchain state after rollback
echo -e "${BLUE}Verifying blockchain state after rollback...${NC}"
java -jar ../target/blockchain-cli.jar status

# Validate blockchain
run_test "Validate after rollback" "java -jar ../target/blockchain-cli.jar validate" 0

# Add more blocks
echo -e "${BLUE}Adding more test blocks...${NC}"
for ((i=1; i<=5; i++)); do
    java -jar ../target/blockchain-cli.jar add-block "Additional Test Block $i" --generate-key > /dev/null 2>&1
done

# Rollback to specific block
run_test "Rollback to block 5" "java -jar ../target/blockchain-cli.jar rollback --to-block 5 --yes" 0

# Verify blockchain state after rollback to specific block
echo -e "${BLUE}Verifying blockchain state after rollback to specific block...${NC}"
java -jar ../target/blockchain-cli.jar status

# Validate blockchain again
run_test "Validate after rollback to specific block" "java -jar ../target/blockchain-cli.jar validate" 0

# Test 5: JSON output
run_test "JSON output" "java -jar ../target/blockchain-cli.jar rollback --blocks 1 --yes --json" 0

# Summary
echo ""
echo -e "${BLUE}🔄 ROLLBACK TEST SUMMARY${NC}"
echo -e "${BLUE}======================${NC}"
echo -e "Test directory: $PWD"
echo -e "Initial state: Exported to before-rollback.json"
echo -e "Final state: $(java -jar ../target/blockchain-cli.jar status --json | grep blockCount)"
echo ""
echo -e "${GREEN}✅ Rollback tests completed${NC}"
echo -e "${YELLOW}⚠️ Note: Test blockchain remains in $TEST_DIR${NC}"
echo -e "${YELLOW}⚠️ Delete this directory when no longer needed${NC}"

# Return to original directory
cd ..
echo ""
echo -e "${BLUE}Returned to: $PWD${NC}"
