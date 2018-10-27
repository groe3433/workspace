#!/bin/bash

OpenOrderDetailHome=/opt/ssfs/runtime/TOGOMSExtracts/OpenOrderDetail;
ARCHIVE=$OpenOrderDetailHome/Archive;
LOGS=$OpenOrderDetailHome/Logs;
SCRIPTS=$OpenOrderDetailHome/Scripts;
TEMP=$OpenOrderDetailHome/Temp;
ToTOG=$OpenOrderDetailHome/ToTOG;
FromTOG=$OpenOrderDetailHome/FromTOG;
Script1="TOG-CallOpenOrderDetailProcedure.sql";
Script2="TOG-GenerateOpenOrderDetailReport.sql";

tempFile="$OpenOrderDetailHome/Open_Order_Detail_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered OpenOrderDetail Generation..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "Report Date: $(date)" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	echo "whoami: $(whoami)" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	mv $OpenOrderDetailHome/Open_Order_Detail_Temp.txt $OpenOrderDetailHome/temp.txt;	
	echo "Open_Order_Detail_Temp.txt file renamed to temp.txt..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	OutputFileNameD="OMS_OpenOrd_download_`date +%Y%m%d%H%M`.csv";
	OutputZipFileNameD="OMS_OpenOrd_download_`date +%Y%m%d%H%M%S`.zip";
	
	echo "Sourcing db2 profile..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	. /home/wasuser/sqllib/db2profile;	
	
	echo "db2 creating connection..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	db2 -tvf $SCRIPTS/$Script1;
	echo "db2 PLSQL executed..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script2");
	echo "db2 sql executed..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/temp.csv;
	echo "db2 output encrypted line by line..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	cp -p $TEMP/temp.csv $TEMP/$OutputFileNameD;
	chmod 775 $TEMP/$OutputFileNameD;
	
	rm $TEMP/temp.csv;
	
	cp -p $TEMP/$OutputFileNameD $ARCHIVE/$OutputFileNameD;
	
	zip -j $TEMP/$OutputZipFileNameD $TEMP/$OutputFileNameD;
	chmod 775 $TEMP/$OutputZipFileNameD;
	
	cp -p $TEMP/$OutputZipFileNameD $ARCHIVE/$OutputZipFileNameD;
	
	cp -p $TEMP/$OutputZipFileNameD $ToTOG/$OutputZipFileNameD;
	
	rm $TEMP/*.zip;
	rm $TEMP/*.csv;

	mv $OpenOrderDetailHome/temp.txt $OpenOrderDetailHome/Open_Order_Detail_Emailer_Temp.txt;	
	echo "temp.txt file renamed to Open_Order_Detail_Emailer_Temp.txt..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	

	echo "Exiting OpenOrderDetail Generation..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
fi

tempFile="$OpenOrderDetailHome/Open_Order_Detail_ATP_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered OpenOrderDetailATP Generation..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "Report Date: $(date)" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	echo "whoami: $(whoami)" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	mv $OpenOrderDetailHome/Open_Order_Detail_ATP_Temp.txt $OpenOrderDetailHome/temp.txt;	
	echo "Open_Order_Detail_ATP_Temp.txt file renamed to temp.txt..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	OutputFileNameA="OMS_OpenOrd_download_ATP_`date +%Y%m%d%H%M`.csv";
	OutputZipFileNameA="ATP_OMS_OpenOrd_download_`date +%Y%m%d%H%M%S`.zip";
	
	echo "Sourcing db2 profile..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	. /home/wasuser/sqllib/db2profile;	
	
	echo "db2 creating connection..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	db2 -tvf $SCRIPTS/$Script1;
	echo "db2 PLSQL executed..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;		
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script2");
	echo "db2 sql executed..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/temp.csv;
	echo "db2 output encrypted line by line..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
	
	cp -p $TEMP/temp.csv $TEMP/$OutputFileNameA;
	chmod 775 $TEMP/$OutputFileNameA;
	
	rm $TEMP/temp.csv;
	
	cp -p $TEMP/$OutputFileNameA $ARCHIVE/$OutputFileNameA;
	
	zip -j $TEMP/$OutputZipFileNameA $TEMP/$OutputFileNameA;
	chmod 775 $TEMP/$OutputZipFileNameA;
	
	cp -p $TEMP/$OutputZipFileNameA $ARCHIVE/$OutputZipFileNameA;
	
	cp -p $TEMP/$OutputZipFileNameA $ToTOG/$OutputZipFileNameA;
	
	rm $TEMP/*.zip;
	rm $TEMP/*.csv;

	mv $OpenOrderDetailHome/temp.txt $OpenOrderDetailHome/Open_Order_Detail_ATP_Emailer_Temp.txt;	
	echo "temp.txt file renamed to Open_Order_Detail_ATP_Emailer_Temp.txt..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	

	echo "Exiting OpenOrderDetailATP Generation..." >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;
	echo "___________________________________________________________________" >> $OpenOrderDetailHome/Logs/OpenOrderDetail.log;	
fi