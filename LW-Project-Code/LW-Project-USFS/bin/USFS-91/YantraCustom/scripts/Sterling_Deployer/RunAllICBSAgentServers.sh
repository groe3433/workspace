#!/bin/sh

SERVERS="NWCGAgent BillingConsume TrackableConsume NWCGGetOperationResultsAgent NWCGMonitorAgent NWCGOrderStatusMonitorAgent NWCGIBIntServer NWCGOBIntServer NWCGCountAgent1 NWCGPurgeAgent"

for server in $SERVERS

do

echo starting $server
$HOME/Sterling_Deployer/in.sh $server
sleep 1

done
