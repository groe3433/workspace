#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles
	
BackOrderDetailHome=/opt/ssfs/runtime/TOGOMSExtracts/BackOrdersDetail;

if [ -f "$BackOrderDetailHome/Back_Order_Detail_Emailer_Temp.txt" ] 
then	
	rm $BackOrderDetailHome/Back_Order_Detail_Emailer_Temp.txt;

	ARCHIVE=$BackOrderDetailHome/Archive
	LOGS=$BackOrderDetailHome/Logs
	SCRIPTS=$BackOrderDetailHome/Scripts
	TEMP=$BackOrderDetailHome/Temp
	ToTOG=$BackOrderDetailHome/ToTOG
	FromTOG=$BackOrderDetailHome/FromTOG
	
	EmailReportBody="TOG-Email-Greeting.txt"
	EmailRecipients="TOG-Email-Recipients.txt"
	
	echo "Entered BackOrdersDetail Emailer..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	
	cd $ToTOG;
	chmod 775 *.csv;
	for file in *.csv; do
		echo "File to send: $file..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
		
		#RECIPIENTS=$(cat $EmailReportHome/$EmailRecipients);
		#cat $EmailReportHome/$EmailReportBody | mailx -s "Back Order Detail Report" -a $file $REPORT_EMAIL;
		cat $EmailReportHome/$EmailReportBody | mailx -s "Back Order Detail Report" $REPORT_EMAIL;
		
		sleep 10s;
		
		##### Approach for applying username & password on the command line with curl. 
		#curl -u OMSPush:<OMSPush_Password_Here> -T $file sftp://tog.b2b.lightwellinc.com/OMSExtracts/upload/$file;
		
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		
		sleep 20s;
		rm $file;
	done;
	echo "Exiting BackOrdersDetail Emailer..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	echo "___________________________________________________________________" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	echo "___________________________________________________________________" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
fi