#!/usr/bin/env bash
cd /home/ec2-user/server || exit
sudo java -jar -Dserver.port=80 \
    *.jar > /dev/null 2> /dev/null < /dev/null &