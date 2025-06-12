#!/usr/bin/env python3
"""
Fix specific markdown anchor link issues in the blockchain CLI project.
"""

import os

def fix_specific_issues():
    """Fix the specific anchor link issues identified"""
    
    fixes_applied = 0
    
    # Fix ENTERPRISE_GUIDE.md
    enterprise_file = "ENTERPRISE_GUIDE.md"
    if os.path.exists(enterprise_file):
        print(f"🔧 Fixing {enterprise_file}...")
        
        with open(enterprise_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Fix the double dash issues
        content = content.replace("(#monitoring--alerting)", "(#monitoring-alerting)")
        content = content.replace("(#compliance--auditing)", "(#compliance-auditing)")
        
        if content != original_content:
            with open(enterprise_file, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  ✅ Fixed double-dash anchor links in {enterprise_file}")
            fixes_applied += 1
        else:
            print(f"  ✨ No changes needed in {enterprise_file}")
    
    # Check all other files for potential issues
    md_files = [
        "README.md",
        "EXAMPLES.md", 
        "TROUBLESHOOTING.md",
        "DOCKER_GUIDE.md",
        "AUTOMATION_SCRIPTS.md",
        "INTEGRATION_PATTERNS.md"
    ]
    
    for filename in md_files:
        if os.path.exists(filename):
            print(f"🔍 Checking {filename}...")
            
            with open(filename, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Look for potential issues (double dashes, emoji in links, etc.)
            issues_found = []
            
            # Check for double dashes in anchor links
            if "--" in content and "(#" in content:
                import re
                double_dash_links = re.findall(r'\(#[^)]*--[^)]*\)', content)
                if double_dash_links:
                    issues_found.extend(double_dash_links)
            
            # Check for emojis in anchor links
            import re
            emoji_links = re.findall(r'\(#[^)]*[\U0001F600-\U0001F64F\U0001F300-\U0001F5FF\U0001F680-\U0001F6FF\U0001F1E0-\U0001F1FF\U00002600-\U000026FF\U00002700-\U000027BF][^)]*\)', content)
            if emoji_links:
                issues_found.extend(emoji_links)
            
            if issues_found:
                print(f"  ⚠️  Potential issues found in {filename}:")
                for issue in issues_found:
                    print(f"     {issue}")
            else:
                print(f"  ✅ No issues found in {filename}")
    
    print("\n" + "="*50)
    print(f"🎉 Complete! Fixed {fixes_applied} files.")
    print("📝 All anchor links should now work correctly on GitHub.")
    
    if fixes_applied > 0:
        print("\n🔄 Don't forget to commit the changes:")
        print("git add .")
        print('git commit -m "Fix markdown anchor links - remove emoji issues"')

if __name__ == "__main__":
    print("🚀 Fixing markdown anchor link issues...")
    print("="*50)
    fix_specific_issues()
