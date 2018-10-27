#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

if [ "$agtServer" = "agtapp02" ] 
then
	. /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

	TOGSystemStatsHome=/opt/ssfs/runtime/TOGSystemStats;

	echo "Entered TOGSystemStats Error List..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	echo "Report Date: $(date)" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	echo "whoami: $(whoami)" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;	
	
	Script1="TOG-ErrorList.sql";
	
	echo "Sourcing db2 profile..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	. /home/wasuser/sqllib/db2profile;
	
	echo "db2 creating connection..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	db2 connect to omdb user db2inst1 using diet4coke;
	
	echo "db2 connection created..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	StdOut=$(db2 -x -tf "$TOGSystemStatsHome/$Script1");
	echo "db2 sql executed..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;	
	
	echo $StdOut | sed -e 's/newLine /\n/g' -e 's/newLine//g' | mailx -s "TOGSystemStats Error List" $MORE_DETAILED_EMAIL;

	echo "Exiting TOGSystemStats Error List..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	echo "___________________________________________________________________" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
	echo "___________________________________________________________________" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
fi
exit	