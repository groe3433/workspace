#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

if [ "$agtServer" = "agtapp01" ] 
then

fi

if [ "$agtServer" = "agtapp02" ] 
then
	#################################################################################
	######## will import orders in TOG.MIGRATION.QUEUE, uncomment if desired ########
	#################################################################################		
	#nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh TOGMIGRATIONAGENT >> /opt/ssfs/runtime/logs/agent_int_logs/TOGMIGRATIONAGENT.log 2>&1 &
	#sleep 5s;
	#################################################################################################################################################
		
	#######################################################################################################################################################################
	######## will get orders to be imported to another system and put them into TOG.COL.OMS.Q2Q.OMS.HOLD/TOG.COL.OMS.Q2Q.OMS.BOUISEND queues, uncomment if desired ########
	#######################################################################################################################################################################	
	#nohup /opt/ssfs/runtime/bin/agentserver.sh OMS_MIGRATION_AGENT >> /opt/ssfs/runtime/logs/agent_int_logs/OMS_MIGRATION_AGENT.log 2>&1 &
	#sleep 5s;	
	###########################################################################################################################################################################
	######## TEST AGENT/SERVICE will email the configured user an XML to be imported into another system from TOG.COL.OMS.Q2Q.OMS.BOUISEND queue, uncomment if desired ########
	###########################################################################################################################################################################
	#nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh TOGEXPORTMIGRATIONORDER >> /opt/ssfs/runtime/logs/agent_int_logs/TOGEXPORTMIGRATIONORDER.log 2>&1 &
	#sleep 5s;
	#################################################################################################################################################
					
	#################################################### RETIRED ####################################################################################
	#nohup /opt/ssfs/runtime/bin/agentserver.sh TOGAGENTSERVER >> /opt/ssfs/runtime/logs/agent_int_logs/TOGAGENTSERVER.log 2>&1 &
	#sleep 5s;
	#################################################### RETIRED ####################################################################################
	##### Called from: ExelThirtyOneStampBackOrderFlag::pushLTOOSOrders -> ScheduleLTOOSOrder
	#nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_SCHEDULE_LTOOS_ORDER >> /opt/ssfs/runtime/logs/agent_int_logs/OMS_SCHEDULE_LTOOS_ORDER.log 2>&1 &
	#sleep 5s;
	#################################################### RETIRED ####################################################################################
	##### Called from: ExelThirtyOneMigrationAgent::executeJob -> PostMsgToMigrationQueue -> OMS_MIGRATION_ORDER/ExelMigrationAsyService
	#nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_MIGRATION_ORDER >> /opt/ssfs/runtime/logs/agent_int_logs/OMS_MIGRATION_ORDER.log 2>&1 &
	#sleep 5s;
	#################################################### RETIRED ####################################################################################
	##### OMS_INV_MONITOR Agent calls INVENTORY_MONITOR
	#nohup /opt/ssfs/runtime/bin/agentserver.sh OMS_INV_MONITOR >> /opt/ssfs/runtime/logs/agent_int_logs/OMS_INV_MONITOR.log 2>&1 &
	#sleep 5s;
	#################################################### RETIRED ####################################################################################
	##### CONS_ADD_INV Agent calls CONSOLIDATE_ADDNL_INV
	#nohup /opt/ssfs/runtime/bin/agentserver.sh CONS_ADD_INV >> /opt/ssfs/runtime/logs/agent_int_logs/CONS_ADD_INV.log 2>&1 &
	#sleep 5s;	
	#################################################################################################################################################
fi
exit