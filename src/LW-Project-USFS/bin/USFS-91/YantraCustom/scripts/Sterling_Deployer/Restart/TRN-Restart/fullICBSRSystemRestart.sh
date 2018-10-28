#!/bin/bash

USERNAME=$USER
if [ $USER != "was" ]; then
  echo "Sudo as WAS to run script"
  exit 1;
fi

if [ $HOSTNAME != "nn603wma001.nwcg.gov" ]; then
  echo "Please run this script from nn603wma001.nwcg.gov..."
  exit 1;
fi

if [ $HOSTNAME = "nn603wma001.nwcg.gov" ]; then

echo "***** Full Restart Script:: Stop Agents";
/opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAgentServers.sh;
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Stop ICBSR01";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAppServers.sh";

echo "***** Full Restart Script:: Stop ICBSR02";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRAppServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Stop Node Agent (ICBSR01)";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRNodeAgentServers.sh";

echo "***** Full Restart Script:: Stop Node Agent (ICBSR02)";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRNodeAgentServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Stop Deployment Manager";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603dm001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/stopICBSRDeploymentManager.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Start Deployment Manager";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603dm001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRDeploymentManager.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Start Node Agent (ICBSR01)";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRNodeAgentServers.sh";

echo "***** Full Restart Script:: Start Node Agent (ICBSR02)";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRNodeAgentServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Start ICBSR01";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms001.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRAppServers.sh";

echo "***** Full Restart Script:: Start ICBSR02";
$SSHPASS/sshpass -p $2 ssh -o StrictHostKeyChecking=no $1@nn603wms002.nwcg.gov "echo $2 | sudo -S -u was /opt/apps/projects/Sterling_Deployer/Restart/startICBSRAppServers.sh";
echo "Allowing 10 seconds for this activity...";
sleep 10;

echo "***** Full Restart Script:: Start Agents";
/opt/apps/projects/Sterling_Deployer/Restart/startICBSRAgentServers.sh;
echo "Check your Agent Servers in 5 minutes...";

fi
