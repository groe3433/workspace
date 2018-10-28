#!/bin/bash

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles;
	
OrderReconciliationHome=/opt/ssfs/runtime/TOGOMSExtracts/OrderReconciliation;

tempFile="$OrderReconciliationHome/Order_Reconciliation_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered OrderReconciliation Generation..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	echo "Report Date: $(date)" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;	
	echo "whoami: $(whoami)" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;	
	
	mv $OrderReconciliationHome/Order_Reconciliation_Temp.txt $OrderReconciliationHome/temp.txt;	
	echo "Order_Reconciliation_Temp.txt file renamed to temp.txt..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;	
	
	ARCHIVE=$OrderReconciliationHome/Archive;
	LOGS=$OrderReconciliationHome/Logs;
	SCRIPTS=$OrderReconciliationHome/Scripts;
	TEMP=$OrderReconciliationHome/Temp;
	ToTOG=$OrderReconciliationHome/ToTOG;
	FromTOG=$OrderReconciliationHome/FromTOG;
	
	EXCEPTIONSFILE="Order_recon_exceptions_`date +%Y%m%d%H%M%S`.txt";
	EXCEPTIONSFILETEXT="Order_recon_exceptions_text_`date +%Y%m%d%H%M%S`.txt";	
	PROCESSEDFILE="Order_Recon_Processed_`date +%Y%m%d%H%M%S`.txt";
	Script1="TOG-OrderReconciliation.sql";
	
	cd $FromTOG;
	
	##### Approach for applying username & password on the command line with curl. 	
	#curl -u OMSPush:<OMSPush_Password_Here> -T $file sftp://tog.b2b.lightwellinc.com/OMSExtracts/download/ORDRECON_TOG.txt;	
	
	/opt/ssfs/runtime/TOGOMSExtracts/downloadFileFromB2B.sh;
	
	chmod 775 *.txt;
	
	cd $FromTOG;
	
	############ TOG and B2B now providing slightly different filename   ############
	############ In old EXEL world they were looking for "ORDRECON*.txt*" ############
	CNT=`find . -name 'Order_Recon_*.txt*' | wc -l | cut -d " " -f 1`;
	#################################################################################
	
	case $CNT in
		0 )
			echo "`date` - No input file received from Thirty-One Gifts" >> $LOGS/OrderReconciliation.log;
			echo "No input file received from Thirty-One Gifts." > $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
			echo "TOG Order Audit: File not Received From Thirty-One Gifts - Action Required" > $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;
			mv $OrderReconciliationHome/temp.txt $OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt;	
			echo "temp.txt file renamed to Order_Reconciliation_Emailer_Temp.txt..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
			echo "Exiting OrderReconciliation Generation..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
			echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
			echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
			exit;
			;;
		1 )
			############ TOG and B2B now providing slightly different filename   ############
			############ In old EXEL world they were looking for "ORDRECON*.txt" ############
			INFILE=`find . -name 'Order_Recon_*.txt'`;
			#################################################################################
			
			cp $INFILE $TEMP/infile.txt;
			;;
		* )
			echo "`date` - Too many input files found" >> $LOGS/OrderReconciliation.log;
			echo "Too many input files found." > $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
			echo "TOG Order Audit: Too many input files found. Process Aborted - Action Required" > $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;
			mv $FromTOG/* $ARCHIVE/;
			mv $OrderReconciliationHome/temp.txt $OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt;	
			echo "temp.txt file renamed to Order_Reconciliation_Emailer_Temp.txt..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
			echo "Exiting OrderReconciliation Generation..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
			echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
			echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
			exit;
			;;
	esac
	
	######## ISO-8859-1 to UTF-8 encoding to deal with our fav...broken pipe!
	iconv -f UTF-8 -t ISO-8859-1 $TEMP/infile.txt > $TEMP/infile1.txt;

	######## Remove the header line and trailer field
	sed '1d' $TEMP/infile1.txt > $TEMP/infile2.txt;
	sed '$d' $TEMP/infile2.txt > $TEMP/infile3.txt;

	######## Load the file into the DB table.
	sed 's/¦/|/g' $TEMP/infile3.txt > $TEMP/infile4.txt;

	echo "Sourcing db2 profile..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	. /home/wasuser/sqllib/db2profile;

	echo "db2 creating connection..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	db2 TRUNCATE TABLE OMCTMETA.EXTN_ORDER_RECON IMMEDIATE;
	echo "db2 table truncated..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	db2 import from $TEMP/infile4.txt of del modified by COLDEL0x7C INSERT INTO OMCTMETA.EXTN_ORDER_RECON;
	echo "db2 data imported..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;	
	
	######## If SQLLDR failed, display the logfile so it's in the logs and copy it to the output file so they see the error.
	if [ $? -ne 0 ]; then
		echo "`date` - SQL loader failure" >> $LOGS/OrderReconciliation.log;
		echo "File Failed to Load into OMS." > $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
		echo "TOG Order Audit: Input file failed to load into OMS. Process Aborted - Action Required" > $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;

		mv $OrderReconciliationHome/temp.txt $OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt;	
		echo "temp.txt file renamed to Order_Reconciliation_Emailer_Temp.txt..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
		echo "Exiting OrderReconciliation Generation..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
		echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
		echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;			
		rm $INFILE;
		exit;
	fi
	
	######## Run the script to execute the package and generate the extract.
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script1");
	echo "db2 sql executed..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' > $TEMP/$EXCEPTIONSFILETEXT;
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/$EXCEPTIONSFILE;
	echo "db2 output encrypted line by line..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
	
	######## Generate email based on the output for the script.
	ORD=`cat $TEMP/$EXCEPTIONSFILETEXT | wc -l | cut -d " " -f 1`;

	mv $INFILE $ARCHIVE/$PROCESSEDFILE;
	chmod 775 $TEMP/$EXCEPTIONSFILE;
	chmod 775 $TEMP/$EXCEPTIONSFILETEXT;
	cp -p $TEMP/$EXCEPTIONSFILE $ARCHIVE/$EXCEPTIONSFILE;
	cp -p $TEMP/$EXCEPTIONSFILE $ToTOG/$EXCEPTIONSFILE;
	cp -p $TEMP/$EXCEPTIONSFILETEXT $ToTOG/$EXCEPTIONSFILETEXT;
	
	echo "ORD :: $ORD" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	case $ORD in
		4 )
			echo "`date` - No Mismatches Found" >> $LOGS/OrderReconciliation.log;
			echo "`date` - No Mismatches Found" > $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
			echo "TOG Order Audit: File Successfully Reconciled" > $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;
			;;
		* )
			echo "`date` - Mismatches Found" >> $LOGS/OrderReconciliation.log;
			echo "`date` - Mismatches Found" > $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
			echo "TOG Order Audit: Order(s) Missing From OM - Action Required" > $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;
			;;
	esac
	
	chmod 775 $EmailReportHome/TOG-OrderReconciliation-Email-Body.txt;
	chmod 775 $EmailReportHome/TOG-OrderReconciliation-Email-Subject.txt;
	
	mv $OrderReconciliationHome/temp.txt $OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt;	
	echo "temp.txt file renamed to Order_Reconciliation_Emailer_Temp.txt..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
	
	echo "Exiting OrderReconciliation Generation..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;		
	echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
	echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;	
fi	