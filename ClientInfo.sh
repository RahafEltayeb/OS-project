SERVER=//servers IP needed
DESTINATION_PATH=// server path needed
while true; do //infinite loop to send info every hour
    TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S") //collect infrmation
    LOG_FILE="/tmp/process_info.log"  //temprory log file
    echo "Collecting system inforamtion by $TIMESTAMP" > "$LOG_FILE"
 echo "  process tree of all running processes : " >> "$LOG_FILE"
    ps aux >> "$LOG_FILE"  
    echo "" >> "$LOG_FILE"  //blank line to make it more  clear
    echo " Dead or zombie processes  :" >> "$LOG_FILE"
    ps aux | grep 'Z' >> "$LOG_FILE"  //search and add zombie processes
    echo "" >> "$LOG_FILE"  // blank line to make it more  clear
    echo "CPU usage information: " >> "$LOG_FILE"
    mpstat >> "$LOG_FILE"  // Add CPU usage stats to the log file
    echo "" >> "$LOG_FILE"  // blank line to make it more  clear
    echo "Memory usage of running processes:" >> "$LOG_FILE" //memory usage of running processes 
    ps -eo pid,comm,%mem --sort=-%mem | head -n 10 >> "$LOG_FILE"  // Add top memory consuming processes 
    echo "" >> "$LOG_FILE"
    echo "Top 5 processes using the most CPU:" >> "$LOG_FILE"
    ps -eo pid,comm,%cpu --sort=-%cpu | head -n 6 >> "$LOG_FILE"  // Adding top consuming  processes
    echo "Transferring log file to the server. Please wait.."
    scp "$LOG_FILE" user@$SERVER:"$DESTINATION_PATH"  
    rm "$LOG_FILE"  // Remove the local log file
    echo "Log file has been sent and deleted from local storage. All done!"
    sleep 3600 //wait 1 hour before the next loop
   done




