#!/bin/bash

sudo yum -y update
sudo yum -y install ruby
sudo yum -y install wget
cd /home/ec2-user
wget https://aws-codedeploy-ap-southeast-2.s3.ap-southeast-2.amazonaws.com/latest/install
sudo chmod +x ./install
sudo ./install auto
sudo yum -y install java-17-amazon-corretto-devel
