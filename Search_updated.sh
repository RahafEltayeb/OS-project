#!/bin/bash

# output file
output_file="bigfile"

# Read password from stdin
password="Rg66177^_^^_^"

# Find files > 1MB and store them in bigfile
find ~ -type f -size +1M > "$output_file"

# date, number of files found
search_date=$(date)
file_count=$(wc -l < "$output_file")

echo "Search Date: $search_date" >> "$output_file"
echo "Number of files larger than 1MB: $file_count" >> "$output_file"

if [ -s "$output_file" ]; then # Check if 'bigfile' is not empty
    {
      echo "Subject: Files larger than 1MB found on VM2"
      echo "To: re1801755@qu.edu.qa"
      echo ""  # Insert a blank line between headers and body
      cat "$output_file"
    } | msmtp --from=re1801755@qu.edu.qa --auth=on --passwordeval="echo $password" -t re1801755@qu.edu.qa
fi
