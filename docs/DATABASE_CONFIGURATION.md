# Database Configuration

Private Blockchain CLI supports multiple database backends with a flexible configuration system. This guide covers all database configuration options, setup procedures, and best practices.

## Table of Contents

- [Supported Databases](#supported-databases)
- [Configuration Priority](#configuration-priority)
- [Configuration Methods](#configuration-methods)
  - [CLI Arguments](#cli-arguments)
  - [Environment Variables](#environment-variables)
  - [Configuration File](#configuration-file)
- [Database-Specific Setup](#database-specific-setup)
  - [H2 (Default)](#h2-default)
  - [PostgreSQL](#postgresql)
  - [MySQL](#mysql)
  - [SQLite](#sqlite)
- [Database Command](#database-command)
  - [Show Configuration](#show-configuration)
  - [Test Connection](#test-connection)
  - [Export Configuration](#export-configuration)
- [Database Migration Command](#database-migration-command)
- [Connection Pooling](#connection-pooling)
- [Security Best Practices](#security-best-practices)
- [Troubleshooting](#troubleshooting)

## Supported Databases

The CLI supports four database backends:

| Database | Status | Use Case | Performance |
|----------|--------|----------|-------------|
| **H2** | Default | Development, Testing | Excellent |
| **PostgreSQL** | Recommended | Production | Excellent |
| **MySQL** | Supported | Production | Very Good |
| **SQLite** | Supported | Embedded, Single-user | Good |

### H2 (Default)

- **Type**: Embedded, in-memory or file-based
- **Best for**: Development, testing, quick demos
- **Advantages**: No setup required, fast, zero configuration
- **Limitations**: Not recommended for production with high concurrency

### PostgreSQL

- **Type**: Client-server
- **Best for**: Production deployments, high concurrency
- **Advantages**: ACID compliance, excellent performance, advanced features
- **Requirements**: PostgreSQL server 12+ installed and running

### MySQL

- **Type**: Client-server
- **Best for**: Production deployments, web applications
- **Advantages**: Wide adoption, good performance, mature ecosystem
- **Requirements**: MySQL 8.0+ or MariaDB 10.5+ installed and running

### SQLite

- **Type**: Embedded, file-based
- **Best for**: Single-user applications, embedded systems
- **Advantages**: Zero configuration, portable, single file
- **Limitations**: Limited concurrency, not for multi-user production

## Configuration Priority

The CLI uses a priority-based configuration system (highest to lowest):

1. **CLI Arguments** (`--db-type`, `--db-host`, etc.)
2. **Environment Variables** (`DB_TYPE`, `DB_HOST`, etc.)
3. **Configuration File** (`~/.blockchain-cli/database.properties`)
4. **Default Values** (H2 embedded database)

Higher priority sources override lower priority sources. For example, CLI arguments override environment variables.

## Configuration Methods

### CLI Arguments

Pass database configuration directly as command-line arguments:

```bash
# PostgreSQL
blockchain --db-type postgresql \
           --db-host localhost \
           --db-port 5432 \
           --db-name blockchain \
           --db-user myuser \
           --db-password mypass \
           status

# MySQL
blockchain --db-type mysql \
           --db-host db.example.com \
           --db-port 3306 \
           --db-name blockchain \
           --db-user blockchain_user \
           --db-password secret123 \
           status

# SQLite
blockchain --db-type sqlite status

# H2 (default)
blockchain status
```

**Available CLI Arguments:**

| Argument | Description | Example |
|----------|-------------|---------|
| `--db-type` | Database type (h2, postgresql, mysql, sqlite) | `--db-type postgresql` |
| `--db-url` | Complete JDBC URL (overrides other settings) | `--db-url jdbc:postgresql://host/db` |
| `--db-host` | Database server hostname | `--db-host localhost` |
| `--db-port` | Database server port | `--db-port 5432` |
| `--db-name` | Database name | `--db-name blockchain` |
| `--db-user` | Database username | `--db-user myuser` |
| `--db-password` | Database password (not recommended) | `--db-password secret` |

⚠️ **Security Warning**: Using `--db-password` is insecure as passwords are visible in process lists and shell history. Use environment variables instead.

### Environment Variables

Set database configuration via environment variables:

```bash
# PostgreSQL
export DB_TYPE=postgresql
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=blockchain
export DB_USER=blockchain_user
export DB_PASSWORD=blockchain_pass

# Run CLI
blockchain status

# MySQL
export DB_TYPE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=blockchain
export DB_USER=blockchain_user
export DB_PASSWORD=blockchain_pass

blockchain status

# SQLite
export DB_TYPE=sqlite

blockchain status
```

**Available Environment Variables:**

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_TYPE` | Database type | `h2` |
| `DB_URL` | Complete JDBC URL | (generated) |
| `DB_HOST` | Database hostname | `localhost` |
| `DB_PORT` | Database port | `5432` (PostgreSQL), `3306` (MySQL) |
| `DB_NAME` | Database name | `blockchain` |
| `DB_USER` | Database username | (none) |
| `DB_PASSWORD` | Database password | (none) |

### Configuration File

Create a persistent configuration file at `~/.blockchain-cli/database.properties`:

```bash
# Create configuration directory
mkdir -p ~/.blockchain-cli

# Edit configuration file
nano ~/.blockchain-cli/database.properties
```

#### PostgreSQL Configuration File

```properties
# Database type
db.type=postgresql

# Option 1: Generic properties (recommended)
db.url=jdbc:postgresql://localhost:5432/blockchain
db.user=blockchain_user
db.password=blockchain_pass

# Option 2: Specific properties
db.postgresql.host=localhost
db.postgresql.port=5432
db.postgresql.database=blockchain
db.postgresql.username=blockchain_user
db.postgresql.password=blockchain_pass

# Connection pool settings
db.connection.pool.min.size=10
db.connection.pool.max.size=60
db.connection.timeout=30000
db.idle.timeout=600000
db.max.lifetime=1800000

# Hibernate settings
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
```

#### MySQL Configuration File

```properties
# Database type
db.type=mysql

# Option 1: Generic properties
db.url=jdbc:mysql://localhost:3306/blockchain?useSSL=true
db.user=blockchain_user
db.password=blockchain_pass

# Option 2: Specific properties
db.mysql.host=localhost
db.mysql.port=3306
db.mysql.database=blockchain
db.mysql.username=blockchain_user
db.mysql.password=blockchain_pass

# Connection pool settings
db.connection.pool.min.size=10
db.connection.pool.max.size=50
db.connection.timeout=30000

# Hibernate settings
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
```

#### SQLite Configuration File

```properties
# Database type
db.type=sqlite

# SQLite file path (optional)
db.sqlite.path=./blockchain.db

# Connection pool settings
db.connection.pool.min.size=1
db.connection.pool.max.size=5

# Hibernate settings
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
```

#### H2 Configuration File

```properties
# Database type
db.type=h2

# H2 file path (optional, defaults to memory)
db.h2.path=./blockchain

# Connection pool settings
db.connection.pool.min.size=5
db.connection.pool.max.size=20

# Hibernate settings
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
```

## Database-Specific Setup

### H2 (Default)

H2 requires no setup - it's embedded and automatically configured:

```bash
# Just run the CLI - H2 is used by default
blockchain status
```

**File Storage**: By default, H2 uses an in-memory database. For persistent storage, configure a file path:

```bash
export DB_TYPE=h2
export DB_URL="jdbc:h2:./blockchain"
blockchain status
```

### PostgreSQL

#### Prerequisites

```bash
# macOS (Homebrew)
brew install postgresql@18
brew services start postgresql@18

# Ubuntu/Debian
sudo apt-get install postgresql-18
sudo systemctl start postgresql

# Verify installation
psql --version
```

#### Database Setup

Use the provided setup script:

```bash
# Default configuration (localhost, port 5432, database: blockchain)
./scripts/setup-postgresql.zsh

# Custom configuration
DB_HOST=db.example.com \
DB_PORT=5433 \
DB_NAME=myblockchain \
DB_USER=myuser \
DB_PASSWORD=mypass \
./scripts/setup-postgresql.zsh
```

The script will:
1. Create the database with UTF-8 encoding
2. Create the user with secure password
3. Grant all necessary privileges
4. Display connection details

#### Manual Setup

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE blockchain
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';

-- Create user
CREATE USER blockchain_user WITH PASSWORD 'blockchain_pass';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE blockchain TO blockchain_user;

-- Connect to blockchain database
\c blockchain

-- Grant schema privileges (PostgreSQL 15+)
GRANT ALL ON SCHEMA public TO blockchain_user;
GRANT ALL ON ALL TABLES IN SCHEMA public TO blockchain_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO blockchain_user;

-- Exit
\q
```

#### Test Connection

```bash
# Using environment variables
export DB_TYPE=postgresql
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=blockchain
export DB_USER=blockchain_user
export DB_PASSWORD=blockchain_pass

blockchain database test

# Or using CLI arguments
blockchain database test \
  --db-type postgresql \
  --db-host localhost \
  --db-port 5432 \
  --db-name blockchain \
  --db-user blockchain_user \
  --db-password blockchain_pass
```

### MySQL

#### Prerequisites

```bash
# macOS (Homebrew)
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt-get install mysql-server
sudo systemctl start mysql

# Verify installation
mysql --version
```

#### Database Setup

Use the provided setup script:

```bash
# Default configuration (localhost, port 3306, database: blockchain)
./scripts/setup-mysql.zsh

# With root password
MYSQL_ROOT_PASSWORD=rootpass ./scripts/setup-mysql.zsh

# Custom configuration
DB_HOST=db.example.com \
DB_PORT=3307 \
DB_NAME=myblockchain \
DB_USER=myuser \
DB_PASSWORD=mypass \
MYSQL_ROOT_PASSWORD=rootpass \
./scripts/setup-mysql.zsh
```

#### Manual Setup

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE IF NOT EXISTS blockchain
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER IF NOT EXISTS 'blockchain_user'@'%' IDENTIFIED BY 'blockchain_pass';
CREATE USER IF NOT EXISTS 'blockchain_user'@'localhost' IDENTIFIED BY 'blockchain_pass';

-- Grant privileges
GRANT ALL PRIVILEGES ON blockchain.* TO 'blockchain_user'@'%';
GRANT ALL PRIVILEGES ON blockchain.* TO 'blockchain_user'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;

-- Exit
EXIT;
```

#### Test Connection

```bash
# Using environment variables
export DB_TYPE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=blockchain
export DB_USER=blockchain_user
export DB_PASSWORD=blockchain_pass

blockchain database test

# Or using CLI arguments
blockchain database test \
  --db-type mysql \
  --db-host localhost \
  --db-port 3306 \
  --db-name blockchain \
  --db-user blockchain_user \
  --db-password blockchain_pass
```

### SQLite

SQLite requires no server setup - just specify the database file path:

```bash
# Default SQLite configuration
export DB_TYPE=sqlite
blockchain status

# Custom database file path
export DB_TYPE=sqlite
export DB_URL="jdbc:sqlite:/path/to/blockchain.db"
blockchain status

# Or via CLI arguments
blockchain --db-type sqlite status
blockchain --db-type sqlite --db-url "jdbc:sqlite:/path/to/blockchain.db" status
```

## Database Command

The `database` command provides utilities for managing and testing database configuration.

### Show Configuration

Display current database configuration:

```bash
# Text output
blockchain database show

# JSON output (for automation)
blockchain database show --json
```

**Example Output:**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 Database Configuration
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Database Type: POSTGRESQL
JDBC URL:      jdbc:postgresql://localhost:5432/blockchain

Connection Pool:
  Min Size:           10
  Max Size:           60
  Connection Timeout: 30000 ms
  Idle Timeout:       600000 ms
  Max Lifetime:       1800000 ms

Hibernate:
  hbm2ddl.auto: update
  show_sql:     false

Configuration Source:
  Priority order: CLI args > Environment > Config file > Default
  Config file:    /Users/user/.blockchain-cli/database.properties
```

### Test Connection

Test database connectivity with comprehensive validation using the CORE DatabaseConnectionTester:

```bash
# Test current configuration
blockchain database test

# Test specific database
blockchain database test \
  --db-type postgresql \
  --db-host localhost \
  --db-user myuser \
  --db-password mypass

# JSON output (for automation)
blockchain database test --json
```

#### Enhanced Testing Features

The `database test` command uses the **CORE DatabaseConnectionTester** to perform comprehensive validation:

1. **JDBC Driver Test**: Verifies driver availability and registration
2. **Network Connectivity**: Tests TCP connection to database server
3. **Authentication**: Validates credentials and permissions
4. **Database Accessibility**: Confirms database exists and is accessible
5. **Read Permissions**: Tests SELECT query execution
6. **Database Version Detection**: Retrieves and displays database version
7. **Response Time Measurement**: Monitors connection performance

**Example Output (Text Format):**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Database Connection Test (Enhanced)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Database: POSTGRESQL

Status: ✅ Success

Response Time: 10 ms

Connection Details:
  Database Version: PostgreSQL 18.0 on aarch64-apple-darwin, compiled by Apple clang version 16.0.0
  Driver Version:   PostgreSQL JDBC Driver 42.7.4
  Can Read:         ✅ Yes
  Read-Only Mode:   ✅ No

✅ All validations passed - database is ready for use
```

**Example Output (JSON Format):**

```json
{
  "successful" : true,
  "responseTimeMs" : 10,
  "databaseType" : "POSTGRESQL",
  "databaseVersion" : "PostgreSQL 18.0 on aarch64-apple-darwin",
  "driverVersion" : "PostgreSQL JDBC Driver 42.7.4",
  "canRead" : true,
  "readOnly" : false
}
```

#### Performance Monitoring

The test command includes automatic performance warnings:

```
Response Time: 1250 ms ⚠️ SLOW
```

If response time exceeds 1000ms, a `SLOW` warning is displayed, helping identify performance issues early.

### Export Configuration

Export database configuration to various file formats for sharing, documentation, or backup purposes. The `database export` command supports three formats and automatically detects format from file extension.

```bash
# Export to properties format (auto-detected from extension)
blockchain database export --file database.properties

# Export to JSON format
blockchain database export --file database.json

# Export to environment variables format
blockchain database export --file database.env

# Export without masking passwords (use with caution!)
blockchain database export --file database.properties --no-mask

# Explicit format specification
blockchain database export --file config.txt --format JSON
```

#### Supported Formats

The export command supports three formats:

**1. Java Properties Format (.properties)**

Best for: Configuration files, Java-based projects, version control (with passwords masked)

```properties
# Database Configuration
# Generated: 2025-10-23T10:02:45.123456Z

# Database Type
db.type=postgresql

# PostgreSQL Configuration
db.postgresql.host=localhost
db.postgresql.port=5432
db.postgresql.database=blockchain
db.postgresql.username=blockchain_user
db.postgresql.password=********
```

Copy to `~/.blockchain-cli/database.properties` to use as default configuration:

```bash
blockchain database export --file ~/.blockchain-cli/database.properties
blockchain status  # Uses exported configuration
```

**2. JSON Format (.json)**

Best for: Automation, configuration management, structured data processing

```json
{
  "type" : "postgresql",
  "host" : "localhost",
  "port" : 5432,
  "database" : "blockchain",
  "username" : "blockchain_user",
  "password" : "********"
}
```

Useful in CI/CD pipelines and infrastructure-as-code tools:

```bash
blockchain database export --file config.json
# Process with jq, python, or other JSON tools
cat config.json | jq '.host'
```

**3. Environment Variables Format (.env)**

Best for: Docker, shell scripts, environment-based configuration

```env
# Database Environment Variables
# Generated: 2025-10-23T10:02:45.123456Z

DB_TYPE=postgresql
DB_HOST=localhost
DB_PORT=5432
DB_DATABASE=blockchain
DB_USERNAME=blockchain_user
DB_PASSWORD=********
```

Source the file in your shell scripts:

```bash
blockchain database export --file database.env
source database.env
blockchain status  # Uses sourced configuration
```

Or in Docker:

```dockerfile
COPY database.env /app/
RUN set -a && source /app/database.env && set +a
```

#### Format Auto-Detection

The export command automatically detects format from file extension:

| Extension | Format | Detected As |
|-----------|--------|-------------|
| `.properties` | Java Properties | PROPERTIES |
| `.json` | JSON | JSON |
| `.env` | Environment Variables | ENV |

```bash
# Automatic detection - no --format needed
blockchain database export --file myconfig.properties  # → Properties
blockchain database export --file myconfig.json        # → JSON
blockchain database export --file myconfig.env         # → ENV
```

#### Explicit Format Specification

Override auto-detection with `--format` flag:

```bash
# Force JSON format despite .txt extension
blockchain database export --file config.txt --format JSON

# Force Properties format
blockchain database export --file config.txt --format PROPERTIES

# Force ENV format
blockchain database export --file config.txt --format ENV
```

#### Password Masking

By default, all passwords and sensitive data are masked with asterisks for security:

```bash
# Default: passwords masked
blockchain database export --file config.properties
# Result: db.postgresql.password=********

# Export without masking (use with extreme caution!)
blockchain database export --file config.properties --no-mask
# Result: db.postgresql.password=secretpassword
```

⚠️ **Security Warning**: Use `--no-mask` only for secure locations like CI/CD vault systems. Never commit unmasked passwords to version control.

#### Usage Examples

**Example 1: Share Configuration (Masked)**

Export configuration safely for sharing with team members:

```bash
# Export with automatic password masking
blockchain database export --file team-config.properties

# Share file - passwords are masked
cat team-config.properties
# Shows: db.postgresql.password=********
```

**Example 2: CI/CD Integration**

Use JSON format for automation:

```bash
#!/usr/bin/env zsh

# Export current config
blockchain database export --file config.json

# Load into CI/CD system (example with Ansible)
ansible-vault encrypt config.json
git add config.json.vault
```

**Example 3: Docker Environment**

Export to .env for Docker:

```bash
# Export configuration
blockchain database export --file app.env

# In Docker container
docker run -v ./app.env:/app/.env myapp
```

**Example 4: Backup Configuration**

Create timestamped backups:

```bash
#!/usr/bin/env zsh

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
blockchain database export --file "database_${TIMESTAMP}.properties"

# Creates: database_20251023_100245.properties
```

#### Exit Codes

The export command returns:

- **0**: Export successful
- **1**: Error (invalid format, file permission issue, etc.)

Use in scripts for validation:

```bash
if blockchain database export --file config.json; then
    echo "✅ Configuration exported successfully"
    jq . config.json
else
    echo "❌ Export failed"
    exit 1
fi
```

#### Error Handling

Common errors and solutions:

**Invalid File Path**
```
❌ Failed to export configuration: Invalid file path
```
Ensure directory exists and you have write permissions.

**Invalid Format Option**
```
❌ Invalid format: YAML
   Supported formats: PROPERTIES, JSON, ENV
```
Use only PROPERTIES, JSON, or ENV.

**Unrecognizable Extension**
```
❌ Cannot determine format from file extension: .txt
   Specify format explicitly with --format
```
Use `--format` flag or change file extension.

#### Error Diagnosis

When a connection test fails, detailed error information and recommendations are provided:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Database Connection Test (Enhanced)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Database: POSTGRESQL

Status: ❌ Failed

Response Time: 52 ms

Error Details:
  Connection refused - ensure database server is running

💡 Recommendations:
  → Verify PostgreSQL service is running: brew services list | grep postgresql
  → Check if port 5432 is accessible: nc -zv localhost 5432
  → Review PostgreSQL logs for startup errors
  → Ensure database server is configured to accept connections

❌ Connection test failed - please check configuration
```

#### Security Warnings

The test command includes intelligent security recommendations:

```
💡 Recommendations:
  → Consider enabling SSL/TLS encryption for production databases
  → Use encrypted connections: jdbc:postgresql://host:5432/db?ssl=true&sslmode=require
```

#### Test Command Validations

The enhanced test verifies:

| Validation | Description |
|------------|-------------|
| **JDBC Driver** | Driver class is available and can be loaded |
| **Network Connectivity** | TCP connection to database server succeeds |
| **Authentication** | Username and password are valid |
| **Database Accessibility** | Database exists and user has access |
| **Read Permissions** | User can execute SELECT queries |
| **Database Version** | Retrieves and displays version information |
| **Response Time** | Measures connection performance (warns if >1000ms) |

#### Exit Codes

The test command returns proper exit codes for automation:

- **0**: All validations passed successfully
- **1**: One or more validations failed

Example automation usage:

```bash
#!/usr/bin/env zsh

if blockchain database test --json > /dev/null 2>&1; then
    echo "✅ Database is ready"
    blockchain status
else
    echo "❌ Database connection failed"
    exit 1
fi
```

## Database Migration Command

The `migrate` command provides schema versioning and migration management using the **CORE DatabaseMigrator**. It enables controlled database schema evolution with full audit trail and validation.

### Overview

Database migrations allow you to:
- Track schema version history
- Apply schema changes in a controlled manner
- Validate schema integrity
- Maintain full audit trail of all schema modifications
- Support multiple database backends consistently

**Important**: When you first connect to a database, the V1 baseline migration is automatically applied by Hibernate's `hbm2ddl.auto=update` feature. This creates the initial blockchain tables (blocks, authorized_keys, off_chain_data, etc.) and registers the V1 migration as a baseline.

### Subcommands

The `migrate` command provides four subcommands:

| Subcommand | Aliases | Description |
|------------|---------|-------------|
| `run` | `migrate` | Apply pending migrations |
| `show-history` | `history` | Display migration history |
| `validate` | - | Validate schema integrity |
| `current-version` | `version` | Show current schema version |

### Run Migrations

Apply all pending migrations to the database:

```bash
# Run pending migrations
blockchain migrate run

# Or use the alias
blockchain migrate

# JSON output (for automation)
blockchain migrate run --json

# With database selection
blockchain migrate run \
  --db-type postgresql \
  --db-host localhost \
  --db-name blockchain
```

**Example Output (Text Format):**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Database Migration
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ Database is up to date
   No pending migrations
```

**Example Output (JSON Format):**

```json
{
  "success" : true,
  "migrationsApplied" : 0,
  "appliedVersions" : [ ],
  "durationMs" : 12
}
```

### Show Migration History

Display all applied migrations with detailed information:

```bash
# Show migration history
blockchain migrate show-history

# Or use the alias
blockchain migrate history

# JSON output
blockchain migrate show-history --json
```

**Example Output (Text Format):**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 Migration History
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Total Migrations: 1

Version    Description                              Installed On         Exec Time       Status
────────────────────────────────────────────────────────────────────────────────────────────────────
V1         Create initial blockchain schema         2025-10-18 10:26:49  1 ms            ✅ Success
```

**Example Output (JSON Format):**

```json
[ {
  "version" : "V1",
  "description" : "Create initial blockchain schema",
  "type" : "SQL",
  "installedBy" : "user",
  "installedOn" : "2025-10-18T08:26:49.789896Z",
  "executionTime" : 1,
  "success" : true,
  "state" : "Success"
} ]
```

### Validate Schema

Validate that the current schema matches the registered migrations:

```bash
# Validate schema
blockchain migrate validate

# JSON output
blockchain migrate validate --json
```

**Example Output (Text Format):**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Schema Validation
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ All migrations validated successfully
```

**Example Output (JSON Format):**

```json
{
  "valid" : true,
  "message" : "All migrations validated successfully",
  "issues" : [ ],
  "hasIssues" : false
}
```

### Current Schema Version

Display the currently applied schema version:

```bash
# Show current version
blockchain migrate current-version

# Or use the alias
blockchain migrate version

# JSON output
blockchain migrate current-version --json
```

**Example Output (Text Format):**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 Current Schema Version
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Current Version: V1
```

**Example Output (JSON Format):**

```json
{
  "currentVersion" : "V1",
  "hasMigrations" : true
}
```

### Migration Workflow

Typical migration workflow for production deployments:

```bash
# 1. Check current version
blockchain migrate current-version

# 2. Validate current schema
blockchain migrate validate

# 3. Show migration history
blockchain migrate show-history

# 4. Run pending migrations (if any)
blockchain migrate run

# 5. Verify new version
blockchain migrate current-version
```

### Automation Example

Using migrations in CI/CD pipelines:

```bash
#!/usr/bin/env zsh

# Database credentials from environment
export DB_TYPE=postgresql
export DB_HOST=db.example.com
export DB_NAME=blockchain
export DB_USER=blockchain_user
export DB_PASSWORD="${DB_PASSWORD}"

# Validate before deploying
echo "🔍 Validating database schema..."
if ! blockchain migrate validate --json > /dev/null 2>&1; then
    echo "❌ Schema validation failed"
    exit 1
fi

# Run migrations
echo "📦 Running database migrations..."
if blockchain migrate run --json > /tmp/migration-result.json; then
    APPLIED=$(jq '.migrationsApplied' /tmp/migration-result.json)
    echo "✅ Applied $APPLIED migrations"
else
    echo "❌ Migration failed"
    exit 1
fi

# Verify final state
echo "📊 Current schema version:"
blockchain migrate current-version
```

### Migration Exit Codes

The migrate command follows standard exit code conventions:

- **0**: Operation successful
- **1**: Operation failed (invalid schema, migration error, etc.)

### Best Practices

1. **Always Validate First**: Run `blockchain migrate validate` before applying new migrations
2. **Review History**: Check `blockchain migrate show-history` to understand current state
3. **Use JSON for Automation**: JSON output provides structured data for scripts
4. **Test Migrations**: Always test migrations in development environment first
5. **Backup Before Migrating**: Create database backup before running migrations in production

### Architecture Notes

#### Automatic V1 Application

When JPAUtil initializes a new database:
1. Hibernate's `hbm2ddl.auto=update` creates initial tables
2. DatabaseMigrator detects the new schema
3. V1 baseline migration is automatically registered
4. Future migrations can build upon this baseline

This ensures all databases have a tracked baseline, even when created with Hibernate auto-generation.

#### Migration Storage

Migration history is stored in the `schema_history` table (created automatically):

```sql
CREATE TABLE schema_history (
    installed_rank INTEGER PRIMARY KEY,
    version VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INTEGER,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL,
    execution_time INTEGER NOT NULL,
    success BOOLEAN NOT NULL
);
```

#### Supported Databases

All migration features work consistently across:
- H2 (embedded)
- PostgreSQL 12+
- MySQL 8.0+
- SQLite 3+

## Connection Pooling

Connection pooling optimizes database performance by reusing connections. Default settings vary by database type:

### PostgreSQL

```properties
db.connection.pool.min.size=10
db.connection.pool.max.size=60
db.connection.timeout=30000      # 30 seconds
db.idle.timeout=600000           # 10 minutes
db.max.lifetime=1800000          # 30 minutes
```

### MySQL

```properties
db.connection.pool.min.size=10
db.connection.pool.max.size=50
db.connection.timeout=30000
db.idle.timeout=600000
db.max.lifetime=1800000
```

### SQLite

```properties
db.connection.pool.min.size=1
db.connection.pool.max.size=5
db.connection.timeout=30000
db.idle.timeout=600000
db.max.lifetime=1800000
```

### H2

```properties
db.connection.pool.min.size=5
db.connection.pool.max.size=20
db.connection.timeout=30000
db.idle.timeout=600000
db.max.lifetime=1800000
```

### Tuning Guidelines

- **Min Size**: Number of connections to maintain in the pool
- **Max Size**: Maximum number of concurrent connections
- **Connection Timeout**: Maximum time to wait for a connection from the pool
- **Idle Timeout**: Time before idle connections are removed
- **Max Lifetime**: Maximum lifetime of a connection before forced renewal

**Recommendations:**
- Production PostgreSQL/MySQL: 10-50 connections for typical workloads
- High concurrency: Increase max size but monitor database server capacity
- Low concurrency: Reduce max size to conserve resources
- SQLite: Keep max size low (1-5) due to write serialization

## Security Best Practices

### Password Management

❌ **Never use CLI arguments for passwords in production:**

```bash
# INSECURE - visible in process list and shell history
blockchain --db-password secret123 status
```

✅ **Use environment variables instead:**

```bash
# Secure - not visible in process list
export DB_PASSWORD='secret123'
blockchain status
```

✅ **Or use configuration file with restricted permissions:**

```bash
# Create secure configuration
cat > ~/.blockchain-cli/database.properties <<EOF
db.type=postgresql
db.url=jdbc:postgresql://localhost:5432/blockchain
db.user=blockchain_user
db.password=secret123
EOF

# Restrict file permissions
chmod 600 ~/.blockchain-cli/database.properties

blockchain status
```

### SSL/TLS Encryption

Enable SSL for database connections in production:

#### PostgreSQL SSL

```bash
# Via JDBC URL
export DB_URL="jdbc:postgresql://localhost:5432/blockchain?ssl=true&sslmode=require"

# Or in properties file
db.url=jdbc:postgresql://localhost:5432/blockchain?ssl=true&sslmode=require
```

#### MySQL SSL

```bash
# Via JDBC URL
export DB_URL="jdbc:mysql://localhost:3306/blockchain?useSSL=true&requireSSL=true"

# Or in properties file
db.url=jdbc:mysql://localhost:3306/blockchain?useSSL=true&requireSSL=true
```

### Network Security

- **Firewall**: Restrict database port access to trusted hosts
- **Bind Address**: Configure database server to listen only on specific interfaces
- **VPN/SSH Tunnel**: Use encrypted tunnels for remote database access

### User Privileges

Grant only necessary privileges:

```sql
-- PostgreSQL: Minimal privileges
GRANT CONNECT ON DATABASE blockchain TO blockchain_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO blockchain_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO blockchain_user;

-- MySQL: Minimal privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON blockchain.* TO 'blockchain_user'@'localhost';
```

## Troubleshooting

### Connection Refused

**Problem**: Cannot connect to database server

```
❌ Error: Connection refused
```

**Solutions:**

1. Verify database server is running:
   ```bash
   # PostgreSQL
   brew services list | grep postgresql
   sudo systemctl status postgresql

   # MySQL
   brew services list | grep mysql
   sudo systemctl status mysql
   ```

2. Check if server is listening on correct port:
   ```bash
   # PostgreSQL (default port 5432)
   lsof -i :5432
   netstat -an | grep 5432

   # MySQL (default port 3306)
   lsof -i :3306
   netstat -an | grep 3306
   ```

3. Verify host and port configuration:
   ```bash
   blockchain database show
   ```

### Authentication Failed

**Problem**: Invalid credentials

```
❌ Error: Authentication failed for user 'blockchain_user'
```

**Solutions:**

1. Verify credentials in configuration:
   ```bash
   blockchain database show
   ```

2. Test database login directly:
   ```bash
   # PostgreSQL
   psql -h localhost -U blockchain_user -d blockchain

   # MySQL
   mysql -h localhost -u blockchain_user -p blockchain
   ```

3. Reset password if needed:
   ```sql
   -- PostgreSQL
   ALTER USER blockchain_user WITH PASSWORD 'newpassword';

   -- MySQL
   ALTER USER 'blockchain_user'@'localhost' IDENTIFIED BY 'newpassword';
   ```

### Database Does Not Exist

**Problem**: Database not found

```
❌ Error: database "blockchain" does not exist
```

**Solutions:**

1. Create database using setup script:
   ```bash
   # PostgreSQL
   ./scripts/setup-postgresql.zsh

   # MySQL
   ./scripts/setup-mysql.zsh
   ```

2. Or create manually (see [Database-Specific Setup](#database-specific-setup))

### Permission Denied

**Problem**: User lacks necessary privileges

```
❌ Error: permission denied for table blocks
```

**Solutions:**

1. Grant necessary privileges:
   ```sql
   -- PostgreSQL
   GRANT ALL PRIVILEGES ON DATABASE blockchain TO blockchain_user;
   \c blockchain
   GRANT ALL ON SCHEMA public TO blockchain_user;
   GRANT ALL ON ALL TABLES IN SCHEMA public TO blockchain_user;
   GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO blockchain_user;

   -- MySQL
   GRANT ALL PRIVILEGES ON blockchain.* TO 'blockchain_user'@'%';
   FLUSH PRIVILEGES;
   ```

### JPA/Hibernate Initialization Failed

**Problem**: ORM initialization errors

```
❌ Error: JPA/Hibernate initialization failed
```

**Solutions:**

1. Verify Hibernate settings in configuration:
   ```properties
   hibernate.hbm2ddl.auto=update
   hibernate.show_sql=false
   ```

2. Enable SQL logging for debugging:
   ```properties
   hibernate.show_sql=true
   ```

3. Check if database user has schema modification privileges (for `hbm2ddl.auto=update`)

### Port Already in Use

**Problem**: Database port conflict

```
❌ Error: Address already in use
```

**Solutions:**

1. Find process using the port:
   ```bash
   # PostgreSQL port 5432
   lsof -i :5432

   # MySQL port 3306
   lsof -i :3306
   ```

2. Stop conflicting service or use different port:
   ```bash
   export DB_PORT=5433
   blockchain status
   ```

### Configuration File Issues

**Problem**: Configuration file not loaded or has errors

```
❌ Error: Invalid property value
```

**Solutions:**

1. Verify file location and permissions:
   ```bash
   ls -la ~/.blockchain-cli/database.properties
   ```

2. Check file syntax (no spaces around `=`):
   ```properties
   # Correct
   db.type=postgresql

   # Incorrect
   db.type = postgresql
   ```

3. Test with environment variables to bypass file:
   ```bash
   export DB_TYPE=postgresql
   export DB_HOST=localhost
   blockchain database test
   ```

### Performance Issues

**Problem**: Slow database operations

**Solutions:**

1. Increase connection pool size:
   ```properties
   db.connection.pool.min.size=20
   db.connection.pool.max.size=100
   ```

2. Reduce connection timeout:
   ```properties
   db.connection.timeout=10000
   ```

3. Monitor connection pool usage and adjust accordingly

4. For PostgreSQL, analyze query performance:
   ```sql
   EXPLAIN ANALYZE SELECT * FROM blocks;
   ```

## Production Deployment Guide

For production deployments, refer to the comprehensive migration strategy and best practices in the **CORE library**:
- **[DATABASE_MIGRATION_STRATEGY.md](https://github.com/rbatllet/privateBlockchain/docs/database/DATABASE_MIGRATION_STRATEGY.md)** - Detailed guidance on schema management for production

Key points:
- Use `blockchain database migrate run` before deploying
- Use `blockchain database migrate validate` to check schema
- Refer to migration history: `blockchain database migrate show-history`

## Additional Resources

- [README.md](../README.md) - General CLI documentation
- [EXAMPLES.md](EXAMPLES.md) - Usage examples
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - General troubleshooting
- [DATABASE_MIGRATIONS.md](DATABASE_MIGRATIONS.md) - CLI migration command reference
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- MySQL Documentation: https://dev.mysql.com/doc/
- H2 Database Documentation: https://www.h2database.com/
- SQLite Documentation: https://www.sqlite.org/docs.html

## Support

For issues not covered in this guide:

1. Check verbose output: `blockchain --verbose database test`
2. Review log files in `~/.blockchain-cli/logs/`
3. Report issues: https://github.com/rbatllet/privateBlockchain-cli/issues
