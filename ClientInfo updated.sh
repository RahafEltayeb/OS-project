SERVER=172.20.10.3
DESTINATION_PATH=/home/machine2/process_info.log


    TIMESTAMP=$(date +"%Y-%m-%d %H:%M:%S") #collect infrmation
    LOG_FILE="/home/rahaf/process_info.log"  #temprory log file
    
    echo "Collecting system inforamtion by $TIMESTAMP" > "$LOG_FILE"
    echo "  process tree of all running processes : " >> "$LOG_FILE"

   pstree -p >> "$LOG_FILE" 
    echo "" >> "$LOG_FILE"  #blank line to make it more  clear
    echo " Dead or zombie processes  :" >> "$LOG_FILE"
    ps aux | grep 'Z' >> "$LOG_FILE"  #search and add zombie processes
    echo "" >> "$LOG_FILE"  # blank line to make it more  clear
    echo "CPU usage information: " >> "$LOG_FILE"
    mpstat >> "$LOG_FILE"  #Add CPU usage stats to the log file
    echo "" >> "$LOG_FILE"  # blank line to make it more  clear
    echo "Memory usage of running processes:" >> "$LOG_FILE" #memory usage of running processes 
    ps -eo pid,comm,%mem --sort=-%mem | head -n 10 >> "$LOG_FILE"  #Add top memory consuming processes 
    echo "" >> "$LOG_FILE"
    echo "Top 5 processes using the most CPU:" >> "$LOG_FILE"
    ps -eo pid,comm,%cpu --sort=-%cpu | head -n 6 >> "$LOG_FILE"  # Adding top consuming  processes
    echo "Transferring log file to the server. Please wait.."
    sshpass -p "machine2" scp "$LOG_FILE" machine2@$SERVER:"$DESTINATION_PATH"  
    rm "$LOG_FILE"  #Remove the local log file
    echo "Log file has been sent and deleted from local storage. All done!"




