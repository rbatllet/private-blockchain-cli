#!/usr/bin/env zsh

# Configuration Management Demo
# Demonstrates CLI configuration features

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
CLI_JAR="$PROJECT_DIR/target/blockchain-cli.jar"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 Configuration Management Demo${NC}"
echo -e "${BLUE}==================================${NC}"
echo

# Check if JAR exists
if [[ ! -f "$CLI_JAR" ]]; then
    echo -e "${RED}❌ CLI JAR not found: $CLI_JAR${NC}"
    echo -e "${YELLOW}💡 Please run 'mvn clean package' first${NC}"
    exit 1
fi

# Function to run CLI command
run_cli() {
    echo -e "${BLUE}$ java -jar blockchain-cli.jar $*${NC}"
    java -jar "$CLI_JAR" "$@"
    echo
}

# Function to show section
show_section() {
    echo -e "${YELLOW}📋 $1${NC}"
    echo -e "${YELLOW}$(echo "$1" | sed 's/./─/g')${NC}"
}

echo -e "${GREEN}✅ Starting Configuration Demo${NC}"
echo

# 1. Show current configuration
show_section "Current Configuration"
run_cli config show

# 2. Show detailed configuration
show_section "Detailed Configuration View"
run_cli config show --detailed

# 3. Show available profiles
show_section "Available Configuration Profiles"
run_cli config profiles

# 4. Apply development profile
show_section "Applying Development Profile"
run_cli config apply-profile --profile development

# 5. Show updated configuration
show_section "Configuration After Development Profile"
run_cli config show

# 6. Set individual configuration values
show_section "Setting Individual Configuration Values"
run_cli config set --key search.limit --value 200
run_cli config set --key verbose.mode --value true
run_cli config set --key output.format --value json

# 7. Show configuration after changes
show_section "Configuration After Individual Changes"
run_cli config show

# 8. Export configuration
show_section "Exporting Configuration"
EXPORT_FILE="/tmp/blockchain-cli-config-backup.properties"
run_cli config export --file "$EXPORT_FILE"
echo -e "${GREEN}📁 Configuration exported to: $EXPORT_FILE${NC}"
echo

# Show exported file contents
if [[ -f "$EXPORT_FILE" ]]; then
    echo -e "${BLUE}📄 Exported Configuration File Contents:${NC}"
    echo -e "${BLUE}────────────────────────────────────────${NC}"
    head -20 "$EXPORT_FILE"
    echo -e "${BLUE}────────────────────────────────────────${NC}"
    echo
fi

# 9. Apply production profile
show_section "Applying Production Profile"
run_cli config apply-profile --profile production

# 10. Show production configuration
show_section "Production Configuration"
run_cli config show --detailed

# 11. Test configuration with actual commands
show_section "Testing Configuration with Search Command"
echo -e "${YELLOW}ℹ️  Current config should use production settings${NC}"
run_cli config show --json | head -10

# 12. Set custom properties
show_section "Setting Custom Properties"
run_cli config set --key custom.demo_mode --value enabled
run_cli config set --key custom.user_preference --value "detailed_output"
run_cli config set --key custom.theme --value "dark"

# 13. Show configuration with custom properties
show_section "Configuration with Custom Properties"
run_cli config show --detailed

# 14. Reset configuration
show_section "Resetting Configuration to Defaults"
run_cli config reset

# 15. Show default configuration
show_section "Default Configuration After Reset"
run_cli config show

# 16. Import previously exported configuration
if [[ -f "$EXPORT_FILE" ]]; then
    show_section "Importing Previously Exported Configuration"
    run_cli config import --file "$EXPORT_FILE"
    
    show_section "Configuration After Import"
    run_cli config show
fi

# 17. Apply performance profile for demo
show_section "Applying Performance Profile"
run_cli config apply-profile --profile performance

# 18. Show JSON output
show_section "Configuration in JSON Format"
run_cli config show --json

# 19. Demonstrate configuration with other commands
show_section "Using Configuration with Other Commands"
echo -e "${YELLOW}ℹ️  Running status command with current config settings${NC}"
run_cli status --json

# Clean up
if [[ -f "$EXPORT_FILE" ]]; then
    rm -f "$EXPORT_FILE"
    echo -e "${GREEN}🧹 Cleaned up temporary export file${NC}"
fi

echo -e "${GREEN}✅ Configuration Demo Completed Successfully!${NC}"
echo
echo -e "${BLUE}📋 Configuration Features Demonstrated:${NC}"
echo -e "${BLUE}  • Viewing current configuration${NC}"
echo -e "${BLUE}  • Applying configuration profiles (dev, production, performance)${NC}"
echo -e "${BLUE}  • Setting individual configuration values${NC}"
echo -e "${BLUE}  • Exporting and importing configuration${NC}"
echo -e "${BLUE}  • Custom properties support${NC}"
echo -e "${BLUE}  • JSON output format${NC}"
echo -e "${BLUE}  • Configuration reset${NC}"
echo
echo -e "${YELLOW}💡 Next Steps:${NC}"
echo -e "${YELLOW}  • Try: blockchain config show --detailed${NC}"
echo -e "${YELLOW}  • Try: blockchain config apply-profile --profile development${NC}"
echo -e "${YELLOW}  • Try: blockchain config set --key search.limit --value 100${NC}"
echo -e "${YELLOW}  • Configuration is saved in ~/.blockchain-cli/config.properties${NC}"