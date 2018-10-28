#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles

OrderReconciliationHome=/opt/ssfs/runtime/TOGOMSExtracts/OrderReconciliation;

if [ -f "$OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt" ]
then
		rm $OrderReconciliationHome/Order_Reconciliation_Emailer_Temp.txt;

		ARCHIVE=$OrderReconciliationHome/Archive
		LOGS=$OrderReconciliationHome/Logs
		SCRIPTS=$OrderReconciliationHome/Scripts
		TEMP=$OrderReconciliationHome/Temp
		ToTOG=$OrderReconciliationHome/ToTOG
		FromTOG=$OrderReconciliationHome/FromTOG

		EmailReportBody="TOG-OrderReconciliation-Email-Body.txt"
		EmailReportSubject="TOG-OrderReconciliation-Email-Subject.txt"

		echo "Entering OrderReconciliation Emailer..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;

		cd $ToTOG;
		OUTFILETEXT=`find . -name 'Order_recon_exceptions_text_*.txt'`;
		if [ -f "$ToTOG/$OUTFILETEXT" ]
		then
				SUBJECT=$(cat $EmailReportHome/$EmailReportSubject);
				BODY=$(cat $EmailReportHome/$EmailReportBody);
				cat $OUTFILETEXT | mailx -s "$SUBJECT" $REPORT_EMAIL;
				rm $ToTOG/$OUTFILETEXT;
				OUTFILE=`find . -name 'Order_recon_exceptions_*.txt'`;
				if [ -f "$ToTOG/$OUTFILE" ]
				then
						echo "Uploading exceptions file to B2B..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
						/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $OUTFILE;
				fi
				sleep 10s;
		else
				SUBJECT=$(cat $EmailReportHome/$EmailReportSubject);
				BODY=$(cat $EmailReportHome/$EmailReportBody);
				echo "$BODY" | mailx -s "$SUBJECT" $REPORT_EMAIL;
		fi
		rm $ToTOG/*.txt;
		rm $TEMP/*.txt;

		echo "Exiting OrderReconciliation Emailer..." >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
		echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
		echo "___________________________________________________________________" >> $OrderReconciliationHome/Logs/OrderReconciliation.log;
fi