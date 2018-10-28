#!/bin/bash

BackOrderSummaryHome=/opt/ssfs/runtime/TOGOMSExtracts/BackOrdersSummary;

tempFile="$BackOrderSummaryHome/Back_Order_Summary_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered BackOrdersSummary Generation..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "Report Date: $(date)" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "whoami: $(whoami)" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	
	mv $BackOrderSummaryHome/Back_Order_Summary_Temp.txt $BackOrderSummaryHome/temp.txt;	
	echo "Back_Order_Summary_Temp.txt file renamed to temp.txt..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	
	ARCHIVE=$BackOrderSummaryHome/Archive;
	LOGS=$BackOrderSummaryHome/Logs;
	SCRIPTS=$BackOrderSummaryHome/Scripts;
	TEMP=$BackOrderSummaryHome/Temp;
	ToTOG=$BackOrderSummaryHome/ToTOG;
	FromTOG=$BackOrderSummaryHome/FromTOG;
	
	OutputFileName="BackOrdersSummary_`date +%Y%m%d%H%M%S`.csv";
	Script1="TOG-GenerateBackOrderSummaryReport.sql";
	
	echo "Sourcing db2 profile..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	. /home/wasuser/sqllib/db2profile;
	
	echo "db2 creating connection..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script1");
	echo "db2 sql executed..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/$OutputFileName;
	echo "db2 output encrypted line by line..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;	
	
	cp $TEMP/$OutputFileName $ToTOG;
	chmod 775 $ToTOG/$OutputFileName;
	cp $TEMP/$OutputFileName $ARCHIVE;
	chmod 775 $ARCHIVE/$OutputFileName;
	rm $TEMP/$OutputFileName;

	mv $BackOrderSummaryHome/temp.txt $BackOrderSummaryHome/Back_Order_Summary_Emailer_Temp.txt;	
	echo "temp.txt file renamed to Back_Order_Summary_Emailer_Temp.txt..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;

	echo "Exiting BackOrdersSummary Generation..." >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "___________________________________________________________________" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
	echo "___________________________________________________________________" >> $BackOrderSummaryHome/Logs/BackOrdersSummary.log;
fi