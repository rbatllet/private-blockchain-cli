package com.rbatllet.blockchain.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;
import com.rbatllet.blockchain.core.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ConfigCommand - CLI configuration management
 * Tests configuration operations, profiles, and persistence
 */
public class ConfigCommandTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        // Initialize blockchain with clean state for each test
        try {
            Blockchain blockchain = new Blockchain();
            blockchain.clearAndReinitialize();
        } catch (Exception e) {
            // If blockchain initialization fails, continue with test
        }
    }

    @AfterEach  
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
    }

    /**
     * Helper method to get the real exit code, following the pattern from working tests
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }

    /**
     * Helper method to check for specific output content
     */
    private void assertOutputContains(String expectedKeyword, String description) {
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains(expectedKeyword), description + ": " + output);
    }
    
    // Method removed - was too permissive with fallback checks

    @Test
    @DisplayName("⚙️ Should display current configuration by default")
    void testShowConfigDefault() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show default configuration
        int exitCode = cmd.execute();
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // Should show configuration information
        assertTrue(allOutput.contains("Configuration"),
                  "Should show configuration: " + allOutput);
    }

    @Test
    @DisplayName("⚙️ Should display configuration with show action")
    void testShowConfigExplicit() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Explicitly show configuration
        int exitCode = cmd.execute("show");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // Should show configuration information
        assertTrue(allOutput.contains("Configuration"),
                  "Should show configuration: " + allOutput);
    }

    @Test
    @DisplayName("⚙️ Should display detailed configuration")
    void testShowConfigDetailed() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show detailed configuration
        int exitCode = cmd.execute("show", "--detailed");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        // For detailed config, expect specific content
        assertTrue(output.contains("Configuration"),
                  "Should show configuration: " + output);
    }

    @Test
    @DisplayName("📄 Should display configuration in JSON format")
    void testShowConfigJson() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show configuration in JSON
        int exitCode = cmd.execute("show", "--json");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("{") && output.contains("}"), 
                  "Should contain JSON format: " + output);
    }

    @Test
    @DisplayName("🔧 Should set individual configuration values")
    void testSetConfig() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Set search limit
        int exitCode = cmd.execute("set", "--key", "search.limit", "--value", "200");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration updated: search.limit = 200", "Should show success message");
    }

    @Test
    @DisplayName("🔧 Should set boolean configuration values")
    void testSetBooleanConfig() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Set verbose mode
        int exitCode = cmd.execute("set", "--key", "verbose.mode", "--value", "true");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration updated: verbose.mode = true", "Should show success message");
    }

    @Test
    @DisplayName("🔧 Should set custom properties")
    void testSetCustomProperty() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Set custom property
        int exitCode = cmd.execute("set", "--key", "custom.demo_mode", "--value", "enabled");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration updated: custom.demo_mode = enabled", "Should show success message");
    }

    @Test
    @DisplayName("❌ Should fail when setting unknown configuration key")
    void testSetUnknownKey() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try to set unknown key
        int exitCode = cmd.execute("set", "--key", "unknown.key", "--value", "value");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ Unknown configuration key: unknown.key", "Should show error message");
        assertOutputContains("Available Configuration Keys:", "Should show available keys");
    }

    @Test
    @DisplayName("❌ Should fail when missing key or value for set")
    void testSetMissingParameters() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try to set without value
        int exitCode = cmd.execute("set", "--key", "search.limit");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ Both --key and --value are required", "Should show error message");
    }

    @Test
    @DisplayName("🔄 Should reset configuration to defaults")
    void testResetConfig() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Reset configuration
        int exitCode = cmd.execute("reset");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration reset to defaults", "Should show success message");
    }

    @Test
    @DisplayName("📋 Should show available profiles")
    void testShowProfiles() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show profiles
        int exitCode = cmd.execute("profiles");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("Available Configuration Profiles"),
                  "Should show profiles: " + output);
    }

    @Test
    @DisplayName("🔧 Should apply development profile")
    void testApplyDevelopmentProfile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Apply development profile
        int exitCode = cmd.execute("apply-profile", "--profile", "development");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Applied configuration profile: development", "Should show success message");
    }

    @Test
    @DisplayName("🏭 Should apply production profile")
    void testApplyProductionProfile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Apply production profile
        int exitCode = cmd.execute("apply-profile", "--profile", "production");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Applied configuration profile: production", "Should show success message");
    }

    @Test
    @DisplayName("⚡ Should apply performance profile")
    void testApplyPerformanceProfile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Apply performance profile
        int exitCode = cmd.execute("apply-profile", "--profile", "performance");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Applied configuration profile: performance", "Should show success message");
    }

    @Test
    @DisplayName("🧪 Should apply testing profile")
    void testApplyTestingProfile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Apply testing profile
        int exitCode = cmd.execute("apply-profile", "--profile", "testing");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Applied configuration profile: testing", "Should show success message");
    }

    @Test
    @DisplayName("🔧 Should apply profile with verbose output")
    void testApplyProfileVerbose() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Apply profile with verbose output
        int exitCode = cmd.execute("apply-profile", "--profile", "development", "--verbose");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Applied configuration profile: development", "Should show success message");
        assertOutputContains("Applied configuration:", "Should show applied config");
        assertOutputContains("🔍", "Should show verbose logging");
    }

    @Test
    @DisplayName("❌ Should fail when applying unknown profile")
    void testApplyUnknownProfile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try to apply unknown profile
        int exitCode = cmd.execute("apply-profile", "--profile", "unknown");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ Unknown profile: unknown", "Should show error message");
    }

    @Test
    @DisplayName("❌ Should fail when missing profile parameter")
    void testApplyProfileMissingParameter() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try to apply profile without parameter
        int exitCode = cmd.execute("apply-profile");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ --profile parameter is required", "Should show error message");
    }

    @Test
    @DisplayName("📤 Should export configuration to file")
    void testExportConfig() throws Exception {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        Path exportFile = tempDir.resolve("test-config-export.properties");
        
        // Execute: Export configuration
        int exitCode = cmd.execute("export", "--file", exportFile.toString());
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration exported", "Should show success message");
        
        // Verify file was created
        assertTrue(Files.exists(exportFile), "Export file should exist");
        assertTrue(Files.size(exportFile) > 0, "Export file should not be empty");
    }

    @Test
    @DisplayName("📥 Should import configuration from file")
    void testImportConfig() throws Exception {
        // First export a configuration
        CommandLine exportCmd = new CommandLine(new ConfigCommand());
        Path exportFile = tempDir.resolve("test-config.properties");
        exportCmd.execute("export", "--file", exportFile.toString());
        
        // Reset output
        outContent.reset();
        
        // Now import it
        CommandLine importCmd = new CommandLine(new ConfigCommand());
        int exitCode = importCmd.execute("import", "--file", exportFile.toString());
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration imported", "Should show success message");
    }

    @Test
    @DisplayName("📥 Should import configuration with verbose output")
    void testImportConfigVerbose() throws Exception {
        // First export a configuration
        CommandLine exportCmd = new CommandLine(new ConfigCommand());
        Path exportFile = tempDir.resolve("test-config.properties");
        exportCmd.execute("export", "--file", exportFile.toString());
        
        // Reset output
        outContent.reset();
        
        // Import with verbose
        CommandLine importCmd = new CommandLine(new ConfigCommand());
        int exitCode = importCmd.execute("import", "--file", exportFile.toString(), "--verbose");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("Configuration imported", "Should show success message");
        // Verbose mode should show import details
        String output = outContent.toString() + errContent.toString();
        assertTrue(output.contains("🔍"),
                  "Should show verbose indicator: " + output);
    }

    @Test
    @DisplayName("❌ Should fail when exporting without file parameter")
    void testExportMissingFile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try export without file
        int exitCode = cmd.execute("export");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ --file parameter is required for export", "Should show error message");
    }

    @Test
    @DisplayName("❌ Should fail when importing non-existent file")
    void testImportNonExistentFile() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try import non-existent file
        int exitCode = cmd.execute("import", "--file", "/non/existent/file.properties");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ Failed to import configuration", "Should show error message");
    }

    @Test
    @DisplayName("❌ Should fail with unknown action")
    void testUnknownAction() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Try unknown action
        int exitCode = cmd.execute("unknown-action");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: expect failure but be flexible
        assertEquals(1, realExitCode, "Should fail with exit code 1, but was: " + realExitCode);
        
        assertOutputContains("❌ Unknown action: unknown-action", "Should show error message");
        assertOutputContains("Configuration Command Help", "Should show help");
    }

    @Test
    @DisplayName("🔍 Should handle verbose mode")
    void testVerboseMode() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show config with verbose
        int exitCode = cmd.execute("show", "--verbose");
        
        int realExitCode = getRealExitCode(exitCode);
        // Following pattern from working tests: commands may return errors due to setup issues
        assertEquals(0, realExitCode, 
                  "Command should succeed, but was: " + realExitCode);
        
        assertOutputContains("🔍", "Should show verbose logging indicators");
    }

    @Test
    @DisplayName("📋 Should provide help with --help")
    void testHelpOutput() {
        CommandLine cmd = new CommandLine(new ConfigCommand());
        
        // Execute: Show help
        int exitCode = cmd.execute("--help");
        
        int realExitCode = getRealExitCode(exitCode);
        // --help behavior varies: some commands return 0, 1, or 2
        assertEquals(0, realExitCode, 
                  "Help option should return reasonable exit code, but was: " + realExitCode);
        
        String output = outContent.toString();
        String errorOutput = errContent.toString();
        String allOutput = output + errorOutput;

        // --help shows error + usage
        assertTrue(allOutput.contains("Unknown option: '--help'"),
                  "Should show unknown option error: " + allOutput);
        assertTrue(allOutput.contains("Usage: config"),
                  "Should show usage: " + allOutput);
    }

    @Test
    @DisplayName("🎯 Should handle profile name variations")
    void testProfileNameVariations() {
        CommandLine cmd1 = new CommandLine(new ConfigCommand());
        CommandLine cmd2 = new CommandLine(new ConfigCommand());
        CommandLine cmd3 = new CommandLine(new ConfigCommand());
        
        // Test short names
        int exitCode1 = cmd1.execute("apply-profile", "--profile", "dev");
        int exitCode2 = cmd2.execute("apply-profile", "--profile", "prod");
        int exitCode3 = cmd3.execute("apply-profile", "--profile", "perf");
        
        assertEquals(0, exitCode1, "Dev profile should work");
        assertEquals(0, exitCode2, "Prod profile should work");
        assertEquals(0, exitCode3, "Perf profile should work");
    }

    @Test
    @DisplayName("🔄 Should handle complete workflow")
    void testCompleteWorkflow() throws Exception {
        // 1. Show initial config
        CommandLine cmd1 = new CommandLine(new ConfigCommand());
        int exitCode1 = cmd1.execute("show");
        assertEquals(0, exitCode1, "Show should succeed");
        
        // 2. Apply production profile
        CommandLine cmd2 = new CommandLine(new ConfigCommand());
        int exitCode2 = cmd2.execute("apply-profile", "--profile", "production");
        assertEquals(0, exitCode2, "Apply profile should succeed");
        
        // 3. Set custom values
        CommandLine cmd3 = new CommandLine(new ConfigCommand());
        int exitCode3 = cmd3.execute("set", "--key", "search.limit", "--value", "500");
        assertEquals(0, exitCode3, "Set config should succeed");
        
        // 4. Export configuration
        Path exportFile = tempDir.resolve("workflow-config.properties");
        CommandLine cmd4 = new CommandLine(new ConfigCommand());
        int exitCode4 = cmd4.execute("export", "--file", exportFile.toString());
        assertEquals(0, exitCode4, "Export should succeed");
        
        // 5. Reset configuration
        CommandLine cmd5 = new CommandLine(new ConfigCommand());
        int exitCode5 = cmd5.execute("reset");
        assertEquals(0, exitCode5, "Reset should succeed");
        
        // 6. Import configuration back
        CommandLine cmd6 = new CommandLine(new ConfigCommand());
        int exitCode6 = cmd6.execute("import", "--file", exportFile.toString());
        assertEquals(0, exitCode6, "Import should succeed");
        
        // All operations should succeed
        String output = outContent.toString();
        long successCount = output.lines()
            .filter(line -> line.contains("✅") || line.contains("Applied configuration") || line.contains("Configuration updated"))
            .count();
        assertTrue(successCount >= 4, "Should show multiple successful operations");
    }
}