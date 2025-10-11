#!/usr/bin/env zsh

# Generate Test Keys Script
# Creates test keys in all supported formats for --key-file testing
# Version: 2.0.0 - Updated for ECDSA + SHA3 cryptography
# ZSH adaptation

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

function print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

function print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

function print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

echo -e "${BLUE}🔑 Test Key Generator for Private Blockchain CLI${NC}"
echo "================================================="
echo -e "${BLUE}Using modern ECDSA P-256 curve + SHA3-256 cryptography${NC}"
echo ""
echo -e "${YELLOW}⚠️  WARNING: These keys are generated WITHOUT encryption (unprotected)${NC}"
echo -e "${YELLOW}⚠️  For PRODUCTION use, generate keys with AES-256-GCM encryption via CLI:${NC}"
echo -e "${YELLOW}   java -jar target/blockchain-cli.jar add-key <owner> --generate --store-private${NC}"
echo ""

# Check if openssl is available
if ! command -v openssl &> /dev/null; then
    echo "❌ OpenSSL is required but not installed."
    echo "Please install OpenSSL and try again."
    echo ""
    echo "Installation instructions:"
    echo "  macOS: brew install openssl"
    echo "  Ubuntu/Debian: sudo apt-get install openssl"
    echo "  CentOS/RHEL: sudo yum install openssl"
    exit 1
fi

# Get script directory and navigate to project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

# Create keys directory at project root
KEYS_DIR="test-keys"
mkdir -p "$KEYS_DIR"
cd "$KEYS_DIR"

print_info "Creating test keys in directory: $(pwd)/$KEYS_DIR (project root)"

# 1. Generate PKCS#8 PEM private key using ECDSA P-256 (recommended format)
print_info "Generating ECDSA P-256 PKCS#8 PEM private key..."
openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 -out private_key_ecdsa_temp.pem 2>/dev/null
if [[ -f "private_key_ecdsa_temp.pem" ]]; then
    # Convert to PKCS#8 format
    openssl pkcs8 -topk8 -nocrypt -in private_key_ecdsa_temp.pem -out private_key_pkcs8.pem 2>/dev/null
    rm private_key_ecdsa_temp.pem
    
    if [[ -f "private_key_pkcs8.pem" ]]; then
        print_success "Created: private_key_pkcs8.pem (ECDSA P-256 PKCS#8 PEM format)"
    else
        echo "❌ Failed to create PKCS#8 PEM key"
        exit 1
    fi
else
    echo "❌ Failed to create temporary ECDSA key"
    exit 1
fi

# 2. Generate corresponding public key (ECDSA P-256)
print_info "Extracting ECDSA public key..."
openssl pkey -in private_key_pkcs8.pem -pubout -out public_key.pem 2>/dev/null
if [[ -f "public_key.pem" ]]; then
    print_success "Created: public_key.pem (ECDSA P-256 public key in PEM format)"
fi

# 3. Convert to DER format
print_info "Converting to DER format..."
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key_pkcs8.pem -out private_key.der -nocrypt 2>/dev/null
if [[ -f "private_key.der" ]]; then
    print_success "Created: private_key.der (DER binary format)"
fi

# 4. Extract Base64 format (raw ECDSA PKCS#8 without PEM headers)
print_info "Extracting Base64 format..."
grep -v "BEGIN\|END" private_key_pkcs8.pem | tr -d '\n' > private_key_base64.key
if [[ -f "private_key_base64.key" && -s "private_key_base64.key" ]]; then
    print_success "Created: private_key_base64.key (Raw Base64 ECDSA format)"
fi

# 5. Create multi-line Base64 format (ECDSA)
print_info "Creating multi-line Base64 format..."
grep -v "BEGIN\|END" private_key_pkcs8.pem > private_key_multiline.key
if [[ -f "private_key_multiline.key" && -s "private_key_multiline.key" ]]; then
    print_success "Created: private_key_multiline.key (Multi-line Base64 ECDSA)"
fi

# RSA keys are not supported - using ECDSA P-256 exclusively for modern security

# 6. Create test files for error handling
print_info "Creating test files for error handling..."

# Empty file
touch empty.key
print_success "Created: empty.key (Empty file for error testing)"

# Invalid key file
echo "This is not a valid cryptographic key" > invalid.key
print_success "Created: invalid.key (Invalid key data for error testing)"

# Corrupted PEM file
cat > corrupted.pem << 'EOF'
-----BEGIN PRIVATE KEY-----
CORRUPTED_ECDSA_DATA_HERE_NOT_VALID_BASE64
-----END PRIVATE KEY-----
EOF
print_success "Created: corrupted.pem (Corrupted ECDSA PEM for error testing)"

# 7. Display key information
echo ""
print_info "Key Information:"
echo "=================="

