#!/bin/bash

# Log file path
log_file="perm_change.log"

# Create or clear the log file
echo "Permission Changes Log" > "$log_file"

# Search all files with 777 permissions
echo "Searching for files with 777 permissions..." | tee -a "$log_file"
files=$(find / -type f -perm 777 2>/dev/null)

# Check if any files were found
if [ -z "$files" ]; then
    echo "No files with 777 permissions found." | tee -a "$log_file"
    
else
    # Display found files and change their permissions
    echo "Found files with 777 permissions:" | tee -a "$log_file"
    echo "$files" | tee -a "$log_file"
    echo "Check permission of file before change: " >> "$log_file"
    ls -l "$files" >> "$log_file" #to check permission changes
    for file in $files; do
        chmod 700 "$file"  # Change permission to 700
        echo "Changed permissions for: $file" >> "$log_file"
        ls -l "$files" >> "$log_file" #to check permission changes
    done
fi


echo "Permission check and changes completed and saved to $log_file"
