# 🔧 --signer Parameter Troubleshooting Guide

This guide covers common issues and solutions when using the `--signer` parameter with the `add-block` command.

## 📖 Quick Reference

### Valid Signing Methods
```bash
# Method 1: Use existing authorized user (RECOMMENDED)
java -jar blockchain-cli.jar add-block "Your data" --signer Alice

# Method 2: Generate new key automatically
java -jar blockchain-cli.jar add-block "Your data" --generate-key

# Method 3: Load from key file (coming soon)
java -jar blockchain-cli.jar add-block "Your data" --key-file path/to/key.pem
```

## 🚨 Common Errors and Solutions

### Error 1: Signer Not Found

**Error Message:**
```
❌ Error: Signer 'UnknownUser' not found in authorized keys
❌ Error: Use 'blockchain list-keys' to see available signers
```

**Cause:** The specified signer name doesn't exist in the blockchain's authorized keys.

**Solutions:**

1. **Check available signers:**
```bash
java -jar blockchain-cli.jar list-keys
```

2. **Add the missing signer (demo mode):**
```bash
java -jar blockchain-cli.jar add-key "UnknownUser" --generate
```

3. **Add the missing signer (production mode):**
```bash
java -jar blockchain-cli.jar add-key "UnknownUser" --generate --store-private
🔐 Enter password to protect private key: [hidden]
🔒 Private key stored securely for: UnknownUser
```

### Error 2: Wrong Password for Stored Key

**Error Message:**
```
❌ Failed to load private key (wrong password?)
```

**Cause:** Incorrect password entered for a stored private key.

**Solutions:**

1. **Try again with correct password:**
```bash
java -jar blockchain-cli.jar add-block "Your data" --signer Alice
🔐 Enter password for Alice: [correct_password]
```

2. **Test password separately:**
```bash
java -jar blockchain-cli.jar manage-keys --test Alice
🔐 Enter password for Alice: [password]
```

3. **If password is forgotten, delete and recreate:**
```bash
java -jar blockchain-cli.jar manage-keys --delete Alice
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
# Note: This creates new keys, old signatures won't match
```

### Error 3: No Signing Method Specified

**Error Message:**
```
❌ Error: No signing method specified
❌ Error: Use one of the following options:
❌ Error:   --generate-key: Generate a new key pair
❌ Error:   --signer <n>: Use an existing authorized key
❌ Error:   --key-file <path>: Load private key from file (not yet implemented)
```

**Cause:** You didn't specify any signing method.

**Solutions:**

1. **Use existing signer (production mode):**
```bash
java -jar blockchain-cli.jar add-block "Your data" --signer Alice
```

2. **Use existing signer (demo mode):**
```bash
java -jar blockchain-cli.jar add-block "Your data" --signer Bob  # Bob has no stored key
```

3. **Generate new key:**
```bash
java -jar blockchain-cli.jar add-block "Your data" --generate-key
```

### Error 4: Password Too Weak

**Error Message:**
```
❌ Password must be at least 8 characters long
❌ Password must contain at least one letter and one number
```

**Cause:** Password doesn't meet security requirements.

**Solution:**
```bash
# Use a strong password with letters and numbers
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
🔐 Enter password to protect private key: SecurePass123
Confirm password: SecurePass123
✅ Password accepted
```
```

**Cause:** You didn't specify any signing method.

**Solutions:**

1. **Use an existing signer:**
```bash
java -jar blockchain-cli.jar add-block "Your data" --signer Alice
```

2. **Generate a new key:**
```bash
java -jar blockchain-cli.jar add-block "Your data" --generate-key
```

### Error 3: Unknown Option

**Error Message:**
```
Unknown option: '--signer'
```

**Cause:** You're using an old version of the CLI that doesn't support the `--signer` parameter.

**Solution:** Update to the latest version that includes the `--signer` bug fix.

## 🎯 Best Practices

### 1. Production Environment Setup (Secure Keys)

```bash
# Step 1: Create production users with stored private keys
java -jar blockchain-cli.jar add-key "Manager" --generate --store-private
java -jar blockchain-cli.jar add-key "Auditor" --generate --store-private
java -jar blockchain-cli.jar add-key "SystemAdmin" --generate --store-private

