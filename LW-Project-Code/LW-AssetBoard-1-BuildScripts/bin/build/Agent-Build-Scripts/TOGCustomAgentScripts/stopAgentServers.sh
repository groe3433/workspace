#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

if [ "$agtServer" = "agtapp01" ] 
then
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name TOGRELEASEAGENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/TOGRELEASEAGENT.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name TOGSCHEDULEAGENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/TOGSCHEDULEAGENT.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name TOGSCHEDULEBACKORDERAGENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/TOGSCHEDULEBACKORDERAGENT.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_TRIGGER_AGENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_TRIGGER_AGENT.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_INVENTORY_SYNC | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_INVENTORY_SYNC.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################	
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name TOGRESETINFINV | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/TOGRESETINFINV.%Y.%m.%d.log 86400 &
	sleep 5s;	
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_ADJUST_INV | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_ADJUST_INV.%Y.%m.%d.log 86400 &
	sleep 5s;	
	#################################################################################################################################################
fi

if [ "$agtServer" = "agtapp02" ] 
then
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_CREATE_ORDER | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_CREATE_ORDER.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_CONFIRM_SHIPMENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_CONFIRM_SHIPMENT.%Y.%m.%d.log 86400 &
	sleep 5s;	
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_ORDER_STATUS_CHANGE | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_ORDER_STATUS_CHANGE.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_RECEIPT_CONF | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_RECEIPT_CONF.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
	nohup /opt/ssfs/runtime/bin/stopIntegrationServer.sh -name OMS_PRODUCT_CATALOG | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/int_logs/OMS_PRODUCT_CATALOG.%Y.%m.%d.log 86400 &
	sleep 5s;	
	#################################################################################################################################################	
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name OMS_CLOSE_ORDER | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/OMS_CLOSE_ORDER.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################	
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name OMS_CLOSE_SHIPMENT | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/OMS_CLOSE_SHIPMENT.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################	
	nohup /opt/ssfs/runtime/bin/agentserverstop.sh -name OMS_INV_PURGE | /opt/ssfs/runtime/bin/rotatelogs /opt/ssfs/runtime/logs/agent_int_logs/OMS_INV_PURGE.%Y.%m.%d.log 86400 &
	sleep 5s;
	#################################################################################################################################################
fi
exit