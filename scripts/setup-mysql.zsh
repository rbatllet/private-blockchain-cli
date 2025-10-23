#!/usr/bin/env zsh

# Setup MySQL database for blockchain CLI
# This script creates the database, user, and grants permissions

set -e

echo "📊 MySQL Database Setup"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Default values
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-blockchain}"
DB_USER="${DB_USER:-blockchain_user}"
DB_PASSWORD="${DB_PASSWORD:-blockchain_pass}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"

echo "ℹ️  Configuration:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

# Check if MySQL is available
if ! command -v mysql &> /dev/null; then
    echo "❌ mysql command not found. Please install MySQL client."
    exit 1
fi

# Prepare MySQL connection arguments as array
# For localhost, MySQL prefers Unix socket - don't specify host/port
if [[ "$DB_HOST" == "localhost" ]]; then
    MYSQL_ARGS=(-u root)
else
    MYSQL_ARGS=(-h "$DB_HOST" -P "$DB_PORT" -u root)
fi

if [[ -n "$MYSQL_ROOT_PASSWORD" ]]; then
    MYSQL_ARGS+=(-p"$MYSQL_ROOT_PASSWORD")
fi

# Test connection
if ! mysql "${MYSQL_ARGS[@]}" -e 'SELECT 1' &>/dev/null; then
    echo "❌ Cannot connect to MySQL server at $DB_HOST:$DB_PORT"
    echo "   Please ensure MySQL is running and root password is correct."
    echo "   Set MYSQL_ROOT_PASSWORD environment variable if needed."
    exit 1
fi

echo "✅ MySQL server is accessible"
echo ""

# Create database
echo "📝 Creating database '$DB_NAME'..."
mysql "${MYSQL_ARGS[@]}" <<EOF
CREATE DATABASE IF NOT EXISTS $DB_NAME
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
EOF

# Create user and grant privileges
echo "📝 Creating user '$DB_USER' and granting privileges..."
mysql "${MYSQL_ARGS[@]}" <<EOF
-- Create user if doesn't exist (MySQL 8.0+ syntax)
CREATE USER IF NOT EXISTS '$DB_USER'@'%' IDENTIFIED BY '$DB_PASSWORD';
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';

-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'%';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';

-- Flush privileges to ensure they take effect
FLUSH PRIVILEGES;
EOF

echo ""
echo "✅ MySQL setup completed successfully!"
echo ""
echo "📋 Connection details:"
echo "  JDBC URL: jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "🔧 Environment variables for CLI:"
echo "  export DB_TYPE=mysql"
echo "  export DB_HOST=$DB_HOST"
echo "  export DB_PORT=$DB_PORT"
echo "  export DB_NAME=$DB_NAME"
echo "  export DB_USER=$DB_USER"
echo "  export DB_PASSWORD=$DB_PASSWORD"
echo ""
echo "Or use CLI arguments:"
echo "  blockchain --db-type mysql --db-host $DB_HOST --db-name $DB_NAME status"
echo ""
echo "🧪 Test connection:"
echo "  mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME"
