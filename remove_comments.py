import os
import re

def remove_comments(text):
    def replacer(match):
        s = match.group(0)
        if s.startswith('/'):
            # It's a comment
            return " " if s.startswith('/*') else ""
        else:
            # It's a string or character literal, keep it
            return s

    # Regex to capture:
    # 1. Double quoted strings: "..."
    # 2. Single quoted chars/strings: '...'
    # 3. Block comments: /* ... */
    # 4. Line comments: // ... (to end of line)
    pattern = re.compile(
        r'("[^"\\]*(?:\\.[^"\\]*)*"|' + 
        r"'[^'\\]*(?:\\.[^'\\]*)*'|" + 
        r'/\*.*?\*/|' + 
        r'//.*?$)',
        re.DOTALL | re.MULTILINE
    )
    return re.sub(pattern, replacer, text)

target_dir = r"C:\Users\PC\Desktop\javatraining\hrms-service\src\main\java"

print(f"Scanning {target_dir}...")
count = 0
for root, dirs, files in os.walk(target_dir):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            try:
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                new_content = remove_comments(content)
                
                if new_content != content:
                    with open(path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    print(f"Cleaned: {file}")
                    count += 1
            except Exception as e:
                print(f"Error processing {file}: {e}")

print(f"Done. Processed {count} files.")
