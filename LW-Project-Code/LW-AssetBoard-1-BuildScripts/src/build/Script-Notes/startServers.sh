echo "STARTING startServers.sh"

########################
echo "Start Health Monitor"
nohup /opt/ssfs/runtime/bin/startHealthMonitor.sh &
sleep 30s;

########################
echo "STARTING INVENTORY AGENTS"
echo "Starting OMS_INVENTORY_SYNC agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_INVENTORY_SYNC > /opt/ssfs/runtime/logs/OMS_INVENTORY_SYNC`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

echo "Starting OMS_ADJUST_INV agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_ADJUST_INV > /opt/ssfs/runtime/logs/OMS_ADJUST_INV`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

echo "Starting OMS_PRODUCT_CATALOG agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_PRODUCT_CATALOG > /opt/ssfs/runtime/logs/OMS_PRODUCT_CATALOG`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

#########################
echo "STARTING ORDER AGENTS"
echo "Starting OMS_CREATE_ORDER agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_CREATE_ORDER > /opt/ssfs/runtime/logs/OMS_CREATE_ORDER`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

echo "Starting OMS_ORDER_STATUS_CHANGE agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_ORDER_STATUS_CHANGE > /opt/ssfs/runtime/logs/OMS_ORDER_STATUS_CHANGE`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

echo "Starting OMS_RECEIPT_CONF agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_RECEIPT_CONF > /opt/ssfs/runtime/logs/OMS_RECEIPT_CONF`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

#########################
echo "STARTING SHIPMENT AGENTS"
echo "Starting OMS_CONFIRM_SHIPMENT agent"
nohup /opt/ssfs/runtime/bin/startIntegrationServer.sh OMS_CONFIRM_SHIPMENT > /opt/ssfs/runtime/logs/OMS_CONFIRM_SHIPMENT`date '+_%Y%m%d_%H%M%S'`_Agent.log &
sleep 20s;

#########################
echo "Starting TOGAGENTSERVER"
rm $SCRIPT/logs/TOGAGENTSERVER.log
nohup $STERLING/bin/agentserver.sh TOGAGENTSERVER > $SCRIPT/logs/TOGAGENTSERVER.log 2>&1 &
chmod 775 $SCRIPT/logs/TOGAGENTSERVER.log

exit