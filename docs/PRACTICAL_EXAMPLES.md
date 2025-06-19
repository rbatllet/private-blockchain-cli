$ java -jar blockchain-cli.jar add-key "ProductionAuditor" --generate --store-private

# Development users without stored keys (demo mode)
$ java -jar blockchain-cli.jar add-key "DevLead" --generate
$ java -jar blockchain-cli.jar add-key "Intern" --generate

# Production operations (secure - password required)
$ java -jar blockchain-cli.jar add-block "Production Deployment: v2.1.0 released to production environment" --signer ProductionManager
🔐 Enter password for ProductionManager: [ProdSecure123]
✅ Using stored private key for signer: ProductionManager

$ java -jar blockchain-cli.jar add-block "Production Audit: System performance meets SLA requirements" --signer ProductionAuditor
🔐 Enter password for ProductionAuditor: [AuditSecure456]
✅ Using stored private key for signer: ProductionAuditor

# Development operations (demo mode - fast and flexible)
$ java -jar blockchain-cli.jar add-block "Development: New feature branch created for user dashboard" --signer DevLead
⚠️  DEMO MODE: No stored private key found for signer: DevLead
✅ Block added successfully!

$ java -jar blockchain-cli.jar add-block "Learning: Completed blockchain tutorial and first test commit" --signer Intern
⚠️  DEMO MODE: No stored private key found for signer: Intern
✅ Block added successfully!
```

### Scenario: Staged Security Implementation

```zsh
# Phase 1: Start with demo mode for all users
$ java -jar blockchain-cli.jar add-key "Manager" --generate
$ java -jar blockchain-cli.jar add-key "Supervisor" --generate
$ java -jar blockchain-cli.jar add-key "Employee" --generate

# Phase 2: Upgrade critical users to secure keys
$ java -jar blockchain-cli.jar add-key "Manager-Secure" --generate --store-private
🔐 Enter password to protect private key: [ManagerSecure123]
✅ Private key stored securely for: Manager-Secure

# Phase 3: Migrate operations to secure keys
$ java -jar blockchain-cli.jar add-block "Security Migration: Manager now using secure key storage" --signer Manager-Secure
🔐 Enter password for Manager-Secure: [ManagerSecure123]
✅ Using stored private key for signer: Manager-Secure

# Phase 4: Verify security status
$ java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
====================
🔑 Manager-Secure
📊 Total: 1 stored private key(s)
```

## 🔧 Key Management Operations

### Complete Key Lifecycle Management

```zsh
# 1. Create user with secure key storage
$ java -jar blockchain-cli.jar add-key "CorporateUser" --generate --store-private
🔐 Enter password to protect private key: [CorporateSecure123]
Confirm password: [CorporateSecure123]
✅ Authorized key added successfully!
🔒 Private key stored securely for: CorporateUser

# 2. Verify key exists and test password
$ java -jar blockchain-cli.jar manage-keys --check CorporateUser
✅ Private key is stored for: CorporateUser
💡 You can use --signer CorporateUser with add-block

$ java -jar blockchain-cli.jar manage-keys --test CorporateUser
🔐 Enter password for CorporateUser: [CorporateSecure123]
✅ Password is correct for: CorporateUser
🔓 Private key loaded successfully

# 3. Use key for signing multiple blocks
$ java -jar blockchain-cli.jar add-block "Corporate Policy: Remote work guidelines updated" --signer CorporateUser
$ java -jar blockchain-cli.jar add-block "Board Meeting: Q4 strategy approved by board of directors" --signer CorporateUser
$ java -jar blockchain-cli.jar add-block "Financial Report: Annual revenue targets exceeded by 12%" --signer CorporateUser

# 4. List all stored keys
$ java -jar blockchain-cli.jar manage-keys --list
🔐 Stored Private Keys:
====================
🔑 CorporateUser
🔑 DepartmentManager  
🔑 ProductionManager
📊 Total: 3 stored private key(s)

# 5. Export for backup (when needed)
$ java -jar blockchain-cli.jar export corporate_backup_2024.json
✅ Blockchain exported successfully to: corporate_backup_2024.json
Exported 15 blocks and 8 authorized keys

