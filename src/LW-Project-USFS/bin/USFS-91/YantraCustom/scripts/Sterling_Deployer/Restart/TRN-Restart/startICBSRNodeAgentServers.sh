#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn603wms001.nwcg.gov" ]; then
  echo "Starting Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/startNode.sh
fi
if [ $HOSTNAME = "nn603wms002.nwcg.gov" ]; then
  echo "Starting Node Agent ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Custom01/bin/startNode.sh
fi
