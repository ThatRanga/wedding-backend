#!/bin/bash

sudo yum -y update
sudo yum -y install ruby
sudo yum -y install wget
sudo yum -y install java-17-amazon-corretto-devel
sudo yum -y install amazon-cloudwatch-agent
mkdir /home/ec2-user/wedding-backend/logs
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ec2-user/amazon-cloudwatch-agent.json
cd /home/ec2-user
wget https://aws-codedeploy-ap-southeast-2.s3.ap-southeast-2.amazonaws.com/latest/install
sudo chmod +x ./install
sudo ./install auto