# 6. Security rotation (delete and recreate when needed)
$ java -jar blockchain-cli.jar manage-keys --delete CorporateUser
⚠️  Are you sure you want to delete the private key for 'CorporateUser'? yes
🗑️  Private key deleted for: CorporateUser
⚠️  You can no longer use --signer CorporateUser (will fallback to demo mode)

$ java -jar blockchain-cli.jar add-key "CorporateUser-New" --generate --store-private
🔐 Enter password to protect private key: [NewCorporateSecure456]
✅ Authorized key added successfully!
🔒 Private key stored securely for: CorporateUser-New
```

### Bulk Key Management for Organizations

```zsh
# Script for bulk user setup
#!/usr/bin/env zsh

# Define user categories and their security levels
EXECUTIVE_USERS=("CEO" "CFO" "CTO" "CHRO")
MANAGER_USERS=("ITManager" "HRManager" "FinanceManager" "SalesManager")  
STAFF_USERS=("Developer1" "Developer2" "Analyst1" "Assistant1")

echo "Setting up organizational blockchain access..."

# Executives - Secure keys mandatory
for user in "${EXECUTIVE_USERS[@]}"; do
    echo "Creating secure key for executive: $user"
    # Note: In practice, each executive would set their own password
    echo -e "Executive${user}Pass123\nExecutive${user}Pass123" | \
    java -jar blockchain-cli.jar add-key "$user" --generate --store-private
done

# Managers - Secure keys recommended  
for user in "${MANAGER_USERS[@]}"; do
    echo "Creating secure key for manager: $user"
    echo -e "Manager${user}Pass456\nManager${user}Pass456" | \
    java -jar blockchain-cli.jar add-key "$user" --generate --store-private
done

# Staff - Demo mode for flexibility
for user in "${STAFF_USERS[@]}"; do
    echo "Creating demo key for staff: $user"
    java -jar blockchain-cli.jar add-key "$user" --generate
done

# Verify setup
java -jar blockchain-cli.jar manage-keys --list
java -jar blockchain-cli.jar list-keys --detailed

echo "Organizational setup complete!"
```

### Monitoring and Auditing

```zsh
# Regular security audit commands
$ java -jar blockchain-cli.jar manage-keys --list --json > security-audit-$(date +%Y%m%d).json

# Verify critical users have secure keys
$ java -jar blockchain-cli.jar manage-keys --check CEO
$ java -jar blockchain-cli.jar manage-keys --check CFO
$ java -jar blockchain-cli.jar manage-keys --check CTO

# Test blockchain integrity
$ java -jar blockchain-cli.jar validate --detailed --json > validation-report-$(date +%Y%m%d).json

# Search for security-related activities
$ java -jar blockchain-cli.jar search "security" --json
$ java -jar blockchain-cli.jar search "audit" --json
$ java -jar blockchain-cli.jar search "compliance" --json
```

## 📊 Integration Patterns

### CI/CD Pipeline Integration

```zsh
#!/usr/bin/env zsh
# deploy.sh - Deployment script with blockchain logging

ENVIRONMENT=$1
BUILD_NUMBER=$2
DEPLOYER="CI-System"

# Setup CI system with demo key (no password needed)
java -jar blockchain-cli.jar add-key "$DEPLOYER" --generate >/dev/null 2>&1

# Log deployment start
java -jar blockchain-cli.jar add-block "Deployment Started: Build #$BUILD_NUMBER to $ENVIRONMENT environment" --signer "$DEPLOYER" --json

# Perform deployment steps...
deploy_application

# Log deployment completion
java -jar blockchain-cli.jar add-block "Deployment Completed: Build #$BUILD_NUMBER successfully deployed to $ENVIRONMENT" --signer "$DEPLOYER" --json

# For production deployments, require secure approval
if [ "$ENVIRONMENT" = "production" ]; then
    echo "Production deployment requires secure approval..."
    java -jar blockchain-cli.jar add-block "Production Deployment Approved: Build #$BUILD_NUMBER cleared for production release" --signer "ProductionManager"
