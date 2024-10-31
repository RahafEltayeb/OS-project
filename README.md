# OS-project
Project of 2 

#Server
sudo useradd -m -d /home/machine1 machine1
sudo useradd -m -d /home/machine2 machine2
sudo passwd machine1 #machine1
sudo passwd machine2 #machine2
sudo apt-get install -y openssh-server
sudo apt-get install -y sshpass
scp -V 
sftp -V #check the versions of sftp and scp assuming they are already installed
#Client#1
sudo apt-get install -y openssh-client -y
sudo apt-get install -y sshpass
ssh machine1@172.20.10.3 #ssh connection with server

#Client#2
sudo apt-get install -y openssh-client -y
sudo apt-get install -y sshpass
sudo apt install net-tools
ssh machine2@172.20.10.3 #ssh connection with server

#Script 1
Setting up email with SMTP on Ubuntu Servers:
Configuration:
Command: sudo apt-get install msmtp msmtp-mta mailutils
Command: sudo nano /etc/msmtprc
# Set default values for all following accounts.
defaults

# Use the mail submission port 587 instead of the SMTP port 25.
port 587

# Always use TLS.
tls on

# Set a list of trusted CAs for TLS. The default is to use system settings, but
# you can select your own file.
tls_trust_file /etc/ssl/certs/ca-certificates.crt

# The SMTP server of your ISP
account outlook
host smtp-mail.outlook.com
from re1801755@qu.edu.qa
auth on
user re1801755@qu.edu.qa

# Set default account to isp
account default: outlook

# Map local users to mail addresses
aliases /etc/aliases
Install and set up mailx:
Command: sudo apt-get install bsd-mailx
Command: sudo nano /etc/mail.rc
et ask askcc append dot save crt
ignore Received Message-Id Resent-Message-Id Status Mail-From Return-Path Via Delivered-To
set mta=/usr/bin/msmtp
Set up aliases:
Command: sudo nano /etc/aliases
Send root to Rahaf
root: re1801755@qu.edu.qa

# Send everything else to admin
default: re1801755@qu.edu.qa
Command: sudo nano /etc/mail.rc
et ask askcc append dot save crt
ignore Received Message-Id Resent-Message-Id Status Mail-From Return-Path Via Delivered-To
set mta=/usr/bin/msmtp
alias root rootjane_doe@example.com


#Script 2
# This cron job runs ClientInfo.sh every hour and logs output
crontab -e
0 * * * * /home/rahaf/ClientInfo.sh >> /home/rahaf/cron_debug.log 2>&1
