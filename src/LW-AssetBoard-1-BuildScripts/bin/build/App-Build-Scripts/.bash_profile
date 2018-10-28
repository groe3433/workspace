# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
        . ~/.bashrc
fi

# User specific environment and startup programs

ANT_HOME=/home/wasuser/TOGScripts/apache-ant-1.8.1
export ANT_HOME

PATH=${ANT_HOME}/bin:$HOME/bin:${PATH}
export PATH

STERLING=/opt/ssfs/runtime
export STERLING

WAS=/opt/IBM/WebSphere/AppServer/profiles/omprofile/
export WAS

WASLOGS=/opt/IBM/WebSphere/AppServer/profiles/omprofile/logs/server1
export WASLOGS

WLOG=/opt/IBM/WebSphere/AppServer/profiles/omprofile/logs/server1
export WLOG

HOT=/opt/IBM/WebSphere/AppServer/profiles/omprofile/installedApps/qusjc01togdomapp01Cell01/smcfsqa.ear/smcfs.war
export HOT

SCRIPT=/home/wasuser/TOGScripts
export SCRIPT

#SMCFS_ENV_NAME=DEV
#SMCFS_ENV_NAME=MC
SMCFS_ENV_NAME=QA
#SMCFS_ENV_NAME=Preprod
#SMCFS_ENV_NAME=Prod
export SMCFS_ENV_NAME

TOG_B2B_NAME=QA
#TOG_B2B_NAME=Preprod
#TOG_B2B_NAME=Prod
export TOG_B2B_NAME

export PATH

## WEBSPHERE COMMERCE 70 ADDITIONS
LIBPATH=$LIBPATH:/opt/IBM/WebSphere/CommerceServer70/bin
export LIBPATH
. /home/omdb/.profile
LD_LIBRARY_PATH=/opt/IBM/DB2/V10.5/lib64:$LD_LIBRARY_PATH
export LD_LIBRARY_PATH
#umask 0027
## END WEBSPHERE COMMERCE 70 ADDITIONS DO NOT EDIT OR MOVE