#!/usr/bin/env zsh

# Setup all databases for blockchain CLI
# This script provides an interactive menu to setup PostgreSQL and/or MySQL

set -e

SCRIPT_DIR="${0:A:h}"

echo "🔗 Blockchain CLI - Database Setup"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "This script will help you setup databases for the blockchain CLI."
echo "You can choose to setup PostgreSQL, MySQL, or both."
echo ""

# Function to setup PostgreSQL
setup_postgresql() {
    echo ""
    echo "📊 Setting up PostgreSQL..."
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    if [[ -f "$SCRIPT_DIR/setup-postgresql.zsh" ]]; then
        "$SCRIPT_DIR/setup-postgresql.zsh"
    else
        echo "❌ setup-postgresql.zsh not found in $SCRIPT_DIR"
        return 1
    fi
}

# Function to setup MySQL
setup_mysql() {
    echo ""
    echo "📊 Setting up MySQL..."
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    if [[ -f "$SCRIPT_DIR/setup-mysql.zsh" ]]; then
        "$SCRIPT_DIR/setup-mysql.zsh"
    else
        echo "❌ setup-mysql.zsh not found in $SCRIPT_DIR"
        return 1
    fi
}

# Interactive menu
echo "Which database(s) do you want to setup?"
echo ""
echo "  1) PostgreSQL only"
echo "  2) MySQL only"
echo "  3) Both PostgreSQL and MySQL"
echo "  4) Exit"
echo ""
read "choice?Enter your choice (1-4): "

case $choice in
    1)
        setup_postgresql
        ;;
    2)
        setup_mysql
        ;;
    3)
        setup_postgresql
        setup_mysql
        ;;
    4)
        echo "ℹ️  Setup cancelled"
        exit 0
        ;;
    *)
        echo "❌ Invalid choice. Please run the script again."
        exit 1
        ;;
esac

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Database setup completed!"
echo ""
echo "📚 Next steps:"
echo "  1. Build the CLI: mvn clean package"
echo "  2. Test with PostgreSQL: blockchain --db-type postgresql status"
echo "  3. Test with MySQL: blockchain --db-type mysql status"
echo "  4. Run full test suite: mvn test"
echo ""
echo "📖 For more information, see docs/DATABASE_CONFIGURATION.md"
