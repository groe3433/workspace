#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";
	
if [ "$agtServer" = "agtapp02" ] 
then
	echo "Date: $(date)" >> /opt/ssfs/runtime/logs/OMS_INV_PURGE.log; 
	nohup /opt/ssfs/runtime/bin/triggeragent.sh INVENTORYPRG >> /opt/ssfs/runtime/logs/OMS_INV_PURGE.log 2>&1 &
fi