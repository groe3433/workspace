#!/bin/bash
grep $HOSTNAME agent.conf | grep -v '#' | while read line ; do
    set -- $line
    NO_OF_INSTANCES=$2
    AGENT_NAME=$3

    a=0
    while [ $a -lt $NO_OF_INSTANCES ]
    do
        ./agentctl stop $AGENT_NAME
        sleep 1
        a=`expr $a + 1`
    done
done