# Get public key from private key for display
if command -v base64 >/dev/null 2>&1; then
    # Try different base64 command formats for macOS vs Linux compatibility
    if base64 --help 2>&1 | grep -q "\-w"; then
        # Linux/GNU base64
        PUBLIC_KEY_STRING=$(openssl pkey -in private_key_pkcs8.pem -pubout -outform DER 2>/dev/null | base64 -w 0)
    else
        # macOS base64
        PUBLIC_KEY_STRING=$(openssl pkey -in private_key_pkcs8.pem -pubout -outform DER 2>/dev/null | base64)
    fi
else
    PUBLIC_KEY_STRING="base64 command not available"
fi

echo "📁 Files created:"
ls -la *.pem *.der *.key 2>/dev/null | while read line; do
    echo "   $line"
done

echo ""
echo "🔑 Public Key (Base64 DER format - ECDSA P-256):"
echo "$PUBLIC_KEY_STRING"

echo ""
echo "📋 Cryptographic Information:"
echo "============================"
echo "• Algorithm: ECDSA (Elliptic Curve Digital Signature Algorithm)"
echo "• Curve: P-256 (secp256r1) - NIST recommended curve"
echo "• Hash: SHA3-256 (used for signing operations)"
echo "• Key Size: 256-bit (equivalent to ~3072-bit RSA security but much faster)"
echo "• Standards: FIPS 186-4, RFC 6090, SEC 2"

echo ""
echo "📋 Usage Examples:"
echo "=================="
echo ""
echo "# Add block using ECDSA P-256 PKCS#8 PEM key (recommended):"
echo "java -jar target/blockchain-cli.jar add-block \"Your data here\" --key-file test-keys/private_key_pkcs8.pem"
echo ""
echo "# Add block using ECDSA DER key:"
echo "java -jar target/blockchain-cli.jar add-block \"Your data here\" --key-file test-keys/private_key.der"
echo ""  
echo "# Add block using ECDSA Base64 key:"
echo "java -jar target/blockchain-cli.jar add-block \"Your data here\" --key-file test-keys/private_key_base64.key"
echo ""
echo "# Test error handling with invalid key:"
echo "java -jar target/blockchain-cli.jar add-block \"Your data here\" --key-file test-keys/invalid.key"

echo ""
print_info "Conversion Commands:"
echo "===================="
echo ""
echo "# Generate new ECDSA P-256 PKCS#8 PEM key:"
echo "openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 | openssl pkcs8 -topk8 -nocrypt -out new_ecdsa_key.pem"
echo ""
echo "# Convert ECDSA PEM to DER:"
echo "openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key_pkcs8.pem -out private_key.der -nocrypt"
echo ""
echo "# Extract Base64 from ECDSA PEM:"
echo "grep -v 'BEGIN\\|END' private_key_pkcs8.pem | tr -d '\\n' > private_key_base64.key"
echo ""
echo "# Verify ECDSA key curve:"
echo "openssl pkey -in private_key_pkcs8.pem -text -noout | grep 'ASN1 OID'"

echo ""
print_warning "Security Notes:"
echo "==============="
echo "• These are TEST keys only - never use in production!"
echo "• These keys are NOT ENCRYPTED - they are stored in plain PKCS#8 format"
echo "• For PRODUCTION, use CLI with --store-private to encrypt keys with AES-256-GCM:"
echo "    java -jar target/blockchain-cli.jar add-key <owner> --generate --store-private"
echo "  This will:"
echo "    - Generate ECDSA P-256 key pair"
echo "    - Encrypt private key with AES-256-GCM (password-based)"
echo "    - Store encrypted key in private-keys/ directory"
echo "    - Add public key to blockchain's authorized keys"
echo ""
echo "• ECDSA P-256 provides equivalent security to 3072-bit RSA but with much better performance"
echo "• SHA3-256 provides enhanced cryptographic security over SHA-2"
echo "• Real private keys should be protected with strong passwords and AES-256-GCM encryption"
echo "• Store production keys in secure key management systems (HSM, TPM, or secure enclaves)"
echo "• Regularly rotate cryptographic keys in production environments"
echo "• Consider using hierarchical key derivation for large-scale deployments"

echo ""
print_success "🎉 ECDSA P-256 test key generation completed!"
print_info "All key formats are ready for testing --key-file functionality with modern cryptography"
print_info "RSA keys are not supported - ECDSA P-256 only for enhanced security and performance"
echo ""
print_warning "REMINDER: These unencrypted keys are for TESTING ONLY!"
print_info "Production keys should use AES-256-GCM encryption via --store-private option"

cd ..
echo ""
print_info "Test keys created at project root: $(pwd)/$KEYS_DIR"
print_info "You can now run CLI commands from the project root using:"
echo "  java -jar target/blockchain-cli.jar add-block \"data\" --key-file $KEYS_DIR/private_key_pkcs8.pem"
