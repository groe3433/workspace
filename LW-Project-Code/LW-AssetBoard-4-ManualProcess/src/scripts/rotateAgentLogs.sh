#!/bin/bash

date;

cd /opt/ssfs/runtime/logs;

logfile=$1;
timestamp=`date +%Y%m%d%H%M%S`;
zipfile=$logfile.$timestamp;

tar -zcvf $zipfile.tar.gz $logfile;
chmod 775 $zipfile.tar.gz;

todayFolder=`date +%Y%m%d`;
if [ ! -d "$todayFolder" ] 
then
	mkdir $todayFolder;
	chmod 775 -R $todayFolder;	
fi

mv $zipfile.tar.gz $todayFolder/$zipfile.tar.gz

cat /dev/null > $logfile;

mv /opt/ssfs/runtime/logs/$1.* $todayFolder/.