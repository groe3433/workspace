#!/bin/bash

if [ -f /opt/ssfs/runtime/TOGCommonScripts/udcDeploymentIndicator.txt ] 
then
	chmod 775 -R /opt/ssfs/runtime/TOGCommonScripts;
	chmod 775 -R /opt/ssfs/runtime/TOGOMSExtracts;
	chmod 775 -R /opt/ssfs/runtime/TOGCustomAgentScripts;
	chmod 775 -R /opt/ssfs/runtime/TOGSystemStats;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCommonScripts;		
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersDetail/Scripts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary/Scripts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Scripts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/ItemSubstitution/Scripts;		
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OrderReconciliation/Scripts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail/Scripts;	
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts/TOGTriggerAgent;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges;
	/home/wasuser/TOGScripts/dos2unix4scripts.sh /opt/ssfs/runtime/TOGSystemStats;
	/home/wasuser/TOGScripts/dos2unix4lists.sh;
	rm /opt/ssfs/runtime/TOGCommonScripts/udcDeploymentIndicator.txt;		
fi