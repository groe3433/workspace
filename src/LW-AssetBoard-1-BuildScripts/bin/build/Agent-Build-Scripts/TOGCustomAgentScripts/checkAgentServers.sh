#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

agtServer="$(echo $HOSTNAME | cut -c11-18)";
agthost="$(echo $HOSTNAME | cut -c1-18)";

if [ -f "/opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt" ] 
then
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
	echo "Date: $(date) :: A Restart Is Happening..." >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;	
else
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;

	######## Assume All Agents are Running Correctly, set to 0. Set to 1 if ANY agent is down or running improperly. 
	restartRequired=0;
	
	######## CHECK Agents and Integration Servers, set restartRequired to 1 if any server needs to be restarted. 
	if [ -f "/opt/ssfs/runtime/$agthost.agent.txt" ] 
	then
		echo "Date: $(date) :: Reading $agthost.agent.txt" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
		while read AGENT_SERVER_NAME || [[ -n "$AGENT_SERVER_NAME" ]]
		do
			echo "Date: $(date) :: Checking $AGENT_SERVER_NAME" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			pidOFAgentServer="$(ps -ef | grep wasuser | grep $AGENT_SERVER_NAME | grep java_wrapper.sh | wc -l)";
			if [ "$pidOFAgentServer" = 1 ] 
			then
				echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: OK" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			else
				echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: DOWN" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;			
				restartRequired=1;
			fi
		done < /opt/ssfs/runtime/$agthost.agent.txt;
	fi
	if [ -f "/opt/ssfs/runtime/$agthost.integration.txt" ] 
	then
		echo "Date: $(date) :: Reading $agthost.integration.txt" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
		while read INTEGRATION_SERVER_NAME || [[ -n "$INTEGRATION_SERVER_NAME" ]]
		do
			echo "Date: $(date) :: Checking $INTEGRATION_SERVER_NAME" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			pidOFIntegrationServer="$(ps -ef | grep wasuser | grep $INTEGRATION_SERVER_NAME | grep java_wrapper.sh | wc -l)";
			if [ "$pidOFIntegrationServer" = 1 ] 
			then
				echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: OK" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			else
				echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: DOWN" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
				restartRequired=1;
			fi			
		done < /opt/ssfs/runtime/$agthost.integration.txt;		
	fi			
	
	######## Determine if a restart is required
	if [ "$restartRequired" = 1 ] 
	then
		echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
		
		######## Set a Check/Restart Indicator in case the process runs long. 
		echo "Date: $(date) :: Restarting Agents..." >> /opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt;	

		if [ "$agtServer" = "agtapp02" ]
		then
			######## Stop Health Monitor Agent
			count=0;
			echo "Date: $(date) :: Stopping HealthMonitor" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			pkill -f "HealthMonitor";
			while pgrep -u $USER -f "HealthMonitor" > /dev/null;
			do
				if [ $count -gt 60 ]; then
					pkill -9 -f "HealthMonitor";
				fi
				count=$(($count+1));
				echo -n .;
			done
			######## Startup Health Monitor Properly
			echo "***** HealthMonitorAgent is down! Restarting it before continuing! ********************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
			nohup /opt/ssfs/runtime/TOGCustomAgentScripts/startHealthMonitorAgentServer.sh &
			echo "***** Health Monitor Agent is running again. ******************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log
		fi
		sleep 1m;	
		if [ -f "/opt/ssfs/runtime/$agthost.agent.txt" ] 
		then
			echo "Date: $(date) :: Reading $agthost.agent.txt" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			while read AGENT_SERVER_NAME || [[ -n "$AGENT_SERVER_NAME" ]]
			do
				pidOFAgentServer="$(ps -ef | grep wasuser | grep $AGENT_SERVER_NAME | grep java_wrapper.sh | wc -l)";
				if [ "$pidOFAgentServer" = 1 ] 
				then
					echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: OK" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
				else
					if [ "$pidOFAgentServer" = 0 ] 
					then		
						echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: DOWN" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
						echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: DOWN, Agent will be started!" | mail -s "$SMCFS_ENV_NAME - $AGENT_SERVER_NAME Status Check" $MORE_DETAILED_EMAIL
						nohup /opt/ssfs/runtime/bin/agentserver.sh $AGENT_SERVER_NAME | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/$AGENT_SERVER_NAME.%Y.%m.%d.log 86400 &
					else
						echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: MULTIPLE INSTANCES RUNNING" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
						echo "Date: $(date) :: $AGENT_SERVER_NAME Status Check :: MULTIPLE INSTANCES RUNNING, Agent Instances will be shutdown and 1 will be restarted!" | mail -s "$SMCFS_ENV_NAME - $AGENT_SERVER_NAME Status Check" $MORE_DETAILED_EMAIL
						nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name $AGENT_SERVER_NAME | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/$AGENT_SERVER_NAME.%Y.%m.%d.log 86400 &
					fi
				fi
			done < /opt/ssfs/runtime/$agthost.agent.txt;
		fi
		if [ -f "/opt/ssfs/runtime/$agthost.integration.txt" ] 
		then
			echo "Date: $(date) :: Reading $agthost.integration.txt" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
			while read INTEGRATION_SERVER_NAME || [[ -n "$INTEGRATION_SERVER_NAME" ]]
			do
				pidOFIntegrationServer="$(ps -ef | grep wasuser | grep $INTEGRATION_SERVER_NAME | grep java_wrapper.sh | wc -l)";
				if [ "$pidOFIntegrationServer" = 1 ] 
				then
					echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: OK" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
				else
					if [ "$pidOFIntegrationServer" = 0 ] 
					then		
						echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: DOWN" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
						echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: DOWN, Agent will be started!" | mail -s "$SMCFS_ENV_NAME - $INTEGRATION_SERVER_NAME Status Check" $MORE_DETAILED_EMAIL
						nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh $INTEGRATION_SERVER_NAME | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/$INTEGRATION_SERVER_NAME.%Y.%m.%d.log 86400 &
					else
						echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: MULTIPLE INSTANCES RUNNING" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;
						echo "Date: $(date) :: $INTEGRATION_SERVER_NAME Status Check :: MULTIPLE INSTANCES RUNNING, Agent Instances will be shutdown and 1 will be restarted!" | mail -s "$SMCFS_ENV_NAME - $INTEGRATION_SERVER_NAME Status Check" $MORE_DETAILED_EMAIL
						nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name $INTEGRATION_SERVER_NAME | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/$INTEGRATION_SERVER_NAME.%Y.%m.%d.log 86400 &
					fi
				fi					
			done < /opt/ssfs/runtime/$agthost.integration.txt;		
		fi
		
		######## Remove the Check/Restart Indicator once process is complete. 
		rm /opt/ssfs/runtime/TOGCustomAgentScripts/restartIndicator.txt;		
	fi
	
	######## Since no agent is down, check Health Monitor Agent
	if [ "$restartRequired" = 0 ] 
	then
		if [ "$agtServer" = "agtapp02" ]
		then
			######## Check Health Monitor
			/opt/ssfs/runtime/TOGCustomAgentScripts/restartHealthMonitorAgent.sh;
		fi
	fi
	
	echo "***************************************************************************************" >> /opt/ssfs/runtime/logs/Agent_Status_Check.log;		
fi