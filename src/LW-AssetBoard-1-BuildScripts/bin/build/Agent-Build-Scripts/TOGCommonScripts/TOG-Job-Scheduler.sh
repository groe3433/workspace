#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

export TZ=":America/New_York";
HOUR=`date +%H`;
MINUTE=`date +%M`;
DAY=`date +%d`;
MONTH=`date +%m`;
DAYOFWEEK=`date +%u`;

if [[ "$agtServer" == "agtapp02" ]] 
then
	######## Close Shipment - Weekly Maintenance job for shipments to be eligible for purge - 21:01 EST ########
	if [[ "$MINUTE" == "01" && "$HOUR" == "21" && "$DAYOFWEEK" == "6" ]] 
	then
		echo "Date: $(date) :: Close Shipment - Weekly Maintenance job for shipments to be eligible for purge - 21:01 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/closeShipment.sh;
	fi
	
	######## Close Order - Weekly Maintenance job for orders to be eligible for purge - 19:01 EST ########
	if [[ "$MINUTE" == "01" && "$HOUR" == "19" && "$DAYOFWEEK" == "6" ]] 
	then
		echo "Date: $(date) :: Close Order - Weekly Maintenance job for orders to be eligible for purge - 19:01 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/closeOrder.sh;
	fi
	
	######## Purge Order - Weekly Maintenance job for orders Archive - 17:01 EST ########
	if [[ "$MINUTE" == "01" && "$HOUR" == "17" && "$DAYOFWEEK" == "0" ]] 
	then
		echo "Date: $(date) :: Purge Order - Weekly Maintenance job for orders Archive - 17:01 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/orderPurge.sh;
	fi
	
	######## Purge Shipment - Weekly Maintenance job for Shipment Archive - 21:01 EST ########
	if [[ "$MINUTE" == "01" && "$HOUR" == "21" && "$DAYOFWEEK" == "0" ]] 
	then
		echo "Date: $(date) :: Purge Shipment - Weekly Maintenance job for Shipment Archive - 21:01 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/shipmentPurge.sh;
	fi
	
	######## Purge YFS_INVENTORY_SUPPLY_TEMP - Daily Maintenance job for Inv Sync to be eligible for purge - 19:45 EST ########
	if [[ "$MINUTE" == "45" && "$HOUR" == "19" ]] 
	then
		echo "Date: $(date) :: Purge YFS_INVENTORY_SUPPLY_TEMP - Daily Maintenance job for Inv Sync to be eligible for purge - 19:45 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/supplyItemPurge.sh;
	fi
	
	######## Purge YFS_INVENTORY_DEMAND/SUPPLY - Weekly Maintenance job for Inv Demand/Suppy purge - 22:01 EST ########
	if [[ "$MINUTE" == "01" && "$HOUR" == "22" && "$DAYOFWEEK" == "0" ]] 
	then
		echo "Date: $(date) :: Purge YFS_INVENTORY_DEMAND/SUPPLY - Weekly Maintenance job for Inv Demand/Suppy purge - 22:01 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/inventoryPurge.sh;
	fi
	
	######## Purge YFS_INVENTORY_AUDIT - Daily Maintenance job for Inv Audit purge - 19:31 EST ########
	if [[ "$MINUTE" == "31" && "$HOUR" == "19" ]] 
	then
		echo "Date: $(date) :: Purge YFS_INVENTORY_AUDIT - Daily Maintenance job for Inv Audit purge - 19:31 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGCustomAgentScripts/TOGPurges/inventoryAuditPurge.sh;
	fi

	######## Agent Box to place trigger file automatically at set time for Open Order Report - 07:00, 19:15, 14:40, and 17:55 ########
	if [[ "$MINUTE" == "00" && "$HOUR" == "07" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Report - 07:00, 19:15, 14:40, and 17:55" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Open_Orders_Temp.txt; 
	fi
	if [[ "$MINUTE" == "15" && "$HOUR" == "19" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Report - 07:00, 19:15, 14:40, and 17:55" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Open_Orders_Temp.txt;
	fi
	if [[ "$MINUTE" == "40" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Report - 07:00, 19:15, 14:40, and 17:55" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Open_Orders_Temp.txt;
	fi
	if [[ "$MINUTE" == "55" && "$HOUR" == "17" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Report - 07:00, 19:15, 14:40, and 17:55" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrders/Open_Orders_Temp.txt;
	fi
	
	######## Agent Box to place trigger file automatically at set time for Open Order Detail Report - 08:10, and 04:00 ########
	if [[ "$MINUTE" == "10" && "$HOUR" == "08" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Detail Report - 08:10, and 04:00" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail/Open_Order_Detail_Temp.txt; 
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "04" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Open Order Detail Report - 08:10, and 04:00" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail/Open_Order_Detail_ATP_Temp.txt;
	fi
	
	######## Agent Box to place trigger file automatically at set time for Back Order Summary Report - 07:15, and 14:00 ######## 
	if [[ "$MINUTE" == "15" && "$HOUR" == "07" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Back Order Summary Report - 07:15, and 14:00" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary/Back_Order_Summary_Temp.txt; 
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Back Order Summary Report - 07:15, and 14:00" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary/Back_Order_Summary_Temp.txt;
	fi
	
	######## Agent Box to place trigger file automatically at set time for Order Reconciliation Report - 10:15 ########
	if [[ "$MINUTE" == "15" && "$HOUR" == "10" ]] 
	then
		echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Order Reconciliation Report - 10:15" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/OrderReconciliation/Order_Reconciliation_Temp.txt; 
	fi
	
	######## Agent Box to place trigger file automatically at set time for Back Order Detail - (Every 15 minutes) ######## 
	#if [[ "$MINUTE" == "00" || "$MINUTE" == "15" || "$MINUTE" == "30" || "$MINUTE" == "45" ]] 
	#then
	#	echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Back Order Detail - (Every 15 minutes)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
	#	cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/BackOrdersDetail/Back_Order_Detail_Temp.txt; 
	#fi
	
	######## Agent Box to place trigger file automatically at set time for Item Substitution Report - (Every 15 minutes) ########
	#if [[ "$MINUTE" == "00" || "$MINUTE" == "15" || "$MINUTE" == "30" || "$MINUTE" == "45" ]] 
	#then
	#	echo "Date: $(date) :: Agent Box to place trigger file automatically at set time for Item Substitution Report - (Every 15 minutes)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
	#	cp -p /opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles/temp.txt /opt/ssfs/runtime/TOGOMSExtracts/ItemSubstitution/Item_Substitution_Temp.txt;
	#fi
	
	######## System Stats - Error List - 07:00 EST, 11:00 EST, and 15:00 EST ########
	if [[ "$MINUTE" == "00" && "$HOUR" == "15" ]] 
	then
		echo "Date: $(date) :: System Stats - Error List - 07:00 EST, 11:00 EST, and 15:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-ErrorList.sh
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "11" ]] 
	then
		echo "Date: $(date) :: System Stats - Error List - 07:00 EST, 11:00 EST, and 15:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-ErrorList.sh
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "07" ]] 
	then
		echo "Date: $(date) :: System Stats - Error List - 07:00 EST, 11:00 EST, and 15:00 EST" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-ErrorList.sh
	fi
	
	######## System Stats - Order Line Stats - 00:30 EST (1st day of month) ########
	if [[ "$MINUTE" == "30" && "$HOUR" == "00" && "$DAY" == "01" ]] 
	then
		echo "Date: $(date) :: System Stats - Order Line Stats - 00:30 EST (1st day of month)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderLineStats.sh
	fi
	
	######## System Stats - SQL Monitor - (Every 15 minutes) ########
	if [[ "$MINUTE" == "00" || "$MINUTE" == "15" || "$MINUTE" == "30" || "$MINUTE" == "45" ]] 
	then
		echo "Date: $(date) :: System Stats - SQL Monitor - (Every 15 minutes)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-SQLMonitor.sh
	fi
	
	######## Count Order to be run 30 min after Schedule (9:10 EST, 14:40 EST, 00:35 EST) ########
	if [[ "$MINUTE" == "10" && "$HOUR" == "09" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule (9:10 EST, 14:40 EST, 00:35 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "40" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule (9:10 EST, 14:40 EST, 00:35 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "35" && "$HOUR" == "00" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule (9:10 EST, 14:40 EST, 00:35 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	
	######## Count Order to be run 30 min after Schedule Backorder (09:00 EST, 14:30 EST, 23:00 EST) ########
	if [[ "$MINUTE" == "00" && "$HOUR" == "09" ]]
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule Backorder (09:00 EST, 14:30 EST, 23:00 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "30" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule Backorder (09:00 EST, 14:30 EST, 23:00 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "00" && "$HOUR" == "23" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Schedule Backorder (09:00 EST, 14:30 EST, 23:00 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	
	######## Count Order to be run 30 min after Release (00:45 EST, 09:20 EST, 14:50 EST) ########
	if [[ "$MINUTE" == "45" && "$HOUR" == "00" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Release (00:45 EST, 09:20 EST, 14:50 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "20" && "$HOUR" == "09" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Release (00:45 EST, 09:20 EST, 14:50 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
	if [[ "$MINUTE" == "50" && "$HOUR" == "14" ]] 
	then
		echo "Date: $(date) :: Count Order to be run 30 min after Release (00:45 EST, 09:20 EST, 14:50 EST)" >> /opt/ssfs/runtime/logs/TOG_JOB_SCHEDULER.log;
		/opt/ssfs/runtime/TOGSystemStats/TOG-OrderCount.sh;
	fi
fi

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