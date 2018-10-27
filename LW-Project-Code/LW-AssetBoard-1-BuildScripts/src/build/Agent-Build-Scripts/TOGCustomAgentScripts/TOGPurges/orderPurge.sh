#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";
	
if [ "$agtServer" = "agtapp02" ] 
then
	echo "Date: $(date)" >> /opt/ssfs/runtime/logs/OMS_CLOSE_ORDER.log; 
	nohup /opt/ssfs/runtime/bin/triggeragent.sh PURGE >> /opt/ssfs/runtime/logs/OMS_CLOSE_ORDER.log 2>&1 &
fi