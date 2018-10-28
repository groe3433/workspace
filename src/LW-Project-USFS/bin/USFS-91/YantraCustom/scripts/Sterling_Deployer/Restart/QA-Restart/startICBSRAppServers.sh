#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn604ap001.nwcg.gov" ]; then
  echo "Starting Cluster Member ICBSR01 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startServer.sh ICBSR01
fi
if [ $HOSTNAME = "nn604ap002.nwcg.gov" ]; then
  echo "Starting Cluster Member ICBSR02 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/startServer.sh ICBSR02
fi
