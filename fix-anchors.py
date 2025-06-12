#!/usr/bin/env python3
"""
Script to fix markdown anchor links by removing emojis from section titles
and ensuring table of contents links match GitHub's auto-generated anchors.
"""

import re
import os
import unicodedata

def remove_emojis(text):
    """Remove emojis from text"""
    return re.sub(r'[\U0001F600-\U0001F64F\U0001F300-\U0001F5FF\U0001F680-\U0001F6FF\U0001F1E0-\U0001F1FF\U00002600-\U000026FF\U00002700-\U000027BF]', '', text)

def create_github_anchor(title):
    """Create GitHub-style anchor from title"""
    # Remove leading ## and whitespace
    title = re.sub(r'^#+\s*', '', title)
    # Remove emojis
    title = remove_emojis(title)
    # Convert to lowercase
    title = title.lower()
    # Replace spaces and special chars with hyphens
    title = re.sub(r'[^\w\s-]', '', title)
    title = re.sub(r'[-\s]+', '-', title)
    # Remove leading/trailing hyphens
    title = title.strip('-')
    return f"#{title}"

def process_markdown_file(filepath):
    """Process a markdown file to fix anchor links"""
    print(f"Processing {filepath}...")
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Find all headers and create mapping
    headers = re.findall(r'^## (.+)$', content, re.MULTILINE)
    anchor_map = {}
    
    for header in headers:
        original_anchor = create_github_anchor(header)
        # Store for reference
        clean_title = remove_emojis(header).strip()
        anchor_map[clean_title.lower()] = original_anchor
        print(f"  Header: '{header}' -> Anchor: '{original_anchor}'")
    
    return anchor_map

def main():
    """Main function"""
    md_files = [
        'README.md',
        'EXAMPLES.md',
        'TROUBLESHOOTING.md',
        'DOCKER_GUIDE.md',
        'ENTERPRISE_GUIDE.md',
        'AUTOMATION_SCRIPTS.md',
        'INTEGRATION_PATTERNS.md'
    ]
    
    print("Analyzing all markdown files for header anchors...")
    print("=" * 60)
    
    for filename in md_files:
        if os.path.exists(filename):
            anchor_map = process_markdown_file(filename)
            print()
    
    print("Analysis complete. Check output above for any issues.")
    print("\nTo fix issues, manually correct the table of contents")
    print("to use the anchors shown above.")

if __name__ == "__main__":
    main()
