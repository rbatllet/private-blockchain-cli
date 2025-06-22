# 🔐 Secure Private Key Management Guide

Complete guide for managing private keys securely in production environments using the Private Blockchain CLI.

## 📋 Table of Contents

- [Overview](#overview)
- [Quick Start Guide](#quick-start-guide)
- [Production Workflows](#production-workflows)
- [Security Best Practices](#security-best-practices)
- [Troubleshooting](#troubleshooting)
- [Migration Scenarios](#migration-scenarios)

## 🚀 Overview

The Private Blockchain CLI now supports **secure private key storage** alongside the existing demo mode, providing a complete solution for both development and production environments.

### Security Modes Comparison

| Feature | Demo Mode | Production Mode |
|---------|-----------|-----------------|
| **Private Key Storage** | Temporary (session only) | AES-256 encrypted file |
| **Key Type** | Single ECDSA key | Hierarchical keys (Root/Intermediate/Operational) |
| **Password Required** | ❌ No | ✅ Yes (with PBKDF2 key derivation) |
| **Security Level** | ⭐⭐ Development | ⭐⭐⭐⭐⭐ Production |
| **Key Rotation** | Manual | Automated with configurable validity periods |
| **Best For** | Testing, Development | Production, Enterprise |

## 🚀 Quick Start Guide

### Step 1: Create User with Secure Key Storage

```zsh
# Create user with secure ECDSA key (secp256r1 curve)
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private --key-type operational
🔐 Enter password to protect private key: [hidden]
🔒 Key must be at least 12 characters with uppercase, lowercase, number, and special character
✅ Authorized key added successfully!
🔐 Private key stored using AES-256 encryption
📝 Key ID: 1a2b3c4d-5e6f-7890-1234-56789abcdef0
💡 You can now use --signer Alice with add-block command
```

### Key Types

- **Root Key** (`--key-type root`): Long-lived master key (1+ years)
- **Intermediate Key** (`--key-type intermediate`): Department/team key (6-12 months)
- **Operational Key** (default): Short-lived key (30-90 days)

Example of creating a hierarchical key structure:

```zsh
# Create root key (very secure, long-term storage)
java -jar blockchain-cli.jar add-key "Root-Key-2025" --generate --store-private --key-type root

# Create intermediate key for development team
java -jar blockchain-cli.jar add-key "Dev-Team-Key" --generate --store-private --key-type intermediate --parent-key Root-Key-2025

# Create operational key for CI/CD pipeline
java -jar blockchain-cli.jar add-key "CI-Pipeline-Key" --generate --store-private --key-type operational --parent-key Dev-Team-Key --validity-days 30
```

### Step 2: Verify Key Storage

```zsh
# Check if key is stored
java -jar blockchain-cli.jar manage-keys --check Alice
✅ Private key is stored for: Alice
💡 You can use --signer Alice with add-block

# List all stored keys
java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
🔑 Alice
📊 Total: 1 stored private key(s)
```

### Step 3: Use Secure Key for Signing

```zsh
# Sign block with stored private key (interactive password prompt)
java -jar blockchain-cli.jar add-block "Secure production data" --signer Alice
🔐 Enter password for Alice: [hidden]
🔒 Using ECDSA-SHA3-256 signature with secp256r1 curve
✅ Block signed and added successfully!
📝 Block hash: a1b2c3d4e5f6...
🔑 Key ID used: 1a2b3c4d-5e6f-7890-1234-56789abcdef0

# Non-interactive signing (for CI/CD pipelines)
# Store password in environment variable or use a password manager
PASSWORD="secure-password-here"
echo "Pipeline deployment data" | java -jar blockchain-cli.jar add-block - --signer CI-Pipeline-Key --password-env PASSWORD
```

### Key Rotation Example

```zsh
# Check key expiration
java -jar blockchain-cli.jar manage-keys --check-expiration

# Rotate an expiring key
java -jar blockchain-cli.jar manage-keys --rotate "Alice" --validity-days 90
🔐 Enter current password for Alice: [hidden]
🔑 Generating new ECDSA key pair...
✅ Key rotated successfully
📅 New expiration: 2025-09-22

# Verify the rotation
java -jar blockchain-cli.jar validate --detailed
```

## 🏭 Production Workflows

### Workflow 1: Multi-Department Enterprise Setup

```zsh
# Setup phase: Create department heads with secure keys
echo "Setting up enterprise blockchain with secure key management..."

# Executive level (highest security)
java -jar blockchain-cli.jar add-key "CEO" --generate --store-private
java -jar blockchain-cli.jar add-key "CFO" --generate --store-private
java -jar blockchain-cli.jar add-key "CTO" --generate --store-private

# Department heads (high security)
java -jar blockchain-cli.jar add-key "HR-Director" --generate --store-private
java -jar blockchain-cli.jar add-key "Legal-Counsel" --generate --store-private
java -jar blockchain-cli.jar add-key "Audit-Manager" --generate --store-private

# Operational staff (demo mode for flexibility)
java -jar blockchain-cli.jar add-key "HR-Assistant" --generate
java -jar blockchain-cli.jar add-key "Developer" --generate
java -jar blockchain-cli.jar add-key "Analyst" --generate

# Verify security setup
java -jar blockchain-cli.jar manage-keys --list
echo "✅ Enterprise security setup complete"

# Executive decisions (secure signing required)
java -jar blockchain-cli.jar add-block "Q4 Budget Approved: $2.5M allocation for R&D expansion" --signer CEO
java -jar blockchain-cli.jar add-block "Financial Audit Report: All compliance requirements met" --signer CFO
java -jar blockchain-cli.jar add-block "Security Infrastructure Upgrade: Multi-factor auth implemented" --signer CTO

# Department operations (secure signing)
java -jar blockchain-cli.jar add-block "New Employee Onboarded: Senior Developer position filled" --signer HR-Director
java -jar blockchain-cli.jar add-block "Contract Review Completed: Vendor agreement terms finalized" --signer Legal-Counsel

# Operational activities (demo mode)
java -jar blockchain-cli.jar add-block "Performance review scheduled for team leads" --signer HR-Assistant
java -jar blockchain-cli.jar add-block "Code review completed for authentication module" --signer Developer
```

### Workflow 2: Financial Services Compliance

```zsh
# Setup compliance-ready environment
echo "Setting up financial services blockchain with compliance controls..."

# Compliance officers (mandatory secure keys)
java -jar blockchain-cli.jar add-key "Chief-Compliance-Officer" --generate --store-private
java -jar blockchain-cli.jar add-key "Risk-Manager" --generate --store-private
java -jar blockchain-cli.jar add-key "Audit-Partner" --generate --store-private

# Trading desk (secure keys for transactions)
java -jar blockchain-cli.jar add-key "Head-Trader" --generate --store-private
java -jar blockchain-cli.jar add-key "Senior-Analyst" --generate --store-private

# Support staff (demo mode)
java -jar blockchain-cli.jar add-key "Junior-Analyst" --generate
java -jar blockchain-cli.jar add-key "Data-Entry" --generate

# Compliance tracking
java -jar blockchain-cli.jar add-block "Daily Compliance Check: All trades within risk parameters" --signer Chief-Compliance-Officer
java -jar blockchain-cli.jar add-block "Risk Assessment: Portfolio exposure within acceptable limits" --signer Risk-Manager

# Trading activities
java -jar blockchain-cli.jar add-block "Major Trade Executed: $50M bond purchase approved and executed" --signer Head-Trader
java -jar blockchain-cli.jar add-block "Market Analysis: Quarterly review indicates bullish tech sector trend" --signer Senior-Analyst

# Support activities (demo mode for flexibility)
java -jar blockchain-cli.jar add-block "Data validation completed for daily reports" --signer Junior-Analyst
```

### Workflow 3: Healthcare Records Management

```zsh
# Setup healthcare environment with HIPAA compliance focus
echo "Setting up healthcare blockchain with secure patient data controls..."

# Medical staff (secure keys mandatory for patient data)
java -jar blockchain-cli.jar add-key "Chief-Medical-Officer" --generate --store-private
java -jar blockchain-cli.jar add-key "Privacy-Officer" --generate --store-private
java -jar blockchain-cli.jar add-key "Head-Nurse" --generate --store-private

# Administrative staff (mixed security levels)
java -jar blockchain-cli.jar add-key "Medical-Records" --generate --store-private
java -jar blockchain-cli.jar add-key "Billing-Supervisor" --generate
java -jar blockchain-cli.jar add-key "Reception" --generate

# Patient care records (highest security)
java -jar blockchain-cli.jar add-block "Patient Treatment Protocol: Diabetes management plan updated for Patient-ID-7429" --signer Chief-Medical-Officer
java -jar blockchain-cli.jar add-block "Privacy Audit: All patient data access logs reviewed and compliant" --signer Privacy-Officer
java -jar blockchain-cli.jar add-block "Medication Administration: Daily rounds completed, all prescriptions verified" --signer Head-Nurse

# Administrative records (secure but operational)
java -jar blockchain-cli.jar add-block "Medical Records Update: Patient consent forms digitized and filed" --signer Medical-Records

# General operations (demo mode for efficiency)
java -jar blockchain-cli.jar add-block "Appointment scheduling system updated with new time slots" --signer Reception
```

## 🔒 Security Best Practices

### Password Security

```zsh
# ✅ Strong password examples
SecurePass123!
MyHospital2025#
BlockchainKey99$

# ❌ Weak password examples
password
12345678
blockchain
```

### Key Rotation Policy

```zsh
# Monthly key rotation for high-security users
echo "Performing monthly key rotation for CEO..."

# 1. Create new key for user
java -jar blockchain-cli.jar add-key "CEO-New" --generate --store-private

# 2. Transfer operations to new key
java -jar blockchain-cli.jar add-block "Key Rotation: CEO credentials updated for Q1 2025" --signer CEO-New

# 3. Archive old key (optional: keep for historical verification)
java -jar blockchain-cli.jar manage-keys --delete CEO

# 4. Rename new key to standard name
# (This would require additional tooling in production)
```

### Access Control Management

```zsh
# Regular security audits
echo "Performing quarterly security audit..."

# Check all stored keys
java -jar blockchain-cli.jar manage-keys --list --json > security-audit-$(date +%Y%m%d).json

# Test access for critical users
java -jar blockchain-cli.jar manage-keys --test CEO
java -jar blockchain-cli.jar manage-keys --test CFO
java -jar blockchain-cli.jar manage-keys --test Chief-Compliance-Officer

# Document audit trail
java -jar blockchain-cli.jar add-block "Security Audit Completed: All executive keys verified and functional" --signer Chief-Compliance-Officer
```

## 🔍 Troubleshooting

### Common Production Issues

#### Issue 1: Forgotten Password

```zsh
# Symptom: Cannot access stored private key
java -jar blockchain-cli.jar add-block "Test" --signer Alice
🔐 Enter password for Alice: [wrong_password]
❌ Failed to load private key (wrong password?)

# Solution: Reset key (creates new key pair)
java -jar blockchain-cli.jar manage-keys --delete Alice
⚠️  Are you sure you want to delete the private key for 'Alice'? yes
java -jar blockchain-cli.jar add-key "Alice" --generate --store-private
```

#### Issue 2: Key File Corruption

```zsh
# Symptom: Error loading key even with correct password
❌ Error loading private key: Invalid key format

# Solution: Delete and recreate
java -jar blockchain-cli.jar manage-keys --delete CorruptedUser
java -jar blockchain-cli.jar add-key "CorruptedUser" --generate --store-private
```

#### Issue 3: Security Compliance Audit

```zsh
# Generate compliance report
echo "Generating security compliance report..."

# List all users and their security status
java -jar blockchain-cli.jar list-keys --detailed > compliance-users.txt
java -jar blockchain-cli.jar manage-keys --list --json > compliance-secure-keys.json

# Verify critical users have secure keys
critical_users=("CEO" "CFO" "CTO" "Compliance-Officer")
for user in "${critical_users[@]}"; do
    if java -jar blockchain-cli.jar manage-keys --check "$user" | grep -q "Private key is stored"; then
        echo "✅ $user: Secure key verified"
    else
        echo "❌ $user: Missing secure key - COMPLIANCE ISSUE"
    fi
done
```

## 🔄 Migration Scenarios

### Scenario 1: Demo to Production Migration

```zsh
# Existing demo user
java -jar blockchain-cli.jar add-block "Demo operation" --signer ExistingUser
⚠️  DEMO MODE: No stored private key found for signer: ExistingUser

# Upgrade to production security
java -jar blockchain-cli.jar add-key "ExistingUser-Secure" --generate --store-private

# Continue with secure operations
java -jar blockchain-cli.jar add-block "Production operation" --signer ExistingUser-Secure
🔐 Enter password for ExistingUser-Secure: [hidden]
✅ Using stored private key for signer: ExistingUser-Secure
```

### Scenario 2: Bulk User Security Upgrade

```zsh
#!/usr/bin/env zsh
# Script to upgrade multiple users to secure keys

users=("Manager" "Supervisor" "TeamLead" "SeniorDev")

for user in "${users[@]}"; do
    echo "Upgrading $user to secure key storage..."
    
    # Check if user exists
    if java -jar blockchain-cli.jar list-keys | grep -q "$user"; then
        echo "User $user exists, checking for stored key..."
        
        if ! java -jar blockchain-cli.jar manage-keys --check "$user" | grep -q "Private key is stored"; then
            echo "Creating secure key for $user..."
            java -jar blockchain-cli.jar add-key "${user}-Secure" --generate --store-private
            echo "✅ $user upgraded to secure key: ${user}-Secure"
        else
            echo "✅ $user already has secure key"
        fi
    else
        echo "❌ User $user not found, creating new secure user..."
        java -jar blockchain-cli.jar add-key "$user" --generate --store-private
    fi
done

echo "🎉 Bulk security upgrade completed!"
```

### Scenario 3: Disaster Recovery

```zsh
# Backup all critical key metadata (not the keys themselves!)
echo "Creating disaster recovery backup..."

# Export blockchain (includes public keys)
java -jar blockchain-cli.jar export disaster-recovery-backup-$(date +%Y%m%d).json

# List all stored private keys for documentation
java -jar blockchain-cli.jar manage-keys --list > stored-keys-inventory-$(date +%Y%m%d).txt

# Document key recovery procedures
cat > key-recovery-procedures.md << EOF
# Key Recovery Procedures

## Critical Users with Stored Keys:
$(java -jar blockchain-cli.jar manage-keys --list)

## Recovery Steps:
1. Restore from blockchain backup
2. Recreate stored private keys for critical users
3. Update passwords and test access
4. Verify blockchain integrity

## Emergency Contacts:
- System Administrator: [contact]
- Security Officer: [contact]
- Blockchain Admin: [contact]
EOF

echo "✅ Disaster recovery documentation created"
```

This guide provides comprehensive examples for implementing secure private key management in production blockchain environments while maintaining the flexibility of demo mode for development workflows.
