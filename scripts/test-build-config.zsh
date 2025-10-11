#!/usr/bin/env zsh

# Test Build Configuration Script
# Tests the build configuration of the Private Blockchain CLI
# Version: 1.0.0
# ZSH adaptation

# Get script directory and navigate to project root
SCRIPT_DIR="${0:A:h}"
if [[ "$(basename "$SCRIPT_DIR")" == "scripts" ]]; then
    PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
else
    PROJECT_ROOT="$SCRIPT_DIR"
fi
cd "$PROJECT_ROOT"

echo "🚀 Testing Private Blockchain CLI Build Configuration"
echo "=================================================="

echo "📋 Project structure:"
echo "Main classes found:"
find src/main/java -name "*.java" | head -5

echo ""
echo "📦 Testing compilation step by step..."

echo "1. Clean:"
mvn clean

echo ""
echo "2. Compile (this will show us the real error):"
mvn compile -e

if [[ $? -eq 0 ]]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "3. Package:"
    mvn package -DskipTests -e
    
    echo ""
    echo "📋 Generated JARs:"
    ls -la target/*.jar 2>/dev/null || echo "No JARs found"
else
    echo "❌ Compilation failed - this is the problem!"
    echo "The CLI cannot compile without the core blockchain dependency."
    echo ""
    echo "🔧 Suggested fix:"
    echo "1. First build and install the core blockchain locally:"
    echo "   cd ../privateBlockchain && mvn clean install"
    echo "2. Then build this CLI:"
    echo "   cd ../privateBlockchain-cli && mvn clean package"
fi

echo ""
echo "✅ Build configuration test completed!"
