#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn604ap001.nwcg.gov" ]; then
  echo "Stopping Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh -username icbsadmin -password icbs@dm1n
fi
if [ $HOSTNAME = "nn604ap002.nwcg.gov" ]; then
  echo "Stopping Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh -username icbsadmin -password icbs@dm1n
fi
