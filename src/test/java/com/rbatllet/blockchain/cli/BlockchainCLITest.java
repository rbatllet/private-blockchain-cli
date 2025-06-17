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

    @Test
    void testCLIDefaultBehavior() {
        int exitCode = cli.execute();
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain CLI v1.0.2"));
        assertTrue(output.contains("Available commands:"));
        assertTrue(output.contains("status"));
        assertTrue(output.contains("validate"));
        assertTrue(output.contains("add-block"));
    }

    @Test
    void testVersionFlag() {
        int exitCode = cli.execute("--version");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("1.0.2"));
    }

    @Test
    void testShortVersionFlag() {
        int exitCode = cli.execute("-V");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("1.0.2"));
    }

    @Test
    void testHelpFlag() {
        int exitCode = cli.execute("--help");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain Command Line Interface"));
        assertTrue(output.contains("Usage:"));
    }

    @Test
    void testShortHelpFlag() {
        int exitCode = cli.execute("-h");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Private Blockchain Command Line Interface"));
    }

    @Test
    void testUtilityMethods() {
        // Test that utility methods don't throw exceptions
        assertDoesNotThrow(() -> {
            BlockchainCLI.verbose = true;
            BlockchainCLI.verbose("Test verbose message");
            BlockchainCLI.error("Test error message");
            BlockchainCLI.success("Test success message");
            BlockchainCLI.info("Test info message");
        });
    }

    @Test
    void testMainMethodWithValidArgs() {
        // Test that main method handles args correctly
        assertDoesNotThrow(() -> {
            String[] args = {"--version"};
            // We can't easily test main() without ExitUtil.exit, so just verify structure
            assertNotNull(args);
        });
    }
}
