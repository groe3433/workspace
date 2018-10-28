#!/bin/bash

agtServer="$(echo $HOSTNAME | cut -c11-18)";

if [ "$agtServer" = "agtapp02" ]
then
        . /opt/ssfs/runtime/TOGCommonScripts/TOGEmailDLs.sh

        TOGSystemStatsHome=/opt/ssfs/runtime/TOGSystemStats;

        echo "Entered TOGSystemStats SQL Monitor..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        echo "Report Date: $(date)" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        echo "whoami: $(whoami)" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;

        Script1="TOG-SQLMonitor.sql";

        echo "Sourcing db2 profile..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        . /home/wasuser/sqllib/db2profile;

        echo "db2 creating connection..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        db2 connect to omdb user db2inst1 using diet4coke;

        echo "db2 connection created..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        StdOut=$(db2 -x -tf "$TOGSystemStatsHome/$Script1");
        echo $StdOut > /opt/ssfs/runtime/TOGSystemStats/TOGSystemStats.txt;

        echo "db2 sql executed..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;

        cd $TOGSystemStatsHome;
        if [[ $(wc -l <TOGSystemStats.txt) -ge 2 ]]
        then
                cat /opt/ssfs/runtime/TOGSystemStats/TOGSystemStats.txt | sed -e 's/newLine /\n/g' -e 's/newLine//g' | mailx -s "TOGSystemStats SQL Monitor" $MORE_DETAILED_EMAIL;
        fi
        rm TOGSystemStats.txt;

        echo "Exiting TOGSystemStats SQL Monitor..." >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        echo "___________________________________________________________________" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
        echo "___________________________________________________________________" >> /opt/ssfs/runtime/logs/TOGSystemStats.log;
fi
exit
