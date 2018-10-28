#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn603dm001.nwcg.gov" ]; then
  echo "Starting Deployment Manager ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Dmgr01/bin/startManager.sh
fi
