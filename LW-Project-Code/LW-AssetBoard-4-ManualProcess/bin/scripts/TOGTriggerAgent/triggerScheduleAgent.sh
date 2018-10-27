#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

TriggerAgentHome=/opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent;
SterlingHome=/opt/ssfs/runtime/bin;

scheduleBackOrderTempFile="$TriggerAgentHome/OMS_SCHEDULE_BO_AGENT.txt";

if [ -f "$scheduleBackOrderTempFile" ] 
then
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ScheduleBackOrderAgentTrigger.log;
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ScheduleBackOrderAgentTrigger.log;
	echo "Entered Trigger Schedule Back Order Agent..." >> $TriggerAgentHome/logs/ScheduleBackOrderAgentTrigger.log;

	$SterlingHome/triggeragent.sh OMS_SCHEDULE_BO_AGENT >> $TriggerAgentHome/logs/ScheduleBackOrderAgentTrigger.log;
	echo "Your request to trigger the Schedule Back Order Agent has been received. Thank you. " | mailx -s "Schedule Back Order Process" $MANUAL_PROCESS;

	rm $scheduleBackOrderTempFile;

	echo "Exiting Trigger Schedule Back Order Agent..." >> $TriggerAgentHome/logs/ScheduleBackOrderAgentTrigger.log;
fi

scheduleTempFile="$TriggerAgentHome/OMS_SCHEDULE_NEW_AGENT.txt";

if [ -f "$scheduleTempFile" ] 
then
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ScheduleAgentTrigger.log;
	echo "___________________________________________________________________" >> $TriggerAgentHome/logs/ScheduleAgentTrigger.log;
	echo "Entered Trigger Schedule Agent..." >> $TriggerAgentHome/logs/ScheduleAgentTrigger.log;

	$SterlingHome/triggeragent.sh OMS_SCHEDULE_NEW_AGENT >> $TriggerAgentHome/logs/ScheduleAgentTrigger.log;
	echo "Your request to trigger the Schedule Agent has been received. Thank you. " | mailx -s "Schedule Process" $MANUAL_PROCESS;	

	rm $scheduleTempFile;

	echo "Exiting Trigger Schedule Agent..." >> $TriggerAgentHome/logs/ScheduleAgentTrigger.log;
fi