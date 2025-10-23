#!/usr/bin/env zsh

# Setup PostgreSQL database for blockchain CLI
# This script creates the database, user, and grants permissions

set -e

echo "📊 PostgreSQL Database Setup"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Default values
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-blockchain}"
DB_USER="${DB_USER:-blockchain_user}"
DB_PASSWORD="${DB_PASSWORD:-blockchain_pass}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"

echo "ℹ️  Configuration:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

# Check if PostgreSQL is running
if ! command -v psql &> /dev/null; then
    echo "❌ psql command not found. Please install PostgreSQL client."
    exit 1
fi

# Test connection
if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" -c '\q' 2>/dev/null; then
    echo "❌ Cannot connect to PostgreSQL server at $DB_HOST:$DB_PORT"
    echo "   Please ensure PostgreSQL is running and accessible."
    exit 1
fi

echo "✅ PostgreSQL server is accessible"
echo ""

# Create user if doesn't exist
echo "📝 Creating user '$DB_USER'..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" <<EOF
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_user WHERE usename = '$DB_USER') THEN
        CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
        RAISE NOTICE 'User $DB_USER created';
    ELSE
        RAISE NOTICE 'User $DB_USER already exists';
    END IF;
END
\$\$;
EOF

# Create database if doesn't exist
echo "📝 Creating database '$DB_NAME'..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" <<EOF
SELECT 'CREATE DATABASE $DB_NAME'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB_NAME')\gexec
EOF

# Grant privileges
echo "🔑 Granting privileges..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$POSTGRES_USER" <<EOF
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
\c $DB_NAME
GRANT ALL ON SCHEMA public TO $DB_USER;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $DB_USER;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
EOF

echo ""
echo "✅ PostgreSQL setup completed successfully!"
echo ""
echo "📋 Connection details:"
echo "  JDBC URL: jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "🔧 Environment variables for CLI:"
echo "  export DB_TYPE=postgresql"
echo "  export DB_HOST=$DB_HOST"
echo "  export DB_PORT=$DB_PORT"
echo "  export DB_NAME=$DB_NAME"
echo "  export DB_USER=$DB_USER"
echo "  export DB_PASSWORD=$DB_PASSWORD"
echo ""
echo "Or use CLI arguments:"
echo "  blockchain --db-type postgresql --db-host $DB_HOST --db-name $DB_NAME status"
echo ""
echo "🧪 Test connection:"
echo "  psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
