#!/bin/bash

. /home/wasuser/.bash_profile

NotEncryptedOutputFileName="Performance_NoEncryption_OpenOrders_`date +%Y%m%d%H%M%S`.csv";
OutputFileName="Performance_OpenOrders_`date +%Y%m%d%H%M%S`.csv";
Script2="TOG-GenerateOpenOrdersReport_PT.sql";
		
. /home/omdb/sqllib/db2profile;
	
echo "db2 creating connection..."	
db2 connect to omdb user db2inst1 using diet4coke;
echo "db2 connection created..."
StdOut=$(db2 -x -tf "$Script2");
echo "db2 sql executed..."
echo $StdOut >> $NotEncryptedOutputFileName;
echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | openssl enc -aes-256-cbc -k TogAESEncryption256 > $OutputFileName;
echo "db2 output encrypted line by line..."

echo "Attached is the OpenOrders PT Test File..." | mailx -s "Performance OpenOrders Report" -a $OutputFileName $REPORT_EMAIL;