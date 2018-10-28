#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

export TZ=":America/New_York";
HOUR=`date +%H`;
MINUTE=`date +%M`;
DAY=`date +%d`;
MONTH=`date +%m`;
DAYOFWEEK=`date +%u`;

if [[ "$agtServer"="agtapp01" ]] 
then
	######## Agent Box to place trigger file automatically at set time for triggering new orders to be Scheduled - 09:10 EST, 14:40 EST, and 00:35 EST ########
	if [[ "$MINUTE" == "10" && "$HOUR" == "09" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Scheduled - 09:10 EST, 14:40 EST, and 00:35 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_NEW_AGENT.txt;
	fi
	if [[ "$MINUTE" == "40" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Scheduled - 09:10 EST, 14:40 EST, and 00:35 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_NEW_AGENT.txt;
	fi
	if [[ "$MINUTE" == "35" && "$HOUR" == "00" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Scheduled - 09:10 EST, 14:40 EST, and 00:35 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_NEW_AGENT.txt;
	fi
	
	######## Agent Box to place trigger file automatically at set time for triggering back orders to be Scheduled - 09:00 EST, 14:30 EST, and 23:00 EST ########
	if [[ "$MINUTE" == "00" && "$HOUR" == "09" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering back orders to be Scheduled - 09:00 EST, 14:30 EST, and 23:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_BO_AGENT.txt;
	fi
	if [[ "$MINUTE" == "30" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering back orders to be Scheduled - 09:00 EST, 14:30 EST, and 23:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_BO_AGENT.txt;
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "23" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering back orders to be Scheduled - 09:00 EST, 14:30 EST, and 23:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_SCHEDULE_BO_AGENT.txt;
	fi
	
	######## Agent Box to place trigger file automatically at set time for triggering new orders to be Released - 00:45 EST, 09:20 EST, and 14:50 EST ########
	if [[ "$MINUTE" == "45" && "$HOUR" == "00" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Released - 00:45 EST, 09:20 EST, and 14:50 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_RELEASE_AGENT.txt;
	fi
	if [[ "$MINUTE" == "20" && "$HOUR" == "09" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Released - 00:45 EST, 09:20 EST, and 14:50 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_RELEASE_AGENT.txt;
	fi
	if [[ "$MINUTE" == "50" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for triggering new orders to be Released - 00:45 EST, 09:20 EST, and 14:50 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		echo "trigger file for schedule agent, leave this file alone!" > /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent/OMS_RELEASE_AGENT.txt;
	fi
fi