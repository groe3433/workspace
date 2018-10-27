#!/bin/bash

##
##   E N V I R O N M E N T  
##


# OMS Environment
RUNAS_USER=${USER}
YFS_HOME=/opt/IBM/Sterling94

NOW=$(date +%Y%m%d-%H%M%S)

AGENTCONF=${YFS_HOME}/bin/agent.conf
AGENTLIST=$(grep ${HOSTNAME} ${AGENTCONF} | awk '/^[^ #\t]/ {print $3}')

export RUNAS_USER YFS_HOME AGENT_HOME NOW AGENTCONF AGENTLIST

#
#   F U N C T I O N S
#
usage() {
cat <<-EOF
Usage: $0 (start|stop|restart) <agentName>

Valid agents on this host are:
==============================
$(printf "    %-s\n" ${AGENTLIST})

EOF
exit 1
}


start() {

    AGENT=$1
    NOOFINSTANCE=$(grep ${HOSTNAME} ${AGENTCONF} | grep " $AGENT "| awk '{print $2}')
    INITHEAP=$(grep ${HOSTNAME} ${AGENTCONF} | grep " $AGENT " | awk '{print $4}')
    MAXHEAP=$(grep ${HOSTNAME} ${AGENTCONF} | grep " $AGENT "| awk '{print $5}')
    JVMPARMS=$(grep ${HOSTNAME} ${AGENTCONF} | grep " $AGENT " | awk '{for (i=6;i<=NF;i++) {  printf "%-s ", $i;}}')
    LOGFILE=${YFS_HOME}/logs/agent_logs/${AGENT}.log
    ERRORLOG=${YFS_HOME}/logs/agent_logs/${AGENT}.error
    export INITHEAP MAXHEAP JVMPARMS NOOFINSTANCE LOGFILE ERRORLOG
    echo "    Attempting to start agent ${AGENT}. Options:"
    echo "    Number of instances - ${NOOFINSTANCE}"
    echo "    Initial heap size - ${INITHEAP}"
    echo "    Max heap size - ${MAXHEAP}"
    echo "    JVM parameters - ${JVMPARMS}"
    echo "    LOGFILE - ${LOGFILE}"
    echo "    ERRORLOGOR - ${ERRORLOG}"

    # Invoke the Highlander Principle
    if pgrep -u ${RUNAS_USER} -x -f "^/.*java .* ${AGENT}$" >/dev/null 2>&1; then
        echo "OMS agent server ${AGENT} is already running. Refusing to start multiple instances."
        exit 1
    else
        echo "OMS agent ${AGENT} is not already running.  Attempting to start it..."
    fi

    if [ ! -d "${YFS_HOME}/logs/agent_logs" ]; then
        echo "Creating agent_logs folder."
        mkdir ${YFS_HOME}/logs/agent_logs
    fi

    #if [ -f "${YFS_HOME}/logs/agent_logs/${AGENT}.log" ]; then
     #   echo "Backing up old agent log."
      #  mv ${YFS_HOME}/logs/agent_logs/${AGENT}.log ${YFS_HOME}/logs/agent_logs/${AGENT}.log.${NOW}
   # fi

    #if [ -f "${YFS_HOME}/logs/agent_logs/${AGENT}.error" ]; then
     #   echo "Backing up old agent error log."
      #  mv ${YFS_HOME}/logs/agent_logs/${AGENT}.error ${YFS_HOME}/logs/agent_logs/${AGENT}.error.${NOW}
   # fi

    for ((i=1;i<=$NOOFINSTANCE;i++)); do
        cd ${YFS_HOME}/bin
        INSTNO=i
	      export INSTNO

    if grep -q "DLOGFILE" tmp.sh
        then echo "LOGFILE VARIABLE EXIST"
       else
        sed -i 's/\DINSTALL_LOCALE=en\b/& -DLOGFILE=${LOGFILE}/' tmp.sh
        fi

        if grep -q "DERRORLOG" tmp.sh
        then echo "ERRORLOGOR VARIABLE EXIST"
        else
        sed -i 's/\DINSTALL_LOCALE=en\b/& -DERRORLOG=${ERRORLOG}/' tmp.sh
        fi

        ./agentserver.sh ${AGENT} ${INITHEAP} ${MAXHEAP} ${JVMPARMS} \
            > ${LOGFILE} \
            2> ${ERRORLOG} &

        echo $SUDO_USER >> ${YFS_HOME}/logs/agent_logs/StartStop.log \
           "started" ${AGENT} >> ${YFS_HOME}/logs/agent_logs/StartStop.log \
           "at" ${NOW} >> ${YFS_HOME}/logs/agent_logs/StartStop.log

        sleep 15

        if pgrep -u ${RUNAS_USER} -x -f "^/.*java .* ${AGENT}$" >/dev/null 2>&1; then
            echo "Sucessfully started OMS agent server ${AGENT}"
        else
            echo "OMS agent server ${AGENT} did not start, please check the log at:"
            echo "  ${ERRORLOG}"
        fi
    done
}


stop() {

    AGENT=$1
    PID=$(ps -ef | grep 'IntegrationAdapter' | grep "$AGENT" | grep -v 'grep' | awk '{print$2}')
    CLEAN_SHUTDOWN=0

    if [[ $PID -eq "" ]]; then
        echo "OMS agent server ${AGENT} is not running."
        return 0
    else
        echo "OMS agent server ${AGENT} is running as pid ${PID}."
    fi

    echo "Sending SIGTERM to OMS agent server ${AGENT}."
    kill ${PID}

    for i in {1..6}; do
        sleep 10
        if ps -p ${PID} >/dev/null 2>&1; then
            echo "Agent ${AGENT} still running..."
            CLEAN_SHUTDOWN=1
        else
            echo "Agent ${AGENT} is terminated."
            CLEAN_SHUTDOWN=0
            break
        fi
    done

    if [[ ${CLEAN_SHUTDOWN} -ne 0 ]]; then
        echo "Process still alive.  Sending SIGKILL to OMS agent server ${AGENT}."
        kill -9 ${PID}
    fi

}


#
#   M A I N
#

# Ignore SIGHUP
trap "" 1

# Check user parameters
if [[ $# -eq 2 ]] && echo ${AGENTLIST} | grep -qw ${2}; then
    AGENT=${2}; export AGENT
else
    usage
fi

case $1 in
    start)      start ${AGENT};;
    stop)       stop ${AGENT};;
    restart)    stop ${AGENT}; start ${AGENT};;
    *|?)        usage;;
esac
