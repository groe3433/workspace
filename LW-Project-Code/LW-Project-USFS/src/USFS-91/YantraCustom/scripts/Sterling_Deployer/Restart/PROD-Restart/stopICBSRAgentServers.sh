#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi
if [ $HOSTNAME = "nn600wma001.nwcg.gov" ]; then
  echo "Stopping Agent Servers ($HOSTNAME)..."
  /opt/apps/projects/Sterling_Deployer/StopICBSAgentServers.sh;
fi