# Step 2: Verify keys are stored securely
java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
🔑 Manager
🔑 Auditor  
🔑 SystemAdmin

# Step 3: Use secure signing for production operations
java -jar blockchain-cli.jar add-block "Quarterly audit completed" --signer Auditor
🔐 Enter password for Auditor: [hidden]
✅ Using stored private key for signer: Auditor

java -jar blockchain-cli.jar add-block "System backup verified" --signer SystemAdmin
🔐 Enter password for SystemAdmin: [hidden]
✅ Using stored private key for signer: SystemAdmin
```

### 2. Development Environment Setup (Demo Mode)

```bash
# Step 1: Create development users without stored keys
java -jar blockchain-cli.jar add-key "DevUser1" --generate
java -jar blockchain-cli.jar add-key "DevUser2" --generate
java -jar blockchain-cli.jar add-key "TestUser" --generate

# Step 2: Use demo mode for fast development
java -jar blockchain-cli.jar add-block "Feature branch created" --signer DevUser1
⚠️  DEMO MODE: No stored private key found for signer: DevUser1
🔑 DEMO: Created temporary key for existing signer: DevUser1

java -jar blockchain-cli.jar add-block "Unit tests added" --signer DevUser2
⚠️  DEMO MODE: No stored private key found for signer: DevUser2
```

### 3. Mixed Environment (Recommended)

```bash
# Production users with stored keys
java -jar blockchain-cli.jar add-key "CEO" --generate --store-private
java -jar blockchain-cli.jar add-key "CTO" --generate --store-private

# Development users without stored keys  
java -jar blockchain-cli.jar add-key "Developer" --generate
java -jar blockchain-cli.jar add-key "Tester" --generate

# Production operations (secure)
java -jar blockchain-cli.jar add-block "Board resolution approved" --signer CEO
🔐 Enter password for CEO: [hidden]
✅ Using stored private key for signer: CEO

# Development operations (demo mode)
java -jar blockchain-cli.jar add-block "Debug log entry" --signer Developer
⚠️  DEMO MODE: No stored private key found for signer: Developer
```

### 4. Key Security Management

```bash
# Regular security checks
java -jar blockchain-cli.jar manage-keys --list
java -jar blockchain-cli.jar manage-keys --check ProductionUser

# Test passwords periodically
java -jar blockchain-cli.jar manage-keys --test ProductionUser
🔐 Enter password for ProductionUser: [hidden]
✅ Password is correct for: ProductionUser

# Rotate keys when needed (user leaves, security incident)
java -jar blockchain-cli.jar manage-keys --delete OldUser
java -jar blockchain-cli.jar add-key "NewUser" --generate --store-private
```
java -jar blockchain-cli.jar add-block "Personal note" --signer MyUser
java -jar blockchain-cli.jar add-block "Project update" --signer MyUser
```

### 3. Temporary/Testing Entries

```bash
# For quick testing, use --generate-key
java -jar blockchain-cli.jar add-block "Test entry" --generate-key
java -jar blockchain-cli.jar add-block "Another test" --generate-key
```

## 🔍 Debugging Commands

### Check Current Blockchain State
```bash
# See overall status
java -jar blockchain-cli.jar status --detailed

# List all authorized signers
java -jar blockchain-cli.jar list-keys --detailed

# Validate blockchain integrity
java -jar blockchain-cli.jar validate --detailed
```

### Verify Your Block Was Added
```bash
# Search for your specific data
java -jar blockchain-cli.jar search "your data content"

# See recent blocks
java -jar blockchain-cli.jar search --limit 5

# Export and examine the blockchain
java -jar blockchain-cli.jar export debug_blockchain.json
```

## 📊 Demo Mode Explanation

When you use `--signer` with an existing user, the CLI operates in **Demo Mode**:

