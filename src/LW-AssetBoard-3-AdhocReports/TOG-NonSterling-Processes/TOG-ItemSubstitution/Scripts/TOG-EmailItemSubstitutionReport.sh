#!/bin/bash

. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

date;

EmailReportHome=/opt/ssfs/runtime/TOGOMSExtracts/CommonReportFiles

ItemSubstitutionHome=/opt/ssfs/runtime/TOGOMSExtracts/ItemSubstitution;

if [ -f "$ItemSubstitutionHome/Item_Substitution_Emailer_Temp.txt" ] 
then	
	rm $ItemSubstitutionHome/Item_Substitution_Emailer_Temp.txt;

	ARCHIVE=$ItemSubstitutionHome/Archive
	LOGS=$ItemSubstitutionHome/Logs
	SCRIPTS=$ItemSubstitutionHome/Scripts
	TEMP=$ItemSubstitutionHome/Temp
	ToTOG=$ItemSubstitutionHome/ToTOG
	FromTOG=$ItemSubstitutionHome/FromTOG
	
	EmailReportBody="TOG-Email-Greeting.txt"
	EmailRecipients="TOG-Email-Recipients.txt"
	
	echo "Entering ItemSubstitution Emailer..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
	
	cd $ToTOG;
	chmod 775 *.csv;
	for file in *.csv; do
		echo "File to send: $file..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
		cat $EmailReportHome/$EmailReportBody | mailx -s "Item Substitution Report" $REPORT_EMAIL;
		sleep 10s;
		/opt/ssfs/runtime/TOGOMSExtracts/uploadFileToB2B.sh $file;	
		sleep 20s;
		rm $file;
	done;
	echo "Exiting ItemSubstitution Emailer..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	echo "___________________________________________________________________" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	echo "___________________________________________________________________" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
fi