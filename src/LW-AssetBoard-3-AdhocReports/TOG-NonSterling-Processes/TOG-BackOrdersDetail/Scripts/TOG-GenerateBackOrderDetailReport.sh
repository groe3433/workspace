#!/bin/bash

BackOrderDetailHome=/opt/ssfs/runtime/TOGOMSExtracts/BackOrdersDetail;

tempFile="$BackOrderDetailHome/Back_Order_Detail_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered BackOrdersDetail Generation..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	echo "Report Date: $(date)" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
	echo "whoami: $(whoami)" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
	
	mv $BackOrderDetailHome/Back_Order_Detail_Temp.txt $BackOrderDetailHome/temp.txt;	
	echo "Back_Order_Detail_Temp.txt file renamed to temp.txt..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	
	ARCHIVE=$BackOrderDetailHome/Archive;
	LOGS=$BackOrderDetailHome/Logs;
	SCRIPTS=$BackOrderDetailHome/Scripts;
	TEMP=$BackOrderDetailHome/Temp;
	ToTOG=$BackOrderDetailHome/ToTOG;
	FromTOG=$BackOrderDetailHome/FromTOG;
	
	OutputFileName="BackOrdersDetail_`date +%Y%m%d%H%M%S`.csv";
	Script1="TOG-GenerateBackOrderDetailReport.sql";
	
	echo "Sourcing db2 profile..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
	. /home/wasuser/sqllib/db2profile;
	
	echo "db2 creating connection..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script1");
	echo "db2 sql executed..." >> $BackOrdersDetail/Logs/BackOrdersDetail.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/$OutputFileName;
	echo "db2 output encrypted line by line..." >> $BackOrdersDetail/Logs/BackOrdersDetail.log;
	
	cp $TEMP/$OutputFileName $ToTOG;
	chmod 775 $ToTOG/$OutputFileName;
	cp $TEMP/$OutputFileName $ARCHIVE;
	chmod 775 $ARCHIVE/$OutputFileName;
	rm $TEMP/$OutputFileName;
	
	mv $BackOrderDetailHome/temp.txt $BackOrderDetailHome/Back_Order_Detail_Emailer_Temp.txt;	
	echo "temp.txt file renamed to Back_Order_Detail_Emailer_Temp.txt..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;	
	
	echo "Exiting BackOrdersDetail Generation..." >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	echo "___________________________________________________________________" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
	echo "___________________________________________________________________" >> $BackOrderDetailHome/Logs/BackOrdersDetail.log;
fi