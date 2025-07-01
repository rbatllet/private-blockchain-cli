#!/usr/bin/env zsh

# Quick test for the demo script
echo "Testing demo script syntax and basic functionality..."

# Check if script exists
if [[ ! -f "run-enhanced-demos.zsh" ]]; then
    echo "❌ Demo script not found!"
    exit 1
fi

# Check syntax
if zsh -n run-enhanced-demos.zsh; then
    echo "✅ Script syntax is valid"
else
    echo "❌ Script syntax error found"
    exit 1
fi

# Test help option
echo "Testing help option..."
if ./run-enhanced-demos.zsh --help >/dev/null; then
    echo "✅ Help option works"
else
    echo "❌ Help option failed"
    exit 1
fi

echo "✅ Demo script basic tests passed!"
echo ""
echo "To run the demo:"
echo "  ./run-enhanced-demos.zsh --auto    # Automatic mode"
echo "  ./run-enhanced-demos.zsh           # Interactive mode"