#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

TriggerAgentHome=/opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent;
SterlingHome=/opt/ssfs/runtime/bin;

releaseTempFile="$TriggerAgentHome/OMS_RELEASE_AGENT.txt";

if [ -f "$releaseTempFile" ] 
then
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ReleaseAgentTrigger.log;
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ReleaseAgentTrigger.log;
	echo "Entered Trigger Release Agent..." >> $TriggerAgentHome/logs/ReleaseAgentTrigger.log;

	$SterlingHome/triggeragent.sh OMS_RELEASE_AGENT >> $TriggerAgentHome/logs/ReleaseAgentTrigger.log;
	echo "Your request to trigger the Release Agent has been received. Thank you. " | mailx -s "Release Process" $MANUAL_PROCESS;		

	rm $releaseTempFile;

	echo "Exiting Trigger Release Agent..." >> $TriggerAgentHome/logs/ReleaseAgentTrigger.log;
fi