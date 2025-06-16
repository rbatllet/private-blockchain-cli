#!/usr/bin/env zsh

# test-rollback-setup.sh
# Setup test data for rollback testing in Private Blockchain CLI
# Version: 1.0.0
# ZSH adaptation

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Header
echo -e "${BLUE}🔄 PRIVATE BLOCKCHAIN CLI - ROLLBACK TEST SETUP${NC}"
echo -e "${BLUE}=============================================${NC}"
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

# Ask for test environment setup
echo -e "${YELLOW}This script will set up a test environment for rollback testing.${NC}"
echo -e "${YELLOW}It will create a separate directory with test data.${NC}"
echo -e "${YELLOW}Your current blockchain data will not be affected.${NC}"
echo ""
read "TEST_DIR?Enter name for test directory [rollback-test]: "
TEST_DIR=${TEST_DIR:-rollback-test}

# Create test directory
mkdir -p "$TEST_DIR"
cd "$TEST_DIR"

echo -e "${BLUE}🔍 Test environment: $PWD${NC}"
echo ""

# Initialize test blockchain
echo -e "${BLUE}Initializing test blockchain...${NC}"
java -jar ../target/blockchain-cli.jar status > /dev/null 2>&1

# Create test key
echo -e "${BLUE}Creating test authorized key...${NC}"
java -jar ../target/blockchain-cli.jar add-key "TestUser" --generate --show-private

# Ask for number of test blocks
read "NUM_BLOCKS?How many test blocks would you like to add? [10]: "
NUM_BLOCKS=${NUM_BLOCKS:-10}

# Add test blocks
echo -e "${BLUE}Adding $NUM_BLOCKS test blocks...${NC}"
for ((i=1; i<=NUM_BLOCKS; i++)); do
    echo -e "Adding block $i of $NUM_BLOCKS..."
    java -jar ../target/blockchain-cli.jar add-block "Test Block $i for rollback testing - $(date)" --signer TestUser
done

# Verify blocks were added
echo -e "${BLUE}Verifying blockchain state...${NC}"
java -jar ../target/blockchain-cli.jar status

# Create backup
echo -e "${BLUE}Creating backup of test blockchain...${NC}"
java -jar ../target/blockchain-cli.jar export initial-state.json

echo ""
echo -e "${GREEN}✅ Test environment setup complete!${NC}"
echo -e "${BLUE}Test directory: $PWD${NC}"
echo -e "${BLUE}Initial state backup: initial-state.json${NC}"
echo -e "${BLUE}Number of blocks: $(java -jar ../target/blockchain-cli.jar status --json | grep blockCount | cut -d':' -f2 | tr -d ' ,')${NC}"
echo ""
echo -e "${YELLOW}To run rollback tests:${NC}"
echo -e "  cd $TEST_DIR"
echo -e "  java -jar ../target/blockchain-cli.jar rollback --blocks <N> [options]"
echo -e "  java -jar ../target/blockchain-cli.jar rollback --to-block <N> [options]"
echo ""
echo -e "${YELLOW}To restore initial state:${NC}"
echo -e "  java -jar ../target/blockchain-cli.jar import initial-state.json --force"
echo ""
echo -e "${BLUE}For more information, see ROLLBACK_TESTING.md${NC}"
