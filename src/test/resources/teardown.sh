#!/bin/bash
cat $1/wso2carbon.pid | head -n1 | cut -d " " -f1  | xargs -n 1 /bin/kill -15
#kill -15 $(pgrep -f org.wso2.carbon);
echo $?;
