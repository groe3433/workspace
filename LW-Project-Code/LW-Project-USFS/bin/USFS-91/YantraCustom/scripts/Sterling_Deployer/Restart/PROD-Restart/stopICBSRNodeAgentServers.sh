#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn600wms001.nwcg.gov" ]; then
  echo "Stopping Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh -username wasadmin -password wasadmin
fi
if [ $HOSTNAME = "nn600wms002.nwcg.gov" ]; then
  echo "Stopping Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/stopNode.sh -username wasadmin -password wasadmin
fi
