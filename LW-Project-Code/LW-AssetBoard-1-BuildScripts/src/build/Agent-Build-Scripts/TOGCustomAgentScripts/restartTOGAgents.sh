#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

echo "restart happening, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt;
chmod 775 /opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt;

if [ "$agtServer" = "agtapp02" ]
then
	######## Startup Health Monitor Properly
	/opt/ssfs/runtime/TOGCustomAgentScripts/restartHealthMonitorAgent.sh;
fi
sleep 1m;
nohup /opt/ssfs/runtime/TOGCustomAgentScripts/stopAgentServers.sh &
sleep 2m;
nohup /opt/ssfs/runtime/TOGCustomAgentScripts/startAgentServers.sh &
sleep 6m;

rm /opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt;