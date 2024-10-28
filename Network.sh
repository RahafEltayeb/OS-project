#!/bin/bash

i=0
flag=0
while [ $i -lt 3 ] 
do
 if ping -c 4 -W 5 "$1"; then 
  flag=1
 fi
i=$((i+1))
done
if [ $flag -eq 1 ] 
then
 echo "$(date +"%Y-%m-%d %H:%M:%S") : Connectivity with $1 is ok." 
else
 echo "$(date +"%Y-%m-%d %H:%M:%S") : Ping fail" | tee -a network.log
 ./traceroute.sh $1
fi

