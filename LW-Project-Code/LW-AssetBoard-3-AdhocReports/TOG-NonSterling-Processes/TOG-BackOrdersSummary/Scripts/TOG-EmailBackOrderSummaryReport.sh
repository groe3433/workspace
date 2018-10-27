#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles

BackOrderSummaryHome=/opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary;

if [ -f "$BackOrderSummaryHome/Back_Order_Summary_Emailer_Temp.txt" ] 
then	
	rm $BackOrderSummaryHome/Back_Order_Summary_Emailer_Temp.txt;

	echo "Entered BackOrdersSummary Emailer..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;

	ARCHIVE=$BackOrderSummaryHome/Archive
	LOGS=$BackOrderSummaryHome/Logs
	SCRIPTS=$BackOrderSummaryHome/Scripts
	TEMP=$BackOrderSummaryHome/Temp
	ToTOG=$BackOrderSummaryHome/ToTOG
	FromTOG=$BackOrderSummaryHome/FromTOG
	
	EmailReportBody="TOG-Email-Greeting.txt"
	EmailRecipients="TOG-Email-Recipients.txt"
	
	cd $ToTOG;
	chmod 775 *.csv;
	for file in *.csv; do
		echo "File to send: $file..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
		
		#RECIPIENTS=$(cat $EmailReportHome/$EmailRecipients);
		#cat $EmailReportHome/$EmailReportBody | mailx -s "Back Order Summary Report" -a $file $REPORT_EMAIL;
		cat $EmailReportHome/$EmailReportBody | mailx -s "Back Order Summary Report" $REPORT_EMAIL;
		
		sleep 10s;		
		
		##### Approach for applying username & password on the command line with curl. 
		#curl -u OMSPush:<OMSPush_Password_Here> -T $file sftp://tog.b2b.lightwellinc.com/OMSExtracts/upload/$file;
		
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		
		sleep 20s;
		rm $file;
	done;
	echo "Exiting BackOrdersSummary Emailer..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "___________________________________________________________________" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "___________________________________________________________________" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
fi