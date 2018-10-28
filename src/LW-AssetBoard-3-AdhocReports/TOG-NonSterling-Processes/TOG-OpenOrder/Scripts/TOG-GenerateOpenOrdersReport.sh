#!/bin/bash

OpenOrderHome=/opt/ssfs/runtime/TOGOMSExtracts/OpenOrders;

tempFile="$OpenOrderHome/Open_Orders_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered OpenOrders Generation..." >> $OpenOrderHome/Logs/OpenOrders.log;
	echo "Report Date: $(date)" >> $OpenOrderHome/Logs/OpenOrders.log;
	echo "whoami: $(whoami)" >> $OpenOrderHome/Logs/OpenOrders.log;
	
	mv $OpenOrderHome/Open_Orders_Temp.txt $OpenOrderHome/temp.txt;	
	echo "Open_Orders_Temp.txt file renamed to temp.txt..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	
	ARCHIVE=$OpenOrderHome/Archive;
	LOGS=$OpenOrderHome/Logs;
	SCRIPTS=$OpenOrderHome/Scripts;
	TEMP=$OpenOrderHome/Temp;
	ToTOG=$OpenOrderHome/ToTOG;
	FromTOG=$OpenOrderHome/FromTOG;
	
	OutputFileName="OpenOrders_`date +%Y%m%d%H%M%S`.csv";
	Script1="TOG-CallOpenOrderProcedure.sql";
	Script2="TOG-GenerateOpenOrdersReport.sql";
	
	echo "Sourcing db2 profile..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	. /home/wasuser/sqllib/db2profile;
	
	echo "db2 creating connection..." >> $OpenOrderHome/Logs/OpenOrders.log;		
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	db2 -tvf $SCRIPTS/$Script1;
	echo "db2 PLSQL executed..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script2");
	echo "db2 sql executed..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/$OutputFileName;
	echo "db2 output encrypted line by line..." >> $OpenOrderHome/Logs/OpenOrders.log;
	
	cp $TEMP/$OutputFileName $ToTOG;
	chmod 775 $ToTOG/$OutputFileName;
	cp $ToTOG/$OutputFileName $ARCHIVE;
	chmod 775 $ARCHIVE/$OutputFileName;
	rm $TEMP/$OutputFileName;

	mv $OpenOrderHome/temp.txt $OpenOrderHome/Open_Orders_Emailer_Temp.txt;	
	echo "Open_Orders_Emailer_Temp.txt file renamed to temp.txt..." >> $OpenOrderHome/Logs/OpenOrders.log;	

	echo "Exiting OpenOrders Generation..." >> $OpenOrderHome/Logs/OpenOrders.log;	
	echo "___________________________________________________________________" >> $OpenOrderHome/Logs/OpenOrders.log;
	echo "___________________________________________________________________" >> $OpenOrderHome/Logs/OpenOrders.log;
fi