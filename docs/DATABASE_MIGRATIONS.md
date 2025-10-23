# Database Migrations Guide

Complete guide to database schema migrations in Private Blockchain CLI using the integrated DatabaseMigrator from the CORE library.

## Table of Contents

- [Overview](#overview)
- [Why Use Migrations?](#why-use-migrations)
- [Quick Start](#quick-start)
- [Migration Commands](#migration-commands)
- [Creating New Migrations](#creating-new-migrations)
- [Migration Best Practices](#migration-best-practices)
- [Real-World Examples](#real-world-examples)
- [Production Workflows](#production-workflows)
- [Troubleshooting](#troubleshooting)

## Overview

The `migrate` command provides controlled database schema versioning and evolution. Unlike Hibernate's `hbm2ddl.auto=update` which automatically modifies the schema, migrations give you:

- **Full Control**: Explicitly define and review every schema change
- **Audit Trail**: Complete history of when and how the schema evolved
- **Reversibility**: Know exactly what changed and when
- **Safety**: Validate schema before deploying to production
- **Multi-Environment**: Keep development, staging, and production in sync

### 🆕 File-Based Migrations (v1.0.5+)

**IMPORTANT**: Starting from version 1.0.5, migrations are stored as **external SQL files** instead of hardcoded Java strings.

**Migration Files Location**:
- Production: `src/main/resources/db/migration/`
- Testing: `src/test/resources/db/migration/`

**Benefits**:
- ✅ Better version control and git diffs
- ✅ SQL syntax highlighting in IDEs
- ✅ Easier code review
- ✅ Separation of SQL logic from Java code

### Current State

**Version**: V1 (baseline)
**Tables**: blocks, authorized_keys, off_chain_data, search_keywords, search_categories, schema_version

**Migration Files**:
- `V1__create_initial_blockchain_schema.sql` - Baseline schema
- `V2__add_search_performance_indexes.sql` - Example (commented out by default)

## Why Use Migrations?

### Hibernate Auto-Update Limitations

Hibernate's `hbm2ddl.auto=update` is great for rapid development but has limitations:

| Operation | Hibernate Auto-Update | Migrations |
|-----------|----------------------|------------|
| Add new tables | ✅ Yes | ✅ Yes |
| Add new columns | ✅ Yes | ✅ Yes |
| Remove unused columns | ❌ No | ✅ Yes |
| Rename columns | ❌ No | ✅ Yes |
| Change column types | ❌ No | ✅ Yes |
| Add indexes | ❌ Limited | ✅ Yes |
| Complex constraints | ❌ No | ✅ Yes |
| Audit trail | ❌ No | ✅ Yes |

### When to Use Migrations

Use migrations when you need to:

1. **Remove or rename columns** - Hibernate can't do this
2. **Optimize performance** - Add custom indexes
3. **Refactor schema** - Complex structural changes
4. **Maintain compliance** - Audit trail required
5. **Production deployments** - Controlled, validated changes
6. **Multi-environment consistency** - Same schema across dev/staging/prod

## Quick Start

### Check Current Version

```bash
blockchain migrate current-version
```

**Output:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 Current Schema Version
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Current Version: V1
```

### View Migration History

```bash
blockchain migrate show-history
```

**Output:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 Migration History
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Total Migrations: 1

Version    Description                              Installed On         Exec Time       Status
────────────────────────────────────────────────────────────────────────────────────────────────────
V1         Create initial blockchain schema         2025-10-18 10:26:49  1 ms            ✅ Success
```

### Validate Schema

```bash
blockchain migrate validate
```

**Output:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Schema Validation
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ All migrations validated successfully
```

### Run Pending Migrations

```bash
blockchain migrate run
```

**Output (when up to date):**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Database Migration
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ Database is up to date
   No pending migrations
```

## Migration Commands

### current-version (alias: version)

Display the current schema version.

```bash
# Text output
blockchain migrate current-version

# JSON output
blockchain migrate current-version --json
```

**JSON Example:**
```json
{
  "currentVersion" : "V1",
  "hasMigrations" : true
}
```

### show-history (alias: history)

Display complete migration history with execution details.

```bash
# Text output
blockchain migrate show-history

# JSON output (for automation)
blockchain migrate show-history --json
```

**JSON Example:**
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

### validate

Validate that the current schema matches registered migrations.

```bash
# Text output
blockchain migrate validate

# JSON output
blockchain migrate validate --json
```

**JSON Example:**
```json
{
  "valid" : true,
  "message" : "All migrations validated successfully",
  "issues" : [ ],
  "hasIssues" : false
}
```

### run (alias: migrate)

Execute all pending migrations.

```bash
# Text output
blockchain migrate run

# JSON output
blockchain migrate run --json

# With database selection
blockchain migrate run \
  --db-type postgresql \
  --db-host localhost \
  --db-name blockchain
```

**JSON Example:**
```json
{
  "success" : true,
  "migrationsApplied" : 2,
  "appliedVersions" : [ "V2", "V3" ],
  "durationMs" : 145
}
```

## Creating New Migrations

### Migration Naming Convention

Migrations follow the pattern: `V{number}__{description}.sql`

Examples:
- `V2__add_block_categories.sql`
- `V3__add_search_indexes.sql`
- `V4__add_signature_algorithm.sql`

### Where to Add Migrations

**Migration SQL files** are stored in `src/main/resources/db/migration/` and registered in `MigrateCommand.java`:

```
src/main/resources/db/migration/
├── V1__create_initial_blockchain_schema.sql
├── V2__add_search_performance_indexes.sql
└── V3__your_new_migration.sql
```

**IMPORTANT**: Also copy migration files to `src/test/resources/db/migration/` so they're available during testing.

### Benefits of File-Based Migrations

Using external SQL files instead of hardcoded Java strings provides several advantages:

1. **✅ Better Version Control**: SQL changes are visible in git diffs
2. **✅ Easier Review**: Code reviewers can see exactly what SQL changes
3. **✅ SQL Syntax Highlighting**: IDEs provide proper SQL syntax highlighting and validation
4. **✅ Reusability**: SQL files can be used by other tools (database IDEs, migration tools)
5. **✅ Separation of Concerns**: SQL logic separated from Java application logic
6. **✅ Documentation**: SQL files can include detailed comments and headers

### Migration File Template

Use this template for new migrations:

```sql
-- =========================================================================
-- V{VERSION}: {TITLE}
-- =========================================================================
-- Description: {Detailed description of what this migration does}
-- Author:      {Your Name}
-- Date:        {Date}
-- =========================================================================
--
-- {Additional notes, impact analysis, rollback instructions, etc.}
--
-- Expected Impact:
-- - {Impact 1}
-- - {Impact 2}
-- =========================================================================

-- Your SQL statements here
{SQL_STATEMENTS}
```

### Step-by-Step: Adding a New Migration

**Example: Add performance indexes**

#### 1. Create the SQL File

Create `src/main/resources/db/migration/V2__add_search_performance_indexes.sql`:

```sql
-- =========================================================================
-- V2: Add Search Performance Indexes
-- =========================================================================
-- Description: Optimize blockchain search operations with targeted indexes
-- Author:      Your Name
-- Date:        2025-10-18
-- =========================================================================
--
-- This migration adds performance indexes for frequently searched fields:
-- - blocks.timestamp: For date-range searches
-- - blocks.previous_hash: For chain traversal
-- - blocks.hash: For hash lookups
-- - authorized_keys.fingerprint: For key searches
-- - off_chain_data.hash: For off-chain data retrieval
--
-- Expected Impact:
-- - Search by date: 10x faster
-- - Chain validation: 5x faster
-- - Hash lookups: Near-instant
-- =========================================================================

-- Index for timestamp-based searches
CREATE INDEX IF NOT EXISTS idx_blocks_timestamp ON blocks(timestamp);

-- Index for blockchain traversal via previous_hash
CREATE INDEX IF NOT EXISTS idx_blocks_previous_hash ON blocks(previous_hash);

-- Index for hash-based lookups
CREATE INDEX IF NOT EXISTS idx_blocks_hash ON blocks(hash);

-- Index for authorized key fingerprint searches
CREATE INDEX IF NOT EXISTS idx_authorized_keys_fingerprint ON authorized_keys(fingerprint);

-- Index for off-chain data hash lookups
CREATE INDEX IF NOT EXISTS idx_off_chain_data_hash ON off_chain_data(hash);

-- Index for off-chain data by block_id (foreign key optimization)
CREATE INDEX IF NOT EXISTS idx_off_chain_data_block_id ON off_chain_data(block_id);
```

#### 2. Copy to Test Resources

```bash
cp src/main/resources/db/migration/V2__add_search_performance_indexes.sql \
   src/test/resources/db/migration/
```

#### 3. Register the Migration in Code

Update `MigrateCommand.java` in the `registerMigrations()` method:

```java
private static void registerMigrations(DatabaseMigrator migrator) {
    // V1: Initial blockchain schema
    migrator.addMigration(Migration.builder()
        .version("V1")
        .description("Create initial blockchain schema")
        .sql(loadMigrationSql("V1__create_initial_blockchain_schema.sql"))
        .script("V1__create_initial_blockchain_schema.sql")
        .build());

    // V2: Add search performance indexes (uncomment to enable)
    // migrator.addMigration(Migration.builder()
    //     .version("V2")
    //     .description("Add search performance indexes")
    //     .sql(loadMigrationSql("V2__add_search_performance_indexes.sql"))
    //     .script("V2__add_search_performance_indexes.sql")
    //     .build());
}
```

**Note**: The `loadMigrationSql()` method reads the SQL content from the classpath resource and returns it as a String.

#### 4. Rebuild the Application

```bash
mvn clean package
```

#### 5. Test the Migration

```bash
# Test on H2 (development)
java -jar target/blockchain-cli.jar migrate run

# Test on PostgreSQL
DB_TYPE=postgresql DB_HOST=localhost DB_NAME=blockchain \
  java -jar target/blockchain-cli.jar migrate run
```

#### 6. Verify the Migration

```bash
# Check version
java -jar target/blockchain-cli.jar migrate current-version
# → Should show: Current Version: V2

# Check history
java -jar target/blockchain-cli.jar migrate show-history
# → Should show both V1 and V2
```

## Migration Best Practices

### 1. Always Test First

```bash
# Test on development database (H2)
blockchain migrate run

# Test on staging
DB_TYPE=postgresql DB_HOST=staging blockchain migrate run

# Only then apply to production
DB_TYPE=postgresql DB_HOST=prod blockchain migrate run
```

### 2. Make Migrations Idempotent

Use `IF NOT EXISTS` and `IF EXISTS` clauses when possible:

```sql
-- Good: Idempotent
CREATE INDEX IF NOT EXISTS idx_blocks_timestamp ON blocks(timestamp);

-- Less safe: Will fail if index exists
CREATE INDEX idx_blocks_timestamp ON blocks(timestamp);
```

### 3. Validate Before Running

```bash
# Always validate first
blockchain migrate validate

# Then run migrations
blockchain migrate run
```

### 4. Backup Before Major Changes

```bash
# PostgreSQL
pg_dump blockchain > backup_$(date +%Y%m%d_%H%M%S).sql

# MySQL
mysqldump blockchain > backup_$(date +%Y%m%d_%H%M%S).sql

# Then run migration
blockchain migrate run
```

### 5. Use Descriptive Names

```bash
# ✅ Good
V2__add_block_categories.sql
V3__optimize_search_indexes.sql
V4__add_signature_validation.sql

# ❌ Bad
V2__update.sql
V3__changes.sql
V4__fix.sql
```

### 6. Keep Migrations Small

```bash
# ✅ Good: One logical change per migration
V2__add_block_category.sql       # Just add category
V3__add_category_index.sql       # Just add index

# ❌ Bad: Too many changes in one migration
V2__add_features_and_refactor.sql  # Multiple unrelated changes
```

### 7. Test Migration Files

Always ensure migration SQL files are copied to test resources:

```bash
# After creating a new migration
cp src/main/resources/db/migration/V{N}__description.sql \
   src/test/resources/db/migration/
```

Run the migration test suite to verify:

```bash
mvn test -Dtest=MigrateCommandTest
```

The test suite ensures:
- ✅ Migration files are accessible from classpath
- ✅ SQL syntax is valid for all supported databases
- ✅ Migrations can be applied and rolled back cleanly
- ✅ Schema history tracking works correctly

## Testing Migrations

### Unit Tests

The `MigrateCommandTest` suite provides comprehensive testing:

- **Fresh Database State**: Each test starts with a clean database and empty migration history
- **Isolation**: Tests use `DatabaseMigrator.resetForTesting()` to ensure independence
- **Multiple Databases**: Tests run against H2 (in-memory) and can be configured for PostgreSQL/MySQL

### Test Structure

```java
@BeforeEach
void setUp() {
    // Configure unique database for test isolation
    String uniqueDbName = "test_migrate_" + System.currentTimeMillis();
    configManager.setCliArguments("h2", null, null, null, uniqueDbName, null, null);

    // Initialize JPAUtil
    JPAUtil.initialize(configManager.getConfig());

    // Reset migration history for clean state
    DatabaseMigrator migrator = new DatabaseMigrator(configManager.getConfig());
    migrator.resetForTesting();
}
```

### Running Tests

```bash
# Run all migration tests (H2 + SQLite)
mvn test -Dtest=MigrateCommandTest

# Run specific test
mvn test -Dtest=MigrateCommandTest#testRunMigrationWithV1AutoApplied

# Test with PostgreSQL (requires running PostgreSQL)
DB_TYPE=postgresql DB_HOST=localhost DB_PORT=5432 \
  DB_NAME=blockchain DB_USER=blockchain_user DB_PASSWORD=blockchain_pass \
  mvn test -Dtest=MigrateCommandTest#testPostgreSQLConfiguration

# Test with MySQL (requires running MySQL)
MYSQL_HOST=localhost MYSQL_PORT=3306 \
  MYSQL_DATABASE=blockchain MYSQL_USER=root MYSQL_PASSWORD=password \
  mvn test -Dtest=MigrateCommandTest#testMySQLConfiguration
```

**Supported Databases**:
- ✅ **H2** - In-memory, fast, all tests run by default
- ✅ **SQLite** - File-based, lightweight, all tests run by default
- ✅ **PostgreSQL** - Production-ready, requires env vars to run tests
- ✅ **MySQL** - Production-ready, requires env vars to run tests

**Test Count**: 22 tests (20 core tests + 2 database-specific tests)

## Real-World Examples

### Example 1: Add Block Categories

**Use Case**: Classify blocks by type (TRANSACTION, METADATA, AUDIT, etc.)

```sql
-- V2__add_block_categories.sql
ALTER TABLE blocks ADD COLUMN category VARCHAR(50) DEFAULT 'TRANSACTION';
CREATE INDEX idx_blocks_category ON blocks(category);
```

**Java Model Update:**
```java
@Entity
@Table(name = "blocks")
public class Block {
    // ... existing fields

    @Column(name = "category", length = 50)
    private String category = "TRANSACTION";

    // ... getters/setters
}
```

**Migration:**
```bash
blockchain migrate run
```

### Example 2: Add Search Performance Indexes

**Use Case**: Optimize search queries on blocks by timestamp and hash

```sql
-- V3__add_search_indexes.sql
CREATE INDEX idx_blocks_timestamp ON blocks(timestamp);
CREATE INDEX idx_blocks_previous_hash ON blocks(previous_hash);
CREATE INDEX idx_blocks_hash ON blocks(hash);
CREATE INDEX idx_off_chain_data_hash ON off_chain_data(hash);
```

**Migration:**
```bash
blockchain migrate run
```

**Verify Performance:**
```bash
# Before migration
blockchain search --keyword "test"  # Slow

# After migration
blockchain search --keyword "test"  # Fast!
```

### Example 3: Add Signature Algorithm Tracking

**Use Case**: Track which signature algorithm was used for each block

```sql
-- V4__add_signature_algorithm.sql
ALTER TABLE blocks ADD COLUMN signature_algorithm VARCHAR(50) DEFAULT 'ECDSA-secp256r1';
CREATE INDEX idx_blocks_signature_algo ON blocks(signature_algorithm);

-- Populate existing blocks
UPDATE blocks SET signature_algorithm = 'ECDSA-secp256r1' WHERE signature_algorithm IS NULL;
```

### Example 4: Add Block Metadata Table

**Use Case**: Store flexible key-value metadata for blocks

```sql
-- V5__add_block_metadata.sql
CREATE TABLE block_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    block_id BIGINT NOT NULL,
    metadata_key VARCHAR(100) NOT NULL,
    metadata_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (block_id) REFERENCES blocks(id) ON DELETE CASCADE,
    UNIQUE KEY uk_block_metadata (block_id, metadata_key)
);

CREATE INDEX idx_block_metadata_key ON block_metadata(metadata_key);
```

### Example 5: Optimize Off-Chain Storage

**Use Case**: Add indexes for faster off-chain data retrieval

```sql
-- V6__optimize_offchain_storage.sql
CREATE INDEX idx_off_chain_data_block_id ON off_chain_data(block_id);
CREATE INDEX idx_off_chain_data_created_at ON off_chain_data(created_at);

-- Add size tracking
ALTER TABLE off_chain_data ADD COLUMN size_bytes BIGINT;
UPDATE off_chain_data SET size_bytes = LENGTH(encrypted_data);
```

## Production Workflows

### Workflow 1: Continuous Integration

```bash
#!/usr/bin/env zsh
# CI/CD Pipeline: validate-and-migrate.zsh

set -e

echo "🔍 Validating database schema..."
if ! blockchain migrate validate --json > /dev/null 2>&1; then
    echo "❌ Schema validation failed"
    exit 1
fi

echo "📊 Current version:"
blockchain migrate current-version

echo "📦 Running migrations..."
if blockchain migrate run --json > /tmp/migration-result.json; then
    APPLIED=$(jq '.migrationsApplied' /tmp/migration-result.json)
    if [ "$APPLIED" -gt 0 ]; then
        echo "✅ Applied $APPLIED new migrations"
    else
        echo "✅ Database already up to date"
    fi
else
    echo "❌ Migration failed"
    exit 1
fi

echo "✅ Migration workflow completed successfully"
```

### Workflow 2: Safe Production Deployment

```bash
#!/usr/bin/env zsh
# Production deployment with safety checks

set -e

# Configuration
DB_TYPE=postgresql
DB_HOST=prod-db.example.com
DB_NAME=blockchain
DB_USER=blockchain_user
BACKUP_DIR=/backups/blockchain

echo "🏭 Production Migration Workflow"
echo "================================"

# 1. Check current state
echo "📊 Checking current version..."
CURRENT_VERSION=$(blockchain migrate current-version --json | jq -r '.currentVersion')
echo "Current version: $CURRENT_VERSION"

# 2. Create backup
echo "💾 Creating database backup..."
BACKUP_FILE="$BACKUP_DIR/blockchain_$(date +%Y%m%d_%H%M%S).sql"
pg_dump -h $DB_HOST -U $DB_USER $DB_NAME > $BACKUP_FILE
echo "Backup created: $BACKUP_FILE"

# 3. Validate schema
echo "🔍 Validating current schema..."
if ! blockchain migrate validate; then
    echo "❌ Schema validation failed - aborting"
    exit 1
fi

# 4. Show pending migrations
echo "📋 Migration history:"
blockchain migrate show-history

# 5. Dry run (show what would be applied)
echo "🔎 Checking for pending migrations..."
RESULT=$(blockchain migrate run --json)
SUCCESS=$(echo $RESULT | jq -r '.success')
APPLIED=$(echo $RESULT | jq -r '.migrationsApplied')

if [ "$SUCCESS" = "true" ]; then
    if [ "$APPLIED" -gt 0 ]; then
        echo "✅ Successfully applied $APPLIED migrations"
        VERSIONS=$(echo $RESULT | jq -r '.appliedVersions[]')
        echo "Applied versions: $VERSIONS"
    else
        echo "✅ Database already up to date"
    fi

    # 6. Verify final state
    echo "📊 Final version:"
    blockchain migrate current-version

    # 7. Cleanup old backups (keep last 10)
    echo "🧹 Cleaning up old backups..."
    ls -t $BACKUP_DIR/blockchain_*.sql | tail -n +11 | xargs rm -f

    echo "✅ Production migration completed successfully"
else
    echo "❌ Migration failed - restoring backup"
    psql -h $DB_HOST -U $DB_USER $DB_NAME < $BACKUP_FILE
    exit 1
fi
```

### Workflow 3: Multi-Environment Sync

```bash
#!/usr/bin/env zsh
# Sync schema across environments

check_version() {
    local env=$1
    local host=$2
    echo "[$env] Checking version..."
    DB_TYPE=postgresql DB_HOST=$host blockchain migrate current-version --json | jq -r '.currentVersion'
}

sync_environment() {
    local env=$1
    local host=$2
    echo ""
    echo "[$env] Syncing to latest version..."
    DB_TYPE=postgresql DB_HOST=$host blockchain migrate run --json | jq '{success, migrationsApplied, appliedVersions}'
}

echo "🌍 Multi-Environment Schema Sync"
echo "================================="

# Check versions
DEV_VERSION=$(check_version "DEV" "localhost")
STAGING_VERSION=$(check_version "STAGING" "staging-db.example.com")
PROD_VERSION=$(check_version "PROD" "prod-db.example.com")

echo ""
echo "Current Versions:"
echo "  Development: $DEV_VERSION"
echo "  Staging:     $STAGING_VERSION"
echo "  Production:  $PROD_VERSION"

# Sync if needed
if [ "$STAGING_VERSION" != "$DEV_VERSION" ]; then
    sync_environment "STAGING" "staging-db.example.com"
fi

if [ "$PROD_VERSION" != "$STAGING_VERSION" ]; then
    echo ""
    echo "⚠️  Production is behind staging!"
    echo "Run manually: DB_TYPE=postgresql DB_HOST=prod-db.example.com blockchain migrate run"
fi
```

## Troubleshooting

### Migration History is Empty

**Symptom**: `blockchain migrate show-history` shows no migrations

**Cause**: Fresh database, V1 not yet applied

**Solution**: V1 is automatically applied when you first access the database:
```bash
blockchain status  # This initializes the database
blockchain migrate show-history  # Now shows V1
```

### Schema Validation Fails

**Symptom**: `blockchain migrate validate` reports issues

**Possible Causes**:
1. Manual schema modifications
2. Missing migration registration
3. Corrupted migration history

**Solution**:
```bash
# Check what's wrong
blockchain migrate validate --json

# Review history
blockchain migrate show-history

# If needed, re-apply from backup
```

### Migration Fails During Execution

**Symptom**: Migration fails with SQL error

**Steps to Recover**:

1. Check the error message:
```bash
blockchain migrate run
```

2. Review migration SQL for syntax errors

3. Restore from backup if needed:
```bash
pg_dump blockchain > backup.sql  # Before attempting again
```

4. Fix the migration SQL and try again

### Different Versions Across Environments

**Symptom**: Development on V5, production on V3

**Solution**: Use the sync workflow above or manually sync:

```bash
# Apply missing migrations to production
DB_TYPE=postgresql DB_HOST=prod blockchain migrate run
```

### Can't Remove Old Migration

**Important**: Never remove or modify applied migrations!

If you need to undo a migration:
1. Create a new migration that reverses the changes
2. Never delete from migration history

Example:
```sql
-- V7__revert_block_categories.sql (reverses V2)
ALTER TABLE blocks DROP COLUMN category;
DROP INDEX idx_blocks_category;
```

## Additional Resources

- [Database Configuration Guide](DATABASE_CONFIGURATION.md) - Complete database setup
- [CORE Library Documentation](https://github.com/rbatllet/privateBlockchain) - DatabaseMigrator API
- [Migration Schema](DATABASE_CONFIGURATION.md#migration-storage) - schema_history table structure

## Support

For issues or questions:
- Check [Troubleshooting Guide](TROUBLESHOOTING.md)
- Review migration history: `blockchain migrate show-history`
- Validate schema: `blockchain migrate validate`
- Report issues at: https://github.com/rbatllet/privateBlockchain-cli/issues
