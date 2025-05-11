#!/bin/bash

yum update -y


yum install -y docker
service docker start
usermod -a -G docker ec2-user


docker pull python:latest
docker pull gcc:latest


yum install -y git


cd /home/ec2-user
git clone https://github.com/shrey209/Remote-Code-Execution-Engine.git
cd Remote-Code-Execution-Engine/RCE-GO

chmod +x rce-app


cd /home/ec2-user/Remote-Code-Execution-Engine/RCE-GO


sudo chown -R ec2-user:ec2-user /home/ec2-user/Remote-Code-Execution-Engine/RCE-GO


sudo chmod -R 755 /home/ec2-user/Remote-Code-Execution-Engine/RCE-GO


cat <<EOF > /etc/systemd/system/rce-app.service
[Unit]
Description=RCE Go Application
After=network.target

[Service]
ExecStart=/home/ec2-user/Remote-Code-Execution-Engine/RCE-GO/rce-app
User=ec2-user
Group=ec2-user
WorkingDirectory=/home/ec2-user/Remote-Code-Execution-Engine/RCE-GO
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF


systemctl daemon-reload
systemctl enable rce-app.service
systemctl start rce-app.service


# (sleep 10; reboot) &
