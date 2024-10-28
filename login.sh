#!/bin/bash

attempt=0
LIMIT=3
flag=0
rm invalid_attempts.log
while [ $attempt -lt $LIMIT ]
do
 echo "Enter Server Username"
 #user prompt
 read serveruser
 echo "Enter Server Password"
 #user prompt
 read -s serverpass

 sshpass -p $serverpass ssh -o StrictHostKeyChecking=no $serveruser@192.168.10.27
 
 if [ $? -ne 0 ]
  then
   time_stamp=$(date +%F_%T)
   echo $time_stamp | tee -a invalid_attempts.log >> client_invalid_attempts.log
   echo $serveruser | tee -a invalid_attempts.log >> client_invalid_attempts.log
   attempt=$((attempt+1))
  else
   break
  fi
done
if [ $attempt -gt $LIMIT ]
then
 echo "Unauthorized user!"
 # Append local file content to remote file using ssh with a 30-second timeout
 
 timeout 30s sshpass -p "pass1" sftp "client1@192.168.10.27" <<EOF
        
put client_invalid_attempts.log
EOF
fi

 #echo "logout in 30 sec"

 exit