#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn604dm001.nwcg.gov" ]; then
  echo "Stopping Deployment Manager ($HOSTNAME)...";
  /opt/IBM/WebSphere/AppServer/profiles/Dmgr01/bin/stopManager.sh -username icbsadmin -password icbs@dm1n
fi
