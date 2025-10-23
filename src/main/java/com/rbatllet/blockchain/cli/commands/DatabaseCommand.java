package com.rbatllet.blockchain.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbatllet.blockchain.cli.BlockchainCLI;
import com.rbatllet.blockchain.cli.config.CLIDatabaseConfigManager;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.config.DatabaseConfig;
import com.rbatllet.blockchain.config.util.DatabaseConnectionTester;
import com.rbatllet.blockchain.config.util.DatabaseConnectionTester.ConnectionTestResult;
import com.rbatllet.blockchain.config.util.SensitiveDataMasker;
import com.rbatllet.blockchain.config.util.ConfigurationExporter;
import com.rbatllet.blockchain.config.util.ConfigurationExporter.ExportFormat;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Database configuration command with subcommands for viewing and testing database connections.
 *
 * <p>This command provides utilities for managing database configuration:</p>
 * <ul>
 *   <li><b>show</b>: Display current database configuration</li>
 *   <li><b>test</b>: Test database connection</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>
 * # Show current database configuration
 * blockchain database show
 *
 * # Show configuration in JSON format
 * blockchain database show --json
 *
 * # Test database connection
 * blockchain database test
 *
 * # Test with verbose output
 * blockchain database test --verbose
 * </pre>
 *
 * @since 1.0.5
 */
