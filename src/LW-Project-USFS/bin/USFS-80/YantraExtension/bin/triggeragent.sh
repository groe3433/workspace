#!/usr/bin/sh
# Copyright 2006, Sterling Commerce, Inc. All rights reserved.

#Set the DB_DRIVER below to point to the location of your database driver
if [ $# -ne 1 ] 
then 
    printf "`basename $0` criteriaid\n"
	exit 1
else

DIRNAME=`/usr/bin/dirname $0`
export DIRNAME

YFS_HOME=${DIRNAME}/../
export YFS_HOME
echo $YFS_HOME

WAS_HOME=/opt/apps/IBM/WebSphere/AppServer

DB_DRIVER=${YFS_HOME}/lib/ojdbc14.jar
export DB_DRIVER


CLASSPATH=${PROJECT_HOME}/fromCVS/USFS/YantraCustom/dist/nwcg_icbs_br1.jar:${YFS_HOME}/extn/yfsdbextn.jar:${YFS_HOME}:${YFS_HOME}/lib/yfcbe.jar:${YFS_HOME}/lib/yantrautil.jar:${YFS_HOME}/lib/ycpbe.jar:${YFS_HOME}/lib/vasbe.jar:${YFS_HOME}/lib/ycsbe.jar:${YFS_HOME}/lib/yantrashared.jar:${YFS_HOME}/lib/ycmbe.jar:${YFS_HOME}/lib/ydmbe.jar:${YFS_HOME}/lib/yompbe.jar:${YFS_HOME}/lib/yimbe.jar:${YFS_HOME}/lib/wmsbe.jar:${YFS_HOME}/lib/activation.jar:${YFS_HOME}/lib/bsf.jar:${YFS_HOME}/lib/bsfengines.jar:${YFS_HOME}/lib/ynwbe.jar:${YFS_HOME}/lib/ynwshared.jar:${YFS_HOME}/lib/ynwversionshared.jar:${YFS_HOME}/lib/jakarta-oro-2.0.8.jar:${YFS_HOME}/lib/js.jar:${YFS_HOME}/lib/jstools.jar:${YFS_HOME}/lib/mail.jar:${YFS_HOME}/lib/xalan.jar:${YFS_HOME}/lib/xercesImpl.jar:${YFS_HOME}/lib/xml-apis.jar:${YFS_HOME}/lib/log4j-1.2.11.jar:${YFS_HOME}/lib/Netcomponents-1.3.8.jar:${DB_DRIVER}:${YFS_HOME}/lib/comm.jar:${YFS_HOME}/lib/commons-pool-1.2.jar:${YFS_HOME}/lib/commons-collections-3.1.jar:${WS_HOME}/installedChannels/channel.tcp.jar${YFS_HOME}:${WS_HOME}:${WS_HOME}/lib/wsbytebufferservice.jar:${WS_HOME}:${WS_HOME}/lib/j2ee.jar:${WS_HOME}/lib/naming.jar:${WS_HOME}/lib/ras.jar:${WS_HOME}/lib/wsexception.jar:${WS_HOME}/lib/bootstrap.jar:${WS_HOME}/lib/emf.jar:${WS_HOME}/lib/namingclient.jar:${WS_HOME}/lib/ecutils.jar:${WS_HOME}/lib/iwsorb.jar:${WS_HOME}/lib/idl.jar:${WS_HOME}/lib/ffdc.jar:${WS_HOME}/lib/utils.jar:${WS_HOME}/lib/naming.jar:${WS_HOME}/lib/ras.jar:${WS_HOME}/lib/wsexception.jar:${WS_HOME}/lib/bootstrap.jar:${WS_HOME}/lib/emf.jar:${WS_HOME}/lib/ecutils.jar:${WS_HOME}/lib/iwsorb.jar:${WS_HOME}/lib/namingclient.jar:${WS_HOME}/lib/runtime.jar:${WS_HOME}/lib/idl.jar:${WS_HOME}/lib/ffdc.jar:${WS_HOME}/lib/utils.jar:${WS_HOME}/lib/messaging.jar:${WS_HOME}/properties:${WS_HOME}/lib/sib.common.jar

export CLASSPATH=${YFS_HOME}:${DB_DRIVER}:${CLASSPATH}

echo "classpath" ${CLASSPATH}

JAVA_HOME=/opt/apps/IBM/WebSphere/AppServer/java
export JAVA_HOME

java -Dcom.ibm.CORBA.ConfigURL=file:${WS_HOME}/profiles/default/properties/sas.client.props   -Djava.ext.dirs=${WS_HOME}/installedChannels/:${WS_HOME}/java/jre/lib/ext:${WS_HOME}/java/jre/lib:${WS_HOME}/classes:${WS_HOME}/lib:${WS_HOME}/lib/ext:${WS_HOME}/properties:${WS_HOME}/lib/WMQ/java/lib -Dyantra.override.properties.file=/resources/yfs.properties.agent com.yantra.ycp.agent.server.YCPAgentTrigger -criteria "$1"

fi
