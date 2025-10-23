package com.rbatllet.blockchain.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
import com.rbatllet.blockchain.config.DatabaseConfig;
import com.rbatllet.blockchain.config.util.DatabaseMigrator;
import com.rbatllet.blockchain.config.util.DatabaseMigrator.Migration;
import com.rbatllet.blockchain.config.util.DatabaseMigrator.MigrationHistoryEntry;
import com.rbatllet.blockchain.config.util.DatabaseMigrator.MigrationResult;
import com.rbatllet.blockchain.config.util.DatabaseMigrator.ValidationResult;
import com.rbatllet.blockchain.util.ExitUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Database migration command with subcommands for schema versioning and management.
 *
 * <p>This command provides comprehensive database migration capabilities:</p>
 * <ul>
 *   <li><b>migrate</b>: Run pending migrations</li>
 *   <li><b>show-history</b>: Display migration history</li>
 *   <li><b>validate</b>: Validate current schema state</li>
 *   <li><b>current-version</b>: Show current schema version</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>
 * # Run all pending migrations
 * blockchain migrate
 *
 * # Show migration history
 * blockchain migrate show-history
 *
 * # Validate current schema
 * blockchain migrate validate
 *
 * # Show current version
 * blockchain migrate current-version
 *
 * # JSON output (for automation)
 * blockchain migrate --json
 * blockchain migrate show-history --json
 * </pre>
 *
 * @since 1.0.5
 */
@Command(
    name = "migrate",
    description = "Database schema migration management",
    subcommands = {
        MigrateCommand.RunCommand.class,
        MigrateCommand.ShowHistoryCommand.class,
        MigrateCommand.ValidateCommand.class,
        MigrateCommand.CurrentVersionCommand.class
    }
)
public class MigrateCommand implements Callable<Integer> {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public Integer call() {
        System.err.println("❌ Please specify a subcommand: migrate, show-history, validate, current-version");
        System.err.println("Use 'blockchain migrate --help' for more information");
        ExitUtil.exit(1);
        return 1;
    }

    /**
     * Run pending database migrations.
     */
    @Command(
        name = "run",
        aliases = {"migrate"},
        description = "Run all pending database migrations"
    )
    public static class RunCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                // Create migrator and register migrations
                DatabaseMigrator migrator = new DatabaseMigrator(config);
                registerMigrations(migrator);

                // Execute migrations
                MigrationResult result = migrator.migrate();

                if (jsonOutput) {
                    outputJson(result);
                } else {
                    outputText(result);
                }