@Command(
    name = "database",
    aliases = {"db"},
    description = "Manage database configuration",
    subcommands = {
        DatabaseCommand.ShowCommand.class,
        DatabaseCommand.TestCommand.class,
        DatabaseCommand.ExportCommand.class
    }
)
public class DatabaseCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        System.err.println("❌ Please specify a subcommand: show, test");
        System.err.println("Use 'blockchain database --help' for more information");
        ExitUtil.exit(1);
        return 1;
    }

    /**
     * Show current database configuration.
     */
    @Command(
        name = "show",
        description = "Display current database configuration"
    )
    public static class ShowCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                if (jsonOutput) {
                    outputJson(config, manager);
                } else {
                    outputText(config, manager);
                }

                ExitUtil.exit(0);
                return 0;

            } catch (Exception e) {
                System.err.println("❌ Error retrieving database configuration: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(DatabaseConfig config, CLIDatabaseConfigManager manager) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📊 Database Configuration");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            System.out.println("Database Type: " + config.getDatabaseType());
            System.out.println("JDBC URL:      " + maskPassword(config.getDatabaseUrl()));
            System.out.println();

            System.out.println("Connection Pool:");
            System.out.println("  Min Size:           " + config.getPoolMinSize());
            System.out.println("  Max Size:           " + config.getPoolMaxSize());
            System.out.println("  Connection Timeout: " + config.getConnectionTimeout() + " ms");
            System.out.println("  Idle Timeout:       " + config.getIdleTimeout() + " ms");
            System.out.println("  Max Lifetime:       " + config.getMaxLifetime() + " ms");
            System.out.println();

            System.out.println("Hibernate:");
            System.out.println("  hbm2ddl.auto: " + config.getHbm2ddlAuto());
            System.out.println("  show_sql:     " + config.isShowSql());
            System.out.println();

            // Show configuration source
            System.out.println("Configuration Source:");
            System.out.println("  Priority order: CLI args > Environment > Config file > Default");
            System.out.println("  Config file:    " + manager.getPropertiesLoader().getConfigFilePath());
            System.out.println();
        }

        private void outputJson(DatabaseConfig config, CLIDatabaseConfigManager manager) throws Exception {
            Map<String, Object> data = new HashMap<>();
            data.put("databaseType", config.getDatabaseType().toString());
            data.put("databaseUrl", maskPassword(config.getDatabaseUrl()));
            data.put("poolMinSize", config.getPoolMinSize());
            data.put("poolMaxSize", config.getPoolMaxSize());
            data.put("connectionTimeout", config.getConnectionTimeout());
            data.put("idleTimeout", config.getIdleTimeout());
            data.put("maxLifetime", config.getMaxLifetime());
            data.put("hbm2ddlAuto", config.getHbm2ddlAuto());
            data.put("showSql", config.isShowSql());

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }

        private String maskPassword(String url) {
            if (url == null) {
                return "null";
            }
            // Use SensitiveDataMasker from CORE for comprehensive masking
            // Detects and masks: password=, passwd=, pwd=, user=, secret=, token=, etc.
            // Also masks URLs with format user:password@host
            return SensitiveDataMasker.maskSensitiveData(url);
        }
    }

    /**
     * Test database connection.
     */
    @Command(
        name = "test",
        description = "Test database connection"
    )
    public static class TestCommand implements Callable<Integer> {

        @Option(names = {"--json"}, description = "Output in JSON format")
        private boolean jsonOutput;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                // Use CORE DatabaseConnectionTester for comprehensive validation
                DatabaseConnectionTester tester = new DatabaseConnectionTester();
                ConnectionTestResult result = tester.testConnection(config);

                if (jsonOutput) {
                    outputJson(result);
                } else {
                    outputText(result);
                }

                int exitCode = result.isSuccessful() ? 0 : 1;
                ExitUtil.exit(exitCode);
                return exitCode;

            } catch (Exception e) {
                System.err.println("❌ Error testing database connection: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }

        private void outputText(ConnectionTestResult result) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔍 Database Connection Test (Enhanced)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            System.out.println("Database: " + result.getConfig().getDatabaseType());
            System.out.println();

            // Overall status
            System.out.println("Status: " + (result.isSuccessful() ? "✅ Success" : "❌ Failed"));
            System.out.println();

            // Response time with performance warning
            long responseTimeMs = result.getResponseTime().toMillis();
            String perfIndicator = responseTimeMs > 1000 ? " ⚠️ SLOW" : "";
            System.out.println("Response Time: " + responseTimeMs + " ms" + perfIndicator);
            System.out.println();

            // Connection details
            if (result.isSuccessful()) {
                System.out.println("Connection Details:");

                if (result.getDatabaseVersion() != null) {
                    System.out.println("  Database Version: " + result.getDatabaseVersion());
                }

                if (result.getDriverVersion() != null) {
                    System.out.println("  Driver Version:   " + result.getDriverVersion());
                }

                System.out.println("  Can Read:         " + (result.canRead() ? "✅ Yes" : "❌ No"));
                System.out.println("  Read-Only Mode:   " + (result.isReadOnly() ? "⚠️ Yes" : "✅ No"));
                System.out.println();
            } else {
                System.out.println("Error Details:");
                System.out.println("  " + result.getErrorMessage());
                System.out.println();
            }

            // Recommendations
            if (result.hasRecommendations()) {
                System.out.println("💡 Recommendations:");
                for (String recommendation : result.getRecommendations()) {
                    System.out.println("  → " + recommendation);
                }
                System.out.println();
            }

            // Final verdict
            if (result.isSuccessful()) {
                System.out.println("✅ All validations passed - database is ready for use");
            } else {
                System.out.println("❌ Connection test failed - please check configuration");
            }
        }

        private void outputJson(ConnectionTestResult result) throws Exception {
            Map<String, Object> data = new HashMap<>();
            data.put("successful", result.isSuccessful());
            data.put("responseTimeMs", result.getResponseTime().toMillis());
            data.put("databaseType", result.getConfig().getDatabaseType().toString());

            if (result.getDatabaseVersion() != null) {
                data.put("databaseVersion", result.getDatabaseVersion());
            }

            if (result.getDriverVersion() != null) {
                data.put("driverVersion", result.getDriverVersion());
            }

            data.put("canRead", result.canRead());
            data.put("readOnly", result.isReadOnly());

            if (result.getErrorMessage() != null) {
                data.put("errorMessage", result.getErrorMessage());
            }

            if (result.hasRecommendations()) {
                data.put("recommendations", result.getRecommendations());
            }

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }
    }

    /**
     * Export database configuration to file.
     * Supports multiple formats: properties, json, env
     * Uses ConfigurationExporter from CORE for centralized export logic.
     */
    @Command(
        name = "export",
        description = "Export database configuration to file (properties, json, or env format)"
    )
    public static class ExportCommand implements Callable<Integer> {

        @Option(names = {"--file"}, description = "Output file path (format auto-detected from extension: .properties, .json, .env)", required = true)
        private String filePath;

        @Option(names = {"--format"}, description = "Export format: PROPERTIES, JSON, ENV (optional, auto-detected from extension)")
        private String format;

        @Option(names = {"--no-mask"}, description = "Export without masking sensitive data (default: masked)")
        private boolean noMask;

        @Override
        public Integer call() {
            try {
                CLIDatabaseConfigManager manager = CLIDatabaseConfigManager.getInstance();
                DatabaseConfig config = manager.getConfig();

                // Create exporter with masking enabled by default
                ConfigurationExporter exporter = new ConfigurationExporter()
                    .withMasking(!noMask);

                // Determine format from --format flag or auto-detect from file extension
                ExportFormat exportFormat = null;
                if (format != null) {
                    try {
                        exportFormat = ExportFormat.valueOf(format.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("❌ Invalid format: " + format);
                        System.err.println("   Supported formats: PROPERTIES, JSON, ENV");
                        ExitUtil.exit(1);
                        return 1;
                    }
                }

                // Export to file (will auto-detect format if not specified)
                exporter.exportToFile(config, java.nio.file.Paths.get(filePath), exportFormat);

                System.out.println("✅ Database configuration exported successfully");
                System.out.println("   File: " + filePath);
                System.out.println("   Format: " + (exportFormat != null ? exportFormat : "auto-detected"));
                if (!noMask) {
                    System.out.println("   ℹ️  Passwords are masked. Use --no-mask to export unmasked.");
                }

                ExitUtil.exit(0);
                return 0;

            } catch (java.nio.file.InvalidPathException e) {
                System.err.println("❌ Invalid file path: " + e.getMessage());
                ExitUtil.exit(1);
                return 1;
            } catch (java.io.IOException e) {
                System.err.println("❌ Failed to export configuration: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            } catch (Exception e) {
                System.err.println("❌ Error exporting database configuration: " + e.getMessage());
                if (BlockchainCLI.verbose) {
                    e.printStackTrace();
                }
                ExitUtil.exit(1);
                return 1;
            }
        }
    }
}
