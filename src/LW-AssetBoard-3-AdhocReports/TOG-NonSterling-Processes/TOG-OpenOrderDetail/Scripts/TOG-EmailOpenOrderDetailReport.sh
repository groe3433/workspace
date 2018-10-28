#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles
OpenOrderDetailHome=/opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail;
ARCHIVE=$OpenOrderDetailHome/Archive;
LOGS=$OpenOrderDetailHome/Logs;
SCRIPTS=$OpenOrderDetailHome/Scripts;
TEMP=$OpenOrderDetailHome/Temp;
ToTOG=$OpenOrderDetailHome/ToTOG;
FromTOG=$OpenOrderDetailHome/FromTOG;	
EmailReportBody="TOG-Email-Greeting.txt";
EmailRecipients="TOG-Email-Recipients.txt";

if [ -f "$OpenOrderDetailHome/Open_Order_Detail_Emailer_Temp.txt" ] 
then	
	rm $OpenOrderDetailHome/Open_Order_Detail_Emailer_Temp.txt;
	
	echo "Entering OpenOrderDetail Emailer..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	
	cd $ToTOG;
	chmod 775 *.zip;
	for file in *.zip; do
		cat $EmailReportHome/$EmailReportBody | mailx -s "Open Order Detail Report :: $file" $REPORT_EMAIL;
		
		sleep 10s;
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		
		sleep 20s;
		rm $file;
	done;
	echo "Exiting OpenOrderDetail Emailer..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
fi

if [ -f "$OpenOrderDetailHome/Open_Order_Detail_ATP_Emailer_Temp.txt" ] 
then
	rm $OpenOrderDetailHome/Open_Order_Detail_ATP_Emailer_Temp.txt;
	
	echo "Entering OpenOrderDetailATP Emailer..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	
	cd $ToTOG;
	chmod 775 *.zip;
	for file in *.zip; do
		cat $EmailReportHome/$EmailReportBody | mailx -s "ATP Open Order Detail Report :: $file" $REPORT_EMAIL;
		
		sleep 10s;
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		
		sleep 20s;
		rm $file;
	done;
	echo "Exiting OpenOrderDetailATP Emailer..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
fi