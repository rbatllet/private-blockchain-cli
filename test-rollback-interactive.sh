#!/usr/bin/env zsh

# test-rollback-interactive.sh
# Interactive rollback testing for Private Blockchain CLI
# Version: 1.0.0
# ZSH adaptation

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Header
echo -e "${BLUE}🔄 PRIVATE BLOCKCHAIN CLI - INTERACTIVE ROLLBACK TESTING${NC}"
echo -e "${BLUE}====================================================${NC}"
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

# Function to display blockchain status
function show_status() {
    echo -e "${BLUE}Current blockchain status:${NC}"
    java -jar target/blockchain-cli.jar status
    echo ""
}

# Function to create a backup
function create_backup() {
    local backup_file="rollback-backup-$(date +%Y%m%d-%H%M%S).json"
    echo -e "${BLUE}Creating backup: $backup_file${NC}"
    java -jar target/blockchain-cli.jar export "$backup_file"
    echo -e "${GREEN}✅ Backup created: $backup_file${NC}"
    echo ""
    return 0
}

# Function to restore from backup
function restore_backup() {
    echo -e "${BLUE}Available backups:${NC}"
    ls -1 rollback-backup-*.json 2>/dev/null
    
    if [[ $? -ne 0 ]]; then
        echo -e "${RED}No backups found.${NC}"
        return 1
    fi
    
    echo ""
    read "backup_file?Enter backup file to restore: "
    
    if [[ ! -f "$backup_file" ]]; then
        echo -e "${RED}❌ Error: Backup file not found.${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}⚠️ Warning: This will overwrite your current blockchain data.${NC}"
    read "confirm?Are you sure you want to proceed? (yes/no): "
    
    if [[ "$confirm" != "yes" ]]; then
        echo -e "${YELLOW}Restore cancelled.${NC}"
        return 1
    fi
    
    echo -e "${BLUE}Restoring from backup: $backup_file${NC}"
    java -jar target/blockchain-cli.jar import "$backup_file" --force
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}✅ Restore completed successfully.${NC}"
        show_status
        return 0
    else
        echo -e "${RED}❌ Restore failed.${NC}"
        return 1
    fi
}

# Function to add test blocks
function add_test_blocks() {
    read "num_blocks?How many test blocks would you like to add? "
    
    if ! [[ "$num_blocks" =~ ^[0-9]+$ ]]; then
        echo -e "${RED}❌ Error: Please enter a valid number.${NC}"
        return 1
    fi
    
    echo -e "${BLUE}Adding $num_blocks test blocks...${NC}"
    
    for ((i=1; i<=num_blocks; i++)); do
        echo -e "Adding block $i of $num_blocks..."
        java -jar target/blockchain-cli.jar add-block "Interactive Test Block $i - $(date)" --generate-key
    done
    
    echo -e "${GREEN}✅ Added $num_blocks test blocks.${NC}"
    show_status
    return 0
}

# Function to perform rollback
function perform_rollback() {
    echo -e "${BLUE}Rollback Options:${NC}"
    echo -e "1) Rollback by number of blocks"
    echo -e "2) Rollback to specific block"
    echo -e "3) Cancel"
    echo ""
    read "rollback_option?Select option: "
    
    case $rollback_option in
        1)
            read "blocks?How many blocks to remove? "
            if ! [[ "$blocks" =~ ^[0-9]+$ ]]; then
                echo -e "${RED}❌ Error: Please enter a valid number.${NC}"
                return 1
            fi
            
            echo -e "${BLUE}Performing dry run first...${NC}"
            java -jar target/blockchain-cli.jar rollback --blocks "$blocks" --dry-run
            
            read "confirm?Proceed with actual rollback? (yes/no): "
            if [[ "$confirm" != "yes" ]]; then
                echo -e "${YELLOW}Rollback cancelled.${NC}"
                return 1
            fi
            
            echo -e "${BLUE}Performing rollback...${NC}"
            java -jar target/blockchain-cli.jar rollback --blocks "$blocks" --yes
            ;;
        2)
            read "target_block?Rollback to which block number? "
            if ! [[ "$target_block" =~ ^[0-9]+$ ]]; then
                echo -e "${RED}❌ Error: Please enter a valid number.${NC}"
                return 1
            fi
            
            echo -e "${BLUE}Performing dry run first...${NC}"
            java -jar target/blockchain-cli.jar rollback --to-block "$target_block" --dry-run
            
            read "confirm?Proceed with actual rollback? (yes/no): "
            if [[ "$confirm" != "yes" ]]; then
                echo -e "${YELLOW}Rollback cancelled.${NC}"
                return 1
            fi
            
            echo -e "${BLUE}Performing rollback...${NC}"
            java -jar target/blockchain-cli.jar rollback --to-block "$target_block" --yes
            ;;
        3)
            echo -e "${YELLOW}Rollback cancelled.${NC}"
            return 0
            ;;
        *)
            echo -e "${RED}❌ Invalid option.${NC}"
            return 1
            ;;
    esac
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}✅ Rollback completed successfully.${NC}"
        show_status
        return 0
    else
        echo -e "${RED}❌ Rollback failed.${NC}"
        return 1
    fi
}

# Function to validate blockchain
function validate_blockchain() {
    echo -e "${BLUE}Validating blockchain...${NC}"
    java -jar target/blockchain-cli.jar validate --detailed
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}✅ Blockchain is valid.${NC}"
        return 0
    else
        echo -e "${RED}❌ Blockchain validation failed.${NC}"
        return 1
    fi
}

# Main menu
while true; do
    echo -e "${CYAN}=== INTERACTIVE ROLLBACK TESTING MENU ===${NC}"
    echo -e "1) Show blockchain status"
    echo -e "2) Create blockchain backup"
    echo -e "3) Restore from backup"
    echo -e "4) Add test blocks"
    echo -e "5) Perform rollback"
    echo -e "6) Validate blockchain"
    echo -e "7) Exit"
    echo ""
    read "option?Select option: "
    echo ""
    
    case $option in
        1)
            show_status
            ;;
        2)
            create_backup
            ;;
        3)
            restore_backup
            ;;
        4)
            add_test_blocks
            ;;
        5)
            perform_rollback
            ;;
        6)
            validate_blockchain
            ;;
        7)
            echo -e "${GREEN}Exiting interactive rollback testing.${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Invalid option. Please try again.${NC}"
            ;;
    esac
    
    echo ""
    read "?Press Enter to continue..."
    clear
done
