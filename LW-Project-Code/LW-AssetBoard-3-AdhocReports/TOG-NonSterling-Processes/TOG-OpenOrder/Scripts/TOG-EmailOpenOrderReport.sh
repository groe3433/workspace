#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles

OpenOrderHome=/opt/ssfs/runtime/TOGOMSExtracts/OpenOrders;

if [ -f "$OpenOrderHome/Open_Orders_Emailer_Temp.txt" ] 
then	
	rm $OpenOrderHome/Open_Orders_Emailer_Temp.txt;

	ARCHIVE=$OpenOrderHome/Archive
	LOGS=$OpenOrderHome/Logs
	SCRIPTS=$OpenOrderHome/Scripts
	TEMP=$OpenOrderHome/Temp
	ToTOG=$OpenOrderHome/ToTOG
	FromTOG=$OpenOrderHome/FromTOG
	
	EmailReportBody="TOG-Email-Greeting.txt"
	EmailRecipients="TOG-Email-Recipients.txt"
	
	echo "Entering OpenOrders Emailer..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	
	cd $ToTOG;
	chmod 775 *.csv;
	for file in *.csv; do
		echo "File to send: $file..." >> $OpenOrderHome/Logs/OpenOrders.log;	
		
		#RECIPIENTS=$(cat $EmailReportHome/$EmailRecipients);
		#cat $EmailReportHome/$EmailReportBody | mailx -s "Open Order Report" -a $file $REPORT_EMAIL;
		cat $EmailReportHome/$EmailReportBody | mailx -s "Open Order Report" $REPORT_EMAIL;
		
		sleep 10s;
		
		##### Approach for applying username & password on the command line with curl. 	
		#curl -u OMSPush:<OMSPush_Password_Here> -T $file sftp://tog.b2b.lightwellinc.com/OMSExtracts/upload/$file;	
		
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		
		sleep 20s;
		rm $file;
	done;
	echo "Exiting OpenOrders Emailer..." >> $OpenOrderHome/Logs/OpenOrders.log;
	echo "___________________________________________________________________" >> $OpenOrderHome/Logs/OpenOrders.log;
	echo "___________________________________________________________________" >> $OpenOrderHome/Logs/OpenOrders.log;
fi