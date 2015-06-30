#!/bin/bash
kill -15 $(pgrep -f org.wso2.carbon);
echo $?;
