#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

##### Make sure you set your environment variables!

if [ -f /home/wasuser/TOGScripts/TOGZipContents.zip ] 
then
	if [ "$1" = "all-unpack" ]
	then
		echo "Starting Agent Unpacking/Restart Script on $SMCFS_ENV_NAME." | mail -s "$SMCFS_ENV_NAME - Agent Unpack Script Triggered..." $MORE_DETAILED_EMAIL
		/home/wasuser/TOGScripts/apache-ant-1.8.1/bin/ant -f /home/wasuser/TOGScripts/unpackTOG.xml $1 >> /home/wasuser/TOGScripts/unpack.log
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCommonScripts;		
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersDetail/Scripts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary/Scripts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Scripts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/ItemSubstitution/Scripts;		
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OrderReconciliation/Scripts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail/Scripts;		
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges;
		/opt/ssfs/runtime/TOGCommonScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGSystemStats;		
		sleep 10m
		nohup /opt/ssfs/runtime/TOGCustomAgentScripts/restartTOGAgents.sh &
		sleep 5m
		echo "Finished Agent Unpacking/Restart Script on $SMCFS_ENV_NAME." | mail -s "$SMCFS_ENV_NAME - Agent Unpack Script Finished." $MORE_DETAILED_EMAIL
	fi
fi