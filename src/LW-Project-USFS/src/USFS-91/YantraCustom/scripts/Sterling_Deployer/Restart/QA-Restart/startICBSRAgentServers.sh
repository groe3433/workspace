#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn604ap003.nwcg.gov" ]; then
  echo "Starting Agent Servers ($HOSTNAME)...";
  /opt/apps/projects/Sterling_Deployer/RunAllICBSAgentServers.sh;
fi