### What Happens:
1. ✅ CLI finds your specified signer in the authorized keys
2. 🔑 Generates a temporary key pair for demonstration
3. 📝 Creates a new temporary authorization entry
4. ✅ Signs the block with the temporary key
5. 💾 Stores the block successfully

### Example Output:
```bash
$ java -jar blockchain-cli.jar add-block "Test data" --signer Alice
ℹ️  DEMO: Created temporary key for existing signer: Alice
ℹ️  DEMO: This simulates using the --signer functionality
🔑 Temp Public Key: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
✅ Block added successfully!
📦 Block number: 3
📝 Data: Test data
🔗 Total blocks in chain: 3
```

### Why Demo Mode?
- **Security:** Real private keys aren't stored in the database
- **Testing:** Allows you to test multi-user workflows
- **Development:** Simulates production behavior safely

## 🚀 Advanced Usage Patterns

### Pattern 1: Workflow with Error Handling
```bash
#!/bin/bash

SIGNER="ProjectManager"
DATA="Project milestone completed"

# Check if signer exists
if java -jar blockchain-cli.jar list-keys | grep -q "$SIGNER"; then
    echo "Using existing signer: $SIGNER"
    java -jar blockchain-cli.jar add-block "$DATA" --signer "$SIGNER"
else
    echo "Signer not found, creating new one..."
    java -jar blockchain-cli.jar add-key "$SIGNER" --generate
    java -jar blockchain-cli.jar add-block "$DATA" --signer "$SIGNER"
fi
```

### Pattern 2: Batch Operations with Same Signer
```bash
#!/bin/bash

SIGNER="DataProcessor"
BATCH_ID="BATCH-$(date +%Y%m%d-%H%M%S)"

# Create signer if doesn't exist
java -jar blockchain-cli.jar add-key "$SIGNER" --generate 2>/dev/null || true

# Process multiple entries
for i in {1..5}; do
    java -jar blockchain-cli.jar add-block "Entry $i for $BATCH_ID" --signer "$SIGNER"
done

# Verify all entries were added
java -jar blockchain-cli.jar search "$BATCH_ID" --detailed
```

### Pattern 3: Role-Based Access Control Simulation
```bash
#!/bin/bash

# Setup roles
declare -A ROLES
ROLES[admin]="System administration tasks"
ROLES[user]="Regular user operations"
ROLES[guest]="Read-only access simulation"

# Create role-based signers
for role in "${!ROLES[@]}"; do
    java -jar blockchain-cli.jar add-key "Role-$role" --generate
done

# Function to add entry with role checking
add_entry_with_role() {
    local role=$1
    local data=$2
    
    case $role in
        admin|user)
            java -jar blockchain-cli.jar add-block "$data" --signer "Role-$role"
            ;;
        guest)
            echo "❌ Error: Role '$role' has read-only access"
            return 1
            ;;
        *)
            echo "❌ Error: Unknown role '$role'"
            return 1
            ;;
    esac
}

# Usage examples
add_entry_with_role "admin" "System configuration updated"
add_entry_with_role "user" "User data processed"
add_entry_with_role "guest" "This should fail"
```

## 📚 Additional Resources

- **README.md**: Complete CLI documentation
- **EXAMPLES.md**: Comprehensive usage examples
- **test-cli.sh**: Automated test suite including --signer tests
- **Troubleshooting Guide**: This document

## 🆘 Getting Help

If you're still having issues:

1. **Check CLI version:**
```bash
java -jar blockchain-cli.jar --version
```

2. **Run validation:**
```bash
java -jar blockchain-cli.jar validate --detailed
```

3. **Check logs:** Look for error messages in the console output

4. **Reset if needed:**
```bash
# Backup first
java -jar blockchain-cli.jar export backup_before_reset.json

# Start fresh (this will delete your blockchain)
rm blockchain.db*
java -jar blockchain-cli.jar status
```

Remember: The `--signer` functionality is now fully implemented and tested. If you encounter any issues not covered in this guide, please check for updates or file a bug report.
