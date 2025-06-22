#!/usr/bin/env zsh

# test-rollback-interactive.sh
# Interactive rollback testing for Private Blockchain CLI
# Version: 1.0.1 - Updated to use new validation API
# ZSH adaptation

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
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

# Function to display blockchain status with enhanced information
function show_status() {
    echo -e "${BLUE}📊 Current blockchain status (enhanced with new validation API):${NC}"
    java -jar target/blockchain-cli.jar status --detailed
    echo ""
    
    echo -e "${CYAN}💡 Status Command Options:${NC}"
    echo -e "  • ${GREEN}status${NC}           - Standard status with validation overview"
    echo -e "  • ${GREEN}status --detailed${NC} - Comprehensive status with system info"
    echo -e "  • ${GREEN}status --json${NC}     - Machine-readable status output"
    echo ""
}

# Function to display detailed validation using new API
function show_detailed_validation() {
    echo -e "${MAGENTA}🔍 Detailed Blockchain Validation (using new validation API):${NC}"
    echo -e "${BLUE}=====================================================${NC}"
    java -jar target/blockchain-cli.jar validate --detailed
    echo ""
    
    echo -e "${CYAN}📋 Validation Options Available:${NC}"
    echo -e "  • ${GREEN}validate${NC}          - Standard validation with summary"
    echo -e "  • ${GREEN}validate --detailed${NC} - Detailed validation with comprehensive report"
    echo -e "  • ${GREEN}validate --quick${NC}    - Quick structural integrity check only"
    echo -e "  • ${GREEN}validate --json${NC}     - Output validation results in JSON format"
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
        echo -e "${BLUE}Validating restored blockchain...${NC}"
        java -jar target/blockchain-cli.jar validate --detailed
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
    return 0
}

# Function to perform rollback with enhanced validation
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
        echo -e "${BLUE}Performing detailed validation after rollback...${NC}"
        java -jar target/blockchain-cli.jar validate --detailed
        return 0
    else
        echo -e "${RED}❌ Rollback failed.${NC}"
        return 1
    fi
}

# Function to validate blockchain with different options
function validate_blockchain() {
    echo -e "${BLUE}Validation Options:${NC}"
    echo -e "1) Standard validation"
    echo -e "2) Detailed validation (recommended)"
    echo -e "3) Quick validation (structural integrity only)"
    echo -e "4) JSON output validation"
    echo -e "5) Cancel"
    echo ""
    read "validation_option?Select validation type: "
    
    case $validation_option in
        1)
            echo -e "${BLUE}Performing standard validation...${NC}"
            java -jar target/blockchain-cli.jar validate
            ;;
        2)
            echo -e "${BLUE}Performing detailed validation...${NC}"
            java -jar target/blockchain-cli.jar validate --detailed
            ;;
        3)
            echo -e "${BLUE}Performing quick validation...${NC}"
            java -jar target/blockchain-cli.jar validate --quick
            ;;
        4)
            echo -e "${BLUE}Performing validation with JSON output...${NC}"
            java -jar target/blockchain-cli.jar validate --json
            ;;
        5)
            echo -e "${YELLOW}Validation cancelled.${NC}"
            return 0
            ;;
        *)
            echo -e "${RED}❌ Invalid option.${NC}"
            return 1
            ;;
    esac
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}✅ Validation completed successfully.${NC}"
        return 0
    else
        echo -e "${RED}❌ Validation failed - blockchain has issues.${NC}"
        echo -e "${YELLOW}💡 Tip: Use 'Detailed validation' for more information about the problems.${NC}"
        return 1
    fi
}

# Function to demonstrate new validation API features
function demo_validation_api() {
    echo -e "${MAGENTA}🎯 VALIDATION API DEMONSTRATION${NC}"
    echo -e "${BLUE}================================${NC}"
    echo ""
    
    echo -e "${CYAN}This demonstration shows the enhanced validation capabilities:${NC}"
    echo ""
    
    echo -e "${GREEN}1. Standard validation (quick overview):${NC}"
    java -jar target/blockchain-cli.jar validate
    echo ""
    
    echo -e "${GREEN}2. Detailed validation (comprehensive analysis):${NC}"
    java -jar target/blockchain-cli.jar validate --detailed
    echo ""
    
    echo -e "${GREEN}3. JSON format (for programmatic use):${NC}"
    java -jar target/blockchain-cli.jar validate --json
    echo ""
    
    echo -e "${CYAN}Key features of the new validation API:${NC}"
    echo -e "  ✅ ${GREEN}isStructurallyIntact()${NC} - Checks chain structure and cryptography"
    echo -e "  ✅ ${GREEN}isFullyCompliant()${NC} - Checks authorization compliance"  
    echo -e "  📊 ${GREEN}getSummary()${NC} - Provides human-readable summary"
    echo -e "  📋 ${GREEN}getDetailedReport()${NC} - Comprehensive validation report"
    echo -e "  🔄 ${GREEN}getRevokedBlocks()${NC} - Count of blocks with revoked signatures"
    echo -e "  ❌ ${GREEN}getInvalidBlocks()${NC} - Count of structurally invalid blocks"
    echo ""
    
    echo -e "${YELLOW}💡 This replaces the old deprecated validateChain() method${NC}"
    echo -e "${YELLOW}   with much more detailed and useful information!${NC}"
    echo ""
}

# Function to show help about new validation features
function show_validation_help() {
    echo -e "${MAGENTA}🔍 NEW VALIDATION API HELP${NC}"
    echo -e "${BLUE}===========================${NC}"
    echo ""
    
    echo -e "${CYAN}The blockchain now uses an enhanced validation system that provides:${NC}"
    echo ""
    
    echo -e "${GREEN}Validation Modes:${NC}"
    echo -e "  • ${YELLOW}Standard${NC}  - Complete validation with summary"
    echo -e "  • ${YELLOW}Detailed${NC}  - Includes comprehensive analysis report"
    echo -e "  • ${YELLOW}Quick${NC}     - Fast structural integrity check"
    echo -e "  • ${YELLOW}JSON${NC}      - Machine-readable output format"
    echo ""
    
    echo -e "${GREEN}Validation Results:${NC}"
    echo -e "  • ${YELLOW}Structurally Intact${NC} - Chain links and cryptography are valid"
    echo -e "  • ${YELLOW}Fully Compliant${NC}     - All signatures use currently authorized keys"
    echo -e "  • ${YELLOW}Revoked Blocks${NC}      - Blocks signed by keys that were later revoked"
    echo -e "  • ${YELLOW}Invalid Blocks${NC}      - Blocks with structural or cryptographic problems"
    echo ""
    
    echo -e "${GREEN}Use Cases:${NC}"
    echo -e "  • ${CYAN}Production Systems${NC}  - Use isStructurallyIntact() for core operations"
    echo -e "  • ${CYAN}Compliance Checks${NC}   - Use isFullyCompliant() for auditing"
    echo -e "  • ${CYAN}Debugging${NC}           - Use detailed reports to identify problems"
    echo -e "  • ${CYAN}Monitoring${NC}          - Use JSON output for automated systems"
    echo ""
    
    echo -e "${YELLOW}💡 This provides much more insight than the old boolean validateChain() method!${NC}"
    echo ""
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
    echo -e "7) 🔍 Detailed validation report"
    echo -e "8) 🎯 Demo validation API features"
    echo -e "9) ❓ Help: New validation API"
    echo -e "10) Exit"
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
            show_detailed_validation
            ;;
        8)
            demo_validation_api
            ;;
        9)
            show_validation_help
            ;;
        10)
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