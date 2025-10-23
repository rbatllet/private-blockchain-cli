package com.rbatllet.blockchain.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import picocli.CommandLine;
import com.rbatllet.blockchain.util.ExitUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Blockchain CLI
 */
public class BlockchainCLITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private CommandLine cli;
    private Path tempDirectory;

    @BeforeEach
    void setUp() throws Exception {
        // Redirect standard output for testing
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
        // Disable System.exit() for testing
        ExitUtil.disableExit();
        
        // Create temporary directory for test database
        tempDirectory = Files.createTempDirectory("blockchain-cli-test");
        System.setProperty("user.dir", tempDirectory.toString());
        
        // Initialize CLI
        cli = new CommandLine(new BlockchainCLI());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Restore standard output
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Re-enable System.exit() after testing
        ExitUtil.enableExit();
        
        // Clean up temporary files
        if (tempDirectory != null && Files.exists(tempDirectory)) {
            Files.walk(tempDirectory)
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.deleteIfExists(tempDirectory);
        }
    }

    /**
     * Get the real exit code considering ExitUtil state
     */
    private int getRealExitCode(int cliExitCode) {
        return ExitUtil.isExitDisabled() ? ExitUtil.getLastExitCode() : cliExitCode;
    }
    
    @Test
    void testCLIDefaultBehavior() {
        int exitCode = cli.execute();
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain CLI v1.0.5"));
        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("status"));
        assertTrue(output.contains("validate"));
        assertTrue(output.contains("add-block"));
    }

    @Test
    void testVersionFlag() {
        int exitCode = cli.execute("--version");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("1.0.5"));
    }

    @Test
    void testShortVersionFlag() {
        int exitCode = cli.execute("-V");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("1.0.5"));
    }

    @Test
    void testHelpFlag() {
        int exitCode = cli.execute("--help");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain Command Line Interface"));
        assertTrue(output.contains("Usage:"));
    }

    @Test
    void testShortHelpFlag() {
        int exitCode = cli.execute("-h");
        int realExitCode = getRealExitCode(exitCode);
        
        assertEquals(0, realExitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain Command Line Interface"));
    }

    @Test
    void testVerboseMethodWhenEnabled() {
        // Test verbose method when verbose flag is enabled
        BlockchainCLI.verbose = true;
        BlockchainCLI.verbose("Test verbose message");
        String output = outContent.toString();
        assertTrue(output.contains("Test verbose message"), "Output should contain verbose message");
        assertTrue(output.contains("🔍 [VERBOSE]"), "Output should contain verbose indicator");
    }
    
    @Test
    void testVerboseMethodWhenDisabled() {
        // Test verbose method when verbose flag is disabled
        BlockchainCLI.verbose = false;
        BlockchainCLI.verbose("Test verbose message");
        String output = outContent.toString();
        assertFalse(output.contains("Test verbose message"), "Output should not contain verbose message when disabled");
    }
    
    @Test
    void testErrorMethod() {
        // Test error method
        BlockchainCLI.error("Test error message");
        String error = errContent.toString();
        assertTrue(error.contains("Test error message"), "Error output should contain error message");
        assertTrue(error.contains("❌ Error:"), "Error output should contain error indicator");
    }
    
    @Test
    void testSuccessMethod() {
        // Test success method
        BlockchainCLI.success("Test success message");
        String output = outContent.toString();
        assertTrue(output.contains("Test success message"), "Output should contain success message");
        assertTrue(output.contains("✅"), "Output should contain success indicator");
    }
    
    @Test
    void testInfoMethod() {
        // Test info method
        BlockchainCLI.info("Test info message");
        String output = outContent.toString();
        assertTrue(output.contains("Test info message"), "Output should contain info message");
        assertTrue(output.contains("ℹ️"), "Output should contain info indicator");
    }

    @Test
    void testMainMethodWithValidArgs() {
        // Test that main method handles args correctly
        assertDoesNotThrow(() -> {
            String[] args = {"--version"};
            BlockchainCLI.main(args);
            String output = outContent.toString();
            assertTrue(output.contains("1.0.5"), "Output should contain version number");
        });
    }
    
    @Test
    void testMainMethodWithInvalidArgs() {
        // Test that main method handles invalid arguments correctly
        assertDoesNotThrow(() -> {
            // Create an invalid command that will trigger error handling
            String[] args = {"--invalid-flag"};
            BlockchainCLI.main(args);
            
            // The main method should handle this gracefully and not throw exceptions
            // We don't check specific output here, just that it doesn't crash
        });
    }
    
    @Test
    void testMainMethodWithNullArgs() {
        // Test that main method handles null arguments gracefully
        assertDoesNotThrow(() -> {
            // Pass null args to main method
            BlockchainCLI.main(null);
            
            // The main method should handle this gracefully and not throw exceptions
            // We don't check specific output here, just that it doesn't crash
        });
    }
    
    @Test
    void testVerboseWithMultipleMessages() {
        // Test verbose method with multiple messages
        BlockchainCLI.verbose = true;
        
        // Send multiple verbose messages
        BlockchainCLI.verbose("First verbose message");
        BlockchainCLI.verbose("Second verbose message");
        BlockchainCLI.verbose("Third verbose message with special chars: !@#$%^&*()");
        
        String output = outContent.toString();
        assertTrue(output.contains("First verbose message"), "Output should contain first verbose message");
        assertTrue(output.contains("Second verbose message"), "Output should contain second verbose message");
        assertTrue(output.contains("Third verbose message"), "Output should contain third verbose message");
    }
    
    @Test
    void testErrorWithMultipleMessages() {
        // Test error method with multiple messages
        BlockchainCLI.error("First error message");
        BlockchainCLI.error("Second error message");
        BlockchainCLI.error("Third error message with special chars: !@#$%^&*()");
        
        String error = errContent.toString();
        assertTrue(error.contains("First error message"), "Error output should contain first error message");
        assertTrue(error.contains("Second error message"), "Error output should contain second error message");
        assertTrue(error.contains("Third error message"), "Error output should contain third error message");
    }
    
    @Test
    void testSuccessWithMultipleMessages() {
        // Test success method with multiple messages
        BlockchainCLI.success("First success message");
        BlockchainCLI.success("Second success message");
        BlockchainCLI.success("Third success message with special chars: !@#$%^&*()");
        
        String output = outContent.toString();
        assertTrue(output.contains("First success message"), "Output should contain first success message");
        assertTrue(output.contains("Second success message"), "Output should contain second success message");
        assertTrue(output.contains("Third success message"), "Output should contain third success message");
    }
    
    @Test
    void testInfoWithMultipleMessages() {
        // Test info method with multiple messages
        BlockchainCLI.info("First info message");
        BlockchainCLI.info("Second info message");
        BlockchainCLI.info("Third info message with special chars: !@#$%^&*()");
        
        String output = outContent.toString();
        assertTrue(output.contains("First info message"), "Output should contain first info message");
        assertTrue(output.contains("Second info message"), "Output should contain second info message");
        assertTrue(output.contains("Third info message"), "Output should contain third info message");
    }
    
    @Test
    void testMainMethodWithEmptyArgs() {
        // Test that main method handles empty args correctly - should show help
        assertDoesNotThrow(() -> {
            BlockchainCLI.main(new String[0]);
            String output = outContent.toString();
            // Should show default help
            assertTrue(output.contains("Private Blockchain CLI"), "Should show CLI header");
            assertTrue(output.contains("Available Commands:"), "Should list available commands");
        });
    }
}
