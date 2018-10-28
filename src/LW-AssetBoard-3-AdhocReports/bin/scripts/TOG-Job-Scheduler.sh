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
fi