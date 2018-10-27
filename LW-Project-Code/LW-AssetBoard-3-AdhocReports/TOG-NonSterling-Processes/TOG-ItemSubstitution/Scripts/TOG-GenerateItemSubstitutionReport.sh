#!/bin/bash

ItemSubstitutionHome=/opt/ssfs/runtime/TOGOMSExtracts/ItemSubstitution;

tempFile="$ItemSubstitutionHome/Item_Substitution_Temp.txt";
if [ -f "$tempFile" ] 
then
	echo "Entered ItemSubstitution Generation..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	echo "Report Date: $(date)" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	echo "whoami: $(whoami)" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	
	mv $ItemSubstitutionHome/Item_Substitution_Temp.txt $ItemSubstitutionHome/temp.txt;	
	echo "Item_Substitution_Temp.txt file renamed to temp.txt..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
	
	ARCHIVE=$ItemSubstitutionHome/Archive;
	LOGS=$ItemSubstitutionHome/Logs;
	SCRIPTS=$ItemSubstitutionHome/Scripts;
	TEMP=$ItemSubstitutionHome/Temp;
	ToTOG=$ItemSubstitutionHome/ToTOG;
	FromTOG=$ItemSubstitutionHome/FromTOG;
	
	OutputFileName="ItemSubstitution_`date +%Y%m%d%H%M%S`.csv";
	Script1="TOG-GenerateItemSubstitutionReport.sql";
	
	echo "Sourcing db2 profile..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
	. /home/wasuser/sqllib/db2profile;
	
	echo "db2 creating connection..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;		
	db2 connect to omdb user db2inst1 using diet4coke;
	echo "db2 connection created..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;		
	StdOut=$(db2 -x -tf "$SCRIPTS/$Script1");
	echo "db2 sql executed..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -a -k TogAESEncryption256 > $TEMP/$OutputFileName;
	echo "db2 output encrypted line by line..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	
	cp $TEMP/$OutputFileName $ToTOG;
	chmod 775 $ToTOG/$OutputFileName;
	cp $ToTOG/$OutputFileName $ARCHIVE;
	chmod 775 $ARCHIVE/$OutputFileName;
	rm $TEMP/$OutputFileName;

	mv $ItemSubstitutionHome/temp.txt $ItemSubstitutionHome/Item_Substitution_Emailer_Temp.txt;	
	echo "Item_Substitution_Emailer_Temp.txt file renamed to temp.txt..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	

	echo "Exiting ItemSubstitution Generation..." >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;	
	echo "___________________________________________________________________" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
	echo "___________________________________________________________________" >> $ItemSubstitutionHome/Logs/ItemSubstitution.log;
fi