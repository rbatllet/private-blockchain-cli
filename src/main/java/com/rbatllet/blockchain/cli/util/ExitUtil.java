package com.rbatllet.blockchain.cli.util;

/**
 * Adapter class that delegates to the core ExitUtil implementation
 * @see com.rbatllet.blockchain.util.ExitUtil
 */
public class ExitUtil {
    
    /**
     * Disable System.exit() calls (for testing)
     */
    public static void disableExit() {
        com.rbatllet.blockchain.util.ExitUtil.disableExit();
    }
    
    /**
     * Enable System.exit() calls (normal operation)
     */
    public static void enableExit() {
        com.rbatllet.blockchain.util.ExitUtil.enableExit();
    }
    
    /**
     * Safe exit that can be disabled for testing
     * @param exitCode the exit code
     */
    public static void exit(int exitCode) {
        com.rbatllet.blockchain.util.ExitUtil.exit(exitCode);
    }
    
    /**
     * Check if exit is currently disabled
     * @return true if exit is disabled (test mode)
     */
    public static boolean isExitDisabled() {
        return com.rbatllet.blockchain.util.ExitUtil.isExitDisabled();
    }
    
    /**
     * Get the last exit code that was attempted (only works in test mode)
     * @return the last exit code, or 0 if none
     */
    public static int getLastExitCode() {
        return com.rbatllet.blockchain.util.ExitUtil.getLastExitCode();
    }
}
