package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.service.SearchMetrics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for SearchMetricsCommand private methods
 * Tests the 2 methods that need complete execution path coverage:
 * - outputText(SearchMetrics)
 * - outputJson(SearchMetrics)
 */
public class SearchMetricsCommandMethodsTest {

    private SearchMetricsCommand command;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private SearchMetrics mockSearchMetrics;

    @Mock
    private SearchMetrics.PerformanceStats mockPerfStats;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        command = new SearchMetricsCommand();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        closeable.close();
    }

    /**
     * Helper method to set the 'detailed' field using reflection
     */
    private void setDetailedFlag(boolean value) throws Exception {
        Field detailedField = SearchMetricsCommand.class.getDeclaredField("detailed");
        detailedField.setAccessible(true);
        detailedField.set(command, value);
    }

    // ========================================
    // Tests for outputText(SearchMetrics)
    // ========================================

    @Test
    @DisplayName("Should throw exception for null SearchMetrics in outputText")
    void testOutputText_NullSearchMetrics() throws Exception {
        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, (SearchMetrics) null),
                "Should throw exception for null SearchMetrics");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("SearchMetrics cannot be null"),
                "Exception message should mention SearchMetrics cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null performance report in outputText")
    void testOutputText_NullPerformanceReport() throws Exception {
        when(mockSearchMetrics.getPerformanceReport()).thenReturn(null);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, mockSearchMetrics),
                "Should throw exception for null performance report");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalStateException,
                "Cause should be IllegalStateException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Performance report cannot be null"),
                "Exception message should mention performance report cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null search type stats in outputText with detailed=true")
    void testOutputText_NullSearchTypeStats() throws Exception {
        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Test Report");
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(null);

        setDetailedFlag(true);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, mockSearchMetrics),
                "Should throw exception for null search type stats");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalStateException,
                "Cause should be IllegalStateException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Search type stats cannot be null"),
                "Exception message should mention search type stats cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle valid SearchMetrics with detailed=false in outputText")
    void testOutputText_ValidSearchMetrics_NotDetailed() throws Exception {
        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Performance Report\nTotal: 10 searches");

        setDetailedFlag(false);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("📊 Search Performance Metrics"),
                "Output should contain metrics header: " + output);
        assertTrue(output.contains("Performance Report"),
                "Output should contain performance report: " + output);
        assertTrue(output.contains("💡 Tips:"),
                "Output should contain tips section: " + output);
        assertFalse(output.contains("🔍 Detailed Metrics:"),
                "Output should NOT contain detailed metrics when detailed=false: " + output);
    }

    @Test
    @DisplayName("Should handle valid SearchMetrics with detailed=true and empty stats in outputText")
    void testOutputText_ValidSearchMetrics_DetailedEmptyStats() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> emptyStats = new HashMap<>();

        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Performance Report");
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(emptyStats);

        setDetailedFlag(true);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("📊 Search Performance Metrics"),
                "Output should contain metrics header: " + output);
        assertTrue(output.contains("🔍 Detailed Metrics:"),
                "Output should contain detailed metrics header: " + output);
        assertTrue(output.contains("⚡ Performance Insights:"),
                "Output should contain performance insights: " + output);
        assertTrue(output.contains("ℹ️  No search operations recorded yet"),
                "Output should show no operations message: " + output);
        assertFalse(output.contains("📋 Search Type Breakdown:"),
                "Output should NOT contain breakdown for empty stats: " + output);
    }

    @Test
    @DisplayName("Should handle valid SearchMetrics with detailed=true and one search type in outputText")
    void testOutputText_ValidSearchMetrics_DetailedOneType() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        when(mockPerfStats.getSearches()).thenReturn(5L);
        when(mockPerfStats.getAverageTimeMs()).thenReturn(25.5);
        when(mockPerfStats.getTotalTimeMs()).thenReturn(127L);
        when(mockPerfStats.getCacheHitRate()).thenReturn(80.0);
        when(mockPerfStats.getAverageResults()).thenReturn(3.4);

        stats.put("SIMPLE", mockPerfStats);

        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Performance Report");
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        setDetailedFlag(true);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("📊 Search Performance Metrics"),
                "Output should contain metrics header: " + output);
        assertTrue(output.contains("🔍 Detailed Metrics:"),
                "Output should contain detailed metrics header: " + output);
        assertTrue(output.contains("📋 Search Type Breakdown:"),
                "Output should contain breakdown section: " + output);
        assertTrue(output.contains("🔹 SIMPLE"),
                "Output should contain search type name: " + output);
        assertTrue(output.contains("Searches: 5"),
                "Output should contain search count: " + output);
        assertTrue(output.contains("Avg Time: 25.50 ms"),
                "Output should contain average time: " + output);
        assertTrue(output.contains("Total Time: 127.00 ms"),
                "Output should contain total time: " + output);
        assertTrue(output.contains("Cache Hits: 80.0%"),
                "Output should contain cache hit rate: " + output);
        assertTrue(output.contains("Avg Results: 3.4"),
                "Output should contain average results: " + output);
        assertTrue(output.contains("⚡ Performance Insights:"),
                "Output should contain insights: " + output);
        assertTrue(output.contains("🚀 Fastest search type: SIMPLE"),
                "Output should show fastest type: " + output);
        assertTrue(output.contains("🐌 Slowest search type: SIMPLE"),
                "Output should show slowest type: " + output);
    }

    @Test
    @DisplayName("Should handle valid SearchMetrics with detailed=true and multiple search types in outputText")
    void testOutputText_ValidSearchMetrics_DetailedMultipleTypes() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        SearchMetrics.PerformanceStats simplePerfStats = mock(SearchMetrics.PerformanceStats.class);
        SearchMetrics.PerformanceStats securePerfStats = mock(SearchMetrics.PerformanceStats.class);
        SearchMetrics.PerformanceStats advancedPerfStats = mock(SearchMetrics.PerformanceStats.class);

        when(simplePerfStats.getSearches()).thenReturn(10L);
        when(simplePerfStats.getAverageTimeMs()).thenReturn(15.0);
        when(simplePerfStats.getTotalTimeMs()).thenReturn(150L);
        when(simplePerfStats.getCacheHitRate()).thenReturn(90.0);
        when(simplePerfStats.getAverageResults()).thenReturn(5.0);

        when(securePerfStats.getSearches()).thenReturn(5L);
        when(securePerfStats.getAverageTimeMs()).thenReturn(50.0);
        when(securePerfStats.getTotalTimeMs()).thenReturn(250L);
        when(securePerfStats.getCacheHitRate()).thenReturn(60.0);
        when(securePerfStats.getAverageResults()).thenReturn(3.0);

        when(advancedPerfStats.getSearches()).thenReturn(3L);
        when(advancedPerfStats.getAverageTimeMs()).thenReturn(100.0);
        when(advancedPerfStats.getTotalTimeMs()).thenReturn(300L);
        when(advancedPerfStats.getCacheHitRate()).thenReturn(40.0);
        when(advancedPerfStats.getAverageResults()).thenReturn(8.0);

        stats.put("SIMPLE", simplePerfStats);
        stats.put("SECURE", securePerfStats);
        stats.put("ADVANCED", advancedPerfStats);

        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Performance Report");
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        setDetailedFlag(true);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("📊 Search Performance Metrics"),
                "Output should contain metrics header: " + output);
        assertTrue(output.contains("📋 Search Type Breakdown:"),
                "Output should contain breakdown section: " + output);
        assertTrue(output.contains("🔹 SIMPLE"),
                "Output should contain SIMPLE type: " + output);
        assertTrue(output.contains("🔹 SECURE"),
                "Output should contain SECURE type: " + output);
        assertTrue(output.contains("🔹 ADVANCED"),
                "Output should contain ADVANCED type: " + output);
        assertTrue(output.contains("⚡ Performance Insights:"),
                "Output should contain insights: " + output);
        assertTrue(output.contains("🚀 Fastest search type: SIMPLE"),
                "Output should show SIMPLE as fastest: " + output);
        assertTrue(output.contains("🐌 Slowest search type: ADVANCED"),
                "Output should show ADVANCED as slowest: " + output);
        assertTrue(output.contains("📈 Overall cache hit rate:"),
                "Output should show overall cache hit rate: " + output);
    }

    @Test
    @DisplayName("Should handle valid SearchMetrics with detailed=true and totalSearches=0 in outputText")
    void testOutputText_ValidSearchMetrics_DetailedZeroSearches() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        when(mockPerfStats.getSearches()).thenReturn(0L);
        when(mockPerfStats.getAverageTimeMs()).thenReturn(0.0);
        when(mockPerfStats.getTotalTimeMs()).thenReturn(0L);
        when(mockPerfStats.getCacheHitRate()).thenReturn(0.0);
        when(mockPerfStats.getAverageResults()).thenReturn(0.0);

        stats.put("SIMPLE", mockPerfStats);

        when(mockSearchMetrics.getPerformanceReport()).thenReturn("Performance Report");
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        setDetailedFlag(true);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputText", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("⚡ Performance Insights:"),
                "Output should contain insights: " + output);
        assertTrue(output.contains("ℹ️  No search operations recorded yet"),
                "Output should show no operations message: " + output);
        assertFalse(output.contains("🚀 Fastest search type:"),
                "Output should NOT show fastest type when no searches: " + output);
        assertFalse(output.contains("🐌 Slowest search type:"),
                "Output should NOT show slowest type when no searches: " + output);
    }

    // ========================================
    // Tests for outputJson(SearchMetrics)
    // ========================================

    @Test
    @DisplayName("Should throw exception for null SearchMetrics in outputJson")
    void testOutputJson_NullSearchMetrics() throws Exception {
        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, (SearchMetrics) null),
                "Should throw exception for null SearchMetrics");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("SearchMetrics cannot be null"),
                "Exception message should mention SearchMetrics cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null search type stats in outputJson")
    void testOutputJson_NullSearchTypeStats() throws Exception {
        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(null);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, mockSearchMetrics),
                "Should throw exception for null search type stats");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalStateException,
                "Cause should be IllegalStateException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Search type stats cannot be null"),
                "Exception message should mention search type stats cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle empty stats in outputJson")
    void testOutputJson_EmptyStats() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> emptyStats = new HashMap<>();

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(emptyStats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("{"),
                "Output should contain opening brace: " + output);
        assertTrue(output.contains("\"searchMetrics\":"),
                "Output should contain searchMetrics field: " + output);
        assertTrue(output.contains("\"totalSearches\": 0"),
                "Output should contain 0 total searches: " + output);
        assertTrue(output.contains("\"totalTimeMs\": 0.00"),
                "Output should contain 0 total time: " + output);
        assertTrue(output.contains("\"averageTimeMs\": 0.00"),
                "Output should contain 0 average time: " + output);
        assertTrue(output.contains("\"overallCacheHitRate\": 0.000"),
                "Output should contain 0 cache hit rate: " + output);
        assertTrue(output.contains("\"searchTypes\": {"),
                "Output should contain empty searchTypes object: " + output);
        assertTrue(output.contains("}"),
                "Output should contain closing brace: " + output);
    }

    @Test
    @DisplayName("Should handle single search type in outputJson")
    void testOutputJson_SingleSearchType() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        when(mockPerfStats.getSearches()).thenReturn(10L);
        when(mockPerfStats.getTotalTimeMs()).thenReturn(500L);
        when(mockPerfStats.getAverageTimeMs()).thenReturn(50.0);
        when(mockPerfStats.getCacheHitRate()).thenReturn(75.0);
        when(mockPerfStats.getAverageResults()).thenReturn(4.5);
        when(mockPerfStats.getMinTimeMs()).thenReturn(20L);
        when(mockPerfStats.getMaxTimeMs()).thenReturn(100L);

        stats.put("SIMPLE", mockPerfStats);

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("\"totalSearches\": 10"),
                "Output should contain total searches: " + output);
        assertTrue(output.contains("\"totalTimeMs\": 500.00"),
                "Output should contain total time: " + output);
        assertTrue(output.contains("\"averageTimeMs\": 50.00"),
                "Output should contain average time: " + output);
        assertTrue(output.contains("\"SIMPLE\":"),
                "Output should contain SIMPLE search type: " + output);
        assertTrue(output.contains("\"searchCount\": 10"),
                "Output should contain search count: " + output);
        assertTrue(output.contains("\"cacheHitRate\": 0.750"),
                "Output should contain cache hit rate: " + output);
        assertTrue(output.contains("\"averageResults\": 4.50"),
                "Output should contain average results: " + output);
        assertTrue(output.contains("\"minTimeMs\": 20.00"),
                "Output should contain min time: " + output);
        assertTrue(output.contains("\"maxTimeMs\": 100.00"),
                "Output should contain max time: " + output);
    }

    @Test
    @DisplayName("Should handle multiple search types in outputJson")
    void testOutputJson_MultipleSearchTypes() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        SearchMetrics.PerformanceStats simplePerfStats = mock(SearchMetrics.PerformanceStats.class);
        SearchMetrics.PerformanceStats securePerfStats = mock(SearchMetrics.PerformanceStats.class);

        when(simplePerfStats.getSearches()).thenReturn(15L);
        when(simplePerfStats.getTotalTimeMs()).thenReturn(300L);
        when(simplePerfStats.getAverageTimeMs()).thenReturn(20.0);
        when(simplePerfStats.getCacheHitRate()).thenReturn(85.0);
        when(simplePerfStats.getAverageResults()).thenReturn(6.0);
        when(simplePerfStats.getMinTimeMs()).thenReturn(10L);
        when(simplePerfStats.getMaxTimeMs()).thenReturn(30L);

        when(securePerfStats.getSearches()).thenReturn(8L);
        when(securePerfStats.getTotalTimeMs()).thenReturn(640L);
        when(securePerfStats.getAverageTimeMs()).thenReturn(80.0);
        when(securePerfStats.getCacheHitRate()).thenReturn(50.0);
        when(securePerfStats.getAverageResults()).thenReturn(2.5);
        when(securePerfStats.getMinTimeMs()).thenReturn(60L);
        when(securePerfStats.getMaxTimeMs()).thenReturn(120L);

        stats.put("SIMPLE", simplePerfStats);
        stats.put("SECURE", securePerfStats);

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("\"totalSearches\": 23"),
                "Output should contain total 23 searches: " + output);
        assertTrue(output.contains("\"SIMPLE\":"),
                "Output should contain SIMPLE type: " + output);
        assertTrue(output.contains("\"SECURE\":"),
                "Output should contain SECURE type: " + output);
        assertTrue(output.contains("\"searchCount\": 15"),
                "Output should contain SIMPLE search count: " + output);
        assertTrue(output.contains("\"searchCount\": 8"),
                "Output should contain SECURE search count: " + output);
    }

    @Test
    @DisplayName("Should escape special characters in search type names in outputJson")
    void testOutputJson_EscapeSpecialCharacters() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        when(mockPerfStats.getSearches()).thenReturn(5L);
        when(mockPerfStats.getTotalTimeMs()).thenReturn(100L);
        when(mockPerfStats.getAverageTimeMs()).thenReturn(20.0);
        when(mockPerfStats.getCacheHitRate()).thenReturn(60.0);
        when(mockPerfStats.getAverageResults()).thenReturn(3.0);
        when(mockPerfStats.getMinTimeMs()).thenReturn(15L);
        when(mockPerfStats.getMaxTimeMs()).thenReturn(25L);

        // Search type name with special characters
        stats.put("TEST\"WITH\\QUOTES", mockPerfStats);

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("\"searchTypes\":"),
                "Output should contain searchTypes field: " + output);
        // FormatUtil.escapeJson should escape the quotes and backslashes
        assertFalse(output.contains("\"TEST\"WITH\\QUOTES\":"),
                "Output should escape special characters in search type name: " + output);
    }

    @Test
    @DisplayName("Should handle null search type key in outputJson")
    void testOutputJson_NullSearchTypeKey() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        when(mockPerfStats.getSearches()).thenReturn(7L);
        when(mockPerfStats.getTotalTimeMs()).thenReturn(210L);
        when(mockPerfStats.getAverageTimeMs()).thenReturn(30.0);
        when(mockPerfStats.getCacheHitRate()).thenReturn(70.0);
        when(mockPerfStats.getAverageResults()).thenReturn(5.0);
        when(mockPerfStats.getMinTimeMs()).thenReturn(25L);
        when(mockPerfStats.getMaxTimeMs()).thenReturn(35L);

        stats.put(null, mockPerfStats);

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("\"searchTypes\":"),
                "Output should contain searchTypes field: " + output);
        assertTrue(output.contains("\"\": {"),
                "Output should handle null key as empty string: " + output);
    }

    @Test
    @DisplayName("Should calculate overall cache hit rate correctly in outputJson")
    void testOutputJson_CalculateOverallCacheHitRate() throws Exception {
        Map<String, SearchMetrics.PerformanceStats> stats = new HashMap<>();

        SearchMetrics.PerformanceStats stats1 = mock(SearchMetrics.PerformanceStats.class);
        SearchMetrics.PerformanceStats stats2 = mock(SearchMetrics.PerformanceStats.class);

        // Stats1: 10 searches with 80% cache hit rate = 8 cache hits
        when(stats1.getSearches()).thenReturn(10L);
        when(stats1.getTotalTimeMs()).thenReturn(200L);
        when(stats1.getAverageTimeMs()).thenReturn(20.0);
        when(stats1.getCacheHitRate()).thenReturn(80.0);
        when(stats1.getAverageResults()).thenReturn(5.0);
        when(stats1.getMinTimeMs()).thenReturn(15L);
        when(stats1.getMaxTimeMs()).thenReturn(25L);

        // Stats2: 5 searches with 60% cache hit rate = 3 cache hits
        when(stats2.getSearches()).thenReturn(5L);
        when(stats2.getTotalTimeMs()).thenReturn(150L);
        when(stats2.getAverageTimeMs()).thenReturn(30.0);
        when(stats2.getCacheHitRate()).thenReturn(60.0);
        when(stats2.getAverageResults()).thenReturn(3.0);
        when(stats2.getMinTimeMs()).thenReturn(25L);
        when(stats2.getMaxTimeMs()).thenReturn(35L);

        // Overall: (8 + 3) / (10 + 5) = 11 / 15 = 0.733... = 73.3%
        stats.put("TYPE1", stats1);
        stats.put("TYPE2", stats2);

        when(mockSearchMetrics.getSearchTypeStats()).thenReturn(stats);

        Method method = SearchMetricsCommand.class.getDeclaredMethod("outputJson", SearchMetrics.class);
        method.setAccessible(true);
        method.invoke(command, mockSearchMetrics);

        String output = outContent.toString();
        assertTrue(output.contains("\"totalSearches\": 15"),
                "Output should contain total 15 searches: " + output);
        assertTrue(output.contains("\"overallCacheHitRate\":"),
                "Output should contain overall cache hit rate: " + output);
        // The overall cache hit rate should be approximately 0.733
        assertTrue(output.contains("\"overallCacheHitRate\": 0.7"),
                "Output should show correct overall cache hit rate starting with 0.7: " + output);
    }
}
