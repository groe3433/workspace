#!/bin/bash

. /home/wasuser/TOGScripts/SMCFSEmailDLs.sh

appServer="$(echo $HOSTNAME | cut -c11-18)";
apphost="$(echo $HOSTNAME | cut -c1-18)";

pidOFNodeAgent="$(ps -ef | grep wasuser | grep [n]odeagent | wc -l)";
pidOFDeploymentMgr="$(ps -ef | grep wasuser | grep [d]mgr | wc -l)";	
pidOFAppServer="$(ps -ef | grep wasuser | grep [s]erver1 | wc -l)";

if [ "$appServer" = "domapp01" ]
then
	if [ "$pidOFDeploymentMgr" = 1 ] 
	then
		echo "Date: $(date) :: $apphost Deployment Manager Status Check :: OK! " >> /opt/ssfs/runtime/logs/App_Status_Check.log;
		if [ -f "/home/wasuser/TOGScripts/DeploymentMgrDownIndicator.txt" ] 
		then
			echo "Date: $(date) :: $apphost Deployment Manager Status Check :: OK! " | mail -s "$SMCFS_ENV_NAME - $apphost Deployment Manager Status Check" $MORE_DETAILED_EMAIL
			rm /home/wasuser/TOGScripts/DeploymentMgrDownIndicator.txt;
		fi
	else		
		echo "Need to restart Deployment Mgr, leave this file alone!" > /home/wasuser/TOGScripts/DeploymentMgrDownIndicator.txt;
		chmod 775 /home/wasuser/TOGScripts/DeploymentMgrDownIndicator.txt;	
		if [ "$pidOFDeploymentMgr" = 0 ] 
		then		
			echo "Date: $(date) :: $apphost Deployment Manager Status Check :: DOWN!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
			echo "Date: $(date) :: $apphost Deployment Manager Status Check :: DOWN! Contact IBM CoC for a resolution!" | mail -s "$SMCFS_ENV_NAME - $apphost Deployment Manager Status Check" $MORE_DETAILED_EMAIL
		else
			echo "Date: $(date) :: $apphost Deployment Manager Status Check :: Multiple Instances Running!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
			echo "Date: $(date) :: $apphost Deployment Manager Status Check :: Multiple Instances Running! Contact IBM CoC for a resolution! " | mail -s "$SMCFS_ENV_NAME - $apphost Deployment Manager Status Check" $MORE_DETAILED_EMAIL
		fi		
	fi	
fi
if [ "$pidOFNodeAgent" = 1 ] 
then
	echo "Date: $(date) :: $apphost Node Agent Status Check :: OK! " >> /opt/ssfs/runtime/logs/App_Status_Check.log;
	if [ -f "/home/wasuser/TOGScripts/NodeAgentDownIndicator.txt" ] 
	then
		echo "Date: $(date) :: $apphost Node Agent Status Check :: OK! " | mail -s "$SMCFS_ENV_NAME - $apphost Node Agent Status Check" $MORE_DETAILED_EMAIL
		rm /home/wasuser/TOGScripts/NodeAgentDownIndicator.txt;
	fi	
else
	echo "Need to restart Node Agent, leave this file alone!" > /home/wasuser/TOGScripts/NodeAgentDownIndicator.txt;
	chmod 775 /home/wasuser/TOGScripts/NodeAgentDownIndicator.txt;		
	if [ "$pidOFNodeAgent" = 0 ] 
	then		
		echo "Date: $(date) :: $apphost Node Agent Status Check :: DOWN!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
		echo "Date: $(date) :: $apphost Node Agent Status Check :: DOWN! Contact IBM CoC for a resolution!" | mail -s "$SMCFS_ENV_NAME - $apphost Node Agent Status Check" $MORE_DETAILED_EMAIL
	else
		echo "Date: $(date) :: $apphost Node Agent Status Check :: Multiple Instances Running!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
		echo "Date: $(date) :: $apphost Node Agent Status Check :: Multiple Instances Running! Contact IBM CoC for a resolution! " | mail -s "$SMCFS_ENV_NAME - $apphost Node Agent Status Check" $MORE_DETAILED_EMAIL
	fi		
fi
if [ "$pidOFAppServer" = 1 ] 
then
	echo "Date: $(date) :: $apphost Application Server Status Check :: OK! " >> /opt/ssfs/runtime/logs/App_Status_Check.log;
	if [ -f "/home/wasuser/TOGScripts/AppServerDownIndicator.txt" ] 
	then
		echo "Date: $(date) :: $apphost Application Server Status Check :: OK! " | mail -s "$SMCFS_ENV_NAME - $apphost Application Server Status Check" $MORE_DETAILED_EMAIL
		rm /home/wasuser/TOGScripts/AppServerDownIndicator.txt;
	fi		
else		
	echo "Need to restart App Server, leave this file alone!" > /home/wasuser/TOGScripts/AppServerDownIndicator.txt;
	chmod 775 /home/wasuser/TOGScripts/AppServerDownIndicator.txt;
	if [ "$pidOFAppServer" = 0 ] 
	then		
		echo "Date: $(date) :: $apphost Application Server Status Check :: DOWN!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
		echo "Date: $(date) :: $apphost Application Server Status Check :: DOWN! Contact IBM CoC for a resolution!" | mail -s "$SMCFS_ENV_NAME - $apphost Application Server Status Check" $MORE_DETAILED_EMAIL
	else
		echo "Date: $(date) :: $apphost Application Server Status Check :: Multiple Instances Running!" >> /opt/ssfs/runtime/logs/App_Status_Check.log;
		echo "Date: $(date) :: $apphost Application Server Status Check :: Multiple Instances Running! Contact IBM CoC for a resolution! " | mail -s "$SMCFS_ENV_NAME - $apphost Application Server Status Check" $MORE_DETAILED_EMAIL
	fi		
fi