                int exitCode = result.isSuccess() ? 0 : 1;
                ExitUtil.exit(exitCode);
                return exitCode;

            } catch (Exception e) {
                System.err.println("❌ Error running migrations: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(MigrationResult result) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📦 Database Migration");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            if (result.isSuccess()) {
                if (result.getMigrationsApplied() == 0) {
                    System.out.println("✅ Database is up to date");
                    System.out.println("   No pending migrations");
                } else {
                    System.out.println("✅ Migration completed successfully");
                    System.out.println();
                    System.out.println("Migrations Applied: " + result.getMigrationsApplied());
                    System.out.println("Duration:           " + result.getDurationMs() + " ms");
                    System.out.println();

                    if (!result.getAppliedVersions().isEmpty()) {
                        System.out.println("Applied Versions:");
                        for (String version : result.getAppliedVersions()) {
                            System.out.println("  ✅ " + version);
                        }
                    }
                }
            } else {
                System.out.println("❌ Migration failed");
                System.out.println();
                System.out.println("Migrations Applied: " + result.getMigrationsApplied());
                System.out.println("Duration:           " + result.getDurationMs() + " ms");
                System.out.println();

                if (result.getErrorMessage() != null) {
                    System.out.println("Error Details:");
                    System.out.println("  " + result.getErrorMessage());
                    System.out.println();
                }

                System.out.println("💡 Recommendations:");
                System.out.println("  → Review the failed migration script");
                System.out.println("  → Fix any SQL syntax errors");
                System.out.println("  → Check database permissions");
                System.out.println("  → Run 'blockchain migrate validate' to check schema state");
            }
        }

        private void outputJson(MigrationResult result) throws Exception {
            Map<String, Object> data = new HashMap<>();
            data.put("success", result.isSuccess());
            data.put("migrationsApplied", result.getMigrationsApplied());
            data.put("appliedVersions", result.getAppliedVersions());
            data.put("durationMs", result.getDurationMs());

            if (result.getErrorMessage() != null) {
                data.put("errorMessage", result.getErrorMessage());
            }

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }
    }

    /**
     * Show migration history.
     */
    @Command(
        name = "show-history",
        aliases = {"history"},
        description = "Display migration history"
    )
    public static class ShowHistoryCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                DatabaseMigrator migrator = new DatabaseMigrator(config);
                List<MigrationHistoryEntry> history = migrator.getHistory();

                if (jsonOutput) {
                    outputJson(history);
                } else {
                    outputText(history);
                }

                ExitUtil.exit(0);
                return 0;

            } catch (Exception e) {
                System.err.println("❌ Error retrieving migration history: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(List<MigrationHistoryEntry> history) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📋 Migration History");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            if (history.isEmpty()) {
                System.out.println("ℹ️  No migrations have been applied yet");
                return;
            }

            System.out.println("Total Migrations: " + history.size());
            System.out.println();

            // Table header
            System.out.println(String.format("%-10s %-40s %-20s %-15s %s",
                "Version", "Description", "Installed On", "Exec Time", "Status"));
            System.out.println("─".repeat(100));

            // Table rows
            for (MigrationHistoryEntry entry : history) {
                String statusIcon = entry.isSuccess() ? "✅" : "❌";
                String timestamp = TIMESTAMP_FORMATTER.format(entry.getInstalledOn());

                System.out.println(String.format("%-10s %-40s %-20s %-15s %s",
                    entry.getVersion(),
                    truncate(entry.getDescription(), 40),
                    timestamp,
                    entry.getExecutionTime() + " ms",
                    statusIcon + " " + entry.getState()
                ));
            }
        }

        private void outputJson(List<MigrationHistoryEntry> history) throws Exception {
            List<Map<String, Object>> data = new ArrayList<>();

            for (MigrationHistoryEntry entry : history) {
                Map<String, Object> entryData = new HashMap<>();
                entryData.put("version", entry.getVersion());
                entryData.put("description", entry.getDescription());
                entryData.put("type", entry.getType());
                entryData.put("installedBy", entry.getInstalledBy());
                entryData.put("installedOn", entry.getInstalledOn().toString());
                entryData.put("executionTime", entry.getExecutionTime());
                entryData.put("success", entry.isSuccess());
                entryData.put("state", entry.getState());
                data.add(entryData);
            }

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }

        private String truncate(String str, int maxLength) {
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        }
    }

    /**
     * Validate current schema state.
     */
    @Command(
        name = "validate",
        description = "Validate current database schema against registered migrations"
    )
    public static class ValidateCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                // Create migrator and register migrations
                DatabaseMigrator migrator = new DatabaseMigrator(config);
                registerMigrations(migrator);

                // Validate schema
                ValidationResult result = migrator.validate();

                if (jsonOutput) {
                    outputJson(result);
                } else {
                    outputText(result);
                }

                int exitCode = result.isValid() ? 0 : 1;
                ExitUtil.exit(exitCode);
                return exitCode;

            } catch (Exception e) {
                System.err.println("❌ Error validating schema: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(ValidationResult result) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔍 Schema Validation");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            if (result.isValid()) {
                System.out.println("✅ " + result.getMessage());
            } else {
                System.out.println("❌ " + result.getMessage());
                System.out.println();

                if (result.hasIssues()) {
                    System.out.println("Issues Found:");
                    for (String issue : result.getIssues()) {
                        System.out.println("  ❌ " + issue);
                    }
                    System.out.println();

                    System.out.println("💡 Recommendations:");
                    System.out.println("  → Review migration scripts");
                    System.out.println("  → Ensure all migrations are registered");
                    System.out.println("  → Check for manual schema modifications");
                    System.out.println("  → Run 'blockchain migrate show-history' to review applied migrations");
                }
            }
        }

        private void outputJson(ValidationResult result) throws Exception {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", result.isValid());
            data.put("message", result.getMessage());
            data.put("issues", result.getIssues());
            data.put("hasIssues", result.hasIssues());

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }
    }

    /**
     * Show current schema version.
     */
    @Command(
        name = "current-version",
        aliases = {"version"},
        description = "Display current database schema version"
    )
    public static class CurrentVersionCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                DatabaseMigrator migrator = new DatabaseMigrator(config);
                String currentVersion = migrator.getCurrentVersion();

                if (jsonOutput) {
                    outputJson(currentVersion);
                } else {
                    outputText(currentVersion);
                }

                ExitUtil.exit(0);
                return 0;

            } catch (Exception e) {
                System.err.println("❌ Error retrieving current version: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(String currentVersion) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📊 Current Schema Version");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            if (currentVersion == null) {
                System.out.println("ℹ️  No migrations have been applied yet");
                System.out.println("   Run 'blockchain migrate' to apply pending migrations");
            } else {
                System.out.println("Current Version: " + currentVersion);
            }
        }

        private void outputJson(String currentVersion) throws Exception {
            Map<String, Object> data = new HashMap<>();
            data.put("currentVersion", currentVersion);
            data.put("hasMigrations", currentVersion != null);

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }
    }

    /**
     * Registers all database migrations.
     *
     * <p>This method centralizes migration registration for consistency across all subcommands.</p>
     * <p>Migrations are loaded from SQL files in src/main/resources/db/migration/</p>
     *
     * @param migrator the migrator instance
     */
    private static void registerMigrations(DatabaseMigrator migrator) {
        // V1: Initial blockchain schema
        migrator.addMigration(Migration.builder()
            .version("V1")
            .description("Create initial blockchain schema")
            .sql(loadMigrationSql("V1__create_initial_blockchain_schema.sql"))
            .script("V1__create_initial_blockchain_schema.sql")
            .build());

        // V2: Search performance indexes (commented out - uncomment to enable)
        // migrator.addMigration(Migration.builder()
        //     .version("V2")
        //     .description("Add search performance indexes")
        //     .sql(loadMigrationSql("V2__add_search_performance_indexes.sql"))
        //     .script("V2__add_search_performance_indexes.sql")
        //     .build());

        // Future migrations can be added here by uncommenting or adding new entries
    }

    /**
     * Loads a migration SQL file from the classpath.
     *
     * <p>Migration files should be located in src/main/resources/db/migration/</p>
     * <p>This allows migrations to be maintained as separate SQL files rather than
     * hardcoded in Java code, making them easier to review, edit, and version control.</p>
     *
     * @param filename the migration filename (e.g., "V1__create_initial_schema.sql")
     * @return the SQL content as a String
     * @throws RuntimeException if the file cannot be found or read
     */
    private static String loadMigrationSql(String filename) {
        String resourcePath = "/db/migration/" + filename;

        try (java.io.InputStream inputStream = MigrateCommand.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Migration file not found: " + resourcePath);
            }

            java.util.Scanner scanner = new java.util.Scanner(inputStream, "UTF-8");
            scanner.useDelimiter("\\A");
            String sql = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            if (sql.trim().isEmpty()) {
                throw new RuntimeException("Migration file is empty: " + resourcePath);
            }

            return sql;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load migration file: " + resourcePath + " - " + e.getMessage(), e);
        }
    }
}
