package com.rbatllet.blockchain.cli.config;

import com.rbatllet.blockchain.config.EncryptionConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CLIConfigManager
 * Tests configuration management, persistence, security, and error handling
 */
@DisplayName("CLIConfigManager Tests")
public class CLIConfigManagerTest {

    @TempDir
    Path tempDir;

    private CLIConfigManager manager;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        manager = new CLIConfigManager(tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // ======================== Constructor Tests ========================

    @Test
    @DisplayName("Should create config manager with valid path")
    void testConstructorWithValidPath() {
        CLIConfigManager validManager = new CLIConfigManager(tempDir.toString());
        assertNotNull(validManager, "Manager should be created");
        assertEquals(tempDir.normalize().toAbsolutePath(), validManager.getConfigDirectory(),
                "Config directory should match");
    }

    @Test
    @DisplayName("Should throw exception for null config path")
    void testConstructorWithNullPath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CLIConfigManager(null),
                "Should throw exception for null path");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty config path")
    void testConstructorWithEmptyPath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CLIConfigManager(""),
                "Should throw exception for empty path");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for whitespace config path")
    void testConstructorWithWhitespacePath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CLIConfigManager("   "),
                "Should throw exception for whitespace path");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should create config manager even with normalized paths")
    void testConstructorWithNormalizedPaths() {
        // After normalization, paths like "/tmp/../etc" become "/etc"
        // This is actually safe because it's normalized to absolute path
        CLIConfigManager testManager = new CLIConfigManager("/tmp/../tmp");
        assertNotNull(testManager, "Manager should be created after normalization");
        assertTrue(testManager.getConfigDirectory().toString().contains("/tmp"),
                "Path should be normalized to /tmp");
    }

    @Test
    @DisplayName("Should throw exception for tilde in path")
    void testConstructorWithTildePath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CLIConfigManager("~/config"),
                "Should throw exception for tilde path");
        assertTrue(exception.getMessage().contains("forbidden characters"),
                "Exception message should mention forbidden characters: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should create config directory if it doesn't exist")
    void testCreatesConfigDirectory() {
        Path newDir = tempDir.resolve("new-config-dir");
        assertFalse(Files.exists(newDir), "Directory should not exist yet");

        new CLIConfigManager(newDir.toString());
        assertTrue(Files.exists(newDir), "Directory should be created");
        assertTrue(Files.isDirectory(newDir), "Should be a directory");
    }

    // ======================== Load Config Tests ========================

    @Test
    @DisplayName("Should load default config when file doesn't exist")
    void testLoadConfigWithoutFile() {
        CLIConfig config = manager.loadConfig();

        assertNotNull(config, "Config should not be null");
        assertEquals("text", config.getOutputFormat(), "Should have default output format");
        assertEquals(50, config.getSearchLimit(), "Should have default search limit");
        assertFalse(config.isVerboseMode(), "Should have default verbose mode");
    }

    @Test
    @DisplayName("Should load config from existing file")
    void testLoadConfigFromFile() {
        // Create a config and save it
        CLIConfig originalConfig = new CLIConfig();
        originalConfig.setOutputFormat("json");
        originalConfig.setSearchLimit(200);
        originalConfig.setVerboseMode(true);

        boolean saved = manager.saveConfig(originalConfig);
        assertTrue(saved, "Config should be saved");

        // Load it back
        CLIConfig loadedConfig = manager.loadConfig();
        assertEquals("json", loadedConfig.getOutputFormat(),
                "Loaded output format should match");
        assertEquals(200, loadedConfig.getSearchLimit(),
                "Loaded search limit should match");
        assertTrue(loadedConfig.isVerboseMode(),
                "Loaded verbose mode should match");
    }

    @Test
    @DisplayName("Should handle invalid numeric values in config file")
    void testLoadConfigWithInvalidNumbers() throws Exception {
        // Create a properties file with invalid numbers
        Path configFile = manager.getConfigPath();
        Properties props = new Properties();
        props.setProperty("search.limit", "not-a-number");
        props.setProperty("command.timeout", "invalid");
        props.setProperty("offchain.threshold", "xyz");
        props.setProperty("max.results", "-100");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test config with invalid numbers");
        }

        // Load should succeed with default values
        CLIConfig config = manager.loadConfig();
        assertNotNull(config, "Config should not be null");
        assertEquals(50, config.getSearchLimit(),
                "Should use default for invalid number");
    }

    @Test
    @DisplayName("Should handle negative numeric values in config file")
    void testLoadConfigWithNegativeNumbers() throws Exception {
        Path configFile = manager.getConfigPath();
        Properties props = new Properties();
        props.setProperty("search.limit", "-50");
        props.setProperty("command.timeout", "0");
        props.setProperty("offchain.threshold", "-1000");

        try (var out = Files.newOutputStream(configFile)) {
            props.store(out, "Test config with negative numbers");
        }

        CLIConfig config = manager.loadConfig();
        assertEquals(50, config.getSearchLimit(),
                "Should use default for negative number");
    }

    @Test
    @DisplayName("Should load encryption config along with CLI config")
    void testLoadConfigWithEncryption() {
        CLIConfig originalConfig = new CLIConfig();
        EncryptionConfig encConfig = new EncryptionConfig();
        originalConfig.setEncryptionConfig(encConfig);

        manager.saveConfig(originalConfig);

        CLIConfig loadedConfig = manager.loadConfig();
        assertNotNull(loadedConfig.getEncryptionConfig(),
                "Encryption config should not be null");
    }

    // ======================== Save Config Tests ========================

    @Test
    @DisplayName("Should save config successfully")
    void testSaveConfig() {
        CLIConfig config = new CLIConfig();
        config.setOutputFormat("json");
        config.setSearchLimit(250);

        boolean result = manager.saveConfig(config);
        assertTrue(result, "Save should succeed");
        assertTrue(Files.exists(manager.getConfigPath()),
                "Config file should exist");
    }

    @Test
    @DisplayName("Should save all config properties correctly")
    void testSaveConfigAllProperties() {
        CLIConfig config = new CLIConfig();
        config.setOutputFormat("json");
        config.setSearchLimit(500);
        config.setVerboseMode(true);
        config.setDetailedOutput(true);
        config.setDefaultSearchType("INTELLIGENT");
        config.setDefaultSearchLevel("EXHAUSTIVE_OFFCHAIN");
        config.setOffChainThreshold(1024000L);
        config.setLogLevel("DEBUG");
        config.setCommandTimeout(5000);
        config.setMaxResults(1000);
        config.setEnableMetrics(true);
        config.setAutoCleanup(false);
        config.setStoreCredentials(true);
        config.setRequireConfirmation(false);
        config.setEnableAuditLog(true);

        manager.saveConfig(config);

        CLIConfig loadedConfig = manager.loadConfig();
        assertEquals("json", loadedConfig.getOutputFormat());
        assertEquals(500, loadedConfig.getSearchLimit());
        assertTrue(loadedConfig.isVerboseMode());
        assertTrue(loadedConfig.isDetailedOutput());
        assertEquals("INTELLIGENT", loadedConfig.getDefaultSearchType());
        assertEquals("EXHAUSTIVE_OFFCHAIN", loadedConfig.getDefaultSearchLevel());
        assertEquals(1024000L, loadedConfig.getOffChainThreshold());
        assertEquals("DEBUG", loadedConfig.getLogLevel());
        assertEquals(5000, loadedConfig.getCommandTimeout());
        assertEquals(1000, loadedConfig.getMaxResults());
        assertTrue(loadedConfig.isEnableMetrics());
        assertFalse(loadedConfig.isAutoCleanup());
        assertTrue(loadedConfig.isStoreCredentials());
        assertFalse(loadedConfig.isRequireConfirmation());
        assertTrue(loadedConfig.isEnableAuditLog());
    }

    @Test
    @DisplayName("Should save and load custom properties")
    void testSaveConfigWithCustomProperties() {
        CLIConfig config = new CLIConfig();
        config.addCustomProperty("test.property", "test-value");
        config.addCustomProperty("another.property", "another-value");

        manager.saveConfig(config);

        CLIConfig loadedConfig = manager.loadConfig();
        assertEquals("test-value", loadedConfig.getCustomProperty("test.property", null),
                "Custom property should be loaded");
        assertEquals("another-value", loadedConfig.getCustomProperty("another.property", null),
                "Another custom property should be loaded");
    }

    // ======================== Reset Config Tests ========================

    @Test
    @DisplayName("Should reset config when files exist")
    void testResetConfig() {
        // Create config files
        CLIConfig config = new CLIConfig();
        manager.saveConfig(config);
        assertTrue(Files.exists(manager.getConfigPath()),
                "Config file should exist before reset");

        boolean result = manager.resetConfig();
        assertTrue(result, "Reset should succeed");
        assertFalse(Files.exists(manager.getConfigPath()),
                "Config file should not exist after reset");
    }

    @Test
    @DisplayName("Should reset config when files don't exist")
    void testResetConfigNoFiles() {
        assertFalse(Files.exists(manager.getConfigPath()),
                "Config file should not exist initially");

        boolean result = manager.resetConfig();
        assertTrue(result, "Reset should succeed even without files");
    }

    // ======================== Config Exists Tests ========================

    @Test
    @DisplayName("Should return false when config doesn't exist")
    void testConfigExistsWhenNotExists() {
        assertFalse(manager.configExists(), "Config should not exist initially");
    }

    @Test
    @DisplayName("Should return true when config exists")
    void testConfigExistsWhenExists() {
        CLIConfig config = new CLIConfig();
        manager.saveConfig(config);

        assertTrue(manager.configExists(), "Config should exist after save");
    }

    // ======================== Export Config Tests ========================

    @Test
    @DisplayName("Should export config to file")
    void testExportConfig() {
        Path exportPath = tempDir.resolve("export-test.properties");
        CLIConfig config = new CLIConfig();
        config.setOutputFormat("json");
        config.setSearchLimit(300);

        boolean result = manager.exportConfig(exportPath, config);
        assertTrue(result, "Export should succeed");
        assertTrue(Files.exists(exportPath), "Export file should exist");
    }

    @Test
    @DisplayName("Should throw exception for null export path")
    void testExportConfigNullPath() {
        CLIConfig config = new CLIConfig();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.exportConfig(null, config),
                "Should throw exception for null path");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null config in export")
    void testExportConfigNullConfig() {
        Path exportPath = tempDir.resolve("test.properties");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.exportConfig(exportPath, null),
                "Should throw exception for null config");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should export to normalized paths successfully")
    void testExportConfigNormalizedPaths() {
        // Test that export works with paths that need normalization
        Path exportPath = tempDir.resolve("subdir").resolve("..").resolve("test.properties");
        CLIConfig config = new CLIConfig();
        config.setOutputFormat("json");

        boolean result = manager.exportConfig(exportPath, config);
        assertTrue(result, "Export should succeed with normalized path");

        // Verify file was created at the normalized location
        Path normalizedPath = exportPath.normalize();
        assertTrue(Files.exists(normalizedPath),
                "File should exist at normalized path: " + normalizedPath);
    }

    @Test
    @DisplayName("Should export config with encryption settings")
    void testExportConfigWithEncryption() {
        Path exportPath = tempDir.resolve("export-with-encryption.properties");
        CLIConfig config = new CLIConfig();
        EncryptionConfig encConfig = new EncryptionConfig();
        config.setEncryptionConfig(encConfig);

        boolean result = manager.exportConfig(exportPath, config);
        assertTrue(result, "Export should succeed");
        assertTrue(Files.exists(exportPath), "Export file should exist");
    }

    @Test
    @DisplayName("Should handle export with null encryption config")
    void testExportConfigWithNullEncryption() {
        Path exportPath = tempDir.resolve("export-null-encryption.properties");
        CLIConfig config = new CLIConfig();
        config.setEncryptionConfig(null);

        boolean result = manager.exportConfig(exportPath, config);
        assertTrue(result, "Export should succeed with null encryption");
    }

    // ======================== Import Config Tests ========================

    @Test
    @DisplayName("Should import config from file")
    void testImportConfig() {
        // Export first
        Path exportPath = tempDir.resolve("import-test.properties");
        CLIConfig originalConfig = new CLIConfig();
        originalConfig.setOutputFormat("json");
        originalConfig.setSearchLimit(400);
        manager.exportConfig(exportPath, originalConfig);

        // Import
        CLIConfig importedConfig = manager.importConfig(exportPath);
        assertNotNull(importedConfig, "Imported config should not be null");
        assertEquals("json", importedConfig.getOutputFormat(),
                "Imported output format should match");
        assertEquals(400, importedConfig.getSearchLimit(),
                "Imported search limit should match");
    }

    @Test
    @DisplayName("Should throw exception for null import path")
    void testImportConfigNullPath() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.importConfig(null),
                "Should throw exception for null path");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should import from normalized paths successfully")
    void testImportConfigNormalizedPaths() {
        // Create a test file first in a subdirectory
        Path subdir = tempDir.resolve("subdir");
        try {
            Files.createDirectories(subdir);
        } catch (Exception e) {
            fail("Failed to create test directory: " + e.getMessage());
        }

        Path testFile = subdir.resolve("test-import.properties");
        CLIConfig originalConfig = new CLIConfig();
        originalConfig.setSearchLimit(250);
        manager.exportConfig(testFile, originalConfig);

        assertTrue(Files.exists(testFile), "Test file should exist");

        // Import using a path that needs normalization
        Path importPath = subdir.resolve("..").resolve("subdir").resolve("test-import.properties");
        assertFalse(importPath.toString().equals(testFile.toString()),
                "Paths should be different before normalization");

        CLIConfig result = manager.importConfig(importPath);
        assertNotNull(result, "Import should succeed with normalized path");
        assertEquals(250, result.getSearchLimit(),
                "Imported config should match original");
    }

    @Test
    @DisplayName("Should return null for non-existent import file")
    void testImportConfigNonExistentFile() {
        Path importPath = tempDir.resolve("non-existent.properties");

        CLIConfig result = manager.importConfig(importPath);
        assertNull(result, "Should return null for non-existent file");
    }

    @Test
    @DisplayName("Should return null for directory instead of file")
    void testImportConfigDirectory() throws Exception {
        Path dirPath = tempDir.resolve("test-directory");
        Files.createDirectory(dirPath);

        CLIConfig result = manager.importConfig(dirPath);
        assertNull(result, "Should return null for directory");
    }

    @Test
    @DisplayName("Should import config with encryption settings")
    void testImportConfigWithEncryption() {
        // Export with encryption
        Path exportPath = tempDir.resolve("import-encryption.properties");
        CLIConfig originalConfig = new CLIConfig();
        EncryptionConfig encConfig = new EncryptionConfig();
        originalConfig.setEncryptionConfig(encConfig);
        manager.exportConfig(exportPath, originalConfig);

        // Import
        CLIConfig importedConfig = manager.importConfig(exportPath);
        assertNotNull(importedConfig, "Imported config should not be null");
        assertNotNull(importedConfig.getEncryptionConfig(),
                "Encryption config should not be null");
    }

    // ======================== Path Security Tests ========================

    @Test
    @DisplayName("Should normalize relative paths")
    void testNormalizeRelativePaths() {
        String relativePath = tempDir.toString() + "/./subdir/../config";
        CLIConfigManager testManager = new CLIConfigManager(relativePath);

        Path configDir = testManager.getConfigDirectory();
        assertFalse(configDir.toString().contains("./"),
                "Path should not contain ./");
        assertFalse(configDir.toString().contains("../"),
                "Path should not contain ../");
    }

    // ======================== Complete Workflow Tests ========================

    @Test
    @DisplayName("Should handle complete save-load-export-import workflow")
    void testCompleteWorkflow() {
        // 1. Create and save config
        CLIConfig originalConfig = new CLIConfig();
        originalConfig.setOutputFormat("json");
        originalConfig.setSearchLimit(350);
        originalConfig.setVerboseMode(true);
        originalConfig.addCustomProperty("workflow.test", "value");

        boolean saved = manager.saveConfig(originalConfig);
        assertTrue(saved, "Save should succeed");

        // 2. Load config
        CLIConfig loadedConfig = manager.loadConfig();
        assertEquals("json", loadedConfig.getOutputFormat(),
                "Loaded config should match saved");

        // 3. Export config
        Path exportPath = tempDir.resolve("workflow-export.properties");
        boolean exported = manager.exportConfig(exportPath, loadedConfig);
        assertTrue(exported, "Export should succeed");

        // 4. Reset config
        boolean reset = manager.resetConfig();
        assertTrue(reset, "Reset should succeed");
        assertFalse(manager.configExists(), "Config should not exist after reset");

        // 5. Import config back
        CLIConfig importedConfig = manager.importConfig(exportPath);
        assertNotNull(importedConfig, "Import should succeed");
        assertEquals("json", importedConfig.getOutputFormat(),
                "Imported config should match original");
        assertEquals(350, importedConfig.getSearchLimit(),
                "Imported search limit should match");
        assertTrue(importedConfig.isVerboseMode(),
                "Imported verbose mode should match");
        assertEquals("value", importedConfig.getCustomProperty("workflow.test", null),
                "Custom property should be preserved");
    }
}
