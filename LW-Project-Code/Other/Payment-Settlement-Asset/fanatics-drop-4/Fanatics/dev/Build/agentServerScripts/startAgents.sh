#!/bin/bash
grep $HOSTNAME agent.conf | grep -v '#' | while read line ; do
    set -- $line
    AGENT_NAME=$3
    ./agentctl start $AGENT_NAME
    sleep 1
done