fi
```

### API Integration Example

```python
# blockchain_integration.py
import subprocess
import json
import getpass

class BlockchainLogger:
    def __init__(self, jar_path="blockchain-cli.jar"):
        self.jar_path = jar_path
    
    def add_secure_block(self, data, signer):
        """Add block with secure signing (will prompt for password)"""
        cmd = ["java", "-jar", self.jar_path, "add-block", data, "--signer", signer, "--json"]
        result = subprocess.run(cmd, capture_output=True, text=True, input=getpass.getpass(f"Password for {signer}: "))
        return json.loads(result.stdout) if result.stdout else None
    
    def add_demo_block(self, data, signer):
        """Add block with demo mode (no password required)"""
        cmd = ["java", "-jar", self.jar_path, "add-block", data, "--signer", signer, "--json"]
        result = subprocess.run(cmd, capture_output=True, text=True)
        return json.loads(result.stdout) if result.stdout else None
    
    def create_secure_user(self, username):
        """Create user with secure key storage"""
        password = getpass.getpass(f"Enter password for {username}: ")
        confirm = getpass.getpass("Confirm password: ")
        
        if password != confirm:
            raise ValueError("Passwords don't match")
            
        cmd = ["java", "-jar", self.jar_path, "add-key", username, "--generate", "--store-private", "--json"]
        result = subprocess.run(cmd, capture_output=True, text=True, input=f"{password}\n{confirm}\n")
        return json.loads(result.stdout) if result.stdout else None

# Usage example
logger = BlockchainLogger()

# Create secure user for financial operations
logger.create_secure_user("FinanceBot")

# Log financial transaction with secure signing
transaction_result = logger.add_secure_block(
    "Wire Transfer: €50,000 to Supplier ABC-123 for Q4 inventory", 
    "FinanceBot"
)

print(f"Transaction logged: Block #{transaction_result['blockNumber']}")
```

### Database Trigger Integration

```sql
-- SQL trigger example for automatic blockchain logging
-- (This would be adapted for your specific database)

CREATE OR REPLACE FUNCTION log_critical_changes()
RETURNS TRIGGER AS $$
DECLARE
    change_description TEXT;
    blockchain_result TEXT;
BEGIN
    -- Construct change description
    change_description := 'Database Change: ' || TG_TABLE_NAME || 
                         ' record ' || TG_OP || ' by user ' || USER;
    
    -- Log to blockchain via external script
    SELECT INTO blockchain_result system('java -jar blockchain-cli.jar add-block "' || 
                                        change_description || '" --signer DatabaseAuditor');
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to critical tables
CREATE TRIGGER audit_financial_records
    AFTER INSERT OR UPDATE OR DELETE ON financial_records
    FOR EACH ROW EXECUTE FUNCTION log_critical_changes();
```

## 🎯 Best Practices Summary

### Security Levels by Use Case

| Use Case | Key Type | Password Required | Best For |
|----------|----------|-------------------|----------|
| **Production/Finance** | Stored Private Key | ✅ Yes | Critical business operations |
| **Healthcare/Legal** | Stored Private Key | ✅ Yes | Regulated industry compliance |
| **Development/Testing** | Demo Mode | ❌ No | Rapid iteration and testing |
| **CI/CD Automation** | Demo Mode | ❌ No | Automated deployments |
| **Mixed Environment** | Both | Conditional | Flexible organizational needs |

### Password Recommendations

```zsh
# Strong password examples for secure keys
SecureFinance2024!
HealthcareSecure#123
LegalDocuments$456
ProductionAccess@789

# Avoid weak passwords
password123
blockchain
company
admin
```

### Operational Workflows

1. **Start with Demo Mode** for development and testing
2. **Upgrade to Secure Keys** for production users
3. **Implement Key Rotation** for high-security environments
4. **Regular Auditing** with `manage-keys --list` and `validate`
5. **Backup Strategy** with `export` command
6. **Access Controls** based on user roles and responsibilities

This practical guide demonstrates how the secure key management system supports both rapid development workflows and enterprise-grade security requirements, providing the flexibility to choose the appropriate security level for each use case.
