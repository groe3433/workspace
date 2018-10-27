#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn603wms001.nwcg.gov" ]; then
  echo "Stopping Cluster Member ICBSR01 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/stopServer.sh ICBSR01 -username wasadmin -password wasadmin
fi
if [ $HOSTNAME = "nn603wms002.nwcg.gov" ]; then
  echo "Stopping Cluster Member ICBSR02 ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/stopServer.sh ICBSR02 -username wasadmin -password wasadmin
fi
