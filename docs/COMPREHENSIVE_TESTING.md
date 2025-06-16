# 🧪 Comprehensive Test Suite for Secure Key Management

This document describes the extensive test suite created for the secure key management functionality in the privateBlockchain-cli project.

## 📋 Table of Contents

- [Overview](#overview)
- [Test Structure](#test-structure)
- [Test Classes](#test-classes)
- [Running Tests](#running-tests)
- [Test Coverage](#test-coverage)
- [Troubleshooting](#troubleshooting)

## 🚀 Overview

The secure key management test suite provides comprehensive coverage for all new security features, including:

- **Secure private key storage** with AES encryption
- **Password validation** and security requirements
- **Command-line interface** integration
- **Multi-mode operation** (production vs demo)
- **Error handling** and edge cases
- **Performance** and stress testing
- **Concurrency** and thread safety

## 🏗️ Test Structure

### Test Categories

| Category | Purpose | Classes |
|----------|---------|---------|
| **Unit Tests** | Test individual components | SecureKeyStorageTest, PasswordUtilTest |
| **Enhanced Tests** | Test new functionality | AddBlockCommandEnhancedTest, ManageKeysCommandTest |
| **Advanced Tests** | Edge cases and security | SecureKeyStorageAdvancedTest |
| **Integration Tests** | End-to-end workflows | SecureKeyManagementIntegrationTest |
| **Stress Tests** | Performance and limits | SecureKeyManagementStressTest |

### Test Environment

- **Isolated**: Each test uses `@TempDir` for clean filesystem state
- **Thread-safe**: Tests handle concurrent operations safely
- **Comprehensive**: Covers success paths, error conditions, and edge cases
- **Realistic**: Uses actual cryptographic operations and real data

## 📚 Test Classes

### 1. ManageKeysCommandTest
**Purpose**: Test key management operations

**Key Features**:
- List stored keys (empty and populated)
- Check key existence
- Delete keys with confirmation
- JSON output validation
- Error handling for non-existent keys

**Example Test**:
```java
@Test
void testListKeysWithStoredKeys() {
    // Store test keys
    SecureKeyStorage.savePrivateKey("User1", keyPair1.getPrivate(), testPassword);
    SecureKeyStorage.savePrivateKey("User2", keyPair2.getPrivate(), testPassword);
    
    // Test list command
    int exitCode = cli.execute("--list");
    assertEquals(0, exitCode);
    
    String output = outContent.toString();
    assertTrue(output.contains("User1"));
    assertTrue(output.contains("User2"));
    assertTrue(output.contains("Total: 2"));
}
```

### 2. PasswordUtilTest
**Purpose**: Test password validation and security

**Key Features**:
- Password strength validation
- Boundary condition testing
- Unicode character support
- Error message validation
- Console input simulation

**Security Tests**:
- Minimum 8 characters
- At least one letter and one number
- Maximum 128 characters
- Special character handling
- Empty/null password rejection

### 3. SecureKeyStorageAdvancedTest
**Purpose**: Advanced security and edge case testing

**Key Features**:
- **Concurrency**: 10 threads, 5 operations each
- **Performance**: 50 keys creation/loading benchmark
- **Security**: Special characters, long passwords
- **Edge Cases**: Corrupted files, resource limits
- **Memory**: Large key testing without leaks

**Concurrency Test Example**:
```java
@Test
void testConcurrentKeyOperations() throws InterruptedException {
    final int threadCount = 10;
    final CountDownLatch latch = new CountDownLatch(threadCount);
    final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    
    // Each thread performs save/load/delete operations
    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            try {
                // Concurrent operations...
                assertTrue(SecureKeyStorage.savePrivateKey(owner, privateKey, password));
                assertNotNull(SecureKeyStorage.loadPrivateKey(owner, password));
                assertTrue(SecureKeyStorage.deletePrivateKey(owner));
            } finally {
                latch.countDown();
            }
        });
    }
    
    assertTrue(latch.await(30, TimeUnit.SECONDS));
}
```

### 4. AddBlockCommandEnhancedTest
**Purpose**: Test enhanced AddBlockCommand with secure key integration

**Key Features**:
- **Secure Mode**: Using stored private keys
- **Demo Mode**: Temporary key generation
- **Error Handling**: Wrong passwords, missing signers
- **Data Validation**: Empty data, size limits
- **JSON Output**: Consistent formatting
- **Mixed Operations**: Different signing methods

**Integration Example**:
```java
@Test
void testSignerWithStoredPrivateKey() {
    // Create user with stored private key
    CommandLine addKeyCmd = new CommandLine(new AddKeyCommand());
    assertEquals(0, addKeyCmd.execute(secureUser, "--generate", "--store-private"));
    
    // Use stored key for signing
    CommandLine addBlockCmd = new CommandLine(new AddBlockCommand());
    int exitCode = addBlockCmd.execute("Test block", "--signer", secureUser);
    
    assertEquals(0, exitCode);
    assertTrue(outContent.toString().contains("Using stored private key"));
}
```

### 5. SecureKeyManagementIntegrationTest
**Purpose**: End-to-end workflow testing

**Key Features**:
- **Complete Workflows**: Key creation → usage → deletion
- **Mode Comparison**: Production vs demo behavior
- **Multi-User**: Different security levels
- **Error Recovery**: System resilience
- **JSON Consistency**: Output format validation

**Workflow Test**:
```java
@Test
void testCompleteWorkflowGenerateKeyAndStoreSecurely() {
    // Step 1: Generate and store secure key
    // Step 2: Verify key storage
    // Step 3: Use key for block signing
    // Each step validates expected behavior
}
```

### 6. SecureKeyManagementStressTest
**Purpose**: Performance and stress testing

**Key Features**:
- **High Volume**: 100+ keys creation/usage
- **Concurrency**: 20 threads, concurrent operations
- **Memory Stress**: Large key operations
- **Resource Limits**: System boundary testing
- **Performance Benchmarks**: Timing validations

**Performance Metrics**:
- Key creation: < 60 seconds for 100 keys
- Key listing: < 5 seconds for any count
- Block signing: < 30 seconds for 20 blocks
- Memory usage: < 100MB increase

## 🚀 Running Tests

### Quick Start
```bash
# Run the integrated secure test suite
./test-cli.sh

# Run with full secure tests enabled
FULL_SECURE_TESTS=true ./test-cli.sh
```

### Test Options

| Option | Description | Time |
|--------|-------------|------|
| **Quick Tests** | Basic functionality validation | ~2-3 minutes |
| **Full Suite** | Comprehensive testing | ~10-15 minutes |
| **Stress Tests** | Performance and limits | ~15-25 minutes |

### Maven Commands
```bash
# Run all new tests
mvn test -Dtest="*SecureKey*,*Password*,*ManageKeys*"

# Run specific test class
mvn test -Dtest="SecureKeyStorageTest"

# Run with verbose output
mvn test -Dtest="SecureKeyStorageTest" -X
```

## 📊 Test Coverage

### Component Coverage

| Component | Unit Tests | Integration Tests | Stress Tests |
|-----------|------------|-------------------|--------------|
| **SecureKeyStorage** | ✅ Basic + Advanced | ✅ Workflow | ✅ Performance |
| **PasswordUtil** | ✅ Comprehensive | ✅ Integration | ✅ Edge Cases |
| **ManageKeysCommand** | ✅ Full Coverage | ✅ Multi-user | ✅ Bulk Ops |
| **AddBlockCommand** | ✅ Enhanced | ✅ Mixed Modes | ✅ Concurrent |

### Scenario Coverage

- ✅ **Happy Path**: Normal operations succeed
- ✅ **Error Conditions**: Graceful error handling
- ✅ **Edge Cases**: Boundary conditions and limits
- ✅ **Security**: Password validation, encryption
- ✅ **Performance**: Speed and resource usage
- ✅ **Concurrency**: Thread safety validation
- ✅ **Integration**: Cross-component workflows

## 🛠️ Troubleshooting

### Common Issues

#### Test Failures Due to File Permissions
```bash
# Ensure temp directory is writable
ls -la /tmp/
# Or set different temp directory
export JAVA_IO_TMPDIR=/path/to/writable/dir
```

#### Memory Issues with Stress Tests
```bash
# Increase JVM heap size
export MAVEN_OPTS="-Xmx2g -Xms1g"
mvn test -Dtest="SecureKeyManagementStressTest"
```

#### Concurrency Test Failures
```bash
# Run with reduced thread count by modifying test
# Or check system resource limits
ulimit -a
```

### Test Environment Requirements

- **Java 21+**: Required for language features
- **Maven 3.6+**: For test execution
- **Sufficient Memory**: 2GB+ recommended for stress tests
- **File System**: Writable temp directory access
- **Network**: Not required (tests are offline)

### Debugging Failed Tests

1. **Enable Verbose Output**:
   ```bash
   mvn test -Dtest="TestClass" -X
   ```

2. **Check Test Reports**:
   ```bash
   ls target/surefire-reports/
   cat target/surefire-reports/TEST-TestClass.xml
   ```

3. **Run Individual Test Methods**:
   ```bash
   mvn test -Dtest="TestClass#testMethod"
   ```

## 📈 Performance Benchmarks

### Expected Performance (Reference System)

| Operation | Target Time | Stress Test Load |
|-----------|-------------|------------------|
| Key Creation | < 600ms each | 100 keys in < 60s |
| Key Loading | < 100ms each | 50 keys in < 5s |
| Block Signing | < 1s each | 20 blocks in < 30s |
| Key Listing | < 50ms | 100+ keys in < 5s |

### System Requirements for Full Test Suite

- **CPU**: Modern multi-core processor
- **RAM**: 4GB+ available
- **Storage**: 1GB+ free space
- **OS**: Any supporting Java 21

## 🔄 Continuous Integration

### GitHub Actions Example
```yaml
name: Secure Key Management Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run Quick Tests
        run: ./test-cli.sh
        # Integrated secure tests run as part of the main test suite
```

### Test Strategy for CI/CD

1. **Pull Request**: Quick tests only
2. **Main Branch**: Full test suite
3. **Release**: Full suite + stress tests
4. **Nightly**: Comprehensive + performance benchmarks

## 📝 Adding New Tests

### Test Development Guidelines

1. **Follow Naming Convention**: `*Test.java` for unit, `*IntegrationTest.java` for integration
2. **Use TempDir**: Always use `@TempDir` for file operations
3. **Clean Up**: Ensure proper resource cleanup in `@AfterEach`
4. **Document**: Add clear JavaDoc for test purpose
5. **Assert Meaningfully**: Use descriptive assertion messages

### Example New Test
```java
@Test
void testNewFeature() {
    // Arrange
    String testData = "test input";
    
    // Act
    boolean result = secureKeyStorage.newFeature(testData);
    
    // Assert
    assertTrue(result, "New feature should handle test input correctly");
}
```

This comprehensive test suite ensures the secure key management functionality is robust, performant, and production-ready.
