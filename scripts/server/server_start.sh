#!/usr/bin/env bash
cd /home/ec2-user/server || exit
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ec2-user/server/amazon-cloudwatch-agent.json
sudo java -jar -Dserver.port=80 -Dspring.profiles.active=prod \
    *.jar > /dev/null 2> /dev/null < /dev/null &