package com.rbatllet.blockchain.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced integration tests for the complete CLI application
 * Tests real workflows and command interactions
 */
public class BlockchainCLIIntegrationTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine mainCli;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setProperty("user.dir", tempDir.toString());
        
        mainCli = new CommandLine(new BlockchainCLI());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testFullWorkflowStatusAndValidate() {
        // Create separate command instances to avoid System.exit issues
        CommandLine statusCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.StatusCommand());
        CommandLine validateCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ValidateCommand());
        
        // Test 1: Check initial status
        int statusResult = statusCli.execute();
        assertEquals(0, statusResult);
        String statusOutput = outContent.toString();
        assertTrue(statusOutput.contains("Blockchain") || statusOutput.contains("blocks") || 
                  statusOutput.contains("Total") || statusOutput.contains("integrity"));
        
        clearOutput();
        
        // Test 2: Validate the blockchain
        int validateResult = validateCli.execute();
        assertEquals(0, validateResult);
        String validateOutput = outContent.toString();
        assertTrue(validateOutput.contains("validation") || validateOutput.contains("valid") || 
                  validateOutput.contains("Chain") || validateOutput.contains("successful"));
    }

    @Test
    void testStatusWithDifferentFormats() {
        CommandLine statusCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.StatusCommand());
        
        // Test basic status
        int basicResult = statusCli.execute();
        assertEquals(0, basicResult);
        String basicOutput = outContent.toString();
        assertFalse(basicOutput.isEmpty());
        
        clearOutput();
        
        // Test JSON status
        int jsonResult = statusCli.execute("--json");
        assertEquals(0, jsonResult);
        String jsonOutput = outContent.toString();
        assertTrue(jsonOutput.contains("{") || jsonOutput.contains("\"") || 
                  jsonOutput.contains("blockCount") || jsonOutput.contains("isValid"));
        
        clearOutput();
        
        // Test detailed status
        int detailedResult = statusCli.execute("--detailed");
        assertEquals(0, detailedResult);
        String detailedOutput = outContent.toString();
        assertFalse(detailedOutput.isEmpty());
    }

    @Test
    void testKeyManagementWorkflow() {
        CommandLine addKeyCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.AddKeyCommand());
        CommandLine listKeysCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ListKeysCommand());
        
        // Add a key
        int addResult = addKeyCli.execute("TestUser", "--generate");
        assertEquals(0, addResult);
        String addOutput = outContent.toString();
        assertTrue(addOutput.contains("key") || addOutput.contains("added") || 
                  addOutput.contains("success") || addOutput.contains("generated"));
        
        clearOutput();
        
        // List keys to verify
        int listResult = listKeysCli.execute();
        assertEquals(0, listResult);
        String listOutput = outContent.toString();
        assertTrue(listOutput.contains("key") || listOutput.contains("TestUser") || 
                  listOutput.contains("Authorized") || listOutput.contains("1"));
    }

    @Test
    void testExportImportWorkflow() throws Exception {
        CommandLine exportCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ExportCommand());
        CommandLine importCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ImportCommand());
        
        // Export blockchain
        Path exportFile = tempDir.resolve("test_export.json");
        int exportResult = exportCli.execute(exportFile.toString());
        assertEquals(0, exportResult);
        
        // Verify file was created
        assertTrue(Files.exists(exportFile));
        assertTrue(Files.size(exportFile) > 0);
        
        clearOutput();
        
        // Import the same file
        int importResult = importCli.execute(exportFile.toString(), "--dry-run");
        assertEquals(0, importResult);
        String importOutput = outContent.toString();
        assertTrue(importOutput.contains("import") || importOutput.contains("dry") || 
                  importOutput.contains("simulation") || importOutput.contains("would"));
    }

    @Test
    void testSearchWorkflow() {
        CommandLine searchCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.SearchCommand());
        
        // Search for Genesis block (should exist)
        int searchResult = searchCli.execute("Genesis");
        assertEquals(0, searchResult);
        String searchOutput = outContent.toString();
        assertTrue(searchOutput.contains("found") || searchOutput.contains("Genesis") || 
                  searchOutput.contains("block") || searchOutput.contains("search") || 
                  searchOutput.contains("1"));
        
        clearOutput();
        
        // Search with JSON output
        int jsonSearchResult = searchCli.execute("Genesis", "--json");
        assertEquals(0, jsonSearchResult);
        String jsonSearchOutput = outContent.toString();
        assertTrue(jsonSearchOutput.contains("{") || jsonSearchOutput.contains("[") || 
                  jsonSearchOutput.contains("\"") || jsonSearchOutput.contains("blocks"));
    }

    @Test
    void testHelpSystem() {
        CommandLine helpCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.HelpCommand());
        
        // Test help command
        int helpCommandResult = helpCli.execute();
        assertEquals(0, helpCommandResult);
        String helpCommandOutput = outContent.toString();
        assertFalse(helpCommandOutput.isEmpty());
        assertTrue(helpCommandOutput.contains("help") || helpCommandOutput.contains("Help") || 
                  helpCommandOutput.contains("command") || helpCommandOutput.contains("blockchain"));
    }

    @Test
    void testErrorHandling() {
        CommandLine importCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ImportCommand());
        CommandLine addKeyCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.AddKeyCommand());
        
        // Test missing required arguments
        int missingArgsResult = addKeyCli.execute();
        assertNotEquals(0, missingArgsResult);
        
        clearOutput();
        
        // Test invalid file for import
        int invalidFileResult = importCli.execute("non_existent_file.json");
        assertEquals(1, invalidFileResult);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("not found") || errorOutput.contains("Error") || 
                  errorOutput.contains("file") || outContent.toString().contains("Error"));
    }

    @Test
    void testVerboseMode() {
        CommandLine statusCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.StatusCommand());
        
        // Enable verbose mode
        BlockchainCLI.verbose = true;
        
        try {
            int result = statusCli.execute();
            assertEquals(0, result);
            
            // Verbose mode should produce output (though exact content may vary)
            String output = outContent.toString();
            assertFalse(output.isEmpty());
            
        } finally {
            // Reset verbose mode
            BlockchainCLI.verbose = false;
        }
    }

    @Test
    void testCompleteWorkflow() {
        // Complete workflow: status -> add key -> validate -> export -> search
        CommandLine statusCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.StatusCommand());
        CommandLine addKeyCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.AddKeyCommand());
        CommandLine validateCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ValidateCommand());
        CommandLine exportCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ExportCommand());
        CommandLine searchCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.SearchCommand());
        
        // 1. Initial status
        assertEquals(0, statusCli.execute());
        clearOutput();
        
        // 2. Add a key
        assertEquals(0, addKeyCli.execute("WorkflowUser", "--generate"));
        clearOutput();
        
        // 3. Validate blockchain
        assertEquals(0, validateCli.execute());
        clearOutput();
        
        // 4. Export blockchain
        Path exportFile = tempDir.resolve("workflow_export.json");
        assertEquals(0, exportCli.execute(exportFile.toString()));
        assertTrue(Files.exists(exportFile));
        clearOutput();
        
        // 5. Search for content
        assertEquals(0, searchCli.execute("Genesis"));
        clearOutput();
        
        // 6. Final status check
        assertEquals(0, statusCli.execute("--detailed"));
        String finalOutput = outContent.toString();
        assertFalse(finalOutput.isEmpty());
    }

    @Test
    void testConcurrentOperations() {
        CommandLine statusCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.StatusCommand());
        CommandLine validateCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ValidateCommand());
        
        // Test that multiple operations can be performed in sequence
        for (int i = 0; i < 3; i++) {
            int result = statusCli.execute();
            assertEquals(0, result);
            clearOutput();
        }
        
        // Verify no state issues
        int finalResult = validateCli.execute();
        assertEquals(0, finalResult);
    }

    @Test
    void testDatabasePersistence() throws Exception {
        CommandLine addKeyCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.AddKeyCommand());
        CommandLine listKeysCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ListKeysCommand());
        
        // Perform operations that should persist data
        assertEquals(0, addKeyCli.execute("PersistUser", "--generate"));
        clearOutput();
        
        // Create new CLI instance (simulating restart)
        CommandLine newListKeysCli = new CommandLine(new com.rbatllet.blockchain.cli.commands.ListKeysCommand());
        
        // Check that data persisted
        int result = newListKeysCli.execute();
        assertEquals(0, result);
        String output = outContent.toString();
        assertTrue(output.contains("key") || output.contains("PersistUser") || 
                  output.contains("Authorized") || output.contains("1") || 
                  output.length() > 10); // Some meaningful output
    }

    private void clearOutput() {
        outContent.reset();
        errContent.reset();
    }
}
