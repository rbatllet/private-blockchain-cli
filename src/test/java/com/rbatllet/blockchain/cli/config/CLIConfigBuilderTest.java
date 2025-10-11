package com.rbatllet.blockchain.cli.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CLIConfig.Builder
 * Tests all builder methods with rigorous validation and security checks
 */
@DisplayName("CLIConfig.Builder Tests")
public class CLIConfigBuilderTest {

    // ======================== outputFormat Tests ========================

    @Test
    @DisplayName("Should set valid text output format")
    void testOutputFormatText() {
        CLIConfig config = CLIConfig.builder()
                .outputFormat("text")
                .build();

        assertEquals("text", config.getOutputFormat(),
                "Output format should be text");
    }

    @Test
    @DisplayName("Should set valid json output format")
    void testOutputFormatJson() {
        CLIConfig config = CLIConfig.builder()
                .outputFormat("json")
                .build();

        assertEquals("json", config.getOutputFormat(),
                "Output format should be json");
    }

    @Test
    @DisplayName("Should set valid csv output format")
    void testOutputFormatCsv() {
        CLIConfig config = CLIConfig.builder()
                .outputFormat("csv")
                .build();

        assertEquals("csv", config.getOutputFormat(),
                "Output format should be csv");
    }

    @Test
    @DisplayName("Should throw exception for null output format")
    void testOutputFormatNull() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.outputFormat(null),
                "Should throw exception for null format");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty output format")
    void testOutputFormatEmpty() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.outputFormat(""),
                "Should throw exception for empty format");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for whitespace output format")
    void testOutputFormatWhitespace() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.outputFormat("   "),
                "Should throw exception for whitespace format");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid output format")
    void testOutputFormatInvalid() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.outputFormat("xml"),
                "Should throw exception for invalid format");
        assertTrue(exception.getMessage().contains("text, json, or csv"),
                "Exception message should list valid formats: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for uppercase output format")
    void testOutputFormatUppercase() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.outputFormat("JSON"),
                "Should throw exception for uppercase format");
        assertTrue(exception.getMessage().contains("text, json, or csv"),
                "Exception message should mention valid formats: " + exception.getMessage());
    }

    // ======================== defaultSearchType Tests ========================

    @Test
    @DisplayName("Should set valid SIMPLE search type")
    void testSearchTypeSimple() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchType("SIMPLE")
                .build();

        assertEquals("SIMPLE", config.getDefaultSearchType(),
                "Search type should be SIMPLE");
    }

    @Test
    @DisplayName("Should set valid SECURE search type")
    void testSearchTypeSecure() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchType("SECURE")
                .build();

        assertEquals("SECURE", config.getDefaultSearchType(),
                "Search type should be SECURE");
    }

    @Test
    @DisplayName("Should set valid INTELLIGENT search type")
    void testSearchTypeIntelligent() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchType("INTELLIGENT")
                .build();

        assertEquals("INTELLIGENT", config.getDefaultSearchType(),
                "Search type should be INTELLIGENT");
    }

    @Test
    @DisplayName("Should set valid ADVANCED search type")
    void testSearchTypeAdvanced() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchType("ADVANCED")
                .build();

        assertEquals("ADVANCED", config.getDefaultSearchType(),
                "Search type should be ADVANCED");
    }

    @Test
    @DisplayName("Should throw exception for null search type")
    void testSearchTypeNull() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchType(null),
                "Should throw exception for null type");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty search type")
    void testSearchTypeEmpty() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchType(""),
                "Should throw exception for empty type");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid search type")
    void testSearchTypeInvalid() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchType("INVALID"),
                "Should throw exception for invalid type");
        assertTrue(exception.getMessage().contains("SIMPLE, SECURE, INTELLIGENT, or ADVANCED"),
                "Exception message should list valid types: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for lowercase search type")
    void testSearchTypeLowercase() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchType("simple"),
                "Should throw exception for lowercase type");
        assertTrue(exception.getMessage().contains("SIMPLE, SECURE, INTELLIGENT, or ADVANCED"),
                "Exception message should mention valid types: " + exception.getMessage());
    }

    // ======================== defaultSearchLevel Tests ========================

    @Test
    @DisplayName("Should set valid FAST_ONLY search level")
    void testSearchLevelFastOnly() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchLevel("FAST_ONLY")
                .build();

        assertEquals("FAST_ONLY", config.getDefaultSearchLevel(),
                "Search level should be FAST_ONLY");
    }

    @Test
    @DisplayName("Should set valid INCLUDE_DATA search level")
    void testSearchLevelIncludeData() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchLevel("INCLUDE_DATA")
                .build();

        assertEquals("INCLUDE_DATA", config.getDefaultSearchLevel(),
                "Search level should be INCLUDE_DATA");
    }

    @Test
    @DisplayName("Should set valid EXHAUSTIVE_OFFCHAIN search level")
    void testSearchLevelExhaustiveOffchain() {
        CLIConfig config = CLIConfig.builder()
                .defaultSearchLevel("EXHAUSTIVE_OFFCHAIN")
                .build();

        assertEquals("EXHAUSTIVE_OFFCHAIN", config.getDefaultSearchLevel(),
                "Search level should be EXHAUSTIVE_OFFCHAIN");
    }

    @Test
    @DisplayName("Should throw exception for null search level")
    void testSearchLevelNull() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchLevel(null),
                "Should throw exception for null level");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty search level")
    void testSearchLevelEmpty() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchLevel(""),
                "Should throw exception for empty level");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid search level")
    void testSearchLevelInvalid() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.defaultSearchLevel("INVALID"),
                "Should throw exception for invalid level");
        assertTrue(exception.getMessage().contains("FAST_ONLY, INCLUDE_DATA, or EXHAUSTIVE_OFFCHAIN"),
                "Exception message should list valid levels: " + exception.getMessage());
    }

    // ======================== offChainThreshold Tests ========================

    @Test
    @DisplayName("Should set minimum valid off-chain threshold (1KB)")
    void testOffChainThresholdMinimum() {
        CLIConfig config = CLIConfig.builder()
                .offChainThreshold(1024)
                .build();

        assertEquals(1024, config.getOffChainThreshold(),
                "Off-chain threshold should be 1024");
    }

    @Test
    @DisplayName("Should set typical off-chain threshold (512KB)")
    void testOffChainThresholdTypical() {
        CLIConfig config = CLIConfig.builder()
                .offChainThreshold(512 * 1024)
                .build();

        assertEquals(512 * 1024, config.getOffChainThreshold(),
                "Off-chain threshold should be 512KB");
    }

    @Test
    @DisplayName("Should set maximum valid off-chain threshold (100MB)")
    void testOffChainThresholdMaximum() {
        CLIConfig config = CLIConfig.builder()
                .offChainThreshold(100 * 1024 * 1024)
                .build();

        assertEquals(100 * 1024 * 1024, config.getOffChainThreshold(),
                "Off-chain threshold should be 100MB");
    }

    @Test
    @DisplayName("Should throw exception for threshold below minimum")
    void testOffChainThresholdBelowMinimum() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.offChainThreshold(1023),
                "Should throw exception for threshold < 1KB");
        assertTrue(exception.getMessage().contains("at least 1KB"),
                "Exception message should mention minimum: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for zero threshold")
    void testOffChainThresholdZero() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.offChainThreshold(0),
                "Should throw exception for zero threshold");
        assertTrue(exception.getMessage().contains("at least 1KB"),
                "Exception message should mention minimum: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative threshold")
    void testOffChainThresholdNegative() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.offChainThreshold(-1000),
                "Should throw exception for negative threshold");
        assertTrue(exception.getMessage().contains("at least 1KB"),
                "Exception message should mention minimum: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for threshold above maximum")
    void testOffChainThresholdAboveMaximum() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.offChainThreshold(101 * 1024 * 1024),
                "Should throw exception for threshold > 100MB");
        assertTrue(exception.getMessage().contains("at most 100MB"),
                "Exception message should mention maximum: " + exception.getMessage());
    }

    // ======================== configFile Tests ========================

    @Test
    @DisplayName("Should set valid config file name")
    void testConfigFileValid() {
        CLIConfig config = CLIConfig.builder()
                .configFile("my-config.properties")
                .build();

        assertEquals("my-config.properties", config.getConfigFile(),
                "Config file should be set");
    }

    @Test
    @DisplayName("Should set config file with path")
    void testConfigFileWithPath() {
        CLIConfig config = CLIConfig.builder()
                .configFile("configs/production.properties")
                .build();

        assertEquals("configs/production.properties", config.getConfigFile(),
                "Config file with path should be set");
    }

    @Test
    @DisplayName("Should throw exception for null config file")
    void testConfigFileNull() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.configFile(null),
                "Should throw exception for null file");
        assertTrue(exception.getMessage().contains("cannot be null"),
                "Exception message should mention null: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty config file")
    void testConfigFileEmpty() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.configFile(""),
                "Should throw exception for empty file");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for whitespace config file")
    void testConfigFileWhitespace() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.configFile("   "),
                "Should throw exception for whitespace file");
        assertTrue(exception.getMessage().contains("cannot be null or empty"),
                "Exception message should mention empty: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for config file with path traversal")
    void testConfigFilePathTraversal() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.configFile("../../../etc/passwd"),
                "Should throw exception for path traversal");
        assertTrue(exception.getMessage().contains("forbidden characters"),
                "Exception message should mention forbidden characters: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for config file with tilde")
    void testConfigFileTilde() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.configFile("~/config.properties"),
                "Should throw exception for tilde in path");
        assertTrue(exception.getMessage().contains("forbidden characters"),
                "Exception message should mention forbidden characters: " + exception.getMessage());
    }

    // ======================== customProperty Tests ========================

    @Test
    @DisplayName("Should add valid custom property")
    void testCustomPropertyValid() {
        CLIConfig config = CLIConfig.builder()
                .customProperty("test.key", "test-value")
                .build();

        assertEquals("test-value", config.getCustomProperty("test.key", null),
                "Custom property should be set");
    }

    @Test
    @DisplayName("Should add multiple custom properties")
    void testCustomPropertyMultiple() {
        CLIConfig config = CLIConfig.builder()
                .customProperty("key1", "value1")
                .customProperty("key2", "value2")
                .customProperty("key3", "value3")
                .build();

        assertEquals("value1", config.getCustomProperty("key1", null),
                "First property should be set");
        assertEquals("value2", config.getCustomProperty("key2", null),
                "Second property should be set");
        assertEquals("value3", config.getCustomProperty("key3", null),
                "Third property should be set");
    }

    @Test
    @DisplayName("Should allow empty string as custom property value")
    void testCustomPropertyEmptyValue() {
        CLIConfig config = CLIConfig.builder()
                .customProperty("empty.key", "")
                .build();

        assertEquals("", config.getCustomProperty("empty.key", null),
                "Empty value should be allowed");
    }

    @Test
    @DisplayName("Should throw exception for null custom property key")
    void testCustomPropertyNullKey() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.customProperty(null, "value"),
                "Should throw exception for null key");
        assertTrue(exception.getMessage().contains("key cannot be null"),
                "Exception message should mention key: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty custom property key")
    void testCustomPropertyEmptyKey() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.customProperty("", "value"),
                "Should throw exception for empty key");
        assertTrue(exception.getMessage().contains("key cannot be null or empty"),
                "Exception message should mention empty key: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for whitespace custom property key")
    void testCustomPropertyWhitespaceKey() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.customProperty("   ", "value"),
                "Should throw exception for whitespace key");
        assertTrue(exception.getMessage().contains("key cannot be null or empty"),
                "Exception message should mention empty key: " + exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null custom property value")
    void testCustomPropertyNullValue() {
        CLIConfig.Builder builder = CLIConfig.builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.customProperty("key", null),
                "Should throw exception for null value");
        assertTrue(exception.getMessage().contains("value cannot be null"),
                "Exception message should mention value: " + exception.getMessage());
    }

    // ======================== storeCredentials Tests ========================

    @Test
    @DisplayName("Should set storeCredentials to true")
    void testStoreCredentialsTrue() {
        CLIConfig config = CLIConfig.builder()
                .storeCredentials(true)
                .build();

        assertTrue(config.isStoreCredentials(),
                "Store credentials should be true");
    }

    @Test
    @DisplayName("Should set storeCredentials to false")
    void testStoreCredentialsFalse() {
        CLIConfig config = CLIConfig.builder()
                .storeCredentials(false)
                .build();

        assertFalse(config.isStoreCredentials(),
                "Store credentials should be false");
    }

    @Test
    @DisplayName("Should have default storeCredentials value")
    void testStoreCredentialsDefault() {
        CLIConfig config = CLIConfig.builder().build();

        assertEquals(CLIConfig.DEFAULT_STORE_CREDENTIALS, config.isStoreCredentials(),
                "Should have default store credentials value");
    }

    // ======================== Builder Chaining Tests ========================

    @Test
    @DisplayName("Should chain multiple builder methods")
    void testBuilderChaining() {
        CLIConfig config = CLIConfig.builder()
                .outputFormat("json")
                .defaultSearchType("INTELLIGENT")
                .defaultSearchLevel("EXHAUSTIVE_OFFCHAIN")
                .offChainThreshold(1024 * 1024)
                .configFile("custom.properties")
                .storeCredentials(true)
                .customProperty("env", "production")
                .build();

        assertEquals("json", config.getOutputFormat(),
                "Output format should be set");
        assertEquals("INTELLIGENT", config.getDefaultSearchType(),
                "Search type should be set");
        assertEquals("EXHAUSTIVE_OFFCHAIN", config.getDefaultSearchLevel(),
                "Search level should be set");
        assertEquals(1024 * 1024, config.getOffChainThreshold(),
                "Off-chain threshold should be set");
        assertEquals("custom.properties", config.getConfigFile(),
                "Config file should be set");
        assertTrue(config.isStoreCredentials(),
                "Store credentials should be true");
        assertEquals("production", config.getCustomProperty("env", null),
                "Custom property should be set");
    }

    @Test
    @DisplayName("Should validate configuration on build")
    void testBuildValidation() {
        // This should fail validation because searchLimit > maxResults
        CLIConfig.Builder builder = CLIConfig.builder()
                .searchLimit(1000)
                .maxResults(500); // Less than searchLimit

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                builder::build,
                "Should throw exception for invalid configuration");
        assertTrue(exception.getMessage().contains("Max results must be >= search limit"),
                "Exception message should mention validation issue: " + exception.getMessage());
    }
}
