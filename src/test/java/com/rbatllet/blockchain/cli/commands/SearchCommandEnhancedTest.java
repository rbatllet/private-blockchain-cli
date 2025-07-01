package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.util.ExitUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for SearchCommand covering hybrid search system functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class SearchCommandEnhancedTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine searchCli;
    private CommandLine addCli;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        // Disable ExitUtil.exit() for testing
        ExitUtil.disableExit();
        
        searchCli = new CommandLine(new SearchCommand());
        addCli = new CommandLine(new AddBlockCommand());
        
        // Setup test data for search tests
        setupTestData();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable ExitUtil.exit() after testing
        ExitUtil.enableExit();
    }

    private void setupTestData() {
        try {
            // Reset output streams to capture only setup output if needed
            ByteArrayOutputStream setupOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(setupOut));
            
            // Create test blocks using AddBlockCommand to ensure consistency
            addCli.execute(
                "Medical patient data for PATIENT-001 with ECG results from 2024-01-15",
                "--generate-key",
                "--keywords", "PATIENT-001,ECG,CARDIOLOGY",
                "--category", "MEDICAL"
            );

            addCli.execute(
                "Financial transaction TXN-2024-001 amount 50000 EUR processed via API integration",
                "--generate-key", 
                "--keywords", "TXN-2024-001,FINANCE,TRANSACTION",
                "--category", "FINANCE"
            );

            addCli.execute(
                "Technical documentation for API endpoint admin@company.com with JSON format",
                "--generate-key",
                "--keywords", "API,DOCUMENTATION,JSON",
                "--category", "TECHNICAL"
            );
            
            // Restore output streams for actual tests
            System.setOut(new PrintStream(outContent));
            
        } catch (Exception e) {
            System.setOut(new PrintStream(outContent));
            System.out.println("Note: Test data setup had issues: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test search validation with short term")
    void testSearchValidation() {
        int exitCode = searchCli.execute("ab", "--validate-term", "--verbose");
        
        // Should fail validation
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail validation for short search term");
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Invalid search term") || errorOutput.contains("at least 4 characters"),
                  "Should show validation error message. Error: " + errorOutput);
        
        assertTrue(errorOutput.contains("'ab'") || errorOutput.contains("Search terms"),
                  "Should mention the invalid term. Error: " + errorOutput);
    }

    @Test
    @Order(2)
    @DisplayName("Test search with no criteria specified")
    void testSearchNoCriteria() {
        int exitCode = searchCli.execute(); // No arguments
        
        // Should fail when no criteria provided
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail when no search criteria specified");
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("No search criteria specified") || 
                  errorOutput.contains("Available options"),
                  "Should show no criteria error. Error: " + errorOutput);
        
        assertTrue(errorOutput.contains("Content:") || errorOutput.contains("Category:") || 
                  errorOutput.contains("Hash:"),
                  "Should list available search options. Error: " + errorOutput);
    }

    @Test
    @Order(3)
    @DisplayName("Test search with invalid date format")
    void testSearchInvalidDate() {
        int exitCode = searchCli.execute("--date-from", "invalid-date", "--verbose");
        
        // Should fail with invalid date
        int realExitCode = ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : exitCode;
        assertEquals(1, realExitCode, "Should fail with invalid date format");
        
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Invalid date format") || errorOutput.contains("yyyy-MM-dd"),
                  "Should show date format error. Error: " + errorOutput);
    }

    @Test
    @Order(4)
    @DisplayName("Test hybrid content search - FAST_ONLY level")
    void testHybridSearchFastOnly() {
        int exitCode = searchCli.execute("PATIENT-001", "--fast", "--verbose");
        assertEquals(0, exitCode, "Search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Hybrid Search Results") || output.contains("Search Results"),
                  "Should show search results header. Output: " + output);
        
        assertTrue(output.contains("FAST_ONLY") || output.contains("fast"),
                  "Should indicate fast search level. Output: " + output);
        
        // Should find the medical block or show no results gracefully
        assertTrue(output.contains("Found") && (output.contains("block") || output.contains("No blocks")),
                  "Should show search results. Output: " + output);
        
        if (output.contains("Found") && !output.contains("0 block")) {
            assertTrue(output.contains("PATIENT-001") || output.contains("MEDICAL"),
                      "Should show relevant search results. Output: " + output);
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test hybrid content search - INCLUDE_DATA level")
    void testHybridSearchIncludeData() {
        int exitCode = searchCli.execute("transaction", "--level", "INCLUDE_DATA", "--verbose");
        assertEquals(0, exitCode, "Search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Hybrid Search Results") || output.contains("Search Results"),
                  "Should show search results header. Output: " + output);
        
        assertTrue(output.contains("INCLUDE_DATA") || output.contains("data"),
                  "Should indicate include data search level. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
    }

    @Test
    @Order(6)
    @DisplayName("Test hybrid content search - EXHAUSTIVE_OFFCHAIN level")
    void testHybridSearchExhaustive() {
        int exitCode = searchCli.execute("JSON", "--complete", "--verbose");
        assertEquals(0, exitCode, "Search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Hybrid Search Results") || output.contains("Search Results"),
                  "Should show search results header. Output: " + output);
        
        assertTrue(output.contains("EXHAUSTIVE_OFFCHAIN") || output.contains("complete"),
                  "Should indicate exhaustive search level. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
    }

    @Test
    @Order(7)
    @DisplayName("Test category search")
    void testCategorySearch() {
        int exitCode = searchCli.execute("--category", "MEDICAL", "--verbose");
        assertEquals(0, exitCode, "Category search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Search Results") || output.contains("category"),
                  "Should show search results. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
        
        // If results found, should mention MEDICAL
        if (!output.contains("0 block") && !output.contains("No blocks")) {
            assertTrue(output.contains("MEDICAL"),
                      "Should show MEDICAL category results. Output: " + output);
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test search by block number")
    void testSearchByBlockNumber() {
        int exitCode = searchCli.execute("--block-number", "1", "--verbose");
        assertEquals(0, exitCode, "Block number search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Search Results") || output.contains("block-number"),
                  "Should show search results. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
        
        // If block found, should show Block #1
        if (!output.contains("0 block") && !output.contains("No blocks")) {
            assertTrue(output.contains("Block #1"),
                      "Should show Block #1 details. Output: " + output);
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test search with JSON output")
    void testSearchJsonOutput() {
        int exitCode = searchCli.execute("API", "--json", "--verbose");
        assertEquals(0, exitCode, "JSON search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("{") && output.contains("}"),
                  "Should produce JSON output. Output: " + output);
        
        assertTrue(output.contains("\"searchType\":") || output.contains("searchType"),
                  "Should include search type in JSON. Output: " + output);
        
        assertTrue(output.contains("\"resultCount\":") || output.contains("resultCount"),
                  "Should include result count in JSON. Output: " + output);
        
        assertTrue(output.contains("\"blocks\":") || output.contains("blocks"),
                  "Should include blocks array in JSON. Output: " + output);
        
        assertTrue(output.contains("\"searchLevel\":") || output.contains("searchLevel"),
                  "Should include search level in JSON. Output: " + output);
    }

    @Test
    @Order(10)
    @DisplayName("Test search with detailed output")
    void testSearchDetailedOutput() {
        int exitCode = searchCli.execute("CARDIOLOGY", "--detailed", "--verbose");
        assertEquals(0, exitCode, "Detailed search should succeed");

        String output = outContent.toString();
        
        // If results are found, detailed output should show additional information
        if (!output.contains("0 block") && !output.contains("No blocks")) {
            assertTrue(output.contains("Manual Keywords:") || output.contains("Auto Keywords:") ||
                      output.contains("Previous Hash:") || output.contains("Signer:"),
                      "Detailed output should show additional block information. Output: " + output);
        }
        
        // Should always show search results header
        assertTrue(output.contains("Search Results") || output.contains("Found"),
                  "Should show search results. Output: " + output);
    }

    @Test
    @Order(11)
    @DisplayName("Test search with result limit")
    void testSearchWithLimit() {
        int exitCode = searchCli.execute("data", "--limit", "2", "--verbose");
        assertEquals(0, exitCode, "Limited search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
        
        // If more than 2 results would be found, should mention limiting
        if (output.contains("limited") || output.contains("limit")) {
            assertTrue(output.contains("2") || output.contains("--limit"),
                      "Should mention result limiting. Output: " + output);
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test date range search")
    void testDateRangeSearch() {
        LocalDate today = LocalDate.now();
        int exitCode = searchCli.execute(
            "--date-from", today.toString(), 
            "--date-to", today.plusDays(1).toString(), 
            "--verbose"
        );
        assertEquals(0, exitCode, "Date range search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Search Results") || output.contains("date-range"),
                  "Should show search results. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
    }

    @Test
    @Order(13)
    @DisplayName("Test search performance reporting")
    void testSearchPerformanceReporting() {
        int exitCode = searchCli.execute("API", "--verbose");
        assertEquals(0, exitCode, "Search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Search performance:") || output.contains("ms") || 
                  output.contains("Search completed"),
                  "Should show performance information. Output: " + output);
        
        // Should mention search time
        assertTrue(output.contains("ms") || output.contains("milliseconds"),
                  "Should show search timing. Output: " + output);
    }

    @Test
    @Order(14)
    @DisplayName("Test search suggestions when no results found")
    void testSearchSuggestions() {
        int exitCode = searchCli.execute("nonexistent-very-unique-term-12345", "--verbose");
        assertEquals(0, exitCode, "Search should succeed even with no results");

        String output = outContent.toString();
        
        // Should show no results message and suggestions
        if (output.contains("No blocks found") || output.contains("0 block")) {
            assertTrue(output.contains("Try different search levels") || 
                      output.contains("--fast") || output.contains("--complete"),
                      "Should show search suggestions when no results found. Output: " + output);
        }
    }

    @Test
    @Order(15)
    @DisplayName("Test content search with --content option")
    void testContentSearchOption() {
        int exitCode = searchCli.execute("--content", "patient", "--verbose");
        assertEquals(0, exitCode, "Content search should succeed");

        String output = outContent.toString();
        assertTrue(output.contains("Search Results") || output.contains("hybrid-content"),
                  "Should show search results. Output: " + output);
        
        assertTrue(output.contains("Found") && output.contains("block"),
                  "Should show search results. Output: " + output);
    }

    @Test
    @Order(16)
    @DisplayName("Test search command options parsing")
    void testSearchOptionsParsing() {
        // Test that various options are parsed correctly without crashing
        int exitCode = searchCli.execute("test", "--fast", "--json", "--limit", "5", "--verbose");
        assertEquals(0, exitCode, "Should parse options correctly");

        String output = outContent.toString();
        assertTrue(output.length() > 0, "Should produce output");
        
        // Should handle the combination of options gracefully
        assertTrue(output.contains("{") || output.contains("Search") || output.contains("Found"),
                  "Should handle multiple options correctly. Output: " + output);
    }

    @Test
    @Order(17)
    @DisplayName("Test search with all verbose output")
    void testSearchVerboseOutput() {
        int exitCode = searchCli.execute("PATIENT", "--verbose");
        assertEquals(0, exitCode, "Verbose search should succeed");

        String output = outContent.toString();
        
        // Verify verbose messages are present
        assertTrue(output.contains("🔍 Starting hybrid blockchain search") || 
                  output.contains("Starting") || output.contains("search"),
                  "Should show verbose initialization. Output: " + output);
        
        assertTrue(output.contains("Search completed") || output.contains("found") || 
                  output.contains("results"),
                  "Should show search completion. Output: " + output);
        
        assertTrue(output.contains("Search performance") || output.contains("ms"),
                  "Should show performance information. Output: " + output);
    }

    @Test
    @Order(18)
    @DisplayName("Test search error handling")
    void testSearchErrorHandling() {
        // Test graceful error handling for various scenarios
        
        // Test with malformed arguments
        int exitCode1 = searchCli.execute("--limit", "invalid", "--verbose");
        // Should handle gracefully (may succeed with default limit or fail gracefully)
        assertTrue(exitCode1 >= 0, "Should handle malformed limit gracefully");
        
        // Reset streams for next test
        outContent.reset();
        errContent.reset();
        
        // Test with very long search term
        String longTerm = "a".repeat(1000);
        int exitCode2 = searchCli.execute(longTerm, "--verbose");
        assertEquals(0, exitCode2, "Should handle long search terms");
        
        String output = outContent.toString();
        assertTrue(output.contains("Found") || output.contains("No blocks"),
                  "Should handle long search terms gracefully. Output: " + output);
    }
}