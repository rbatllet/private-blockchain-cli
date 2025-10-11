package com.rbatllet.blockchain.cli.commands;

import com.rbatllet.blockchain.core.Blockchain;
import com.rbatllet.blockchain.entity.Block;
import com.rbatllet.blockchain.entity.OffChainData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for OffChainCommand private methods
 * Tests the 4 methods that were missing test coverage:
 * - outputRetrieveResultJson(Block, Object)
 * - outputStoreResultJson(Block, String)
 * - outputListJson(List)
 * - showOffChainStatistics(Blockchain)
 */
public class OffChainCommandMethodsTest {

    private OffChainCommand command;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private Block mockBlock;

    @Mock
    private OffChainData mockOffChainData;

    @Mock
    private Blockchain mockBlockchain;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        command = new OffChainCommand();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        closeable.close();
    }

    // ========================================
    // Tests for outputRetrieveResultJson
    // ========================================

    @Test
    @DisplayName("Should throw exception for null block in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_NullBlock() throws Exception {
        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, null, null),
                "Should throw exception for null block");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Block cannot be null"),
                "Exception message should mention block cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle valid block without off-chain data in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_ValidBlockNoOffChain() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(5L);
        when(mockBlock.getHash()).thenReturn("abc123def456");
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, null);

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"retrieve\""),
                "Output should contain retrieve operation: " + output);
        assertTrue(output.contains("\"success\": true"),
                "Output should contain success flag: " + output);
        assertTrue(output.contains("\"blockNumber\": 5"),
                "Output should contain block number: " + output);
        assertTrue(output.contains("\"blockHash\""),
                "Output should contain block hash: " + output);
        assertTrue(output.contains("abc123def456"),
                "Output should contain actual hash value: " + output);
    }

    @Test
    @DisplayName("Should handle valid block with off-chain data in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_ValidBlockWithOffChain() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(10L);
        when(mockBlock.getHash()).thenReturn("hash789xyz");
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(mockOffChainData.getFileSize()).thenReturn(1024L);
        when(mockOffChainData.getContentType()).thenReturn("application/pdf");
        when(mockOffChainData.getFilePath()).thenReturn("/path/to/file.pdf");

        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, null);

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"retrieve\""),
                "Output should contain retrieve operation: " + output);
        assertTrue(output.contains("\"blockNumber\": 10"),
                "Output should contain block number: " + output);
        assertTrue(output.contains("\"offChainData\""),
                "Output should contain offChainData object: " + output);
        assertTrue(output.contains("\"fileSize\": 1024"),
                "Output should contain file size: " + output);
        assertTrue(output.contains("\"contentType\": \"application/pdf\""),
                "Output should contain content type: " + output);
        assertTrue(output.contains("\"filePath\""),
                "Output should contain file path: " + output);
    }

    @Test
    @DisplayName("Should handle null hash in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_NullHash() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(1L);
        when(mockBlock.getHash()).thenReturn(null);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, null);

        String output = outContent.toString();
        assertTrue(output.contains("\"blockHash\": \"\""),
                "Output should contain empty hash for null: " + output);
    }

    @Test
    @DisplayName("Should handle null off-chain data object in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_NullOffChainDataObject() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(3L);
        when(mockBlock.getHash()).thenReturn("hash123");
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(null);

        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, null);

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"retrieve\""),
                "Output should contain retrieve operation: " + output);
        assertFalse(output.contains("\"offChainData\""),
                "Output should NOT contain offChainData when data is null: " + output);
    }

    @Test
    @DisplayName("Should escape special characters in hash in outputRetrieveResultJson")
    void testOutputRetrieveResultJson_EscapeSpecialChars() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(2L);
        when(mockBlock.getHash()).thenReturn("hash\"with\\quotes");
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputRetrieveResultJson", Block.class, Object.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, null);

        String output = outContent.toString();
        assertTrue(output.contains("\"blockHash\""),
                "Output should contain blockHash field: " + output);
        // FormatUtil.escapeJson should escape the quotes
        assertFalse(output.contains("\"hash\"with\\quotes\""),
                "Output should escape special characters: " + output);
    }

    // ========================================
    // Tests for outputStoreResultJson
    // ========================================

    @Test
    @DisplayName("Should throw exception for null block in outputStoreResultJson")
    void testOutputStoreResultJson_NullBlock() throws Exception {
        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, null, "file.txt"),
                "Should throw exception for null block");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Block cannot be null"),
                "Exception message should mention block cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null original file in outputStoreResultJson")
    void testOutputStoreResultJson_NullOriginalFile() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(1L);

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, mockBlock, null),
                "Should throw exception for null original file");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Original file cannot be null"),
                "Exception message should mention original file cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle valid block without off-chain data in outputStoreResultJson")
    void testOutputStoreResultJson_ValidBlockNoOffChain() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(7L);
        when(mockBlock.getHash()).thenReturn("store123");
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, "test-file.pdf");

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"store\""),
                "Output should contain store operation: " + output);
        assertTrue(output.contains("\"success\": true"),
                "Output should contain success flag: " + output);
        assertTrue(output.contains("\"blockNumber\": 7"),
                "Output should contain block number: " + output);
        assertTrue(output.contains("\"originalFile\": \"test-file.pdf\""),
                "Output should contain original file name: " + output);
        assertTrue(output.contains("\"encrypted\": false"),
                "Output should contain encrypted flag: " + output);
    }

    @Test
    @DisplayName("Should handle valid block with off-chain data in outputStoreResultJson")
    void testOutputStoreResultJson_ValidBlockWithOffChain() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(15L);
        when(mockBlock.getHash()).thenReturn("store456");
        when(mockBlock.getIsEncrypted()).thenReturn(true);
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(mockOffChainData.getFileSize()).thenReturn(2048L);
        when(mockOffChainData.getContentType()).thenReturn("text/plain");

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, "document.txt");

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"store\""),
                "Output should contain store operation: " + output);
        assertTrue(output.contains("\"blockNumber\": 15"),
                "Output should contain block number: " + output);
        assertTrue(output.contains("\"encrypted\": true"),
                "Output should contain encrypted true: " + output);
        assertTrue(output.contains("\"offChainData\""),
                "Output should contain offChainData object: " + output);
        assertTrue(output.contains("\"fileSize\": 2048"),
                "Output should contain file size: " + output);
        assertTrue(output.contains("\"contentType\": \"text/plain\""),
                "Output should contain content type: " + output);
    }

    @Test
    @DisplayName("Should handle null encryption flag in outputStoreResultJson")
    void testOutputStoreResultJson_NullEncryption() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(4L);
        when(mockBlock.getHash()).thenReturn("hash444");
        when(mockBlock.getIsEncrypted()).thenReturn(null);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, "file.dat");

        String output = outContent.toString();
        assertTrue(output.contains("\"encrypted\": false"),
                "Output should default to false for null encryption: " + output);
    }

    @Test
    @DisplayName("Should handle null off-chain data object in outputStoreResultJson")
    void testOutputStoreResultJson_NullOffChainDataObject() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(6L);
        when(mockBlock.getHash()).thenReturn("hash666");
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(null);

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, "test.bin");

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"store\""),
                "Output should contain store operation: " + output);
        assertFalse(output.contains("\"offChainData\""),
                "Output should NOT contain offChainData when data is null: " + output);
    }

    @Test
    @DisplayName("Should escape special characters in file name in outputStoreResultJson")
    void testOutputStoreResultJson_EscapeFileName() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(8L);
        when(mockBlock.getHash()).thenReturn("hash888");
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("outputStoreResultJson", Block.class, String.class);
        method.setAccessible(true);
        method.invoke(command, mockBlock, "file\"with\\quotes.txt");

        String output = outContent.toString();
        assertTrue(output.contains("\"originalFile\""),
                "Output should contain originalFile field: " + output);
        // FormatUtil.escapeJson should escape the quotes
        assertFalse(output.contains("\"file\"with\\quotes.txt\""),
                "Output should escape special characters in file name: " + output);
    }

    // ========================================
    // Tests for outputListJson
    // ========================================

    @Test
    @DisplayName("Should throw exception for null list in outputListJson")
    void testOutputListJson_NullList() throws Exception {
        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, (List<Block>) null),
                "Should throw exception for null list");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Off-chain blocks list cannot be null"),
                "Exception message should mention list cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle empty list in outputListJson")
    void testOutputListJson_EmptyList() throws Exception {
        List<Block> emptyList = new ArrayList<>();

        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);
        method.invoke(command, emptyList);

        String output = outContent.toString();
        assertTrue(output.contains("\"operation\": \"list\""),
                "Output should contain list operation: " + output);
        assertTrue(output.contains("\"totalBlocks\": 0"),
                "Output should contain 0 total blocks: " + output);
        assertTrue(output.contains("\"blocks\": ["),
                "Output should contain empty blocks array: " + output);
    }

    @Test
    @DisplayName("Should handle list with single block in outputListJson")
    void testOutputListJson_SingleBlock() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(12L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(mockBlock.getHash()).thenReturn("single123");
        when(mockBlock.getIsEncrypted()).thenReturn(true);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("\"totalBlocks\": 1"),
                "Output should contain 1 total block: " + output);
        assertTrue(output.contains("\"blockNumber\": 12"),
                "Output should contain block number: " + output);
        assertTrue(output.contains("\"encrypted\": true"),
                "Output should contain encrypted flag: " + output);
    }

    @Test
    @DisplayName("Should handle list with multiple blocks in outputListJson")
    void testOutputListJson_MultipleBlocks() throws Exception {
        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        when(mockBlock1.getBlockNumber()).thenReturn(20L);
        when(mockBlock1.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(mockBlock1.getHash()).thenReturn("multi123");
        when(mockBlock1.getIsEncrypted()).thenReturn(false);
        when(mockBlock1.hasOffChainData()).thenReturn(false);

        when(mockBlock2.getBlockNumber()).thenReturn(21L);
        when(mockBlock2.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 11, 0));
        when(mockBlock2.getHash()).thenReturn("multi456");
        when(mockBlock2.getIsEncrypted()).thenReturn(true);
        when(mockBlock2.hasOffChainData()).thenReturn(true);
        when(mockBlock2.getOffChainData()).thenReturn(mockOffChainData);
        when(mockOffChainData.getFileSize()).thenReturn(4096L);
        when(mockOffChainData.getContentType()).thenReturn("image/png");

        List<Block> blocks = Arrays.asList(mockBlock1, mockBlock2);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("\"totalBlocks\": 2"),
                "Output should contain 2 total blocks: " + output);
        assertTrue(output.contains("\"blockNumber\": 20"),
                "Output should contain first block number: " + output);
        assertTrue(output.contains("\"blockNumber\": 21"),
                "Output should contain second block number: " + output);
        assertTrue(output.contains("\"fileSize\": 4096"),
                "Output should contain file size for second block: " + output);
    }

    @Test
    @DisplayName("Should handle null blocks in list in outputListJson")
    void testOutputListJson_NullBlocksInList() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(30L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 15, 0));
        when(mockBlock.getHash()).thenReturn("null123");
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(mockBlock, null, mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        // Should skip null blocks - list has 3 elements but only 2 non-null
        assertTrue(output.contains("\"totalBlocks\": 3"),
                "Output should contain 3 total blocks (including null): " + output);
        // Should have valid JSON structure
        assertTrue(output.contains("\"blocks\": ["),
                "Output should contain blocks array: " + output);
    }

    @Test
    @DisplayName("Should escape special characters in outputListJson")
    void testOutputListJson_EscapeSpecialChars() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(40L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 16, 0));
        when(mockBlock.getHash()).thenReturn("hash\"with\\quotes");
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(mockOffChainData.getFileSize()).thenReturn(512L);
        when(mockOffChainData.getContentType()).thenReturn("type\"with\\quotes");

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListJson", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("\"hash\""),
                "Output should contain hash field: " + output);
        assertTrue(output.contains("\"contentType\""),
                "Output should contain contentType field: " + output);
        // FormatUtil.escapeJson should escape the quotes
        assertFalse(output.contains("\"hash\"with\\quotes\""),
                "Output should escape special characters in hash: " + output);
    }

    // ========================================
    // Tests for showOffChainStatistics
    // ========================================

    @Test
    @DisplayName("Should throw exception for null blockchain in showOffChainStatistics")
    void testShowOffChainStatistics_NullBlockchain() throws Exception {
        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, (Blockchain) null),
                "Should throw exception for null blockchain");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Blockchain cannot be null"),
                "Exception message should mention blockchain cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle empty blockchain in showOffChainStatistics")
    void testShowOffChainStatistics_EmptyBlockchain() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(0L);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);
        method.invoke(command, mockBlockchain);

        String output = outContent.toString();
        assertTrue(output.contains("Off-Chain Storage Statistics"),
                "Output should contain statistics header: " + output);
        assertTrue(output.contains("Total blocks: 0"),
                "Output should show 0 total blocks: " + output);
        assertTrue(output.contains("Off-chain blocks: 0"),
                "Output should show 0 off-chain blocks: " + output);
    }

    @Test
    @DisplayName("Should handle blockchain with blocks but no off-chain data in showOffChainStatistics")
    void testShowOffChainStatistics_NoOffChainBlocks() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(5L);
        when(mockBlockchain.getBlock(anyLong())).thenReturn(mockBlock);
        when(mockBlock.hasOffChainData()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);
        method.invoke(command, mockBlockchain);

        String output = outContent.toString();
        assertTrue(output.contains("Total blocks: 5"),
                "Output should show 5 total blocks: " + output);
        assertTrue(output.contains("Off-chain blocks: 0"),
                "Output should show 0 off-chain blocks: " + output);
        assertTrue(output.contains("Encrypted off-chain: 0"),
                "Output should show 0 encrypted off-chain: " + output);
    }

    @Test
    @DisplayName("Should handle blockchain with off-chain blocks in showOffChainStatistics")
    void testShowOffChainStatistics_WithOffChainBlocks() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(10L);

        // Create two different mock blocks for different behaviors
        Block blockWithOffChain1 = mock(Block.class);
        Block blockWithOffChain2 = mock(Block.class);
        Block blockWithoutOffChain = mock(Block.class);

        when(blockWithOffChain1.hasOffChainData()).thenReturn(true);
        OffChainData mockOffChainData1 = mock(OffChainData.class);
        when(blockWithOffChain1.getOffChainData()).thenReturn(mockOffChainData1);
        when(blockWithOffChain1.getIsEncrypted()).thenReturn(true);

        when(blockWithOffChain2.hasOffChainData()).thenReturn(true);
        OffChainData mockOffChainData2 = mock(OffChainData.class);
        when(blockWithOffChain2.getOffChainData()).thenReturn(mockOffChainData2);
        when(blockWithOffChain2.getIsEncrypted()).thenReturn(false);

        when(blockWithoutOffChain.hasOffChainData()).thenReturn(false);

        when(mockOffChainData1.getFileSize()).thenReturn(1000L);
        when(mockOffChainData2.getFileSize()).thenReturn(2000L);

        // Mock getBlock to return specific blocks for specific indices
        when(mockBlockchain.getBlock(1L)).thenReturn(blockWithOffChain1);
        when(mockBlockchain.getBlock(2L)).thenReturn(blockWithOffChain2);
        when(mockBlockchain.getBlock(3L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(4L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(5L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(6L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(7L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(8L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(9L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(10L)).thenReturn(blockWithoutOffChain);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);
        method.invoke(command, mockBlockchain);

        String output = outContent.toString();
        assertTrue(output.contains("Total blocks: 10"),
                "Output should show 10 total blocks: " + output);
        assertTrue(output.contains("Off-chain blocks: 2"),
                "Output should show 2 off-chain blocks: " + output);
        assertTrue(output.contains("Encrypted off-chain: 1"),
                "Output should show 1 encrypted off-chain: " + output);
        assertTrue(output.contains("Unencrypted off-chain: 1"),
                "Output should show 1 unencrypted off-chain: " + output);
    }

    @Test
    @DisplayName("Should handle null off-chain data object in showOffChainStatistics")
    void testShowOffChainStatistics_NullOffChainDataObject() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(3L);
        when(mockBlockchain.getBlock(anyLong())).thenReturn(mockBlock);
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(null);
        when(mockBlock.getIsEncrypted()).thenReturn(false);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);
        method.invoke(command, mockBlockchain);

        String output = outContent.toString();
        assertTrue(output.contains("Total blocks: 3"),
                "Output should show 3 total blocks: " + output);
        // Should handle null off-chain data gracefully
        assertFalse(output.contains("null"),
                "Output should not contain null values: " + output);
    }

    @Test
    @DisplayName("Should calculate percentages correctly in showOffChainStatistics")
    void testShowOffChainStatistics_PercentageCalculations() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(4L);

        Block blockWithOffChain = mock(Block.class);
        Block blockWithoutOffChain = mock(Block.class);

        when(blockWithOffChain.hasOffChainData()).thenReturn(true);
        when(blockWithOffChain.getOffChainData()).thenReturn(mockOffChainData);
        when(blockWithOffChain.getIsEncrypted()).thenReturn(true);
        when(mockOffChainData.getFileSize()).thenReturn(5000L);

        when(blockWithoutOffChain.hasOffChainData()).thenReturn(false);

        // First 2 blocks have off-chain data, rest don't
        when(mockBlockchain.getBlock(1L)).thenReturn(blockWithOffChain);
        when(mockBlockchain.getBlock(2L)).thenReturn(blockWithOffChain);
        when(mockBlockchain.getBlock(3L)).thenReturn(blockWithoutOffChain);
        when(mockBlockchain.getBlock(4L)).thenReturn(blockWithoutOffChain);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);
        method.invoke(command, mockBlockchain);

        String output = outContent.toString();
        assertTrue(output.contains("Total blocks: 4"),
                "Output should show 4 total blocks: " + output);
        assertTrue(output.contains("Off-chain blocks: 2"),
                "Output should show 2 off-chain blocks: " + output);
        assertTrue(output.contains("Off-chain usage rate:"),
                "Output should show off-chain usage rate: " + output);
        assertTrue(output.contains("Average off-chain size:"),
                "Output should show average size: " + output);
        assertTrue(output.contains("Off-chain encryption rate:"),
                "Output should show encryption rate: " + output);
    }

    @Test
    @DisplayName("Should handle division by zero in showOffChainStatistics")
    void testShowOffChainStatistics_DivisionByZero() throws Exception {
        when(mockBlockchain.getBlockCount()).thenReturn(0L);

        Method method = OffChainCommand.class.getDeclaredMethod("showOffChainStatistics", Blockchain.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(command, mockBlockchain),
                "Should not throw exception for empty blockchain");

        String output = outContent.toString();
        assertTrue(output.contains("Total blocks: 0"),
                "Output should handle empty blockchain gracefully: " + output);
    }

    // ========================================
    // Tests for outputListText
    // ========================================

    @Test
    @DisplayName("Should throw exception for null list in outputListText")
    void testOutputListText_NullList() throws Exception {
        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(command, (List<Block>) null),
                "Should throw exception for null list");

        // Unwrap InvocationTargetException to get the real cause
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Exception should have a cause");
        assertTrue(cause instanceof IllegalArgumentException,
                "Cause should be IllegalArgumentException but was: " + cause.getClass().getName());
        assertTrue(cause.getMessage().contains("Off-chain blocks list cannot be null"),
                "Exception message should mention list cannot be null: " + cause.getMessage());
    }

    @Test
    @DisplayName("Should handle empty list in outputListText")
    void testOutputListText_EmptyList() throws Exception {
        List<Block> emptyList = new ArrayList<>();

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, emptyList);

        String output = outContent.toString();
        assertTrue(output.contains("Off-Chain Data Blocks"),
                "Output should contain header: " + output);
        assertTrue(output.contains("No blocks with off-chain data found"),
                "Output should show no blocks message: " + output);
    }

    @Test
    @DisplayName("Should handle single block in outputListText")
    void testOutputListText_SingleBlock() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(42L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 6, 15, 10, 30));
        when(mockBlock.getHash()).thenReturn("abc123def456");
        when(mockBlock.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Found 1 block(s) with off-chain data"),
                "Output should show 1 block: " + output);
        assertTrue(output.contains("Block #42"),
                "Output should show block number: " + output);
    }

    @Test
    @DisplayName("Should handle multiple blocks in outputListText")
    void testOutputListText_MultipleBlocks() throws Exception {
        Block block1 = mock(Block.class);
        Block block2 = mock(Block.class);

        when(block1.getBlockNumber()).thenReturn(10L);
        when(block1.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(block1.getHash()).thenReturn("hash1");
        when(block1.hasOffChainData()).thenReturn(false);

        when(block2.getBlockNumber()).thenReturn(20L);
        when(block2.getTimestamp()).thenReturn(LocalDateTime.of(2024, 1, 2, 11, 0));
        when(block2.getHash()).thenReturn("hash2");
        when(block2.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(block1, block2);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Found 2 block(s) with off-chain data"),
                "Output should show 2 blocks: " + output);
        assertTrue(output.contains("Block #10"),
                "Output should show first block: " + output);
        assertTrue(output.contains("Block #20"),
                "Output should show second block: " + output);
    }

    @Test
    @DisplayName("Should handle block with off-chain data in outputListText")
    void testOutputListText_BlockWithOffChainData() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(100L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 3, 15, 14, 30));
        when(mockBlock.getHash()).thenReturn("offchain123");
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(mockBlock.getIsEncrypted()).thenReturn(true);
        when(mockOffChainData.getFileSize()).thenReturn(5120L);
        when(mockOffChainData.getContentType()).thenReturn("application/pdf");

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Block #100"),
                "Output should show block number: " + output);
        assertTrue(output.contains("Size:"),
                "Output should show size: " + output);
        assertTrue(output.contains("Type: application/pdf"),
                "Output should show content type: " + output);
        assertTrue(output.contains("Encrypted: Yes"),
                "Output should show encrypted status: " + output);
    }

    @Test
    @DisplayName("Should handle block without off-chain data in outputListText")
    void testOutputListText_BlockWithoutOffChainData() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(50L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 2, 10, 9, 15));
        when(mockBlock.getHash()).thenReturn("nooffchain");
        when(mockBlock.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Block #50"),
                "Output should show block number: " + output);
        assertFalse(output.contains("Size:"),
                "Output should NOT show size for block without off-chain data: " + output);
        assertFalse(output.contains("Type:"),
                "Output should NOT show type for block without off-chain data: " + output);
    }

    @Test
    @DisplayName("Should handle null off-chain data object in outputListText")
    void testOutputListText_NullOffChainDataObject() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(75L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 4, 20, 16, 45));
        when(mockBlock.getHash()).thenReturn("nulldata");
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(null);

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Block #75"),
                "Output should show block number: " + output);
        assertFalse(output.contains("Size:"),
                "Output should NOT show size when off-chain data is null: " + output);
    }

    @Test
    @DisplayName("Should handle null blocks in list in outputListText")
    void testOutputListText_NullBlocksInList() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(88L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 5, 5, 12, 0));
        when(mockBlock.getHash()).thenReturn("valid");
        when(mockBlock.hasOffChainData()).thenReturn(false);

        List<Block> blocks = Arrays.asList(mockBlock, null, mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Found 3 block(s) with off-chain data"),
                "Output should show total count including nulls: " + output);
        assertTrue(output.contains("Block #88"),
                "Output should show valid blocks: " + output);
        // Should skip null blocks without crashing
    }

    @Test
    @DisplayName("Should handle null content type in outputListText")
    void testOutputListText_NullContentType() throws Exception {
        when(mockBlock.getBlockNumber()).thenReturn(99L);
        when(mockBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 7, 1, 8, 30));
        when(mockBlock.getHash()).thenReturn("nulltype");
        when(mockBlock.hasOffChainData()).thenReturn(true);
        when(mockBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(mockBlock.getIsEncrypted()).thenReturn(false);
        when(mockOffChainData.getFileSize()).thenReturn(2048L);
        when(mockOffChainData.getContentType()).thenReturn(null);

        List<Block> blocks = Arrays.asList(mockBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Block #99"),
                "Output should show block number: " + output);
        assertTrue(output.contains("Type: Unknown"),
                "Output should show 'Unknown' for null content type: " + output);
    }

    @Test
    @DisplayName("Should handle encrypted and unencrypted blocks in outputListText")
    void testOutputListText_EncryptedAndUnencrypted() throws Exception {
        Block encryptedBlock = mock(Block.class);
        Block unencryptedBlock = mock(Block.class);

        when(encryptedBlock.getBlockNumber()).thenReturn(111L);
        when(encryptedBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 8, 10, 14, 0));
        when(encryptedBlock.getHash()).thenReturn("encrypted");
        when(encryptedBlock.hasOffChainData()).thenReturn(true);
        when(encryptedBlock.getOffChainData()).thenReturn(mockOffChainData);
        when(encryptedBlock.getIsEncrypted()).thenReturn(true);

        when(unencryptedBlock.getBlockNumber()).thenReturn(222L);
        when(unencryptedBlock.getTimestamp()).thenReturn(LocalDateTime.of(2024, 8, 11, 15, 0));
        when(unencryptedBlock.getHash()).thenReturn("unencrypted");
        when(unencryptedBlock.hasOffChainData()).thenReturn(true);
        OffChainData mockOffChainData2 = mock(OffChainData.class);
        when(unencryptedBlock.getOffChainData()).thenReturn(mockOffChainData2);
        when(unencryptedBlock.getIsEncrypted()).thenReturn(false);

        when(mockOffChainData.getFileSize()).thenReturn(1024L);
        when(mockOffChainData.getContentType()).thenReturn("text/plain");
        when(mockOffChainData2.getFileSize()).thenReturn(2048L);
        when(mockOffChainData2.getContentType()).thenReturn("image/png");

        List<Block> blocks = Arrays.asList(encryptedBlock, unencryptedBlock);

        Method method = OffChainCommand.class.getDeclaredMethod("outputListText", List.class);
        method.setAccessible(true);
        method.invoke(command, blocks);

        String output = outContent.toString();
        assertTrue(output.contains("Block #111"),
                "Output should show encrypted block: " + output);
        assertTrue(output.contains("Block #222"),
                "Output should show unencrypted block: " + output);
        assertTrue(output.contains("Encrypted: Yes"),
                "Output should show Yes for encrypted: " + output);
        assertTrue(output.contains("Encrypted: No"),
                "Output should show No for unencrypted: " + output);
    }
}
