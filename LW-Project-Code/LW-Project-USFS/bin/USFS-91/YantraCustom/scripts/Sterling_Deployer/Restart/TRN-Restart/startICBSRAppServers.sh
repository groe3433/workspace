#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn603wms001.nwcg.gov" ]; then
  echo "Starting Cluster Member ICBSR01 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/startServer.sh ICBSR01
fi
if [ $HOSTNAME = "nn603wms002.nwcg.gov" ]; then
  echo "Starting Cluster Member ICBSR02 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/startServer.sh ICBSR02
fi
