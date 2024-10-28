#!/bin/bash


# Trace the route to the target VM
echo -e "\nTracing route to $1..." | tee -a network.log
traceroute "$1" | tee -a network.log

# Display the routing table
echo -e "Routing table:\n" | tee -a network.log #-e to use escape 
netstat -nr | tee -a network.log #n: numerical ip , r: routing table

# Display the hostname of the server
echo -e "\nHostname: $(hostname)" | tee -a network.log


# Test the local DNS server by resolving google.com
echo -e "\n dns lookup for google.com" | tee -a network.log
nslookup google.com | tee -a network.log
 

# Trace the route to google.com
echo -e "\nTracing route to google.com..." | tee -a network.log
traceroute google.com | tee -a network.log

# Ping google.com to check internet connectivity
echo -e "\nPinging google.com to check connectivity:" | tee -a network.log
if ping -c 4 google.com | tee -a network.log ; then
    echo "google.com is reachable" | tee -a network.log
else
    echo "google.com is unreachable. Rebooting the machine..." | tee -a network.log
    sudo reboot
fi

