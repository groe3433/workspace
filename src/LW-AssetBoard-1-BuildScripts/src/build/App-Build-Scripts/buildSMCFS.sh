#!/bin/bash

. /home/wasuser/TOGScripts/SMCFSEmailDLs.sh

##### Make sure you set your environment variables!

if [ "$1" = "all-linux" ]
then
	echo "Starting Build/Deploy/Restart Script on $SMCFS_ENV_NAME, $SMCFS_ENV_NAME will be going down NOW!" | mail -s "$SMCFS_ENV_NAME - Build/Deploy/Restart Triggered" $MORE_DETAILED_EMAIL
	$ANT_HOME/bin/ant -f buildSMCFS.xml $1
	echo "Finished Build/Deploy/Restart Script on $SMCFS_ENV_NAME, please attempt to login and validate the new build!" | mail -s "$SMCFS_ENV_NAME - Build/Deploy/Restart Finished" $MORE_DETAILED_EMAIL
elif [ "$1" = "restartSMCFS" ] 
then
	echo "Starting $SMCFS_ENV_NAME Restart." | mail -s "$SMCFS_ENV_NAME will be going down NOW!" $MORE_DETAILED_EMAIL
	$ANT_HOME/bin/ant -f buildSMCFS.xml $1
	echo "Finished $SMCFS_ENV_NAME Restart." | mail -s "Please login to $SMCFS_ENV_NAME and validate it." $MORE_DETAILED_EMAIL
elif [ "$1" = "cdt-from-source-to-xml" ] 
then
	echo "Starting CDT EXPORT." | mail -s "Starting CDT EXPORT on $SMCFS_ENV_NAME!" $MORE_DETAILED_EMAIL
	$ANT_HOME/bin/ant -f buildSMCFS.xml $1
	echo "Finished CDT EXPORT on $SMCFS_ENV_NAME. Your CDT EXPORT will be in /tmp/TOG-CDT-EXPORT.zip" | mail -s "Finished CDT EXPORT on $SMCFS_ENV_NAME!" $MORE_DETAILED_EMAIL
elif [ "$1" = "cdt-from-xml-to-target" ] 
then
	echo "Starting CDT IMPORT." | mail -s "Starting CDT IMPORT on $SMCFS_ENV_NAME!" $MORE_DETAILED_EMAIL
	$ANT_HOME/bin/ant -f buildSMCFS.xml $1
	echo "Finished CDT IMPORT on $SMCFS_ENV_NAME. Please restart $SMCFS_ENV_NAME." | mail -s "Finished CDT IMPORT on $SMCFS_ENV_NAME." $MORE_DETAILED_EMAIL	
fi