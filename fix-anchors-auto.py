#!/usr/bin/env python3
"""
Script to automatically fix all markdown anchor links in the blockchain CLI project.
"""

import re
import os

def fix_markdown_file(filepath, fixes):
    """Apply fixes to a markdown file"""
    if not os.path.exists(filepath):
        print(f"❌ File not found: {filepath}")
        return False
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    changes_made = 0
    
    print(f"🔧 Fixing {filepath}...")
    
    for old_link, new_link in fixes:
        if old_link in content:
            content = content.replace(old_link, new_link)
            changes_made += 1
            print(f"  ✅ Fixed: {old_link} → {new_link}")
    
    if changes_made > 0:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"  💾 Saved {changes_made} changes to {filepath}")
        return True
    else:
        print(f"  ✨ No changes needed in {filepath}")
        return False

def main():
    """Main function to fix all markdown files"""
    print("🚀 Fixing markdown anchor links...")
    print("=" * 50)
    
    # Fixes for README.md
    readme_fixes = [
        # These should already be correct, but double-checking
        ("(#overview)", "(#overview)"),  # ✅ Already correct
        ("(#prerequisites)", "(#prerequisites)"),  # ✅ Already correct  
        ("(#installation)", "(#installation)"),  # ✅ Already correct
        ("(#quick-start)", "(#quick-start)"),  # ✅ Already correct
        ("(#commands)", "(#commands)"),  # ✅ Already correct
        ("(#building-from-source)", "(#building-from-source)"),  # ✅ Already correct
        ("(#basic-testing)", "(#basic-testing)"),  # ✅ Already correct
        ("(#technical-details)", "(#technical-details)"),  # ✅ Already correct
        ("(#documentation)", "(#documentation)"),  # ✅ Already correct
        ("(#license)", "(#license)"),  # ✅ Already correct
    ]
    
    # Fixes for EXAMPLES.md
    examples_fixes = [
        ("(#quick-start-examples)", "(#quick-start-examples)"),  # ✅ Already correct
        ("(#real-world-use-cases)", "(#real-world-use-cases)"),  # ✅ Already correct
        ("(#advanced-scenarios)", "(#advanced-scenarios)"),  # ✅ Already correct
        ("(#docker-examples)", "(#docker-examples)"),  # ✅ Already correct
        ("(#automation-scripts)", "(#automation-scripts)"),  # ✅ Already correct
    ]
    
    # Fixes for TROUBLESHOOTING.md
    troubleshooting_fixes = [
        ("(#diagnostic-commands)", "(#diagnostic-commands)"),  # ✅ Already correct
        ("(#common-scenarios)", "(#common-scenarios)"),  # ✅ Already correct
        ("(#step-by-step-guide)", "(#step-by-step-guide)"),  # ✅ Already correct
        ("(#environment-issues)", "(#environment-issues)"),  # ✅ Already correct
        ("(#performance-problems)", "(#performance-problems)"),  # ✅ Already correct
        ("(#docker-issues)", "(#docker-issues)"),  # ✅ Already correct
        ("(#debug-mode)", "(#debug-mode)"),  # ✅ Already correct
    ]
    
    # Fixes for DOCKER_GUIDE.md
    docker_fixes = [
        ("(#quick-start)", "(#quick-start)"),  # ✅ Already correct
        ("(#installation)", "(#installation)"),  # ✅ Already correct
        ("(#basic-usage)", "(#basic-usage)"),  # ✅ Already correct
        ("(#docker-compose)", "(#docker-compose)"),  # ✅ Already correct
        ("(#volume-management)", "(#volume-management)"),  # ✅ Already correct
        ("(#production-usage)", "(#production-usage)"),  # ✅ Already correct
        ("(#troubleshooting)", "(#troubleshooting)"),  # ✅ Already correct
    ]
    
    # Fixes for ENTERPRISE_GUIDE.md
    enterprise_fixes = [
        ("(#security-best-practices)", "(#security-best-practices)"),  # ✅ Already correct
        ("(#performance-best-practices)", "(#performance-best-practices)"),  # ✅ Already correct
        ("(#operational-best-practices)", "(#operational-best-practices)"),  # ✅ Already correct
        ("(#monitoring--alerting)", "(#monitoring-alerting)"),  # ❌ Needs fix: & should be removed
        ("(#compliance--auditing)", "(#compliance-auditing)"),  # ❌ Needs fix: & should be removed
        ("(#production-deployment)", "(#production-deployment)"),  # ✅ Already correct
    ]
    
    # Fixes for AUTOMATION_SCRIPTS.md
    automation_fixes = [
        ("(#daily-operations-scripts)", "(#daily-operations-scripts)"),  # ✅ Already correct
        ("(#backup-and-recovery-scripts)", "(#backup-and-recovery-scripts)"),  # ✅ Already correct
        ("(#monitoring-and-health-checks)", "(#monitoring-and-health-checks)"),  # ✅ Already correct
        ("(#cicd-integration-scripts)", "(#cicd-integration-scripts)"),  # ✅ Already correct
        ("(#maintenance-scripts)", "(#maintenance-scripts)"),  # ✅ Already correct
        ("(#docker-automation)", "(#docker-automation)"),  # ✅ Already correct
        ("(#troubleshooting-scripts)", "(#troubleshooting-scripts)"),  # ✅ Already correct
    ]
    
    # Fixes for INTEGRATION_PATTERNS.md
    integration_fixes = [
        ("(#cicd-integration)", "(#cicd-integration)"),  # ✅ Already correct
        ("(#rest-api-integration)", "(#rest-api-integration)"),  # ✅ Already correct
        ("(#monitoring-integration)", "(#monitoring-integration)"),  # ✅ Already correct
        ("(#enterprise-systems-integration)", "(#enterprise-systems-integration)"),  # ✅ Already correct
        ("(#cloud-integration-patterns)", "(#cloud-integration-patterns)"),  # ✅ Already correct
        ("(#security-integration)", "(#security-integration)"),  # ✅ Already correct
    ]
    
    # Apply fixes to each file
    files_to_fix = [
        ("README.md", readme_fixes),
        ("EXAMPLES.md", examples_fixes),
        ("TROUBLESHOOTING.md", troubleshooting_fixes),
        ("DOCKER_GUIDE.md", docker_fixes),
        ("ENTERPRISE_GUIDE.md", enterprise_fixes),
        ("AUTOMATION_SCRIPTS.md", automation_fixes),
        ("INTEGRATION_PATTERNS.md", integration_fixes),
    ]
    
    total_files_changed = 0
    
    for filename, fixes in files_to_fix:
        if fix_markdown_file(filename, fixes):
            total_files_changed += 1
        print()
    
    print("=" * 50)
    print(f"🎉 Process completed! {total_files_changed} files were modified.")
    print("📝 All anchor links should now work correctly on GitHub.")

if __name__ == "__main__":
    main()
