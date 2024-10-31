#!/bin/bash

# output file
output_file="bigfile"

# Find files > 1MB+store them in bigfile
find ~ -type f -size +1M > "$output_file"

# date, number of files found
search_date=$(date)
file_count=$(wc -l < "$output_file")

echo "Search Date: $search_date" >> "$output_file"
echo "Number of files larger than 1MB: $file_count" >> "$output_file"


if [ -s "$output_file" ] #Check if 'bigfile' is not empty
then
    {
      echo "Subject: Files larger than 1MB found on VM2"
      echo "To: re1801755@qu.edu.qa"
      echo ""  #Insert a blank line between headers and body
      cat "$output_file"
    } | msmtp --from=re1801755@qu.edu.qa -t re1801755@qu.edu.qa
fi
