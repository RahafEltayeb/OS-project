#!/bin/bash

# Log file paths
disk_log="disk_info.log"
mem_cpu_log="mem_cpu_info.log"

# Gather Disk Information
echo "Disk Space Usage of HOME directory" | tee "$disk_log"
df -h ~ | tee -a "$disk_log"  # Disk space usage of HOME directory
echo "=========================================" | tee -a "$disk_log"
echo "Disk Usage for directories" | tee -a "$disk_log"
du -h ~ | tee -a "$disk_log"  # Disk usage of directories and subdirectories
echo "Disk information saved to: $disk_log" 


# Gather Memory and CPU Information
echo "Memory and CPU Information " > "$mem_cpu_log" #| tee "$mem_cpu_log"
echo "Free and Used Memory: " >> "$mem_cpu_log"
free -h | awk '/Mem:/ {print "Used: "$3", Free: "$4}' >> "$mem_cpu_log"

echo "CPU Model: " >> "$mem_cpu_log"
lscpu | grep "Model name:" | awk -F': ' '{print $2}' >> "$mem_cpu_log"

echo "CPU Core Count:" >> "$mem_cpu_log"
lscpu | grep "^CPU(s):" | awk -F': ' '{print $2}' >> "$mem_cpu_log"

echo "Memory and CPU information saved to: $mem_cpu_log" 
