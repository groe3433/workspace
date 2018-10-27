#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi

if [ $HOSTNAME != "nn600wma001.nwcg.gov" ]; then
  echo "Please run this script from nn600wma001.nwcg.gov..."
  exit 1;
fi

if [ $HOSTNAME = "nn600wma001.nwcg.gov" ]; then

echo "***** Common Restart Script:: Stop Agents";
/opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAgentServers.sh;
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Common Restart Script:: Stop ICBSR01";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn600wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAppServers.sh";

echo "***** Common Restart Script:: Stop ICBSR02";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn600wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAppServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Common Restart Script:: Start ICBSR01";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn600wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRAppServers.sh";

echo "***** Common Restart Script:: Start ICBSR02";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn600wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRAppServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Common Restart Script:: Start Agents";
/opt/apps/projects/Sterling_Deployer/Restart/startICBSRAgentServers.sh;
echo "Check your Agent Servers in 5 minutes...";

fi
