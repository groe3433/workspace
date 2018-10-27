#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

noOfPID=$(ps -ef | grep com.yantra.ycp.hm.HealthMonitor | grep java_wrapper.sh | wc -l);

if [ "$noOfPID" = "0" ] 
then		
	echo "Date: $(date) :: Number of HeatlthMonitor Agent Processes :: $noOfPID" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***** HealthMonitorAgent is down! Restarting it before continuing! ********************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "Date: $(date) :: HealthMonitor Status Check :: DOWN, it will be started! You should login to System Management Console and clear the cache!" | mail -s "$SMCFS_ENV_NAME - HealthMonitor Status Check" $MORE_DETAILED_EMAIL	
	nohup /opt/ssfs/runtime/TOGCustomAgentScripts/startHealthMonitorAgentServer.sh &
	echo "***** Health Monitor Agent is running again. ******************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
elif [ "$noOfPID" = "1" ] 
then
	echo "Date: $(date) :: Number of HeatlthMonitor Agent Processes :: $noOfPID" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log		
	echo "***** HealthMonitor Agent is running. *************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
else		
	echo "Date: $(date) :: Number of HeatlthMonitor Agent Processes :: $noOfPID" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***** Multiple HealthMonitor Agents are running! Restarting it before continuing! *****" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	ps -ef | grep com.yantra.ycp.hm.HealthMonitor | grep com.sterlingcommerce.woodstock.noapp.NoAppLoader | awk '{print "kill -9 " $2}' | sh;
	echo "***** Multiple HealthMonitor Agents have been killed! *********************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***** Starting 1 HealthMonitor Agent. *************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "Date: $(date) :: HealthMonitor Status Check :: MULTIPLE Instances Running, all will be shutdown and 1 will be started! You should login to System Management Console and clear the cache!" | mail -s "$SMCFS_ENV_NAME - HealthMonitor Status Check" $MORE_DETAILED_EMAIL
	nohup /opt/ssfs/runtime/TOGCustomAgentScripts/startHealthMonitorAgentServer.sh &
	echo "***** HealthMonitor Agent is running again. *******************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
fi